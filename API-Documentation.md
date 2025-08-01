# Mini Supermarket - REST API Documentation

## API Endpoints Overview

Tổng cộng **28 REST Controllers** đã được tạo với đầy đủ CRUD operations:

### 1. **Quản lý Người dùng & Nhân viên**
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

### 2. **Quản lý Khách hàng**
- **POST** `/api/khachhang` - Tạo khách hàng mới
- **GET** `/api/khachhang` - Lấy tất cả khách hàng
- **GET** `/api/khachhang/{id}` - Lấy khách hàng theo ID
- **PUT** `/api/khachhang/{id}` - Cập nhật khách hàng
- **DELETE** `/api/khachhang/{id}` - Xóa khách hàng

### 3. **Quản lý Cửa hàng & Kho**
- **POST** `/api/cuahang` - Tạo cửa hàng mới
- **GET** `/api/cuahang` - Lấy tất cả cửa hàng
- **GET** `/api/cuahang/{id}` - Lấy cửa hàng theo ID
- **PUT** `/api/cuahang/{id}` - Cập nhật cửa hàng
- **DELETE** `/api/cuahang/{id}` - Xóa cửa hàng

- **POST** `/api/kho` - Tạo kho mới
- **GET** `/api/kho` - Lấy tất cả kho
- **GET** `/api/kho/{id}` - Lấy kho theo ID
- **PUT** `/api/kho/{id}` - Cập nhật kho
- **DELETE** `/api/kho/{id}` - Xóa kho

### 4. **Quản lý Sản phẩm**
- **POST** `/api/sanpham` - Tạo sản phẩm mới
- **GET** `/api/sanpham` - Lấy tất cả sản phẩm
- **GET** `/api/sanpham/{id}` - Lấy sản phẩm theo ID
- **PUT** `/api/sanpham/{id}` - Cập nhật sản phẩm
- **DELETE** `/api/sanpham/{id}` - Xóa sản phẩm

- **POST** `/api/loaisanpham` - Tạo loại sản phẩm mới
- **GET** `/api/loaisanpham` - Lấy tất cả loại sản phẩm
- **GET** `/api/loaisanpham/{id}` - Lấy loại sản phẩm theo ID
- **PUT** `/api/loaisanpham/{id}` - Cập nhật loại sản phẩm
- **DELETE** `/api/loaisanpham/{id}` - Xóa loại sản phẩm

- **POST** `/api/giasanpham` - Tạo giá sản phẩm mới
- **GET** `/api/giasanpham` - Lấy tất cả giá sản phẩm
- **GET** `/api/giasanpham/{id}` - Lấy giá sản phẩm theo ID
- **PUT** `/api/giasanpham/{id}` - Cập nhật giá sản phẩm
- **DELETE** `/api/giasanpham/{id}` - Xóa giá sản phẩm

- **POST** `/api/hinhanh` - Tạo hình ảnh sản phẩm mới
- **GET** `/api/hinhanh` - Lấy tất cả hình ảnh
- **GET** `/api/hinhanh/{id}` - Lấy hình ảnh theo ID
- **PUT** `/api/hinhanh/{id}` - Cập nhật hình ảnh
- **DELETE** `/api/hinhanh/{id}` - Xóa hình ảnh

### 5. **Quản lý Nhà cung cấp**
- **POST** `/api/nhacungcap` - Tạo nhà cung cấp mới
- **GET** `/api/nhacungcap` - Lấy tất cả nhà cung cấp
- **GET** `/api/nhacungcap/{id}` - Lấy nhà cung cấp theo ID
- **PUT** `/api/nhacungcap/{id}` - Cập nhật nhà cung cấp
- **DELETE** `/api/nhacungcap/{id}` - Xóa nhà cung cấp

### 6. **Quản lý Hóa đơn**
- **POST** `/api/hoadon` - Tạo hóa đơn mới
- **GET** `/api/hoadon` - Lấy tất cả hóa đơn
- **GET** `/api/hoadon/{id}` - Lấy hóa đơn theo ID
- **PUT** `/api/hoadon/{id}` - Cập nhật hóa đơn
- **DELETE** `/api/hoadon/{id}` - Xóa hóa đơn

- **POST** `/api/chitiethoadon` - Tạo chi tiết hóa đơn mới
- **GET** `/api/chitiethoadon` - Lấy tất cả chi tiết hóa đơn
- **GET** `/api/chitiethoadon/{id}` - Lấy chi tiết hóa đơn theo ID
- **PUT** `/api/chitiethoadon/{id}` - Cập nhật chi tiết hóa đơn
- **DELETE** `/api/chitiethoadon/{id}` - Xóa chi tiết hóa đơn

### 7. **Quản lý Giỏ hàng**
- **POST** `/api/giohang` - Tạo giỏ hàng mới
- **GET** `/api/giohang` - Lấy tất cả giỏ hàng
- **GET** `/api/giohang/{id}` - Lấy giỏ hàng theo ID
- **PUT** `/api/giohang/{id}` - Cập nhật giỏ hàng
- **DELETE** `/api/giohang/{id}` - Xóa giỏ hàng

- **POST** `/api/chitietgiohang` - Tạo chi tiết giỏ hàng mới
- **GET** `/api/chitietgiohang` - Lấy tất cả chi tiết giỏ hàng
- **GET** `/api/chitietgiohang/{id}` - Lấy chi tiết giỏ hàng theo ID
- **PUT** `/api/chitietgiohang/{id}` - Cập nhật chi tiết giỏ hàng
- **DELETE** `/api/chitietgiohang/{id}` - Xóa chi tiết giỏ hàng

### 8. **Quản lý Khuyến mãi**
- **POST** `/api/khuyenmai` - Tạo khuyến mãi mới
- **GET** `/api/khuyenmai` - Lấy tất cả khuyến mãi
- **GET** `/api/khuyenmai/{id}` - Lấy khuyến mãi theo ID
- **PUT** `/api/khuyenmai/{id}` - Cập nhật khuyến mãi
- **DELETE** `/api/khuyenmai/{id}` - Xóa khuyến mãi

- **POST** `/api/khuyenmaikhachhang` - Tạo khuyến mãi cho khách hàng mới
- **GET** `/api/khuyenmaikhachhang` - Lấy tất cả khuyến mãi khách hàng
- **GET** `/api/khuyenmaikhachhang/{id}` - Lấy khuyến mãi khách hàng theo ID
- **PUT** `/api/khuyenmaikhachhang/{id}` - Cập nhật khuyến mãi khách hàng
- **DELETE** `/api/khuyenmaikhachhang/{id}` - Xóa khuyến mãi khách hàng

- **POST** `/api/khuyenmaisanpham` - Tạo khuyến mãi cho sản phẩm mới
- **GET** `/api/khuyenmaisanpham` - Lấy tất cả khuyến mãi sản phẩm
- **GET** `/api/khuyenmaisanpham/{id}` - Lấy khuyến mãi sản phẩm theo ID
- **PUT** `/api/khuyenmaisanpham/{id}` - Cập nhật khuyến mãi sản phẩm
- **DELETE** `/api/khuyenmaisanpham/{id}` - Xóa khuyến mãi sản phẩm

### 9. **Quản lý Thanh toán**
- **POST** `/api/phuongthucthanhtoan` - Tạo phương thức thanh toán mới
- **GET** `/api/phuongthucthanhtoan` - Lấy tất cả phương thức thanh toán
- **GET** `/api/phuongthucthanhtoan/{id}` - Lấy phương thức thanh toán theo ID
- **PUT** `/api/phuongthucthanhtoan/{id}` - Cập nhật phương thức thanh toán
- **DELETE** `/api/phuongthucthanhtoan/{id}` - Xóa phương thức thanh toán

- **POST** `/api/thanhtoan` - Tạo giao dịch thanh toán mới
- **GET** `/api/thanhtoan` - Lấy tất cả giao dịch thanh toán
- **GET** `/api/thanhtoan/{id}` - Lấy giao dịch thanh toán theo ID
- **PUT** `/api/thanhtoan/{id}` - Cập nhật giao dịch thanh toán
- **DELETE** `/api/thanhtoan/{id}` - Xóa giao dịch thanh toán

### 10. **Quản lý Nhập/Xuất kho**
- **POST** `/api/phieunhaphang` - Tạo phiếu nhập hàng mới
- **GET** `/api/phieunhaphang` - Lấy tất cả phiếu nhập hàng
- **GET** `/api/phieunhaphang/{id}` - Lấy phiếu nhập hàng theo ID
- **PUT** `/api/phieunhaphang/{id}` - Cập nhật phiếu nhập hàng
- **DELETE** `/api/phieunhaphang/{id}` - Xóa phiếu nhập hàng

- **POST** `/api/chitietphieunhap` - Tạo chi tiết phiếu nhập mới
- **GET** `/api/chitietphieunhap` - Lấy tất cả chi tiết phiếu nhập
- **GET** `/api/chitietphieunhap/{id}` - Lấy chi tiết phiếu nhập theo ID
- **PUT** `/api/chitietphieunhap/{id}` - Cập nhật chi tiết phiếu nhập
- **DELETE** `/api/chitietphieunhap/{id}` - Xóa chi tiết phiếu nhập

- **POST** `/api/phieuxuatkho` - Tạo phiếu xuất kho mới
- **GET** `/api/phieuxuatkho` - Lấy tất cả phiếu xuất kho
- **GET** `/api/phieuxuatkho/{id}` - Lấy phiếu xuất kho theo ID
- **PUT** `/api/phieuxuatkho/{id}` - Cập nhật phiếu xuất kho
- **DELETE** `/api/phieuxuatkho/{id}` - Xóa phiếu xuất kho

- **POST** `/api/chitietphieuxuat` - Tạo chi tiết phiếu xuất mới
- **GET** `/api/chitietphieuxuat` - Lấy tất cả chi tiết phiếu xuất
- **GET** `/api/chitietphieuxuat/{id}` - Lấy chi tiết phiếu xuất theo ID
- **PUT** `/api/chitietphieuxuat/{id}` - Cập nhật chi tiết phiếu xuất
- **DELETE** `/api/chitietphieuxuat/{id}` - Xóa chi tiết phiếu xuất

### 11. **Quản lý Tồn kho**
- **POST** `/api/tonkhochitiet` - Tạo tồn kho chi tiết mới
- **GET** `/api/tonkhochitiet` - Lấy tất cả tồn kho chi tiết
- **GET** `/api/tonkhochitiet/{id}` - Lấy tồn kho chi tiết theo ID
- **PUT** `/api/tonkhochitiet/{id}` - Cập nhật tồn kho chi tiết
- **DELETE** `/api/tonkhochitiet/{id}` - Xóa tồn kho chi tiết

### 12. **Quản lý Ca làm việc**
- **POST** `/api/calamviec` - Tạo ca làm việc mới
- **GET** `/api/calamviec` - Lấy tất cả ca làm việc
- **GET** `/api/calamviec/{id}` - Lấy ca làm việc theo ID
- **PUT** `/api/calamviec/{id}` - Cập nhật ca làm việc
- **DELETE** `/api/calamviec/{id}` - Xóa ca làm việc

- **POST** `/api/lichlamviec` - Tạo lịch làm việc mới
- **GET** `/api/lichlamviec` - Lấy tất cả lịch làm việc
- **GET** `/api/lichlamviec/{id}` - Lấy lịch làm việc theo ID
- **PUT** `/api/lichlamviec/{id}` - Cập nhật lịch làm việc
- **DELETE** `/api/lichlamviec/{id}` - Xóa lịch làm việc

### 13. **Quản lý Bảng lương**
- **POST** `/api/bangluong` - Tạo bảng lương mới
- **GET** `/api/bangluong` - Lấy tất cả bảng lương
- **GET** `/api/bangluong/{id}` - Lấy bảng lương theo ID
- **PUT** `/api/bangluong/{id}` - Cập nhật bảng lương
- **DELETE** `/api/bangluong/{id}` - Xóa bảng lương

### 14. **Quản lý Thống kê & Báo cáo**
- **POST** `/api/thongkebaocao` - Tạo báo cáo mới
- **GET** `/api/thongkebaocao` - Lấy tất cả báo cáo
- **GET** `/api/thongkebaocao/{id}` - Lấy báo cáo theo ID
- **PUT** `/api/thongkebaocao/{id}` - Cập nhật báo cáo
- **DELETE** `/api/thongkebaocao/{id}` - Xóa báo cáo

## Tính năng chính

### ✅ **CRUD Operations**
- **Create** (POST) - Tạo mới
- **Read** (GET) - Lấy dữ liệu
- **Update** (PUT) - Cập nhật
- **Delete** (DELETE) - Xóa

### ✅ **Response Format**
- **Success**: HTTP 200 OK + JSON data
- **Not Found**: HTTP 404 Not Found
- **Error**: HTTP 400/500 + Error message

### ✅ **Soft Delete Support**
- Tương thích với hệ thống soft delete
- Sử dụng `deleteById()` methods từ service layer

### ✅ **Status Codes**
- Hỗ trợ status codes (0,1,2,3) cho các trạng thái
- Tuân thủ memory requirements

## Cách sử dụng

### **Base URL**: `http://localhost:8080`

### **Content-Type**: `application/json`

### **Ví dụ Requests**:

```bash
# Lấy tất cả sản phẩm
GET http://localhost:8080/api/sanpham

# Lấy sản phẩm theo ID
GET http://localhost:8080/api/sanpham/SP00001

# Tạo sản phẩm mới
POST http://localhost:8080/api/sanpham
Content-Type: application/json

{
  "maSP": "SP00006",
  "tenSP": "Sản phẩm mới",
  "giaBan": 50000,
  "moTa": "Mô tả sản phẩm"
}

# Cập nhật sản phẩm
PUT http://localhost:8080/api/sanpham/SP00001
Content-Type: application/json

{
  "tenSP": "Tên sản phẩm đã cập nhật",
  "giaBan": 60000
}

# Xóa sản phẩm
DELETE http://localhost:8080/api/sanpham/SP00001
```

### **Ví dụ BangLuong API**:

```bash
# Lấy tất cả bảng lương
GET http://localhost:8080/api/bangluong

# Lấy bảng lương theo ID
GET http://localhost:8080/api/bangluong/1

# Tạo bảng lương mới
POST http://localhost:8080/api/bangluong
Content-Type: application/json

{
  "nhanVien": {
    "maNV": "NV001"
  },
  "thangLuong": 7,
  "namLuong": 2024,
  "luongCoBan": 15000000,
  "phuCap": 2000000,
  "thuong": 1000000,
  "khauTru": 500000,
  "soNgayLam": 22,
  "soGioLam": 176.0,
  "ghiChu": "Lương tháng 7/2024"
}

# Cập nhật bảng lương
PUT http://localhost:8080/api/bangluong/1
Content-Type: application/json

{
  "luongCoBan": 16000000,
  "thuong": 1500000,
  "ghiChu": "Đã cập nhật lương tháng 7/2024"
}

# Xóa bảng lương
DELETE http://localhost:8080/api/bangluong/1
```

**Tổng cộng: 28 REST Controllers với 140+ API endpoints!** 🚀 