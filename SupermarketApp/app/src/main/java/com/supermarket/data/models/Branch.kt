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
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price")
    val price: Double
)
