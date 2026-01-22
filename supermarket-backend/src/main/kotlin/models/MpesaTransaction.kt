package com.supermarket.models

import org.jetbrains.exposed.sql.*

object MpesaTransactions : Table() {
    val id = integer("id").autoIncrement()
    val checkoutRequestId = varchar("checkout_request_id", 255).uniqueIndex()
    val merchantRequestId = varchar("merchant_request_id", 255)
    val phoneNumber = varchar("phone_number", 20)
    val amount = double("amount")
    val status = varchar("status", 50).default("pending")
    val createdAt = datetime("created_at").default(System.currentTimeMillis())
    
    override val primaryKey = PrimaryKey(id)
}

data class MpesaRequest(
    val phoneNumber: String,
    val amount: Double,
    val accountReference: String,
    val transactionDesc: String
)

data class MpesaResponse(
    val success: Boolean,
    val message: String,
    val checkoutRequestId: String? = null,
    val merchantRequestId: String? = null
)

class MpesaService {
    fun initiateStkPush(request: MpesaRequest): MpesaResponse {
        return Database.transaction {
            // For demo purposes, simulate successful payment
            if (com.supermarket.config.MpesaConfig.SIMULATE_SUCCESS) {
                val checkoutRequestId = "CHK_${System.currentTimeMillis()}"
                val merchantRequestId = "MER_${System.currentTimeMillis()}"
                
                MpesaTransactions.insert {
                    it[checkoutRequestId] = checkoutRequestId
                    it[merchantRequestId] = merchantRequestId
                    it[phoneNumber] = request.phoneNumber
                    it[amount] = request.amount
                    it[status] = "completed"
                }
                
                MpesaResponse(
                    success = true,
                    message = "Payment successful",
                    checkoutRequestId = checkoutRequestId,
                    merchantRequestId = merchantRequestId
                )
            } else {
                // In production, integrate with actual M-Pesa API
                MpesaResponse(
                    success = false,
                    message = "M-Pesa integration not configured"
                )
            }
        }
    }
    
    fun checkPaymentStatus(checkoutRequestId: String): MpesaResponse {
        return Database.transaction {
            val transaction = MpesaTransactions.select { 
                MpesaTransactions.checkoutRequestId eq checkoutRequestId 
            }.singleOrNull()
            
            if (transaction != null) {
                MpesaResponse(
                    success = transaction[MpesaTransactions.status] == "completed",
                    message = "Payment ${transaction[MpesaTransactions.status]}",
                    checkoutRequestId = checkoutRequestId,
                    merchantRequestId = transaction[MpesaTransactions.merchantRequestId]
                )
            } else {
                MpesaResponse(
                    success = false,
                    message = "Transaction not found"
                )
            }
        }
    }
    
    // Handle MPesa callback from Daraja API
    fun handleCallback(callbackData: Map<String, Any>): Map<String, String> {
        return try {
            Database.transaction {
                // Extract callback data (simplified for demo)
                val stkCallback = callbackData["Body"] as? Map<String, Any>
                val callbackMetadata = stkCallback?.get("stkCallback") as? Map<String, Any>
                val merchantRequestId = callbackMetadata?.get("MerchantRequestID") as? String
                val checkoutRequestId = callbackMetadata?.get("CheckoutRequestID") as? String
                val resultCode = callbackMetadata?.get("ResultCode") as? Double
                val resultDesc = callbackMetadata?.get("ResultDesc") as? String
                
                if (checkoutRequestId != null && merchantRequestId != null) {
                    val status = if (resultCode == 0.0) "completed" else "failed"
                    
                    MpesaTransactions.update({ MpesaTransactions.checkoutRequestId eq checkoutRequestId }) {
                        it[status] = status
                    }
                    
                    mapOf(
                        "status" to "success",
                        "message" to "Callback processed successfully"
                    )
                } else {
                    mapOf(
                        "status" to "error",
                        "message" to "Invalid callback data"
                    )
                }
            }
        } catch (e: Exception) {
            mapOf(
                "status" to "error", 
                "message" to "Failed to process callback: ${e.message}"
            )
        }
    }
}
