package com.supermarket.data.models

import com.google.gson.annotations.SerializedName

data class Sale(
    @SerializedName("id")
    val id: Int,
    @SerializedName("branchId")
    val branchId: Int,
    @SerializedName("items")
    val items: List<SaleItem>,
    @SerializedName("total")
    val total: Double
)

data class SalesReport(
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("total_quantity")
    val totalQuantity: Int,
    @SerializedName("total_income")
    val totalIncome: Double
)

data class OverallReport(
    @SerializedName("totalSales")
    val totalSales: Double,
    @SerializedName("totalOrders")
    val totalOrders: Int
)
