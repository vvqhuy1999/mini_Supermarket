# JWT Payload Analysis

## Cấu trúc JWT Payload hiện tại

Dựa trên code trong `JwtUtil.java`, JWT payload có cấu trúc như sau:

### 1. Các Claims chuẩn (Standard Claims)

```json
{
  "sub": "username",           // Subject - tên đăng nhập
  "iss": "mini-supermarket",   // Issuer - người phát hành
  "iat": 1234567890,          // Issued At - thời gian phát hành
  "exp": 1234567890           // Expiration - thời gian hết hạn
}
```

### 2. Custom Claims

```json
{
  "role": "ADMIN"             // Vai trò người dùng
}
```

### 3. Cấu hình JWT

- **Algorithm**: HS256 (HMAC SHA-256)
- **Secret Key**: `lj4rZPDvdCsTGInKfrNgFbKPq6xSg5svMzrKfDsTSpATgFuOM268cvRCRzWw2WLc`
- **Expiration**: 86400 giây (24 giờ)

## Cách kiểm tra JWT Payload

### 1. Sử dụng API endpoints

#### Decode token có sẵn:
```bash
POST /api/auth/decode-token
Content-Type: application/json

{
  "token": "your-jwt-token-here"
}
```

#### Test tạo token mới:
```bash
POST /api/auth/test-token
Content-Type: application/json

{
  "email": "admin@example.com",
  "matKhau": "password123"
}
```

### 2. Sử dụng JWT Debugger online

1. Truy cập: https://jwt.io/
2. Paste JWT token vào
3. Xem payload trong phần "Payload"

### 3. Sử dụng code Java

```java
// Decode token
SignedJWT signedJWT = SignedJWT.parse(token);
JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

// Lấy thông tin
String username = claimsSet.getSubject();
String role = (String) claimsSet.getClaim("role");
String issuer = claimsSet.getIssuer();
Date issueTime = claimsSet.getIssueTime();
Date expirationTime = claimsSet.getExpirationTime();
```

## Ví dụ Payload thực tế

```json
{
  "sub": "admin@example.com",
  "role": "ADMIN",
  "iss": "mini-supermarket",
  "iat": 1703123456789,
  "exp": 1703209856789
}
```

## Các phương thức kiểm tra trong code

### 1. `decodeTokenPayload(String token)`
- Decode và hiển thị toàn bộ thông tin payload
- Log ra console để debug
- Trả về Map chứa thông tin payload

### 2. `getUsernameFromToken(String token)`
- Lấy username từ claim "sub"

### 3. `getRoleFromToken(String token)`
- Lấy role từ custom claim "role"

### 4. `getExpirationFromToken(String token)`
- Lấy thời gian hết hạn từ claim "exp"

### 5. `validateToken(String token)`
- Kiểm tra tính hợp lệ của token
- Kiểm tra chữ ký
- Kiểm tra thời gian hết hạn
- Kiểm tra blacklist

## Lưu ý bảo mật

1. **Secret Key**: Đảm bảo secret key đủ dài (ít nhất 256 bits cho HS256)
2. **Expiration**: Token có thời hạn 24 giờ
3. **Blacklist**: Token đã logout sẽ được thêm vào blacklist
4. **Validation**: Luôn validate token trước khi sử dụng

## Debug JWT Token

Để debug JWT token, có thể sử dụng:

1. **Online JWT Debugger**: https://jwt.io/
2. **API endpoints** đã tạo trong AuthenticationController
3. **Logs**: JwtUtil sẽ log thông tin payload khi decode
4. **Browser DevTools**: Xem token trong Network tab 