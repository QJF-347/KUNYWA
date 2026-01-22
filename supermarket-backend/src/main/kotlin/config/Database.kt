package com.supermarket.config

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Database {
    private val database: Database
    
    init {
        val databaseUrl = System.getenv("DATABASE_URL") 
            ?: "jdbc:postgresql://localhost:5432/supermarket"
        val databaseUser = System.getenv("DATABASE_USER") ?: "postgres"
        val databasePassword = System.getenv("DATABASE_PASSWORD") ?: "password"
        
        database = Database.connect(
            url = databaseUrl,
            driver = "org.postgresql.Driver",
            user = databaseUser,
            password = databasePassword
        )
    }
    
    fun init() {
        transaction(database) {
            // Create tables
            SchemaUtils.create(
                com.supermarket.models.Users,
                com.supermarket.models.Branches,
                com.supermarket.models.Products,
                com.supermarket.models.Stocks,
                com.supermarket.models.Sales,
                com.supermarket.models.MpesaTransactions
            )
        }
    }
    
    fun <T> transaction(block: Transaction.() -> T): T {
        return org.jetbrains.exposed.sql.transactions.transaction(database, block)
    }
}
