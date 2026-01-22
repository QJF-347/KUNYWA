package com.supermarket.ui.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supermarket.data.models.*
import com.supermarket.data.repository.SupermarketRepository
import kotlinx.coroutines.launch

class CustomerViewModel : ViewModel() {
    
    private val repository = SupermarketRepository()
    
    private val _branches = MutableLiveData<Result<List<Branch>>>()
    val branches: LiveData<Result<List<Branch>>> = _branches
    
    private val _branchStock = MutableLiveData<Result<List<Stock>>>()
    val branchStock: LiveData<Result<List<Stock>>> = _branchStock
    
    private val _saleResult = MutableLiveData<Result<Sale>>()
    val saleResult: LiveData<Result<Sale>> = _saleResult
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _cart = MutableLiveData<MutableList<CartItem>>()
    val cart: LiveData<MutableList<CartItem>> = _cart
    
    init {
        _cart.value = mutableListOf()
    }
    
    fun loadBranches() {
        _isLoading.value = true
        
        viewModelScope.launch {
            val result = repository.getBranches()
            _branches.value = result
            _isLoading.value = false
        }
    }
    
    fun loadBranchStock(branchId: Int) {
        _isLoading.value = true
        
        viewModelScope.launch {
            val result = repository.getBranchStock(branchId)
            _branchStock.value = result
            _isLoading.value = false
        }
    }
    
    fun addToCart(product: Product, branchId: Int) {
        val currentCart = _cart.value?.toMutableList() ?: mutableListOf()
        val existingItem = currentCart.find { it.product.id == product.id && it.branchId == branchId }
        
        if (existingItem != null) {
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
            currentCart[currentCart.indexOf(existingItem)] = updatedItem
        } else {
            currentCart.add(CartItem(product, 1, branchId))
        }
        
        _cart.value = currentCart
    }
    
    fun removeFromCart(product: Product, branchId: Int) {
        val currentCart = _cart.value?.toMutableList() ?: mutableListOf()
        val item = currentCart.find { it.product.id == product.id && it.branchId == branchId }
        
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
        return _cart.value?.sumOf { it.getTotalPrice() } ?: 0.0
    }
    
    fun checkout(branchId: Int) {
        val cartItems = _cart.value ?: return
        
        if (cartItems.isEmpty()) {
            _saleResult.value = Result.failure(Exception("Cart is empty"))
            return
        }
        
        _isLoading.value = true
        
        viewModelScope.launch {
            val saleItems = cartItems.map { 
                SaleItem(it.product.id, it.quantity) 
            }
            
            val result = repository.createSale(branchId, saleItems)
            _saleResult.value = result
            _isLoading.value = false
        }
    }
}
