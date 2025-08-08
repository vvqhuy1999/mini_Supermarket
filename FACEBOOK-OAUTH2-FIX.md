# 🔧 Khắc phục vấn đề Facebook OAuth2

## 📋 Vấn đề hiện tại

Hệ thống không thể đăng nhập hoặc đăng ký bằng Facebook. Các nguyên nhân có thể:

1. **Cấu hình Facebook App không đúng**
2. **Redirect URI không khớp**
3. **Thiếu quyền truy cập email**
4. **Cấu hình Spring Security OAuth2 không đúng**

## 🛠️ Các bước khắc phục

### Bước 1: Kiểm tra cấu hình Facebook App

#### 1.1 Truy cập Facebook Developers
1. Vào [Facebook Developers](https://developers.facebook.com/)
2. Chọn app của bạn hoặc tạo app mới
3. Vào "Settings" → "Basic"

#### 1.2 Cấu hình App Domains
- Thêm `localhost` vào **App Domains**
- Thêm `127.0.0.1` vào **App Domains**

#### 1.3 Cấu hình OAuth Redirect URIs
- Vào "Facebook Login" → "Settings"
- Thêm vào **Valid OAuth Redirect URIs**:
  ```
  http://localhost:8080/login/oauth2/code/facebook
  ```

#### 1.4 Cấu hình Client OAuth Settings
- **Client OAuth Login**: Bật
- **Web OAuth Login**: Bật
- **Enforce HTTPS**: Tắt (cho development)
- **Use Strict Mode for Redirect URIs**: Tắt

### Bước 2: Kiểm tra cấu hình Spring Boot

#### 2.1 Cập nhật application.yaml
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          facebook:
            client-id: YOUR_FACEBOOK_APP_ID
            client-secret: YOUR_FACEBOOK_APP_SECRET
            scope:
              - email
              - public_profile
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
        provider:
          facebook:
            authorization-uri: https://www.facebook.com/dialog/oauth
            token-uri: https://graph.facebook.com/oauth/access_token
            user-info-uri: https://graph.facebook.com/me?fields=id,first_name,middle_name,last_name,name,email,verified
            user-name-attribute: id
```

#### 2.2 Kiểm tra SecurityConfig
Đảm bảo các endpoint OAuth2 được cho phép:
```java
private final String[] PUBLIC_ENDPOINTS = {
    "/oauth2/**",
    "/login/oauth2/code/**",
    "/api/oauth2/**"
};
```

### Bước 3: Test và Debug

#### 3.1 Test cấu hình
```bash
curl -X GET http://localhost:8080/api/oauth2/test-config
```

#### 3.2 Test Facebook OAuth2
1. Mở trình duyệt và truy cập: `http://localhost:8080/facebook-test.html`
2. Click "Test OAuth2 Config"
3. Click "Đăng nhập với Facebook"

#### 3.3 Debug Facebook OAuth2
```bash
curl -X GET http://localhost:8080/api/oauth2/debug/facebook
```

### Bước 4: Kiểm tra logs

#### 4.1 Xem logs Spring Boot
```bash
tail -f logs/application.log
```

#### 4.2 Kiểm tra logs Facebook
- Vào Facebook Developers → App → Analytics
- Xem "Login" events

## 🔍 Debug chi tiết

### 1. Kiểm tra Facebook App Settings
```bash
# Test Facebook App ID
curl -X GET "https://graph.facebook.com/v3.0/YOUR_APP_ID?access_token=YOUR_APP_ACCESS_TOKEN"
```

### 2. Test OAuth2 Flow
```bash
# Bước 1: Authorization URL
curl -L "https://www.facebook.com/dialog/oauth?client_id=YOUR_APP_ID&redirect_uri=http://localhost:8080/login/oauth2/code/facebook&scope=email,public_profile"

# Bước 2: Sau khi authorize, kiểm tra callback
curl -X GET "http://localhost:8080/api/oauth2/callback/facebook"
```

### 3. Kiểm tra User Info
```bash
# Test Facebook Graph API
curl -X GET "https://graph.facebook.com/me?fields=id,first_name,last_name,name,email&access_token=USER_ACCESS_TOKEN"
```

## 🚨 Các lỗi thường gặp

### Lỗi 1: "Invalid redirect_uri"
**Nguyên nhân**: Redirect URI không khớp với cấu hình Facebook
**Giải pháp**: 
- Kiểm tra Valid OAuth Redirect URIs trong Facebook App
- Đảm bảo URI chính xác: `http://localhost:8080/login/oauth2/code/facebook`

### Lỗi 2: "App not configured for OAuth"
**Nguyên nhân**: Facebook App chưa được cấu hình OAuth
**Giải pháp**:
- Vào Facebook Developers → App → Add Product → Facebook Login
- Cấu hình OAuth settings

### Lỗi 3: "Email not provided"
**Nguyên nhân**: Facebook không trả về email
**Giải pháp**:
- Kiểm tra scope có `email` không
- Đảm bảo user cho phép truy cập email
- Kiểm tra App Review Status

### Lỗi 4: "Client ID not configured"
**Nguyên nhân**: Client ID không đúng hoặc không được load
**Giải pháp**:
- Kiểm tra `application.yaml` có đúng Client ID không
- Restart ứng dụng sau khi thay đổi config

## 📝 Checklist khắc phục

- [ ] Facebook App đã được tạo và cấu hình
- [ ] App Domains có `localhost`
- [ ] Valid OAuth Redirect URIs có `http://localhost:8080/login/oauth2/code/facebook`
- [ ] Facebook Login product đã được thêm
- [ ] Client OAuth Login đã được bật
- [ ] `application.yaml` có đúng Client ID và Secret
- [ ] Spring Security config cho phép OAuth2 endpoints
- [ ] Ứng dụng đã restart sau khi thay đổi config
- [ ] Test cấu hình thành công
- [ ] Test đăng nhập Facebook thành công

## 🔗 Tài liệu tham khảo

- [Facebook Login Documentation](https://developers.facebook.com/docs/facebook-login/)
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)
- [Facebook Graph API](https://developers.facebook.com/docs/graph-api/)

## 📞 Hỗ trợ

Nếu vẫn gặp vấn đề, hãy:

1. Kiểm tra logs chi tiết
2. Test từng bước theo hướng dẫn
3. Đảm bảo Facebook App đã được review (nếu cần)
4. Kiểm tra network connectivity
5. Test với Facebook test users 