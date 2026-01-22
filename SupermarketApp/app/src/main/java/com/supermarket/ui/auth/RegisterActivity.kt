package com.supermarket.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.supermarket.R
import com.supermarket.data.models.User
import com.supermarket.ui.auth.LoginViewModel

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var viewModel: LoginViewModel
    
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        
        initViews()
        setupObservers()
        setupClickListeners()
    }
    
    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun setupObservers() {
        viewModel.loginResult.observe(this) { result ->
            result.fold(
                onSuccess = { user: User ->
                    Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                },
                onFailure = { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                }
            )
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            btnRegister.isEnabled = !isLoading
        }
    }
    
    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            
            if (username.isEmpty()) {
                etUsername.error = "Username required"
                return@setOnClickListener
            }
            
            if (email.isEmpty()) {
                etEmail.error = "Email required"
                return@setOnClickListener
            }
            
            if (phone.isEmpty()) {
                etPhone.error = "Phone required"
                return@setOnClickListener
            }
            
            if (password.isEmpty()) {
                etPassword.error = "Password required"
                return@setOnClickListener
            }
            
            if (password != confirmPassword) {
                etConfirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }
            
            // Register user (using login viewmodel for simplicity)
            viewModelScope.launch {
                val repository = com.supermarket.data.repository.SupermarketRepository()
                val result = repository.register(username, email, phone, password)
                viewModel.loginResult.postValue(result)
            }
        }
    }
}
