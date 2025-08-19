# BaoCaoDoanhThu API Documentation

## Tổng quan
API BaoCaoDoanhThu cung cấp các chức năng quản lý báo cáo doanh thu cho hệ thống mini_Supermarket, bao gồm tạo báo cáo tự động, thống kê sản phẩm bán chạy, phân tích khách hàng tiềm năng và theo dõi tăng trưởng doanh thu.

## Base URL
```
http://localhost:8080/api/baocao-doanhthu
```

## Authentication
Tất cả các endpoint yêu cầu xác thực JWT token trong header:
```
Authorization: Bearer <jwt_token>
```

---

## 1. CRUD Operations

### 1.1 Lấy danh sách tất cả báo cáo
**GET** `/api/baocao-doanhthu`

**Response:**
```json
[
  {
    "maBaoCao": 1,
    "loaiBaoCao": "THANG",
    "tuNgay": "2024-01-01",
    "denNgay": "2024-01-31",
    "tongDoanhThu": 45000000.00,
    "soLuongHoaDon": 850,
    "soLuongSanPhamBan": 2500,
    "tiLeThanhCong": 95.5,
    "tangTruongDoanhThu": 12.5,
    "tangTruongHoaDon": 8.3,
    "soLuongKhachHang": 320,
    "soLuongSanPham": 150,
    "cuaHang": {
      "maCH": "CH001",
      "tenCH": "Siêu thị ABC"
    },
    "ngayTao": "2024-02-01T00:00:00",
    "ghiChu": "Báo cáo tháng 1/2024"
  }
]
```

### 1.2 Lấy báo cáo theo ID
**GET** `/api/baocao-doanhthu/{id}`

**Response:**
```json
{
  "maBaoCao": 1,
  "loaiBaoCao": "THANG",
  "tuNgay": "2024-01-01",
  "denNgay": "2024-01-31",
  "tongDoanhThu": 45000000.00,
  "soLuongHoaDon": 850,
  "chiTietList": [
    {
      "maChiTiet": 1,
      "loaiChiTiet": "SANPHAM",
      "maDoiTuong": "SP001",
      "tenDoiTuong": "Coca Cola 330ml",
      "soLuong": 500,
      "doanhThu": 2500000.00,
      "tiLe": 5.56,
      "tangTruong": 15.2,
      "ranking": 1
    }
  ]
}
```

### 1.3 Tạo báo cáo mới
**POST** `/api/baocao-doanhthu`

**Request Body:**
```json
{
  "loaiBaoCao": "THANG",
  "tuNgay": "2024-02-01",
  "denNgay": "2024-02-29",
  "maCH": "CH001",
  "ghiChu": "Báo cáo tháng 2/2024"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Tạo báo cáo doanh thu thành công",
  "data": {
    "maBaoCao": 2,
    "loaiBaoCao": "THANG",
    "tuNgay": "2024-02-01",
    "denNgay": "2024-02-29",
    "tongDoanhThu": 52000000.00,
    "soLuongHoaDon": 920,
    "tangTruongDoanhThu": 15.6
  }
}
```

### 1.4 Cập nhật báo cáo
**PUT** `/api/baocao-doanhthu/{id}`

**Request Body:**
```json
{
  "ghiChu": "Báo cáo đã được cập nhật",
  "tongDoanhThu": 52500000.00
}
```

### 1.5 Xóa báo cáo (soft delete)
**DELETE** `/api/baocao-doanhthu/{id}`

**Response:**
```json
{
  "success": true,
  "message": "Xóa báo cáo thành công"
}
```

---

## 2. Search Operations

### 2.1 Tìm kiếm theo loại báo cáo
**GET** `/api/baocao-doanhthu/search/loai/{loai}`

**Parameters:**
- `loai`: NGAY, TUAN, THANG, NAM

### 2.2 Tìm kiếm theo cửa hàng
**GET** `/api/baocao-doanhthu/search/cuahang/{maCH}`

### 2.3 Tìm kiếm theo khoảng thời gian
**GET** `/api/baocao-doanhthu/search/date-range`

**Query Parameters:**
- `tuNgay`: 2024-01-01 (required)
- `denNgay`: 2024-01-31 (required)

### 2.4 Tìm kiếm nâng cao
**GET** `/api/baocao-doanhthu/search/advanced`

**Query Parameters:**
- `loai`: THANG (optional)
- `maCH`: CH001 (optional)
- `tuNgay`: 2024-01-01 (required)
- `denNgay`: 2024-01-31 (required)

---

## 3. Report Generation

### 3.1 Tạo báo cáo tự động
**POST** `/api/baocao-doanhthu/generate`

**Request Body:**
```json
{
  "loaiBaoCao": "THANG",
  "tuNgay": "2024-03-01",
  "denNgay": "2024-03-31",
  "maCH": "CH001"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Tạo báo cáo doanh thu thành công",
  "data": {
    "maBaoCao": 3,
    "tongDoanhThu": 48500000.00,
    "soLuongHoaDon": 875,
    "tangTruongDoanhThu": -6.7,
    "chiTietList": [...]
  }
}
```

### 3.2 Tạo lại báo cáo (ghi đè)
**POST** `/api/baocao-doanhthu/regenerate`

---

## 4. Detail Operations

### 4.1 Lấy chi tiết báo cáo
**GET** `/api/baocao-doanhthu/{id}/chitiet`

### 4.2 Lấy chi tiết theo loại
**GET** `/api/baocao-doanhthu/{id}/chitiet/{loai}`

**Parameters:**
- `loai`: SANPHAM, KHACHHANG

### 4.3 Lấy top sản phẩm bán chạy
**GET** `/api/baocao-doanhthu/{id}/top-sanpham`

**Query Parameters:**
- `limit`: 10 (default)

**Response:**
```json
[
  {
    "maChiTiet": 1,
    "loaiChiTiet": "SANPHAM",
    "maDoiTuong": "SP001",
    "tenDoiTuong": "Coca Cola 330ml",
    "soLuong": 500,
    "doanhThu": 2500000.00,
    "tiLe": 5.56,
    "ranking": 1,
    "soLanGiaoDich": 125,
    "giaTriTrungBinh": 20000.00
  }
]
```

### 4.4 Lấy top khách hàng tiềm năng
**GET** `/api/baocao-doanhthu/{id}/top-khachhang`

**Query Parameters:**
- `limit`: 10 (default)

---

## 5. Statistics Operations

### 5.1 Thống kê sản phẩm bán chạy
**GET** `/api/baocao-doanhthu/thongke/sanpham-banchay`

**Query Parameters:**
- `tuNgay`: 2024-01-01 (required)
- `denNgay`: 2024-01-31 (required)
- `maCH`: CH001 (optional)
- `limit`: 10 (default)

**Response:**
```json
[
  {
    "maSP": "SP001",
    "tenSP": "Coca Cola 330ml",
    "soLuongBan": 500,
    "doanhThu": 2500000.00,
    "tiLeDongGop": 5.56,
    "tangTruong": 15.2,
    "soLanBan": 125,
    "giaTriTrungBinh": 20000.00,
    "ranking": 1
  }
]
```

### 5.2 Thống kê sản phẩm tăng trưởng
**GET** `/api/baocao-doanhthu/thongke/sanpham-tangtruong`

### 5.3 Thống kê khách hàng tiềm năng
**GET** `/api/baocao-doanhthu/thongke/khachhang-tiemnang`

**Response:**
```json
[
  {
    "maKH": "KH001",
    "tenKH": "Nguyễn Văn A",
    "soLanMua": 15,
    "tongChiTieu": 3500000.00,
    "chiTieuTrungBinh": 233333.33,
    "tangTruong": 25.8,
    "lanMuaCuoi": "2024-01-30",
    "soNgayKhongMua": 5,
    "ranking": 1
  }
]
```

### 5.4 Thống kê khách hàng tăng trưởng
**GET** `/api/baocao-doanhthu/thongke/khachhang-tangtruong`

---

## 6. Utility Operations

### 6.1 Kiểm tra báo cáo tồn tại
**GET** `/api/baocao-doanhthu/check-exists`

**Query Parameters:**
- `loai`: THANG (required)
- `tuNgay`: 2024-01-01 (required)
- `denNgay`: 2024-01-31 (required)
- `maCH`: CH001 (optional)

**Response:**
```json
{
  "exists": true,
  "message": "Báo cáo đã tồn tại"
}
```

### 6.2 Lấy báo cáo kỳ trước
**GET** `/api/baocao-doanhthu/previous-period`

**Query Parameters:**
- `loai`: THANG (required)
- `tuNgay`: 2024-02-01 (required)
- `maCH`: CH001 (optional)

---

## Error Codes

| Code | Message | Description |
|------|---------|-------------|
| 400 | Bad Request | Dữ liệu đầu vào không hợp lệ |
| 404 | Not Found | Không tìm thấy báo cáo |
| 409 | Conflict | Báo cáo đã tồn tại |
| 500 | Internal Server Error | Lỗi hệ thống |

## Error Response Format
```json
{
  "success": false,
  "message": "Mô tả lỗi",
  "error": "Chi tiết lỗi kỹ thuật"
}
```

---

## Data Models

### BaoCaoDoanhThuRequest
```json
{
  "loaiBaoCao": "string", // NGAY, TUAN, THANG, NAM
  "tuNgay": "date",       // YYYY-MM-DD
  "denNgay": "date",      // YYYY-MM-DD
  "maCH": "string",       // Optional
  "ghiChu": "string"      // Optional
}
```

### ThongKeSanPhamDTO
```json
{
  "maSP": "string",
  "tenSP": "string",
  "soLuongBan": "integer",
  "doanhThu": "decimal",
  "tiLeDongGop": "decimal",
  "tangTruong": "decimal",
  "soLanBan": "integer",
  "giaTriTrungBinh": "decimal",
  "ranking": "integer"
}
```

### ThongKeKhachHangDTO
```json
{
  "maKH": "string",
  "tenKH": "string",
  "soLanMua": "integer",
  "tongChiTieu": "decimal",
  "chiTieuTrungBinh": "decimal",
  "tangTruong": "decimal",
  "lanMuaCuoi": "date",
  "soNgayKhongMua": "integer",
  "ranking": "integer"
}
```

---

## Usage Examples

### Tạo báo cáo tháng
```bash
curl -X POST http://localhost:8080/api/baocao-doanhthu/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "loaiBaoCao": "THANG",
    "tuNgay": "2024-03-01",
    "denNgay": "2024-03-31",
    "maCH": "CH001"
  }'
```

### Lấy top 5 sản phẩm bán chạy
```bash
curl -X GET "http://localhost:8080/api/baocao-doanhthu/thongke/sanpham-banchay?tuNgay=2024-01-01&denNgay=2024-01-31&limit=5" \
  -H "Authorization: Bearer <token>"
```

### Kiểm tra báo cáo tồn tại
```bash
curl -X GET "http://localhost:8080/api/baocao-doanhthu/check-exists?loai=THANG&tuNgay=2024-01-01&denNgay=2024-01-31&maCH=CH001" \
  -H "Authorization: Bearer <token>"
```