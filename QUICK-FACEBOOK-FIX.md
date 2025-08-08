# 🚀 Khắc phục nhanh Facebook OAuth2

## 📋 Vấn đề hiện tại
Hệ thống không thể đăng nhập/đăng ký bằng Facebook.

## 🔧 Giải pháp nhanh

### Bước 1: Kiểm tra cấu hình hiện tại
```bash
# Chạy script test
.\test-facebook-oauth2.ps1
```

### Bước 2: Cấu hình Facebook App

#### 2.1 Truy cập Facebook Developers
1. Vào [Facebook Developers](https://developers.facebook.com/)
2. Chọn app của bạn hoặc tạo app mới

#### 2.2 Cấu hình App Settings
- **App Domains**: Thêm `localhost`
- **Valid OAuth Redirect URIs**: Thêm `http://localhost:8080/login/oauth2/code/facebook`

#### 2.3 Cấu hình Facebook Login
- Vào "Facebook Login" → "Settings"
- **Client OAuth Login**: Bật
- **Web OAuth Login**: Bật
- **Enforce HTTPS**: Tắt (cho development)

### Bước 3: Test ngay lập tức

#### 3.1 Mở trình duyệt
```
http://localhost:8080/facebook-test.html
```

#### 3.2 Test từng bước
1. Click "Test OAuth2 Config" - kiểm tra cấu hình
2. Click "Đăng nhập với Facebook" - test đăng nhập
3. Xem kết quả trong trang test

### Bước 4: Debug nếu cần

#### 4.1 Kiểm tra logs
```bash
# Xem logs Spring Boot
tail -f logs/application.log
```

#### 4.2 Test API endpoints
```bash
# Test cấu hình
curl http://localhost:8080/api/oauth2/test-config

# Test Facebook debug
curl http://localhost:8080/api/oauth2/debug/facebook
```

## 🚨 Các lỗi thường gặp

### Lỗi "Invalid redirect_uri"
**Giải pháp**: Đảm bảo Facebook App có redirect URI: `http://localhost:8080/login/oauth2/code/facebook`

### Lỗi "App not configured for OAuth"
**Giải pháp**: Thêm Facebook Login product vào app

### Lỗi "Email not provided"
**Giải pháp**: Kiểm tra scope có `email` và user cho phép truy cập email

## ✅ Checklist nhanh

- [ ] Facebook App đã được tạo
- [ ] App Domains có `localhost`
- [ ] Valid OAuth Redirect URIs có `http://localhost:8080/login/oauth2/code/facebook`
- [ ] Facebook Login product đã được thêm
- [ ] Client OAuth Login đã được bật
- [ ] Test cấu hình thành công
- [ ] Test đăng nhập Facebook thành công

## 📞 Hỗ trợ nhanh

Nếu vẫn gặp vấn đề:
1. Kiểm tra Facebook App configuration
2. Restart ứng dụng Spring Boot
3. Clear browser cache
4. Test với Facebook test users 