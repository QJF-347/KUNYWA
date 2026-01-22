package com.supermarket.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supermarket.data.models.OverallReport
import com.supermarket.data.repository.SupermarketRepository

class AdminViewModel : ViewModel() {
    
    private val repository = SupermarketRepository()
    
    private val _restockResult = MutableLiveData<Result<Unit>>()
    val restockResult: LiveData<Result<Unit>> = _restockResult
    
    private val _reports = MutableLiveData<Result<OverallReport>>()
    val reports: LiveData<Result<OverallReport>> = _reports
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun restock(branchId: Int, productId: Int, quantity: Int) {
        _isLoading.value = true
        
        viewModelScope.launch {
            val result = repository.restock(branchId, productId, quantity)
            _restockResult.value = result
            _isLoading.value = false
        }
    }
    
    fun loadReports() {
        _isLoading.value = true
        
        viewModelScope.launch {
            val result = repository.getReports()
            _reports.value = result
            _isLoading.value = false
        }
    }
}
