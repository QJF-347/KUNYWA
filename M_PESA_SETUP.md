# M-Pesa Sandbox Configuration

## 📋 Overview
This document explains how to configure M-Pesa integration for the supermarket system. For demo purposes, the system uses simulated payments, but real M-Pesa integration can be enabled by following these steps.

## 🔧 Real M-Pesa Integration

### 1. Get Sandbox Credentials
1. Visit [Safaricom Developer Portal](https://developer.safaricom.co.ke/)
2. Create an account and log in
3. Create a new app
4. Get your Consumer Key and Consumer Secret

### 2. Update Configuration
In `supermarket-backend/src/main/kotlin/config/MpesaConfig.kt`:

```kotlin
object MpesaConfig {
    // Replace with your actual sandbox credentials
    const val CONSUMER_KEY = "YOUR_ACTUAL_CONSUMER_KEY"
    const val CONSUMER_SECRET = "YOUR_ACTUAL_CONSUMER_SECRET"
    const val SHORTCODE = "174379"  // Test shortcode
    const val PASSKEY = "YOUR_ACTUAL_PASSKEY"
    
    const val BASE_URL = "https://sandbox.safaricom.co.ke"
    const val OAUTH_ENDPOINT = "/oauth/v1/generate?grant_type=client_credentials"
    const val STK_PUSH_ENDPOINT = "/mpesa/stkpush/v1/processrequest"
    const val QUERY_ENDPOINT = "/mpesa/stkpushquery/v1/query"
    
    // Set to false for real M-Pesa integration
    const val SIMULATE_SUCCESS = false
}
```

### 3. Test Phone Numbers
Use these test numbers for sandbox:
- `254708374149` - Success
- `254712345678` - Default success
- `254700000000` - Any valid Kenya number

## 🎯 Demo Mode (Current Setup)

The system is currently configured for **demo mode** with simulated payments:

```kotlin
const val SIMULATE_SUCCESS = true
```

### How Demo Mode Works
1. Customer initiates payment
2. System generates fake checkout request ID
3. Payment is marked as "completed" instantly
4. Sale is processed and stock updated
5. Customer sees success message

### Benefits of Demo Mode
- ✅ No internet connection required
- ✅ No M-Pesa credentials needed
- ✅ Instant payment processing
- ✅ Perfect for classroom demos
- ✅ No API rate limits

## 🔄 Switching to Real M-Pesa

To enable real M-Pesa integration:

1. **Get Credentials**: Follow step 1 above
2. **Update Config**: Replace placeholder values in `MpesaConfig.kt`
3. **Disable Simulation**: Set `SIMULATE_SUCCESS = false`
4. **Restart Server**: Restart the backend server
5. **Test**: Make a small test transaction

## 📱 Testing Real Integration

### Test Transaction Flow
1. Customer selects products and proceeds to checkout
2. System sends STK Push request to M-Pesa
3. Customer receives actual M-Pesa prompt on phone
4. Customer enters PIN to confirm payment
5. System checks payment status
6. Sale is completed upon successful payment

### Sample STK Push Request
```json
{
  "BusinessShortCode": "174379",
  "Password": "generated_password",
  "Timestamp": "20240122120000",
  "TransactionType": "CustomerPayBillOnline",
  "Amount": 120,
  "PartyA": "254712345678",
  "PartyB": "174379",
  "PhoneNumber": "254712345678",
  "CallBackURL": "http://your-server/mpesa/callback",
  "AccountReference": "SUPERMARKET-1",
  "TransactionDesc": "Purchase at Nairobi"
}
```

## ⚠️ Important Notes

### Security
- Never commit real credentials to version control
- Use environment variables in production
- Implement proper callback URL handling
- Validate all payment responses

### Rate Limits
- Sandbox has rate limits
- Implement proper error handling
- Add retry logic for failed requests
- Monitor API usage

### Callback URL
For production, you'll need:
- Publicly accessible callback URL
- HTTPS certificate
- Proper request validation
- Webhook security

## 🎓 For Academic Demo

**Keep demo mode enabled** for classroom presentations:
- No setup required
- Works offline
- Instant results
- No API dependencies
- Reliable for grading

## 🆘 Troubleshooting

### Common Issues
1. **Invalid Credentials**: Check Consumer Key/Secret
2. **Invalid Phone Number**: Use Kenya format (254...)
3. **Network Issues**: Ensure internet connectivity
4. **Callback URL**: Must be accessible from internet

### Debug Mode
Enable logging in `Application.kt`:
```kotlin
install(CallLogging) {
    level = Level.DEBUG
}
```

---

**For class demo: Keep SIMULATE_SUCCESS = true** ✅
