package com.supermarket.ui.admin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.supermarket.R
import com.supermarket.data.models.Branch
import com.supermarket.data.models.Product
import com.supermarket.ui.customer.CustomerViewModel

class RestockActivity : AppCompatActivity() {
    
    private lateinit var viewModel: AdminViewModel
    private lateinit var customerViewModel: CustomerViewModel
    
    private lateinit var spinnerBranch: Spinner
    private lateinit var spinnerProduct: Spinner
    private lateinit var etQuantity: TextView
    private lateinit var btnRestock: Button
    private lateinit var progressBar: ProgressBar
    
    private val branches = listOf(
        Branch(1, "Nairobi", "Nairobi"),
        Branch(2, "Kisumu", "Kisumu"),
        Branch(3, "Mombasa", "Mombasa"),
        Branch(4, "Nakuru", "Nakuru"),
        Branch(5, "Eldoret", "Eldoret")
    )
    
    private val products = listOf(
        Product(1, "Coke", 120.0),
        Product(2, "Fanta", 120.0),
        Product(3, "Sprite", 120.0)
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restock)
        
        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]
        customerViewModel = ViewModelProvider(this)[CustomerViewModel::class.java]
        
        initViews()
        setupSpinners()
        setupObservers()
        setupClickListeners()
    }
    
    private fun initViews() {
        spinnerBranch = findViewById(R.id.spinnerBranch)
        spinnerProduct = findViewById(R.id.spinnerProduct)
        etQuantity = findViewById(R.id.etQuantity)
        btnRestock = findViewById(R.id.btnRestock)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun setupSpinners() {
        val branchAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, branches.map { it.name })
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBranch.adapter = branchAdapter
        
        val productAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, products.map { it.name })
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProduct.adapter = productAdapter
    }
    
    private fun setupObservers() {
        viewModel.restockResult.observe(this) { result ->
            result.fold(
                onSuccess = {
                    Toast.makeText(this, "Restock successful!", Toast.LENGTH_SHORT).show()
                    etQuantity.text = ""
                },
                onFailure = { error ->
                    Toast.makeText(this, "Restock failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            btnRestock.isEnabled = !isLoading
        }
    }
    
    private fun setupClickListeners() {
        btnRestock.setOnClickListener {
            val selectedBranch = branches[spinnerBranch.selectedItemPosition]
            val selectedProduct = products[spinnerProduct.selectedItemPosition]
            val quantityText = etQuantity.text.toString()
            
            if (quantityText.isEmpty()) {
                etQuantity.error = "Quantity required"
                return@setOnClickListener
            }
            
            val quantity = quantityText.toIntOrNull()
            if (quantity == null || quantity <= 0) {
                etQuantity.error = "Invalid quantity"
                return@setOnClickListener
            }
            
            viewModel.restock(selectedBranch.id, selectedProduct.id, quantity)
        }
    }
}
