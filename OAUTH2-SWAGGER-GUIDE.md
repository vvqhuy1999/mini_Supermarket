# 🔐 OAuth2 Swagger API Guide

## 📋 **Tổng quan:**

Đã thay thế tất cả HTML test pages bằng **Swagger UI** để test OAuth2 APIs một cách chuyên nghiệp và đầy đủ.

---

## 🌐 **Quick Start:**

### **1️⃣ Khởi động ứng dụng:**
```bash
./mvnw spring-boot:run
# Hoặc
.\mvnw.cmd spring-boot:run
```

### **2️⃣ Mở Swagger UI:**
```
http://localhost:8080/swagger-ui/index.html
```

### **3️⃣ Đăng nhập OAuth2:**
```
http://localhost:8080/oauth2/authorization/google
http://localhost:8080/oauth2/authorization/facebook
```

### **4️⃣ Test bằng PowerShell:**
```powershell
.\test-oauth2-swagger.ps1
```

---

## 🎯 **OAuth2 APIs trong Swagger:**

### **🔐 OAuth2 Authentication Section:**

| API | Method | Mô tả | Auth Required |
|-----|--------|-------|---------------|
| **✅ /oauth2/success** | GET | Main OAuth2 success handler | ✅ OAuth2 |
| **🔍 /oauth2/analyze** | GET | Phân tích OAuth2 user data | ✅ OAuth2 |
| **📧 /oauth2/check-email** | GET | Kiểm tra email tồn tại | ❌ Public |
| **🆔 /oauth2/check-sub** | GET | Kiểm tra OAuth2 sub tồn tại | ❌ Public |
| **🎫 /oauth2/get-token** | GET | Lấy JWT token | ✅ OAuth2 |
| **🔒 /oauth2/logout** | POST | Local logout | 🎫 JWT |
| **🌐 /oauth2/logout/website** | POST | Website logout | 🎫 JWT |
| **🔧 /oauth2/test-config** | GET | Test cấu hình OAuth2 | ❌ Public |

---

## 🧪 **Testing Flow:**

### **📝 Step 1: Login OAuth2**
```bash
# Mở browser và đăng nhập
http://localhost:8080/oauth2/authorization/google
```

### **📝 Step 2: Kiểm tra Success**
- Sau login, auto redirect về Swagger UI
- Tìm section "🔐 OAuth2 Authentication"
- Test endpoint `/oauth2/success`

### **📝 Step 3: Lấy JWT Token**
- Gọi `/oauth2/get-token` 
- Copy JWT token từ response
- Dùng token cho protected APIs

### **📝 Step 4: Test Protected APIs**
- Click "Authorize" button trong Swagger
- Nhập: `Bearer {JWT_TOKEN}`
- Test các logout APIs

---

## 🎫 **JWT Token Usage:**

### **🔧 Lấy Token:**
```http
GET /api/oauth2/get-token
Authorization: OAuth2 (sau khi login)
```

### **📤 Response:**
```json
{
  "success": true,
  "message": "Lấy JWT token thành công",
  "result": {
    "jwt_token": "eyJhbGciOiJIUzUxMiJ9...",
    "user_email": "user@gmail.com",
    "user_role": "KHACH_HANG",
    "provider": "Google",
    "authenticated": true,
    "token_type": "Bearer"
  }
}
```

### **🔐 Sử dụng Token:**
```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

## 🔍 **Swagger Features:**

### **✅ Auto Documentation:**
- 📚 Mô tả chi tiết cho từng API
- 📊 Request/Response examples
- 🏷️ Parameter descriptions
- ⚠️ Error codes và messages

### **✅ Interactive Testing:**
- 🧪 "Try it out" button
- 📝 Fill parameters trực tiếp
- 🚀 Execute requests in browser
- 📋 Copy curl commands

### **✅ Authentication Support:**
- 🔐 OAuth2 flow integration
- 🎫 JWT Bearer token support
- 🔒 Protected endpoint testing

---

## 📊 **API Response Examples:**

### **✅ Success Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "result": {
    "data": "..."
  }
}
```

### **❌ Error Response:**
```json
{
  "success": false,
  "message": "Error description",
  "error": "Technical error details"
}
```

---

## 🔧 **Configuration:**

### **📂 Swagger Settings (application.yaml):**
```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    display-request-duration: true
    operations-sorter: method
```

### **📂 OAuth2 Redirect (application.yaml):**
```yaml
oauth2:
  frontend:
    base-url: http://localhost:8080
    success-path: /swagger-ui/index.html
    failure-path: /swagger-ui/index.html
```

---

## 🚀 **Advanced Testing:**

### **🔄 Test Complete Flow:**
1. **Login:** `/oauth2/authorization/google`
2. **Success:** `/oauth2/success` (auto called)
3. **Get Token:** `/oauth2/get-token`
4. **Analyze:** `/oauth2/analyze`
5. **Logout:** `/oauth2/logout/website`

### **📧 Test Email Check:**
```http
GET /api/oauth2/check-email?email=test@gmail.com
```

### **🆔 Test Sub Check:**
```http
GET /api/oauth2/check-sub?sub=113575857897519834201
```

---

## ⚠️ **Common Issues:**

### **🔧 OAuth2 Not Working:**
- ✅ Check `application.yaml` Google config
- ✅ Verify redirect URI in Google Console
- ✅ Ensure app is running on port 8080

### **🎫 JWT Token Error:**
- ✅ Login OAuth2 first
- ✅ Copy complete token (starts with `eyJ`)
- ✅ Use `Bearer ` prefix in Authorization

### **📊 API Not Showing:**
- ✅ Check controller `@Tag` annotations
- ✅ Restart Spring Boot app
- ✅ Clear browser cache

---

## 🎯 **Best Practices:**

### **🧪 For Testing:**
1. **Always login OAuth2 first**
2. **Use Swagger "Authorize" button for JWT**
3. **Test endpoints in order (success → token → logout)**
4. **Copy responses for documentation**

### **🔐 For Security:**
1. **Don't share JWT tokens**
2. **Logout after testing**
3. **Use different browsers for different accounts**
4. **Clear tokens before committing code**

---

## 📱 **Mobile/Frontend Integration:**

### **🌐 For Frontend Developers:**
```javascript
// Redirect user to OAuth2 login
window.location.href = 'http://localhost:8080/oauth2/authorization/google';

// Handle success callback
// User will be redirected to your frontend with token in URL params

// Use JWT for API calls
fetch('/api/protected-endpoint', {
  headers: {
    'Authorization': `Bearer ${jwtToken}`
  }
});
```

### **📱 For Mobile Apps:**
```http
# Login flow
GET /oauth2/authorization/google

# After callback success
GET /api/oauth2/get-token
Authorization: Bearer {session_token}

# Use JWT for subsequent calls
Authorization: Bearer {jwt_token}
```

---

## 🎉 **Summary:**

### **✅ Advantages of Swagger UI:**
- ✅ **Professional API documentation**
- ✅ **Interactive testing without HTML pages**
- ✅ **Auto-generated from annotations**
- ✅ **Support for OAuth2 and JWT**
- ✅ **Easy to share with team/frontend**

### **🔥 Key Features:**
- ✅ **Complete OAuth2 flow testing**
- ✅ **JWT token management**
- ✅ **Detailed API documentation**
- ✅ **Error handling examples**
- ✅ **Ready for production use**

**🚀 OAuth2 system hoàn chỉnh với Swagger UI - Ready for professional development!** 🎯🔐
