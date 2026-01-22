package com.supermarket.data

import com.supermarket.config.Database
import com.supermarket.models.*
import org.jetbrains.exposed.sql.insert
import org.mindrot.jbcrypt.BCrypt

object DatabaseInitializer {
    
    fun seedData() {
        Database.transaction {
            // Insert default admin user
            val adminExists = Users.select { Users.username eq "admin" }.count() > 0
            if (!adminExists) {
                Users.insert {
                    it[username] = "admin"
                    it[email] = "admin@supermarket.com"
                    it[phone] = "254700000000"
                    it[password] = BCrypt.hashpw("admin123", BCrypt.gensalt())
                    it[role] = "admin"
                }
            }
            
            // Insert branches
            val branches = listOf(
                "Nairobi" to "Nairobi",
                "Kisumu" to "Kisumu",
                "Mombasa" to "Mombasa",
                "Nakuru" to "Nakuru",
                "Eldoret" to "Eldoret"
            )
            
            for ((name, location) in branches) {
                val branchExists = Branches.select { Branches.name eq name }.count() > 0
                if (!branchExists) {
                    Branches.insert {
                        it[Branches.name] = name
                        it[Branches.location] = location
                    }
                }
            }
            
            // Insert products with fixed prices
            val products = listOf(
                "Coke" to 120.0,
                "Fanta" to 120.0,
                "Sprite" to 120.0
            )
            
            for ((name, price) in products) {
                val productExists = Products.select { Products.name eq name }.count() > 0
                if (!productExists) {
                    Products.insert {
                        it[Products.name] = name
                        it[Products.price] = price
                    }
                }
            }
            
            // Initialize stock for all branches
            val allBranches = Branches.selectAll()
            val allProducts = Products.selectAll()
            
            for (branch in allBranches) {
                for (product in allProducts) {
                    val stockExists = Stocks.select { 
                        (Stocks.branchId eq branch[Branches.id]) and (Stocks.productId eq product[Products.id]) 
                    }.count() > 0
                    
                    if (!stockExists) {
                        Stocks.insert {
                            it[branchId] = branch[Branches.id]
                            it[productId] = product[Products.id]
                            it[quantity] = 50 // Initial stock
                        }
                    }
                }
            }
        }
    }
}
