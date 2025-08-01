CREATE DATABASE IF NOT EXISTS QuanLySieuThi;
USE QuanLySieuThi;
drop database QuanLySieuThi;

-- Bảng quản lý thông tin người dùng hệ thống
CREATE TABLE NguoiDung (
    MaNguoiDung NVARCHAR(10) PRIMARY KEY,
    Email NVARCHAR(50),
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
    Email NVARCHAR(100),
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
    MaQuanLy NVARCHAR(10) COMMENT 'Nhân viên quản lý phụ trách khuyến mãi',
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
        VALUES (product_id, CONCAT( product_id, '_main.jfif'
                CONCAT('Hình chính ', product_name), TRUE, 1);
        
        -- Chèn hình góc nghiêng 1
        INSERT INTO HinhAnh (MaSP, URL, MoTa, LaChinh, ThuTuHienThi) 
        VALUES (product_id, CONCAT( product_id, '_main1.jfif'
                'Góc nghiêng 1', FALSE, 2);
        
        -- Chèn hình góc nghiêng 2
        INSERT INTO HinhAnh (MaSP, URL, MoTa, LaChinh, ThuTuHienThi) 
        VALUES (product_id, CONCAT( product_id, '_main2.jfif'), 
                'Góc nghiêng 2', FALSE, 3);
                
    END LOOP;
    CLOSE product_cursor;
END$$
DELIMITER ;

-- Thực thi stored procedure để chèn dữ liệu hình ảnh
CALL InsertProductImages();

-- Xóa stored procedure sau khi sử dụng
-- DROP PROCEDURE InsertProductImages;

INSERT INTO GiaSanPham (MaSP, Gia, NgayBatDau, NgayKetThuc) VALUES
-- LSP001 – Tươi sống --
('SP001', 395693.53, '2025-10-04', '2025-10-24'),
('SP002', 147656.27, '2025-02-08', '2025-02-16'),
('SP003', 380553.72, '2025-03-02', '2025-03-20'),
('SP004', 268890.93, '2025-01-21', '2025-02-12'),
('SP005', 70635.8, '2025-09-20', '2025-10-10'),
('SP006', 6549.51, '2025-07-28', '2025-08-20'),
('SP007', 45187.22, '2025-10-06', '2025-10-21'),
('SP008', 400174.44, '2025-07-03', '2025-07-21'),
('SP009', 334548.29, '2025-04-24', '2025-05-10'),
('SP010', 199673.71, '2025-08-22', '2025-09-11'),
('SP011', 397024.64, '2025-01-21', '2025-02-19'),
('SP012', 482174.68, '2025-02-23', '2025-03-15'),
('SP013', 439572.74, '2025-07-27', '2025-08-06'),
('SP014', 112531.39, '2025-03-01', '2025-03-30'),
('SP015', 261726.98, '2025-09-16', '2025-10-04'),
('SP016', 446607.53, '2025-06-29', '2025-07-06'),
('SP017', 280513.61, '2025-10-04', '2025-10-22'),
('SP018', 18750.33, '2025-05-23', '2025-06-05'),
('SP019', 330681.59, '2025-11-12', '2025-11-21'),
('SP020', 477662.81, '2025-05-30', '2025-06-27'),

-- LSP002 – Đông lạnh --
('SP021', 27000, '2025-06-01', '2025-06-30'),
('SP022', 52000, '2025-07-05', '2025-07-25'),
('SP023', 48000, '2025-07-10', '2025-08-10'),
('SP024', 32000, '2025-08-01', '2025-08-31'),
('SP025', 49000, '2025-06-15', '2025-07-15'),
('SP026', 58000, '2025-07-20', '2025-08-20'),
('SP027', 51000, '2025-05-25', '2025-06-25'),
('SP028', 33000, '2025-06-10', '2025-07-10'),
('SP029', 47000, '2025-07-01', '2025-07-31'),
('SP030', 56000, '2025-09-01', '2025-09-30'),
('SP031', 29000, '2025-08-15', '2025-09-15'),
('SP032', 61000, '2025-07-18', '2025-08-18'),
('SP033', 46000, '2025-06-20', '2025-07-20'),
('SP034', 44000, '2025-05-10', '2025-06-10'),
('SP035', 55000, '2025-09-05', '2025-10-05'),
('SP036', 43000, '2025-07-07', '2025-08-07'),
('SP037', 62000, '2025-08-10', '2025-09-10'),
('SP038', 45000, '2025-07-15', '2025-08-15'),
('SP039', 49000, '2025-07-25', '2025-08-25'),
('SP040', 60000, '2025-06-01', '2025-06-30'),

-- LSP003 – Đồ đóng hộp --
('SP041', 18000, '2025-05-01', '2025-05-31'),
('SP042', 22000, '2025-06-10', '2025-07-10'),
('SP043', 25000, '2025-07-01', '2025-07-31'),
('SP044', 30000, '2025-07-20', '2025-08-20'),
('SP045', 21000, '2025-08-01', '2025-08-31'),
('SP046', 26000, '2025-08-10', '2025-09-10'),
('SP047', 23000, '2025-09-01', '2025-09-30'),
('SP048', 27000, '2025-06-15', '2025-07-15'),
('SP049', 20000, '2025-05-25', '2025-06-25'),
('SP050', 24000, '2025-06-01', '2025-06-30'),
('SP051', 19500, '2025-07-05', '2025-08-05'),
('SP052', 25500, '2025-07-12', '2025-08-12'),
('SP053', 27500, '2025-08-18', '2025-09-18'),
('SP054', 28500, '2025-07-25', '2025-08-25'),
('SP055', 29500, '2025-08-05', '2025-09-05'),
('SP056', 22500, '2025-06-01', '2025-07-01'),
('SP057', 23500, '2025-07-10', '2025-08-10'),
('SP058', 24500, '2025-05-10', '2025-06-10'),
('SP059', 26500, '2025-06-20', '2025-07-20'),
('SP060', 28000, '2025-09-01', '2025-09-30'),

-- LSP004 – Đồ uống -- 
('SP061', 10000, '2025-07-01', '2025-07-31'),
('SP062', 11000, '2025-07-03', '2025-08-03'),
('SP063', 12000, '2025-06-20', '2025-07-20'),
('SP064', 8500,  '2025-08-01', '2025-08-31'),
('SP065', 9500,  '2025-08-05', '2025-09-05'),
('SP066', 13000, '2025-07-12', '2025-08-12'),
('SP067', 14000, '2025-06-01', '2025-06-30'),
('SP068', 15000, '2025-05-15', '2025-06-15'),
('SP069', 16000, '2025-06-18', '2025-07-18'),
('SP070', 17000, '2025-07-07', '2025-08-07'),
('SP071', 11500, '2025-08-15', '2025-09-15'),
('SP072', 12500, '2025-07-25', '2025-08-25'),
('SP073', 13500, '2025-06-25', '2025-07-25'),
('SP074', 14500, '2025-05-10', '2025-06-10'),
('SP075', 15500, '2025-09-01', '2025-09-30'),
('SP076', 16500, '2025-07-20', '2025-08-20'),
('SP077', 17500, '2025-08-10', '2025-09-10'),
('SP078', 18500, '2025-06-05', '2025-07-05'),
('SP079', 19500, '2025-05-01', '2025-05-31'),
('SP080', 20500, '2025-06-15', '2025-07-15'),

-- LSP005 – Sữa & em bé --
('SP081', 26000, '2025-06-01', '2025-06-30'),
('SP082', 31000, '2025-06-15', '2025-07-15'),
('SP083', 45000, '2025-07-01', '2025-07-31'),
('SP084', 52000, '2025-08-01', '2025-08-31'),
('SP085', 68000, '2025-07-10', '2025-08-10'),
('SP086', 73000, '2025-05-20', '2025-06-20'),
('SP087', 81000, '2025-06-10', '2025-07-10'),
('SP088', 90000, '2025-08-15', '2025-09-15'),
('SP089', 97000, '2025-09-01', '2025-09-30'),
('SP090', 105000, '2025-06-25', '2025-07-25'),
('SP091', 27500, '2025-07-12', '2025-08-12'),
('SP092', 36000, '2025-07-20', '2025-08-20'),
('SP093', 41500, '2025-08-10', '2025-09-10'),
('SP094', 47000, '2025-06-05', '2025-07-05'),
('SP095', 52000, '2025-05-10', '2025-06-10'),
('SP096', 63000, '2025-08-01', '2025-08-31'),
('SP097', 74000, '2025-06-01', '2025-07-01'),
('SP098', 82000, '2025-07-05', '2025-08-05'),
('SP099', 91000, '2025-07-25', '2025-08-25'),
('SP100', 98000, '2025-09-01', '2025-09-30'),

-- LSP006 – Gia vị & Dầu ăn 
('SP101', 12000, '2025-07-01', '2025-07-31'),
('SP102', 18000, '2025-06-10', '2025-07-10'),
('SP103', 24000, '2025-07-20', '2025-08-20'),
('SP104', 29000, '2025-08-01', '2025-08-31'),
('SP105', 35000, '2025-07-10', '2025-08-10'),
('SP106', 40000, '2025-05-01', '2025-05-31'),
('SP107', 46000, '2025-06-01', '2025-06-30'),
('SP108', 51000, '2025-09-01', '2025-09-30'),
('SP109', 57000, '2025-07-05', '2025-08-05'),
('SP110', 62000, '2025-08-15', '2025-09-15'),
('SP111', 13500, '2025-06-01', '2025-07-01'),
('SP112', 18500, '2025-05-20', '2025-06-20'),
('SP113', 25000, '2025-07-12', '2025-08-12'),
('SP114', 31000, '2025-08-01', '2025-09-01'),
('SP115', 37000, '2025-06-18', '2025-07-18'),
('SP116', 42000, '2025-07-25', '2025-08-25'),
('SP117', 48000, '2025-09-01', '2025-09-30'),
('SP118', 53000, '2025-06-15', '2025-07-15'),
('SP119', 59000, '2025-05-01', '2025-06-01'),
('SP120', 64000, '2025-07-05', '2025-08-05'),

-- LSP007 – Hóa phẩm & Tẩy rửa -- 
('SP121', 18000, '2025-07-01', '2025-07-31'),
('SP122', 23000, '2025-06-01', '2025-06-30'),
('SP123', 28000, '2025-08-01', '2025-08-31'),
('SP124', 33000, '2025-07-20', '2025-08-20'),
('SP125', 38000, '2025-09-01', '2025-09-30'),
('SP126', 43000, '2025-06-10', '2025-07-10'),
('SP127', 48000, '2025-05-15', '2025-06-15'),
('SP128', 53000, '2025-07-01', '2025-08-01'),
('SP129', 58000, '2025-06-25', '2025-07-25'),
('SP130', 63000, '2025-08-10', '2025-09-10'),
('SP131', 19000, '2025-05-01', '2025-06-01'),
('SP132', 24000, '2025-07-05', '2025-08-05'),
('SP133', 29000, '2025-08-01', '2025-08-31'),
('SP134', 34000, '2025-06-20', '2025-07-20'),
('SP135', 39000, '2025-07-10', '2025-08-10'),
('SP136', 44000, '2025-05-10', '2025-06-10'),
('SP137', 49000, '2025-06-01', '2025-07-01'),
('SP138', 54000, '2025-07-15', '2025-08-15'),
('SP139', 59000, '2025-08-05', '2025-09-05'),
('SP140', 64000, '2025-09-01', '2025-09-30');

INSERT INTO TonKhoChiTiet (MaSP, MaKho, SoLuongTon) VALUES
-- Kho CH001 (MaKho = 1)
('SP001', 1, 100),
('SP002', 1, 200),
('SP003', 1, 150),
('SP004', 1, 80),
('SP005', 1, 90),
('SP006', 1, 120),
('SP007', 1, 60),
('SP008', 1, 75),
('SP009', 1, 110),
('SP010', 1, 50),
('SP011', 1, 100),
('SP012', 1, 130),
('SP013', 1, 70),
('SP014', 1, 85),
('SP015', 1, 95),
('SP016', 1, 140),
('SP017', 1, 60),
('SP018', 1, 80),
('SP019', 1, 150),
('SP020', 1, 200),
('SP021', 1, 90),
('SP022', 1, 140),
('SP023', 1, 120),
('SP024', 1, 130),
('SP025', 1, 60),
('SP026', 1, 75),
('SP027', 1, 100),
('SP028', 1, 180),
('SP029', 1, 160),
('SP030', 1, 50),
('SP031', 1, 70),
('SP032', 1, 100),
('SP033', 1, 90),
('SP034', 1, 60),
('SP035', 1, 95),
('SP036', 1, 150),
('SP037', 1, 200),
('SP038', 1, 80),
('SP039', 1, 110),
('SP040', 1, 100),
('SP041', 1, 120),
('SP042', 1, 60),
('SP043', 1, 150),
('SP044', 1, 200),
('SP045', 1, 170),
('SP046', 1, 90),
('SP047', 1, 60),
('SP048', 1, 130),
('SP049', 1, 80),
('SP050', 1, 140),
('SP051', 1, 100),
('SP052', 1, 60),
('SP053', 1, 90),
('SP054', 1, 75),
('SP055', 1, 130),
('SP056', 1, 100),
('SP057', 1, 60),
('SP058', 1, 95),
('SP059', 1, 200),
('SP060', 1, 110),
('SP061', 1, 80),
('SP062', 1, 70),
('SP063', 1, 150),
('SP064', 1, 100),
('SP065', 1, 140),
('SP066', 1, 90),
('SP067', 1, 50),
('SP068', 1, 170),
('SP069', 1, 120),
('SP070', 1, 180),
('SP071', 1, 60),
('SP072', 1, 130),
('SP073', 1, 140),
('SP074', 1, 90),
('SP075', 1, 150),
('SP076', 1, 80),
('SP077', 1, 160),
('SP078', 1, 60),
('SP079', 1, 95),
('SP080', 1, 100),
('SP081', 1, 110),
('SP082', 1, 60),
('SP083', 1, 75),
('SP084', 1, 100),
('SP085', 1, 130),
('SP086', 1, 150),
('SP087', 1, 90),
('SP088', 1, 200),
('SP089', 1, 140),
('SP090', 1, 80),
('SP091', 1, 95),
('SP092', 1, 60),
('SP093', 1, 100),
('SP094', 1, 50),
('SP095', 1, 110),
('SP096', 1, 130),
('SP097', 1, 70),
('SP098', 1, 180),
('SP099', 1, 150),
('SP100', 1, 120),
('SP101', 1, 60),
('SP102', 1, 90),
('SP103', 1, 100),
('SP104', 1, 140),
('SP105', 1, 80),
('SP106', 1, 130),
('SP107', 1, 60),
('SP108', 1, 150),
('SP109', 1, 95),
('SP110', 1, 200),
('SP111', 1, 100),
('SP112', 1, 160),
('SP113', 1, 140),
('SP114', 1, 120),
('SP115', 1, 80),
('SP116', 1, 90),
('SP117', 1, 75),
('SP118', 1, 100),
('SP119', 1, 130),
('SP120', 1, 180),
('SP121', 1, 140),
('SP122', 1, 200),
('SP123', 1, 90),
('SP124', 1, 60),
('SP125', 1, 95),
('SP126', 1, 80),
('SP127', 1, 100),
('SP128', 1, 70),
('SP129', 1, 60),
('SP130', 1, 110),
('SP131', 1, 150),
('SP132', 1, 100),
('SP133', 1, 90),
('SP134', 1, 50),
('SP135', 1, 80),
('SP136', 1, 130),
('SP137', 1, 120),
('SP138', 1, 140),
('SP139', 1, 160),
('SP140', 1, 200),

-- Kho CH002 (MaKho = 2)
('SP001', 2, 90),
('SP002', 2, 150),
('SP003', 2, 130),
('SP004', 2, 70),
('SP005', 2, 100),
('SP006', 2, 140),
('SP007', 2, 60),
('SP008', 2, 75),
('SP009', 2, 120),
('SP010', 2, 50),
('SP011', 2, 100),
('SP012', 2, 120),
('SP013', 2, 90),
('SP014', 2, 80),
('SP015', 2, 95),
('SP016', 2, 130),
('SP017', 2, 70),
('SP018', 2, 85),
('SP019', 2, 160),
('SP020', 2, 200),
('SP021', 2, 90),
('SP022', 2, 130),
('SP023', 2, 110),
('SP024', 2, 100),
('SP025', 2, 60),
('SP026', 2, 70),
('SP027', 2, 140),
('SP028', 2, 180),
('SP029', 2, 160),
('SP030', 2, 55),
('SP031', 2, 80),
('SP032', 2, 95),
('SP033', 2, 90),
('SP034', 2, 60),
('SP035', 2, 100),
('SP036', 2, 150),
('SP037', 2, 200),
('SP038', 2, 85),
('SP039', 2, 120),
('SP040', 2, 100),
('SP041', 2, 110),
('SP042', 2, 65),
('SP043', 2, 150),
('SP044', 2, 190),
('SP045', 2, 170),
('SP046', 2, 95),
('SP047', 2, 60),
('SP048', 2, 130),
('SP049', 2, 75),
('SP050', 2, 130),
('SP051', 2, 95),
('SP052', 2, 70),
('SP053', 2, 90),
('SP054', 2, 75),
('SP055', 2, 140),
('SP056', 2, 100),
('SP057', 2, 70),
('SP058', 2, 95),
('SP059', 2, 200),
('SP060', 2, 110),
('SP061', 2, 90),
('SP062', 2, 80),
('SP063', 2, 150),
('SP064', 2, 100),
('SP065', 2, 130),
('SP066', 2, 95),
('SP067', 2, 55),
('SP068', 2, 170),
('SP069', 2, 120),
('SP070', 2, 180),
('SP071', 2, 60),
('SP072', 2, 140),
('SP073', 2, 130),
('SP074', 2, 95),
('SP075', 2, 150),
('SP076', 2, 85),
('SP077', 2, 150),
('SP078', 2, 70),
('SP079', 2, 95),
('SP080', 2, 100),
('SP081', 2, 120),
('SP082', 2, 60),
('SP083', 2, 75),
('SP084', 2, 105),
('SP085', 2, 130),
('SP086', 2, 140),
('SP087', 2, 90),
('SP088', 2, 200),
('SP089', 2, 140),
('SP090', 2, 85),
('SP091', 2, 95),
('SP092', 2, 60),
('SP093', 2, 110),
('SP094', 2, 50),
('SP095', 2, 120),
('SP096', 2, 130),
('SP097', 2, 75),
('SP098', 2, 170),
('SP099', 2, 150),
('SP100', 2, 120),
('SP101', 2, 65),
('SP102', 2, 95),
('SP103', 2, 100),
('SP104', 2, 130),
('SP105', 2, 85),
('SP106', 2, 130),
('SP107', 2, 65),
('SP108', 2, 150),
('SP109', 2, 100),
('SP110', 2, 200),
('SP111', 2, 100),
('SP112', 2, 150),
('SP113', 2, 130),
('SP114', 2, 120),
('SP115', 2, 80),
('SP116', 2, 95),
('SP117', 2, 75),
('SP118', 2, 110),
('SP119', 2, 130),
('SP120', 2, 180),
('SP121', 2, 140),
('SP122', 2, 190),
('SP123', 2, 95),
('SP124', 2, 60),
('SP125', 2, 100),
('SP126', 2, 80),
('SP127', 2, 100),
('SP128', 2, 75),
('SP129', 2, 60),
('SP130', 2, 110),
('SP131', 2, 150),
('SP132', 2, 100),
('SP133', 2, 90),
('SP134', 2, 55),
('SP135', 2, 80),
('SP136', 2, 130),
('SP137', 2, 120),
('SP138', 2, 140),
('SP139', 2, 150),
('SP140', 2, 190);

INSERT INTO PhieuNhapHang (MaNCC, MaKho, MaNVLap, NgayNhap, TongTienNhap) VALUES
('NCC001', 1, 'NV002', '2025-07-01 09:00:00', 5500000), -- LSP001
('NCC002', 1, 'NV003', '2025-07-01 10:00:00', 2800000), -- LSP002
('NCC003', 2, 'NV004', '2025-07-01 11:00:00', 80000000), -- LSP003
('NCC004', 2, 'NV003', '2025-07-01 12:00:00', 15000000), -- LSP004
('NCC005', 2, 'NV001', '2025-07-01 13:00:00', 22000000), -- LSP005
('NCC006', 1, 'NV002', '2025-07-02 09:00:00', 9000000),  -- LSP006
('NCC007', 2, 'NV003', '2025-07-02 10:00:00', 12000000); -- LSP007

INSERT INTO ChiTietPhieuNhap (MaPN, MaSP, SoLuongNhap, DonGiaNhap, NgayHetHan) VALUES
-- NCC001
(1, 'SP001', 100, 21000, '2025-12-31'),
(1, 'SP002', 200, 9500,  '2025-11-30'),

-- NCC002
(2, 'SP003', 50,  1200000, '2027-01-01'),
(2, 'SP004', 80,  90000,   '2025-09-01'),

-- NCC003
(3, 'SP005', 50,  150000,  '2025-11-30'),
(3, 'SP006', 70,  50000,   '2025-12-15'),
(3, 'SP007', 60,  45000,   '2025-11-20'),

-- NCC004
(4, 'SP008', 100, 60000,   '2025-10-10'),
(4, 'SP009', 90,  45000,   '2025-12-31'),

-- NCC005
(5, 'SP010', 150, 75000,   '2025-12-31'),
(5, 'SP011', 120, 65000,   '2025-09-20'),
(5, 'SP012', 80,  80000,   '2026-01-15'),

-- NCC006
(6, 'SP013', 100, 50000,   '2025-12-01'),
(6, 'SP014', 110, 60000,   '2025-11-25'),

-- NCC007
(7, 'SP015', 70,  45000,   '2025-10-30'),
(7, 'SP016', 90,  70000,   '2025-09-15'),
(7, 'SP017', 100, 80000,   '2025-12-31');

INSERT INTO PhieuXuatKho (MaKho, MaNVLap, NgayXuat, TongSoLuong, LyDoXuat, TrangThai) VALUES
(1, 'NV002', '2025-07-05 08:00:00', 180, N'Bán hàng', 1),
(1, 'NV003', '2025-07-06 09:00:00', 200, N'Bán hàng', 1),
(2, 'NV005', '2025-07-07 10:00:00', 140, N'Chuyển kho', 1),
(2, 'NV006', '2025-07-08 11:00:00', 150, N'Bán hàng', 1),
(1, 'NV007', '2025-07-09 12:00:00', 100, N'Sử dụng nội bộ', 1);

INSERT INTO ChiTietPhieuXuat (MaPXK, MaSP, SoLuongXuat, DonGiaXuat, ThanhTien) VALUES
-- PXK 1: xuất các SP001, SP002, SP003
(1, 'SP001', 40, 21000, 840000),
(1, 'SP002', 60, 9500, 570000),
(1, 'SP003', 30, 1200000, 36000000),

-- PXK 2: xuất các SP004, SP005, SP006, SP007
(2, 'SP004', 40, 90000, 3600000),
(2, 'SP005', 30, 150000, 4500000),
(2, 'SP006', 50, 50000, 2500000),
(2, 'SP007', 80, 45000, 3600000),

-- PXK 3: xuất các SP008, SP009, SP010
(3, 'SP008', 40, 60000, 2400000),
(3, 'SP009', 50, 30000, 1500000),
(3, 'SP010', 50, 28000, 1400000),

-- PXK 4: xuất các SP011, SP012, SP013, SP014
(4, 'SP011', 50, 100000, 5000000),
(4, 'SP012', 40, 95000, 3800000),
(4, 'SP013', 30, 110000, 3300000),
(4, 'SP014', 30, 70000, 2100000),

-- PXK 5: xuất các SP015, SP016, SP017
(5, 'SP015', 30, 65000, 1950000),
(5, 'SP016', 40, 90000, 3600000),
(5, 'SP017', 30, 85000, 2550000);

INSERT INTO HoaDon (MaKH, MaNVLap, MaKM, NgayLap, TongTien, MaPTTT, TrangThai) VALUES
('KH001', 'NV002', 'KMSP001', '2025-07-11 14:00:00', 500000, 'PTTT001', 1),
('KH002', 'NV003', 'KMSP002', '2025-07-11 15:00:00', 750000, 'PTTT002', 1),
('KH003', 'NV005', NULL, '2025-07-11 16:00:00', 900000, 'PTTT001', 2),
('KH004', 'NV006', NULL, '2025-07-12 08:30:00', 200000, 'PTTT002', 3),
('KH005', 'NV001', 'KMSP003', '2025-07-12 09:45:00', 300000, 'PTTT001', 1),
('KH006', 'NV002', 'KMSP004', '2025-07-13 10:00:00', 400000, 'PTTT001', 1),
('KH007', 'NV003', NULL, '2025-07-13 11:00:00', 350000, 'PTTT002', 1),
('KH008', 'NV004', 'KMSP005', '2025-07-13 12:00:00', 450000, 'PTTT001', 1),
('KH009', 'NV005', NULL, '2025-07-13 13:00:00', 500000, 'PTTT002', 1),
('KH010', 'NV006', 'KMSP006', '2025-07-13 14:00:00', 600000, 'PTTT001', 1);

INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGiaBan, ThanhTien) VALUES
(1, 'SP001', 2, 21000, 42000),
(2, 'SP002', 5, 9500, 47500),
(3, 'SP003', 1, 1200000, 1200000),
(4, 'SP004', 2, 90000, 180000),
(5, 'SP005', 2, 150000, 300000),
(6, 'SP006', 4, 18000, 72000),
(7, 'SP007', 3, 25000, 75000),
(8, 'SP008', 5, 30000, 150000),
(9, 'SP009', 2, 40000, 80000),
(10, 'SP010', 1, 60000, 60000);

INSERT INTO KhuyenMaiSanPham (MaKM, MaSP) VALUES
('KMSP001', 'SP001'),
('KMSP001', 'SP002'),
('KMSP003', 'SP004'),
('KMSP004', 'SP005'),
('KMSP002', 'SP003'),
('KMSP004', 'SP006'),
('KMSP005', 'SP007'),
('KMSP005', 'SP008'),
('KMSP006', 'SP009'),
('KMSP006', 'SP010');

INSERT INTO KhuyenMaiKhachHang (MaKM, MaKH) VALUES
('KMKH001', 'KH001'),
('KMKH002', 'KH002'),
('KMKH003', 'KH003'),
('KMKH004', 'KH004'),
('KMKH005', 'KH005'),
('KMKH004', 'KH006'),
('KMKH005', 'KH007'),
('KMKH005', 'KH008'),
('KMKH006', 'KH009'),
('KMKH006', 'KH010');

INSERT INTO ThanhToan (MaHD, MaPTTT, SoTienThanhToan, NgayGioTT, TrangThaiTT) VALUES
(1, 'PTTT001', 500000, '2025-07-11 14:05:00', 1),
(2, 'PTTT002', 750000, '2025-07-11 15:10:00', 1),
(3, 'PTTT001', 900000, '2025-07-11 16:20:00', 0),
(5, 'PTTT001', 300000, '2025-07-12 10:00:00', 1),
(4, 'PTTT002', 200000, '2025-07-12 09:00:00', 3),
(6, 'PTTT001', 400000, '2025-07-13 10:10:00', 1),
(7, 'PTTT002', 350000, '2025-07-13 11:10:00', 1),
(8, 'PTTT001', 450000, '2025-07-13 12:15:00', 1),
(9, 'PTTT002', 500000, '2025-07-13 13:15:00', 1),
(10, 'PTTT001', 600000, '2025-07-13 14:15:00', 1);

INSERT INTO GioHang (MaKH, MaNV, NgayTao, TrangThai) VALUES
('KH001', 'NV002', '2025-07-10 10:00:00', 0),
('KH002', 'NV003', '2025-07-10 11:00:00', 0),
('KH003', 'NV005', '2025-07-10 12:00:00', 1),
('KH004', 'NV006', '2025-07-10 13:00:00', 2),
('KH005', 'NV001', '2025-07-10 14:00:00', 3),
('KH006', 'NV002', '2025-07-11 09:00:00', 0),
('KH007', 'NV003', '2025-07-11 09:30:00', 1),
('KH008', 'NV004', '2025-07-11 10:00:00', 2),
('KH009', 'NV005', '2025-07-11 10:30:00', 3),
('KH010', 'NV006', '2025-07-11 11:00:00', 0);

INSERT INTO ChiTietGioHang (MaGH, MaSP, SoLuong, DonGiaHienTai) VALUES
(1, 'SP001', 2, 21000),
(2, 'SP002', 5, 9500),
(3, 'SP003', 1, 1200000),
(4, 'SP004', 2, 90000),
(5, 'SP005', 3, 150000),
(6, 'SP006', 2, 18000),
(7, 'SP007', 2, 25000),
(8, 'SP008', 4, 30000),
(9, 'SP009', 1, 40000),
(10, 'SP010', 3, 60000);

INSERT INTO ThongKeBaoCao (MaCH, MaNV, LoaiBaoCao, SoTien, NgayBaoCao, NoiDung) VALUES
('CH001', 'NV001', N'Doanh thu', 5000000, '2025-07-10 18:00:00', N'Báo cáo doanh thu Q1'),
('CH002', 'NV008', N'Chi phí', 1500000, '2025-07-10 18:30:00', N'Báo cáo chi phí Q3');

-- ===================================
-- THÊM CÁC KHÓA NGOẠI (FOREIGN KEYS)
-- ===================================

-- Khóa ngoại cho bảng NhanVien
ALTER TABLE NhanVien ADD CONSTRAINT Fk_NhanVien_NguoiDung 
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung);

ALTER TABLE NhanVien ADD CONSTRAINT Fk_NhanVien_QuanLy 
    FOREIGN KEY (MaQuanLy) REFERENCES NhanVien(MaNV);

ALTER TABLE NhanVien ADD CONSTRAINT Fk_NhanVien_CuaHang 
    FOREIGN KEY (MaCH) REFERENCES CuaHang(MaCH);

-- Khóa ngoại cho bảng KhachHang
ALTER TABLE KhachHang ADD CONSTRAINT Fk_KhachHang_NguoiDung 
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung);

-- Khóa ngoại cho bảng SanPham
ALTER TABLE SanPham ADD CONSTRAINT Fk_SanPham_LoaiSanPham 
    FOREIGN KEY (MaLoaiSP) REFERENCES LoaiSanPham(MaLoaiSP);

-- Khóa ngoại cho bảng Kho
ALTER TABLE Kho ADD CONSTRAINT Fk_Kho_CuaHang 
    FOREIGN KEY (MaCH) REFERENCES CuaHang(MaCH);

-- Khóa ngoại cho bảng LichLamViec
ALTER TABLE LichLamViec ADD CONSTRAINT Fk_LichLamViec_NhanVien 
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV);

ALTER TABLE LichLamViec ADD CONSTRAINT Fk_LichLamViec_Ca 
    FOREIGN KEY (MaCa) REFERENCES CaLamViec(MaCa);

ALTER TABLE LichLamViec ADD CONSTRAINT Fk_LichLamViec_QuanLy 
    FOREIGN KEY (MaNVQuanLy) REFERENCES NhanVien(MaNV);

-- Khóa ngoại cho bảng HinhAnh
ALTER TABLE HinhAnh ADD CONSTRAINT Fk_HinhAnh_SanPham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);

-- Khóa ngoại cho bảng GiaSanPham
ALTER TABLE GiaSanPham ADD CONSTRAINT Fk_GiaSanPham_SanPham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);

-- Khóa ngoại cho bảng TonKhoChiTiet
ALTER TABLE TonKhoChiTiet ADD CONSTRAINT Fk_TonKhoChiTiet_SanPham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);

ALTER TABLE TonKhoChiTiet ADD CONSTRAINT Fk_TonKhoChiTiet_Kho 
    FOREIGN KEY (MaKho) REFERENCES Kho(MaKho);


-- Khóa ngoại cho bảng PhieuNhapHang
ALTER TABLE PhieuNhapHang ADD CONSTRAINT Fk_PhieuNhapHang_NhaCungCap 
    FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC);

ALTER TABLE PhieuNhapHang ADD CONSTRAINT Fk_PhieuNhapHang_Kho 
    FOREIGN KEY (MaKho) REFERENCES Kho(MaKho);

ALTER TABLE PhieuNhapHang ADD CONSTRAINT Fk_PhieuNhapHang_NhanVien 
    FOREIGN KEY (MaNVLap) REFERENCES NhanVien(MaNV);


-- Khóa ngoại cho bảng ChiTietPhieuNhap
ALTER TABLE ChiTietPhieuNhap ADD CONSTRAINT Fk_ChiTietPhieuNhap_PhieuNhap 
    FOREIGN KEY (MaPN) REFERENCES PhieuNhapHang(MaPN);

ALTER TABLE ChiTietPhieuNhap ADD CONSTRAINT Fk_ChiTietPhieuNhap_SanPham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);

-- Khóa ngoại cho bảng PhieuXuatKho
ALTER TABLE PhieuXuatKho ADD CONSTRAINT Fk_PhieuXuatKho_Kho 
    FOREIGN KEY (MaKho) REFERENCES Kho(MaKho);

ALTER TABLE PhieuXuatKho ADD CONSTRAINT Fk_PhieuXuatKho_NhanVien 
    FOREIGN KEY (MaNVLap) REFERENCES NhanVien(MaNV);

-- Khóa ngoại cho bảng ChiTietPhieuXuat
ALTER TABLE ChiTietPhieuXuat ADD CONSTRAINT Fk_ChiTietPhieuXuat_PhieuXuat 
    FOREIGN KEY (MaPXK) REFERENCES PhieuXuatKho(MaPXK);

ALTER TABLE ChiTietPhieuXuat ADD CONSTRAINT Fk_ChiTietPhieuXuat_SanPham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);

-- Khóa ngoại cho bảng HoaDon
ALTER TABLE HoaDon ADD CONSTRAINT Fk_HoaDon_KhachHang 
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH);

ALTER TABLE HoaDon ADD CONSTRAINT Fk_HoaDon_NhanVien 
    FOREIGN KEY (MaNVLap) REFERENCES NhanVien(MaNV);

ALTER TABLE HoaDon ADD CONSTRAINT Fk_HoaDon_KhuyenMai 
    FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM);

ALTER TABLE HoaDon ADD CONSTRAINT Fk_HoaDon_PhuongThucThanhToan 
    FOREIGN KEY (MaPTTT) REFERENCES PhuongThucThanhToan(MaPTTT);

-- Khóa ngoại cho bảng ChiTietHoaDon
ALTER TABLE ChiTietHoaDon ADD CONSTRAINT Fk_ChiTietHoaDon_HoaDon 
    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD);

ALTER TABLE ChiTietHoaDon ADD CONSTRAINT Fk_ChiTietHoaDon_SanPham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);
    
-- Khóa ngoại cho bảng KhuyenMai
ALTER TABLE KhuyenMai ADD CONSTRAINT Fk_KhuyenMai_QuanLy  
    FOREIGN KEY (MaQuanLy) REFERENCES NhanVien(MaNV);

-- Khóa ngoại cho bảng KhuyenMaiSanPham
ALTER TABLE KhuyenMaiSanPham ADD CONSTRAINT Fk_KhuyenMaiSanPham_KhuyenMai 
    FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM);

ALTER TABLE KhuyenMaiSanPham ADD CONSTRAINT Fk_KhuyenMaiSanPham_SanPham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);

-- Khóa ngoại cho bảng KhuyenMaiKhachHang
ALTER TABLE KhuyenMaiKhachHang ADD CONSTRAINT Fk_KhuyenMaiKhachHang_KhuyenMai 
    FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM);

ALTER TABLE KhuyenMaiKhachHang ADD CONSTRAINT Fk_KhuyenMaiKhachHang_KhachHang 
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH);

-- Khóa ngoại cho bảng ThanhToan
ALTER TABLE ThanhToan ADD CONSTRAINT Fk_ThanhToan_HoaDon 
    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD);

ALTER TABLE ThanhToan ADD CONSTRAINT Fk_ThanhToan_PhuongThucThanhToan 
    FOREIGN KEY (MaPTTT) REFERENCES PhuongThucThanhToan(MaPTTT);

-- Khóa ngoại cho bảng GioHang
ALTER TABLE GioHang ADD CONSTRAINT Fk_GioHang_KhachHang 
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH);

ALTER TABLE GioHang ADD CONSTRAINT Fk_GioHang_NhanVien 
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV);

-- Khóa ngoại cho bảng ChiTietGioHang
ALTER TABLE ChiTietGioHang ADD CONSTRAINT Fk_ChiTietGioHang_GioHang 
    FOREIGN KEY (MaGH) REFERENCES GioHang(MaGH);

ALTER TABLE ChiTietGioHang ADD CONSTRAINT Fk_ChiTietGioHang_SanPham 
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP);

-- Khóa ngoại cho bảng ThongKeBaoCao
ALTER TABLE ThongKeBaoCao ADD CONSTRAINT Fk_ThongKeBaoCao_CuaHang 
    FOREIGN KEY (MaCH) REFERENCES CuaHang(MaCH);

ALTER TABLE ThongKeBaoCao ADD CONSTRAINT Fk_ThongKeBaoCao_NhanVien 
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV);

-- Tự sinh mã sản phẩm (auto ID dạng SP001, SP002...) --
DELIMITER $$
CREATE FUNCTION Fn_TaoMaSPTuDong()
RETURNS VARCHAR(10)
DETERMINISTIC
BEGIN
    DECLARE MaSP VARCHAR(10);
    DECLARE So INT;

    SELECT RIGHT(MAX(MaSP), 3) INTO So FROM SanPham;
    IF So IS NULL THEN
        SET So = 0;
    END IF;
    SET MaSP = CONCAT('SP', LPAD(So + 1, 3, '0'));
    RETURN MaSP;
END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER Tg_TuSinhMaSP
BEFORE INSERT ON SanPham
FOR EACH ROW
BEGIN
    IF NEW.MaSP IS NULL OR NEW.MaSP = '' THEN
        SET NEW.MaSP = Fn_TaoMaSPTuDong();
    END IF;
END$$
DELIMITER ;

-- Tự cập nhật tồn kho khi nhập hàng --
DELIMITER $$
CREATE TRIGGER Tg_CapNhatTonKho_Nhap
AFTER INSERT ON ChiTietPhieuNhap
FOR EACH ROW
BEGIN
    UPDATE SanPham 
    SET SoLuongTon = SoLuongTon + NEW.SoLuongNhap
    WHERE MaSP = NEW.MaSP;
END$$
DELIMITER ;

-- Tự cập nhật tồn kho khi bán hàng --
DELIMITER $$
CREATE TRIGGER Tg_CapNhatTonKho_Ban
AFTER INSERT ON ChiTietHoaDon
FOR EACH ROW
BEGIN
    UPDATE SanPham
    SET SoLuongTon = SoLuongTon - NEW.SoLuong
    WHERE MaSP = NEW.MaSP;
END$$
DELIMITER ;

-- Tính tổng tiền của đơn hàng -- 
DELIMITER $$
CREATE FUNCTION Fn_TinhTongTienDonHang(MaDonThamSo VARCHAR(10))
RETURNS DECIMAL(18,2)
DETERMINISTIC
BEGIN
    DECLARE tong DECIMAL(18,2);
    SELECT SUM(SoLuong * DonGia) INTO tong
    FROM ChiTietHoaDon
    WHERE MaDon = MaDonThamSo;

    RETURN IFNULL(tong, 0);
END$$
DELIMITER ;

-- Trả về số lượng tồn kho hiện tại --
DELIMITER $$
CREATE FUNCTION Fn_LayTonKho(p_MaSP VARCHAR(10)) -- (p = parameter) Tham số đầu vào
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE v_SoLuongTon INT; -- (v = variable) Biến nội bộ để lưu dữ liệu
    SELECT SoLuongTon 
    INTO v_SoLuongTon 
    FROM SanPham 
    WHERE MaSP = p_MaSP; 
    RETURN IFNULL(v_SoLuongTon, 0);
END$$
DELIMITER ;

-- Thống kê doanh thu theo tháng --
DELIMITER $$
CREATE PROCEDURE sp_ThongKeDoanhThuTheoThang(
    IN p_Nam INT,
    IN p_Thang INT
)
BEGIN 
    SELECT 
        DATE(hd.NgayLap) AS Ngay,
        SUM(ct.SoLuong * ct.DonGia) AS DoanhThu
    FROM HoaDon AS hd
    JOIN ChiTietHoaDon AS ct ON hd.MaHD = ct.MaHD
    WHERE YEAR(hd.NgayLap) = p_Nam AND MONTH(hd.NgayLap) = p_Thang
    GROUP BY Ngay;
END$$
DELIMITER ;

-- Tạo hàm tự sinh mã cửa hàng --
DELIMITER $$
CREATE FUNCTION Fn_TaoMaCH()
RETURNS VARCHAR(10)
DETERMINISTIC
BEGIN
    DECLARE newMaCH VARCHAR(10);
    DECLARE maxMa INT;

    SELECT MAX(CAST(SUBSTRING(MaCH, 3) AS UNSIGNED)) INTO maxMa FROM CuaHang;
    IF maxMa IS NULL THEN
        SET maxMa = 0;
    END IF;

    SET newMaCH = CONCAT('CH', LPAD(maxMa + 1, 3, '0'));
    RETURN newMaCH;
END$$
DELIMITER ;	

-- Tạo Trigger tự sinh mã khi thêm mới --
DELIMITER $$
CREATE TRIGGER Tg_TuSinhMaCH
BEFORE INSERT ON CuaHang
FOR EACH ROW
BEGIN
    IF NEW.MaCH IS NULL OR NEW.MaCH = '' THEN
        SET NEW.MaCH = fn_TaoMaCH();
    END IF;
END$$
DELIMITER ;

-- Tạo Function tự sinh mã khách hàng --
DELIMITER $$
CREATE FUNCTION Fn_TaoMaKH()
RETURNS NVARCHAR(10)
DETERMINISTIC
BEGIN
    DECLARE maxSo INT;
    DECLARE newMa NVARCHAR(10);

    SELECT MAX(CAST(SUBSTRING(MaKH, 3) AS UNSIGNED)) INTO maxSo FROM KhachHang;
    IF maxSo IS NULL THEN
        SET maxSo = 0;
    END IF;

    SET newMa = CONCAT('KH', LPAD(maxSo + 1, 3, '0'));
    RETURN newMa;
END$$
DELIMITER ;

-- Trigger Thêm khách hàng -- 
DELIMITER $$
CREATE TRIGGER Tg_ThemKhachHang
BEFORE INSERT ON KhachHang
FOR EACH ROW
BEGIN
    -- Tự sinh mã nếu bỏ trống
    IF NEW.MaKH IS NULL OR NEW.MaKH = '' THEN
        SET NEW.MaKH = fn_TaoMaKH();
    END IF;

    -- Chuẩn hóa số điện thoại và email
    SET NEW.SDT = REPLACE(NEW.SDT, ' ', '');
    SET NEW.Email = LOWER(NEW.Email);

    -- Nếu điểm tích lũy âm (dù CHECK có rồi), vẫn xử lý an toàn
    IF NEW.DiemTichLuy IS NULL OR NEW.DiemTichLuy < 0 THEN
        SET NEW.DiemTichLuy = 0;
    END IF;
END$$
DELIMITER ;

-- Trigger Cập nhật khách hàng -- 
DELIMITER $$
CREATE TRIGGER Tg_CapNhatKhachHang
BEFORE UPDATE ON KhachHang
FOR EACH ROW
BEGIN
    -- Chuẩn hóa số điện thoại và email
    SET NEW.SDT = REPLACE(NEW.SDT, ' ', '');
    SET NEW.Email = LOWER(NEW.Email);

    -- Xử lý an toàn cho điểm tích lũy
    IF NEW.DiemTichLuy IS NULL OR NEW.DiemTichLuy < 0 THEN
        SET NEW.DiemTichLuy = 0;
    END IF;
END$$
DELIMITER ;

-- Tạo Function sinh mã khuyến mãi --
DELIMITER $$
CREATE FUNCTION Fn_TaoMaKM()
RETURNS VARCHAR(10)
DETERMINISTIC
BEGIN
    DECLARE maxSo INT;
    DECLARE newMa VARCHAR(10);

    SELECT MAX(CAST(SUBSTRING(MaKM, 3) AS UNSIGNED))
      INTO maxSo FROM KhuyenMai;

    IF maxSo IS NULL THEN
        SET maxSo = 0;
    END IF;

    SET newMa = CONCAT('KM', LPAD(maxSo + 1, 3, '0'));
    RETURN newMa;
END$$
DELIMITER ;

-- Trigger Thêm khuyến mãi -- 
DELIMITER $$
CREATE TRIGGER tg_ThemKhuyenMai
BEFORE INSERT ON KhuyenMai
FOR EACH ROW
BEGIN
    -- Tự sinh mã nếu chưa có
    IF NEW.MaKM IS NULL OR NEW.MaKM = '' THEN
        SET NEW.MaKM = 	Fn_TaoMaKM();
    END IF;

    -- Xử lý loại khuyến mãi viết hoa chữ cái đầu (tùy chọn)
    SET NEW.LoaiKM = CONCAT(UCASE(LEFT(NEW.LoaiKM,1)), LCASE(SUBSTRING(NEW.LoaiKM,2)));

    -- Nếu giá trị khuyến mãi âm, gán về 0
    IF NEW.GiaTriKM IS NULL OR NEW.GiaTriKM < 0 THEN
        SET NEW.GiaTriKM = 0;
    END IF;

    -- Kiểm tra ngày bắt đầu và kết thúc
    IF NEW.NgayKetThuc < NEW.NgayBatDau THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Ngày kết thúc phải sau ngày bắt đầu';
    END IF;
END$$
DELIMITER ;

-- Trigger Cập Nhật khuyến mãi -- 
DELIMITER $$
CREATE TRIGGER tg_CapNhatKhuyenMai
BEFORE UPDATE ON KhuyenMai
FOR EACH ROW
BEGIN
    -- Xử lý loại khuyến mãi viết hoa chữ cái đầu
    SET NEW.LoaiKM = INITCAP(NEW.LoaiKM);

    -- Không cho phép giá trị âm
    IF NEW.GiaTriKM IS NULL OR NEW.GiaTriKM < 0 THEN
        SET NEW.GiaTriKM = 0;
    END IF;

    -- Ngày kết thúc phải sau ngày bắt đầu
    IF NEW.NgayKetThuc < NEW.NgayBatDau THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày kết thúc phải sau ngày bắt đầu';
    END IF;
END$$
DELIMITER ;

-- Tạo Function sinh mã loại sản phẩm --
DELIMITER $$
CREATE FUNCTION Fn_TaoMaLoaiSP()
RETURNS NVARCHAR(10)
DETERMINISTIC
BEGIN
    DECLARE maxSo INT;
    DECLARE newMa NVARCHAR(10);

    SELECT MAX(CAST(SUBSTRING(MaLoaiSP, 4) AS UNSIGNED)) INTO maxSo FROM LoaiSanPham;
    IF maxSo IS NULL THEN
        SET maxSo = 0;
    END IF;

    SET newMa = CONCAT('LSP', LPAD(maxSo + 1, 3, '0'));
    RETURN newMa;
END$$
DELIMITER ;

-- Trigger Thêm loại sản phẩm mới --
DELIMITER $$
CREATE TRIGGER Tg_ThemLoaiSanPham
BEFORE INSERT ON LoaiSanPham
FOR EACH ROW
BEGIN
    -- Tự sinh mã nếu để trống
    IF NEW.MaLoaiSP IS NULL OR NEW.MaLoaiSP = '' THEN
        SET NEW.MaLoaiSP = fn_TaoMaLoaiSP();
    END IF;

    -- Nếu tên loại bị null thì báo lỗi
    IF NEW.TenLoai IS NULL OR TRIM(NEW.TenLoai) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên loại sản phẩm không được để trống';
    END IF;
END$$
DELIMITER ;

-- Trigger Cập nhật loại sản phẩm --
DELIMITER $$
CREATE TRIGGER Tg_CapNhatLoaiSanPham
BEFORE UPDATE ON LoaiSanPham
FOR EACH ROW
BEGIN
    -- Không cho tên loại trống khi cập nhật
    IF NEW.TenLoai IS NULL OR TRIM(NEW.TenLoai) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên loại sản phẩm không được để trống khi cập nhật';
    END IF;
END$$
DELIMITER ;

-- Function tự sinh mã MaNguoiDung --
DELIMITER $$
CREATE FUNCTION Fn_TaoMaNguoiDung()
RETURNS NVARCHAR(10)
DETERMINISTIC
BEGIN
    DECLARE maxSo INT;
    DECLARE newMa NVARCHAR(10);

    SELECT MAX(CAST(SUBSTRING(MaNguoiDung, 3) AS UNSIGNED)) INTO maxSo FROM NguoiDung;
    IF maxSo IS NULL THEN
        SET maxSo = 0;
    END IF;

    SET newMa = CONCAT('ND', LPAD(maxSo + 1, 3, '0'));
    RETURN newMa;
END$$
DELIMITER ;

-- Trigger Thêm mới NguoiDung --
DELIMITER $$
CREATE TRIGGER Tg_ThemNguoiDung
BEFORE INSERT ON NguoiDung
FOR EACH ROW
BEGIN
    -- Tự sinh mã nếu để trống
    IF NEW.MaNguoiDung IS NULL OR NEW.MaNguoiDung = '' THEN
        SET NEW.MaNguoiDung = Fn_TaoMaNguoiDung();
    END IF;

    -- Kiểm tra Tên đăng nhập
    IF NEW.Email IS NULL OR TRIM(NEW.Email) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên đăng nhập không được để trống';
    END IF;

    -- Kiểm tra Mật khẩu
    IF NEW.MatKhau IS NULL OR TRIM(NEW.MatKhau) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Mật khẩu không được để trống';
    END IF;

    -- Kiểm tra Vai trò hợp lệ (0–3)
    IF NEW.VaiTro < 0 OR NEW.VaiTro > 3 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Vai trò không hợp lệ (chỉ từ 0 đến 3)';
    END IF;
END$$
DELIMITER ;

-- Trigger Cập nhật NguoiDung -- 
DELIMITER $$
CREATE TRIGGER Tg_CapNhatNguoiDung
BEFORE UPDATE ON NguoiDung
FOR EACH ROW
BEGIN
    -- Không được để trống Tên đăng nhập
    IF NEW.Email IS NULL OR TRIM(NEW.Email) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên đăng nhập không được để trống khi cập nhật';
    END IF;

    -- Không được để trống Mật khẩu
    IF NEW.MatKhau IS NULL OR TRIM(NEW.MatKhau) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Mật khẩu không được để trống khi cập nhật';
    END IF;

    -- Kiểm tra Vai trò hợp lệ
    IF NEW.VaiTro < 0 OR NEW.VaiTro > 3 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Vai trò không hợp lệ khi cập nhật (chỉ từ 0 đến 3)';
    END IF;
END$$
DELIMITER ;

-- Function tự sinh mã MaNCC -- 
DELIMITER $$
CREATE FUNCTION Fn_TaoMaNCC()
RETURNS NVARCHAR(10)
DETERMINISTIC
BEGIN
    DECLARE maxSo INT;
    DECLARE newMa NVARCHAR(10);

    SELECT MAX(CAST(SUBSTRING(MaNCC, 4) AS UNSIGNED)) INTO maxSo FROM NhaCungCap;
    IF maxSo IS NULL THEN
        SET maxSo = 0;
    END IF;

    SET newMa = CONCAT('NCC', LPAD(maxSo + 1, 3, '0'));
    RETURN newMa;
END$$
DELIMITER ;

-- Trigger Thêm mới nhà cung cấp -- 
DELIMITER $$
CREATE TRIGGER Tg_ThemNhaCungCap
BEFORE INSERT ON NhaCungCap
FOR EACH ROW
BEGIN
    -- Tự sinh mã nếu bỏ trống
    IF NEW.MaNCC IS NULL OR NEW.MaNCC = '' THEN
        SET NEW.MaNCC = fn_TaoMaNCC();
    END IF;

    -- Kiểm tra Tên không để trống
    IF NEW.TenNCC IS NULL OR TRIM(NEW.TenNCC) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên nhà cung cấp không được để trống';
    END IF;

    -- Kiểm tra SDT không để trống
    IF NEW.SDT IS NULL OR TRIM(NEW.SDT) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số điện thoại không được để trống';
    END IF;
END$$
DELIMITER ;

-- Trigger Cập nhật thông tin nhà cung cấp --
DELIMITER $$
CREATE TRIGGER Tg_CapNhatNhaCungCap
BEFORE UPDATE ON NhaCungCap
FOR EACH ROW
BEGIN
    -- Không cho phép cập nhật tên rỗng
    IF NEW.TenNCC IS NULL OR TRIM(NEW.TenNCC) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên nhà cung cấp không được để trống khi cập nhật';
    END IF;

    -- Không cho phép SDT rỗng
    IF NEW.SDT IS NULL OR TRIM(NEW.SDT) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số điện thoại không được để trống khi cập nhật';
    END IF;
END$$
DELIMITER ;

-- Function tự sinh mã MaNV --
DELIMITER $$
CREATE FUNCTION Fn_TaoMaNV()
RETURNS VARCHAR(10)
DETERMINISTIC
BEGIN
    DECLARE maxSo INT;
    DECLARE newMa VARCHAR(10);

    SELECT MAX(CAST(SUBSTRING(MaNV, 3) AS UNSIGNED)) INTO maxSo FROM NhanVien;
    IF maxSo IS NULL THEN
        SET maxSo = 0;
    END IF;

    SET newMa = CONCAT('NV', LPAD(maxSo + 1, 3, '0'));
    RETURN newMa;
END$$
DELIMITER ;

-- Trigger Thêm nhân viên --
DELIMITER $$
CREATE TRIGGER Tg_ThemNhanVien
BEFORE INSERT ON NhanVien
FOR EACH ROW
BEGIN
    -- Tự sinh mã nếu chưa nhập
    IF NEW.MaNV IS NULL OR NEW.MaNV = '' THEN
        SET NEW.MaNV = Fn_TaoMaNV();
    END IF;

    -- Kiểm tra họ tên không rỗng
    IF NEW.HoTen IS NULL OR TRIM(NEW.HoTen) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Họ tên nhân viên không được để trống';
    END IF;

    -- Kiểm tra số điện thoại không rỗng
    IF NEW.SDT IS NULL OR TRIM(NEW.SDT) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số điện thoại không được để trống';
    END IF;

    -- Kiểm tra ngày sinh phải trước hiện tại
    IF NEW.NgaySinh IS NULL OR NEW.NgaySinh >= CURRENT_DATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày sinh không hợp lệ';
    END IF;

    -- Kiểm tra lương không âm
    IF NEW.Luong IS NULL OR NEW.Luong < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Lương phải lớn hơn hoặc bằng 0';
    END IF;
END$$
DELIMITER ;

-- Trigger Cập nhật nhân viên -- 
DELIMITER $$
CREATE TRIGGER Tg_CapNhatNhanVien
BEFORE UPDATE ON NhanVien
FOR EACH ROW
BEGIN
    DECLARE msg VARCHAR(255);

    -- Kiểm tra họ tên không rỗng
    IF NEW.HoTen IS NULL OR TRIM(NEW.HoTen) = '' THEN
        SET msg = 'Họ tên nhân viên không được để trống khi cập nhật';
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = msg;
    END IF;

    -- Kiểm tra SDT không rỗng
    IF NEW.SDT IS NULL OR TRIM(NEW.SDT) = '' THEN
        SET msg = 'Số điện thoại không được để trống khi cập nhật';
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = msg;
    END IF;

    -- Kiểm tra ngày sinh hợp lệ
    IF NEW.NgaySinh IS NULL OR NEW.NgaySinh >= CURRENT_DATE() THEN
        SET msg = 'Ngày sinh không hợp lệ khi cập nhật';
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = msg;
    END IF;

    -- Kiểm tra lương
    IF NEW.Luong IS NULL OR NEW.Luong < 0 THEN
        SET msg = 'Lương phải >= 0 khi cập nhật';
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = msg;
    END IF;

    -- Nếu lương thay đổi thì báo lỗi và hiển thị giá trị cũ/mới
    IF NEW.Luong <> OLD.Luong THEN
        SET msg = CONCAT('Lương thay đổi từ ', OLD.Luong, ' thành ', NEW.Luong);
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = msg;
    END IF;
END$$
DELIMITER ;

-- Function tự sinh mã MaPTTT --
DELIMITER $$
CREATE FUNCTION Fn_TaoMaPTTT()
RETURNS NVARCHAR(10)
DETERMINISTIC
BEGIN
    DECLARE maxSo INT;
    DECLARE newMa NVARCHAR(10);

    SELECT MAX(CAST(SUBSTRING(MaPTTT, 6) AS UNSIGNED)) INTO maxSo FROM PhuongThucThanhToan;
    IF maxSo IS NULL THEN
        SET maxSo = 0;
    END IF;

    SET newMa = CONCAT('PTTT', LPAD(maxSo + 1, 3, '0'));
    RETURN newMa;
END$$
DELIMITER ;

-- Trigger Thêm phương thức thanh toán --
DELIMITER $$
CREATE TRIGGER Tg_ThemPhuongThucThanhToan
BEFORE INSERT ON PhuongThucThanhToan
FOR EACH ROW
BEGIN
    -- Tự sinh mã nếu chưa nhập
    IF NEW.MaPTTT IS NULL OR NEW.MaPTTT = '' THEN
        SET NEW.MaPTTT = Fn_TaoMaPTTT();
    END IF;

    -- Kiểm tra tên không rỗng
    IF NEW.TenPTTT IS NULL OR TRIM(NEW.TenPTTT) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên phương thức thanh toán không được để trống';
    END IF;
END$$
DELIMITER ;

-- Trigger Cập nhật phương thức thanh toán --
DELIMITER $$
CREATE TRIGGER Tg_CapNhatPhuongThucThanhToan
BEFORE UPDATE ON PhuongThucThanhToan
FOR EACH ROW
BEGIN
    -- Kiểm tra tên không rỗng
    IF NEW.TenPTTT IS NULL OR TRIM(NEW.TenPTTT) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên phương thức thanh toán không được để trống khi cập nhật';
    END IF;
END$$	
DELIMITER ;

-- Trigger Thêm giá sản phẩm -- 
DELIMITER $$
CREATE TRIGGER Tg_ThemGiaSanPham
BEFORE INSERT ON GiaSanPham
FOR EACH ROW
BEGIN
    -- Kiểm tra giá phải lớn hơn 0
    IF NEW.Gia <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Giá sản phẩm phải lớn hơn 0';
    END IF;

    -- Kiểm tra ngày bắt đầu không được null
    IF NEW.NgayBatDau IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày bắt đầu không được để trống';
    END IF;

    -- Kiểm tra ngày kết thúc nếu có thì phải lớn hơn hoặc bằng ngày bắt đầu
    IF NEW.NgayKetThuc IS NOT NULL AND NEW.NgayKetThuc < NEW.NgayBatDau THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu';
    END IF;
END$$
DELIMITER ;

-- Trigger Cập nhật giá sản phẩm -- 
DELIMITER $$
CREATE TRIGGER Tg_CapNhatGiaSanPham
BEFORE UPDATE ON GiaSanPham
FOR EACH ROW
BEGIN
    -- Kiểm tra giá phải lớn hơn 0
    IF NEW.Gia <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Giá sản phẩm phải lớn hơn 0 khi cập nhật';
    END IF;

    -- Kiểm tra ngày bắt đầu không được null
    IF NEW.NgayBatDau IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày bắt đầu không được để trống khi cập nhật';
    END IF;

    -- Kiểm tra ngày kết thúc nếu có thì phải lớn hơn hoặc bằng ngày bắt đầu
    IF NEW.NgayKetThuc IS NOT NULL AND NEW.NgayKetThuc < NEW.NgayBatDau THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu khi cập nhật';
    END IF;
END$$
DELIMITER ;

-- Trigger thêm hóa đơn --
DELIMITER $$
CREATE TRIGGER Tg_HoaDon_Insert
BEFORE INSERT ON HoaDon
FOR EACH ROW
BEGIN
    -- Kiểm tra tổng tiền không âm
    IF NEW.TongTien < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tổng tiền không được nhỏ hơn 0';
    END IF;

    -- Kiểm tra ngày lập không được NULL
    IF NEW.NgayLap IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày lập không được để trống';
    END IF;

    -- Kiểm tra trạng thái chỉ cho phép 0,1,2,3
    IF NEW.TrangThai NOT IN (0,1,2,3) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Trạng thái hóa đơn không hợp lệ';
    END IF;
END$$
DELIMITER ;

-- Trigger cập nhật hóa đơn --
DELIMITER $$
CREATE TRIGGER Tg_HoaDon_Update
BEFORE UPDATE ON HoaDon
FOR EACH ROW
BEGIN
    -- Kiểm tra tổng tiền không âm
    IF NEW.TongTien < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tổng tiền không được nhỏ hơn 0 khi cập nhật';
    END IF;

    -- Kiểm tra ngày lập không được NULL
    IF NEW.NgayLap IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày lập không được để trống khi cập nhật';
    END IF;

    -- Kiểm tra trạng thái chỉ cho phép 0,1,2,3
    IF NEW.TrangThai NOT IN (0,1,2,3) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Trạng thái hóa đơn không hợp lệ';
    END IF;

    -- Tự động ghi lại thời gian sửa
    SET NEW.NgaySua = CURRENT_TIMESTAMP;
END$$
DELIMITER ;

-- Trigger Thêm tồn kho -- 
DELIMITER $$
CREATE TRIGGER Tg_TonKhoChiTiet_Insert
BEFORE INSERT ON TonKhoChiTiet
FOR EACH ROW
BEGIN
    -- Kiểm tra số lượng tồn phải >= 0
    IF NEW.SoLuongTon < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số lượng tồn không được nhỏ hơn 0';
    END IF;
END$$
DELIMITER ;

-- Trigger Cập nhật tồn kho -- 
DELIMITER $$
CREATE TRIGGER Tg_TonKhoChiTiet_Update
BEFORE UPDATE ON TonKhoChiTiet
FOR EACH ROW
BEGIN
    -- Kiểm tra số lượng tồn phải >= 0
    IF NEW.SoLuongTon < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Số lượng tồn không được nhỏ hơn 0 khi cập nhật';
    END IF;
END$$
DELIMITER ;

-- Trigger Thêm lịch làm việc --
DELIMITER $$
CREATE TRIGGER Tg_LichLamViec_Insert
BEFORE INSERT ON LichLamViec
FOR EACH ROW
BEGIN
    -- Kiểm tra ngày làm không được NULL
    IF NEW.NgayLam IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày làm không được để trống';
    END IF;

    -- Kiểm tra ngày làm không được nhỏ hơn ngày hiện tại
    IF NEW.NgayLam < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày làm không được nhỏ hơn ngày hiện tại';
    END IF;

    -- Mặc định trạng thái là 0 (chờ duyệt) nếu chưa nhập
    IF NEW.TrangThai IS NULL THEN
        SET NEW.TrangThai = 0;
    END IF;
END$$
DELIMITER ;

-- Trigger Cập nhật lịch làm việc --
DELIMITER $$
CREATE TRIGGER Tg_LichLamViec_Update
BEFORE UPDATE ON LichLamViec
FOR EACH ROW
BEGIN
    -- Kiểm tra ngày làm không được NULL
    IF NEW.NgayLam IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày làm không được để trống khi cập nhật';
    END IF;

    -- Kiểm tra ngày làm không được nhỏ hơn ngày hiện tại (chỉ khi chưa làm)
    IF NEW.NgayLam < CURDATE() AND NEW.TrangThai = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể cập nhật ngày làm nhỏ hơn ngày hiện tại';
    END IF;

    -- Nếu trạng thái được duyệt/từ chối/hủy và chưa có ngày duyệt thì tự động set NgayDuyet
    IF NEW.TrangThai IN (1, 2, 3) AND (OLD.TrangThai <> NEW.TrangThai) AND NEW.NgayDuyet IS NULL THEN
        SET NEW.NgayDuyet = NOW();
    END IF;
END$$
DELIMITER ;

-- Trigger Thêm giỏ hàng --
DELIMITER $$
CREATE TRIGGER Tg_GioHang_Insert
BEFORE INSERT ON GioHang
FOR EACH ROW
BEGIN
    -- Nếu không nhập NgayTao thì gán mặc định thời điểm hiện tại
    IF NEW.NgayTao IS NULL THEN
        SET NEW.NgayTao = NOW();
    END IF;

    -- Nếu không nhập trạng thái thì gán mặc định = 0 (Đang chọn hàng)
    IF NEW.TrangThai IS NULL THEN
        SET NEW.TrangThai = 0;
    END IF;
END$$
DELIMITER ;

-- Trigger Cập nhật giỏ hàng --
DELIMITER $$
CREATE TRIGGER Tg_GioHang_Update
BEFORE UPDATE ON GioHang
FOR EACH ROW
BEGIN
    -- Ngăn không cho chỉnh sửa NgayTao về NULL
    IF NEW.NgayTao IS NULL THEN
        SET NEW.NgayTao = OLD.NgayTao;
    END IF;

    -- Nếu trạng thái chuyển từ Đang chọn hàng (0) sang Đã đặt hàng (1)
    -- mà chưa có NgayTao thì bổ sung thời điểm hiện tại
    IF OLD.TrangThai = 0 AND NEW.TrangThai = 1 AND NEW.NgayTao IS NULL THEN
        SET NEW.NgayTao = NOW();
    END IF;

    -- Không cho sửa trạng thái giỏ hàng đã thanh toán (2) hoặc đã hủy (3)
    IF OLD.TrangThai IN (2, 3) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể chỉnh sửa giỏ hàng đã thanh toán hoặc đã hủy';
    END IF;
END$$
DELIMITER ;