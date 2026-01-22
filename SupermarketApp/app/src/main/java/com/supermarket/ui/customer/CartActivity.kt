package com.supermarket.ui.customer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.supermarket.R
import com.supermarket.data.models.CartItem
import com.supermarket.data.models.MpesaResponse
import com.supermarket.utils.MpesaManager
import com.supermarket.utils.PreferencesManager

class CartActivity : AppCompatActivity() {
    
    private lateinit var viewModel: CustomerViewModel
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var mpesaManager: MpesaManager
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnCheckout: Button
    private lateinit var cartAdapter: CartAdapter
    
    private var branchId: Int = 0
    private var branchName: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        
        viewModel = ViewModelProvider(this)[CustomerViewModel::class.java]
        preferencesManager = PreferencesManager(this)
        mpesaManager = MpesaManager(this)
        
        getIntentData()
        initViews()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }
    
    private fun getIntentData() {
        branchId = intent.getIntExtra("branch_id", 0)
        branchName = intent.getStringExtra("branch_name") ?: ""
    }
    
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        btnCheckout = findViewById(R.id.btnCheckout)
    }
    
    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onIncreaseQuantity = { cartItem ->
                viewModel.addToCart(cartItem.product, cartItem.branchId)
            },
            onDecreaseQuantity = { cartItem ->
                viewModel.removeFromCart(cartItem.product, cartItem.branchId)
            }
        )
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = cartAdapter
    }
    
    private fun setupObservers() {
        viewModel.cart.observe(this) { cart ->
            cartAdapter.submitList(cart.toList())
            tvTotalAmount.text = "Total: KES ${viewModel.getCartTotal()}"
        }
        
        viewModel.saleResult.observe(this) { result ->
            result.fold(
                onSuccess = { sale ->
                    Toast.makeText(this, "Sale completed successfully!", Toast.LENGTH_LONG).show()
                    viewModel.clearCart()
                    finish()
                },
                onFailure = { error ->
                    Toast.makeText(this, "Sale failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            btnCheckout.isEnabled = !isLoading
        }
    }
    
    private fun setupClickListeners() {
        btnCheckout.setOnClickListener {
            val cart = viewModel.cart.value ?: return@setOnClickListener
            if (cart.isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val totalAmount = viewModel.getCartTotal()
            val userPhone = preferencesManager.getString("user_phone", "254712345678") // Default for demo
            
            // Initiate M-Pesa payment
            mpesaManager.initiatePayment(
                phoneNumber = userPhone,
                amount = totalAmount,
                accountReference = "SUPERMARKET-$branchId",
                transactionDesc = "Purchase at $branchName",
                onPaymentInitiated = { response ->
                    if (response.success) {
                        // Check payment status
                        checkPaymentStatus(response.checkoutRequestId ?: "")
                    }
                },
                onPaymentFailed = { error ->
                    Toast.makeText(this, "Payment initiation failed: $error", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
    
    private fun checkPaymentStatus(checkoutRequestId: String) {
        mpesaManager.checkPaymentStatus(
            checkoutRequestId = checkoutRequestId,
            onStatusReceived = { response ->
                if (response.success && response.message.contains("completed", ignoreCase = true)) {
                    // Payment successful, complete the sale
                    viewModel.checkout(branchId)
                } else {
                    Toast.makeText(this, "Payment failed or incomplete", Toast.LENGTH_LONG).show()
                }
            },
            onStatusCheckFailed = { error ->
                Toast.makeText(this, "Status check failed: $error", Toast.LENGTH_LONG).show()
            }
        )
    }
}

class CartAdapter(
    private val onIncreaseQuantity: (CartItem) -> Unit,
    private val onDecreaseQuantity: (CartItem) -> Unit
) : androidx.recyclerview.widget.ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): CartViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class CartViewHolder(itemView: android.view.View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        private val btnIncrease: TextView = itemView.findViewById(R.id.btnIncrease)
        private val btnDecrease: TextView = itemView.findViewById(R.id.btnDecrease)
        private val tvItemTotal: TextView = itemView.findViewById(R.id.tvItemTotal)
        
        fun bind(cartItem: CartItem) {
            tvProductName.text = cartItem.product.name
            tvProductPrice.text = "KES ${cartItem.product.price}"
            tvQuantity.text = cartItem.quantity.toString()
            tvItemTotal.text = "KES ${cartItem.getTotalPrice()}"
            
            btnIncrease.setOnClickListener {
                // Handle increase
            }
            
            btnDecrease.setOnClickListener {
                // Handle decrease
            }
        }
    }
}

class CartDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.product.id == newItem.product.id && oldItem.branchId == newItem.branchId
    }
    
    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }
}
