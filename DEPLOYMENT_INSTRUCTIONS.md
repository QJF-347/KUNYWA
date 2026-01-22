# 🚀 Cloud Deployment Instructions

## 📋 Overview
This guide shows how to deploy the supermarket backend to cloud hosting and configure the Android app to connect to it.

## 🌐 Backend Deployment Options

### Option 1: Railway (Recommended for Students)
```bash
# 1. Install Railway CLI
npm install -g @railway/cli

# 2. Login to Railway
railway login

# 3. Initialize project
cd supermarket-backend
railway init

# 4. Deploy
railway up

# 5. Add PostgreSQL database
railway add postgresql

# 6. Set environment variables in Railway dashboard:
# - MPESA_CONSUMER_KEY
# - MPESA_CONSUMER_SECRET  
# - MPESA_PASSKEY
# - SIMULATE_MPESA = true

# 7. Get your deployed URL from Railway dashboard
# Example: https://supermarket-api.up.railway.app
```

### Option 2: Render
```bash
# 1. Create render.yaml (already provided)
# 2. Push to GitHub
git add .
git commit -m "Deploy to Render"
git push origin main

# 3. Connect GitHub repo to render.com
# 4. Render will auto-deploy using render.yaml
```

### Option 3: Fly.io
```bash
# 1. Install Fly CLI
curl -L https://fly.io/install.sh | sh

# 2. Login
fly auth login

# 3. Launch app
cd supermarket-backend
fly launch

# 4. Set secrets
fly secrets set DATABASE_URL="postgresql://..."
fly secrets set MPESA_CONSUMER_KEY="..."
fly secrets set MPESA_CONSUMER_SECRET="..."
fly secrets set MPESA_PASSKEY="..."
fly secrets set SIMULATE_MPESA="true"

# 5. Deploy
fly deploy
```

## 📱 Android App Configuration

### 1. Update Constants.kt
```kotlin
// In SupermarketApp/app/src/main/java/com/supermarket/utils/Constants.kt
object Constants {
    const val BASE_URL = "https://your-deployed-url.com/" // Replace with your deployed URL
    // ... rest of constants
}
```

### 2. Update Network Security Config
```xml
<!-- In app/src/main/res/xml/network_security_config.xml -->
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">your-deployed-url.com</domain>
    </domain-config>
</network-security-config>
```

### 3. Build and Install APK
```bash
cd SupermarketApp
./gradlew assembleDebug

# Install on devices
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 🔧 Environment Variables

### Required for Production
```bash
DATABASE_URL=postgresql://user:password@host:port/database
DATABASE_USER=postgres
DATABASE_PASSWORD=your_password
PORT=8080 (or 10000 for Render)
```

### M-Pesa Configuration
```bash
MPESA_CONSUMER_KEY=your_sandbox_consumer_key
MPESA_CONSUMER_SECRET=your_sandbox_consumer_secret
MPESA_PASSKEY=your_sandbox_passkey
SIMULATE_MPESA=true  # Set to false for real M-Pesa
```

## 🎯 Demo Setup

### Step 1: Deploy Backend
1. Choose hosting platform (Railway recommended)
2. Deploy using instructions above
3. Note your deployed URL

### Step 2: Configure Android App
1. Update `BASE_URL` in Constants.kt
2. Update network security config
3. Build APK

### Step 3: Install on 4 Devices
1. Install same APK on all 4 devices
2. Test with different user roles

### Step 4: Test Demo Scenario
1. **Device 1 (Admin)**: Login with `admin`/`admin123`
2. **Devices 2,3,4 (Customers)**: Register and login
3. All devices work independently through cloud backend

## 🌍 Testing Cloud Deployment

### Health Check
```bash
curl https://your-deployed-url.com/customer/branches
```

### Expected Response
```json
[
  {"id":1,"name":"Nairobi","location":"Nairobi"},
  {"id":2,"name":"Kisumu","location":"Kisumu"},
  {"id":3,"name":"Mombasa","location":"Mombasa"},
  {"id":4,"name":"Nakuru","location":"Nakuru"},
  {"id":5,"name":"Eldoret","location":"Eldoret"}
]
```

## 📋 Deployment Checklist

### Backend
- [ ] PostgreSQL database created
- [ ] Environment variables set
- [ ] HTTPS endpoints working
- [ ] M-Pesa callbacks configured
- [ ] Health check passing

### Android App
- [ ] BASE_URL updated to HTTPS
- [ ] Network security config updated
- [ ] APK built and signed
- [ ] Tested on real devices

### Demo Ready
- [ ] 4 devices can connect simultaneously
- [ ] Real-time stock updates working
- [ ] Sales reports accurate
- [ ] M-Pesa simulation functional

## 🆘 Troubleshooting

### Common Issues
1. **Database Connection**: Check DATABASE_URL format
2. **CORS Errors**: Ensure backend allows your domain
3. **HTTPS Issues**: Use HTTPS URLs in Android
4. **M-Pesa Failures**: Check sandbox credentials

### Debug Commands
```bash
# Check backend logs
railway logs

# Test API endpoints
curl -X POST https://your-url.com/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## 🎓 Academic Success

✅ **Cloud-hosted backend** - No laptop dependency  
✅ **HTTPS endpoints** - Secure for M-Pesa callbacks  
✅ **4 device support** - Real-time distributed system  
✅ **Single APK** - Role-based behavior  
✅ **Production ready** - Environment variables, Docker  
✅ **Demo safe** - Simulated M-Pesa option  

**Your distributed Android system is now cloud-ready!** 🚀
