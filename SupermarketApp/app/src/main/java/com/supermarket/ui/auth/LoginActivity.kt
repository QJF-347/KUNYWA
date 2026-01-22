package com.supermarket.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.supermarket.MainActivity
import com.supermarket.R
import com.supermarket.ui.admin.AdminDashboardActivity
import com.supermarket.ui.customer.CustomerDashboardActivity
import com.supermarket.utils.Constants
import com.supermarket.utils.PreferencesManager

class LoginActivity : AppCompatActivity() {
    
    private lateinit var viewModel: LoginViewModel
    private lateinit var preferencesManager: PreferencesManager
    
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var progressBar: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_login)
            viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
            preferencesManager = PreferencesManager(this)
            
            initViews()
            setupObservers()
            setupClickListeners()
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error loading login screen: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun initViews() {
        try {
            etUsername = findViewById(R.id.etUsername)
            etPassword = findViewById(R.id.etPassword)
            btnLogin = findViewById(R.id.btnLogin)
            tvRegister = findViewById(R.id.tvRegister)
            progressBar = findViewById(R.id.progressBar)
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error initializing views: ${e.message}", e)
            Toast.makeText(this, "Error initializing login screen: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun setupObservers() {
        viewModel.loginResult.observe(this) { result ->
            result.fold(
                onSuccess = { user ->
                    try {
                        Log.d("LoginActivity", "Login successful for user: ${user.username}, role: ${user.role}")
                        
                        // Validate user data before proceeding
                        if (user.username.isNullOrEmpty() || user.role.isNullOrEmpty()) {
                            Log.e("LoginActivity", "Invalid user data received: username=${user.username}, role=${user.role}")
                            Toast.makeText(this, "Invalid user data received", Toast.LENGTH_LONG).show()
                            return@fold
                        }
                        
                        // Save user session
                        preferencesManager.saveBoolean(Constants.KEY_IS_LOGGED_IN, true)
                        preferencesManager.saveString(Constants.KEY_USER_ROLE, user.role ?: "customer")
                        preferencesManager.saveInt(Constants.KEY_USER_ID, user.id)
                        preferencesManager.saveString(Constants.KEY_USERNAME, user.username ?: "")
                        
                        // Navigate based on role
                        when (user.role) {
                            "admin" -> {
                                Log.d("LoginActivity", "Navigating to AdminDashboardActivity")
                                try {
                                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                                } catch (e: Exception) {
                                    Log.e("LoginActivity", "Error starting AdminDashboardActivity: ${e.message}", e)
                                    Toast.makeText(this, "Error opening admin dashboard", Toast.LENGTH_LONG).show()
                                }
                            }
                            "customer" -> {
                                Log.d("LoginActivity", "Navigating to CustomerDashboardActivity")
                                try {
                                    startActivity(Intent(this, CustomerDashboardActivity::class.java))
                                } catch (e: Exception) {
                                    Log.e("LoginActivity", "Error starting CustomerDashboardActivity: ${e.message}", e)
                                    Toast.makeText(this, "Error opening customer dashboard", Toast.LENGTH_LONG).show()
                                }
                            }
                            else -> {
                                Log.w("LoginActivity", "Unknown user role: ${user.role}")
                                Toast.makeText(this, "Unknown user role: ${user.role}", Toast.LENGTH_LONG).show()
                            }
                        }
                        finish()
                    } catch (e: Exception) {
                        Log.e("LoginActivity", "Error during login success handling: ${e.message}", e)
                        Toast.makeText(this, "Error navigating after login: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                },
                onFailure = { error ->
                    Log.e("LoginActivity", "Login failed: ${error.message}", error)
                    Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            btnLogin.isEnabled = !isLoading
        }
    }
    
    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            try {
                val username = etUsername.text.toString().trim()
                val password = etPassword.text.toString().trim()
                
                Log.d("LoginActivity", "Login attempt for username: $username")
                
                if (username.isEmpty()) {
                    etUsername.error = "Username required"
                    return@setOnClickListener
                }
                
                if (password.isEmpty()) {
                    etPassword.error = "Password required"
                    return@setOnClickListener
                }
                
                Log.d("LoginActivity", "Calling viewModel.login()")
                viewModel.login(username, password)
            } catch (e: Exception) {
                Log.e("LoginActivity", "Error in login button click: ${e.message}", e)
                Toast.makeText(this, "Error during login: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        
        tvRegister.setOnClickListener {
            try {
                Log.d("LoginActivity", "Navigating to RegisterActivity")
                startActivity(Intent(this, RegisterActivity::class.java))
            } catch (e: Exception) {
                Log.e("LoginActivity", "Error navigating to register: ${e.message}", e)
                Toast.makeText(this, "Error opening registration: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
