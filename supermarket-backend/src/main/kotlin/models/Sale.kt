package com.supermarket.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.and

object Sales : Table() {
    val id = integer("id").autoIncrement()
    val branchId = reference("branch_id", Branches.id)
    val productId = reference("product_id", Products.id)
    val quantity = integer("quantity")
    val totalAmount = double("total_amount")
    val customerPhone = varchar("customer_phone", 20)
    val paymentStatus = varchar("payment_status", 20).default("pending")
    val createdAt = datetime("created_at").default(System.currentTimeMillis())
    
    override val primaryKey = PrimaryKey(id)
}

data class Sale(
    val id: Int,
    val branchId: Int,
    val productId: Int,
    val quantity: Int,
    val totalAmount: Double,
    val customerPhone: String,
    val paymentStatus: String,
    val createdAt: String,
    val product: Product,
    val branch: Branch
)

data class SaleRequest(
    val branchId: Int,
    val items: List<SaleItem>
)

data class SaleItem(
    val productId: Int,
    val quantity: Int
)

data class SalesReport(
    val productName: String,
    val totalQuantity: Int,
    val totalIncome: Double
)

data class OverallReport(
    val salesByProduct: List<SalesReport>,
    val grandTotalIncome: Double
)

class SalesService {
    fun createSale(request: SaleRequest, customerPhone: String): Sale {
        return Database.transaction {
            // Calculate total amount
            var totalAmount = 0.0
            val saleItems = mutableListOf<Sale>()
            
            for (item in request.items) {
                val product = Products.select { Products.id eq item.productId }.singleOrNull()
                    ?: throw IllegalArgumentException("Product not found")
                
                val itemTotal = product[Products.price] * item.quantity
                totalAmount += itemTotal
                
                // Check stock availability
                val stock = Stocks.select { 
                    (Stocks.branchId eq request.branchId) and (Stocks.productId eq item.productId) 
                }.singleOrNull()
                
                if (stock == null || stock[Stocks.quantity] < item.quantity) {
                    throw IllegalArgumentException("Insufficient stock for product: ${product[Products.name]}")
                }
                
                // Update stock
                Stocks.update({ 
                    (Stocks.branchId eq request.branchId) and (Stocks.productId eq item.productId) 
                }) {
                    it[quantity] = stock[Stocks.quantity] - item.quantity
                }
                
                // Create sale record
                val saleId = Sales.insert {
                    it[branchId] = request.branchId
                    it[productId] = item.productId
                    it[quantity] = item.quantity
                    it[totalAmount] = itemTotal
                    it[customerPhone] = customerPhone
                    it[paymentStatus] = "completed"
                }[Sales.id]
                
                val branch = Branches.select { Branches.id eq request.branchId }.single()
                
                saleItems.add(Sale(
                    id = saleId,
                    branchId = request.branchId,
                    productId = item.productId,
                    quantity = item.quantity,
                    totalAmount = itemTotal,
                    customerPhone = customerPhone,
                    paymentStatus = "completed",
                    createdAt = System.currentTimeMillis().toString(),
                    product = Product(
                        id = product[Products.id],
                        name = product[Products.name],
                        price = product[Products.price]
                    ),
                    branch = Branch(
                        id = branch[Branches.id],
                        name = branch[Branches.name],
                        location = branch[Branches.location]
                    )
                ))
            }
            
            saleItems.first()
        }
    }
    
    fun getOverallReport(): OverallReport {
        return Database.transaction {
            val salesData = (Sales innerJoin Products)
                .selectAll()
                .map { row ->
                    Pair(
                        row[Products.name],
                        Pair(row[Sales.quantity], row[Sales.totalAmount])
                    )
                }
            
            val groupedSales = salesData.groupBy({ it.first }) { it.second }
            
            val salesByProduct = groupedSales.map { (productName, sales) ->
                SalesReport(
                    productName = productName,
                    totalQuantity = sales.sumOf { it.first },
                    totalIncome = sales.sumOf { it.second }
                )
            }
            
            OverallReport(
                salesByProduct = salesByProduct,
                grandTotalIncome = salesByProduct.sumOf { it.totalIncome }
            )
        }
    }
}
