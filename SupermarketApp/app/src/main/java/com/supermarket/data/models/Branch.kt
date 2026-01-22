package com.supermarket.data.models

import com.google.gson.annotations.SerializedName

data class Branch(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("location")
    val location: String
)

data class Stock(
    @SerializedName("id")
    val id: Int,
    @SerializedName("branch_id")
    val branchId: Int,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("product")
    val product: Product
)
