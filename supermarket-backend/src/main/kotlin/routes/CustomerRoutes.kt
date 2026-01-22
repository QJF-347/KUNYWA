package com.supermarket.routes

import com.supermarket.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.customerRoutes() {
    val branchService = BranchService()
    val salesService = SalesService()
    
    route("/customer") {
        get("/branches") {
            try {
                val branches = branchService.getAllBranches()
                call.respond(HttpStatusCode.OK, branches)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
        
        get("/branches/{branchId}/stock") {
            try {
                val branchId = call.parameters["branchId"]?.toIntOrNull()
                if (branchId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid branch ID"))
                    return@get
                }
                
                val stock = branchService.getBranchStock(branchId)
                call.respond(HttpStatusCode.OK, stock)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
        
        post("/sales") {
            try {
                val request = call.receive<SaleRequest>()
                val customerPhone = "254712345678" // Default for demo
                
                val sale = salesService.createSale(request, customerPhone)
                call.respond(HttpStatusCode.Created, sale)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}
