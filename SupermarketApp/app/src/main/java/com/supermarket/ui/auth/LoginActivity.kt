package com.supermarket.ui.auth

import android.content.Intent
import android.os.Bundle
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
        setContentView(R.layout.activity_login)
        
        preferencesManager = PreferencesManager(this)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        
        initViews()
        setupObservers()
        setupClickListeners()
    }
    
    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun setupObservers() {
        viewModel.loginResult.observe(this) { result ->
            result.fold(
                onSuccess = { user ->
                    // Save user session
                    preferencesManager.saveBoolean(Constants.KEY_IS_LOGGED_IN, true)
                    preferencesManager.saveString(Constants.KEY_USER_ROLE, user.role)
                    preferencesManager.saveInt(Constants.KEY_USER_ID, user.id)
                    preferencesManager.saveString(Constants.KEY_USERNAME, user.username)
                    preferencesManager.saveString(Constants.KEY_AUTH_TOKEN, user.token ?: "")
                    
                    // Navigate based on role
                    when (user.role) {
                        "admin" -> {
                            startActivity(Intent(this, AdminDashboardActivity::class.java))
                        }
                        "customer" -> {
                            startActivity(Intent(this, CustomerDashboardActivity::class.java))
                        }
                    }
                    finish()
                },
                onFailure = { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
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
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            
            if (username.isEmpty()) {
                etUsername.error = "Username required"
                return@setOnClickListener
            }
            
            if (password.isEmpty()) {
                etPassword.error = "Password required"
                return@setOnClickListener
            }
            
            viewModel.login(username, password)
        }
        
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
