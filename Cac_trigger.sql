USE QuanLySieuThi;

-- ===== TẠO CÁC TRIGGER VÀ VIEW HỖ TRỢ =====

-- Trigger tự động cập nhật tổng tiền phiếu nhập
DELIMITER //
CREATE TRIGGER TR_UpdateTongTienPhieuNhap
    AFTER INSERT ON ChiTietPhieuNhap
    FOR EACH ROW
BEGIN
    UPDATE PhieuNhapHang
    SET TongTienNhap = (
        SELECT COALESCE(SUM(ThanhTien), 0)
        FROM ChiTietPhieuNhap
        WHERE MaPN = NEW.MaPN AND IsDeleted = 0
    )
    WHERE MaPN = NEW.MaPN;
END //
DELIMITER ;

-- Trigger tự động cập nhật tồn kho khi nhập hàng
DELIMITER //
CREATE TRIGGER TR_UpdateTonKhoNhapHang
    AFTER INSERT ON ChiTietPhieuNhap
    FOR EACH ROW
BEGIN
    INSERT INTO TonKhoChiTiet (MaSP, MaKho, SoLuongTon)
    SELECT NEW.MaSP, pn.MaKho, NEW.SoLuongNhap
    FROM PhieuNhapHang pn
    WHERE pn.MaPN = NEW.MaPN
        ON DUPLICATE KEY UPDATE
                             SoLuongTon = SoLuongTon + NEW.SoLuongNhap;
END //
DELIMITER ;

-- Trigger tự động cập nhật tổng tiền hóa đơn
DELIMITER //
CREATE TRIGGER TR_UpdateTongTienHoaDon
    AFTER INSERT ON ChiTietHoaDon
    FOR EACH ROW
BEGIN
    UPDATE HoaDon
    SET TongTienHang = (
        SELECT COALESCE(SUM(ThanhTienSauGiam), 0)
        FROM ChiTietHoaDon
        WHERE MaHD = NEW.MaHD AND IsDeleted = 0
    )
    WHERE MaHD = NEW.MaHD;
END //
DELIMITER ;

-- View hiển thị thông tin sản phẩm kèm tồn kho
CREATE VIEW V_SanPhamTonKho AS
SELECT
    sp.MaSP,
    sp.TenSP,
    lsp.TenLoai,
    sp.GiaBan,
    sp.DonViTinh,
    COALESCE(SUM(tk.SoLuongTon), 0) as TongTonKho,
    COUNT(tk.MaKho) as SoKhoTon
FROM SanPham sp
         LEFT JOIN LoaiSanPham lsp ON sp.MaLoaiSP = lsp.MaLoaiSP
         LEFT JOIN TonKhoChiTiet tk ON sp.MaSP = tk.MaSP AND tk.IsDeleted = 0
WHERE sp.IsDeleted = 0
GROUP BY sp.MaSP, sp.TenSP, lsp.TenLoai, sp.GiaBan, sp.DonViTinh;

-- View thống kê doanh thu theo tháng
CREATE VIEW V_DoanhThuTheoThang AS
SELECT
        YEAR(NgayLap) as Nam,
        MONTH(NgayLap) as Thang,
        COUNT(*) as SoHoaDon,
        SUM(TongTien) as TongDoanhThu,
        AVG(TongTien) as DoanhThuTrungBinh
        FROM HoaDon
        WHERE TrangThai = 1 AND IsDeleted = 0
        GROUP BY YEAR(NgayLap), MONTH(NgayLap)
        ORDER BY Nam DESC, Thang DESC;

-- View top sản phẩm bán chạy
CREATE VIEW V_TopSanPhamBanChay AS
SELECT
    sp.MaSP,
    sp.TenSP,
    lsp.TenLoai,
    SUM(cthd.SoLuong) as TongSoLuongBan,
    SUM(cthd.ThanhTienSauGiam) as TongDoanhThu,
    COUNT(DISTINCT cthd.MaHD) as SoHoaDonCo
FROM SanPham sp
         JOIN ChiTietHoaDon cthd ON sp.MaSP = cthd.MaSP
         JOIN HoaDon hd ON cthd.MaHD = hd.MaHD
         JOIN LoaiSanPham lsp ON sp.MaLoaiSP = lsp.MaLoaiSP
WHERE hd.TrangThai = 1 AND hd.IsDeleted = 0 AND cthd.IsDeleted = 0
GROUP BY sp.MaSP, sp.TenSP, lsp.TenLoai
ORDER BY TongSoLuongBan DESC;

-- Tạo các index bổ sung để tối ưu hiệu suất
CREATE INDEX idx_hoadon_composite ON HoaDon(TrangThai, NgayLap, IsDeleted);
CREATE INDEX idx_chitiethoadon_composite ON ChiTietHoaDon(MaHD, MaSP, IsDeleted);
CREATE INDEX idx_tonkho_composite ON TonKhoChiTiet(MaSP, MaKho, IsDeleted);

-- Kết thúc script tạo database
-- SELECT 'Database QuanLySieuThi đã được tạo thành công!' as KetQua;


-- ===== FUNCTIONS TỰ SINH MÃ =====

-- Function tự sinh mã sản phẩm (SP001, SP002...)
DELIMITER $$
CREATE FUNCTION Fn_TaoMaSPTuDong()
    RETURNS NVARCHAR(10)
                    DETERMINISTIC
BEGIN
    DECLARE MaSP NVARCHAR(10);
    DECLARE So INT;

SELECT CAST(RIGHT(MAX(MaSP), 3) AS UNSIGNED) INTO So FROM SanPham;
IF So IS NULL THEN
        SET So = 0;
END IF;
    SET MaSP = CONCAT('SP', LPAD(So + 1, 3, '0'));
RETURN MaSP;
END$$
DELIMITER ;

-- Function tự sinh mã cửa hàng (CH001, CH002...)
DELIMITER $$
CREATE FUNCTION Fn_TaoMaCH()
    RETURNS NVARCHAR(10)
                    DETERMINISTIC
BEGIN
    DECLARE newMaCH NVARCHAR(10);
    DECLARE maxMa INT;

SELECT MAX(CAST(SUBSTRING(MaCH, 3) AS UNSIGNED)) INTO maxMa FROM CuaHang;
IF maxMa IS NULL THEN
        SET maxMa = 0;
END IF;

    SET newMaCH = CONCAT('CH', LPAD(maxMa + 1, 3, '0'));
RETURN newMaCH;
END$$
DELIMITER ;

-- Function tự sinh mã khách hàng (KH001, KH002...)
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

-- Function tự sinh mã khuyến mãi (KM001, KM002...)
DELIMITER $$
CREATE FUNCTION Fn_TaoMaKM()
    RETURNS NVARCHAR(10)
                    DETERMINISTIC
BEGIN
    DECLARE maxSo INT;
    DECLARE newMa NVARCHAR(10);

SELECT MAX(CAST(SUBSTRING(MaKM, 3) AS UNSIGNED))
INTO maxSo FROM KhuyenMai;

IF maxSo IS NULL THEN
        SET maxSo = 0;
END IF;

    SET newMa = CONCAT('KM', LPAD(maxSo + 1, 3, '0'));
RETURN newMa;
END$$
DELIMITER ;

-- Function tự sinh mã loại sản phẩm (LSP001, LSP002...)
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

-- Function tự sinh mã người dùng (ND001, ND002...)
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

-- Function tự sinh mã nhà cung cấp (NCC001, NCC002...)
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

-- Function tự sinh mã nhân viên (NV001, NV002...)
DELIMITER $$
CREATE FUNCTION Fn_TaoMaNV()
    RETURNS NVARCHAR(10)
                    DETERMINISTIC
BEGIN
    DECLARE maxSo INT;
    DECLARE newMa NVARCHAR(10);

SELECT MAX(CAST(SUBSTRING(MaNV, 3) AS UNSIGNED)) INTO maxSo FROM NhanVien;
IF maxSo IS NULL THEN
        SET maxSo = 0;
END IF;

    SET newMa = CONCAT('NV', LPAD(maxSo + 1, 3, '0'));
RETURN newMa;
END$$
DELIMITER ;

-- Function tự sinh mã phương thức thanh toán (PTTT001, PTTT002...)
DELIMITER $$
CREATE FUNCTION Fn_TaoMaPTTT()
    RETURNS NVARCHAR(10)
                    DETERMINISTIC
BEGIN
    DECLARE maxSo INT;
    DECLARE newMa NVARCHAR(10);

SELECT MAX(CAST(SUBSTRING(MaPTTT, 5) AS UNSIGNED)) INTO maxSo FROM PhuongThucThanhToan;
IF maxSo IS NULL THEN
        SET maxSo = 0;
END IF;

    SET newMa = CONCAT('PTTT', LPAD(maxSo + 1, 3, '0'));
RETURN newMa;
END$$
DELIMITER ;

-- ===== FUNCTIONS TÍNH TOÁN =====

-- Function trả về số lượng tồn kho hiện tại của sản phẩm
DELIMITER $$
CREATE FUNCTION Fn_LayTonKho(p_MaSP NVARCHAR(10), p_MaKho INT)
    RETURNS INT
    DETERMINISTIC
    READS SQL DATA
BEGIN
    DECLARE v_SoLuongTon INT;

SELECT COALESCE(SoLuongTon, 0)
INTO v_SoLuongTon
FROM TonKhoChiTiet
WHERE MaSP = p_MaSP AND MaKho = p_MaKho AND IsDeleted = 0;

RETURN IFNULL(v_SoLuongTon, 0);
END$$
DELIMITER ;

-- Function tính tổng tiền hóa đơn
DELIMITER $$
CREATE FUNCTION Fn_TinhTongTienHoaDon(p_MaHD INT)
    RETURNS DECIMAL(18,2)
    DETERMINISTIC
    READS SQL DATA
BEGIN
    DECLARE v_TongTien DECIMAL(18,2);

SELECT COALESCE(SUM(ThanhTienSauGiam), 0)
INTO v_TongTien
FROM ChiTietHoaDon
WHERE MaHD = p_MaHD AND IsDeleted = 0;

RETURN IFNULL(v_TongTien, 0);
END$$
DELIMITER ;

-- Function kiểm tra mã khuyến mãi còn hiệu lực
DELIMITER $$
CREATE FUNCTION Fn_KiemTraKhuyenMai(p_MaKM NVARCHAR(10))
    RETURNS BOOLEAN
    DETERMINISTIC
    READS SQL DATA
BEGIN
    DECLARE v_Count INT DEFAULT 0;

SELECT COUNT(*) INTO v_Count
FROM KhuyenMai
WHERE MaKM = p_MaKM
  AND IsDeleted = 0
  AND TrangThai = 1
  AND NgayBatDau <= NOW()
  AND (NgayKetThuc IS NULL OR NgayKetThuc >= NOW())
  AND (SoLuongToiDa IS NULL OR DaSuDung < SoLuongToiDa);

RETURN v_Count > 0;
END$$
DELIMITER ;

-- Function tính điểm tích lũy từ tổng tiền
DELIMITER $$
CREATE FUNCTION Fn_TinhDiemTichLuy(p_TongTien DECIMAL(15,2))
    RETURNS INT
    DETERMINISTIC
BEGIN
    -- Quy đổi: 1000 VND = 1 điểm
    DECLARE v_Diem INT;
    SET v_Diem = FLOOR(p_TongTien / 1000);
RETURN v_Diem;
END$$
DELIMITER ;

-- ===== STORED PROCEDURES =====

-- Thống kê doanh thu theo tháng
DELIMITER $$
CREATE PROCEDURE Sp_ThongKeDoanhThuTheoThang(
    IN p_Nam INT,
    IN p_Thang INT
)
BEGIN
SELECT
    DATE(hd.NgayLap) AS Ngay,
    COUNT(*) AS SoHoaDon,
    SUM(hd.TongTien) AS DoanhThu,
    AVG(hd.TongTien) AS DoanhThuTrungBinh
FROM HoaDon hd
WHERE YEAR(hd.NgayLap) = p_Nam
  AND MONTH(hd.NgayLap) = p_Thang
  AND hd.TrangThai = 1
  AND hd.IsDeleted = 0
GROUP BY DATE(hd.NgayLap)
ORDER BY Ngay;
END$$
DELIMITER ;

-- Thống kê sản phẩm bán chạy
DELIMITER $$
CREATE PROCEDURE Sp_ThongKeSanPhamBanChay(
    IN p_NgayTu DATE,
    IN p_NgayDen DATE,
    IN p_Limit INT
)
BEGIN
SELECT
    sp.MaSP,
    sp.TenSP,
    lsp.TenLoai,
    SUM(cthd.SoLuong) AS TongSoLuongBan,
    SUM(cthd.ThanhTienSauGiam) AS TongDoanhThu,
    COUNT(DISTINCT cthd.MaHD) AS SoHoaDonCo,
    AVG(cthd.DonGiaBan) AS GiaTrungBinh
FROM SanPham sp
         JOIN ChiTietHoaDon cthd ON sp.MaSP = cthd.MaSP
         JOIN HoaDon hd ON cthd.MaHD = hd.MaHD
         JOIN LoaiSanPham lsp ON sp.MaLoaiSP = lsp.MaLoaiSP
WHERE DATE(hd.NgayLap) BETWEEN p_NgayTu AND p_NgayDen
  AND hd.TrangThai = 1
  AND hd.IsDeleted = 0
  AND cthd.IsDeleted = 0
GROUP BY sp.MaSP, sp.TenSP, lsp.TenLoai
ORDER BY TongSoLuongBan DESC
    LIMIT p_Limit;
END$$
DELIMITER ;

-- Procedure cập nhật điểm tích lũy khách hàng
DELIMITER $$
CREATE PROCEDURE Sp_CapNhatDiemTichLuy(
    IN p_MaKH NVARCHAR(10),
    IN p_DiemThem INT
        )
BEGIN
    DECLARE v_DiemHienTai INT DEFAULT 0;
    DECLARE v_LoaiKH NVARCHAR(50);

    -- Lấy điểm hiện tại
SELECT DiemTichLuy INTO v_DiemHienTai
FROM KhachHang
WHERE MaKH = p_MaKH AND IsDeleted = 0;

-- Cộng điểm mới
SET v_DiemHienTai = v_DiemHienTai + p_DiemThem;

    -- Xác định loại khách hàng dựa trên điểm
CASE
        WHEN v_DiemHienTai >= 10000 THEN SET v_LoaiKH = 'Kim cương';
WHEN v_DiemHienTai >= 5000 THEN SET v_LoaiKH = 'Vàng';
WHEN v_DiemHienTai >= 2000 THEN SET v_LoaiKH = 'Bạc';
WHEN v_DiemHienTai >= 500 THEN SET v_LoaiKH = 'VIP';
ELSE SET v_LoaiKH = 'Thường';
END CASE;

    -- Cập nhật thông tin khách hàng
UPDATE KhachHang
SET DiemTichLuy = v_DiemHienTai,
    LoaiKhachHang = v_LoaiKH
WHERE MaKH = p_MaKH;

SELECT v_DiemHienTai AS DiemMoi, v_LoaiKH AS LoaiKhachHang;
END$$
DELIMITER ;

-- ===== TRIGGERS TỰ SINH MÃ =====

-- Trigger tự sinh mã sản phẩm
DELIMITER $$
CREATE TRIGGER Tg_TuSinhMaSP
    BEFORE INSERT ON SanPham
    FOR EACH ROW
BEGIN
    IF NEW.MaSP IS NULL OR NEW.MaSP = '' THEN
        SET NEW.MaSP = Fn_TaoMaSPTuDong();
END IF;

-- Kiểm tra tên sản phẩm
IF NEW.TenSP IS NULL OR TRIM(NEW.TenSP) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên sản phẩm không được để trống';
END IF;

    -- Kiểm tra giá bán
    IF NEW.GiaBan IS NULL OR NEW.GiaBan <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Giá bán phải lớn hơn 0';
END IF;
END$$
DELIMITER ;

-- Trigger tự sinh mã cửa hàng
DELIMITER $$
CREATE TRIGGER Tg_TuSinhMaCH
    BEFORE INSERT ON CuaHang
    FOR EACH ROW
BEGIN
    IF NEW.MaCH IS NULL OR NEW.MaCH = '' THEN
        SET NEW.MaCH = Fn_TaoMaCH();
END IF;

IF NEW.TenCH IS NULL OR TRIM(NEW.TenCH) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên cửa hàng không được để trống';
END IF;
END$$
DELIMITER ;

-- Trigger tự sinh mã khách hàng
DELIMITER $$
CREATE TRIGGER Tg_ThemKhachHang
    BEFORE INSERT ON KhachHang
    FOR EACH ROW
BEGIN
    IF NEW.MaKH IS NULL OR NEW.MaKH = '' THEN
        SET NEW.MaKH = Fn_TaoMaKH();
END IF;

-- Chuẩn hóa dữ liệu
IF NEW.SDT IS NOT NULL THEN
        SET NEW.SDT = REPLACE(NEW.SDT, ' ', '');
END IF;

    IF NEW.Email IS NOT NULL THEN
        SET NEW.Email = LOWER(TRIM(NEW.Email));
END IF;

    -- Kiểm tra điểm tích lũy
    IF NEW.DiemTichLuy IS NULL OR NEW.DiemTichLuy < 0 THEN
        SET NEW.DiemTichLuy = 0;
END IF;

    -- Thiết lập loại khách hàng mặc định
    IF NEW.LoaiKhachHang IS NULL THEN
        SET NEW.LoaiKhachHang = 'Thường';
END IF;
END$$
DELIMITER ;

-- Trigger tự sinh mã khuyến mãi
DELIMITER $$
CREATE TRIGGER Tg_ThemKhuyenMai
    BEFORE INSERT ON KhuyenMai
    FOR EACH ROW
BEGIN
    IF NEW.MaKM IS NULL OR NEW.MaKM = '' THEN
        SET NEW.MaKM = Fn_TaoMaKM();
END IF;

-- Chuẩn hóa loại khuyến mãi
IF NEW.LoaiKM IS NOT NULL THEN
        SET NEW.LoaiKM = CONCAT(UPPER(LEFT(NEW.LoaiKM,1)), LOWER(SUBSTRING(NEW.LoaiKM,2)));
END IF;

    -- Kiểm tra giá trị khuyến mãi
    IF NEW.GiaTriKM IS NULL OR NEW.GiaTriKM < 0 THEN
        SET NEW.GiaTriKM = 0;
END IF;

    -- Kiểm tra ngày
    IF NEW.NgayKetThuc < NEW.NgayBatDau THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày kết thúc phải sau ngày bắt đầu';
END IF;

    -- Khởi tạo số lượng đã sử dụng
    IF NEW.DaSuDung IS NULL THEN
        SET NEW.DaSuDung = 0;
END IF;
END$$
DELIMITER ;

-- Function tự sinh mã loại sản phẩm (đã được định nghĩa ở trên) --

-- Trigger tự sinh mã loại sản phẩm --
DELIMITER $$
CREATE TRIGGER Tg_ThemLoaiSanPham
    BEFORE INSERT ON LoaiSanPham
    FOR EACH ROW
BEGIN
    IF NEW.MaLoaiSP IS NULL OR NEW.MaLoaiSP = '' THEN
        SET NEW.MaLoaiSP = Fn_TaoMaLoaiSP();
END IF;
END$$
DELIMITER ;


-- Trigger tự sinh mã người dùng
DELIMITER $$
CREATE TRIGGER Tg_ThemNguoiDung
    BEFORE INSERT ON NguoiDung
    FOR EACH ROW
BEGIN
    -- Tự sinh mã nếu để trống
    IF NEW.MaNguoiDung IS NULL OR NEW.MaNguoiDung = '' THEN
        SET NEW.MaNguoiDung = Fn_TaoMaNguoiDung();
END IF;

-- Kiểm tra Email không rỗng
IF NEW.Email IS NULL OR TRIM(NEW.Email) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Email không được để trống';
END IF;

    -- Kiểm tra mật khẩu không rỗng
    IF NEW.MatKhau IS NULL OR TRIM(NEW.MatKhau) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Mật khẩu không được để trống';
END IF;

    -- Kiểm tra vai trò hợp lệ (0-3)
    IF NEW.VaiTro NOT IN (0, 1, 2, 3) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Vai trò không hợp lệ (chỉ từ 0 đến 3)';
END IF;

    -- Chuẩn hóa email
    SET NEW.Email = LOWER(TRIM(NEW.Email));
END$$
DELIMITER ;

-- Trigger tự sinh mã nhà cung cấp
DELIMITER $$
CREATE TRIGGER Tg_ThemNhaCungCap
    BEFORE INSERT ON NhaCungCap
    FOR EACH ROW
BEGIN
    -- Tự sinh mã nếu bỏ trống
    IF NEW.MaNCC IS NULL OR NEW.MaNCC = '' THEN
        SET NEW.MaNCC = Fn_TaoMaNCC();
END IF;

-- Kiểm tra tên không để trống
IF NEW.TenNCC IS NULL OR TRIM(NEW.TenNCC) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Tên nhà cung cấp không được để trống';
END IF;

    -- Chuẩn hóa email nếu có
    IF NEW.Email IS NOT NULL THEN
        SET NEW.Email = LOWER(TRIM(NEW.Email));
END IF;

    -- Thiết lập trạng thái mặc định
    IF NEW.TrangThai IS NULL THEN
        SET NEW.TrangThai = 1;
END IF;
END$$
DELIMITER ;

-- Trigger tự sinh mã nhân viên
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

    -- Kiểm tra ngày sinh hợp lệ
    IF NEW.NgaySinh IS NOT NULL AND NEW.NgaySinh >= CURRENT_DATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày sinh không hợp lệ';
END IF;

    -- Kiểm tra ngày vào làm
    IF NEW.NgayVaoLam IS NOT NULL AND NEW.NgaySinh IS NOT NULL AND NEW.NgayVaoLam <= NEW.NgaySinh THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày vào làm phải sau ngày sinh';
END IF;

    -- Thiết lập trạng thái mặc định
    IF NEW.TrangThai IS NULL THEN
        SET NEW.TrangThai = 1;
END IF;
END$$
DELIMITER ;

-- Trigger tự sinh mã phương thức thanh toán
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

    -- Kiểm tra phí giao dịch
    IF NEW.PhiGiaoDich IS NULL OR NEW.PhiGiaoDich < 0 THEN
        SET NEW.PhiGiaoDich = 0;
END IF;

    -- Thiết lập trạng thái mặc định
    IF NEW.TrangThai IS NULL THEN
        SET NEW.TrangThai = 1;
END IF;
END$$
DELIMITER ;

-- ===== TRIGGERS CẬP NHẬT TỒN KHO =====

-- Trigger cập nhật tồn kho khi nhập hàng
DELIMITER $$
CREATE TRIGGER Tg_CapNhatTonKho_Nhap
    AFTER INSERT ON ChiTietPhieuNhap
    FOR EACH ROW
BEGIN
    DECLARE v_MaKho INT;

    -- Lấy mã kho từ phiếu nhập
    SELECT MaKho INTO v_MaKho
    FROM PhieuNhapHang
    WHERE MaPN = NEW.MaPN;

    -- Cập nhật tồn kho
    INSERT INTO TonKhoChiTiet (MaSP, MaKho, SoLuongTon)
    VALUES (NEW.MaSP, v_MaKho, NEW.SoLuongNhap)
        ON DUPLICATE KEY UPDATE
                             SoLuongTon = SoLuongTon + NEW.SoLuongNhap,
                             NgayCapNhat = CURRENT_TIMESTAMP;
    END$$
    DELIMITER ;

-- Trigger cập nhật tồn kho khi bán hàng
DELIMITER $$
    CREATE TRIGGER Tg_CapNhatTonKho_Ban
        AFTER INSERT ON ChiTietHoaDon
        FOR EACH ROW
    BEGIN
        DECLARE v_MaCH NVARCHAR(10);
    DECLARE v_MaKho INT;
    DECLARE v_TonHienTai INT DEFAULT 0;

    -- Lấy cửa hàng từ nhân viên lập hóa đơn
        SELECT nv.MaCH INTO v_MaCH
        FROM HoaDon hd
                 JOIN NhanVien nv ON hd.MaNVLap = nv.MaNV
        WHERE hd.MaHD = NEW.MaHD;

        -- Lấy kho chính của cửa hàng
        SELECT MaKho INTO v_MaKho
        FROM Kho
        WHERE MaCH = v_MaCH AND TrangThai = 1
        ORDER BY MaKho
            LIMIT 1;

        IF v_MaKho IS NOT NULL THEN
        -- Kiểm tra tồn kho hiện tại
        SELECT SoLuongTon INTO v_TonHienTai
        FROM TonKhoChiTiet
        WHERE MaSP = NEW.MaSP AND MaKho = v_MaKho;

        -- Kiểm tra đủ hàng để bán
        IF v_TonHienTai < NEW.SoLuong THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Không đủ hàng trong kho để bán';
    END IF;

    -- Trừ tồn kho
    UPDATE TonKhoChiTiet
    SET SoLuongTon = SoLuongTon - NEW.SoLuong,
        NgayCapNhat = CURRENT_TIMESTAMP
    WHERE MaSP = NEW.MaSP AND MaKho = v_MaKho;
END IF;
END$$
DELIMITER ;

-- Trigger cập nhật tồn kho khi xuất kho
DELIMITER $$
CREATE TRIGGER Tg_CapNhatTonKho_Xuat
    AFTER INSERT ON ChiTietPhieuXuat
    FOR EACH ROW
BEGIN
    DECLARE v_MaKho INT;
    DECLARE v_TonHienTai INT DEFAULT 0;

    -- Lấy mã kho từ phiếu xuất
    SELECT MaKho INTO v_MaKho
    FROM PhieuXuatKho
    WHERE MaPXK = NEW.MaPXK;

    -- Kiểm tra tồn kho hiện tại
    SELECT SoLuongTon INTO v_TonHienTai
    FROM TonKhoChiTiet
    WHERE MaSP = NEW.MaSP AND MaKho = v_MaKho;

    -- Kiểm tra đủ hàng để xuất
    IF v_TonHienTai < NEW.SoLuongXuat THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không đủ hàng trong kho để xuất';
END IF;

-- Trừ tồn kho
UPDATE TonKhoChiTiet
SET SoLuongTon = SoLuongTon - NEW.SoLuongXuat,
    NgayCapNhat = CURRENT_TIMESTAMP
WHERE MaSP = NEW.MaSP AND MaKho = v_MaKho;
END$$
DELIMITER ;

-- ===== TRIGGERS CẬP NHẬT TỔNG TIỀN =====

-- Trigger tự động cập nhật tổng tiền phiếu nhập
DELIMITER $$
CREATE TRIGGER Tg_UpdateTongTienPhieuNhap_Insert
    AFTER INSERT ON ChiTietPhieuNhap
    FOR EACH ROW
BEGIN
    UPDATE PhieuNhapHang
    SET TongTienNhap = (
        SELECT COALESCE(SUM(ThanhTien), 0)
        FROM ChiTietPhieuNhap
        WHERE MaPN = NEW.MaPN AND IsDeleted = 0
    )
    WHERE MaPN = NEW.MaPN;
    END$$
    DELIMITER ;

-- Trigger cập nhật tổng tiền phiếu nhập khi sửa/xóa
DELIMITER $$
    CREATE TRIGGER Tg_UpdateTongTienPhieuNhap_Update
        AFTER UPDATE ON ChiTietPhieuNhap
        FOR EACH ROW
    BEGIN
        UPDATE PhieuNhapHang
        SET TongTienNhap = (
            SELECT COALESCE(SUM(ThanhTien), 0)
            FROM ChiTietPhieuNhap
            WHERE MaPN = NEW.MaPN AND IsDeleted = 0
        )
        WHERE MaPN = NEW.MaPN;
        END$$
        DELIMITER ;

-- Trigger tự động cập nhật tổng tiền hóa đơn
DELIMITER $$
        CREATE TRIGGER Tg_UpdateTongTienHoaDon_Insert
            AFTER INSERT ON ChiTietHoaDon
            FOR EACH ROW
        BEGIN
            UPDATE HoaDon
            SET TongTienHang = (
                SELECT COALESCE(SUM(ThanhTienSauGiam), 0)
                FROM ChiTietHoaDon
                WHERE MaHD = NEW.MaHD AND IsDeleted = 0
            )
            WHERE MaHD = NEW.MaHD;
            END$$
            DELIMITER ;

-- Trigger cập nhật tổng tiền hóa đơn khi sửa/xóa
DELIMITER $$
            CREATE TRIGGER Tg_UpdateTongTienHoaDon_Update
                AFTER UPDATE ON ChiTietHoaDon
                FOR EACH ROW
            BEGIN
                UPDATE HoaDon
                SET TongTienHang = (
                    SELECT COALESCE(SUM(ThanhTienSauGiam), 0)
                    FROM ChiTietHoaDon
                    WHERE MaHD = NEW.MaHD AND IsDeleted = 0
                )
                WHERE MaHD = NEW.MaHD;
                END$$
                DELIMITER ;

-- ===== TRIGGERS KIỂM TRA DỮ LIỆU =====

-- Trigger kiểm tra thêm hóa đơn
DELIMITER $$
                CREATE TRIGGER Tg_HoaDon_Insert
                    BEFORE INSERT ON HoaDon
                    FOR EACH ROW
                BEGIN
                    -- Kiểm tra ngày lập không được NULL
                    IF NEW.NgayLap IS NULL THEN
        SET NEW.NgayLap = CURRENT_TIMESTAMP;
                END IF;

                -- Kiểm tra trạng thái hợp lệ
                IF NEW.TrangThai NOT IN (0,1,2,3,4) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Trạng thái hóa đơn không hợp lệ';
            END IF;

            -- Thiết lập người tạo nếu chưa có
            IF NEW.NguoiTao IS NULL THEN
        SET NEW.NguoiTao = NEW.MaNVLap;
        END IF;

        -- Khởi tạo điểm tích lũy
        IF NEW.DiemTichLuy IS NULL THEN
        SET NEW.DiemTichLuy = 0;
    END IF;
    END$$
    DELIMITER ;

-- Trigger cập nhật hóa đơn
DELIMITER $$
    CREATE TRIGGER Tg_HoaDon_Update
        BEFORE UPDATE ON HoaDon
        FOR EACH ROW
    BEGIN
        -- Không cho chỉnh sửa hóa đơn đã thanh toán hoặc đã hủy (trừ khi set IsDeleted = 1)
        IF OLD.TrangThai = 1 AND NEW.TrangThai != 1 AND (NEW.IsDeleted IS NULL OR NEW.IsDeleted = 0) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể sửa hóa đơn đã thanh toán!';
    END IF;
    IF OLD.TrangThai = 3 AND NEW.TrangThai != 3 AND (NEW.IsDeleted IS NULL OR NEW.IsDeleted = 0) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể sửa hóa đơn đã hủy!';
END IF;

-- Kiểm tra trạng thái hợp lệ
IF NEW.TrangThai NOT IN (0,1,2,3,4) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Trạng thái hóa đơn không hợp lệ';
END IF;

    -- Tự động cập nhật ngày sửa
    SET NEW.NgaySua = CURRENT_TIMESTAMP;
END$$
DELIMITER ;

-- Trigger kiểm tra điểm tích lũy khách hàng cập nhật về âm (an toàn)
DELIMITER $$
CREATE TRIGGER Tg_KhachHang_Update
    BEFORE UPDATE ON KhachHang
    FOR EACH ROW
BEGIN
    IF NEW.DiemTichLuy < 0 THEN
        SET NEW.DiemTichLuy = 0;
END IF;
END$$
DELIMITER ;

-- Trigger kiểm tra (và chuẩn hóa) khi cập nhật thông tin khách hàng
DELIMITER $$
CREATE TRIGGER Tg_KhachHang_Normalize
    BEFORE UPDATE ON KhachHang
    FOR EACH ROW
BEGIN
    IF NEW.Email IS NOT NULL THEN
        SET NEW.Email = LOWER(TRIM(NEW.Email));
END IF;
IF NEW.SDT IS NOT NULL THEN
        SET NEW.SDT = REPLACE(NEW.SDT, ' ', '');
END IF;
END$$
DELIMITER ;

-- Trigger cập nhật điểm tích lũy và loại khách hàng sau khi hóa đơn được thanh toán
DELIMITER $$
CREATE TRIGGER Tg_CapNhatDiemTichLuySauThanhToan
    AFTER UPDATE ON HoaDon
    FOR EACH ROW
BEGIN
    -- Chỉ thực hiện khi trạng thái chuyển từ chưa thanh toán (0,2) sang đã thanh toán (1)
    IF OLD.TrangThai IN (0,2) AND NEW.TrangThai = 1 THEN
        CALL Sp_CapNhatDiemTichLuy(NEW.MaKH, Fn_TinhDiemTichLuy(NEW.TongTien));
END IF;
END$$
DELIMITER ;

-- Trigger tự động gán người sửa cho hóa đơn khi cập nhật
DELIMITER $$
CREATE TRIGGER Tg_HoaDon_AddNguoiSua
    BEFORE UPDATE ON HoaDon
    FOR EACH ROW
BEGIN
    IF NEW.NguoiSua IS NULL AND NEW.NguoiTao IS NOT NULL THEN
        SET NEW.NguoiSua = NEW.NguoiTao;
END IF;
END$$
DELIMITER ;


-- Procedure thống kê tồn kho
DELIMITER $$
CREATE PROCEDURE Sp_ThongKeTonKho(
    IN p_MaKho INT,
    IN p_MaSP NVARCHAR(10)
)
BEGIN
    SELECT 
        sp.MaSP,
        sp.TenSP,
        lsp.TenLoai,
        tk.SoLuongTon,
        tk.SoLuongToiThieu,
        tk.SoLuongToiDa,
        tk.NgayCapNhat,
        CASE 
            WHEN tk.SoLuongTon <= tk.SoLuongToiThieu THEN 'Cần nhập hàng'
            WHEN tk.SoLuongTon >= tk.SoLuongToiDa THEN 'Tồn kho cao'
            ELSE 'Bình thường'
        END as TrangThaiTonKho
    FROM TonKhoChiTiet tk
    JOIN SanPham sp ON tk.MaSP = sp.MaSP
    JOIN LoaiSanPham lsp ON sp.MaLoaiSP = lsp.MaLoaiSP
    WHERE tk.IsDeleted = 0 
        AND sp.IsDeleted = 0
        AND (p_MaKho IS NULL OR tk.MaKho = p_MaKho)
        AND (p_MaSP IS NULL OR tk.MaSP = p_MaSP)
    ORDER BY tk.SoLuongTon ASC;
END$$
DELIMITER ;

-- Procedure báo cáo nhân viên
DELIMITER $$
CREATE PROCEDURE Sp_BaoCaoNhanVien(
    IN p_MaCH NVARCHAR(10),
    IN p_Thang INT,
    IN p_Nam INT
)
BEGIN
    SELECT 
        nv.MaNV,
        nv.HoTen,
        nv.ChucVu,
        COUNT(llv.MaLich) as SoNgayLam,
        SUM(llv.GioVao IS NOT NULL AND llv.GioRa IS NOT NULL) as SoNgayLamThucTe,
        COUNT(DISTINCT hd.MaHD) as SoHoaDonLap,
        SUM(hd.TongTien) as TongDoanhThu
    FROM NhanVien nv
    LEFT JOIN LichLamViec llv ON nv.MaNV = llv.MaNV 
        AND MONTH(llv.NgayLam) = p_Thang 
        AND YEAR(llv.NgayLam) = p_Nam
        AND llv.IsDeleted = 0
    LEFT JOIN HoaDon hd ON nv.MaNV = hd.MaNVLap 
        AND MONTH(hd.NgayLap) = p_Thang 
        AND YEAR(hd.NgayLap) = p_Nam
        AND hd.IsDeleted = 0
    WHERE nv.IsDeleted = 0
        AND (p_MaCH IS NULL OR nv.MaCH = p_MaCH)
    GROUP BY nv.MaNV, nv.HoTen, nv.ChucVu
    ORDER BY TongDoanhThu DESC;
END$$
DELIMITER ;

-- Procedure quản lý khuyến mãi
DELIMITER $$
CREATE PROCEDURE Sp_QuanLyKhuyenMai(
    IN p_TrangThai INT,
    IN p_NgayTu DATE,
    IN p_NgayDen DATE
)
BEGIN
    SELECT 
        km.MaKM,
        km.TenChuongTrinh,
        km.LoaiKM,
        km.GiaTriKM,
        km.NgayBatDau,
        km.NgayKetThuc,
        km.SoLuongToiDa,
        km.DaSuDung,
        km.TrangThai,
        CASE 
            WHEN km.NgayKetThuc < NOW() THEN 'Hết hạn'
            WHEN km.NgayBatDau > NOW() THEN 'Chưa bắt đầu'
            WHEN km.TrangThai = 0 THEN 'Tạm dừng'
            ELSE 'Đang áp dụng'
        END as TrangThaiHienTai
    FROM KhuyenMai km
    WHERE km.IsDeleted = 0
        AND (p_TrangThai IS NULL OR km.TrangThai = p_TrangThai)
        AND (p_NgayTu IS NULL OR km.NgayBatDau >= p_NgayTu)
        AND (p_NgayDen IS NULL OR km.NgayKetThuc <= p_NgayDen)
    ORDER BY km.NgayBatDau DESC;
END$$
DELIMITER ;

-- Procedure thống kê doanh thu theo cửa hàng
DELIMITER $$
CREATE PROCEDURE Sp_ThongKeDoanhThuTheoCuaHang(
    IN p_NgayTu DATE,
    IN p_NgayDen DATE
)
BEGIN
    SELECT 
        ch.MaCH,
        ch.TenCH,
        COUNT(DISTINCT hd.MaHD) as SoHoaDon,
        SUM(hd.TongTien) as TongDoanhThu,
        AVG(hd.TongTien) as DoanhThuTrungBinh,
        COUNT(DISTINCT hd.MaKH) as SoKhachHang
    FROM CuaHang ch
    LEFT JOIN NhanVien nv ON ch.MaCH = nv.MaCH AND nv.IsDeleted = 0
    LEFT JOIN HoaDon hd ON nv.MaNV = hd.MaNVLap 
        AND hd.IsDeleted = 0
        AND DATE(hd.NgayLap) BETWEEN p_NgayTu AND p_NgayDen
    WHERE ch.IsDeleted = 0
    GROUP BY ch.MaCH, ch.TenCH
    ORDER BY TongDoanhThu DESC;
END$$
DELIMITER ;

-- ===== BỔ SUNG TRIGGERS =====

-- Trigger cập nhật tồn kho khi xóa chi tiết phiếu nhập
DELIMITER $$
CREATE TRIGGER Tg_CapNhatTonKho_XoaChiTietPhieuNhap
    AFTER DELETE ON ChiTietPhieuNhap
    FOR EACH ROW
BEGIN
    DECLARE v_MaKho INT;

    -- Lấy mã kho từ phiếu nhập
    SELECT MaKho INTO v_MaKho
    FROM PhieuNhapHang
    WHERE MaPN = OLD.MaPN;

    -- Trừ tồn kho
    UPDATE TonKhoChiTiet
    SET SoLuongTon = GREATEST(SoLuongTon - OLD.SoLuongNhap, 0),
        NgayCapNhat = CURRENT_TIMESTAMP
    WHERE MaSP = OLD.MaSP AND MaKho = v_MaKho;
END$$
DELIMITER ;

-- Trigger cập nhật tồn kho khi sửa chi tiết phiếu nhập
DELIMITER $$
CREATE TRIGGER Tg_CapNhatTonKho_SuaChiTietPhieuNhap
    AFTER UPDATE ON ChiTietPhieuNhap
    FOR EACH ROW
BEGIN
    DECLARE v_MaKho INT;

    -- Lấy mã kho từ phiếu nhập
    SELECT MaKho INTO v_MaKho
    FROM PhieuNhapHang
    WHERE MaPN = NEW.MaPN;

    -- Cập nhật tồn kho (trừ số lượng cũ, cộng số lượng mới)
    UPDATE TonKhoChiTiet
    SET SoLuongTon = GREATEST(SoLuongTon - OLD.SoLuongNhap + NEW.SoLuongNhap, 0),
        NgayCapNhat = CURRENT_TIMESTAMP
    WHERE MaSP = NEW.MaSP AND MaKho = v_MaKho;
END$$
DELIMITER ;

-- Trigger cập nhật tồn kho khi xóa chi tiết hóa đơn
DELIMITER $$
CREATE TRIGGER Tg_CapNhatTonKho_XoaChiTietHoaDon
    AFTER DELETE ON ChiTietHoaDon
    FOR EACH ROW
BEGIN
    DECLARE v_MaCH NVARCHAR(10);
    DECLARE v_MaKho INT;

    -- Lấy cửa hàng từ nhân viên lập hóa đơn
    SELECT nv.MaCH INTO v_MaCH
    FROM HoaDon hd
    JOIN NhanVien nv ON hd.MaNVLap = nv.MaNV
    WHERE hd.MaHD = OLD.MaHD;

    -- Lấy kho chính của cửa hàng
    SELECT MaKho INTO v_MaKho
    FROM Kho
    WHERE MaCH = v_MaCH AND TrangThai = 1
    ORDER BY MaKho
    LIMIT 1;

    IF v_MaKho IS NOT NULL THEN
        -- Cộng lại tồn kho
        UPDATE TonKhoChiTiet
        SET SoLuongTon = SoLuongTon + OLD.SoLuong,
            NgayCapNhat = CURRENT_TIMESTAMP
        WHERE MaSP = OLD.MaSP AND MaKho = v_MaKho;
    END IF;
END$$
DELIMITER ;

-- Trigger cập nhật tồn kho khi sửa chi tiết hóa đơn
DELIMITER $$
CREATE TRIGGER Tg_CapNhatTonKho_SuaChiTietHoaDon
    AFTER UPDATE ON ChiTietHoaDon
    FOR EACH ROW
BEGIN
    DECLARE v_MaCH NVARCHAR(10);
    DECLARE v_MaKho INT;

    -- Lấy cửa hàng từ nhân viên lập hóa đơn
    SELECT nv.MaCH INTO v_MaCH
    FROM HoaDon hd
    JOIN NhanVien nv ON hd.MaNVLap = nv.MaNV
    WHERE hd.MaHD = NEW.MaHD;

    -- Lấy kho chính của cửa hàng
    SELECT MaKho INTO v_MaKho
    FROM Kho
    WHERE MaCH = v_MaCH AND TrangThai = 1
    ORDER BY MaKho
    LIMIT 1;

    IF v_MaKho IS NOT NULL THEN
        -- Cập nhật tồn kho (cộng số lượng cũ, trừ số lượng mới)
        UPDATE TonKhoChiTiet
        SET SoLuongTon = SoLuongTon + OLD.SoLuong - NEW.SoLuong,
            NgayCapNhat = CURRENT_TIMESTAMP
        WHERE MaSP = NEW.MaSP AND MaKho = v_MaKho;
    END IF;
END$$
DELIMITER ;

-- Trigger cập nhật tồn kho khi xóa chi tiết phiếu xuất
DELIMITER $$
CREATE TRIGGER Tg_CapNhatTonKho_XoaChiTietPhieuXuat
    AFTER DELETE ON ChiTietPhieuXuat
    FOR EACH ROW
BEGIN
    DECLARE v_MaKho INT;

    -- Lấy mã kho từ phiếu xuất
    SELECT MaKho INTO v_MaKho
    FROM PhieuXuatKho
    WHERE MaPXK = OLD.MaPXK;

    -- Cộng lại tồn kho
    UPDATE TonKhoChiTiet
    SET SoLuongTon = SoLuongTon + OLD.SoLuongXuat,
        NgayCapNhat = CURRENT_TIMESTAMP
    WHERE MaSP = OLD.MaSP AND MaKho = v_MaKho;
END$$
DELIMITER ;

-- Trigger cập nhật tồn kho khi sửa chi tiết phiếu xuất
DELIMITER $$
CREATE TRIGGER Tg_CapNhatTonKho_SuaChiTietPhieuXuat
    AFTER UPDATE ON ChiTietPhieuXuat
    FOR EACH ROW
BEGIN
    DECLARE v_MaKho INT;

    -- Lấy mã kho từ phiếu xuất
    SELECT MaKho INTO v_MaKho
    FROM PhieuXuatKho
    WHERE MaPXK = NEW.MaPXK;

    -- Cập nhật tồn kho (cộng số lượng cũ, trừ số lượng mới)
    UPDATE TonKhoChiTiet
    SET SoLuongTon = SoLuongTon + OLD.SoLuongXuat - NEW.SoLuongXuat,
        NgayCapNhat = CURRENT_TIMESTAMP
    WHERE MaSP = NEW.MaSP AND MaKho = v_MaKho;
END$$
DELIMITER ;

-- ===== BỔ SUNG FUNCTIONS =====

-- Function tính tổng doanh thu theo khoảng thời gian
DELIMITER $$
CREATE FUNCTION Fn_TinhTongDoanhThu(p_NgayTu DATE, p_NgayDen DATE)
    RETURNS DECIMAL(18,2)
    DETERMINISTIC
    READS SQL DATA
BEGIN
    DECLARE v_TongDoanhThu DECIMAL(18,2);

    SELECT COALESCE(SUM(TongTien), 0)
    INTO v_TongDoanhThu
    FROM HoaDon
    WHERE TrangThai = 1 
        AND IsDeleted = 0
        AND DATE(NgayLap) BETWEEN p_NgayTu AND p_NgayDen;

    RETURN IFNULL(v_TongDoanhThu, 0);
END$$
DELIMITER ;

-- Function kiểm tra sản phẩm có đủ hàng trong kho
DELIMITER $$
CREATE FUNCTION Fn_KiemTraDuHang(p_MaSP NVARCHAR(10), p_SoLuong INT, p_MaKho INT)
    RETURNS BOOLEAN
    DETERMINISTIC
    READS SQL DATA
BEGIN
    DECLARE v_TonKho INT DEFAULT 0;

    SELECT COALESCE(SoLuongTon, 0)
    INTO v_TonKho
    FROM TonKhoChiTiet
    WHERE MaSP = p_MaSP AND MaKho = p_MaKho AND IsDeleted = 0;

    RETURN v_TonKho >= p_SoLuong;
END$$
DELIMITER ;

-- Function tính điểm tích lũy cần thiết cho level tiếp theo
DELIMITER $$
CREATE FUNCTION Fn_TinhDiemCanLevelTiepTheo(p_DiemHienTai INT)
    RETURNS INT
    DETERMINISTIC
BEGIN
    DECLARE v_DiemCan INT;

    CASE 
        WHEN p_DiemHienTai < 500 THEN SET v_DiemCan = 500;
        WHEN p_DiemHienTai < 2000 THEN SET v_DiemCan = 2000;
        WHEN p_DiemHienTai < 5000 THEN SET v_DiemCan = 5000;
        WHEN p_DiemHienTai < 10000 THEN SET v_DiemCan = 10000;
        ELSE SET v_DiemCan = p_DiemHienTai; -- Đã đạt level cao nhất
    END CASE;

    RETURN v_DiemCan;
END$$
DELIMITER ;

-- ===== BỔ SUNG VIEWS =====

-- View thống kê khách hàng theo loại
CREATE VIEW V_ThongKeKhachHang AS
SELECT 
    LoaiKhachHang,
    COUNT(*) as SoLuongKhachHang,
    AVG(DiemTichLuy) as DiemTrungBinh,
    SUM(DiemTichLuy) as TongDiemTichLuy
FROM KhachHang
WHERE IsDeleted = 0
GROUP BY LoaiKhachHang
ORDER BY SoLuongKhachHang DESC;

-- View thống kê nhân viên theo cửa hàng
CREATE VIEW V_ThongKeNhanVien AS
SELECT 
    ch.MaCH,
    ch.TenCH,
    COUNT(nv.MaNV) as SoNhanVien,
    COUNT(CASE WHEN nv.TrangThai = 1 THEN 1 END) as SoNhanVienDangLam,
    COUNT(CASE WHEN nv.TrangThai = 0 THEN 1 END) as SoNhanVienNghiViec
FROM CuaHang ch
LEFT JOIN NhanVien nv ON ch.MaCH = nv.MaCH AND nv.IsDeleted = 0
WHERE ch.IsDeleted = 0
GROUP BY ch.MaCH, ch.TenCH;

-- View thống kê sản phẩm theo loại
CREATE VIEW V_ThongKeSanPham AS
SELECT 
    lsp.MaLoaiSP,
    lsp.TenLoai,
    COUNT(sp.MaSP) as SoSanPham,
    COUNT(CASE WHEN sp.TrangThai = 1 THEN 1 END) as SoSanPhamDangBan,
    AVG(sp.GiaBan) as GiaTrungBinh,
    SUM(tk.SoLuongTon) as TongTonKho
FROM LoaiSanPham lsp
LEFT JOIN SanPham sp ON lsp.MaLoaiSP = sp.MaLoaiSP AND sp.IsDeleted = 0
LEFT JOIN TonKhoChiTiet tk ON sp.MaSP = tk.MaSP AND tk.IsDeleted = 0
WHERE lsp.IsDeleted = 0
GROUP BY lsp.MaLoaiSP, lsp.TenLoai;

-- ===== BỔ SUNG INDEXES =====

-- Index cho tìm kiếm sản phẩm theo tên
CREATE INDEX idx_sanpham_ten ON SanPham(TenSP);

-- Index cho tìm kiếm khách hàng theo tên
CREATE INDEX idx_khachhang_ten ON KhachHang(HoTen);

-- Index cho tìm kiếm nhân viên theo tên
CREATE INDEX idx_nhanvien_ten ON NhanVien(HoTen);

-- Index cho tìm kiếm hóa đơn theo khách hàng và ngày
CREATE INDEX idx_hoadon_kh_ngay ON HoaDon(MaKH, NgayLap);

-- Index cho tìm kiếm phiếu nhập theo nhà cung cấp và ngày
CREATE INDEX idx_phieunhap_ncc_ngay ON PhieuNhapHang(MaNCC, NgayNhap);

-- ===== BỔ SUNG CONSTRAINTS =====

-- Constraint kiểm tra email hợp lệ
ALTER TABLE NguoiDung ADD CONSTRAINT chk_email_format 
    CHECK (Email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Constraint kiểm tra số điện thoại hợp lệ
ALTER TABLE KhachHang ADD CONSTRAINT chk_sdt_format 
    CHECK (SDT REGEXP '^[0-9]{10,11}$');

-- Constraint kiểm tra giá sản phẩm không âm
ALTER TABLE GiaSanPham ADD CONSTRAINT chk_gia_duong 
    CHECK (Gia > 0);

-- Constraint kiểm tra số lượng không âm
ALTER TABLE ChiTietHoaDon ADD CONSTRAINT chk_soluong_duong 
    CHECK (SoLuong > 0);

-- ===== BỔ SUNG COMMENTS =====

-- Thêm comment cho các bảng quan trọng
ALTER TABLE HoaDon COMMENT = 'Bảng quản lý hóa đơn bán hàng - bảng chính của hệ thống';
ALTER TABLE SanPham COMMENT = 'Bảng quản lý thông tin sản phẩm - bảng chính của hệ thống';
ALTER TABLE NhanVien COMMENT = 'Bảng quản lý thông tin nhân viên - bảng chính của hệ thống';
ALTER TABLE KhachHang COMMENT = 'Bảng quản lý thông tin khách hàng - bảng chính của hệ thống';

-- Thêm comment cho các cột quan trọng
ALTER TABLE HoaDon MODIFY COLUMN TongTien DECIMAL(15,2) 
    GENERATED ALWAYS AS (TongTienHang - TienGiamGia) STORED 
    COMMENT 'Tổng tiền cuối cùng sau khi trừ giảm giá';

ALTER TABLE SanPham MODIFY COLUMN GiaBan DECIMAL(15,2) 
    COMMENT 'Giá bán hiện tại của sản phẩm (VND)';

ALTER TABLE KhachHang MODIFY COLUMN DiemTichLuy INT 
    COMMENT 'Điểm tích lũy từ các giao dịch mua hàng (1 điểm = 1000 VND)';

-- ===== KẾT THÚC FILE SỬA CHỮA =====
SELECT 'File sửa chữa đã được thực thi thành công!' as KetQua; 

-- ===== BỔ SUNG FUNCTIONS THIẾU =====

-- Function tính tổng tiền đơn hàng (từ QuanLySieuThi.sql)
DELIMITER $$
CREATE FUNCTION Fn_TinhTongTienDonHang(MaDonThamSo VARCHAR(10))
RETURNS DECIMAL(18,2)
DETERMINISTIC
BEGIN
    DECLARE tong DECIMAL(18,2);
    SELECT SUM(SoLuong * DonGia) INTO tong
    FROM ChiTietHoaDon
    WHERE MaHD = MaDonThamSo;

    RETURN IFNULL(tong, 0);
END$$
DELIMITER ;

-- Function lấy tồn kho (phiên bản cũ - 1 tham số)
DELIMITER $$
CREATE FUNCTION Fn_LayTonKhoCu(p_MaSP VARCHAR(10))
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE v_SoLuongTon INT;
    SELECT SoLuongTon 
    INTO v_SoLuongTon 
    FROM SanPham 
    WHERE MaSP = p_MaSP; 
    RETURN IFNULL(v_SoLuongTon, 0);
END$$
DELIMITER ;


-- Trigger cập nhật khách hàng
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

-- Trigger cập nhật khuyến mãi
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

-- Trigger cập nhật loại sản phẩm
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

-- Trigger cập nhật người dùng
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

-- Trigger cập nhật nhà cung cấp
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

-- Trigger cập nhật nhân viên
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

    -- Kiểm tra ngày vào làm hợp lệ
    IF NEW.NgayVaoLam IS NOT NULL AND NEW.NgaySinh IS NOT NULL AND NEW.NgayVaoLam <= NEW.NgaySinh THEN
        SET msg = 'Ngày vào làm phải sau ngày sinh khi cập nhật';
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = msg;
    END IF;

    -- Kiểm tra trạng thái hợp lệ
    IF NEW.TrangThai NOT IN (0, 1) THEN
        SET msg = 'Trạng thái nhân viên không hợp lệ (chỉ 0 hoặc 1)';
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = msg;
    END IF;
END$$
DELIMITER ;

-- Trigger cập nhật phương thức thanh toán
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

-- Trigger thêm giá sản phẩm
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

-- Trigger cập nhật giá sản phẩm
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

-- Trigger thêm tồn kho chi tiết
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

-- Trigger cập nhật tồn kho chi tiết
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

-- Trigger thêm lịch làm việc
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

-- Trigger cập nhật lịch làm việc
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

-- Trigger thêm giỏ hàng
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

-- Trigger cập nhật giỏ hàng
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




