# JWT Payload Verification Guide

## Tổng quan về JWT Payload

JWT (JSON Web Token) payload chứa các thông tin (claims) về người dùng và token. Dựa trên code hiện tại, payload có cấu trúc như sau:

### Cấu trúc Payload

```json
{
  "sub": "username@example.com",     // Subject - tên đăng nhập
  "role": "ADMIN",                   // Custom claim - vai trò người dùng
  "iss": "mini-supermarket",         // Issuer - người phát hành token
  "iat": 1703123456789,             // Issued At - thời gian phát hành
  "exp": 1703209856789              // Expiration - thời gian hết hạn
}
```

## Cách kiểm tra JWT Payload

### 1. Sử dụng API Endpoints

#### Decode token có sẵn:
```bash
curl -X POST http://localhost:8080/api/auth/decode-token \
  -H "Content-Type: application/json" \
  -d '{"token": "your-jwt-token-here"}'
```

#### Test tạo token mới:
```bash
curl -X POST http://localhost:8080/api/auth/test-token \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "matKhau": "password123"
  }'
```

### 2. Sử dụng Online JWT Debugger

1. Truy cập: https://jwt.io/
2. Paste JWT token vào ô "Encoded"
3. Xem payload trong phần "Decoded" > "Payload"

### 3. Sử dụng Code Java

```java
// Decode token
SignedJWT signedJWT = SignedJWT.parse(token);
JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

// Lấy thông tin từ payload
String username = claimsSet.getSubject();           // "sub" claim
String role = (String) claimsSet.getClaim("role");  // Custom "role" claim
String issuer = claimsSet.getIssuer();              // "iss" claim
Date issueTime = claimsSet.getIssueTime();          // "iat" claim
Date expirationTime = claimsSet.getExpirationTime(); // "exp" claim

// In ra thông tin
System.out.println("Username: " + username);
System.out.println("Role: " + role);
System.out.println("Issuer: " + issuer);
System.out.println("Issue Time: " + issueTime);
System.out.println("Expiration Time: " + expirationTime);
```

## Các phương thức kiểm tra trong JwtUtil

### 1. `decodeTokenPayload(String token)`
- Decode toàn bộ payload và trả về Map
- Log thông tin ra console
- Trả về null nếu token không hợp lệ

### 2. `getUsernameFromToken(String token)`
- Lấy username từ claim "sub"
- Trả về null nếu token không hợp lệ

### 3. `getRoleFromToken(String token)`
- Lấy role từ custom claim "role"
- Trả về null nếu token không hợp lệ

### 4. `getExpirationFromToken(String token)`
- Lấy thời gian hết hạn từ claim "exp"
- Trả về null nếu token không hợp lệ

### 5. `validateToken(String token)`
- Kiểm tra chữ ký token
- Kiểm tra thời gian hết hạn
- Kiểm tra blacklist
- Trả về true/false

## Ví dụ thực tế

### Token được tạo:
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsInJvbGUiOiJBRE1JTiIsImlzcyI6Im1pbmktc3VwZXJtYXJrZXQiLCJpYXQiOjE3MDMxMjM0NTY3ODksImV4cCI6MTcwMzIwOTg1Njc4OX0.signature
```

### Payload được decode:
```json
{
  "sub": "admin@example.com",
  "role": "ADMIN",
  "iss": "mini-supermarket",
  "iat": 1703123456789,
  "exp": 1703209856789
}
```

## Cấu hình JWT

- **Algorithm**: HS256 (HMAC SHA-256)
- **Secret Key**: `lj4rZPDvdCsTGInKfrNgFbKPq6xSg5svMzrKfDsTSpATgFuOM268cvRCRzWw2WLc`
- **Expiration**: 86400 giây (24 giờ)
- **Issuer**: "mini-supermarket"

## Debug và Troubleshooting

### 1. Token không hợp lệ
- Kiểm tra format token (3 phần được phân tách bằng dấu chấm)
- Kiểm tra chữ ký
- Kiểm tra thời gian hết hạn

### 2. Payload không đúng
- Kiểm tra secret key
- Kiểm tra algorithm
- Kiểm tra issuer

### 3. Claims thiếu
- Kiểm tra code tạo token
- Kiểm tra các claims được thêm vào

## Bảo mật

1. **Secret Key**: Đảm bảo đủ dài (ít nhất 256 bits cho HS256)
2. **Expiration**: Token có thời hạn 24 giờ
3. **Blacklist**: Token đã logout được thêm vào blacklist
4. **Validation**: Luôn validate token trước khi sử dụng
5. **HTTPS**: Sử dụng HTTPS trong production

## Tools hữu ích

1. **JWT.io**: https://jwt.io/
2. **JWT Debugger**: Browser extension
3. **Postman**: Test API endpoints
4. **cURL**: Command line testing
5. **Java Nimbus JOSE+JWT**: Library hiện tại 