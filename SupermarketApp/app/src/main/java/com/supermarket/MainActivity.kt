package com.supermarket

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.supermarket.ui.auth.LoginActivity
import com.supermarket.utils.Constants
import com.supermarket.utils.PreferencesManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_main)
            preferencesManager = PreferencesManager(this)
            
            // Check if user is already logged in
            val isLoggedIn = preferencesManager.getBoolean(Constants.KEY_IS_LOGGED_IN, false)
            val userRole = preferencesManager.getString(Constants.KEY_USER_ROLE, "")
            
            Log.d("MainActivity", "isLoggedIn: $isLoggedIn, userRole: $userRole")
            
            if (isLoggedIn) {
                when (userRole) {
                    "admin" -> {
                        startActivity(Intent(this, com.supermarket.ui.admin.AdminDashboardActivity::class.java))
                    }
                    "customer" -> {
                        startActivity(Intent(this, com.supermarket.ui.customer.CustomerDashboardActivity::class.java))
                    }
                    else -> {
                        Log.w("MainActivity", "Unknown user role: $userRole, redirecting to login")
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                }
                finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in MainActivity: ${e.message}", e)
            Toast.makeText(this, "Error starting app: ${e.message}", Toast.LENGTH_LONG).show()
            
            // Fallback to login screen
            try {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } catch (fallbackException: Exception) {
                Log.e("MainActivity", "Fallback failed: ${fallbackException.message}", fallbackException)
                Toast.makeText(this, "Critical error: Please restart app", Toast.LENGTH_LONG).show()
            }
        }
    }
}
