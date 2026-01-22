package com.supermarket.ui.customer

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
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
        
        tvBranchName.text = branchName
    }
    
    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product, stock ->
            viewModel.addToCart(product, branchId)
            Toast.makeText(this, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productAdapter
    }
    
    private fun setupObservers() {
        viewModel.branchStock.observe(this) { result ->
            result.fold(
                onSuccess = { stockList ->
                    val products = stockList.map { it.product }
                    productAdapter.submitList(products)
                },
                onFailure = { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                }
            )
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }
    }
    
    private fun setupClickListeners() {
        btnViewCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("branch_id", branchId)
            intent.putExtra("branch_name", branchName)
            startActivity(intent)
        }
    }
}

class ProductAdapter(
    private val onAddToCart: (Product, Stock) -> Unit
) : androidx.recyclerview.widget.ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ProductViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ProductViewHolder(itemView: android.view.View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val btnAddToCart: TextView = itemView.findViewById(R.id.btnAddToCart)
        
        fun bind(product: Product) {
            tvProductName.text = product.name
            tvProductPrice.text = "KES ${product.price}"
            
            btnAddToCart.setOnClickListener {
                // Handle click through callback
            }
        }
    }
}

class ProductDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
