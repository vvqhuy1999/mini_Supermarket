# Mini Supermarket Management System

Hệ thống quản lý siêu thị mini được xây dựng bằng Spring Boot, cung cấp các API REST để quản lý các hoạt động của siêu thị.

## 🚀 Công nghệ sử dụng

- **Backend:** Java 21, Spring Boot 3.5.3
- **Database:** MySQL 8.0
- **Build Tool:** Maven
- **Container:** Docker & Docker Compose
- **ORM:** Spring Data JPA
- **Documentation:** OpenAPI/Swagger

## 📋 Tính năng chính

### Quản lý cơ bản
- ✅ Quản lý sản phẩm và loại sản phẩm
- ✅ Quản lý khách hàng
- ✅ Quản lý nhân viên và ca làm việc
- ✅ Quản lý nhà cung cấp
- ✅ Quản lý kho hàng và tồn kho

### Bán hàng
- ✅ Giỏ hàng và chi tiết giỏ hàng
- ✅ Hóa đơn và thanh toán
- ✅ Khuyến mãi cho khách hàng và sản phẩm
- ✅ Phương thức thanh toán

### Kho hàng
- ✅ Phiếu nhập hàng
- ✅ Phiếu xuất kho
- ✅ Quản lý giá sản phẩm
- ✅ Hình ảnh sản phẩm

### Báo cáo
- ✅ Thống kê báo cáo
- ✅ Lịch làm việc nhân viên

## 🏗️ Cấu trúc dự án

```
Mini_Supermarket/
├── src/main/java/com/example/mini_supermarket/
│   ├── entity/          # Các entity JPA (27 entities)
│   ├── dao/             # Repository interfaces
│   ├── service/         # Service interfaces
│   ├── impl/            # Service implementations
│   └── rest/controller/ # REST Controllers (27 controllers)
├── src/main/resources/
│   ├── application.yaml           # Cấu hình chính
│   ├── application-docker.yaml    # Cấu hình Docker
│   └── application-local.yaml     # Cấu hình local
├── docker/
│   └── init.sql        # Database schema và sample data
├── Dockerfile          # Docker configuration
├── docker-compose.yml  # Docker Compose setup
└── README.md
```

## 🚀 Cách chạy dự án

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
- MySQL: localhost:3306
- Username: root, Password: rootpassword

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

## 🔧 Cấu hình môi trường

### Profiles có sẵn

- **default**: Cấu hình mặc định
- **docker**: Cấu hình cho Docker environment
- **local**: Cấu hình cho development local

### Biến môi trường

| Biến | Mô tả | Giá trị mặc định |
|------|--------|------------------|
| `DB_HOST` | MySQL host | localhost |
| `DB_PORT` | MySQL port | 3306 |
| `DB_NAME` | Database name | mini_supermarket |
| `DB_USERNAME` | Database username | root |
| `DB_PASSWORD` | Database password | rootpassword |

## 📊 Database Schema

Hệ thống bao gồm 21 bảng chính:

### Bảng chính
- `nguoi_dung` - Thông tin người dùng
- `nhan_vien` - Thông tin nhân viên
- `khach_hang` - Thông tin khách hàng
- `san_pham` - Sản phẩm
- `loai_san_pham` - Loại sản phẩm
- `nha_cung_cap` - Nhà cung cấp
- `kho` - Kho hàng
- `cua_hang` - Cửa hàng

### Bảng giao dịch
- `hoa_don` - Hóa đơn bán hàng
- `chi_tiet_hoa_don` - Chi tiết hóa đơn
- `thanh_toan` - Thanh toán
- `gio_hang` - Giỏ hàng
- `chi_tiet_gio_hang` - Chi tiết giỏ hàng

### Bảng kho hàng
- `phieu_nhap_hang` - Phiếu nhập hàng
- `chi_tiet_phieu_nhap` - Chi tiết phiếu nhập
- `phieu_xuat_kho` - Phiếu xuất kho
- `chi_tiet_phieu_xuat` - Chi tiết phiếu xuất
- `ton_kho_chi_tiet` - Tồn kho chi tiết

### Bảng khác
- `khuyen_mai` - Khuyến mãi
- `khuyen_mai_khach_hang` - Khuyến mãi khách hàng
- `khuyen_mai_san_pham` - Khuyến mãi sản phẩm
- `lich_lam_viec` - Lịch làm việc
- `ca_lam_viec` - Ca làm việc
- `gia_san_pham` - Giá sản phẩm
- `hinh_anh` - Hình ảnh
- `phuong_thuc_thanh_toan` - Phương thức thanh toán
- `thong_ke_bao_cao` - Thống kê báo cáo

## 🌐 API Endpoints

### Tổng quan
- **Tổng số controllers:** 27
- **Tổng số endpoints:** 135+ 
- **Base URL:** http://localhost:8080

### Endpoints chính

#### Khách hàng
- `GET /api/khachhang` - Lấy tất cả khách hàng
- `GET /api/khachhang/{id}` - Lấy khách hàng theo ID
- `POST /api/khachhang` - Tạo khách hàng mới
- `PUT /api/khachhang/{id}` - Cập nhật khách hàng
- `DELETE /api/khachhang/{id}` - Xóa khách hàng (soft delete)

#### Sản phẩm
- `GET /api/sanpham` - Lấy tất cả sản phẩm
- `GET /api/sanpham/{id}` - Lấy sản phẩm theo ID
- `POST /api/sanpham` - Tạo sản phẩm mới
- `PUT /api/sanpham/{id}` - Cập nhật sản phẩm
- `DELETE /api/sanpham/{id}` - Xóa sản phẩm (soft delete)

#### Các endpoints khác
Tất cả 27 entities đều có 5 endpoints cơ bản tương tự:
- `GET /rest/{entity}` - Lấy tất cả
- `GET /rest/{entity}/{id}` - Lấy theo ID
- `POST /rest/{entity}` - Tạo mới
- `PUT /rest/{entity}/{id}` - Cập nhật
- `DELETE /rest/{entity}/{id}` - Xóa (soft delete)

### Danh sách đầy đủ endpoints
Chi tiết tất cả 135+ endpoints có thể xem trong file `API-Documentation.md`

## 🔒 Bảo mật

- **Soft Delete:** Tất cả các thao tác xóa đều sử dụng soft delete (đánh dấu `isDeleted = true`)
- **Status Codes:** Sử dụng mã trạng thái số (0,1,2,3) thay vì chuỗi
- **CORS:** Đã được cấu hình cho phép cross-origin requests
- **Serializable:** Tất cả entity đều implement Serializable interface

## 🧪 Testing

### Sử dụng Swagger UI (Khuyến nghị)

1. **Truy cập Swagger UI:**
   - URL: http://localhost:8080/swagger-ui.html
   - Giao diện trực quan để test API
   - Có thể test trực tiếp từ browser

2. **Hướng dẫn chi tiết:**
   - Xem file `SWAGGER-GUIDE.md` để biết cách sử dụng Swagger
   - Test tất cả 135+ endpoints một cách dễ dàng
   - Xem documentation và schema của API

### Test cơ bản với cURL

1. **Lấy tất cả khách hàng:**
```bash
curl -X GET http://localhost:8080/api/khachhang
```

2. **Tạo khách hàng mới:**
```bash
curl -X POST http://localhost:8080/api/khachhang \
  -H "Content-Type: application/json" \
  -d '{"maKH":"KH001","tenKH":"Nguyen Van A","email":"nva@email.com"}'
```

3. **Lấy tất cả sản phẩm:**
```bash
curl -X GET http://localhost:8080/api/sanpham
```

## 🐛 Troubleshooting

### Lỗi thường gặp

1. **Port 8080 đã được sử dụng**
```bash
# Kiểm tra process đang sử dụng port
netstat -tulpn | grep 8080
# Hoặc đổi port trong application.yaml
server:
  port: 8081
```

2. **Không kết nối được MySQL**
```bash
# Kiểm tra MySQL container
docker ps
docker logs mini_supermarket_mysql
```

3. **Lỗi build Maven**
```bash
# Clean và build lại
mvn clean install -DskipTests
```

## 📚 Tài liệu tham khảo

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Docker Documentation](https://docs.docker.com/)
- [MySQL Documentation](https://dev.mysql.com/doc/)

## 🤝 Đóng góp

1. Fork dự án
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## 📝 License

Dự án này được phát triển cho mục đích học tập và nghiên cứu.

---

**Phiên bản:** 1.0.0  
**Ngày cập nhật:** 2025  
**Tác giả:** Mini Supermarket Team 