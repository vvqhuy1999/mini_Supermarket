-- Migration script for BaoCaoDoanhThu_HoaDon junction table
-- This creates the Many-to-Many relationship between BaoCaoDoanhThu and HoaDon

-- Create the junction table
CREATE TABLE IF NOT EXISTS BaoCaoDoanhThu_HoaDon (
    MaBaoCao VARCHAR(20) NOT NULL,
    MaHD INT NOT NULL,
    NgayLienKet DATETIME DEFAULT CURRENT_TIMESTAMP,
    NguoiTao VARCHAR(50),
    PRIMARY KEY (MaBaoCao, MaHD),
    
    -- Foreign key constraints
    CONSTRAINT FK_BaoCaoDoanhThu_HoaDon_BaoCao 
        FOREIGN KEY (MaBaoCao) REFERENCES BaoCaoDoanhThu(MaBaoCao) 
        ON DELETE CASCADE ON UPDATE CASCADE,
        
    CONSTRAINT FK_BaoCaoDoanhThu_HoaDon_HoaDon 
        FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_baocao_hoadon_baocao ON BaoCaoDoanhThu_HoaDon(MaBaoCao);
CREATE INDEX idx_baocao_hoadon_hoadon ON BaoCaoDoanhThu_HoaDon(MaHD);
CREATE INDEX idx_baocao_hoadon_ngaylienket ON BaoCaoDoanhThu_HoaDon(NgayLienKet);

-- Add comments for documentation
ALTER TABLE BaoCaoDoanhThu_HoaDon 
COMMENT = 'Junction table linking BaoCaoDoanhThu with HoaDon entities';

ALTER TABLE BaoCaoDoanhThu_HoaDon 
MODIFY COLUMN MaBaoCao BIGINT NOT NULL COMMENT 'Foreign key to BaoCaoDoanhThu.MaBaoCao';

ALTER TABLE BaoCaoDoanhThu_HoaDon 
MODIFY COLUMN MaHD INT NOT NULL COMMENT 'Foreign key to HoaDon.MaHD';

ALTER TABLE BaoCaoDoanhThu_HoaDon 
MODIFY COLUMN NgayLienKet DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when the relationship was created';

ALTER TABLE BaoCaoDoanhThu_HoaDon 
MODIFY COLUMN NguoiTao VARCHAR(50) COMMENT 'User who created the relationship';

-- Sample data insertion procedure (optional)
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS LinkHoaDonsToBaoCao(
    IN p_MaBaoCao BIGINT,
    IN p_TuNgay DATETIME,
    IN p_DenNgay DATETIME
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_MaHD INT;
    DECLARE cur CURSOR FOR 
        SELECT MaHD FROM HoaDon 
        WHERE NgayLap BETWEEN p_TuNgay AND p_DenNgay 
        AND TrangThai = 1 
        AND IsDeleted = FALSE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO v_MaHD;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- Insert if not exists
        INSERT IGNORE INTO BaoCaoDoanhThu_HoaDon (MaBaoCao, MaHD, NgayLienKet)
        VALUES (p_MaBaoCao, v_MaHD, NOW());
    END LOOP;
    CLOSE cur;
END //
DELIMITER ;

-- Example usage:
-- CALL LinkHoaDonsToBaoCao(1, '2024-01-01 00:00:00', '2024-01-31 23:59:59');
