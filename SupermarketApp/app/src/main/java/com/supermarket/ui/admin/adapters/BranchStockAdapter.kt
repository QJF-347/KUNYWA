package com.supermarket.ui.admin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.supermarket.R
import com.supermarket.data.models.BranchStock
import com.supermarket.data.models.Stock

class BranchStockAdapter(
    private val branchStockList: List<BranchStock>
) : RecyclerView.Adapter<BranchStockAdapter.BranchStockViewHolder>() {

    class BranchStockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBranchName: TextView = itemView.findViewById(R.id.tvBranchName)
        val recyclerViewStockItems: RecyclerView = itemView.findViewById(R.id.recyclerViewStockItems)
        val tvEmptyState: TextView = itemView.findViewById(R.id.tvEmptyState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchStockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_branch_stock, parent, false)
        return BranchStockViewHolder(view)
    }

    override fun onBindViewHolder(holder: BranchStockViewHolder, position: Int) {
        val branchStock = branchStockList[position]
        
        holder.tvBranchName.text = branchStock.branchName
        
        if (branchStock.stocks.isEmpty()) {
            holder.recyclerViewStockItems.visibility = View.GONE
            holder.tvEmptyState.visibility = View.VISIBLE
        } else {
            holder.recyclerViewStockItems.visibility = View.VISIBLE
            holder.tvEmptyState.visibility = View.GONE
            
            val stockAdapter = StockItemAdapter(branchStock.stocks)
            holder.recyclerViewStockItems.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.recyclerViewStockItems.adapter = stockAdapter
        }
    }

    override fun getItemCount(): Int = branchStockList.size
}
