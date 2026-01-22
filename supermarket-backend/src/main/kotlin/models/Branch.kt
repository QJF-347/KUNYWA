package com.supermarket.models

import org.jetbrains.exposed.sql.*

object Branches : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100).uniqueIndex()
    val location = varchar("location", 100)
    
    override val primaryKey = PrimaryKey(id)
}

object Stocks : Table() {
    val id = integer("id").autoIncrement()
    val branchId = reference("branch_id", Branches.id)
    val productId = reference("product_id", Products.id)
    val quantity = integer("quantity").default(0)
    
    override val primaryKey = PrimaryKey(id)
}

data class Branch(
    val id: Int,
    val name: String,
    val location: String
)

data class Stock(
    val id: Int,
    val branchId: Int,
    val productId: Int,
    val quantity: Int,
    val product: Product
)

class BranchService {
    fun getAllBranches(): List<Branch> {
        return Database.transaction {
            Branches.selectAll().map {
                Branch(
                    id = it[Branches.id],
                    name = it[Branches.name],
                    location = it[Branches.location]
                )
            }
        }
    }
    
    fun getBranchStock(branchId: Int): List<Stock> {
        return Database.transaction {
            (Stocks innerJoin Products)
                .select { Stocks.branchId eq branchId }
                .map { row ->
                    Stock(
                        id = row[Stocks.id],
                        branchId = row[Stocks.branchId],
                        productId = row[Stocks.productId],
                        quantity = row[Stocks.quantity],
                        product = Product(
                            id = row[Products.id],
                            name = row[Products.name],
                            price = row[Products.price]
                        )
                    )
                }
        }
    }
}
