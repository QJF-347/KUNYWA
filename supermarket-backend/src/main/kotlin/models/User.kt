package com.supermarket.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100)
    val phone = varchar("phone", 20)
    val password = varchar("password", 255)
    val role = varchar("role", 20) // "admin" or "customer"
    
    override val primaryKey = PrimaryKey(id)
}

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val phone: String,
    val role: String,
    val token: String? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val phone: String,
    val password: String
)

class UserService {
    fun register(request: RegisterRequest): User {
        return Database.transaction {
            val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
            val userId = Users.insert {
                it[username] = request.username
                it[email] = request.email
                it[phone] = request.phone
                it[password] = hashedPassword
                it[role] = "customer"
            }[Users.id]
            
            User(
                id = userId,
                username = request.username,
                email = request.email,
                phone = request.phone,
                role = "customer",
                token = generateToken(userId, request.username, "customer")
            )
        }
    }
    
    fun login(request: LoginRequest): User? {
        return Database.transaction {
            val user = Users.select { Users.username eq request.username }.singleOrNull()
            if (user != null && BCrypt.checkpw(request.password, user[Users.password])) {
                User(
                    id = user[Users.id],
                    username = user[Users.username],
                    email = user[Users.email],
                    phone = user[Users.phone],
                    role = user[Users.role],
                    token = generateToken(user[Users.id], user[Users.username], user[Users.role])
                )
            } else {
                null
            }
        }
    }
    
    private fun generateToken(userId: Int, username: String, role: String): String {
        // Simple token generation for demo (in production, use JWT)
        return "${userId}_${username}_${role}_${System.currentTimeMillis()}"
    }
    
    fun validateToken(token: String): User? {
        return try {
            val parts = token.split("_")
            if (parts.size >= 3) {
                val userId = parts[0].toInt()
                val username = parts[1]
                val role = parts[2]
                
                Database.transaction {
                    val user = Users.select { Users.id eq userId }.singleOrNull()
                    if (user != null && user[Users.username] == username && user[Users.role] == role) {
                        User(
                            id = user[Users.id],
                            username = user[Users.username],
                            email = user[Users.email],
                            phone = user[Users.phone],
                            role = user[Users.role],
                            token = token
                        )
                    } else {
                        null
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
