package com.supermarket.ui.customer

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.ImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.supermarket.R
import com.supermarket.data.models.Product
import com.supermarket.data.models.Stock

class ProductListActivity : AppCompatActivity() {
    
    private lateinit var viewModel: CustomerViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvBranchName: TextView
    private lateinit var btnViewCart: TextView
    private lateinit var tvCartBadge: TextView
    private lateinit var fabCart: FloatingActionButton
    private lateinit var productAdapter: ProductAdapter
    
    private var branchId: Int = 0
    private var branchName: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)
        
        viewModel = ViewModelProvider(this)[CustomerViewModel::class.java]
        
        getIntentData()
        initViews()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        viewModel.loadBranchStock(branchId)
    }
    
    private fun getIntentData() {
        branchId = intent.getIntExtra("branch_id", 0)
        branchName = intent.getStringExtra("branch_name") ?: ""
    }
    
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        tvBranchName = findViewById(R.id.tvBranchName)
        btnViewCart = findViewById(R.id.btnViewCart)
        tvCartBadge = findViewById(R.id.tvCartBadge)
        fabCart = findViewById(R.id.fabCart)
        
        tvBranchName.text = branchName
    }
    
    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { stock ->
            val product = Product(stock.productId, stock.productName, stock.price)
            viewModel.addToCart(product, branchId)
            Toast.makeText(this, "${stock.productName} added to cart", Toast.LENGTH_SHORT).show()
            
            // Debug: Check cart after adding
            val currentCart = viewModel.cart.value
            android.util.Log.d("ProductListActivity", "Cart after adding: ${currentCart?.size} items")
            currentCart?.forEach { item ->
                android.util.Log.d("ProductListActivity", "Item: ${item.product.name}, Quantity: ${item.quantity}")
            }
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productAdapter
    }
    
    private fun setupObservers() {
        viewModel.branchStock.observe(this) { result ->
            result.fold(
                onSuccess = { stockList ->
                    productAdapter.submitList(stockList)
                },
                onFailure = { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                }
            )
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }
        
        // Observe cart changes to update badge
        viewModel.cart.observe(this) { cartItems ->
            val itemCount = cartItems?.sumOf { it.quantity } ?: 0
            updateCartBadge(itemCount)
        }
    }
    
    private fun updateCartBadge(count: Int) {
        if (count > 0) {
            tvCartBadge.text = count.toString()
            tvCartBadge.visibility = android.view.View.VISIBLE
        } else {
            tvCartBadge.visibility = android.view.View.GONE
        }
    }
    
    private fun setupClickListeners() {
        btnViewCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("branch_id", branchId)
            intent.putExtra("branch_name", branchName)
            startActivity(intent)
        }
        
        fabCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("branch_id", branchId)
            intent.putExtra("branch_name", branchName)
            startActivity(intent)
        }
    }
}

class ProductAdapter(
    private val onAddToCart: (Stock) -> Unit
) : androidx.recyclerview.widget.ListAdapter<Stock, ProductAdapter.ProductViewHolder>(StockDiffCallback()) {
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ProductViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), onAddToCart)
    }
    
    class ProductViewHolder(itemView: android.view.View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val tvStockQuantity: TextView = itemView.findViewById(R.id.tvStockQuantity)
        private val ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
        private val btnAddToCart: TextView = itemView.findViewById(R.id.btnAddToCart)
        
        fun bind(stock: Stock, onAddToCart: (Stock) -> Unit) {
            tvProductName.text = stock.productName
            tvProductPrice.text = "KES ${stock.price}"
            tvStockQuantity.text = "${stock.quantity} remaining"
            
            // Set product image based on product name
            when (stock.productName.lowercase()) {
                "coke" -> {
                    ivProduct.setImageResource(R.drawable.coke)
                    tvProductName.setTextColor(android.graphics.Color.parseColor("#FF0000")) // Red
                    btnAddToCart.setBackgroundColor(android.graphics.Color.parseColor("#FF0000"))
                }
                "fanta" -> {
                    ivProduct.setImageResource(R.drawable.fanta)
                    tvProductName.setTextColor(android.graphics.Color.parseColor("#FFA500")) // Orange
                    btnAddToCart.setBackgroundColor(android.graphics.Color.parseColor("#FFA500"))
                }
                "sprite" -> {
                    ivProduct.setImageResource(R.drawable.sprite)
                    tvProductName.setTextColor(android.graphics.Color.parseColor("#008000")) // Green
                    btnAddToCart.setBackgroundColor(android.graphics.Color.parseColor("#008000"))
                }
                else -> {
                    ivProduct.setImageResource(R.drawable.logo)
                    tvProductName.setTextColor(android.graphics.Color.parseColor("#000000")) // Black
                    btnAddToCart.setBackgroundColor(itemView.context.getColor(android.R.color.holo_blue_dark))
                }
            }
            
            btnAddToCart.setOnClickListener {
                onAddToCart(stock)
            }
        }
    }
}

class StockDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Stock>() {
    override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
        return oldItem == newItem
    }
}
