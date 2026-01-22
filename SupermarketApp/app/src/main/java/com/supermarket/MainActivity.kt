package com.supermarket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.supermarket.ui.auth.LoginActivity
import com.supermarket.utils.Constants
import com.supermarket.utils.PreferencesManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        preferencesManager = PreferencesManager(this)
        
        // Check if user is already logged in
        val isLoggedIn = preferencesManager.getBoolean(Constants.KEY_IS_LOGGED_IN, false)
        val userRole = preferencesManager.getString(Constants.KEY_USER_ROLE, "")
        
        if (isLoggedIn) {
            when (userRole) {
                "admin" -> {
                    startActivity(Intent(this, com.supermarket.ui.admin.AdminDashboardActivity::class.java))
                }
                "customer" -> {
                    startActivity(Intent(this, com.supermarket.ui.customer.CustomerDashboardActivity::class.java))
                }
            }
            finish()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
