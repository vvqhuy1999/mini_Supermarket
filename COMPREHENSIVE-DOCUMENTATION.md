# 📚 Mini Supermarket - Tài liệu Tổng hợp

> Tài liệu này gộp tất cả các hướng dẫn và tài liệu của dự án Mini Supermarket thành một file duy nhất để dễ dàng tra cứu và quản lý.

---

## 📖 Mục lục

1. [Tổng quan Dự án](#1-tổng-quan-dự-án)
2. [Cài đặt và Chạy Dự án](#2-cài-đặt-và-chạy-dự-án)
3. [API Documentation](#3-api-documentation)
4. [OAuth2 Authentication](#4-oauth2-authentication)
5. [JWT và Security](#5-jwt-và-security)
6. [Docker Setup](#6-docker-setup)
7. [Swagger Documentation](#7-swagger-documentation)
8. [Troubleshooting](#8-troubleshooting)
9. [Environment Setup](#9-environment-setup)

---

## 1. Tổng quan Dự án

### 📋 Giới thiệu

**Mini Supermarket Management System** là hệ thống quản lý siêu thị mini được xây dựng bằng Spring Boot, cung cấp các API REST để quản lý các hoạt động của siêu thị.

### 🚀 Công nghệ sử dụng

- **Backend:** Java 21, Spring Boot 3.5.3
- **Database:** MySQL 8.0
- **Build Tool:** Maven
- **Container:** Docker & Docker Compose
- **ORM:** Spring Data JPA
- **Documentation:** OpenAPI/Swagger
- **Authentication:** OAuth2 (Google, Facebook), JWT
- **Security:** Spring Security, BCrypt

### 📋 Tính năng chính

#### Quản lý cơ bản
- ✅ Quản lý sản phẩm và loại sản phẩm
- ✅ Quản lý khách hàng
- ✅ Quản lý nhân viên và ca làm việc
- ✅ Quản lý nhà cung cấp
- ✅ Quản lý kho hàng và tồn kho

#### Bán hàng
- ✅ Giỏ hàng và chi tiết giỏ hàng
- ✅ Hóa đơn và thanh toán
- ✅ Khuyến mãi cho khách hàng và sản phẩm
- ✅ Phương thức thanh toán

#### Kho hàng
- ✅ Phiếu nhập hàng
- ✅ Phiếu xuất kho
- ✅ Quản lý giá sản phẩm
- ✅ Hình ảnh sản phẩm

#### Báo cáo
- ✅ Thống kê báo cáo
- ✅ Lịch làm việc nhân viên

### 🏗️ Cấu trúc dự án

```
Mini_Supermarket/
├── src/main/java/com/example/mini_supermarket/
│   ├── entity/          # Các entity JPA (27 entities)
│   ├── dao/             # Repository interfaces
│   ├── service/         # Service interfaces
│   ├── impl/            # Service implementations
│   ├── config/          # Configuration classes
│   ├── util/            # Utility classes
│   └── rest/controller/ # REST Controllers (27 controllers)
├── src/main/resources/
│   ├── application.yaml           # Cấu hình chính
│   ├── application-docker.yaml    # Cấu hình Docker
│   └── application-local.yaml     # Cấu hình local
├── docker/
│   └── init.sql        # Database schema và sample data
├── Dockerfile          # Docker configuration
├── docker-compose.yml  # Docker Compose setup
└── Documentation files (.md)
```

---

## 2. Cài đặt và Chạy Dự án

### Phương pháp 1: Sử dụng Docker (Khuyến nghị)

#### Yêu cầu
- Docker Desktop
- Docker Compose

#### Các bước thực hiện

1. **Clone dự án**
```bash
git clone <repository-url>
cd Mini_Supermarket
```

2. **Khởi chạy với Docker Compose**
```bash
docker-compose up -d
```

3. **Kiểm tra trạng thái**
```bash
docker-compose ps
```

4. **Truy cập ứng dụng**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- MySQL: localhost:3306 (Username: root, Password: rootpassword)

5. **Dừng ứng dụng**
```bash
docker-compose down
```

### Phương pháp 2: Chạy local (MySQL riêng)

#### Yêu cầu
- Java 21 JDK
- Maven 3.6+
- MySQL 8.0

#### Các bước thực hiện

1. **Cài đặt MySQL và tạo database**
```sql
CREATE DATABASE mini_supermarket;
```

2. **Cấu hình database trong `application-local.yaml`**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mini_supermarket
    username: your_username
    password: your_password
```

3. **Chạy ứng dụng**
```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Phương pháp 3: Sử dụng Maven Wrapper

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

---

## 3. API Documentation

### Tổng quan API

- **Tổng số controllers:** 28
- **Tổng số endpoints:** 140+
- **Base URL:** http://localhost:8080

### Endpoints chính

#### 1. Quản lý Người dùng & Nhân viên
- **POST** `/api/nguoidung` - Tạo người dùng mới
- **GET** `/api/nguoidung` - Lấy tất cả người dùng
- **GET** `/api/nguoidung/{id}` - Lấy người dùng theo ID
- **PUT** `/api/nguoidung/{id}` - Cập nhật người dùng
- **DELETE** `/api/nguoidung/{id}` - Xóa người dùng

- **POST** `/api/nhanvien` - Tạo nhân viên mới
- **GET** `/api/nhanvien` - Lấy tất cả nhân viên
- **GET** `/api/nhanvien/{id}` - Lấy nhân viên theo ID
- **PUT** `/api/nhanvien/{id}` - Cập nhật nhân viên
- **DELETE** `/api/nhanvien/{id}` - Xóa nhân viên

#### 2. Quản lý Khách hàng
- **POST** `/api/khachhang` - Tạo khách hàng mới
- **GET** `/api/khachhang` - Lấy tất cả khách hàng
- **GET** `/api/khachhang/{id}` - Lấy khách hàng theo ID
- **PUT** `/api/khachhang/{id}` - Cập nhật khách hàng
- **DELETE** `/api/khachhang/{id}` - Xóa khách hàng

#### 3. Quản lý Sản phẩm
- **POST** `/api/sanpham` - Tạo sản phẩm mới
- **GET** `/api/sanpham` - Lấy tất cả sản phẩm
- **GET** `/api/sanpham/{id}` - Lấy sản phẩm theo ID
- **PUT** `/api/sanpham/{id}` - Cập nhật sản phẩm
- **DELETE** `/api/sanpham/{id}` - Xóa sản phẩm

- **POST** `/api/loaisanpham` - Tạo loại sản phẩm mới
- **GET** `/api/loaisanpham` - Lấy tất cả loại sản phẩm

#### 4. Quản lý Hóa đơn
- **POST** `/api/hoadon` - Tạo hóa đơn mới
- **GET** `/api/hoadon` - Lấy tất cả hóa đơn
- **GET** `/api/hoadon/{id}` - Lấy hóa đơn theo ID

#### 5. Authentication & OAuth2
- **POST** `/api/auth/log-in` - Đăng nhập thường
- **POST** `/api/auth/register` - Đăng ký
- **GET** `/oauth2/authorization/google` - Đăng nhập Google
- **GET** `/oauth2/authorization/facebook` - Đăng nhập Facebook

### Response Format

#### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "result": {
    // data object
  }
}
```

#### Error Response
```json
{
  "success": false,
  "message": "Error message",
  "error": "Error details"
}
```

### Ví dụ Requests

#### Tạo sản phẩm mới:
```bash
POST http://localhost:8080/api/sanpham
Content-Type: application/json

{
  "maSP": "SP00006",
  "tenSP": "Sản phẩm mới",
  "giaBan": 50000,
  "moTa": "Mô tả sản phẩm"
}
```

#### Cập nhật sản phẩm:
```bash
PUT http://localhost:8080/api/sanpham/SP00001
Content-Type: application/json

{
  "tenSP": "Tên sản phẩm đã cập nhật",
  "giaBan": 60000
}
```

---

## 4. OAuth2 Authentication

### 🔧 Cấu hình OAuth2

#### Google OAuth2 Setup:
1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo project mới hoặc chọn project hiện có
3. Enable Google+ API
4. Tạo OAuth2 credentials
5. Thêm redirect URI: `http://localhost:8080/login/oauth2/code/google`

#### Facebook OAuth2 Setup:
1. Truy cập [Facebook Developers](https://developers.facebook.com/)
2. Tạo app mới
3. Thêm Facebook Login product
4. Cấu hình redirect URI: `http://localhost:8080/login/oauth2/code/facebook`

#### Application Configuration (application.yaml):
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID:your-google-client-id}
            client-secret: ${GOOGLE_CLIENT_SECRET:your-google-client-secret}
            scope:
              - email
              - profile
            redirect-uri: "http://localhost:8080/login/oauth2/code/google"
          facebook:
            client-id: ${FACEBOOK_CLIENT_ID:your-facebook-client-id}
            client-secret: ${FACEBOOK_CLIENT_SECRET:your-facebook-client-secret}
            scope:
              - email
              - public_profile
            redirect-uri: "http://localhost:8080/login/oauth2/code/facebook"
        provider:
          google:
            authorization-uri: https://accounts.google.com/o/oauth2/auth?prompt=consent

# OAuth2 Frontend Integration
oauth2:
  frontend:
    base-url: ${FRONTEND_URL:http://localhost:3000}
    success-path: ${OAUTH2_SUCCESS_PATH:/oauth2/success}
    failure-path: ${OAUTH2_FAILURE_PATH:/login?error=oauth2_failed}
```

### 🔗 OAuth2 Flow

#### Complete Flow:
```
1. Frontend: User clicks "Login with Google"
   → Redirect to: http://localhost:8080/oauth2/authorization/google

2. Google OAuth2 authentication

3. Google redirects to backend callback:
   → http://localhost:8080/login/oauth2/code/google

4. Backend processes login & creates JWT token

5. Backend redirects to frontend with token:
   → http://localhost:3000/oauth2/success?success=true&token=JWT_TOKEN&email=user@gmail.com&role=KHACH_HANG&userId=NDA1B2C3D4

6. Frontend extracts token từ URL parameters
7. Frontend stores token và redirect to dashboard
```

### 🎯 Frontend Integration

#### Google Login Button:
```html
<a href="http://localhost:8080/oauth2/authorization/google">
    Đăng nhập với Google
</a>
```

#### React/Vue/Angular - OAuth2 Success Page:
```javascript
// /oauth2/success page component
import { useEffect } from 'react';
import { useRouter } from 'next/router';

export default function OAuth2Success() {
    const router = useRouter();
    
    useEffect(() => {
        const handleOAuth2Success = () => {
            // Extract parameters from URL
            const urlParams = new URLSearchParams(window.location.search);
            const success = urlParams.get('success');
            const token = urlParams.get('token');
            const email = urlParams.get('email');
            const role = urlParams.get('role');
            const userId = urlParams.get('userId');
            
            if (success === 'true' && token) {
                // Store JWT token
                localStorage.setItem('jwt_token', token);
                localStorage.setItem('user_email', email);
                localStorage.setItem('user_role', role);
                localStorage.setItem('user_id', userId);
                
                console.log('✅ OAuth2 Login successful!');
                
                // Redirect to dashboard
                router.push('/dashboard');
            } else {
                console.error('❌ OAuth2 Login failed');
                router.push('/login?error=oauth2_failed');
            }
        };
        
        handleOAuth2Success();
    }, [router]);
    
    return (
        <div className="oauth2-processing">
            <h2>🔄 Đang xử lý đăng nhập...</h2>
            <p>Vui lòng chờ trong giây lát...</p>
        </div>
    );
}
```

#### API Calls với JWT Token:
```javascript
// Utility function để gọi API với JWT token
async function apiCall(endpoint, options = {}) {
    const token = localStorage.getItem('jwt_token');
    
    if (!token) {
        throw new Error('No JWT token found');
    }
    
    const defaultOptions = {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
            ...options.headers
        }
    };
    
    const response = await fetch(`http://localhost:8080${endpoint}`, {
        ...options,
        ...defaultOptions,
        headers: { ...defaultOptions.headers, ...options.headers }
    });
    
    if (!response.ok) {
        if (response.status === 401) {
            localStorage.removeItem('jwt_token');
            window.location.href = '/login';
            return;
        }
        throw new Error(`API call failed: ${response.statusText}`);
    }
    
    return response.json();
}
```

### 🔧 Google Cloud Console Configuration

#### 🔧 Cấu hình OAuth Consent Screen:

**A. Truy cập OAuth Consent Screen:**
```
1. Trong Google Cloud Console
2. Menu bên trái → APIs & Services → OAuth consent screen
3. Click "Configure Consent Screen"
```

**B. Cấu hình App Information:**
```
User Type: External
App name: Mini Supermarket
User support email: [your-email@gmail.com]
Developer contact information: [your-email@gmail.com]
```

**C. Cấu hình Test Users:**
```
1. Scroll xuống phần "Test users"
2. Click "Add users"
3. Thêm các email test cần thiết
4. Click "Add"
```

### 🧪 Testing OAuth2

#### Test với API:
```bash
# Test cấu hình OAuth2
GET /api/oauth2/test-config

# Success callback
GET /api/oauth2/success

# Email duplicate check
GET /api/oauth2/check-email?email=test@gmail.com
```

#### Test Cases:
1. **Đăng nhập Google lần đầu**
2. **Đăng nhập Google lần thứ 2**
3. **Đăng nhập Facebook**
4. **Test với email không hợp lệ**

---

## 5. JWT và Security

### 🔒 JWT Configuration

#### Cấu trúc JWT Payload:
```json
{
  "sub": "username",           // Subject - tên đăng nhập
  "iss": "mini-supermarket",   // Issuer - người phát hành
  "iat": 1234567890,          // Issued At - thời gian phát hành
  "exp": 1234567890,          // Expiration - thời gian hết hạn
  "role": "ADMIN"             // Vai trò người dùng
}
```

#### JWT Configuration:
```yaml
jwt:
  secret: ${JWT_SECRET:lj4rZPDvdCsTGInKfrNgFbKPq6xSg5svMzrKfDsTSpATgFuOM268cvRCRzWw2WLc}
  expiration: ${JWT_EXPIRATION:86400}
```

### 🔐 Security Features

#### Password Encryption:
```java
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

#### CORS Configuration:
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Arrays.asList(
        "http://localhost:3000",    // Frontend
        "http://localhost:8080",    // Backend
        "http://127.0.0.1:3000",
        "http://127.0.0.1:8080"
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### 🛠️ JWT Utility Methods

#### Generate Token:
```java
public String generateToken(String username, String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);
    return createToken(claims, username);
}
```

#### Validate Token:
```java
public boolean validateToken(String token) {
    try {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(secret.getBytes(StandardCharsets.UTF_8));
        
        boolean signatureValid = signedJWT.verify(verifier);
        boolean notExpired = new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime());
        boolean notInBlacklist = !isTokenBlacklisted(token);
        
        return signatureValid && notExpired && notInBlacklist;
    } catch (Exception e) {
        return false;
    }
}
```

### 🧪 JWT Debug Endpoints

#### Debug Token:
```bash
POST /api/auth/debug-token
Content-Type: application/json

{
  "token": "YOUR_JWT_TOKEN_HERE"
}
```

#### Validate Token:
```bash
POST /api/auth/validate-token
Content-Type: application/json

{
  "token": "YOUR_JWT_TOKEN_HERE"
}
```

---

## 6. Docker Setup

### 🐳 Docker Configuration

#### Dockerfile:
```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/mini-supermarket-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### docker-compose.yml:
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: mini_supermarket_mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: mini_supermarket
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./docker/init.sql:/docker-entrypoint-initdb.d/init.sql

  app:
    build: .
    container_name: mini_supermarket_app
    depends_on:
      - mysql
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      DB_HOST: mysql
      DB_PORT: 3306
      DB_NAME: mini_supermarket
      DB_USERNAME: root
      DB_PASSWORD: rootpassword

volumes:
  mysql_data:
```

### 🚀 Docker Commands

#### Build và chạy:
```bash
docker-compose up --build
```

#### Chạy chỉ database:
```bash
docker-compose up mysql
```

#### Xem logs:
```bash
# Xem logs của tất cả services
docker-compose logs

# Xem logs của service cụ thể
docker-compose logs app
docker-compose logs mysql
```

#### Dừng và xóa:
```bash
# Dừng ứng dụng
docker-compose down

# Dừng và xóa volumes (xóa dữ liệu database)
docker-compose down -v
```

### 🔧 Docker Troubleshooting

#### Port đã được sử dụng:
```bash
# Kiểm tra port đang được sử dụng
docker ps
netstat -tulpn | grep 8080
```

#### Build lỗi:
```bash
# Xóa cache và build lại
docker-compose down
docker system prune -a
docker-compose up --build
```

---

## 7. Swagger Documentation

### 🌟 Swagger Overview

Swagger (OpenAPI) cung cấp giao diện tương tác để khám phá và test API.

### 🚀 Truy cập Swagger

#### URLs:
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs JSON:** http://localhost:8080/api-docs

### 📱 Sử dụng Swagger UI

#### Test API trực tiếp:

1. **Chọn endpoint**
   - Click vào endpoint muốn test
   - Click nút **"Try it out"**

2. **Nhập parameters**
   - **Path Parameters:** Nhập ID cần thiết
   - **Request Body:** Nhập JSON data
   - **Query Parameters:** Nhập filter/search parameters

3. **Thực hiện request**
   - Click nút **"Execute"**
   - Xem kết quả trong **Response**

#### Ví dụ Test POST tạo khách hàng:
```json
{
  "maKH": "KH001",
  "tenKH": "Nguyen Van A",
  "email": "nva@email.com",
  "sdt": "0123456789",
  "diaChi": "Ha Noi",
  "isDeleted": false
}
```

### 🛠️ Swagger Customization

#### Configuration trong application.yaml:
```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operations-sorter: alpha
    tags-sorter: alpha
    try-it-out-enabled: true
    doc-expansion: none
    default-models-expand-depth: 1
```

#### Annotations cho Controllers:
```java
@Tag(name = "Khách hàng", description = "API quản lý khách hàng")
@RestController
@RequestMapping("/api/khachhang")
public class KhachHangController {
    
    @Operation(summary = "Lấy tất cả khách hàng", description = "Trả về danh sách tất cả khách hàng")
    @ApiResponse(responseCode = "200", description = "Thành công")
    @GetMapping
    public ResponseEntity<ApiResponse<List<KhachHang>>> getAllKhachHang() {
        // implementation
    }
}
```

### 🔒 Security trong Production

#### Vô hiệu hóa Swagger:
```yaml
spring:
  profiles: production
springdoc:
  swagger-ui:
    enabled: false
```

---

## 8. Troubleshooting

### 🐛 Common Issues

#### 1. OAuth2 Errors

**"Access blocked: authorisation error"**
- **Nguyên nhân:** OAuth Consent Screen chưa được cấu hình đúng
- **Giải pháp:** Cấu hình Google Cloud Console theo hướng dẫn
- **Check:** Test users, scopes, redirect URIs

**"OAuth 2 parameters can only have a single value: prompt"**
- **Nguyên nhân:** Duplicate prompt parameter
- **Giải pháp:** Chỉ thêm prompt ở một nơi (application.yaml hoặc CustomAuthorizationRequestResolver)

**"Unable to convert claim 'iss' of type 'class java.lang.String' to URL"**
- **Nguyên nhân:** ID token validation error với openid scope
- **Giải pháp:** Loại bỏ openid scope hoặc custom ID token validation

#### 2. JWT Errors

**"Invalid Signature"**
- **Nguyên nhân:** Secret key không đủ mạnh hoặc không nhất quán
- **Giải pháp:** 
  - Đảm bảo secret key >= 32 bytes
  - Sử dụng StandardCharsets.UTF_8
  - Cùng secret key cho generate và validate

**"Token expired"**
- **Nguyên nhân:** Token đã hết hạn
- **Giải pháp:** Đăng nhập lại để lấy token mới

#### 3. Database Errors

**"Access denied for user"**
- **Nguyên nhân:** Cấu hình database sai
- **Giải pháp:** Kiểm tra username/password trong application.yaml

**"Unknown database"**
- **Nguyên nhân:** Database chưa được tạo
- **Giải pháp:** Tạo database trước khi chạy app

#### 4. Port Errors

**"Port 8080 already in use"**
```bash
# Kiểm tra process đang sử dụng port
netstat -tulpn | grep 8080

# Hoặc đổi port trong application.yaml
server:
  port: 8081
```

#### 5. Maven Errors

**"Maven not found"**
```bash
# Sử dụng Maven wrapper
./mvnw.cmd spring-boot:run  # Windows
./mvnw spring-boot:run      # Linux/Mac
```

**"Build failed"**
```bash
# Clean và build lại
mvn clean install -DskipTests
```

### 🔧 Debug Commands

#### Check application status:
```bash
curl http://localhost:8080/actuator/health
```

#### Test OAuth2 config:
```bash
curl http://localhost:8080/api/oauth2/test-config
```

#### Debug JWT token:
```bash
curl -X POST http://localhost:8080/api/auth/debug-token \
  -H "Content-Type: application/json" \
  -d '{"token": "YOUR_TOKEN_HERE"}'
```

### 📊 Monitoring

#### Application Logs:
```bash
# Spring Boot console logs
tail -f logs/application.log

# Docker logs
docker-compose logs app
```

#### Database Logs:
```bash
# MySQL logs
docker-compose logs mysql
```

---

## 9. Environment Setup

### 🔧 Environment Variables

#### Tạo file .env:
```bash
cp env.example .env
```

#### Database Configuration:
```env
DB_URL=jdbc:mysql://localhost:3306/QuanLySieuThi?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=admin
```

#### JWT Configuration:
```env
JWT_SECRET=your-super-secret-jwt-key-here-make-it-long-and-random
JWT_EXPIRATION=86400
```

#### Google OAuth2 Configuration:
```env
GOOGLE_CLIENT_ID=your-google-client-id-here
GOOGLE_CLIENT_SECRET=your-google-client-secret-here
```

#### Facebook OAuth2 Configuration:
```env
FACEBOOK_CLIENT_ID=your-facebook-client-id-here
FACEBOOK_CLIENT_SECRET=your-facebook-client-secret-here
```

#### Frontend Configuration:
```env
FRONTEND_URL=http://localhost:3000
OAUTH2_SUCCESS_PATH=/oauth2/success
OAUTH2_FAILURE_PATH=/login?error=oauth2_failed
```

### 🔒 Security Notes

1. **KHÔNG BAO GIỜ** commit file `.env` lên git
2. File `.env` đã được thêm vào `.gitignore`
3. Chỉ commit file `env.example` để làm mẫu
4. JWT_SECRET phải đủ dài và phức tạp (ít nhất 32 ký tự)
5. Thay đổi tất cả các giá trị mặc định trong file `.env`

### 📋 Profiles Configuration

#### Available Profiles:
- **default**: Cấu hình mặc định
- **docker**: Cấu hình cho Docker environment
- **local**: Cấu hình cho development local

#### Environment Variables:
| Biến | Mô tả | Giá trị mặc định |
|------|--------|------------------|
| `DB_HOST` | MySQL host | localhost |
| `DB_PORT` | MySQL port | 3306 |
| `DB_NAME` | Database name | mini_supermarket |
| `DB_USERNAME` | Database username | root |
| `DB_PASSWORD` | Database password | rootpassword |
| `FRONTEND_URL` | Frontend URL | http://localhost:3000 |
| `JWT_SECRET` | JWT secret key | (auto-generated) |

---

## 📞 Support và Resources

### 🔗 Useful Links

- **Application:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/api-docs
- **Google Cloud Console:** https://console.cloud.google.com/
- **Facebook Developers:** https://developers.facebook.com/
- **JWT Debugger:** https://jwt.io/

### 📚 Documentation References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Docker Documentation](https://docs.docker.com/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [OpenAPI Specification](https://swagger.io/specification/)

### 🛠️ Tools và Testing

- **PowerShell Test Script:** `.\oauth2-complete-test.ps1`
- **Test HTML:** `http://localhost:8080/oauth2-test.html`
- **cURL Commands:** Xem trong từng section
- **Postman Collection:** Import từ Swagger

### 🎯 Best Practices

#### Development:
- Sử dụng profiles để phân biệt environments
- Test API với Swagger UI trước khi integrate
- Monitor logs để debug issues
- Sử dụng proper HTTP status codes

#### Security:
- Luôn validate JWT tokens
- Sử dụng HTTPS trong production
- Rotate OAuth2 secrets định kỳ
- Implement proper CORS policies

#### Database:
- Backup database trước khi deploy
- Sử dụng connection pooling
- Monitor database performance
- Implement proper indexing

---

## 🎉 Kết luận

**Mini Supermarket Management System** đã được triển khai thành công với:

### ✅ **Features đã hoàn thành:**
- ✅ **Complete CRUD APIs** cho 28 entities
- ✅ **OAuth2 Authentication** với Google và Facebook
- ✅ **JWT Security** với proper validation
- ✅ **Docker containerization** cho easy deployment
- ✅ **Swagger documentation** cho API testing
- ✅ **Frontend integration** support
- ✅ **Comprehensive error handling**
- ✅ **Security best practices**

### 🚀 **Ready for:**
- **Frontend integration** với React/Vue/Angular
- **Production deployment** với Docker
- **API consumption** từ mobile apps
- **Third-party integrations**
- **Scalable architecture**

### 📖 **Documentation coverage:**
- **Complete API documentation**
- **OAuth2 setup guides**
- **JWT implementation details**
- **Docker deployment instructions**
- **Troubleshooting guides**
- **Environment setup procedures**

**Hệ thống đã sẵn sàng để sử dụng trong production hoặc tiếp tục phát triển!** 🚀

---

**Phiên bản:** 1.0.0  
**Ngày cập nhật:** 2025  
**Tác giả:** Mini Supermarket Team

**Happy coding!** 👨‍💻👩‍💻
