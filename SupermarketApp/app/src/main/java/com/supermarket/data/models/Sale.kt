package com.supermarket.data.models

import com.google.gson.annotations.SerializedName

data class Sale(
    @SerializedName("id")
    val id: Int,
    @SerializedName("branch_id")
    val branchId: Int,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("total_amount")
    val totalAmount: Double,
    @SerializedName("customer_phone")
    val customerPhone: String,
    @SerializedName("payment_status")
    val paymentStatus: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("product")
    val product: Product,
    @SerializedName("branch")
    val branch: Branch
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
    @SerializedName("sales_by_product")
    val salesByProduct: List<SalesReport>,
    @SerializedName("grand_total_income")
    val grandTotalIncome: Double
)
