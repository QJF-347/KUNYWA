package com.supermarket.ui.customer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.supermarket.R
import com.supermarket.ui.auth.LoginActivity
import com.supermarket.utils.Constants
import com.supermarket.utils.PreferencesManager

class CustomerDashboardActivity : AppCompatActivity() {
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var tvWelcome: TextView
    private lateinit var btnSelectBranch: Button
    private lateinit var btnLogout: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_dashboard)
        
        preferencesManager = PreferencesManager(this)
        
        initViews()
        setupClickListeners()
        displayUserInfo()
    }
    
    private fun initViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        btnSelectBranch = findViewById(R.id.btnSelectBranch)
        btnLogout = findViewById(R.id.btnLogout)
    }
    
    private fun setupClickListeners() {
        btnSelectBranch.setOnClickListener {
            startActivity(Intent(this, BranchSelectionActivity::class.java))
        }
        
        btnLogout.setOnClickListener {
            preferencesManager.clearAll()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    private fun displayUserInfo() {
        val username = preferencesManager.getString(Constants.KEY_USERNAME, "")
        tvWelcome.text = "Welcome, $username!"
    }
}
