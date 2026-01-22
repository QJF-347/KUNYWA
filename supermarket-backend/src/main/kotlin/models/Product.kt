package com.supermarket.models

import org.jetbrains.exposed.sql.*

object Products : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100).uniqueIndex()
    val price = double("price")
    
    override val primaryKey = PrimaryKey(id)
}

data class Product(
    val id: Int,
    val name: String,
    val price: Double
)

class ProductService {
    fun getAllProducts(): List<Product> {
        return Database.transaction {
            Products.selectAll().map {
                Product(
                    id = it[Products.id],
                    name = it[Products.name],
                    price = it[Products.price]
                )
            }
        }
    }
    
    fun getProduct(id: Int): Product? {
        return Database.transaction {
            Products.select { Products.id eq id }.singleOrNull()?.let {
                Product(
                    id = it[Products.id],
                    name = it[Products.name],
                    price = it[Products.price]
                )
            }
        }
    }
}
