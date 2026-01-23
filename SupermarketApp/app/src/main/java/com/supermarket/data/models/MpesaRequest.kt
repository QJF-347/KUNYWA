package com.supermarket.data.models

import com.google.gson.annotations.SerializedName

data class MpesaRequest(
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("accountReference")
    val accountReference: String,
    @SerializedName("transactionDesc")
    val transactionDesc: String
)

data class MpesaResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("checkoutRequestId")
    val checkoutRequestId: String? = null
)

data class SaleRequest(
    @SerializedName("branchId")
    val branchId: Int,
    @SerializedName("items")
    val items: List<SaleItem>,
    @SerializedName("total")
    val total: Double
)

data class SaleItem(
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("quantity")
    val quantity: Int
)

data class RestockRequest(
    @SerializedName("branchId")
    val branchId: Int,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("quantity")
    val quantity: Int
)
