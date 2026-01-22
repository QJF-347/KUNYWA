package com.supermarket.data.remote

import com.supermarket.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Auth endpoints
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<User>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<User>
    
    // Customer endpoints
    @GET("customer/branches")
    suspend fun getBranches(): Response<List<Branch>>
    
    @GET("customer/branches/{branchId}/stock")
    suspend fun getBranchStock(@Path("branchId") branchId: Int): Response<List<Stock>>
    
    @POST("customer/sales")
    suspend fun createSale(@Body request: SaleRequest): Response<Sale>
    
    // Admin endpoints
    @POST("admin/restock")
    suspend fun restock(@Body request: RestockRequest): Response<Unit>
    
    @GET("admin/reports")
    suspend fun getReports(): Response<OverallReport>
    
    // Mpesa endpoints
    @POST("mpesa/stk-push")
    suspend fun initiateStkPush(@Body request: MpesaRequest): Response<MpesaResponse>
    
    @GET("mpesa/status/{checkoutRequestId}")
    suspend fun checkPaymentStatus(@Path("checkoutRequestId") checkoutRequestId: String): Response<MpesaResponse>
}

data class RestockRequest(
    val branchId: Int,
    val productId: Int,
    val quantity: Int
)

data class SaleRequest(
    val branchId: Int,
    val items: List<SaleItem>
)

data class MpesaRequest(
    val phoneNumber: String,
    val amount: Double,
    val accountReference: String,
    val transactionDesc: String
)
