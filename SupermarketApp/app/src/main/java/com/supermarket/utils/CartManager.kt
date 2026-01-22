package com.supermarket.utils

import com.supermarket.data.models.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object CartManager {
    private val _cart = MutableStateFlow<MutableList<CartItem>>(mutableListOf())
    val cart: StateFlow<List<CartItem>> = _cart
    
    fun addToCart(cartItem: CartItem) {
        val currentCart = _cart.value.toMutableList()
        val existingItem = currentCart.find { it.product.id == cartItem.product.id && it.branchId == cartItem.branchId }
        
        if (existingItem != null) {
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
            currentCart[currentCart.indexOf(existingItem)] = updatedItem
        } else {
            currentCart.add(cartItem)
        }
        
        _cart.value = currentCart
    }
    
    fun removeFromCart(cartItem: CartItem) {
        val currentCart = _cart.value.toMutableList()
        val item = currentCart.find { it.product.id == cartItem.product.id && it.branchId == cartItem.branchId }
        
        if (item != null) {
            if (item.quantity > 1) {
                val updatedItem = item.copy(quantity = item.quantity - 1)
                currentCart[currentCart.indexOf(item)] = updatedItem
            } else {
                currentCart.remove(item)
            }
        }
        
        _cart.value = currentCart
    }
    
    fun clearCart() {
        _cart.value = mutableListOf()
    }
    
    fun getCartTotal(): Double {
        return _cart.value.sumOf { it.getTotalPrice() }
    }
    
    fun getItemCount(): Int {
        return _cart.value.sumOf { it.quantity }
    }
}
