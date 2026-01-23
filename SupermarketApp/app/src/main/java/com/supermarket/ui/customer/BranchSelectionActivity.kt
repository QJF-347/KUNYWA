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
import com.supermarket.data.models.Branch

class BranchSelectionActivity : AppCompatActivity() {
    
    private lateinit var viewModel: CustomerViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var branchAdapter: BranchAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_branch_selection)
        
        viewModel = ViewModelProvider(this)[CustomerViewModel::class.java]
        
        initViews()
        setupRecyclerView()
        setupObservers()
        
        viewModel.loadBranches()
    }
    
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun setupRecyclerView() {
        branchAdapter = BranchAdapter { branch ->
            val intent = Intent(this, ProductListActivity::class.java)
            intent.putExtra("branch_id", branch.id)
            intent.putExtra("branch_name", branch.name)
            startActivity(intent)
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = branchAdapter
    }
    
    private fun setupObservers() {
        viewModel.branches.observe(this) { result ->
            result.fold(
                onSuccess = { branches ->
                    // Check if we have all expected branches
                    if (branches.size < 5) {
                        // Fallback: Add missing branches manually
                        val allBranches = branches.toMutableList()
                        val existingNames = branches.map { it.name }.toSet()
                        
                        // Add Nakuru if missing
                        if (!existingNames.contains("Nakuru")) {
                            allBranches.add(com.supermarket.data.models.Branch(4, "Nakuru", "Nakuru"))
                        }
                        
                        // Add Eldoret if missing
                        if (!existingNames.contains("Eldoret")) {
                            allBranches.add(com.supermarket.data.models.Branch(5, "Eldoret", "Eldoret"))
                        }
                        
                        branchAdapter.submitList(allBranches)
                    } else {
                        branchAdapter.submitList(branches)
                    }
                },
                onFailure = { error ->
                    // Fallback: Show hardcoded branches
                    val fallbackBranches = listOf(
                        com.supermarket.data.models.Branch(1, "Nairobi", "Nairobi"),
                        com.supermarket.data.models.Branch(2, "Kisumu", "Kisumu"),
                        com.supermarket.data.models.Branch(3, "Mombasa", "Mombasa"),
                        com.supermarket.data.models.Branch(4, "Nakuru", "Nakuru"),
                        com.supermarket.data.models.Branch(5, "Eldoret", "Eldoret")
                    )
                    branchAdapter.submitList(fallbackBranches)
                    Toast.makeText(this, "Using offline branch list", Toast.LENGTH_SHORT).show()
                }
            )
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }
    }
}

class BranchAdapter(
    private val onBranchClick: (Branch) -> Unit
) : androidx.recyclerview.widget.ListAdapter<Branch, BranchAdapter.BranchViewHolder>(BranchDiffCallback()) {
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): BranchViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_branch, parent, false)
        return BranchViewHolder(view, onBranchClick)
    }
    
    override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class BranchViewHolder(itemView: android.view.View, private val onBranchClick: (Branch) -> Unit) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val tvBranchName: TextView = itemView.findViewById(R.id.tvBranchName)
        private val tvBranchAbbreviation: TextView = itemView.findViewById(R.id.tvBranchAbbreviation)
        private val cardBranch: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cardBranch)
        
        fun bind(branch: Branch) {
            tvBranchName.text = branch.name
            
            // Hide abbreviation for all branches except Nairobi
            when (branch.name) {
                "Nairobi" -> {
                    tvBranchAbbreviation.text = "HQ"
                    tvBranchAbbreviation.visibility = android.view.View.VISIBLE
                    cardBranch.setCardBackgroundColor(android.graphics.Color.parseColor("#2E86AB")) // Blue
                    tvBranchName.setTextColor(android.graphics.Color.parseColor("#FFFFFF")) // White text on blue
                }
                "Kisumu" -> {
                    tvBranchAbbreviation.visibility = android.view.View.GONE
                    cardBranch.setCardBackgroundColor(android.graphics.Color.parseColor("#A23B72")) // Purple
                    tvBranchName.setTextColor(android.graphics.Color.parseColor("#FFFFFF")) // White text on purple
                }
                "Mombasa" -> {
                    tvBranchAbbreviation.visibility = android.view.View.GONE
                    cardBranch.setCardBackgroundColor(android.graphics.Color.parseColor("#F18F01")) // Orange
                    tvBranchName.setTextColor(android.graphics.Color.parseColor("#FFFFFF")) // White text on orange
                }
                "Nakuru" -> {
                    tvBranchAbbreviation.visibility = android.view.View.GONE
                    cardBranch.setCardBackgroundColor(android.graphics.Color.parseColor("#C73E1D")) // Red
                    tvBranchName.setTextColor(android.graphics.Color.parseColor("#FFFFFF")) // White text
                }
                "Eldoret" -> {
                    tvBranchAbbreviation.visibility = android.view.View.GONE
                    cardBranch.setCardBackgroundColor(android.graphics.Color.parseColor("#6A994E")) // Green
                    tvBranchName.setTextColor(android.graphics.Color.parseColor("#FFFFFF")) // White text
                }
                else -> {
                    tvBranchAbbreviation.visibility = android.view.View.GONE
                    cardBranch.setCardBackgroundColor(android.graphics.Color.parseColor("#5C5C5C")) // Gray
                    tvBranchName.setTextColor(android.graphics.Color.parseColor("#FFFFFF")) // White text
                }
            }
            
            itemView.setOnClickListener {
                onBranchClick(branch)
            }
        }
    }
}

class BranchDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Branch>() {
    override fun areItemsTheSame(oldItem: Branch, newItem: Branch): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Branch, newItem: Branch): Boolean {
        return oldItem == newItem
    }
}
