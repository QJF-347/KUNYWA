package com.supermarket.utils

object Constants {
    const val BASE_URL = "https://kunywa-bakend.onrender.com/" // Deployed backend URL
    
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
}
