# BaoCaoDoanhThu - HoaDon Relationship API Documentation

## Tổng quan
Tài liệu này mô tả các API endpoints mới được thêm vào để hỗ trợ mối quan hệ Many-to-Many giữa BaoCaoDoanhThu và HoaDon.

## Các thay đổi chính

### 1. Entity Changes
- **BaoCaoDoanhThu**: Thêm field `List<HoaDon> hoaDons` với annotation `@ManyToMany`
- **HoaDon**: Thêm field `List<BaoCaoDoanhThu> baoCaoDoanhThus` với annotation `@ManyToMany(mappedBy = "hoaDons")`
- **Junction Table**: `BaoCaoDoanhThu_HoaDon` được tạo tự động bởi JPA

### 2. Repository Methods
Các method mới trong `BaoCaoDoanhThuRepository`:
- `findByHoaDonId(Integer maHD)`: Tìm báo cáo theo ID hóa đơn
- `findHoaDonsByBaoCaoId(Long maBaoCao)`: Lấy danh sách hóa đơn của báo cáo
- `countHoaDonsByBaoCaoId(Long maBaoCao)`: Đếm số hóa đơn trong báo cáo
- `sumDoanhThuFromHoaDons(Long maBaoCao)`: Tính tổng doanh thu từ hóa đơn liên kết
- `findByHoaDonDateRange(LocalDateTime, LocalDateTime)`: Tìm báo cáo theo khoảng thời gian hóa đơn

## API Endpoints

### 1. Tìm kiếm báo cáo theo hóa đơn
```http
GET /api/baocaodoanhthu/search/hoadon/{maHD}
```
**Mô tả**: Tìm tất cả báo cáo có chứa hóa đơn với ID cụ thể

**Parameters**:
- `maHD` (path): ID của hóa đơn

**Response**:
```json
[
  {
    "maBaoCao": 1,
    "tenBaoCao": "Báo cáo doanh thu tháng 01/2024",
    "loaiBaoCao": "THANG",
    "tongDoanhThu": 50000000,
    ...
  }
]
```

### 2. Lấy danh sách hóa đơn của báo cáo
```http
GET /api/baocaodoanhthu/{id}/hoadons
```
**Mô tả**: Lấy tất cả hóa đơn liên kết với báo cáo

**Parameters**:
- `id` (path): ID của báo cáo

**Response**:
```json
[
  {
    "maHD": 1001,
    "ngayLap": "2024-01-15T10:30:00",
    "tongTien": 250000,
    "trangThai": 1,
    ...
  }
]
```

### 3. Đếm số hóa đơn trong báo cáo
```http
GET /api/baocaodoanhthu/{id}/hoadons/count
```
**Mô tả**: Đếm tổng số hóa đơn liên kết với báo cáo

**Response**:
```json
{
  "count": 150
}
```

### 4. Tính doanh thu từ hóa đơn liên kết
```http
GET /api/baocaodoanhthu/{id}/doanhthu-from-hoadons
```
**Mô tả**: Tính tổng doanh thu từ các hóa đơn đã liên kết

**Response**:
```json
{
  "doanhThu": 45000000.00
}
```

### 5. Liên kết hóa đơn với báo cáo
```http
POST /api/baocaodoanhthu/{id}/link-hoadons
```
**Mô tả**: Thêm các hóa đơn vào báo cáo

**Request Body**:
```json
[1001, 1002, 1003, 1004]
```

**Response**:
```json
{
  "success": true,
  "message": "Liên kết hóa đơn thành công",
  "data": {
    "maBaoCao": 1,
    "tenBaoCao": "...",
    ...
  }
}
```

### 6. Hủy liên kết hóa đơn khỏi báo cáo
```http
POST /api/baocaodoanhthu/{id}/unlink-hoadons
```
**Mô tả**: Xóa các hóa đơn khỏi báo cáo

**Request Body**:
```json
[1001, 1002]
```

**Response**:
```json
{
  "success": true,
  "message": "Hủy liên kết hóa đơn thành công",
  "data": {
    "maBaoCao": 1,
    ...
  }
}
```

### 7. Tìm báo cáo theo khoảng thời gian hóa đơn
```http
GET /api/baocaodoanhthu/search/hoadon-daterange?tuNgay={tuNgay}&denNgay={denNgay}
```
**Mô tả**: Tìm báo cáo có chứa hóa đơn trong khoảng thời gian

**Parameters**:
- `tuNgay` (query): Thời gian bắt đầu (ISO DateTime format)
- `denNgay` (query): Thời gian kết thúc (ISO DateTime format)

**Example**:
```
GET /api/baocaodoanhthu/search/hoadon-daterange?tuNgay=2024-01-01T00:00:00&denNgay=2024-01-31T23:59:59
```

## Tự động liên kết hóa đơn

Khi tạo báo cáo mới thông qua endpoint `/generate` hoặc `/regenerate`, hệ thống sẽ tự động:

1. Tìm tất cả hóa đơn trong khoảng thời gian báo cáo
2. Liên kết các hóa đơn này với báo cáo vừa tạo
3. Cập nhật thông tin báo cáo

## Database Schema

### Bảng BaoCaoDoanhThu_HoaDon
```sql
CREATE TABLE BaoCaoDoanhThu_HoaDon (
    MaBaoCao BIGINT NOT NULL,
    MaHD INT NOT NULL,
    NgayLienKet DATETIME DEFAULT CURRENT_TIMESTAMP,
    NguoiTao VARCHAR(50),
    PRIMARY KEY (MaBaoCao, MaHD)
);
```

### Indexes
- `idx_baocao_hoadon_baocao` trên `MaBaoCao`
- `idx_baocao_hoadon_hoadon` trên `MaHD`
- `idx_baocao_hoadon_ngaylienket` trên `NgayLienKet`

## Lưu ý quan trọng

1. **Performance**: Với mối quan hệ Many-to-Many, cần chú ý performance khi load dữ liệu. Sử dụng `FetchType.LAZY` để tránh N+1 problem.

2. **Data Consistency**: Khi xóa hóa đơn hoặc báo cáo, mối quan hệ sẽ tự động được xóa nhờ `CASCADE` constraint.

3. **Automatic Linking**: Hệ thống tự động liên kết hóa đơn khi tạo báo cáo, nhưng có thể thêm/xóa thủ công qua API.

4. **Error Handling**: Tất cả endpoints đều có xử lý lỗi và trả về response thống nhất.

## Migration

Để áp dụng các thay đổi này:

1. Chạy migration script: `database/baocaodoanhthu_hoadon_migration.sql`
2. Restart ứng dụng để load các entity changes
3. Test các API endpoints mới

## Examples

### Tạo báo cáo và tự động liên kết hóa đơn
```bash
curl -X POST "http://localhost:8080/api/baocaodoanhthu/generate" \
  -d "loaiBaoCao=THANG&tuNgay=2024-01-01&denNgay=2024-01-31"
```

### Thêm hóa đơn vào báo cáo có sẵn
```bash
curl -X POST "http://localhost:8080/api/baocaodoanhthu/1/link-hoadons" \
  -H "Content-Type: application/json" \
  -d "[1001, 1002, 1003]"
```

### Kiểm tra hóa đơn trong báo cáo
```bash
curl "http://localhost:8080/api/baocaodoanhthu/1/hoadons"
```
