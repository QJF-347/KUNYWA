package com.supermarket.config

object MpesaConfig {
    // Get credentials from environment variables for production
    val CONSUMER_KEY: String get() = System.getenv("MPESA_CONSUMER_KEY") ?: "YOUR_CONSUMER_KEY"
    val CONSUMER_SECRET: String get() = System.getenv("MPESA_CONSUMER_SECRET") ?: "YOUR_CONSUMER_SECRET"
    val PASSKEY: String get() = System.getenv("MPESA_PASSKEY") ?: "YOUR_PASSKEY"
    
    const val SHORTCODE = "174379"
    
    const val BASE_URL = "https://sandbox.safaricom.co.ke"
    const val OAUTH_ENDPOINT = "/oauth/v1/generate?grant_type=client_credentials"
    const val STK_PUSH_ENDPOINT = "/mpesa/stkpush/v1/processrequest"
    const val QUERY_ENDPOINT = "/mpesa/stkpushquery/v1/query"
    
    // For demo purposes, can be overridden by environment variable
    val SIMULATE_SUCCESS: Boolean get() = System.getenv("SIMULATE_MPESA")?.toBoolean() ?: true
}
