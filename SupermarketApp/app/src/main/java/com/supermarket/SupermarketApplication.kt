package com.supermarket

import android.app.Application
import android.util.Log

class SupermarketApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        Log.d("SupermarketApp", "Application started successfully")
    }
}
