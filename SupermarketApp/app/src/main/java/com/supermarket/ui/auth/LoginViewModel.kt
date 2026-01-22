package com.supermarket.ui.auth

import android.util.Log
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
        Log.d("LoginViewModel", "Starting login for username: $username")
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Calling repository.login()")
                val result = repository.login(username, password)
                Log.d("LoginViewModel", "Repository login result: $result")
                _loginResult.value = result
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Exception during login: ${e.message}", e)
                _loginResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun setLoginResult(result: Result<User>) {
        Log.d("LoginViewModel", "Setting login result: $result")
        _loginResult.value = result
    }
}
