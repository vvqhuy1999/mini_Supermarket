# 🚀 Hướng dẫn nhanh đăng ký OAuth2 Credentials

## 📋 Tổng quan
Hướng dẫn này giúp bạn đăng ký OAuth2 credentials cho Google và Facebook để test đăng nhập OAuth2.

## 🔐 Đăng ký Google OAuth2

### Bước 1: Truy cập Google Cloud Console
1. Mở trình duyệt: https://console.cloud.google.com/
2. Đăng nhập bằng tài khoản Google

### Bước 2: Tạo Project
1. Click dropdown góc trên trái → "New Project"
2. Đặt tên: "Mini Supermarket OAuth2"
3. Click "Create"

### Bước 3: Enable API
1. Vào "APIs & Services" → "Library"
2. Tìm "Google+ API" hoặc "Google Identity"
3. Click "Enable"

### Bước 4: Tạo OAuth2 Credentials
1. Vào "APIs & Services" → "Credentials"
2. Click "Create Credentials" → "OAuth 2.0 Client IDs"
3. Nếu cần, tạo OAuth consent screen:
   - App name: "Mini Supermarket"
   - User support email: email của bạn
   - Developer contact: email của bạn
4. Quay lại tạo OAuth 2.0 Client ID:
   - Application type: "Web application"
   - Name: "Mini Supermarket Web Client"
   - **Authorized redirect URIs:**
     ```
     http://localhost:8080/oauth2/callback/google
     http://localhost:8080/login/oauth2/code/google
     ```
5. Click "Create"
6. **Copy Client ID và Client Secret**

## 📘 Đăng ký Facebook OAuth2

### Bước 1: Truy cập Facebook Developers
1. Mở trình duyệt: https://developers.facebook.com/
2. Đăng nhập bằng tài khoản Facebook

### Bước 2: Tạo App
1. Click "Create App"
2. Chọn "Consumer" hoặc "Business"
3. Đặt tên: "Mini Supermarket"
4. Click "Create App"

### Bước 3: Thêm Facebook Login
1. Trong dashboard, tìm "Add Product"
2. Click "Facebook Login" → "Set Up"
3. Chọn "Web" platform
4. Site URL: `http://localhost:8080`
5. Click "Save"

### Bước 4: Cấu hình OAuth
1. Vào "Facebook Login" → "Settings"
2. **Valid OAuth Redirect URIs:**
   ```
   http://localhost:8080/oauth2/callback/facebook
   http://localhost:8080/login/oauth2/code/facebook
   ```
3. Click "Save Changes"

### Bước 5: Lấy Credentials
1. Vào "Settings" → "Basic"
2. **Copy App ID và App Secret**

## ⚙️ Cấu hình ứng dụng

### Bước 1: Tạo file .env
```bash
# Copy từ env.example
cp env.example .env
```

### Bước 2: Cập nhật file .env
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
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

## 🧪 Test OAuth2

### Cách 1: Sử dụng script PowerShell (Windows)
```powershell
.\test-oauth2.ps1
```

### Cách 2: Sử dụng script Bash (Linux/Mac)
```bash
./test-oauth2.sh
```

### Cách 3: Test bằng trình duyệt
1. Mở trình duyệt: `http://localhost:8080/oauth2-test.html`
2. Click "Test OAuth2 Config"
3. Click "Đăng nhập với Google" hoặc "Đăng nhập với Facebook"

### Cách 4: Test bằng API
```bash
# Test config
curl -X GET http://localhost:8080/api/oauth2/test-config

# Test Google OAuth
curl -L http://localhost:8080/oauth2/authorization/google

# Test Facebook OAuth
curl -L http://localhost:8080/oauth2/authorization/facebook
```

## 🔍 Troubleshooting

### Lỗi "Client ID not configured"
- Kiểm tra file `.env` có đúng format không
- Đảm bảo biến môi trường được load đúng

### Lỗi "Redirect URI mismatch"
- Kiểm tra redirect URI trong Google/Facebook console
- Đảm bảo URI khớp với cấu hình trong `application.yaml`

### Lỗi "Email not provided"
- Đảm bảo scope được cấu hình đúng (email, profile)
- Kiểm tra quyền truy cập email trong OAuth app

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

## 🔗 Tài liệu tham khảo

- [Google OAuth2 Setup](https://developers.google.com/identity/protocols/oauth2)
- [Facebook OAuth2 Setup](https://developers.facebook.com/docs/facebook-login/)
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html) 