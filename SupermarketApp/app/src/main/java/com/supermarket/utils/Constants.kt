package com.supermarket.utils

object Constants {
    const val BASE_URL = "https://kunywa-bakend.onrender.com/" // Deployed backend URL
    
    // M-Pesa Configuration
    const val MPESA_CONSUMER_KEY = "6U8UmjMUtn7MgUs2FiFEU9wG0GhrSNXSXMaXw5ikxnIzzlaG"
    const val MPESA_CONSUMER_SECRET = "PaM9cBZpk9MC2NEFXQChRmMvS21mebZUMMpRZYdVxUVmrApdkEwvXImJVV8vhxcG"
    const val MPESA_SHORTCODE = "174379"
    const val MPESA_PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"
    const val MPESA_CALLBACK_URL = "https://webhook.site/1ce723ac-ef61-4f40-95ef-33f7f5c0c28f"
    
    // SharedPreferences keys
    const val PREF_NAME = "SupermarketPrefs"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_USER_ROLE = "user_role"
    const val KEY_USER_ID = "user_id"
    const val KEY_USERNAME = "username"
    const val KEY_AUTH_TOKEN = "auth_token"
    
    // Product prices (fixed across all branches)
    const val COKE_PRICE = 120.0
    const val FANTA_PRICE = 120.0
    const val SPRITE_PRICE = 120.0
    
    // Branch locations
    const val NAIROBI = "Nairobi"
    const val KISUMU = "Kisumu"
    const val MOMBASA = "Mombasa"
    const val NAKURU = "Nakuru"
    const val ELDORET = "Eldoret"
    
    // All branches list for fallback
    val ALL_BRANCHES = listOf(NAIROBI, KISUMU, MOMBASA, NAKURU, ELDORET)
}
