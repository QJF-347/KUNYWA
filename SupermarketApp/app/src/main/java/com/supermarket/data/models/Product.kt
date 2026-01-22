package com.supermarket.data.models

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Double
)

data class CartItem(
    val product: Product,
    val quantity: Int,
    val branchId: Int
) {
    fun getTotalPrice(): Double {
        return product.price * quantity
    }
}
