package com.supermarket.config

import com.supermarket.models.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// Simple authentication middleware for admin endpoints
suspend fun ApplicationCall.authenticateAdmin(): Boolean {
    val userService = UserService()
    
    val authHeader = request.headers["Authorization"]
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        respond(HttpStatusCode.Unauthorized, mapOf("error" to "Authorization token required"))
        return false
    }
    
    val token = authHeader.substring(7) // Remove "Bearer " prefix
    val user = userService.validateToken(token)
    
    if (user == null || user.role != "admin") {
        respond(HttpStatusCode.Forbidden, mapOf("error" to "Admin access required"))
        return false
    }
    
    // Store user in call attributes for potential use in the route
    attributes.put(AttributeKey<User>("user"), user)
    return true
}

// Extension function to protect admin routes
fun Route.adminOnly(block: Route.() -> Unit) {
    route {
        intercept(ApplicationCallPipeline.Plugins) {
            if (!call.authenticateAdmin()) {
                return@intercept finish()
            }
        }
        block()
    }
}
