package com.supermarket.ui.admin

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.supermarket.R
import com.supermarket.data.models.OverallReport
import com.supermarket.data.models.BranchStock
import com.supermarket.ui.admin.adapters.BranchStockAdapter

class ReportsActivity : AppCompatActivity() {
    
    private lateinit var viewModel: AdminViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvGrandTotal: TextView
    private lateinit var tvTotalSales: TextView
    private lateinit var tvTotalOrders: TextView
    private lateinit var btnRefresh: Button
    private lateinit var branchStockAdapter: BranchStockAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)
        
        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]
        
        initViews()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        // Load both reports and stock data
        viewModel.loadReports()
        viewModel.loadAllBranchesStock()
    }
    
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        tvGrandTotal = findViewById(R.id.tvGrandTotal)
        tvTotalSales = findViewById(R.id.tvTotalSales)
        tvTotalOrders = findViewById(R.id.tvTotalOrders)
        btnRefresh = findViewById(R.id.btnRefresh)
    }
    
    private fun setupRecyclerView() {
        recyclerView.visibility = android.view.View.VISIBLE
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
    
    private fun setupObservers() {
        // Observe reports data
        viewModel.reports.observe(this) { result ->
            result.fold(
                onSuccess = { report ->
                    // Update summary TextViews with proper formatting
                    tvGrandTotal.text = String.format("KES %.2f", report.totalSales)
                    tvTotalSales.text = String.format("KES %.2f", report.totalSales)
                    tvTotalOrders.text = report.totalOrders.toString()
                },
                onFailure = { error ->
                    tvGrandTotal.text = "Error"
                    tvTotalSales.text = "Error"
                    tvTotalOrders.text = "Error"
                }
            )
        }
        
        // Observe stock data
        viewModel.allBranchesStock.observe(this) { result ->
            result.fold(
                onSuccess = { branchStockList ->
                    branchStockAdapter = BranchStockAdapter(branchStockList)
                    recyclerView.adapter = branchStockAdapter
                },
                onFailure = { error ->
                    // Show empty state or error message
                    branchStockAdapter = BranchStockAdapter(emptyList())
                    recyclerView.adapter = branchStockAdapter
                }
            )
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            btnRefresh.isEnabled = !isLoading
        }
    }
    
    private fun setupClickListeners() {
        btnRefresh.setOnClickListener {
            viewModel.loadReports()
            viewModel.loadAllBranchesStock()
        }
    }
}
