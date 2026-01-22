# BACKEND DIAGNOSTIC

## 1. PROJECT OVERVIEW
- **Backend framework**: Ktor 2.3.6
- **Kotlin version**: 1.9.10
- **Build tool**: Gradle (Kotlin DSL)
- **JVM version**: Java 8 (configured via jvmToolchain)
- **Entry point class and method**: `com.supermarket.ApplicationKt.main()`

## 2. RUN & BUILD DETAILS
- **Exact command used to run the backend locally**: `./gradlew :supermarket-backend:run`
- **Exact command used to build the runnable JAR**: `./gradlew :supermarket-backend:build`
- **Output JAR name and location**: `supermarket-backend/build/libs/supermarket-backend-1.0.jar`

## 3. SERVER CONFIGURATION
- **How the server host and port are configured**: 
  - Host: Hardcoded to "0.0.0.0" in Application.kt
  - Port: Environment variable "PORT" with fallback to 8080
- **Whether the PORT environment variable is supported**: YES
- **Default port if PORT is not set**: 8080

## 4. DEPLOYMENT READINESS
- **Whether the project is suitable for Render**: YES (render.yaml configuration present)
- **Whether a Dockerfile exists**: YES
- **Whether Android-specific dependencies exist**: NO
- **Any hardcoded localhost references**: YES (Database.kt line 11 - fallback DATABASE_URL uses localhost)

## 5. API ENDPOINTS
- **POST /auth/login** - User authentication
- **POST /auth/register** - User registration
- **GET /customer/branches** - List all supermarket branches
- **GET /customer/branches/{branchId}/stock** - Get stock for specific branch
- **POST /customer/sales** - Create a new sale
- **POST /admin/restock** - Restock products at branch (admin only)
- **GET /admin/reports** - Get overall sales report (admin only)
- **POST /mpesa/stk-push** - Initiate M-Pesa STK Push payment
- **GET /mpesa/status/{checkoutRequestId}** - Check M-Pesa payment status

**Endpoints requiring authentication**: None implemented (no authentication middleware)

**Admin-only endpoints**: 
- POST /admin/restock
- GET /admin/reports

## 6. AUTHENTICATION & ROLES
- **Auth mechanism used**: Simple token-based (not JWT)
- **User roles supported**: "admin" and "customer"
- **How admin users are identified**: Role field in Users table set to "admin"

## 7. DATABASE
- **Database type**: PostgreSQL (with H2 for local testing)
- **ORM or DB library used**: Exposed ORM
- **Connection configuration method**: Environment variables with localhost fallback
- **Environment variables required**: DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD
- **Tables and their purpose**:
  - Users: User authentication and role management
  - Branches: Supermarket branch information
  - Products: Product catalog with pricing
  - Stocks: Inventory levels per branch
  - Sales: Sales transactions
  - MpesaTransactions: M-Pesa payment records

## 8. MPESA INTEGRATION
- **Whether MPesa sandbox is implemented**: YES (simulation mode)
- **STK Push endpoint**: POST /mpesa/stk-push
- **Callback endpoint**: NOT IMPLEMENTED
- **Whether callback URL is configurable**: NOT IMPLEMENTED

## 9. ENVIRONMENT VARIABLES
**Required variables**:
- DATABASE_URL (mandatory for production)
- DATABASE_USER (mandatory for production)
- DATABASE_PASSWORD (mandatory for production)

**Optional variables**:
- PORT (defaults to 8080)
- MPESA_CONSUMER_KEY (defaults to "YOUR_CONSUMER_KEY")
- MPESA_CONSUMER_SECRET (defaults to "YOUR_CONSUMER_SECRET")
- MPESA_PASSKEY (defaults to "YOUR_PASSKEY")
- SIMULATE_MPESA (defaults to true)

## 10. KNOWN ISSUES / RISKS
- **Hardcoded localhost reference**: Database.kt fallback uses localhost which will fail on Render
- **No authentication middleware**: All endpoints are publicly accessible despite having auth routes
- **M-Pesa simulation only**: Real M-Pesa integration not implemented
- **No callback URL**: M-Pesa callbacks cannot be received
- **Default admin credentials**: Hardcoded admin user (admin/admin123) created automatically
- **No input validation**: Limited error handling and input validation
- **No rate limiting**: API endpoints are not protected against abuse
- **Database seeding**: Automatically populates data which may not be desirable in production
