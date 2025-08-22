-- ===================================
-- BaoCaoDoanhThu Enhanced Relationships Migration
-- ===================================
-- This script adds new relationships and fields to BaoCaoDoanhThu entity
-- Run this after the existing baocaodoanhthu_hoadon_migration.sql

-- Add new columns to BaoCaoDoanhThu table
ALTER TABLE BaoCaoDoanhThu 
ADD COLUMN IF NOT EXISTS MaNVTao VARCHAR(10),
ADD COLUMN IF NOT EXISTS MaCH VARCHAR(10),
ADD COLUMN IF NOT EXISTS TongChiPhi DECIMAL(18,2) DEFAULT 0.00,
ADD COLUMN IF NOT EXISTS SoLuongHoaDon INT DEFAULT 0;

-- Add foreign key constraints
ALTER TABLE BaoCaoDoanhThu 
ADD CONSTRAINT FK_BaoCaoDoanhThu_NhanVien 
FOREIGN KEY (MaNVTao) REFERENCES NhanVien(MaNV) ON DELETE SET NULL;

ALTER TABLE BaoCaoDoanhThu 
ADD CONSTRAINT FK_BaoCaoDoanhThu_CuaHang 
FOREIGN KEY (MaCH) REFERENCES CuaHang(MaCH) ON DELETE SET NULL;

-- Create junction table for BaoCaoDoanhThu and PhieuNhapHang
CREATE TABLE IF NOT EXISTS BaoCaoDoanhThu_PhieuNhap (
    MaBaoCao VARCHAR(20) NOT NULL,
    MaPN INT NOT NULL,
    NgayLienKet DATETIME DEFAULT CURRENT_TIMESTAMP,
    NguoiTao VARCHAR(10),
    PRIMARY KEY (MaBaoCao, MaPN),
    FOREIGN KEY (MaBaoCao) REFERENCES BaoCaoDoanhThu(MaBaoCao) ON DELETE CASCADE,
    FOREIGN KEY (MaPN) REFERENCES PhieuNhapHang(MaPN) ON DELETE CASCADE,
    FOREIGN KEY (NguoiTao) REFERENCES NhanVien(MaNV) ON DELETE SET NULL
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_baocao_nhanvientao ON BaoCaoDoanhThu(MaNVTao);
CREATE INDEX IF NOT EXISTS idx_baocao_cuahang ON BaoCaoDoanhThu(MaCH);
CREATE INDEX IF NOT EXISTS idx_baocao_phieunhap_baocao ON BaoCaoDoanhThu_PhieuNhap(MaBaoCao);
CREATE INDEX IF NOT EXISTS idx_baocao_phieunhap_phieunhap ON BaoCaoDoanhThu_PhieuNhap(MaPN);
CREATE INDEX IF NOT EXISTS idx_baocao_phieunhap_ngaylienket ON BaoCaoDoanhThu_PhieuNhap(NgayLienKet);

-- ===================================
-- STORED PROCEDURES
-- ===================================

-- Procedure to link PhieuNhapHang to BaoCaoDoanhThu based on date range
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS LinkPhieuNhapHangToBaoCao(
    IN p_MaBaoCao VARCHAR(20),
    IN p_TuNgay DATE,
    IN p_DenNgay DATE,
    IN p_NguoiTao VARCHAR(10)
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_MaPN INT;
    
    DECLARE phieunhap_cursor CURSOR FOR
        SELECT MaPN 
        FROM PhieuNhapHang 
        WHERE DATE(NgayNhap) BETWEEN p_TuNgay AND p_DenNgay
        AND TrangThai = 1 -- Đã nhập kho
        AND IsDeleted = FALSE;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN phieunhap_cursor;
    
    read_loop: LOOP
        FETCH phieunhap_cursor INTO v_MaPN;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- Insert if not already linked
        INSERT IGNORE INTO BaoCaoDoanhThu_PhieuNhap (MaBaoCao, MaPN, NguoiTao)
        VALUES (p_MaBaoCao, v_MaPN, p_NguoiTao);
    END LOOP;
    
    CLOSE phieunhap_cursor;
    
    -- Update TongChiPhi in BaoCaoDoanhThu
    UPDATE BaoCaoDoanhThu 
    SET TongChiPhi = (
        SELECT COALESCE(SUM(pn.TongTienNhap), 0)
        FROM BaoCaoDoanhThu_PhieuNhap bpn
        JOIN PhieuNhapHang pn ON bpn.MaPN = pn.MaPN
        WHERE bpn.MaBaoCao = p_MaBaoCao
        AND pn.IsDeleted = FALSE
    )
    WHERE MaBaoCao = p_MaBaoCao;
END //
DELIMITER ;

-- Procedure to calculate comprehensive statistics for BaoCaoDoanhThu
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS CapNhatThongKeBaoCaoToanDien(
    IN p_MaBaoCao VARCHAR(20)
)
BEGIN
    DECLARE v_TongDoanhThu DECIMAL(18,2) DEFAULT 0;
    DECLARE v_TongChiPhi DECIMAL(18,2) DEFAULT 0;
    DECLARE v_SoLuongHoaDon INT DEFAULT 0;
    DECLARE v_TongSoHoaDon INT DEFAULT 0;
    
    -- Calculate revenue from linked HoaDon
    SELECT 
        COALESCE(SUM(hd.TongTien), 0),
        COUNT(hd.MaHD)
    INTO v_TongDoanhThu, v_SoLuongHoaDon
    FROM BaoCaoDoanhThu_HoaDon bh
    JOIN HoaDon hd ON bh.MaHD = hd.MaHD
    WHERE bh.MaBaoCao = p_MaBaoCao
    AND hd.IsDeleted = FALSE
    AND hd.TrangThai = 1;
    
    -- Calculate cost from linked PhieuNhapHang
    SELECT COALESCE(SUM(pn.TongTienNhap), 0)
    INTO v_TongChiPhi
    FROM BaoCaoDoanhThu_PhieuNhap bpn
    JOIN PhieuNhapHang pn ON bpn.MaPN = pn.MaPN
    WHERE bpn.MaBaoCao = p_MaBaoCao
    AND pn.IsDeleted = FALSE;
    
    -- Get total invoice count for the period (for comparison)
    SELECT TongSoHoaDon INTO v_TongSoHoaDon
    FROM BaoCaoDoanhThu
    WHERE MaBaoCao = p_MaBaoCao;
    
    -- Update BaoCaoDoanhThu with calculated values
    UPDATE BaoCaoDoanhThu 
    SET 
        TongDoanhThu = v_TongDoanhThu,
        TongChiPhi = v_TongChiPhi,
        SoLuongHoaDon = v_SoLuongHoaDon,
        DoanhThuTrungBinh = CASE 
            WHEN v_SoLuongHoaDon > 0 THEN v_TongDoanhThu / v_SoLuongHoaDon 
            ELSE 0 
        END,
        HoaDonTrungBinh = CASE 
            WHEN v_TongSoHoaDon > 0 THEN v_TongDoanhThu / v_TongSoHoaDon 
            ELSE 0 
        END,
        NgayCapNhat = CURRENT_TIMESTAMP
    WHERE MaBaoCao = p_MaBaoCao;
END //
DELIMITER ;

-- ===================================
-- TRIGGERS
-- ===================================

-- Trigger to auto-update statistics when HoaDon is linked/unlinked
DELIMITER //
CREATE TRIGGER IF NOT EXISTS trg_BaoCaoHoaDon_AfterInsert
AFTER INSERT ON BaoCaoDoanhThu_HoaDon
FOR EACH ROW
BEGIN
    CALL CapNhatThongKeBaoCaoToanDien(NEW.MaBaoCao);
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER IF NOT EXISTS trg_BaoCaoHoaDon_AfterDelete
AFTER DELETE ON BaoCaoDoanhThu_HoaDon
FOR EACH ROW
BEGIN
    CALL CapNhatThongKeBaoCaoToanDien(OLD.MaBaoCao);
END //
DELIMITER ;

-- Trigger to auto-update cost when PhieuNhapHang is linked/unlinked
DELIMITER //
CREATE TRIGGER IF NOT EXISTS trg_BaoCaoPhieuNhap_AfterInsert
AFTER INSERT ON BaoCaoDoanhThu_PhieuNhap
FOR EACH ROW
BEGIN
    UPDATE BaoCaoDoanhThu 
    SET TongChiPhi = (
        SELECT COALESCE(SUM(pn.TongTienNhap), 0)
        FROM BaoCaoDoanhThu_PhieuNhap bpn
        JOIN PhieuNhapHang pn ON bpn.MaPN = pn.MaPN
        WHERE bpn.MaBaoCao = NEW.MaBaoCao
        AND pn.IsDeleted = FALSE
    ),
    NgayCapNhat = CURRENT_TIMESTAMP
    WHERE MaBaoCao = NEW.MaBaoCao;
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER IF NOT EXISTS trg_BaoCaoPhieuNhap_AfterDelete
AFTER DELETE ON BaoCaoDoanhThu_PhieuNhap
FOR EACH ROW
BEGIN
    UPDATE BaoCaoDoanhThu 
    SET TongChiPhi = (
        SELECT COALESCE(SUM(pn.TongTienNhap), 0)
        FROM BaoCaoDoanhThu_PhieuNhap bpn
        JOIN PhieuNhapHang pn ON bpn.MaPN = pn.MaPN
        WHERE bpn.MaBaoCao = OLD.MaBaoCao
        AND pn.IsDeleted = FALSE
    ),
    NgayCapNhat = CURRENT_TIMESTAMP
    WHERE MaBaoCao = OLD.MaBaoCao;
END //
DELIMITER ;

-- ===================================
-- SAMPLE DATA UPDATES
-- ===================================

-- Update existing BaoCaoDoanhThu records with default relationships (optional)
-- This links reports to the first available employee and store if not already set

UPDATE BaoCaoDoanhThu 
SET MaNVTao = (SELECT MaNV FROM NhanVien WHERE TrangThai = 1 LIMIT 1)
WHERE MaNVTao IS NULL;

UPDATE BaoCaoDoanhThu 
SET MaCH = (SELECT MaCH FROM CuaHang LIMIT 1)
WHERE MaCH IS NULL;

-- ===================================
-- VERIFICATION QUERIES
-- ===================================

-- Check the new structure
SELECT 
    'BaoCaoDoanhThu Enhanced Structure' as Info,
    COUNT(*) as TotalReports,
    COUNT(MaNVTao) as ReportsWithEmployee,
    COUNT(MaCH) as ReportsWithStore,
    AVG(TongChiPhi) as AvgCost
FROM BaoCaoDoanhThu;

-- Check junction tables
SELECT 
    'Junction Tables' as Info,
    (SELECT COUNT(*) FROM BaoCaoDoanhThu_HoaDon) as HoaDonLinks,
    (SELECT COUNT(*) FROM BaoCaoDoanhThu_PhieuNhap) as PhieuNhapLinks;

COMMIT;
