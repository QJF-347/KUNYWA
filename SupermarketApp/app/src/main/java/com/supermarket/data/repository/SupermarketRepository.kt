package com.supermarket.data.repository

import com.supermarket.data.models.*
import com.supermarket.data.remote.ApiClient

class SupermarketRepository {
    
    private val apiService = ApiClient.apiService
    
    suspend fun login(username: String, password: String): Result<User> {
        return try {
            val response = apiService.login(com.supermarket.data.models.LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun register(username: String, email: String, phone: String, password: String): Result<User> {
        return try {
            val response = apiService.register(com.supermarket.data.models.RegisterRequest(username, email, phone, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getBranches(): Result<List<Branch>> {
        return try {
            val response = apiService.getBranches()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch branches: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getBranchStock(branchId: Int): Result<List<Stock>> {
        return try {
            val response = apiService.getBranchStock(branchId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch stock: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createSale(branchId: Int, items: List<com.supermarket.data.models.SaleItem>): Result<Sale> {
        return try {
            val response = apiService.createSale(SaleRequest(branchId, items))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create sale: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun restock(branchId: Int, productId: Int, quantity: Int): Result<Unit> {
        return try {
            val response = apiService.restock(RestockRequest(branchId, productId, quantity))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Restock failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getReports(): Result<OverallReport> {
        return try {
            val response = apiService.getReports()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch reports: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun initiateStkPush(phoneNumber: String, amount: Double, accountReference: String, transactionDesc: String): Result<MpesaResponse> {
        return try {
            val response = apiService.initiateStkPush(MpesaRequest(phoneNumber, amount, accountReference, transactionDesc))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("M-Pesa payment failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun checkPaymentStatus(checkoutRequestId: String): Result<MpesaResponse> {
        return try {
            val response = apiService.checkPaymentStatus(checkoutRequestId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to check payment status: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
