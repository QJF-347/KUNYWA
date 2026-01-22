package com.supermarket.routes

import com.supermarket.models.LoginRequest
import com.supermarket.models.RegisterRequest
import com.supermarket.models.User
import com.supermarket.models.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes() {
    val userService = UserService()
    
    route("/auth") {
        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                val user = userService.login(request)
                
                if (user != null) {
                    call.respond(HttpStatusCode.OK, user)
                } else {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
        
        post("/register") {
            try {
                val request = call.receive<RegisterRequest>()
                val user = userService.register(request)
                call.respond(HttpStatusCode.Created, user)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}
