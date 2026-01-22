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

data class User(val id: Int, val username: String, val role: String)
data class LoginRequest(val username: String, val password: String)
data class RegisterRequest(val username: String, val email: String, val phone: String, val password: String)
data class Branch(val id: Int, val name: String, val location: String)

val users = mutableListOf<User>()
val branches = listOf(
    Branch(1, "Nairobi", "Nairobi"),
    Branch(2, "Kisumu", "Kisumu"),
    Branch(3, "Mombasa", "Mombasa")
)

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json()
        }
        
        install(CORS) {
            anyHost()
        }
        
        routing {
            get("/") {
                call.respond(mapOf("status" to "Supermarket Backend is running"))
            }
            
            get("/health") {
                call.respond(mapOf("status" to "healthy"))
            }
            
            get("/debug") {
                call.respond(mapOf(
                    "users" to users,
                    "branches" to branches
                ))
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
        }
    }.start(wait = true)
}
