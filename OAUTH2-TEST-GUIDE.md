# 🔐 Hướng dẫn Test OAuth2 Login với Google và Facebook

## 📋 Tổng quan

Dự án Mini Supermarket đã được tích hợp OAuth2 để hỗ trợ đăng nhập bằng Google và Facebook. Tài liệu này hướng dẫn cách test tính năng này.

## 🚀 Cách test OAuth2

### Bước 1: Cấu hình OAuth2 Credentials

#### 1.1 Google OAuth2
1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo project mới hoặc chọn project có sẵn
3. Enable Google+ API
4. Tạo OAuth 2.0 credentials:
   - Vào "Credentials" → "Create Credentials" → "OAuth 2.0 Client IDs"
   - Application type: "Web application"
   - Authorized redirect URIs: `http://localhost:8080/oauth2/callback/google`
5. Copy Client ID và Client Secret

#### 1.2 Facebook OAuth2
1. Truy cập [Facebook Developers](https://developers.facebook.com/)
2. Tạo app mới
3. Thêm Facebook Login product
4. Cấu hình OAuth redirect URIs: `http://localhost:8080/oauth2/callback/facebook`
5. Copy App ID và App Secret

### Bước 2: Cập nhật file .env

Tạo file `.env` từ `env.example` và cập nhật:

```env
# Google OAuth2 Configuration
GOOGLE_CLIENT_ID=your-google-client-id-here
GOOGLE_CLIENT_SECRET=your-google-client-secret-here

# Facebook OAuth2 Configuration
FACEBOOK_CLIENT_ID=your-facebook-client-id-here
FACEBOOK_CLIENT_SECRET=your-facebook-client-secret-here
```

### Bước 3: Khởi động ứng dụng

```bash
# Chạy ứng dụng Spring Boot
./mvnw spring-boot:run
```

### Bước 4: Test OAuth2

#### 4.1 Sử dụng trang test HTML
1. Mở trình duyệt và truy cập: `http://localhost:8080/oauth2-test.html`
2. Click "Test OAuth2 Config" để kiểm tra cấu hình
3. Click "Đăng nhập với Google" hoặc "Đăng nhập với Facebook"

#### 4.2 Test bằng API endpoints

##### Test cấu hình OAuth2:
```bash
curl -X GET http://localhost:8080/api/oauth2/test-config
```

##### Test đăng nhập Google:
```bash
# Bước 1: Redirect đến Google OAuth
curl -L http://localhost:8080/oauth2/authorization/google

# Bước 2: Sau khi đăng nhập Google, sẽ redirect về:
curl -X GET http://localhost:8080/api/oauth2/callback/google
```

##### Test đăng nhập Facebook:
```bash
# Bước 1: Redirect đến Facebook OAuth
curl -L http://localhost:8080/oauth2/authorization/facebook

# Bước 2: Sau khi đăng nhập Facebook, sẽ redirect về:
curl -X GET http://localhost:8080/api/oauth2/callback/facebook
```

##### Lấy thông tin user:
```bash
curl -X GET http://localhost:8080/api/oauth2/user-info
```

## 🔧 API Endpoints

### OAuth2 Endpoints
- `GET /oauth2/authorization/google` - Redirect đến Google OAuth
- `GET /oauth2/authorization/facebook` - Redirect đến Facebook OAuth
- `GET /oauth2/callback/google` - Callback từ Google OAuth
- `GET /oauth2/callback/facebook` - Callback từ Facebook OAuth

### Custom API Endpoints
- `GET /api/oauth2/test-config` - Kiểm tra cấu hình OAuth2
- `GET /api/oauth2/user-info` - Lấy thông tin OAuth2 user

## 📊 Response Format

### Success Response
```json
{
  "success": true,
  "message": "Đăng nhập Google thành công!",
  "result": {
    "authenticated": true,
    "message": "Đăng nhập Google thành công!",
    "user": {
      "maNguoiDung": "GOOGLE_123456789",
      "email": "user@gmail.com",
      "vaiTro": 3,
      "isDeleted": false
    },
    "role": "KHACH_HANG",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Email không được cung cấp từ Google!",
  "error": "Missing email"
}
```

## 🔍 Debug và Troubleshooting

### 1. Kiểm tra cấu hình
```bash
curl -X GET http://localhost:8080/api/oauth2/test-config
```

### 2. Kiểm tra logs
```bash
# Xem logs Spring Boot
tail -f logs/application.log
```

### 3. Các lỗi thường gặp

#### Lỗi "Client ID not configured"
- Kiểm tra file `.env` có đúng format không
- Đảm bảo biến môi trường được load đúng

#### Lỗi "Redirect URI mismatch"
- Kiểm tra redirect URI trong Google/Facebook console
- Đảm bảo URI khớp với cấu hình trong `application.yaml`

#### Lỗi "Email not provided"
- Đảm bảo scope được cấu hình đúng (email, profile)
- Kiểm tra quyền truy cập email trong OAuth app

## 🛠️ Cấu hình nâng cao

### Custom Redirect URI
Trong `application.yaml`:
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            redirect-uri: "{baseUrl}/custom-callback/google"
          facebook:
            redirect-uri: "{baseUrl}/custom-callback/facebook"
```

### Custom Scopes
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            scope:
              - email
              - profile
              - openid
          facebook:
            scope:
              - email
              - public_profile
              - user_friends
```

## 📝 Lưu ý quan trọng

1. **Bảo mật**: Không commit file `.env` lên git
2. **HTTPS**: Trong production, sử dụng HTTPS cho OAuth2
3. **Redirect URIs**: Cấu hình đúng redirect URIs trong OAuth providers
4. **Scopes**: Chỉ request những scope cần thiết
5. **Error Handling**: Xử lý lỗi OAuth2 một cách graceful

## 🎯 Test Cases

### Test Case 1: Đăng nhập Google lần đầu
1. Click "Đăng nhập với Google"
2. Đăng nhập Google account
3. Kiểm tra tài khoản mới được tạo
4. Kiểm tra JWT token được trả về

### Test Case 2: Đăng nhập Google lần thứ 2
1. Click "Đăng nhập với Google"
2. Đăng nhập cùng Google account
3. Kiểm tra tài khoản cũ được sử dụng
4. Kiểm tra JWT token được trả về

### Test Case 3: Đăng nhập Facebook
1. Click "Đăng nhập với Facebook"
2. Đăng nhập Facebook account
3. Kiểm tra tài khoản được tạo/cập nhật
4. Kiểm tra JWT token được trả về

### Test Case 4: Test với email không hợp lệ
1. Sử dụng Google account không có email
2. Kiểm tra error message phù hợp

## 🔗 Tài liệu tham khảo

- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)
- [Google OAuth2](https://developers.google.com/identity/protocols/oauth2)
- [Facebook OAuth2](https://developers.facebook.com/docs/facebook-login/)
- [JWT Documentation](https://jwt.io/) 