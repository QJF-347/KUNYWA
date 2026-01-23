package com.supermarket

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.util.Base64
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class User(val id: Int, val username: String, val role: String)

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class RegisterRequest(val username: String, val email: String, val phone: String, val password: String)

@Serializable
data class Branch(val id: Int, val name: String, val location: String)

@Serializable
data class Stock(val id: Int, val productId: Int, val productName: String, val quantity: Int, val price: Double)

@Serializable
data class SaleItem(val productId: Int, val quantity: Int)

@Serializable
data class SaleRequest(val branchId: Int, val items: List<SaleItem>, val total: Double)

@Serializable
data class Sale(val id: Int, val branchId: Int, val items: List<SaleItem>, val total: Double)

@Serializable
data class RestockRequest(val branchId: Int, val productId: Int, val quantity: Int)

@Serializable
data class OverallReport(val totalSales: Double, val totalOrders: Int)

@Serializable
data class MpesaRequest(val phoneNumber: String, val amount: Double, val accountReference: String, val transactionDesc: String, val branchId: Int? = null, val items: List<SaleItem>? = null)

@Serializable
data class MpesaResponse(val success: Boolean, val message: String, val checkoutRequestId: String? = null)

@Serializable
data class StkPushResponse(
    val MerchantRequestID: String? = null,
    val CheckoutRequestID: String? = null,
    val ResponseCode: String? = null,
    val ResponseDescription: String? = null,
    val CustomerMessage: String? = null
)

@Serializable
data class StkPushErrorResponse(
    val requestId: String? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null
)

@Serializable
data class StkPushRequest(
    val BusinessShortCode: String,
    val Password: String,
    val Timestamp: String,
    val TransactionType: String,
    val Amount: Int,
    val PartyA: String,
    val PartyB: String,
    val PhoneNumber: String,
    val CallBackURL: String,
    val AccountReference: String,
    val TransactionDesc: String
)

@Serializable
data class DebugResponse(val users: List<User>, val branches: List<Branch>)

// NEW: Add M-Pesa token response data class
@Serializable
data class MpesaTokenResponse(
    val access_token: String,
    val expires_in: String
)

// M-Pesa Configuration
const val MPESA_CONSUMER_KEY = "upAZTWNlz1TvCOS5aijVToREKRoxRdelnGxw95Ux13Ermam5"
const val MPESA_CONSUMER_SECRET = "2vADCUq1BzGG3HCnECD3h8fqrZwuR2i8zmtt6Vtqf9z8mc4JGx4ptSlcCLNlcIiU"
const val MPESA_SHORTCODE = "174379"
const val MPESA_PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"
const val MPESA_CALLBACK_URL = "https://webhook.site/1ce723ac-ef61-4f40-95ef-33f7f5c0c28f"

val httpClient = HttpClient(Java)

// M-Pesa Helper Functions - FIXED VERSION
suspend fun getMpesaAccessToken(): String? {
    return try {
        val credentials = Base64.getEncoder().encodeToString("$MPESA_CONSUMER_KEY:$MPESA_CONSUMER_SECRET".toByteArray())
        val response = httpClient.get("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials") {
            headers {
                append(HttpHeaders.Authorization, "Basic $credentials")
                append(HttpHeaders.ContentType, "application/json")
            }
        }
        val responseBody = response.bodyAsText()
        println("M-Pesa token response: $responseBody")
        println("M-Pesa token status: ${response.status.value}")
        
        if (responseBody.isEmpty()) {
            println("Empty response from M-Pesa token endpoint")
            return null
        }
        
        // FIX: Use the MpesaTokenResponse data class for proper deserialization
        val json = Json { ignoreUnknownKeys = true }
        val tokenResponse = json.decodeFromString<MpesaTokenResponse>(responseBody.trim())
        tokenResponse.access_token
    } catch (e: Exception) {
        println("Error getting M-Pesa access token: ${e.message}")
        e.printStackTrace()
        null
    }
}

suspend fun initiateStkPush(phoneNumber: String, amount: Double, accountReference: String, transactionDesc: String, branchId: Int? = null, items: List<SaleItem>? = null): MpesaResponse {
    return try {
        // Reduce stock immediately if branchId and items are provided
        if (branchId != null && items != null) {
            reduceStockImmediately(branchId, items)
        }

        val accessToken = getMpesaAccessToken()
        if (accessToken == null) {
            return MpesaResponse(false, "Failed to get M-Pesa access token")
        }

        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val password = Base64.getEncoder().encodeToString("$MPESA_SHORTCODE$MPESA_PASSKEY$timestamp".toByteArray())

        val stkPushRequest = StkPushRequest(
            BusinessShortCode = MPESA_SHORTCODE,
            Password = password,
            Timestamp = timestamp,
            TransactionType = "CustomerPayBillOnline",
            Amount = amount.toInt(),
            PartyA = phoneNumber,
            PartyB = MPESA_SHORTCODE,
            PhoneNumber = phoneNumber,
            CallBackURL = MPESA_CALLBACK_URL,
            AccountReference = accountReference,
            TransactionDesc = transactionDesc
        )

        val response = httpClient.post("https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(Json.encodeToString(stkPushRequest))
        }

        val responseBody = response.bodyAsText()
        val json = Json { ignoreUnknownKeys = true }
        
        if (response.status.value == 200) {
            try {
                val responseMap = json.decodeFromString<StkPushResponse>(responseBody)
                MpesaResponse(true, "STK Push sent successfully", responseMap.CheckoutRequestID)
            } catch (e: Exception) {
                // Try to parse as error response
                try {
                    val errorResponse = json.decodeFromString<StkPushErrorResponse>(responseBody)
                    MpesaResponse(false, errorResponse.errorMessage ?: "Unknown error")
                } catch (ex: Exception) {
                    // Fallback: try to extract CheckoutRequestID manually
                    val checkoutId = try {
                        // Parse as string and extract CheckoutRequestID using string manipulation
                        if (responseBody.contains("\"CheckoutRequestID\"")) {
                            val startIndex = responseBody.indexOf("\"CheckoutRequestID\":\"") + 20
                            val endIndex = responseBody.indexOf("\"", startIndex)
                            if (startIndex > 19 && endIndex > startIndex) {
                                responseBody.substring(startIndex, endIndex)
                            } else null
                        } else null
                    } catch (ex: Exception) {
                        null
                    }
                    if (checkoutId != null) {
                        MpesaResponse(true, "STK Push sent successfully", checkoutId)
                    } else {
                        MpesaResponse(false, "Failed to parse M-Pesa response")
                    }
                }
            }
        } else {
            try {
                val errorResponse = json.decodeFromString<StkPushErrorResponse>(responseBody)
                MpesaResponse(false, errorResponse.errorMessage ?: "Unknown error")
            } catch (e: Exception) {
                // Fallback: try to extract error message manually
                val errorMessage = try {
                    // Parse as string and extract error messages using string manipulation
                    when {
                        responseBody.contains("\"errorMessage\"") -> {
                            val startIndex = responseBody.indexOf("\"errorMessage\":\"") + 15
                            val endIndex = responseBody.indexOf("\"", startIndex)
                            if (startIndex > 14 && endIndex > startIndex) responseBody.substring(startIndex, endIndex) else "Unknown error"
                        }
                        responseBody.contains("\"ResponseDescription\"") -> {
                            val startIndex = responseBody.indexOf("\"ResponseDescription\":\"") + 21
                            val endIndex = responseBody.indexOf("\"", startIndex)
                            if (startIndex > 20 && endIndex > startIndex) responseBody.substring(startIndex, endIndex) else "Unknown error"
                        }
                        else -> "Unknown error"
                    }
                } catch (ex: Exception) {
                    "Unknown error"
                }
                MpesaResponse(false, errorMessage)
            }
        }
    } catch (e: Exception) {
        println("Error initiating STK push: ${e.message}")
        MpesaResponse(false, "Failed to initiate STK push: ${e.message}")
    }
}

fun reduceStockImmediately(branchId: Int, items: List<SaleItem>) {
    try {
        val branchStocks = stocks[branchId]
        if (branchStocks != null) {
            val updatedStocks = branchStocks.map { stock ->
                val saleItem = items.find { it.productId == stock.productId }
                if (saleItem != null) {
                    val newQuantity = stock.quantity - saleItem.quantity
                    if (newQuantity >= 0) {
                        stock.copy(quantity = newQuantity)
                    } else {
                        stock // Don't allow negative stock
                    }
                } else {
                    stock
                }
            }
            stocks[branchId] = updatedStocks
            println("Stock reduced immediately for branch $branchId")
        }
    } catch (e: Exception) {
        println("Error reducing stock: ${e.message}")
    }
}

val users = mutableListOf<User>()
val branches = listOf(
    Branch(1, "Nairobi", "Nairobi"),
    Branch(2, "Kisumu", "Kisumu"),
    Branch(3, "Mombasa", "Mombasa"),
    Branch(4, "Nakuru", "Nakuru"),
    Branch(5, "Eldoret", "Eldoret")
)

val stocks = mutableMapOf<Int, List<Stock>>()
val sales = mutableListOf<Sale>()
var saleIdCounter = 1

fun initializeStocks() {
    stocks[1] = listOf(
        Stock(1, 1, "Coke", 50, 1.0),
        Stock(2, 2, "Fanta", 30, 1.0),
        Stock(3, 3, "Sprite", 40, 1.0)
    )
    stocks[2] = listOf(
        Stock(4, 1, "Coke", 35, 1.0),
        Stock(5, 2, "Fanta", 25, 1.0),
        Stock(6, 3, "Sprite", 20, 1.0)
    )
    stocks[3] = listOf(
        Stock(7, 1, "Coke", 45, 1.0),
        Stock(8, 2, "Fanta", 40, 1.0),
        Stock(9, 3, "Sprite", 30, 120.0)
    )
    stocks[4] = listOf(
        Stock(10, 1, "Coke", 65, 1.0),
        Stock(11, 2, "Fanta", 48, 1.0),
        Stock(12, 3, "Sprite", 17, 1.0)
    )
    stocks[5] = listOf(
        Stock(13, 1, "Coke", 55, 1.0),
        Stock(14, 2, "Fanta", 34, 1.0),
        Stock(15, 3, "Sprite", 32, 1.0)
    )
}

fun main() {
    initializeStocks()
    
    // Initialize admin user if no users exist
    if (users.isEmpty()) {
        val adminUser = User(1, "admin", "admin")
        users.add(adminUser)
    }
    
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json()
        }
        
        install(CORS) {
            anyHost()
        }
        
        routing {
            get("/") {
                call.respond(mapOf("status" to "Supermarket Backend is running", "branches_count" to branches.size))
            }
            
            get("/health") {
                call.respond(mapOf("status" to "healthy"))
            }
            
            get("/debug") {
                val debugResponse = DebugResponse(users, branches)
                call.respond(debugResponse)
            }
            
            post("/auth/login") {
                try {
                    val request = call.receive<LoginRequest>()
                    val foundUser = users.firstOrNull { it.username == request.username }
                    if (foundUser != null) {
                        call.respond(foundUser)
                    } else {
                        call.respond(mapOf("error" to "Invalid credentials"))
                    }
                } catch (e: Exception) {
                    call.respond(mapOf("error" to "Login failed"))
                }
            }
            
            post("/auth/register") {
                try {
                    val request = call.receive<RegisterRequest>()
                    val newUser = User(users.size + 1, request.username, "customer")
                    users.add(newUser)
                    call.respond(newUser)
                } catch (e: Exception) {
                    call.respond(mapOf("error" to "Registration failed"))
                }
            }
            
            post("/create-admin") {
                try {
                    // Create admin user with hardcoded credentials
                    val adminUser = User(users.size + 1, "admin", "admin")
                    users.add(adminUser)
                    call.respond(adminUser)
                } catch (e: Exception) {
                    call.respond(mapOf("error" to "Admin creation failed"))
                }
            }
            
            get("/init-admin") {
                try {
                    // Initialize with default admin user if not exists
                    val existingAdmin = users.firstOrNull { it.username == "admin" }
                    if (existingAdmin == null) {
                        val adminUser = User(users.size + 1, "admin", "admin")
                        users.add(adminUser)
                        call.respond(mapOf("message" to "Admin user created", "user" to adminUser))
                    } else {
                        call.respond(mapOf("message" to "Admin user already exists", "user" to existingAdmin))
                    }
                } catch (e: Exception) {
                    call.respond(mapOf("error" to "Admin initialization failed"))
                }
            }
            
            get("/customer/branches") {
                call.respond(branches)
            }
            
            get("/customer/branches/{branchId}/stock") {
                val branchId = call.parameters["branchId"]?.toIntOrNull()
                if (branchId != null && stocks.containsKey(branchId)) {
                    call.respond(stocks[branchId] ?: emptyList())
                } else {
                    call.respond(mapOf("error" to "Branch not found"))
                }
            }
            
            post("/customer/sales") {
                try {
                    val request = call.receive<SaleRequest>()
                    
                    // Check if all items have sufficient stock
                    val branchStocks = stocks[request.branchId]
                    if (branchStocks == null) {
                        call.respond(mapOf("error" to "Branch not found"))
                        return@post
                    }
                    
                    for (item in request.items) {
                        val stock = branchStocks.find { it.productId == item.productId }
                        if (stock == null) {
                            call.respond(mapOf("error" to "Product with ID ${item.productId} not found in branch"))
                            return@post
                        }
                        if (stock.quantity < item.quantity) {
                            call.respond(mapOf("error" to "Insufficient stock for product ${stock.productName}. Available: ${stock.quantity}, Requested: ${item.quantity}"))
                            return@post
                        }
                    }
                    
                    // Reduce stock quantities
                    val updatedStocks = branchStocks.map { stock ->
                        val saleItem = request.items.find { it.productId == stock.productId }
                        if (saleItem != null) {
                            stock.copy(quantity = stock.quantity - saleItem.quantity)
                        } else {
                            stock
                        }
                    }
                    stocks[request.branchId] = updatedStocks
                    
                    // Create the sale
                    val newSale = Sale(saleIdCounter++, request.branchId, request.items, request.total)
                    sales.add(newSale)
                    call.respond(newSale)
                } catch (e: Exception) {
                    call.respond(mapOf("error" to "Sale creation failed: ${e.message}"))
                }
            }
            
            post("/admin/restock") {
                try {
                    val request = call.receive<RestockRequest>()
                    
                    // Find the stock for the given branch and product
                    val branchStocks = stocks[request.branchId]
                    if (branchStocks != null) {
                        val stock = branchStocks.find { it.productId == request.productId }
                        if (stock != null) {
                            // Update the stock quantity
                            val updatedStock = stock.copy(quantity = stock.quantity + request.quantity)
                            val updatedStocks = branchStocks.map { if (it.id == stock.id) updatedStock else it }
                            stocks[request.branchId] = updatedStocks
                            call.respond(mapOf("success" to true, "newQuantity" to updatedStock.quantity))
                        } else {
                            call.respond(mapOf("error" to "Product not found in branch"))
                        }
                    } else {
                        call.respond(mapOf("error" to "Branch not found"))
                    }
                } catch (e: Exception) {
                    call.respond(mapOf("error" to "Restock failed: ${e.message}"))
                }
            }
            
            get("/admin/reports") {
                val totalSales = sales.sumOf { it.total }
                val totalOrders = sales.size
                call.respond(OverallReport(totalSales, totalOrders))
            }
            
            post("/mpesa/stk-push") {
                try {
                    val request = call.receive<MpesaRequest>()
                    val response = initiateStkPush(
                        phoneNumber = request.phoneNumber,
                        amount = request.amount,
                        accountReference = request.accountReference,
                        transactionDesc = request.transactionDesc,
                        branchId = request.branchId,
                        items = request.items
                    )
                    call.respond(response)
                } catch (e: Exception) {
                    call.respond(MpesaResponse(false, "M-Pesa payment failed: ${e.message}"))
                }
            }
            
            get("/mpesa/status/{checkoutRequestId}") {
                val checkoutRequestId = call.parameters["checkoutRequestId"]
                if (checkoutRequestId != null) {
                    val response = MpesaResponse(
                        success = true,
                        message = "Payment completed successfully",
                        checkoutRequestId = checkoutRequestId
                    )
                    call.respond(response)
                } else {
                    call.respond(mapOf("error" to "Invalid checkout request ID"))
                }
            }
        }
    }.start(wait = true)
}