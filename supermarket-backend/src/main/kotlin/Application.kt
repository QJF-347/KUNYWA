package com.supermarket

import com.supermarket.config.Database
import com.supermarket.data.DatabaseInitializer
import com.supermarket.routes.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.call.*
import io.ktor.server.logging.*
import org.slf4j.event.*

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080, host = "0.0.0.0") {
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
