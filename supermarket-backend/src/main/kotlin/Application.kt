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
        configurePlugins()
        configureRouting()
    }.start(wait = true)
}

fun Application.configurePlugins() {
    install(ContentNegotiation) {
        json()
    }
    
    install(CORS) {
        anyHost()
        allowHeader("Authorization")
        allowHeader("Content-Type")
        allowMethod(io.ktor.http.HttpMethod.Get)
        allowMethod(io.ktor.http.HttpMethod.Post)
        allowMethod(io.ktor.http.HttpMethod.Put)
        allowMethod(io.ktor.http.HttpMethod.Delete)
    }
    
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(mapOf("error" to (cause.message ?: "Unknown error")))
        }
    }
    
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    
    // Initialize database
    Database.init()
    DatabaseInitializer.seedData()
}

fun Application.configureRouting() {
    authRoutes()
    customerRoutes()
    adminRoutes()
    mpesaRoutes()
}
