-- Tạo bảng BaoCaoDoanhThu (Revenue Report)
CREATE TABLE BaoCaoDoanhThu (
    MaBaoCao BIGINT AUTO_INCREMENT PRIMARY KEY,
    LoaiBaoCao VARCHAR(50) NOT NULL COMMENT 'NGAY, TUAN, THANG, NAM',
    TuNgay DATE NOT NULL COMMENT 'Từ ngày',
    DenNgay DATE NOT NULL COMMENT 'Đến ngày',
    TongDoanhThu DECIMAL(15,2) DEFAULT 0 COMMENT 'Tổng doanh thu',
    SoLuongHoaDon INT DEFAULT 0 COMMENT 'Số lượng hóa đơn',
    SoLuongSanPhamBan INT DEFAULT 0 COMMENT 'Số lượng sản phẩm bán',
    TiLeThanhCong DECIMAL(5,2) DEFAULT 0 COMMENT 'Tỷ lệ thành công (%)',
    TangTruongDoanhThu DECIMAL(5,2) DEFAULT 0 COMMENT 'Tăng trưởng doanh thu (%)',
    TangTruongHoaDon DECIMAL(5,2) DEFAULT 0 COMMENT 'Tăng trưởng số hóa đơn (%)',
    SoLuongKhachHang INT DEFAULT 0 COMMENT 'Số lượng khách hàng',
    SoLuongSanPham INT DEFAULT 0 COMMENT 'Số lượng sản phẩm',
    MaCH VARCHAR(10) COMMENT 'Mã cửa hàng',
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    NgayCapNhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    TrangThai TINYINT(1) DEFAULT 1 COMMENT '1: Active, 0: Deleted',
    GhiChu TEXT COMMENT 'Ghi chú báo cáo',
    
    -- Indexes
    INDEX idx_baocao_loai (LoaiBaoCao),
    INDEX idx_baocao_ngay (TuNgay, DenNgay),
    INDEX idx_baocao_cuahang (MaCH),
    INDEX idx_baocao_trangthai (TrangThai),
    INDEX idx_baocao_ngaytao (NgayTao),
    
    -- Unique constraint
    UNIQUE KEY uk_baocao_unique (LoaiBaoCao, TuNgay, DenNgay, MaCH),
    
    -- Foreign key
    FOREIGN KEY (MaCH) REFERENCES CuaHang(MaCH) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng báo cáo doanh thu';

-- Tạo bảng BaoCaoDoanhThuChiTiet (Revenue Report Detail)
CREATE TABLE BaoCaoDoanhThuChiTiet (
    MaChiTiet BIGINT AUTO_INCREMENT PRIMARY KEY,
    MaBaoCao BIGINT NOT NULL COMMENT 'Mã báo cáo',
    LoaiChiTiet VARCHAR(50) NOT NULL COMMENT 'SANPHAM, KHACHHANG',
    MaDoiTuong VARCHAR(20) NOT NULL COMMENT 'Mã sản phẩm hoặc mã khách hàng',
    TenDoiTuong VARCHAR(255) NOT NULL COMMENT 'Tên sản phẩm hoặc tên khách hàng',
    SoLuong INT DEFAULT 0 COMMENT 'Số lượng bán/mua',
    DoanhThu DECIMAL(15,2) DEFAULT 0 COMMENT 'Doanh thu',
    TiLe DECIMAL(5,2) DEFAULT 0 COMMENT 'Tỷ lệ đóng góp (%)',
    TangTruong DECIMAL(5,2) DEFAULT 0 COMMENT 'Tăng trưởng (%)',
    Ranking INT DEFAULT 0 COMMENT 'Thứ hạng',
    SoLanGiaoDich INT DEFAULT 0 COMMENT 'Số lần giao dịch',
    GiaTriTrungBinh DECIMAL(15,2) DEFAULT 0 COMMENT 'Giá trị trung bình mỗi giao dịch',
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    NgayCapNhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    TrangThai TINYINT(1) DEFAULT 1 COMMENT '1: Active, 0: Deleted',
    ThongTinBosung JSON COMMENT 'Thông tin bổ sung dạng JSON',
    
    -- Indexes
    INDEX idx_chitiet_baocao (MaBaoCao),
    INDEX idx_chitiet_loai (LoaiChiTiet),
    INDEX idx_chitiet_doituong (MaDoiTuong),
    INDEX idx_chitiet_doanhthu (DoanhThu DESC),
    INDEX idx_chitiet_ranking (Ranking),
    INDEX idx_chitiet_trangthai (TrangThai),
    
    -- Foreign key
    FOREIGN KEY (MaBaoCao) REFERENCES BaoCaoDoanhThu(MaBaoCao) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng chi tiết báo cáo doanh thu';

-- Tạo view để lấy thống kê nhanh
CREATE VIEW v_BaoCaoDoanhThu_Summary AS
SELECT 
    bc.MaBaoCao,
    bc.LoaiBaoCao,
    bc.TuNgay,
    bc.DenNgay,
    bc.TongDoanhThu,
    bc.SoLuongHoaDon,
    bc.TangTruongDoanhThu,
    ch.TenCH as TenCuaHang,
    COUNT(ct.MaChiTiet) as SoChiTiet,
    bc.NgayTao
FROM BaoCaoDoanhThu bc
LEFT JOIN CuaHang ch ON bc.MaCH = ch.MaCH
LEFT JOIN BaoCaoDoanhThuChiTiet ct ON bc.MaBaoCao = ct.MaBaoCao AND ct.TrangThai = 1
WHERE bc.TrangThai = 1
GROUP BY bc.MaBaoCao, bc.LoaiBaoCao, bc.TuNgay, bc.DenNgay, bc.TongDoanhThu, 
         bc.SoLuongHoaDon, bc.TangTruongDoanhThu, ch.TenCH, bc.NgayTao;

-- Tạo stored procedure để tính toán doanh thu tự động
DELIMITER //

CREATE PROCEDURE sp_TinhDoanhThu(
    IN p_LoaiBaoCao VARCHAR(50),
    IN p_TuNgay DATE,
    IN p_DenNgay DATE,
    IN p_MaCH VARCHAR(10)
)
BEGIN
    DECLARE v_TongDoanhThu DECIMAL(15,2) DEFAULT 0;
    DECLARE v_SoLuongHoaDon INT DEFAULT 0;
    DECLARE v_SoLuongSanPhamBan INT DEFAULT 0;
    DECLARE v_SoLuongKhachHang INT DEFAULT 0;
    DECLARE v_SoLuongSanPham INT DEFAULT 0;
    
    -- Tính tổng doanh thu
    SELECT 
        COALESCE(SUM(hd.TongTien), 0),
        COUNT(DISTINCT hd.MaHD),
        COALESCE(SUM(cthd.SoLuong), 0),
        COUNT(DISTINCT hd.MaKH),
        COUNT(DISTINCT cthd.MaSP)
    INTO v_TongDoanhThu, v_SoLuongHoaDon, v_SoLuongSanPhamBan, v_SoLuongKhachHang, v_SoLuongSanPham
    FROM HoaDon hd
    INNER JOIN ChiTietHoaDon cthd ON hd.MaHD = cthd.MaHD
    WHERE hd.NgayLap BETWEEN p_TuNgay AND p_DenNgay
    AND hd.TrangThai = 1
    AND (p_MaCH IS NULL OR hd.MaCH = p_MaCH);
    
    -- Trả về kết quả
    SELECT 
        v_TongDoanhThu as TongDoanhThu,
        v_SoLuongHoaDon as SoLuongHoaDon,
        v_SoLuongSanPhamBan as SoLuongSanPhamBan,
        v_SoLuongKhachHang as SoLuongKhachHang,
        v_SoLuongSanPham as SoLuongSanPham;
END //

DELIMITER ;

-- Tạo stored procedure để lấy top sản phẩm bán chạy
DELIMITER //

CREATE PROCEDURE sp_TopSanPhamBanChay(
    IN p_TuNgay DATE,
    IN p_DenNgay DATE,
    IN p_MaCH VARCHAR(10),
    IN p_Limit INT
)
BEGIN
    SELECT 
        sp.MaSP,
        sp.TenSP,
        SUM(cthd.SoLuong) as TongSoLuong,
        SUM(cthd.SoLuong * cthd.DonGia) as TongDoanhThu,
        COUNT(DISTINCT hd.MaHD) as SoLanBan,
        AVG(cthd.SoLuong * cthd.DonGia) as DoanhThuTrungBinh
    FROM SanPham sp
    INNER JOIN ChiTietHoaDon cthd ON sp.MaSP = cthd.MaSP
    INNER JOIN HoaDon hd ON cthd.MaHD = hd.MaHD
    WHERE hd.NgayLap BETWEEN p_TuNgay AND p_DenNgay
    AND hd.TrangThai = 1
    AND sp.TrangThai = 1
    AND (p_MaCH IS NULL OR hd.MaCH = p_MaCH)
    GROUP BY sp.MaSP, sp.TenSP
    ORDER BY TongDoanhThu DESC
    LIMIT p_Limit;
END //

DELIMITER ;

-- Tạo stored procedure để lấy top khách hàng tiềm năng
DELIMITER //

CREATE PROCEDURE sp_TopKhachHangTiemNang(
    IN p_TuNgay DATE,
    IN p_DenNgay DATE,
    IN p_MaCH VARCHAR(10),
    IN p_Limit INT
)
BEGIN
    SELECT 
        kh.MaKH,
        kh.TenKH,
        COUNT(DISTINCT hd.MaHD) as SoLanMua,
        SUM(hd.TongTien) as TongChiTieu,
        AVG(hd.TongTien) as ChiTieuTrungBinh,
        MAX(hd.NgayLap) as LanMuaCuoi,
        DATEDIFF(CURDATE(), MAX(hd.NgayLap)) as SoNgayKhongMua
    FROM KhachHang kh
    INNER JOIN HoaDon hd ON kh.MaKH = hd.MaKH
    WHERE hd.NgayLap BETWEEN p_TuNgay AND p_DenNgay
    AND hd.TrangThai = 1
    AND kh.TrangThai = 1
    AND (p_MaCH IS NULL OR hd.MaCH = p_MaCH)
    GROUP BY kh.MaKH, kh.TenKH
    ORDER BY TongChiTieu DESC, SoLanMua DESC
    LIMIT p_Limit;
END //

DELIMITER ;

-- Tạo trigger để tự động cập nhật ngày cập nhật
DELIMITER //

CREATE TRIGGER tr_BaoCaoDoanhThu_UpdateTime
BEFORE UPDATE ON BaoCaoDoanhThu
FOR EACH ROW
BEGIN
    SET NEW.NgayCapNhat = CURRENT_TIMESTAMP;
END //

CREATE TRIGGER tr_BaoCaoDoanhThuChiTiet_UpdateTime
BEFORE UPDATE ON BaoCaoDoanhThuChiTiet
FOR EACH ROW
BEGIN
    SET NEW.NgayCapNhat = CURRENT_TIMESTAMP;
END //

DELIMITER ;

-- Tạo function để tính tăng trưởng
DELIMITER //

CREATE FUNCTION fn_TinhTangTruong(
    p_GiaTriHienTai DECIMAL(15,2),
    p_GiaTriTruoc DECIMAL(15,2)
) RETURNS DECIMAL(5,2)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_TangTruong DECIMAL(5,2) DEFAULT 0;
    
    IF p_GiaTriTruoc > 0 THEN
        SET v_TangTruong = ((p_GiaTriHienTai - p_GiaTriTruoc) / p_GiaTriTruoc) * 100;
    ELSEIF p_GiaTriHienTai > 0 THEN
        SET v_TangTruong = 100;
    END IF;
    
    RETURN v_TangTruong;
END //

DELIMITER ;

-- Insert dữ liệu mẫu (optional)
-- INSERT INTO BaoCaoDoanhThu (LoaiBaoCao, TuNgay, DenNgay, TongDoanhThu, SoLuongHoaDon, MaCH, GhiChu)
-- VALUES 
-- ('NGAY', '2024-01-01', '2024-01-01', 1500000.00, 25, 'CH001', 'Báo cáo doanh thu ngày 01/01/2024'),
-- ('TUAN', '2024-01-01', '2024-01-07', 8500000.00, 120, 'CH001', 'Báo cáo doanh thu tuần đầu tháng 1/2024'),
-- ('THANG', '2024-01-01', '2024-01-31', 45000000.00, 850, 'CH001', 'Báo cáo doanh thu tháng 1/2024');

-- Tạo index bổ sung cho performance
CREATE INDEX idx_baocao_composite ON BaoCaoDoanhThu (LoaiBaoCao, MaCH, TuNgay, DenNgay, TrangThai);
CREATE INDEX idx_chitiet_composite ON BaoCaoDoanhThuChiTiet (MaBaoCao, LoaiChiTiet, TrangThai, DoanhThu DESC);

-- Tạo event scheduler để tự động tạo báo cáo hàng ngày (optional)
-- SET GLOBAL event_scheduler = ON;
-- 
-- DELIMITER //
-- 
-- CREATE EVENT ev_TaoBaoCaoHangNgay
-- ON SCHEDULE EVERY 1 DAY
-- STARTS '2024-01-01 23:59:00'
-- DO
-- BEGIN
--     DECLARE v_NgayHomQua DATE DEFAULT DATE_SUB(CURDATE(), INTERVAL 1 DAY);
--     
--     -- Tạo báo cáo cho từng cửa hàng
--     INSERT INTO BaoCaoDoanhThu (LoaiBaoCao, TuNgay, DenNgay, MaCH, GhiChu)
--     SELECT 
--         'NGAY',
--         v_NgayHomQua,
--         v_NgayHomQua,
--         ch.MaCH,
--         CONCAT('Báo cáo tự động ngày ', v_NgayHomQua)
--     FROM CuaHang ch
--     WHERE ch.TrangThai = 1
--     AND NOT EXISTS (
--         SELECT 1 FROM BaoCaoDoanhThu bc 
--         WHERE bc.LoaiBaoCao = 'NGAY' 
--         AND bc.TuNgay = v_NgayHomQua 
--         AND bc.MaCH = ch.MaCH
--         AND bc.TrangThai = 1
--     );
-- END //
-- 
-- DELIMITER ;