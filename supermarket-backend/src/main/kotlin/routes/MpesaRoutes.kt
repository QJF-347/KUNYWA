package com.supermarket.routes

import com.supermarket.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.mpesaRoutes() {
    val mpesaService = MpesaService()
    
    route("/mpesa") {
        post("/stk-push") {
            try {
                val request = call.receive<MpesaRequest>()
                val response = mpesaService.initiateStkPush(request)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
        
        get("/status/{checkoutRequestId}") {
            try {
                val checkoutRequestId = call.parameters["checkoutRequestId"]
                if (checkoutRequestId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Checkout request ID required"))
                    return@get
                }
                
                val response = mpesaService.checkPaymentStatus(checkoutRequestId)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
        
        // MPesa callback endpoint for Daraja notifications
        post("/callback") {
            try {
                val callbackData = call.receive<Map<String, Any>>()
                val response = mpesaService.handleCallback(callbackData)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}
