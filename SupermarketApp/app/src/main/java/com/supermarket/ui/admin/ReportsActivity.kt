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
import com.supermarket.data.models.SalesReport

class ReportsActivity : AppCompatActivity() {
    
    private lateinit var viewModel: AdminViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvGrandTotal: TextView
    private lateinit var btnRefresh: Button
    private lateinit var reportsAdapter: ReportsAdapter
    
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
        btnRefresh = findViewById(R.id.btnRefresh)
    }
    
    private fun setupRecyclerView() {
        reportsAdapter = ReportsAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = reportsAdapter
    }
    
    private fun setupObservers() {
        viewModel.reports.observe(this) { result ->
            result.fold(
                onSuccess = { report ->
                    reportsAdapter.submitList(report.salesByProduct)
                    tvGrandTotal.text = "Grand Total Income: KES ${report.grandTotalIncome}"
                },
                onFailure = { error ->
                    tvGrandTotal.text = "Error loading reports"
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

class ReportsAdapter : androidx.recyclerview.widget.ListAdapter<SalesReport, ReportsAdapter.ReportsViewHolder>(ReportsDiffCallback()) {
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ReportsViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportsViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ReportsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ReportsViewHolder(itemView: android.view.View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvTotalQuantity: TextView = itemView.findViewById(R.id.tvTotalQuantity)
        private val tvTotalIncome: TextView = itemView.findViewById(R.id.tvTotalIncome)
        
        fun bind(report: SalesReport) {
            tvProductName.text = report.productName
            tvTotalQuantity.text = "Units Sold: ${report.totalQuantity}"
            tvTotalIncome.text = "Income: KES ${report.totalIncome}"
        }
    }
}

class ReportsDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<SalesReport>() {
    override fun areItemsTheSame(oldItem: SalesReport, newItem: SalesReport): Boolean {
        return oldItem.productName == newItem.productName
    }
    
    override fun areContentsTheSame(oldItem: SalesReport, newItem: SalesReport): Boolean {
        return oldItem == newItem
    }
}
