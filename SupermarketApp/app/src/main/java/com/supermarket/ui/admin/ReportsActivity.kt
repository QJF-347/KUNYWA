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

class ReportsActivity : AppCompatActivity() {
    
    private lateinit var viewModel: AdminViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvGrandTotal: TextView
    private lateinit var tvTotalSales: TextView
    private lateinit var tvTotalOrders: TextView
    private lateinit var btnRefresh: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)
        
        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]
        
        initViews()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        viewModel.loadReports()
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
        // Since we only have summary data, we don't need a RecyclerView for now
        recyclerView.visibility = android.view.View.GONE
    }
    
    private fun setupObservers() {
        viewModel.reports.observe(this) { result ->
            result.fold(
                onSuccess = { report ->
                    // Update all summary TextViews
                    tvGrandTotal.text = report.totalSales.toString()
                    tvTotalSales.text = report.totalSales.toString()
                    tvTotalOrders.text = report.totalOrders.toString()
                },
                onFailure = { error ->
                    tvGrandTotal.text = "Error"
                    tvTotalSales.text = "Error"
                    tvTotalOrders.text = "Error"
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
        }
    }
}
