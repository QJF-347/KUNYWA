package com.supermarket.ui.admin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.supermarket.R
import com.supermarket.data.models.Stock

class StockItemAdapter(
    private val stockList: List<Stock>
) : RecyclerView.Adapter<StockItemAdapter.StockItemViewHolder>() {

    class StockItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stock_product, parent, false)
        return StockItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockItemViewHolder, position: Int) {
        val stock = stockList[position]
        
        holder.tvProductName.text = stock.productName
        holder.tvQuantity.text = stock.quantity.toString()
        holder.tvPrice.text = String.format("KES %.2f", stock.price)
    }

    override fun getItemCount(): Int = stockList.size
}
