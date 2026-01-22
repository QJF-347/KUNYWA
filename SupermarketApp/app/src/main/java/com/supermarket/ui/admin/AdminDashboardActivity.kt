package com.supermarket.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.supermarket.R
import com.supermarket.ui.auth.LoginActivity
import com.supermarket.utils.Constants
import com.supermarket.utils.PreferencesManager

class AdminDashboardActivity : AppCompatActivity() {
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var tvWelcome: TextView
    private lateinit var btnRestock: Button
    private lateinit var btnReports: Button
    private lateinit var btnLogout: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)
        
        preferencesManager = PreferencesManager(this)
        
        initViews()
        setupClickListeners()
        displayUserInfo()
    }
    
    private fun initViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        btnRestock = findViewById(R.id.btnRestock)
        btnReports = findViewById(R.id.btnReports)
        btnLogout = findViewById(R.id.btnLogout)
    }
    
    private fun setupClickListeners() {
        btnRestock.setOnClickListener {
            startActivity(Intent(this, RestockActivity::class.java))
        }
        
        btnReports.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }
        
        btnLogout.setOnClickListener {
            preferencesManager.clearAll()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    private fun displayUserInfo() {
        val username = preferencesManager.getString(Constants.KEY_USERNAME, "")
        tvWelcome.text = "Admin Dashboard - Welcome, $username!"
    }
}
