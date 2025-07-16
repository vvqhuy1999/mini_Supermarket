-- Initialize database for Mini Supermarket
-- This file will be executed when MySQL container starts

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS QuanLySieuThi;

-- Use the database
USE QuanLySieuThi;

-- The tables will be created automatically by Hibernate when the application starts 

-- DROP DATABASE IF EXISTS QuanLySieuThi;
-- CREATE DATABASE QuanLySieuThi;
-- USE QuanLySieuThi;

-- Bảng quản lý thông tin người dùng hệ thống
CREATE TABLE NguoiDung (
    MaNguoiDung NVARCHAR(10) PRIMARY KEY,
    TenDangNhap NVARCHAR(50),
    MatKhau NVARCHAR(255),
    VaiTro INT NOT NULL COMMENT '0=Quản trị, 1=Quản lý, 2=Nhân viên, 3=Khách hàng',
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý thông tin các cửa hàng trong hệ thống
CREATE TABLE CuaHang (
    MaCH NVARCHAR(10) PRIMARY KEY,
    TenCH NVARCHAR(255),
    DiaChi NVARCHAR(255),
    SDT NVARCHAR(15),
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý thông tin nhà cung cấp sản phẩm
CREATE TABLE NhaCungCap (
    MaNCC NVARCHAR(10) PRIMARY KEY,
    TenNCC NVARCHAR(255),
    DiaChi NVARCHAR(255),
    SDT NVARCHAR(15),
    ThongTinHopDong LONGTEXT,
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý thông tin nhân viên làm việc tại cửa hàng
CREATE TABLE NhanVien (
    MaNV NVARCHAR(10) PRIMARY KEY,
    MaNguoiDung NVARCHAR(10),
    HoTen NVARCHAR(255),
    SDT NVARCHAR(15),
    DiaChi NVARCHAR(255),
    NgaySinh DATE,
    Luong DECIMAL(15,2),
    MaQuanLy NVARCHAR(10) COMMENT 'Mã nhân viên quản lý trực tiếp',
    MaCH NVARCHAR(10) COMMENT 'Cửa hàng nơi nhân viên làm việc',
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý thông tin khách hàng và điểm tích lũy
CREATE TABLE KhachHang (
    MaKH NVARCHAR(10) PRIMARY KEY,
    MaNguoiDung NVARCHAR(10),
    HoTen NVARCHAR(255),
    SDT NVARCHAR(15),
    Email NVARCHAR(100) CHECK (Email LIKE '%@%.%'),
    DiaChi NVARCHAR(255),
    DiemTichLuy INT CHECK (DiemTichLuy >= 0) COMMENT 'Điểm tích lũy từ các giao dịch mua hàng',
    IsDeleted BIT DEFAULT 0,
    INDEX idx_khachhang_sdt (SDT),
    INDEX idx_khachhang_email (Email)
);

-- Bảng phân loại các sản phẩm trong hệ thống
CREATE TABLE LoaiSanPham (
    MaLoaiSP NVARCHAR(10) PRIMARY KEY,
    TenLoai NVARCHAR(255),
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý thông tin chi tiết sản phẩm
CREATE TABLE SanPham (
    MaSP NVARCHAR(10) PRIMARY KEY,
    MaLoaiSP NVARCHAR(10),
    TenSP NVARCHAR(255),
    MoTa LONGTEXT,
    GiaBan DECIMAL(15,2) COMMENT 'Giá bán hiện tại của sản phẩm',
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý các chương trình khuyến mãi
CREATE TABLE KhuyenMai (
    MaKM NVARCHAR(10) PRIMARY KEY,
    TenChuongTrinh NVARCHAR(255),
    MoTa LONGTEXT,
    LoaiKM NVARCHAR(50) COMMENT 'Loại khuyến mãi: PhầnTrăm, Điểm, Khác',
    GiaTriKM DECIMAL(15,2) COMMENT 'Giá trị khuyến mãi (% hoặc số tiền)',
    NgayBatDau DATETIME,
    NgayKetThuc DATETIME,
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý các phương thức thanh toán được chấp nhận
CREATE TABLE PhuongThucThanhToan (
    MaPTTT NVARCHAR(10) PRIMARY KEY,
    TenPTTT NVARCHAR(100),
    MoTa LONGTEXT,
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý thông tin các kho hàng
CREATE TABLE Kho (
    MaKho INT AUTO_INCREMENT PRIMARY KEY,
    TenKho NVARCHAR(255),
    DiaChi NVARCHAR(255),
    MaCH NVARCHAR(10) COMMENT 'Cửa hàng quản lý kho',
    IsDeleted BIT DEFAULT 0
);

-- Bảng định nghĩa các ca làm việc trong ngày
CREATE TABLE CaLamViec (
    MaCa INT AUTO_INCREMENT PRIMARY KEY,
    TenCa NVARCHAR(100),
    GioBatDau TIME COMMENT 'Thời gian bắt đầu ca làm',
    GioKetThuc TIME COMMENT 'Thời gian kết thúc ca làm',
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý lịch làm việc của nhân viên
CREATE TABLE LichLamViec (
    MaLich INT AUTO_INCREMENT PRIMARY KEY,
    MaNV NVARCHAR(10),
    MaCa INT,
    NgayLam DATE,
    MaNVQuanLy NVARCHAR(10) COMMENT 'Người quản lý phê duyệt lịch',
    TrangThai INT COMMENT '0=Chờ duyệt, 1=Đã duyệt, 2=Từ chối, 3=Hủy',
    NgayDuyet DATETIME,
    GhiChu LONGTEXT,
    IsDeleted BIT DEFAULT 0
);

-- Bảng lưu trữ hình ảnh sản phẩm
CREATE TABLE HinhAnh (
    MaHinh INT AUTO_INCREMENT PRIMARY KEY,
    MaSP NVARCHAR(10),
    URL NVARCHAR(255),
    MoTa NVARCHAR(255),
    LaChinh BOOLEAN DEFAULT FALSE COMMENT 'Đánh dấu ảnh chính của sản phẩm',
    ThuTuHienThi INT DEFAULT 0 COMMENT 'Thứ tự hiển thị ảnh',
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý lịch sử giá sản phẩm
CREATE TABLE GiaSanPham (
    MaGia INT AUTO_INCREMENT PRIMARY KEY,
    MaSP NVARCHAR(10),
    Gia DECIMAL(15,2),
    NgayBatDau DATE COMMENT 'Ngày bắt đầu áp dụng giá mới',
    NgayKetThuc DATE COMMENT 'Ngày kết thúc áp dụng giá',
    IsDeleted BIT DEFAULT 0
);

-- Bảng theo dõi số lượng tồn kho của sản phẩm
CREATE TABLE TonKhoChiTiet (
    MaTKCT INT AUTO_INCREMENT PRIMARY KEY,
    MaSP NVARCHAR(10),
    MaKho INT,
    SoLuongTon INT CHECK (SoLuongTon >= 0) COMMENT 'Số lượng sản phẩm hiện có trong kho',
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý phiếu nhập hàng từ nhà cung cấp
CREATE TABLE PhieuNhapHang (
    MaPN INT AUTO_INCREMENT PRIMARY KEY,
    MaNCC NVARCHAR(10),
    MaKho INT,
    MaNVLap NVARCHAR(10) COMMENT 'Nhân viên lập phiếu nhập',
    NgayNhap DATETIME,
    TongTienNhap DECIMAL(15,2),
    IsDeleted BIT DEFAULT 0
);

-- Bảng chi tiết các sản phẩm trong phiếu nhập
CREATE TABLE ChiTietPhieuNhap (
    MaCTPN INT AUTO_INCREMENT PRIMARY KEY,
    MaPN INT,
    MaSP NVARCHAR(10),
    SoLuongNhap INT CHECK (SoLuongNhap > 0),
    DonGiaNhap DECIMAL(15,2) CHECK (DonGiaNhap > 0),
    NgayHetHan DATE COMMENT 'Hạn sử dụng của sản phẩm',
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý phiếu xuất kho
CREATE TABLE PhieuXuatKho (
    MaPXK INT AUTO_INCREMENT PRIMARY KEY,
    MaKho INT,
    MaNVLap NVARCHAR(10) COMMENT 'Nhân viên lập phiếu xuất',
    NgayXuat DATETIME,
    TongSoLuong DECIMAL(15,2),
    LyDoXuat NVARCHAR(255),
    TrangThai INT COMMENT '0=Chờ xử lý, 1=Đã xuất, 2=Từ chối, 3=Hủy',
    IsDeleted BIT DEFAULT 0
);

-- Bảng chi tiết các sản phẩm trong phiếu xuất
CREATE TABLE ChiTietPhieuXuat (
    MaCTPXK INT AUTO_INCREMENT PRIMARY KEY,
    MaPXK INT,
    MaSP NVARCHAR(10),
    SoLuongXuat INT CHECK (SoLuongXuat > 0),
    DonGiaXuat DECIMAL(15,2) CHECK (DonGiaXuat > 0),
    ThanhTien DECIMAL(15,2) CHECK (ThanhTien >= 0),
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý hóa đơn bán hàng
CREATE TABLE HoaDon (
    MaHD INT AUTO_INCREMENT PRIMARY KEY,
    MaKH NVARCHAR(10),
    MaNVLap NVARCHAR(10) COMMENT 'Nhân viên lập hóa đơn',
    MaKM NVARCHAR(10) COMMENT 'Mã khuyến mãi áp dụng',
    NgayLap DATETIME,
    TongTien DECIMAL(15,2),
    MaPTTT NVARCHAR(10),
    TrangThai INT DEFAULT 0 COMMENT '0=Chờ xử lý, 1=Đã thanh toán, 2=Đang xử lý, 3=Hủy',
    NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    NguoiTao NVARCHAR(10),
    NgaySua DATETIME,
    NguoiSua NVARCHAR(10),
    IsDeleted BIT DEFAULT 0,
    INDEX idx_hoadon_ngaylap (NgayLap),
    INDEX idx_hoadon_trangthai (TrangThai)
);

-- Bảng chi tiết các sản phẩm trong hóa đơn
CREATE TABLE ChiTietHoaDon (
    MaCTHD INT AUTO_INCREMENT PRIMARY KEY,
    MaHD INT,
    MaSP NVARCHAR(10),
    SoLuong INT CHECK (SoLuong > 0),
    DonGiaBan DECIMAL(15,2),
    ThanhTien DECIMAL(15,2),
    IsDeleted BIT DEFAULT 0
);

-- Bảng áp dụng khuyến mãi cho sản phẩm
CREATE TABLE KhuyenMaiSanPham (
    MaKMSP INT AUTO_INCREMENT PRIMARY KEY,
    MaKM NVARCHAR(10),
    MaSP NVARCHAR(10),
    IsDeleted BIT DEFAULT 0
);

-- Bảng áp dụng khuyến mãi cho khách hàng
CREATE TABLE KhuyenMaiKhachHang (
    MaKMKH INT AUTO_INCREMENT PRIMARY KEY,
    MaKM NVARCHAR(10),
    MaKH NVARCHAR(10),
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý các giao dịch thanh toán
CREATE TABLE ThanhToan (
    MaTT INT AUTO_INCREMENT PRIMARY KEY,
    MaHD INT,
    MaPTTT NVARCHAR(10),
    SoTienThanhToan DECIMAL(15,2),
    NgayGioTT DATETIME,
    TrangThaiTT INT COMMENT '0=Chờ xử lý, 1=Thành công, 2=Thất bại, 3=Hủy',
    IsDeleted BIT DEFAULT 0
);

-- Bảng quản lý giỏ hàng của khách
CREATE TABLE GioHang (
    MaGH INT AUTO_INCREMENT PRIMARY KEY,
    MaKH NVARCHAR(10),
    MaNV NVARCHAR(10) COMMENT 'Nhân viên hỗ trợ (nếu có)',
    NgayTao DATETIME,
    TrangThai INT DEFAULT 0 COMMENT '0=Đang chọn hàng, 1=Đã đặt hàng, 2=Đã thanh toán, 3=Hủy',
    IsDeleted BIT DEFAULT 0
);

-- Bảng chi tiết sản phẩm trong giỏ hàng
CREATE TABLE ChiTietGioHang (
    MaCTGH INT AUTO_INCREMENT PRIMARY KEY,
    MaGH INT,
    MaSP NVARCHAR(10),
    SoLuong INT CHECK (SoLuong > 0),
    DonGiaHienTai DECIMAL(15,2) CHECK (DonGiaHienTai > 0) COMMENT 'Giá sản phẩm tại thời điểm thêm vào giỏ',
    IsDeleted BIT DEFAULT 0
);

-- Bảng thống kê báo cáo
CREATE TABLE ThongKeBaoCao (
    MaBaoCao INT AUTO_INCREMENT PRIMARY KEY,
    MaCH NVARCHAR(10),
    MaNV NVARCHAR(10) COMMENT 'Nhân viên lập báo cáo',
    LoaiBaoCao NVARCHAR(100) COMMENT 'Loại báo cáo: Doanh thu, Chi phí, Tồn kho, ...',
    SoTien DECIMAL(15,2),
    NgayBaoCao DATETIME,
    NoiDung LONGTEXT,
    IsDeleted BIT DEFAULT 0
);

-- ===================================
-- THÊM DỮ LIỆU MẪU
-- ===================================

-- Thêm dữ liệu mẫu cho bảng chính
-- VaiTro: 0=Admin, 1=QuanLy, 2=NhanVien, 3=KhachHang
INSERT INTO NguoiDung (MaNguoiDung, TenDangNhap, MatKhau, VaiTro) VALUES
('ND00001', 'admin1', 'pass123', 0),
('ND00002', 'quanly1', 'pass123', 1),
('ND00003', 'nhanvien1', 'pass123', 2),
('ND00004', 'nhanvien2', 'pass123', 2),
('ND00005', 'khach1', 'pass123', 3),
('ND00006', 'khach2', 'pass123', 3);

INSERT INTO CuaHang (MaCH, TenCH, DiaChi, SDT) VALUES
('CH00001', N'Cửa Hàng Quận 1', N'123 Lê Lợi, Q1', '0909123456'),
('CH00002', N'Cửa Hàng Quận 3', N'456 Nguyễn Đình Chiểu, Q3', '0911222333'),
('CH00003', N'Cửa Hàng Gò Vấp', N'789 Phan Văn Trị, Gò Vấp', '0933444555'),
('CH00004', N'Cửa Hàng Thủ Đức', N'111 Võ Văn Ngân, Thủ Đức', '0944555666'),
('CH00005', N'Cửa Hàng Tân Bình', N'222 Trường Chinh, Tân Bình', '0955666777');

INSERT INTO NhaCungCap (MaNCC, TenNCC, DiaChi, SDT, ThongTinHopDong) VALUES
('NCC00001', N'Công ty Gạo ST', N'Long An', '0901234567', N'Hợp đồng 2025 Gạo ST'),
('NCC00002', N'C2 Beverage', N'Hà Nội', '0912345678', N'Hợp đồng trà xanh'),
('NCC00003', N'Sunhouse', N'TP HCM', '0923456789', N'Hợp đồng thiết bị'),
('NCC00004', N'Mỹ phẩm Sakura', N'Tokyo, Nhật Bản', '0934567890', N'Hợp đồng làm đẹp'),
('NCC00005', N'May Mặc Nam', N'TP HCM', '0945678901', N'Hợp đồng quần áo nam');

INSERT INTO NhanVien (MaNV, MaNguoiDung, HoTen, SDT, DiaChi, NgaySinh, Luong, MaQuanLy, MaCH) VALUES
('NV00001', 'ND00001', N'Nguyễn Văn A', '0909111222', N'123 Lê Lợi', '1990-01-01', 10000000, NULL, 'CH00001'),
('NV00002', 'ND00002', N'Trần Thị B', '0909333444', N'456 Nguyễn Đình Chiểu', '1992-02-02', 9500000, 'NV00001', 'CH00001'),
('NV00003', 'ND00003', N'Lê Văn C', '0911223344', N'789 Phan Văn Trị', '1993-03-03', 9000000, 'NV00001', 'CH00001'),
('NV00004', 'ND00004', N'Phạm Thị D', '0922334455', N'111 Võ Văn Ngân', '1994-04-04', 9200000, 'NV00001', 'CH00001');

INSERT INTO KhachHang (MaKH, MaNguoiDung, HoTen, SDT, Email, DiaChi, DiemTichLuy) VALUES
('KH00001', 'ND00005', N'Nguyễn Văn KH1', '0988111222', 'kh1@example.com', N'123 Q1', 100),
('KH00002', 'ND00006', N'Trần Thị KH2', '0977223344', 'kh2@example.com', N'456 Q3', 200),
('KH00003', 'ND00005', N'Lê Văn KH3', '0966334455', 'kh3@example.com', N'789 Gò Vấp', 150),
('KH00004', 'ND00006', N'Phạm Thị KH4', '0955445566', 'kh4@example.com', N'111 Thủ Đức', 50),
('KH00005', 'ND00005', N'Đỗ Văn KH5', '0944556677', 'kh5@example.com', N'222 Tân Bình', 300);

INSERT INTO LoaiSanPham (MaLoaiSP, TenLoai) VALUES
('LSP00001', N'Gạo'),
('LSP00002', N'Nước giải khát'),
('LSP00003', N'Đồ điện gia dụng'),
('LSP00004', N'Mỹ phẩm'),
('LSP00005', N'Quần áo');

INSERT INTO SanPham (MaSP, MaLoaiSP, TenSP, MoTa, GiaBan) VALUES
('SP00001', 'LSP00001', N'Gạo ST25', N'Gạo đặc sản ST25', 21000),
('SP00002', 'LSP00002', N'Trà xanh C2', N'Nước giải khát trà xanh', 9500),
('SP00003', 'LSP00003', N'Nồi cơm điện', N'Nồi cơm điện 1.8L', 1200000),
('SP00004', 'LSP00004', N'Sữa rửa mặt', N'Sữa rửa mặt da dầu', 90000),
('SP00005', 'LSP00005', N'Áo thun nam', N'Áo thun cotton nam', 150000);

INSERT INTO KhuyenMai (MaKM, TenChuongTrinh, MoTa, LoaiKM, GiaTriKM, NgayBatDau, NgayKetThuc) VALUES
('KM00001', N'Giảm giá tháng 7', N'Giảm giá cho tất cả mặt hàng', N'PhầnTrăm', 10.0, '2025-07-01', '2025-07-31'),
('KM00002', N'Tặng điểm tích lũy', N'Tặng điểm cho khách hàng VIP', N'Điểm', 50, '2025-07-01', '2025-07-31'),
('KM00003', N'Mua 1 tặng 1', N'Áp dụng cho sản phẩm mỹ phẩm', N'Khác', 0, '2025-07-10', '2025-07-20'),
('KM00004', N'Giảm giá cuối tuần', N'Giảm 20% vào cuối tuần', N'PhầnTrăm', 20.0, '2025-07-12', '2025-07-13'),
('KM00005', N'Miễn phí ship', N'Miễn phí vận chuyển', N'Khác', 0, '2025-07-01', '2025-07-31');

INSERT INTO PhuongThucThanhToan (MaPTTT, TenPTTT, MoTa) VALUES
('PTTT00001', N'Tiền Mặt', N'Thanh toán bằng tiền mặt'),
('PTTT00002', N'Chuyển Khoản', N'Thanh toán qua ngân hàng'),
('PTTT00003', N'MoMo', N'Thanh toán bằng ví điện tử MoMo'),
('PTTT00004', N'ZaloPay', N'Thanh toán qua ZaloPay'),
('PTTT00005', N'Thẻ Tín Dụng', N'Thanh toán bằng thẻ tín dụng');

-- Thêm dữ liệu mẫu cho bảng con (AUTO_INCREMENT)
INSERT INTO Kho (TenKho, DiaChi, MaCH) VALUES
(N'Kho Q1', N'123 Lê Lợi, Q1', 'CH00001'),
(N'Kho Q3', N'456 Nguyễn Đình Chiểu, Q3', 'CH00002'),
(N'Kho Gò Vấp', N'789 Phan Văn Trị, Gò Vấp', 'CH00003'),
(N'Kho Thủ Đức', N'111 Võ Văn Ngân, Thủ Đức', 'CH00004'),
(N'Kho Tân Bình', N'222 Trường Chinh, Tân Bình', 'CH00005');

INSERT INTO CaLamViec (TenCa, GioBatDau, GioKetThuc) VALUES
(N'Sáng', '08:00:00', '12:00:00'),
(N'Chiều', '13:00:00', '17:00:00'),
(N'Tối', '18:00:00', '22:00:00'),
(N'Cả ngày', '08:00:00', '22:00:00'),
(N'Ca đêm', '22:00:00', '06:00:00');

INSERT INTO LichLamViec (MaNV, MaCa, NgayLam, MaNVQuanLy, TrangThai, NgayDuyet, GhiChu) VALUES
('NV00002', 1, '2025-07-12', 'NV00001', 1, '2025-07-11 10:00:00', N'Làm ca sáng'),
('NV00003', 2, '2025-07-12', 'NV00001', 1, '2025-07-11 10:00:00', N'Làm ca chiều'),
('NV00004', 3, '2025-07-12', 'NV00001', 1, '2025-07-11 10:00:00', N'Làm ca tối'),
('NV00002', 4, '2025-07-12', 'NV00001', 0, NULL, N'Đang chờ duyệt'),
('NV00003', 5, '2025-07-13', 'NV00001', 1, '2025-07-11 10:00:00', N'Ca đêm');

-- Chèn hình ảnh mẫu cho tất cả sản phẩm
DELIMITER $$
CREATE PROCEDURE InsertProductImages()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE product_id VARCHAR(10);
    DECLARE product_name VARCHAR(255);
    DECLARE product_cursor CURSOR FOR 
        SELECT MaSP, TenSP FROM SanPham;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN product_cursor;
    read_loop: LOOP
        FETCH product_cursor INTO product_id, product_name;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- Chèn hình chính
        INSERT INTO HinhAnh (MaSP, URL, MoTa, LaChinh, ThuTuHienThi) 
        VALUES (product_id, CONCAT('/images/products/', product_id, '_main.jpg'), 
                CONCAT('Hình chính ', product_name), TRUE, 1);
        
        -- Chèn hình góc nghiêng 1
        INSERT INTO HinhAnh (MaSP, URL, MoTa, LaChinh, ThuTuHienThi) 
        VALUES (product_id, CONCAT('/images/products/', product_id, '_angle1.jpg'), 
                'Góc nghiêng 1', FALSE, 2);
        
        -- Chèn hình góc nghiêng 2
        INSERT INTO HinhAnh (MaSP, URL, MoTa, LaChinh, ThuTuHienThi) 
        VALUES (product_id, CONCAT('/images/products/', product_id, '_angle2.jpg'), 
                'Góc nghiêng 2', FALSE, 3);
                
    END LOOP;
    CLOSE product_cursor;
END$$
DELIMITER ;

-- Thực thi stored procedure để chèn dữ liệu hình ảnh
CALL InsertProductImages();

-- Xóa stored procedure sau khi sử dụng
DROP PROCEDURE InsertProductImages;

INSERT INTO GiaSanPham (MaSP, Gia, NgayBatDau, NgayKetThuc) VALUES
('SP00001', 21000, '2025-07-01', '2025-07-31'),
('SP00002', 9500, '2025-07-01', '2025-07-31'),
('SP00003', 1200000, '2025-07-01', '2025-07-31'),
('SP00004', 90000, '2025-07-01', '2025-07-31'),
('SP00005', 150000, '2025-07-01', '2025-07-31');

INSERT INTO TonKhoChiTiet (MaSP, MaKho, SoLuongTon) VALUES
('SP00001', 1, 100),
('SP00002', 1, 200),
('SP00003', 2, 50),
('SP00004', 2, 80),
('SP00005', 3, 60);

INSERT INTO PhieuNhapHang (MaNCC, MaKho, MaNVLap, NgayNhap, TongTienNhap) VALUES
('NCC00001', 1, 'NV00002', '2025-07-01 09:00:00', 2100000),
('NCC00002', 1, 'NV00003', '2025-07-01 10:00:00', 950000),
('NCC00003', 2, 'NV00004', '2025-07-01 11:00:00', 60000000),
('NCC00004', 2, 'NV00003', '2025-07-01 12:00:00', 7200000),
('NCC00005', 3, 'NV00001', '2025-07-01 13:00:00', 7500000);

INSERT INTO ChiTietPhieuNhap (MaPN, MaSP, SoLuongNhap, DonGiaNhap, NgayHetHan) VALUES
(1, 'SP00001', 100, 21000, '2025-12-31'),
(2, 'SP00002', 100, 9500, '2025-10-15'),
(3, 'SP00003', 50, 1200000, '2027-01-01'),
(4, 'SP00004', 80, 90000, '2025-09-01'),
(5, 'SP00005', 50, 150000, '2025-11-30');

INSERT INTO PhieuXuatKho (MaKho, MaNVLap, NgayXuat, TongSoLuong, LyDoXuat, TrangThai) VALUES
(1, 'NV00002', '2025-07-05 08:00:00', 100, N'Bán hàng', 1),
(1, 'NV00003', '2025-07-06 09:00:00', 50, N'Bán hàng', 1),
(2, 'NV00004', '2025-07-07 10:00:00', 40, N'Chuyển kho', 1),
(2, 'NV00003', '2025-07-08 11:00:00', 60, N'Bán hàng', 1),
(3, 'NV00001', '2025-07-09 12:00:00', 30, N'Sử dụng nội bộ', 1);

INSERT INTO ChiTietPhieuXuat (MaPXK, MaSP, SoLuongXuat, DonGiaXuat, ThanhTien) VALUES
(1, 'SP00001', 40, 21000, 840000),
(2, 'SP00002', 20, 9500, 190000),
(3, 'SP00003', 10, 1200000, 12000000),
(4, 'SP00004', 15, 90000, 1350000),
(5, 'SP00005', 10, 150000, 1500000);

INSERT INTO HoaDon (MaKH, MaNVLap, MaKM, NgayLap, TongTien, MaPTTT, TrangThai) VALUES
('KH00001', 'NV00002', 'KM00001', '2025-07-11 14:00:00', 500000, 'PTTT00001', 1),
('KH00002', 'NV00003', 'KM00002', '2025-07-11 15:00:00', 750000, 'PTTT00002', 1),
('KH00003', 'NV00004', NULL, '2025-07-11 16:00:00', 900000, 'PTTT00001', 2),
('KH00004', 'NV00003', NULL, '2025-07-12 08:30:00', 200000, 'PTTT00002', 3),
('KH00005', 'NV00001', 'KM00003', '2025-07-12 09:45:00', 300000, 'PTTT00001', 1);

INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGiaBan, ThanhTien) VALUES
(1, 'SP00001', 2, 21000, 42000),
(2, 'SP00002', 5, 9500, 47500),
(3, 'SP00003', 1, 1200000, 1200000),
(4, 'SP00004', 2, 90000, 180000),
(5, 'SP00005', 2, 150000, 300000);

INSERT INTO KhuyenMaiSanPham (MaKM, MaSP) VALUES
('KM00001', 'SP00001'),
('KM00001', 'SP00002'),
('KM00003', 'SP00004'),
('KM00004', 'SP00005'),
('KM00002', 'SP00003');

INSERT INTO KhuyenMaiKhachHang (MaKM, MaKH) VALUES
('KM00001', 'KH00001'),
('KM00002', 'KH00002'),
('KM00003', 'KH00003'),
('KM00004', 'KH00004'),
('KM00005', 'KH00005');

INSERT INTO ThanhToan (MaHD, MaPTTT, SoTienThanhToan, NgayGioTT, TrangThaiTT) VALUES
(1, 'PTTT00001', 500000, '2025-07-11 14:05:00', 1),
(2, 'PTTT00002', 750000, '2025-07-11 15:10:00', 1),
(3, 'PTTT00001', 900000, '2025-07-11 16:20:00', 0),
(5, 'PTTT00001', 300000, '2025-07-12 10:00:00', 1),
(4, 'PTTT00002', 200000, '2025-07-12 09:00:00', 3);

INSERT INTO GioHang (MaKH, MaNV, NgayTao, TrangThai) VALUES
('KH00001', 'NV00002', '2025-07-10 10:00:00', 0),
('KH00002', 'NV00003', '2025-07-10 11:00:00', 0),
('KH00003', 'NV00004', '2025-07-10 12:00:00', 1),
('KH00004', 'NV00003', '2025-07-10 13:00:00', 2),
('KH00005', 'NV00001', '2025-07-10 14:00:00', 3);

INSERT INTO ChiTietGioHang (MaGH, MaSP, SoLuong, DonGiaHienTai) VALUES
(1, 'SP00001', 2, 21000),
(2, 'SP00002', 5, 9500),
(3, 'SP00003', 1, 1200000),
(4, 'SP00004', 2, 90000),
(5, 'SP00005', 3, 150000);

INSERT INTO ThongKeBaoCao (MaCH, MaNV, LoaiBaoCao, SoTien, NgayBaoCao, NoiDung) VALUES
('CH00001', 'NV00001', N'Doanh thu', 5000000, '2025-07-10 18:00:00', N'Báo cáo doanh thu Q1'),
('CH00002', 'NV00002', N'Chi phí', 1500000, '2025-07-10 18:30:00', N'Báo cáo chi phí Q3'),
('CH00003', 'NV00003', N'Tồn kho', 2000000, '2025-07-10 19:00:00', N'Báo cáo hàng tồn kho'),
('CH00004', 'NV00004', N'Khuyến mãi', 3000000, '2025-07-10 19:30:00', N'Chi khuyến mãi tháng 7'),
('CH00005', 'NV00002', N'Nhập hàng', 4000000, '2025-07-10 20:00:00', N'Tổng chi nhập hàng');




-- ===================================
-- THÊM CÁC KHÓA NGOẠI (FOREIGN KEYS)
-- ===================================

-- Khóa ngoại cho bảng NhanVien
ALTER TABLE NhanVien ADD CONSTRAINT fk_nhanvien_nguoidung 
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung);

ALTER TABLE NhanVien ADD CONSTRAINT fk_nhanvien_quanly 
    FOREIGN KEY (MaQuanLy) REFERENCES NhanVien(MaNV);

ALTER TABLE NhanVien ADD CONSTRAINT fk_nhanvien_cuahang 
    FOREIGN KEY (MaCH) REFERENCES CuaHang(MaCH);

-- Khóa ngoại cho bảng KhachHang
ALTER TABLE KhachHang ADD CONSTRAINT fk_khachhang_nguoidung 
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung);

-- Khóa ngoại cho bảng SanPham
ALTER TABLE SanPham ADD CONSTRAINT fk_sanpham_loaisanpham 
    FOREIGN KEY (MaLoaiSP) REFERENCES LoaiSanPham(MaLoaiSP);

-- Khóa ngoại cho bảng Kho
ALTER TABLE Kho ADD CONSTRAINT fk_kho_cuahang 
    FOREIGN KEY (MaCH) REFERENCES CuaHang(MaCH);

-- Khóa ngoại cho bảng LichLamViec
ALTER TABLE LichLamViec ADD CONSTRAINT fk_lichlamviec_nhanvien 
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV);

ALTER TABLE LichLamViec ADD CONSTRAINT fk_lichlamviec_ca 
    FOREIGN KEY (MaCa) REFERENCES CaLamViec(MaCa);

ALTER TABLE LichLamViec ADD CONSTRAINT fk_lichlamviec_quanly 
    FOREIGN KEY (MaNVQuanLy) REFERENCES NhanVien(MaNV);

-- Khóa ngoại cho bảng HinhAnh
ALTER TABLE HinhAnh ADD CONSTRAINT fk_hinhanh_sanpham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);

-- Khóa ngoại cho bảng GiaSanPham
ALTER TABLE GiaSanPham ADD CONSTRAINT fk_giasanpham_sanpham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);

-- Khóa ngoại cho bảng TonKhoChiTiet
ALTER TABLE TonKhoChiTiet ADD CONSTRAINT fk_tonkho_sanpham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);

ALTER TABLE TonKhoChiTiet ADD CONSTRAINT fk_tonkho_kho 
    FOREIGN KEY (MaKho) REFERENCES Kho(MaKho);


-- Khóa ngoại cho bảng PhieuNhapHang
ALTER TABLE PhieuNhapHang ADD CONSTRAINT fk_phieunhap_nhacungcap 
    FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC);

ALTER TABLE PhieuNhapHang ADD CONSTRAINT fk_phieunhap_kho 
    FOREIGN KEY (MaKho) REFERENCES Kho(MaKho);

ALTER TABLE PhieuNhapHang ADD CONSTRAINT fk_phieunhap_nhanvien 
    FOREIGN KEY (MaNVLap) REFERENCES NhanVien(MaNV);


-- Khóa ngoại cho bảng ChiTietPhieuNhap
ALTER TABLE ChiTietPhieuNhap ADD CONSTRAINT fk_chitietphieunhap_phieunhap 
    FOREIGN KEY (MaPN) REFERENCES PhieuNhapHang(MaPN);

ALTER TABLE ChiTietPhieuNhap ADD CONSTRAINT fk_chitietphieunhap_sanpham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);

-- Khóa ngoại cho bảng PhieuXuatKho
ALTER TABLE PhieuXuatKho ADD CONSTRAINT fk_phieuxuat_kho 
    FOREIGN KEY (MaKho) REFERENCES Kho(MaKho);

ALTER TABLE PhieuXuatKho ADD CONSTRAINT fk_phieuxuat_nhanvien 
    FOREIGN KEY (MaNVLap) REFERENCES NhanVien(MaNV);

-- Khóa ngoại cho bảng ChiTietPhieuXuat
ALTER TABLE ChiTietPhieuXuat ADD CONSTRAINT fk_chitietphieuxuat_phieuxuat 
    FOREIGN KEY (MaPXK) REFERENCES PhieuXuatKho(MaPXK);

ALTER TABLE ChiTietPhieuXuat ADD CONSTRAINT fk_chitietphieuxuat_sanpham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);

-- Khóa ngoại cho bảng HoaDon
ALTER TABLE HoaDon ADD CONSTRAINT fk_hoadon_khachhang 
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH);

ALTER TABLE HoaDon ADD CONSTRAINT fk_hoadon_nhanvien 
    FOREIGN KEY (MaNVLap) REFERENCES NhanVien(MaNV);

ALTER TABLE HoaDon ADD CONSTRAINT fk_hoadon_khuyenmai 
    FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM);

ALTER TABLE HoaDon ADD CONSTRAINT fk_hoadon_phuongthucthanhtoan 
    FOREIGN KEY (MaPTTT) REFERENCES PhuongThucThanhToan(MaPTTT);

-- Khóa ngoại cho bảng ChiTietHoaDon
ALTER TABLE ChiTietHoaDon ADD CONSTRAINT fk_chitiethoadon_hoadon 
    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD);

ALTER TABLE ChiTietHoaDon ADD CONSTRAINT fk_chitiethoadon_sanpham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);


-- Khóa ngoại cho bảng KhuyenMaiSanPham
ALTER TABLE KhuyenMaiSanPham ADD CONSTRAINT fk_khuyenmaisanpham_khuyenmai 
    FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM);

ALTER TABLE KhuyenMaiSanPham ADD CONSTRAINT fk_khuyenmaisanpham_sanpham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);

-- Khóa ngoại cho bảng KhuyenMaiKhachHang
ALTER TABLE KhuyenMaiKhachHang ADD CONSTRAINT fk_khuyenmaikhachhang_khuyenmai 
    FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM);

ALTER TABLE KhuyenMaiKhachHang ADD CONSTRAINT fk_khuyenmaikhachhang_khachhang 
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH);

-- Khóa ngoại cho bảng ThanhToan
ALTER TABLE ThanhToan ADD CONSTRAINT fk_thanhtoan_hoadon 
    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD);

ALTER TABLE ThanhToan ADD CONSTRAINT fk_thanhtoan_phuongthucthanhtoan 
    FOREIGN KEY (MaPTTT) REFERENCES PhuongThucThanhToan(MaPTTT);

-- Khóa ngoại cho bảng GioHang
ALTER TABLE GioHang ADD CONSTRAINT fk_giohang_khachhang 
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH);

ALTER TABLE GioHang ADD CONSTRAINT fk_giohang_nhanvien 
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV);

-- Khóa ngoại cho bảng ChiTietGioHang
ALTER TABLE ChiTietGioHang ADD CONSTRAINT fk_chitietgiohang_giohang 
    FOREIGN KEY (MaGH) REFERENCES GioHang(MaGH);

ALTER TABLE ChiTietGioHang ADD CONSTRAINT fk_chitietgiohang_sanpham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);

-- Khóa ngoại cho bảng ThongKeBaoCao
ALTER TABLE ThongKeBaoCao ADD CONSTRAINT fk_thongkebaocao_cuahang 
    FOREIGN KEY (MaCH) REFERENCES CuaHang(MaCH);
    
ALTER TABLE ThongKeBaoCao ADD CONSTRAINT fk_thongkebaocao_nhanvien 
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV);

