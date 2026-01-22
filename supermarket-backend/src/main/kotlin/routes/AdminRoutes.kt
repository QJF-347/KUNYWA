package com.supermarket.routes

import com.supermarket.config.adminOnly
import com.supermarket.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update

fun Route.adminRoutes() {
    val salesService = SalesService()
    
    route("/admin") {
        // Protect all admin routes with authentication
        adminOnly {
            post("/restock") {
                try {
                    val request = call.receive<RestockRequest>()
                    
                    Database.transaction {
                        val existingStock = Stocks.select { 
                            (Stocks.branchId eq request.branchId) and (Stocks.productId eq request.productId) 
                        }.singleOrNull()
                        
                        if (existingStock != null) {
                            Stocks.update({ 
                                (Stocks.branchId eq request.branchId) and (Stocks.productId eq request.productId) 
                            }) {
                                it[quantity] = existingStock[Stocks.quantity] + request.quantity
                            }
                        } else {
                            Stocks.insert {
                                it[branchId] = request.branchId
                                it[productId] = request.productId
                                it[quantity] = request.quantity
                            }
                        }
                    }
                    
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Restock successful"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            get("/reports") {
                try {
                    val report = salesService.getOverallReport()
                    call.respond(HttpStatusCode.OK, report)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }
        }
    }
}

data class RestockRequest(
    val branchId: Int,
    val productId: Int,
    val quantity: Int
)
