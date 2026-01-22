package com.supermarket.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supermarket.data.models.User
import com.supermarket.data.repository.SupermarketRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    
    private val repository = SupermarketRepository()
    
    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun login(username: String, password: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            val result = repository.login(username, password)
            _loginResult.value = result
            _isLoading.value = false
        }
    }
}
