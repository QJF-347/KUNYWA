package com.supermarket

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
        }
    }.start(wait = true)
}
