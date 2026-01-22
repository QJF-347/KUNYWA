package com.supermarket.config

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Database {
    private val database: Database
    
    init {
        val databaseUrl = System.getenv("DATABASE_URL") 
            ?: throw IllegalArgumentException("DATABASE_URL is not set")
        val databaseUser = System.getenv("DATABASE_USER") 
            ?: throw IllegalArgumentException("DATABASE_USER is not set")
        val databasePassword = System.getenv("DATABASE_PASSWORD") 
            ?: throw IllegalArgumentException("DATABASE_PASSWORD is not set")
        
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
