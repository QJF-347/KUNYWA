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
data class Sale(val id: Int, val items: List<SaleItem>, val total: Double)

@Serializable
data class RestockRequest(val branchId: Int, val productId: Int, val quantity: Int)

@Serializable
data class OverallReport(val totalSales: Double, val totalOrders: Int)

@Serializable
data class MpesaRequest(val phoneNumber: String, val amount: Double, val accountReference: String, val transactionDesc: String)

@Serializable
data class MpesaResponse(val success: Boolean, val message: String, val checkoutRequestId: String? = null)

@Serializable
data class DebugResponse(val users: List<User>, val branches: List<Branch>)

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
        Stock(1, 1, "Coke", 50, 120.0),
        Stock(2, 2, "Fanta", 30, 120.0),
        Stock(3, 3, "Sprite", 40, 120.0)
    )
    stocks[2] = listOf(
        Stock(4, 1, "Coke", 35, 120.0),
        Stock(5, 2, "Fanta", 25, 120.0),
        Stock(6, 3, "Sprite", 20, 120.0)
    )
    stocks[3] = listOf(
        Stock(7, 1, "Coke", 45, 120.0),
        Stock(8, 2, "Fanta", 40, 120.0),
        Stock(9, 3, "Sprite", 30, 120.0)
    )
    stocks[4] = listOf(
        Stock(10, 1, "Coke", 65, 120.0),
        Stock(11, 2, "Fanta", 48, 120.0),
        Stock(12, 3, "Sprite", 17, 120.0)
    )
    stocks[5] = listOf(
        Stock(13, 1, "Coke", 55, 120.0),
        Stock(14, 2, "Fanta", 34, 120.0),
        Stock(15, 3, "Sprite", 32, 120.0)
    )
}

fun main() {
    initializeStocks()
    
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
                    val request = call.receive<Sale>()
                    val newSale = Sale(saleIdCounter++, request.items, request.total)
                    sales.add(newSale)
                    call.respond(newSale)
                } catch (e: Exception) {
                    call.respond(mapOf("error" to "Sale creation failed"))
                }
            }
            
            post("/admin/restock") {
                try {
                    val request = call.receive<RestockRequest>()
                    call.respond(mapOf("success" to true))
                } catch (e: Exception) {
                    call.respond(mapOf("error" to "Restock failed"))
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
                    val response = MpesaResponse(
                        success = true,
                        message = "STK Push sent successfully",
                        checkoutRequestId = "ws_CO_${System.currentTimeMillis()}"
                    )
                    call.respond(response)
                } catch (e: Exception) {
                    call.respond(mapOf("error" to "M-Pesa payment failed"))
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
