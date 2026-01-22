# Supermarket Distributed Android System

A complete distributed supermarket management system with Android client and Kotlin backend, supporting multiple customers purchasing drinks from different branches via Mpesa payments.

## 🏗️ Project Structure

```
├── SupermarketApp/                    # Android Application
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/supermarket/
│   │   │   │   ├── data/
│   │   │   │   │   ├── models/       # Data models
│   │   │   │   │   ├── remote/       # API client
│   │   │   │   │   └── repository/   # Repository pattern
│   │   │   │   ├── ui/
│   │   │   │   │   ├── auth/         # Login/Register
│   │   │   │   │   ├── customer/     # Customer features
│   │   │   │   │   └── admin/        # Admin features
│   │   │   │   └── utils/            # Utilities
│   │   │   └── res/
│   │   └── build.gradle.kts
│   └── build.gradle.kts
├── supermarket-backend/               # Kotlin Backend Server
│   ├── src/main/kotlin/
│   │   ├── config/                   # Database & M-Pesa config
│   │   ├── models/                    # Data models & services
│   │   ├── routes/                    # API routes
│   │   ├── data/                      # Database initialization
│   │   └── Application.kt             # Main server
│   ├── resources/
│   └── build.gradle.kts
├── DEMO_INSTRUCTIONS.md              # Complete demo guide
└── README.md                         # This file
```

## 🚀 Quick Start

### 1. Start Backend Server
```bash
cd supermarket-backend
./gradlew run
```
Server starts on `http://0.0.0.0:8080`

### 2. Configure Android App
Update `Constants.kt` with your laptop IP:
```kotlin
const val BASE_URL = "http://YOUR_LAPTOP_IP:8080/"
```

### 3. Build Android App
```bash
cd SupermarketApp
# Open in Android Studio and build APK
```

## 📱 Features

### Customer Features
- User registration and login
- Branch selection (Nairobi, Kisumu, Mombasa, Nakuru, Eldoret)
- Product browsing (Coke, Fanta, Sprite - KES 120 each)
- Shopping cart management
- M-Pesa STK Push payment (simulated)
- Purchase history

### Admin Features
- Admin login (username: `admin`, password: `admin123`)
- Branch restocking
- Real-time sales reports
- Total income tracking
- Product-wise sales analytics

### Technical Features
- **Architecture**: MVVM pattern
- **Networking**: Retrofit with REST API
- **Database**: H2 in-memory database
- **Backend**: Ktor framework
- **Payment**: M-Pesa integration (simulated)
- **Multi-device**: Supports 4 simultaneous devices
- **Real-time**: Live stock and sales updates

## 🎯 Demo Scenario

The system supports the exact demo scenario required:

1. **Admin Device**: Login and restock all branches
2. **Customer Devices 2, 3, 4**: Each selects different branch and purchases drinks
3. **Live Updates**: Stock decreases and sales reports update in real-time
4. **Payment**: M-Pesa STK Push simulation completes purchases

## 📊 Expected Demo Results

After 3 customers purchase from different branches:
```
Sales Report:
- Coke: 4 units, KES 480
- Fanta: 3 units, KES 360
- Sprite: 5 units, KES 600
Grand Total: KES 1,440
```

## 🔧 Requirements

- **Android Studio**: Latest version
- **Kotlin**: 1.9.10
- **JDK**: 8 or higher
- **Network**: All devices on same WiFi
- **M-Pesa**: Sandbox credentials (optional for demo)

## 🎓 Academic Use

This project demonstrates:
- Distributed systems architecture
- Mobile-backend integration
- RESTful API design
- Database transaction management
- Real-time state synchronization
- Payment gateway integration
- Role-based access control

Perfect for class projects and academic demonstrations!

## 📖 Detailed Instructions

See `DEMO_INSTRUCTIONS.md` for complete step-by-step demo guide.

---

**Built with ❤️ for academic purposes**
# KUNYWA
