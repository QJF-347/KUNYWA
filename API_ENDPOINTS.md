# Supermarket Backend API Endpoints

## Base URL
```
https://kunywa-bakend.onrender.com/
```

## Authentication Endpoints

### POST /auth/login
Login user with username and password

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response (Success):**
```json
{
  "id": 1,
  "username": "john",
  "role": "customer"
}
```

**Response (Error):**
```json
{
  "error": "Invalid credentials"
}
```

### POST /auth/register
Register new user account

**Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "phone": "string",
  "password": "string"
}
```

**Response (Success):**
```json
{
  "id": 1,
  "username": "john",
  "role": "customer"
}
```

**Response (Error):**
```json
{
  "error": "Registration failed"
}
```

## Customer Endpoints

### GET /customer/branches
Get all available supermarket branches

**Response:**
```json
[
  {
    "id": 1,
    "name": "Nairobi",
    "location": "Nairobi"
  },
  {
    "id": 2,
    "name": "Kisumu",
    "location": "Kisumu"
  },
  {
    "id": 3,
    "name": "Mombasa",
    "location": "Mombasa"
  }
]
```

### GET /customer/branches/{branchId}/stock
Get stock for a specific branch

**Response:**
```json
[
  {
    "id": 1,
    "productId": 1,
    "productName": "Milk",
    "quantity": 50,
    "price": 120.0
  }
]
```

### POST /customer/sales
Create a new sale

**Request Body:**
```json
{
  "branchId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

**Response:**
```json
{
  "id": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ],
  "total": 240.0
}
```

## Admin Endpoints

### POST /admin/restock
Restock products at a branch

**Request Body:**
```json
{
  "branchId": 1,
  "productId": 1,
  "quantity": 100
}
```

**Response:**
```json
{
  "success": true
}
```

### GET /admin/reports
Get sales reports and analytics

**Response:**
```json
{
  "totalSales": 15000.0,
  "totalOrders": 45
}
```

## M-Pesa Payment Endpoints

### POST /mpesa/stk-push
Initiate M-Pesa STK Push payment

**Request Body:**
```json
{
  "phoneNumber": "0712345678",
  "amount": 240.0,
  "accountReference": "ORDER123",
  "transactionDesc": "Supermarket Purchase"
}
```

**Response:**
```json
{
  "success": true,
  "message": "STK Push sent successfully",
  "checkoutRequestId": "ws_CO_123456789"
}
```

### GET /mpesa/status/{checkoutRequestId}
Check M-Pesa payment status

**Response:**
```json
{
  "success": true,
  "message": "Payment completed successfully"
}
```

## System Endpoints

### GET /
Check if backend is running

**Response:**
```json
{
  "status": "Supermarket Backend is running"
}
```

### GET /health
Health check endpoint

**Response:**
```json
{
  "status": "healthy"
}
```

### GET /debug
Debug endpoint to view stored data

**Response:**
```json
{
  "users": [
    {
      "id": 1,
      "username": "john",
      "role": "customer"
    }
  ],
  "branches": [
    {
      "id": 1,
      "name": "Nairobi",
      "location": "Nairobi"
    }
  ]
}
```

## HTTP Status Codes

- **200 OK**: Request successful
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Authentication required
- **404 Not Found**: Endpoint not found
- **500 Internal Server Error**: Server error

## Error Response Format

All errors return JSON with an "error" field:
```json
{
  "error": "Error message description"
}
```

## CORS

The backend supports CORS for all origins, allowing cross-origin requests from web and mobile applications.

## Notes

- All timestamps are in UTC
- All monetary values are in Kenyan Shillings (KES)
- User roles: "admin" or "customer"
- All endpoints require proper authentication except system endpoints
- Data is stored in memory and resets on server restart
