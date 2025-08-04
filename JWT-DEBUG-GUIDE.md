# Hướng dẫn Debug JWT Invalid Signature

## Vấn đề
Lỗi "Invalid Signature" xảy ra khi JWT token được tạo với một secret key nhưng được verify với một secret key khác, hoặc khi secret key không đủ mạnh cho thuật toán HS256.

## Các thay đổi đã thực hiện

### 1. Cải thiện JwtUtil.java
- Thêm logging để debug
- Đảm bảo secret key có độ dài đủ cho HS256 (ít nhất 32 bytes)
- Sử dụng StandardCharsets.UTF_8 để encoding nhất quán
- Thêm method debugToken() để phân tích token

### 2. Cập nhật Secret Key
- Tăng độ dài secret key trong `application.yaml`
- Đảm bảo secret key đủ mạnh cho thuật toán HS256

### 3. Thêm Debug Endpoint
- Endpoint `/api/auth/debug-token` để phân tích token
- Endpoint `/api/auth/validate-token` để kiểm tra token

## Cách Test và Debug

### 1. Test với Postman hoặc curl

#### Đăng nhập để lấy token:
```bash
curl -X POST http://localhost:8080/api/auth/log-in \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "matKhau": "password"
  }'
```

#### Debug token:
```bash
curl -X POST http://localhost:8080/api/auth/debug-token \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_JWT_TOKEN_HERE"
  }'
```

#### Validate token:
```bash
curl -X POST http://localhost:8080/api/auth/validate-token \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_JWT_TOKEN_HERE"
  }'
```

### 2. Kiểm tra Logs
Khi chạy ứng dụng, bạn sẽ thấy các log debug trong console:
- Token generation logs
- Token validation logs
- Signature verification logs

### 3. Sử dụng Swagger UI
1. Mở http://localhost:8080/swagger-ui.html
2. Tìm đến AuthenticationController
3. Test các endpoints:
   - POST /api/auth/log-in
   - POST /api/auth/debug-token
   - POST /api/auth/validate-token

## Các nguyên nhân có thể gây lỗi Invalid Signature

### 1. Secret Key không đủ mạnh
- HS256 yêu cầu secret key ít nhất 256 bits (32 bytes)
- Secret key quá ngắn sẽ gây lỗi

### 2. Encoding không nhất quán
- Sử dụng StandardCharsets.UTF_8 để đảm bảo encoding nhất quán
- Tránh sử dụng String.getBytes() không chỉ định charset

### 3. Secret Key khác nhau giữa các lần tạo và verify
- Đảm bảo cùng một secret key được sử dụng
- Kiểm tra cấu hình trong application.yaml

### 4. Token bị modify
- Token có thể bị thay đổi trong quá trình truyền
- Kiểm tra token có bị encode/decode không đúng

## Cách khắc phục

### 1. Kiểm tra Secret Key
```java
// Trong JwtUtil.java
byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
System.out.println("Secret key length: " + secretBytes.length);
```

### 2. Debug Token
Sử dụng endpoint debug-token để xem chi tiết:
- Algorithm
- Issuer
- Subject
- Role
- Signature validity
- Secret key length

### 3. Regenerate Token
Nếu token cũ có vấn đề, tạo token mới:
1. Đăng nhập lại
2. Lấy token mới
3. Test với token mới

### 4. Kiểm tra Environment
- Đảm bảo cùng một application profile được sử dụng
- Kiểm tra secret key trong tất cả các file cấu hình

## Test Cases

### Test Case 1: Generate và Validate Token
```java
String token = jwtUtil.generateToken("test@example.com", "ADMIN");
boolean isValid = jwtUtil.validateToken(token);
assertTrue(isValid);
```

### Test Case 2: Invalid Token
```java
boolean isValid = jwtUtil.validateToken("invalid.token.here");
assertFalse(isValid);
```



## Monitoring và Logging

### Log Levels
- DEBUG: Chi tiết về token generation và validation
- WARN: Secret key quá ngắn
- ERROR: Lỗi trong quá trình xử lý token

### Metrics
- Token generation success rate
- Token validation success rate
- Invalid signature errors

## Troubleshooting Checklist

- [ ] Secret key có độ dài đủ (>= 32 bytes)
- [ ] Encoding nhất quán (UTF-8)
- [ ] Cùng secret key cho generate và validate
- [ ] Token không bị modify
- [ ] Thuật toán HS256 được sử dụng
- [ ] Token format đúng (3 parts separated by dots)
- [ ] Token không expired
- [ ] Token không trong blacklist

## Contact
Nếu vẫn gặp vấn đề, hãy:
1. Chạy debug-token endpoint
2. Kiểm tra logs
3. Cung cấp thông tin debug cho support 