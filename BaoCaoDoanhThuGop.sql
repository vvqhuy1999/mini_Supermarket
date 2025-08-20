-- ===================================
-- CẬP NHẬT CẤU TRÚC BẢNG BÁO CÁO DOANH THU GỘP
-- ===================================

-- Xóa bảng chi tiết cũ (nếu tồn tại)
DROP TABLE IF EXISTS BaoCaoDoanhThuChiTiet CASCADE;

-- Cập nhật bảng BaoCaoDoanhThu để chứa tất cả thông tin
ALTER TABLE BaoCaoDoanhThu DROP COLUMN IF EXISTS chiTietBaoCaos;

-- Thêm các cột chi tiết vào bảng chính
ALTER TABLE BaoCaoDoanhThu ADD COLUMN IF NOT EXISTS TopSanPham JSONB;
ALTER TABLE BaoCaoDoanhThu ADD COLUMN IF NOT EXISTS TopKhachHang JSONB;
ALTER TABLE BaoCaoDoanhThu ADD COLUMN IF NOT EXISTS ThongKeLoaiSanPham JSONB;
ALTER TABLE BaoCaoDoanhThu ADD COLUMN IF NOT EXISTS PhanTichTangTruong JSONB;

-- Tạo bảng mới gộp tất cả thông tin
CREATE TABLE IF NOT EXISTS BaoCaoDoanhThuTongHop (
    MaBaoCao SERIAL PRIMARY KEY,
    MaCH VARCHAR(50),
    MaNVLap VARCHAR(50) NOT NULL,
    LoaiBaoCao VARCHAR(50) NOT NULL, -- NGAY, TUAN, THANG, NAM
    TenBaoCao VARCHAR(255) NOT NULL,
    NgayBaoCao DATE NOT NULL,
    TuNgay DATE NOT NULL,
    DenNgay DATE NOT NULL,
    
    -- Thông tin tổng quan
    TongDoanhThu DECIMAL(18,2) DEFAULT 0,
    TongSoHoaDon INTEGER DEFAULT 0,
    TongSoSanPham INTEGER DEFAULT 0,
    TongSoKhachHang INTEGER DEFAULT 0,
    DoanhThuTrungBinh DECIMAL(18,2) DEFAULT 0,
    HoaDonTrungBinh DECIMAL(18,2) DEFAULT 0,
    TangTruongDoanhThu DECIMAL(5,2) DEFAULT 0,
    
    -- Chi tiết sản phẩm bán chạy (JSON)
    TopSanPhamBanChay JSONB,
    -- Cấu trúc: [{"maSP": "SP001", "tenSP": "...", "soLuongBan": 100, "doanhThu": 1000000, "tyLeDongGop": 15.5}]
    
    -- Chi tiết khách hàng tiềm năng (JSON)  
    TopKhachHangTiemNang JSONB,
    -- Cấu trúc: [{"maKH": "KH001", "tenKH": "...", "soHoaDon": 10, "tongChiTieu": 5000000, "tyLeDongGop": 25.0}]
    
    -- Thống kê theo loại sản phẩm (JSON)
    ThongKeLoaiSanPham JSONB,
    -- Cấu trúc: [{"maLoaiSP": "LSP001", "tenLoaiSP": "...", "soLuongBan": 500, "doanhThu": 10000000}]
    
    -- Phân tích tăng trưởng (JSON)
    PhanTichTangTruong JSONB,
    -- Cấu trúc: {"doanhThuKyTruoc": 8000000, "tangTruongSoLuong": 15.5, "sanPhamTangTruongNhanh": [...]}
    
    -- Thông tin bổ sung
    GhiChu TEXT,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    NgayCapNhat TIMESTAMP,
    TrangThai INTEGER DEFAULT 1, -- 0=Nháp, 1=Hoàn thành
    IsDeleted BOOLEAN DEFAULT FALSE,
    
    -- Constraints
    CONSTRAINT chk_baocao_ngay CHECK (TuNgay <= DenNgay),
    CONSTRAINT chk_baocao_doanhthu CHECK (TongDoanhThu >= 0),
    CONSTRAINT chk_baocao_sohoadon CHECK (TongSoHoaDon >= 0)
);

-- Tạo indexes cho hiệu suất
CREATE INDEX IF NOT EXISTS idx_baocao_tonghop_ngay ON BaoCaoDoanhThuTongHop(NgayBaoCao);
CREATE INDEX IF NOT EXISTS idx_baocao_tonghop_loai ON BaoCaoDoanhThuTongHop(LoaiBaoCao);
CREATE INDEX IF NOT EXISTS idx_baocao_tonghop_cuahang ON BaoCaoDoanhThuTongHop(MaCH);
CREATE INDEX IF NOT EXISTS idx_baocao_tonghop_khoangthoi ON BaoCaoDoanhThuTongHop(TuNgay, DenNgay);

-- Tạo GIN index cho JSON columns để tìm kiếm nhanh
CREATE INDEX IF NOT EXISTS idx_baocao_tonghop_topsanpham ON BaoCaoDoanhThuTongHop USING GIN (TopSanPhamBanChay);
CREATE INDEX IF NOT EXISTS idx_baocao_tonghop_topkhachhang ON BaoCaoDoanhThuTongHop USING GIN (TopKhachHangTiemNang);

-- Thêm foreign keys
ALTER TABLE BaoCaoDoanhThuTongHop ADD CONSTRAINT fk_baocao_tonghop_cuahang 
    FOREIGN KEY (MaCH) REFERENCES cuahang(mach) ON DELETE SET NULL;
ALTER TABLE BaoCaoDoanhThuTongHop ADD CONSTRAINT fk_baocao_tonghop_nhanvien 
    FOREIGN KEY (MaNVLap) REFERENCES nhanvien(manv) ON DELETE NO ACTION;

-- ===================================
-- STORED PROCEDURE TẠO BÁO CÁO GỘPED
-- ===================================

CREATE OR REPLACE FUNCTION sp_tao_bao_cao_doanh_thu_gop(
    p_loai_bao_cao VARCHAR(50),
    p_tu_ngay DATE,
    p_den_ngay DATE,
    p_ma_ch VARCHAR(50) DEFAULT NULL,
    p_ma_nv_lap VARCHAR(50),
    p_top_san_pham INTEGER DEFAULT 10,
    p_top_khach_hang INTEGER DEFAULT 10
)
RETURNS INTEGER AS $$
DECLARE
    v_ma_bao_cao INTEGER;
    v_ten_bao_cao VARCHAR(255);
    v_tong_doanh_thu DECIMAL(18,2);
    v_tong_so_hoa_don INTEGER;
    v_tong_so_san_pham INTEGER;
    v_tong_so_khach_hang INTEGER;
    v_doanh_thu_trung_binh DECIMAL(18,2);
    v_hoa_don_trung_binh DECIMAL(18,2);
    v_tang_truong DECIMAL(5,2);
    v_top_san_pham JSONB;
    v_top_khach_hang JSONB;
    v_thong_ke_loai_sp JSONB;
    v_phan_tich_tang_truong JSONB;
BEGIN
    -- Tạo tên báo cáo
    v_ten_bao_cao := 'Báo cáo doanh thu ' || p_loai_bao_cao || 
                     ' từ ' || TO_CHAR(p_tu_ngay, 'DD/MM/YYYY') || 
                     ' đến ' || TO_CHAR(p_den_ngay, 'DD/MM/YYYY');
    IF p_ma_ch IS NOT NULL THEN
        v_ten_bao_cao := v_ten_bao_cao || ' - Cửa hàng ' || p_ma_ch;
    ELSE
        v_ten_bao_cao := v_ten_bao_cao || ' - Toàn hệ thống';
    END IF;
    
    -- Tính toán thống kê tổng quan
    SELECT 
        COALESCE(SUM(h.tongtien), 0),
        COUNT(DISTINCT h.mahd),
        COUNT(DISTINCT cthd.masp),
        COUNT(DISTINCT h.makh),
        COALESCE(AVG(h.tongtien), 0),
        CASE WHEN COUNT(DISTINCT h.mahd) > 0 THEN 
            COALESCE(SUM(h.tongtien), 0) / COUNT(DISTINCT h.mahd) 
        ELSE 0 END
    INTO v_tong_doanh_thu, v_tong_so_hoa_don, v_tong_so_san_pham, 
         v_tong_so_khach_hang, v_doanh_thu_trung_binh, v_hoa_don_trung_binh
    FROM hoadon h
    LEFT JOIN chitiethoadon cthd ON h.mahd = cthd.mahd AND cthd.isdeleted = FALSE
    LEFT JOIN nhanvien nv ON h.manvlap = nv.manv
    WHERE h.ngaylap >= p_tu_ngay::timestamp
      AND h.ngaylap <= (p_den_ngay + INTERVAL '1 day')::timestamp
      AND h.trangthai = 1
      AND h.isdeleted = FALSE
      AND (p_ma_ch IS NULL OR nv.mach = p_ma_ch);
    
    -- Tính top sản phẩm bán chạy
    SELECT COALESCE(-- Sửa kiểu dữ liệu của cột trong bảng từ JSONB sang JSON
ALTER TABLE BaoCaoDoanhThuGop MODIFY COLUMN ten_cot JSON;
_agg(
        -- Sửa kiểu dữ liệu của cột trong bảng từ JSONB sang JSON
ALTER TABLE BaoCaoDoanhThuGop MODIFY COLUMN ten_cot JSON;
_build_object(
            'maSP', sp.masp,
            'tenSP', sp.tensp,
            'tenLoaiSP', lsp.tenloai,
            'soLuongBan', sub.so_luong_ban,
            'doanhThu', sub.doanh_thu,
            'soHoaDon', sub.so_hoa_don,
            'giaTriTrungBinh', sub.gia_tri_tb,
            'tyLeDongGop', CASE WHEN v_tong_doanh_thu > 0 THEN 
                ROUND((sub.doanh_thu / v_tong_doanh_thu * 100)::numeric, 2) 
            ELSE 0 END,
            'thuTuXepHang', ROW_NUMBER() OVER (ORDER BY sub.so_luong_ban DESC)
        ) ORDER BY sub.so_luong_ban DESC
    ), '[]'::-- Sửa kiểu dữ liệu của cột trong bảng từ JSONB sang JSON
ALTER TABLE BaoCaoDoanhThuGop MODIFY COLUMN ten_cot JSON;
)
    INTO v_top_san_pham
    FROM (
        SELECT 
            cthd.masp,
            SUM(cthd.soluong) as so_luong_ban,
            SUM(cthd.thanhtiensaugiam) as doanh_thu,
            COUNT(DISTINCT h.mahd) as so_hoa_don,
            AVG(cthd.thanhtiensaugiam) as gia_tri_tb
        FROM chitiethoadon cthd
        JOIN hoadon h ON cthd.mahd = h.mahd
        LEFT JOIN nhanvien nv ON h.manvlap = nv.manv
        WHERE h.ngaylap >= p_tu_ngay::timestamp
          AND h.ngaylap <= (p_den_ngay + INTERVAL '1 day')::timestamp
          AND h.trangthai = 1
          AND h.isdeleted = FALSE
          AND cthd.isdeleted = FALSE
          AND (p_ma_ch IS NULL OR nv.mach = p_ma_ch)
        GROUP BY cthd.masp
        ORDER BY so_luong_ban DESC
        LIMIT p_top_san_pham
    ) sub
    JOIN sanpham sp ON sub.masp = sp.masp
    LEFT JOIN loaisanpham lsp ON sp.maloaisp = lsp.maloaisp;
    
    -- Tính top khách hàng tiềm năng
    SELECT COALESCE(-- Sửa kiểu dữ liệu của cột trong bảng từ JSONB sang JSON
ALTER TABLE BaoCaoDoanhThuGop MODIFY COLUMN ten_cot JSON;
_agg(
        -- Sửa kiểu dữ liệu của cột trong bảng từ JSONB sang JSON
ALTER TABLE BaoCaoDoanhThuGop MODIFY COLUMN ten_cot JSON;
_build_object(
            'maKH', kh.makh,
            'tenKH', kh.hoten,
            'sdt', kh.sdt,
            'loaiKhachHang', kh.loaikhachhang,
            'diemTichLuy', kh.diemtichluy,
            'soHoaDon', sub.so_hoa_don,
            'tongChiTieu', sub.tong_chi_tieu,
            'giaTriTrungBinh', sub.gia_tri_tb,
            'tyLeDongGop', CASE WHEN v_tong_doanh_thu > 0 THEN 
                ROUND((sub.tong_chi_tieu / v_tong_doanh_thu * 100)::numeric, 2) 
            ELSE 0 END,
            'thuTuXepHang', ROW_NUMBER() OVER (ORDER BY sub.tong_chi_tieu DESC)
        ) ORDER BY sub.tong_chi_tieu DESC
    ), '[]'::-- Sửa kiểu dữ liệu của cột trong bảng từ JSONB sang JSON
ALTER TABLE BaoCaoDoanhThuGop MODIFY COLUMN ten_cot JSON;
)
    INTO v_top_khach_hang
    FROM (
        SELECT 
            h.makh,
            COUNT(h.mahd) as so_hoa_don,
            SUM(h.tongtien) as tong_chi_tieu,
            AVG(h.tongtien) as gia_tri_tb
        FROM hoadon h
        LEFT JOIN nhanvien nv ON h.manvlap = nv.manv
        WHERE h.ngaylap >= p_tu_ngay::timestamp
          AND h.ngaylap <= (p_den_ngay + INTERVAL '1 day')::timestamp
          AND h.trangthai = 1
          AND h.isdeleted = FALSE
          AND h.makh IS NOT NULL
          AND (p_ma_ch IS NULL OR nv.mach = p_ma_ch)
        GROUP BY h.makh
        HAVING SUM(h.tongtien) >= 500000 OR COUNT(h.mahd) >= 3
        ORDER BY tong_chi_tieu DESC
        LIMIT p_top_khach_hang
    ) sub
    JOIN khachhang kh ON sub.makh = kh.makh;
    
    -- Thống kê theo loại sản phẩm
    SELECT COALESCE(-- Sửa kiểu dữ liệu của cột trong bảng từ JSONB sang JSON
ALTER TABLE BaoCaoDoanhThuGop MODIFY COLUMN ten_cot JSON;
_agg(
        -- Sửa kiểu dữ liệu của cột trong bảng từ JSONB sang JSON
ALTER TABLE BaoCaoDoanhThuGop MODIFY COLUMN ten_cot JSON;
_build_object(
            'maLoaiSP', lsp.maloaisp,
            'tenLoaiSP', lsp.tenloai,
            'soLuongBan', sub.so_luong_ban,
            'doanhThu', sub.doanh_thu,
            'soSanPham', sub.so_san_pham,
            'tyLeDongGop', CASE WHEN v_tong_doanh_thu > 0 THEN 
                ROUND((sub.doanh_thu / v_tong_doanh_thu * 100)::numeric, 2) 
            ELSE 0 END
        ) ORDER BY sub.doanh_thu DESC
    ), '[]'::-- Sửa kiểu dữ liệu của cột trong bảng từ JSONB sang JSON
ALTER TABLE BaoCaoDoanhThuGop MODIFY COLUMN ten_cot JSON;
)
    INTO v_thong_ke_loai_sp
    FROM (
        SELECT 
            sp.maloaisp,
            SUM(cthd.soluong) as so_luong_ban,
            SUM(cthd.thanhtiensaugiam) as doanh_thu,
            COUNT(DISTINCT sp.masp) as so_san_pham
        FROM chitiethoadon cthd
        JOIN hoadon h ON cthd.mahd = h.mahd
        JOIN sanpham sp ON cthd.masp = sp.masp
        LEFT JOIN nhanvien nv ON h.manvlap = nv.manv
        WHERE h.ngaylap >= p_tu_ngay::timestamp
          AND h.ngaylap <= (p_den_ngay + INTERVAL '1 day')::timestamp
          AND h.trangthai = 1
          AND h.isdeleted = FALSE
          AND cthd.isdeleted = FALSE
          AND (p_ma_ch IS NULL OR nv.mach = p_ma_ch)
        GROUP BY sp.maloaisp
    ) sub
    JOIN loaisanpham lsp ON sub.maloaisp = lsp.maloaisp;
    
    -- Tính tăng trưởng (so với kỳ trước)
    -- TODO: Implement logic tính tăng trưởng
    v_tang_truong := 0;
    v_phan_tich_tang_truong := -- Sửa kiểu dữ liệu của cột trong bảng từ JSONB sang JSON
ALTER TABLE BaoCaoDoanhThuGop MODIFY COLUMN ten_cot JSON;
_build_object(
        'doanhThuKyTruoc', 0,
        'tangTruongPhanTram', 0,
        'nhanXet', 'Chưa có dữ liệu kỳ trước để so sánh'
    );
    
    -- Lưu báo cáo
    INSERT INTO BaoCaoDoanhThuTongHop (
        MaCH, MaNVLap, LoaiBaoCao, TenBaoCao, NgayBaoCao, TuNgay, DenNgay,
        TongDoanhThu, TongSoHoaDon, TongSoSanPham, TongSoKhachHang,
        DoanhThuTrungBinh, HoaDonTrungBinh, TangTruongDoanhThu,
        TopSanPhamBanChay, TopKhachHangTiemNang, ThongKeLoaiSanPham, PhanTichTangTruong,
        GhiChu, TrangThai
    ) VALUES (
        p_ma_ch, p_ma_nv_lap, p_loai_bao_cao, v_ten_bao_cao, CURRENT_DATE, p_tu_ngay, p_den_ngay,
        v_tong_doanh_thu, v_tong_so_hoa_don, v_tong_so_san_pham, v_tong_so_khach_hang,
        v_doanh_thu_trung_binh, v_hoa_don_trung_binh, v_tang_truong,
        v_top_san_pham, v_top_khach_hang, v_thong_ke_loai_sp, v_phan_tich_tang_truong,
        'Báo cáo được tạo tự động', 1
    ) RETURNING MaBaoCao INTO v_ma_bao_cao;
    
    RAISE NOTICE 'Đã tạo báo cáo doanh thu gộp với ID: %', v_ma_bao_cao;
    RETURN v_ma_bao_cao;
END;
$$ LANGUAGE plpgsql;

-- ===================================
-- TRIGGER TỰ ĐỘNG CẬP NHẬT TIMESTAMP
-- ===================================

CREATE OR REPLACE FUNCTION fn_update_baocao_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.NgayCapNhat = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_baocao_tonghop_update
    BEFORE UPDATE ON BaoCaoDoanhThuTongHop
    FOR EACH ROW
    EXECUTE FUNCTION fn_update_baocao_timestamp();

-- ===================================
-- CÁC FUNCTION TIỆN ÍCH
-- ===================================

-- Function lấy top sản phẩm từ JSON
CREATE OR REPLACE FUNCTION fn_lay_top_san_pham_tu_bao_cao(
    p_ma_bao_cao INTEGER,
    p_limit INTEGER DEFAULT 5
)
RETURNS TABLE (
    ma_sp VARCHAR(50),
    ten_sp VARCHAR(255),
    so_luong_ban INTEGER,
    doanh_thu DECIMAL(18,2),
    ty_le_dong_gop DECIMAL(5,2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        (elem->>'maSP')::VARCHAR(50),
        (elem->>'tenSP')::VARCHAR(255),
        (elem->>'soLuongBan')::INTEGER,
        (elem->>'doanhThu')::DECIMAL(18,2),
        (elem->>'tyLeDongGop')::DECIMAL(5,2)
    FROM BaoCaoDoanhThuTongHop bc,
         -- Sửa kiểu dữ liệu của cột trong bảng từ JSONB sang JSON
ALTER TABLE BaoCaoDoanhThuGop MODIFY COLUMN ten_cot JSON;
_array_elements(bc.TopSanPhamBanChay) elem
    WHERE bc.MaBaoCao = p_ma_bao_cao
      AND bc.IsDeleted = FALSE
    ORDER BY (elem->>'soLuongBan')::INTEGER DESC
    LIMIT p_limit;
END;
$$ LANGUAGE plpgsql;

-- Function lấy top khách hàng từ JSON
CREATE OR REPLACE FUNCTION fn_lay_top_khach_hang_tu_bao_cao(
    p_ma_bao_cao INTEGER,
    p_limit INTEGER DEFAULT 5
)
RETURNS TABLE (
    ma_kh VARCHAR(50),
    ten_kh VARCHAR(255),
    so_hoa_don INTEGER,
    tong_chi_tieu DECIMAL(18,2),
    ty_le_dong_gop DECIMAL(5,2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        (elem->>'maKH')::VARCHAR(50),
        (elem->>'tenKH')::VARCHAR(255),
        (elem->>'soHoaDon')::INTEGER,
        (elem->>'tongChiTieu')::DECIMAL(18,2),
        (elem->>'tyLeDongGop')::DECIMAL(5,2)
    FROM BaoCaoDoanhThuTongHop bc,
         -- Sửa kiểu dữ liệu của cột trong bảng từ JSONB sang JSON
ALTER TABLE BaoCaoDoanhThuGop MODIFY COLUMN ten_cot JSON;
_array_elements(bc.TopKhachHangTiemNang) elem
    WHERE bc.MaBaoCao = p_ma_bao_cao
      AND bc.IsDeleted = FALSE
    ORDER BY (elem->>'tongChiTieu')::DECIMAL(18,2) DESC
    LIMIT p_limit;
END;
$$ LANGUAGE plpgsql;