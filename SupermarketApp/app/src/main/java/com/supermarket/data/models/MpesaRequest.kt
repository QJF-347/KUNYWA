package com.supermarket.data.models

import com.google.gson.annotations.SerializedName

data class MpesaRequest(
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("account_reference")
    val accountReference: String,
    @SerializedName("transaction_desc")
    val transactionDesc: String
)

data class MpesaResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("checkout_request_id")
    val checkoutRequestId: String? = null,
    @SerializedName("merchant_request_id")
    val merchantRequestId: String? = null
)

data class SaleRequest(
    @SerializedName("branch_id")
    val branchId: Int,
    @SerializedName("items")
    val items: List<SaleItem>
)

data class SaleItem(
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("quantity")
    val quantity: Int
)
