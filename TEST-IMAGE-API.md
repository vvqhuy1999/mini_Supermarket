# 🖼️ Test API Hình ảnh - Mini Supermarket

## 🚀 Cách sử dụng

### 1. Khởi động ứng dụng
```bash
mvn spring-boot:run
```

### 2. Chạy script test

#### Windows (PowerShell):
```powershell
.\test-image-api.ps1
```

#### Linux/Mac (Bash):
```bash
chmod +x test-image-api.sh
./test-image-api.sh
```

### 3. Test thủ công bằng curl

#### Lấy tất cả hình ảnh:
```bash
curl -X GET "http://localhost:8080/api/hinhanh"
```

#### Lấy hình ảnh theo ID:
```bash
curl -X GET "http://localhost:8080/api/hinhanh/1"
```

#### Lấy ảnh sản phẩm theo mã:
```bash
curl -X GET "http://localhost:8080/api/upload/product-images/SP001"
```

#### Upload ảnh sản phẩm:
```bash
curl -X POST "http://localhost:8080/api/upload/product-image" \
  -F "file=@product.jpg" \
  -F "maSP=SP001" \
  -F "moTa=Ảnh chính sản phẩm" \
  -F "laChinh=true" \
  -F "thuTuHienThi=0"
```

#### Upload nhiều ảnh:
```bash
curl -X POST "http://localhost:8080/api/upload/product-images" \
  -F "files=@image1.jpg" \
  -F "files=@image2.jpg" \
  -F "maSP=SP001"
```

#### Cập nhật thông tin ảnh:
```bash
curl -X PUT "http://localhost:8080/api/upload/product-image/1" \
  -H "Content-Type: application/json" \
  -d '{
    "moTa": "Ảnh sản phẩm đã cập nhật",
    "laChinh": true,
    "thuTuHienThi": 1
  }'
```

#### Xóa ảnh:
```bash
curl -X DELETE "http://localhost:8080/api/upload/product-image/1"
```

## 🔗 Các API Endpoints

### 📸 Hình ảnh cơ bản (HinhAnhRestController)
- `GET /api/hinhanh` - Lấy tất cả hình ảnh
- `GET /api/hinhanh/{id}` - Lấy hình ảnh theo ID
- `POST /api/hinhanh` - Tạo hình ảnh mới
- `PUT /api/hinhanh/{id}` - Cập nhật hình ảnh
- `DELETE /api/hinhanh/{id}` - Xóa hình ảnh

### 📤 Upload ảnh sản phẩm (ImageUploadController)
- `POST /api/upload/product-image` - Upload ảnh sản phẩm đơn lẻ
- `POST /api/upload/product-images` - Upload nhiều ảnh sản phẩm
- `GET /api/upload/product-images/{maSP}` - Lấy ảnh sản phẩm theo mã
- `PUT /api/upload/product-image/{maHinh}` - Cập nhật thông tin ảnh
- `DELETE /api/upload/product-image/{maHinh}` - Xóa ảnh sản phẩm

## 📋 Tham số Upload

### POST /api/upload/product-image
- `file` (required): File ảnh (MultipartFile)
- `maSP` (required): Mã sản phẩm
- `moTa` (optional): Mô tả ảnh
- `laChinh` (optional): Ảnh chính hay không (default: false)
- `thuTuHienThi` (optional): Thứ tự hiển thị (default: 0)

### POST /api/upload/product-images
- `files` (required): Mảng file ảnh (MultipartFile[])
- `maSP` (required): Mã sản phẩm

## ✅ Validation

- **Định dạng file**: jpg, jpeg, png, gif, webp, jfif
- **Kích thước tối đa**: 10MB
- **Tên file**: Tự động tạo unique với format: `{maSP}_{timestamp}_{uuid}.{extension}`

## 🗂️ Cấu trúc thư mục

```
mini_Supermarket/
├── uploads/
│   └── images/
│       └── products/          # Ảnh sản phẩm
├── static/
│   └── images/
│       └── default/           # Ảnh mặc định
└── src/main/java/.../
    └── rest/controller/
        ├── HinhAnhRestController.java      # API hình ảnh cơ bản
        └── ImageUploadController.java      # API upload ảnh sản phẩm
```

## 🔧 Cấu hình

### application.yaml
```yaml
app:
  images:
    upload-dir: ./uploads/images
    static-dir: ./static/images
    allowed-extensions: jpg,jpeg,png,gif,webp,jfif
    max-size: 10485760  # 10MB

spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
      enabled: true
```

## 🧪 Test với Swagger UI

1. Mở trình duyệt: `http://localhost:8080/swagger-ui.html`
2. Tìm section "Upload Ảnh" hoặc "Hình ảnh"
3. Click "Try it out" để test từng API
4. Nhập tham số và click "Execute"

## 🚨 Xử lý lỗi

### Lỗi thường gặp:
- **400 Bad Request**: File không hợp lệ, thiếu tham số
- **404 Not Found**: Không tìm thấy sản phẩm hoặc ảnh
- **500 Internal Server Error**: Lỗi server

### Debug:
- Kiểm tra logs Spring Boot
- Kiểm tra thư mục uploads có tồn tại không
- Kiểm tra quyền ghi file

## 📝 Ghi chú

- Tất cả API đều được thêm vào PUBLIC_ENDPOINTS (không cần authentication)
- Ảnh được lưu trong thư mục `uploads/images/products/`
- URL ảnh có format: `/images/products/{filename}`
- Sử dụng soft delete (isDeleted = true) thay vì xóa hoàn toàn
