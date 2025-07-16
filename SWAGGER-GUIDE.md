# Hướng dẫn sử dụng Swagger cho Mini Supermarket API

## 🌟 Tổng quan

Swagger (OpenAPI) là một công cụ mạnh mẽ để tạo documentation tương tác cho REST APIs. Dự án Mini Supermarket đã tích hợp Swagger để cung cấp giao diện thân thiện cho việc khám phá và test API.

## 🚀 Cách truy cập Swagger

### 1. Khởi chạy ứng dụng
```bash
# Sử dụng Docker
docker-compose up -d

# Hoặc chạy local
mvn spring-boot:run
```

### 2. Truy cập Swagger UI
Mở trình duyệt và truy cập:
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs JSON:** http://localhost:8080/api-docs

## 📱 Giao diện Swagger UI

### Thành phần chính:
1. **API Info** - Thông tin cơ bản về API
2. **Servers** - Danh sách servers (Development, Production)
3. **Tags** - Nhóm endpoints theo chức năng
4. **Endpoints** - Danh sách các API endpoints
5. **Models** - Schema của các entity

### Các tags chính:
- **Khách hàng** - API quản lý khách hàng
- **Sản phẩm** - API quản lý sản phẩm
- **Các tags khác** - Tương ứng với 25 controllers còn lại

## 🔧 Cách sử dụng Swagger UI

### 1. Khám phá API
- Click vào từng tag để xem các endpoints
- Mở rộng endpoint để xem chi tiết
- Xem request/response schema

### 2. Test API trực tiếp

#### Bước 1: Chọn endpoint
- Click vào endpoint muốn test
- Click nút **"Try it out"**

#### Bước 2: Nhập parameters
- **Path Parameters:** Nhập ID cần thiết
- **Request Body:** Nhập JSON data (nếu cần)
- **Query Parameters:** Nhập filter/search parameters

#### Bước 3: Thực hiện request
- Click nút **"Execute"**
- Xem kết quả trong **Response**

### 3. Ví dụ cụ thể

#### Test GET all khách hàng:
1. Mở tag **"Khách hàng"**
2. Click **GET /api/khachhang**
3. Click **"Try it out"**
4. Click **"Execute"**
5. Xem response JSON

#### Test POST tạo khách hàng mới:
1. Mở tag **"Khách hàng"**
2. Click **POST /api/khachhang**
3. Click **"Try it out"**
4. Nhập JSON trong Request body:
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
5. Click **"Execute"**
6. Xem response với khách hàng mới được tạo

## 📊 Các tính năng hữu ích

### 1. Response Examples
- Xem ví dụ response cho mỗi status code
- Hiểu cấu trúc JSON trả về

### 2. Schema Documentation
- Xem chi tiết các fields của entity
- Kiểm tra required fields và data types

### 3. Export OpenAPI Spec
- Download API specification ở định dạng JSON/YAML
- Sử dụng cho code generation hoặc external tools

### 4. Try it out
- Test API trực tiếp từ browser
- Không cần Postman hay công cụ khác
- Thay đổi server endpoint dễ dàng

## 🛠️ Customization

### Thay đổi cấu hình trong application.yaml:
```yaml
springdoc:
  api-docs:
    path: /api-docs                    # Đường dẫn API docs
  swagger-ui:
    path: /swagger-ui.html             # Đường dẫn Swagger UI
    enabled: true                      # Bật/tắt Swagger UI
    operations-sorter: alpha           # Sắp xếp operations
    tags-sorter: alpha                 # Sắp xếp tags
    try-it-out-enabled: true           # Bật/tắt "Try it out"
    doc-expansion: none                # Mở rộng documentation
    default-models-expand-depth: 1     # Độ sâu mở rộng models
```

### Thêm annotations cho controllers:
```java
@Tag(name = "Tên Tag", description = "Mô tả tag")
@Operation(summary = "Tóm tắt", description = "Mô tả chi tiết")
@ApiResponse(responseCode = "200", description = "Thành công")
@Parameter(description = "Mô tả parameter", required = true)
```

## 🔒 Security với Swagger

### Trong môi trường production:
1. **Vô hiệu hóa Swagger UI:**
```yaml
springdoc:
  swagger-ui:
    enabled: false
```

2. **Sử dụng profiles:**
```yaml
spring:
  profiles:
    active: production
---
spring:
  profiles: production
springdoc:
  swagger-ui:
    enabled: false
```

3. **Bảo vệ endpoint:**
- Thêm Spring Security
- Restrict access to /swagger-ui.html
- Chỉ cho phép internal network

## 📋 Checklist sử dụng Swagger

### Cho Developer:
- [ ] Thêm @Tag cho mỗi controller
- [ ] Viết @Operation cho mỗi endpoint
- [ ] Thêm @ApiResponse cho các response codes
- [ ] Mô tả @Parameter cho path/query parameters
- [ ] Test API thông qua Swagger UI

### Cho Tester:
- [ ] Sử dụng Swagger UI để test các endpoints
- [ ] Kiểm tra response format
- [ ] Validate error responses
- [ ] Test edge cases

### Cho Frontend Developer:
- [ ] Sử dụng Swagger để hiểu API contract
- [ ] Export OpenAPI spec để generate client code
- [ ] Kiểm tra request/response schemas
- [ ] Test integration với backend

## 🎯 Best Practices

### 1. Documentation:
- Viết mô tả rõ ràng cho API
- Sử dụng examples cho request/response
- Cập nhật documentation khi thay đổi API

### 2. Testing:
- Test tất cả endpoints qua Swagger UI
- Verify response formats
- Check error handling

### 3. Maintenance:
- Thường xuyên review API documentation
- Cập nhật examples và descriptions
- Đảm bảo consistency across endpoints

## 🚨 Troubleshooting

### 1. Swagger UI không load:
- Kiểm tra ứng dụng đã chạy: http://localhost:8080
- Verify endpoint: http://localhost:8080/api-docs
- Check browser console for errors

### 2. API không hiển thị:
- Kiểm tra package-to-scan trong application.yaml
- Verify controller có @RestController annotation
- Check paths-to-match configuration

### 3. Annotations không hoạt động:
- Kiểm tra import statements
- Verify springdoc-openapi dependency trong pom.xml
- Restart ứng dụng sau khi thay đổi

## 📚 Tài liệu tham khảo

- [OpenAPI Specification](https://swagger.io/specification/)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)
- [OpenAPI Generator](https://openapi-generator.tech/)

---

**Swagger URL:** http://localhost:8080/swagger-ui.html  
**API Docs:** http://localhost:8080/api-docs  
**Cập nhật:** 2024 