# 🔑 Hướng dẫn lấy JWT Token từ OAuth2 Login

## 📋 **Tổng quan:**

Sau khi đăng nhập Google OAuth2 thành công, có nhiều cách để lấy JWT token để sử dụng cho các API calls tiếp theo.

---

## 🎯 **Cách 1: Từ endpoint `/api/oauth2/success`**

### **📍 Endpoint:**
```
GET /api/oauth2/success
```

### **📝 Cách sử dụng:**
1. **Đăng nhập Google** bằng cách truy cập: `http://localhost:8080/oauth2/authorization/google`
2. **Sau khi đăng nhập thành công**, bạn sẽ được redirect về: `http://localhost:8080/api/oauth2/success`
3. **Response sẽ chứa token**:

```json
{
  "success": true,
  "message": "OAuth2 login successful",
  "result": {
    "message": "OAuth2 login successful and saved to database",
    "user": { /* OAuth2 user data */ },
    "provider": "Google",
    "saved_to_database": true,
    "auth_response": {
      "authenticated": true,
      "message": "Đăng nhập Google thành công!",
      "user": { /* Database user info */ },
      "role": "KHACH_HANG",
      "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGdtYWlsLmNvbSIsInJvbGUiOiJLSEFDSF9IQU5HIiwiaXNzIjoibWluaS1zdXBlcm1hcmtldCIsImlhdCI6MTY0MDk5NTIwMCwiZXhwIjoxNjQxMDgxNjAwfQ.signature"
    },
    "jwt_token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGdtYWlsLmNvbSIsInJvbGUiOiJLSEFDSF9IQU5HIiwiaXNzIjoibWluaS1zdXBlcm1hcmtldCIsImlhdCI6MTY0MDk5NTIwMCwiZXhwIjoxNjQxMDgxNjAwfQ.signature"
  }
}
```

---

## 🎫 **Cách 2: Endpoint chuyên dụng `/api/oauth2/get-token`**

### **📍 Endpoint:**
```
GET /api/oauth2/get-token
```

### **📝 Cách sử dụng:**
1. **Đăng nhập Google trước**
2. **Gọi endpoint** này để lấy token:

```bash
curl -X GET "http://localhost:8080/api/oauth2/get-token" \
     -H "Content-Type: application/json"
```

### **📄 Response:**
```json
{
  "success": true,
  "message": "Lấy JWT token thành công",
  "result": {
    "jwt_token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGdtYWlsLmNvbSIsInJvbGUiOiJLSEFDSF9IQU5HIiwiaXNzIjoibWluaS1zdXBlcm1hcmtldCIsImlhdCI6MTY0MDk5NTIwMCwiZXhwIjoxNjQxMDgxNjAwfQ.signature",
    "user_email": "user@gmail.com",
    "user_role": "KHACH_HANG",
    "provider": "Google",
    "authenticated": true,
    "token_type": "Bearer",
    "user_info": {
      "maNguoiDung": "NDA1B2C3D4",
      "email": "user@gmail.com",
      "sub": "123456789012345678901",
      "vaiTro": 3,
      "isDeleted": false
    }
  }
}
```

---

## 🌐 **Cách 3: Từ Frontend (oauth2-test.html)**

### **📝 Cách sử dụng:**
1. **Mở browser**: `http://localhost:8080/oauth2-test.html`
2. **Đăng nhập Google** bằng nút "🔍 Đăng nhập với Google"
3. **Sau khi đăng nhập thành công**, click nút "🎫 Lấy JWT Token"
4. **Token sẽ hiển thị** và tự động copy vào clipboard

### **🖼️ Frontend UI:**
```html
<div class="test-section">
    <h3>🔑 Get JWT Token</h3>
    <button class="btn btn-success" onclick="getJWTToken()">🎫 Lấy JWT Token</button>
    <div id="tokenResult" class="result"></div>
</div>
```

### **📄 JavaScript code:**
```javascript
async function getJWTToken() {
    try {
        const response = await fetch(`${API_BASE}/api/oauth2/get-token`);
        const data = await response.json();
        
        if (data.success) {
            const result = data.result;
            console.log('JWT Token:', result.jwt_token);
            
            // Auto copy to clipboard
            if (navigator.clipboard) {
                navigator.clipboard.writeText(result.jwt_token);
            }
        }
    } catch (error) {
        console.error('Error getting token:', error);
    }
}
```

---

## 🔐 **JWT Token Structure**

### **📄 Header:**
```json
{
  "alg": "HS512",
  "typ": "JWT"
}
```

### **📄 Payload:**
```json
{
  "sub": "user@gmail.com",          // Subject (email)
  "role": "KHACH_HANG",             // User role
  "iss": "mini-supermarket",        // Issuer
  "iat": 1640995200,                // Issued at
  "exp": 1641081600                 // Expires at
}
```

### **🔐 Signature:**
```
HMACSHA512(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret
)
```

---

## 🚀 **Sử dụng Token cho API calls**

### **📍 Header format:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGdtYWlsLmNvbSIsInJvbGUiOiJLSEFDSF9IQU5HIiwiaXNzIjoibWluaS1zdXBlcm1hcmtldCIsImlhdCI6MTY0MDk5NTIwMCwiZXhwIjoxNjQxMDgxNjAwfQ.signature
```

### **📝 Example API call:**
```bash
curl -X GET "http://localhost:8080/api/protected-endpoint" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
     -H "Content-Type: application/json"
```

### **📄 JavaScript example:**
```javascript
const token = "eyJhbGciOiJIUzUxMiJ9..."; // Your JWT token

fetch(`${API_BASE}/api/protected-endpoint`, {
    method: 'GET',
    headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    }
})
.then(response => response.json())
.then(data => console.log(data));
```

---

## 🛠️ **Testing Tools**

### **🧪 PowerShell script:**
```powershell
# Test lấy token
.\oauth2-complete-test.ps1

# Mở browser để test thủ công
.\oauth2-complete-test.ps1 -OpenBrowser
```

### **🌐 Browser testing:**
1. **Truy cập**: `http://localhost:8080/oauth2-test.html`
2. **Đăng nhập Google**
3. **Click**: "🎫 Lấy JWT Token"
4. **Token sẽ hiển thị** và copy vào clipboard

### **📱 API testing tools:**
- **Postman**: Import endpoints và test
- **Insomnia**: REST client
- **curl**: Command line testing

---

## ⚠️ **Lưu ý quan trọng**

### **🔒 Security:**
- ✅ **Không share token** qua email hoặc chat
- ✅ **Lưu token an toàn** (localStorage/sessionStorage)
- ✅ **Kiểm tra expiration** trước khi sử dụng
- ✅ **Refresh token** khi hết hạn

### **⏰ Token expiration:**
- **Default**: 24 giờ (86400 seconds)
- **Kiểm tra exp claim** trong payload
- **Refresh** bằng cách đăng nhập lại

### **🔄 Refresh flow:**
1. **Kiểm tra token hết hạn**
2. **Nếu hết hạn**: Gọi lại `/api/oauth2/get-token`
3. **Cập nhật token mới**

---

## 🐛 **Troubleshooting**

### **❌ Problem: "Chưa đăng nhập OAuth2!"**
**🔧 Solution:**
1. Đăng nhập Google trước: `http://localhost:8080/oauth2/authorization/google`
2. Sau đó gọi `/api/oauth2/get-token`

### **❌ Problem: "Token không hợp lệ"**
**🔧 Solution:**
1. Kiểm tra token format
2. Kiểm tra expiration time
3. Đăng nhập lại để lấy token mới

### **❌ Problem: "OAuth2 user is null"**
**🔧 Solution:**
1. Đảm bảo đã hoàn thành OAuth2 flow
2. Kiểm tra Spring Security configuration
3. Xem logs để debug

---

## 📊 **Flow tổng thể:**

```mermaid
graph TD
    A[User clicks Google Login] --> B[OAuth2 Authentication]
    B --> C[Redirect to /api/oauth2/success]
    C --> D[Get JWT Token from response]
    D --> E[Use token for API calls]
    
    F[Alternative: Call /api/oauth2/get-token] --> G[Get JWT Token]
    G --> E
    
    H[Frontend: Click Get Token button] --> I[Call /api/oauth2/get-token]
    I --> G
```

---

## 🎉 **Kết luận:**

Bạn có **3 cách chính** để lấy JWT token sau khi đăng nhập Google OAuth2:

1. **📄 Từ `/api/oauth2/success`** - Tự động sau khi đăng nhập
2. **🎫 Từ `/api/oauth2/get-token`** - Endpoint chuyên dụng
3. **🌐 Từ Frontend** - UI button với auto-copy

**Token này có thể sử dụng** cho tất cả API calls có yêu cầu authentication với format `Bearer {token}` trong header! 🚀
