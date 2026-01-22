# Supermarket Distributed System - Demo Instructions

## 📋 System Overview
This is a distributed supermarket management system with Android client and Kotlin backend supporting multiple customers purchasing drinks from different branches via Mpesa payments, with admin oversight for stock management and sales reporting.

## 🏗️ Architecture
- **Android App**: Kotlin with MVVM pattern, Retrofit networking
- **Backend**: Kotlin with Ktor framework, H2 database
- **Payment**: M-Pesa STK Push (simulated for demo)
- **Branches**: Nairobi, Kisumu, Mombasa, Nakuru, Eldoret
- **Products**: Coke, Fanta, Sprite (KES 120 each)

## 🚀 Setup Instructions

### 1. Backend Setup
```bash
# Navigate to backend directory
cd supermarket-backend

# Build and run the server
./gradlew run

# Server will start on http://0.0.0.0:8080
```

### 2. Android App Setup
```bash
# Navigate to Android project
cd SupermarketApp

# Open in Android Studio
# Build and install APK on devices
```

### 3. Network Configuration
- **IMPORTANT**: Update `Constants.kt` in Android app with your laptop's IP:
```kotlin
const val BASE_URL = "http://YOUR_LAPTOP_IP:8080/"
```
- Find your IP: `ipconfig` (Windows) or `ifconfig` (Linux/Mac)

## 👥 Demo Scenario (4 Devices)

### Device 1: Admin
1. **Login**: Username: `admin`, Password: `admin123`
2. **Restock All Branches**:
   - Select each branch (Nairobi, Kisumu, Mombasa, Nakuru, Eldoret)
   - Add 50 units of each product (Coke, Fanta, Sprite)
3. **Monitor Reports**: Check sales totals after customers purchase

### Device 2: Customer 1
1. **Register**: Create new customer account
2. **Login**: Use customer credentials
3. **Select Branch**: Choose Nairobi
4. **Purchase Products**:
   - Add Coke (2 units) - KES 240
   - Add Fanta (1 unit) - KES 120
   - Total: KES 360
5. **Checkout**: Complete M-Pesa payment (simulated)

### Device 3: Customer 2
1. **Register**: Create new customer account
2. **Login**: Use customer credentials
3. **Select Branch**: Choose Kisumu
4. **Purchase Products**:
   - Add Sprite (3 units) - KES 360
   - Add Coke (1 unit) - KES 120
   - Total: KES 480
5. **Checkout**: Complete M-Pesa payment (simulated)

### Device 4: Customer 3
1. **Register**: Create new customer account
2. **Login**: Use customer credentials
3. **Select Branch**: Choose Mombasa
4. **Purchase Products**:
   - Add Fanta (2 units) - KES 240
   - Add Sprite (2 units) - KES 240
   - Total: KES 480
5. **Checkout**: Complete M-Pesa payment (simulated)

## 📊 Expected Results

### Admin Report After Demo
```
Sales Report:
- Coke: 4 units sold, KES 480 income
- Fanta: 3 units sold, KES 360 income  
- Sprite: 5 units sold, KES 600 income

Grand Total Income: KES 1,440
```

### Stock Updates
- **Nairobi**: Coke: 48, Fanta: 49, Sprite: 50
- **Kisumu**: Coke: 49, Fanta: 50, Sprite: 47
- **Mombasa**: Coke: 50, Fanta: 48, Sprite: 48
- **Nakuru**: Coke: 50, Fanta: 50, Sprite: 50
- **Eldoret**: Coke: 50, Fanta: 50, Sprite: 50

## 🔧 Troubleshooting

### Common Issues
1. **Network Connection**: Ensure all devices are on same WiFi network
2. **Backend Not Running**: Verify backend server is running on port 8080
3. **IP Address**: Double-check BASE_URL in Constants.kt matches laptop IP
4. **Firewall**: Ensure port 8080 is not blocked by firewall

### Testing Endpoints
```bash
# Test backend health
curl http://YOUR_LAPTOP_IP:8080/customer/branches

# Expected response:
[{"id":1,"name":"Nairobi","location":"Nairobi"}, ...]
```

## 🎯 Success Criteria
✅ All 4 devices connect to same backend  
✅ Admin can restock all branches  
✅ Customers can select different branches  
✅ Stock updates in real-time  
✅ Sales reports show correct totals  
✅ M-Pesa simulation works  
✅ No crashes during demo  

## 📱 Features Demonstrated
- Multi-device distributed architecture
- Real-time stock management
- Role-based access control (Admin/Customer)
- M-Pesa payment integration (simulated)
- Live sales reporting
- MVVM architecture pattern
- RESTful API design

## 🎓 Academic Notes
This system demonstrates:
- Distributed systems principles
- Mobile-backend integration
- Database transaction management
- API design and implementation
- Real-time state synchronization
- Payment gateway integration
- Role-based security

**Ready for class presentation!** 🚀
