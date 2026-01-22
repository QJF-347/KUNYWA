package com.supermarket.utils

import android.content.Context
import android.widget.Toast
import com.supermarket.data.models.MpesaResponse
import com.supermarket.data.repository.SupermarketRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MpesaManager(private val context: Context) {
    
    private val repository = SupermarketRepository()
    
    fun initiatePayment(
        phoneNumber: String,
        amount: Double,
        accountReference: String,
        transactionDesc: String,
        onPaymentInitiated: (MpesaResponse) -> Unit,
        onPaymentFailed: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Format phone number (remove leading 0 and add 254)
                val formattedPhone = if (phoneNumber.startsWith("0")) {
                    "254${phoneNumber.substring(1)}"
                } else if (phoneNumber.startsWith("+254")) {
                    phoneNumber.substring(1)
                } else if (phoneNumber.startsWith("254")) {
                    phoneNumber
                } else {
                    "254$phoneNumber"
                }
                
                val result = repository.initiateStkPush(formattedPhone, amount, accountReference, transactionDesc)
                
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = { response ->
                            if (response.success) {
                                onPaymentInitiated(response)
                                Toast.makeText(context, "M-Pesa STK Push sent. Please check your phone.", Toast.LENGTH_LONG).show()
                            } else {
                                onPaymentFailed(response.message)
                                Toast.makeText(context, "Payment failed: ${response.message}", Toast.LENGTH_LONG).show()
                            }
                        },
                        onFailure = { error ->
                            onPaymentFailed(error.message ?: "Unknown error")
                            Toast.makeText(context, "Payment error: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onPaymentFailed(e.message ?: "Unknown error")
                    Toast.makeText(context, "Payment error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    fun checkPaymentStatus(
        checkoutRequestId: String,
        onStatusReceived: (MpesaResponse) -> Unit,
        onStatusCheckFailed: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = repository.checkPaymentStatus(checkoutRequestId)
                
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = { response ->
                            onStatusReceived(response)
                        },
                        onFailure = { error ->
                            onStatusCheckFailed(error.message ?: "Unknown error")
                        }
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onStatusCheckFailed(e.message ?: "Unknown error")
                }
            }
        }
    }
}
