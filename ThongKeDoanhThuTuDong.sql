-- ===================================
-- STORED PROCEDURES VÀ TRIGGERS TỰ ĐỘNG TÍNH TOÁN THỐNG KÊ
-- ===================================

-- 1. Stored Procedure tính toán doanh thu theo tháng
CREATE OR REPLACE FUNCTION sp_tinh_doanh_thu_thang(
    p_thang INT,
    p_nam INT,
    p_mach VARCHAR(50) DEFAULT NULL
)
RETURNS VOID AS $$
DECLARE
    v_tu_ngay DATE;
    v_den_ngay DATE;
    v_tong_doanh_thu DECIMAL(15,2);
    v_so_hoa_don INT;
    v_so_khach_hang INT;
    v_ma_baocao INT;
    v_manv VARCHAR(50);
    v_ten_baocao VARCHAR(255);
BEGIN
    -- Tính ngày đầu và cuối tháng
    v_tu_ngay := DATE(p_nam || '-' || p_thang || '-01');
    v_den_ngay := (v_tu_ngay + INTERVAL '1 month' - INTERVAL '1 day')::DATE;
    
    -- Lấy nhân viên hệ thống (có thể là admin hoặc nhân viên đầu tiên)
    SELECT manv INTO v_manv FROM nhanvien WHERE vaitro = 0 OR vaitro = 1 LIMIT 1;
    IF v_manv IS NULL THEN
        SELECT manv INTO v_manv FROM nhanvien LIMIT 1;
    END IF;
    
    -- Tạo tên báo cáo
    v_ten_baocao := 'Báo cáo doanh thu tháng ' || p_thang || '/' || p_nam;
    IF p_mach IS NOT NULL THEN
        v_ten_baocao := v_ten_baocao || ' - Cửa hàng ' || p_mach;
    END IF;
    
    -- Tính toán doanh thu tổng
    SELECT 
        COALESCE(SUM(h.tongtien), 0),
        COUNT(DISTINCT h.mahd),
        COUNT(DISTINCT h.makh)
    INTO v_tong_doanh_thu, v_so_hoa_don, v_so_khach_hang
    FROM hoadon h
    WHERE h.ngaylap >= v_tu_ngay::timestamp
      AND h.ngaylap <= (v_den_ngay + INTERVAL '1 day')::timestamp
      AND h.trangthai = 1 -- Chỉ tính hóa đơn đã thanh toán
      AND h.isdeleted = FALSE
      AND (p_mach IS NULL OR EXISTS (
          SELECT 1 FROM nhanvien nv 
          WHERE nv.manv = h.manvlap 
            AND nv.mach = p_mach
      ));
    
    -- Kiểm tra xem báo cáo đã tồn tại chưa
    SELECT mabaocao INTO v_ma_baocao
    FROM thongkebaocao 
    WHERE loaibaocao = 'DOANH_THU_THANG'
      AND EXTRACT(MONTH FROM thoigiantu) = p_thang
      AND EXTRACT(YEAR FROM thoigiantu) = p_nam
      AND (p_mach IS NULL OR (cuahang.mach = p_mach))
      AND isdeleted = FALSE
    LIMIT 1;
    
    -- Nếu chưa có thì tạo mới, nếu có rồi thì cập nhật
    IF v_ma_baocao IS NULL THEN
        INSERT INTO thongkebaocao (
            mach, manv, loaibaocao, tenbaocao, thoigiantu, thoigianden,
            sotien, soluong, ngaybaocao, noidung, trangthai, isdeleted
        ) VALUES (
            p_mach, v_manv, 'DOANH_THU_THANG', v_ten_baocao,
            v_tu_ngay::timestamp, (v_den_ngay + INTERVAL '1 day')::timestamp,
            v_tong_doanh_thu, v_so_hoa_don, CURRENT_TIMESTAMP,
            'Tổng doanh thu: ' || v_tong_doanh_thu || 
            ', Số hóa đơn: ' || v_so_hoa_don || 
            ', Số khách hàng: ' || v_so_khach_hang,
            1, FALSE
        );
    ELSE
        UPDATE thongkebaocao 
        SET sotien = v_tong_doanh_thu,
            soluong = v_so_hoa_don,
            ngaybaocao = CURRENT_TIMESTAMP,
            noidung = 'Tổng doanh thu: ' || v_tong_doanh_thu || 
                     ', Số hóa đơn: ' || v_so_hoa_don || 
                     ', Số khách hàng: ' || v_so_khach_hang
        WHERE mabaocao = v_ma_baocao;
    END IF;
    
    RAISE NOTICE 'Đã cập nhật thống kê doanh thu tháng %/% - Doanh thu: %', p_thang, p_nam, v_tong_doanh_thu;
END;
$$ LANGUAGE plpgsql;

-- 2. Stored Procedure tính toán khách hàng tiềm năng
CREATE OR REPLACE FUNCTION sp_tinh_khach_hang_tiem_nang(
    p_thang INT,
    p_nam INT,
    p_mach VARCHAR(50) DEFAULT NULL
)
RETURNS VOID AS $$
DECLARE
    v_tu_ngay DATE;
    v_den_ngay DATE;
    v_so_kh_tiem_nang INT;
    v_tong_chi_tieu DECIMAL(15,2);
    v_ma_baocao INT;
    v_manv VARCHAR(50);
    v_ten_baocao VARCHAR(255);
    rec RECORD;
BEGIN
    -- Tính ngày đầu và cuối tháng
    v_tu_ngay := DATE(p_nam || '-' || p_thang || '-01');
    v_den_ngay := (v_tu_ngay + INTERVAL '1 month' - INTERVAL '1 day')::DATE;
    
    -- Lấy nhân viên hệ thống
    SELECT manv INTO v_manv FROM nhanvien WHERE vaitro = 0 OR vaitro = 1 LIMIT 1;
    IF v_manv IS NULL THEN
        SELECT manv INTO v_manv FROM nhanvien LIMIT 1;
    END IF;
    
    -- Tạo tên báo cáo
    v_ten_baocao := 'Khách hàng tiềm năng tháng ' || p_thang || '/' || p_nam;
    IF p_mach IS NOT NULL THEN
        v_ten_baocao := v_ten_baocao || ' - Cửa hàng ' || p_mach;
    END IF;
    
    -- Tính toán khách hàng tiềm năng (chi tiêu >= 1,000,000 VND hoặc >= 5 hóa đơn)
    WITH khach_hang_stats AS (
        SELECT 
            h.makh,
            COUNT(h.mahd) as so_hoa_don,
            SUM(h.tongtien) as tong_chi_tieu
        FROM hoadon h
        WHERE h.ngaylap >= v_tu_ngay::timestamp
          AND h.ngaylap <= (v_den_ngay + INTERVAL '1 day')::timestamp
          AND h.trangthai = 1
          AND h.isdeleted = FALSE
          AND h.makh IS NOT NULL
          AND (p_mach IS NULL OR EXISTS (
              SELECT 1 FROM nhanvien nv 
              WHERE nv.manv = h.manvlap 
                AND nv.mach = p_mach
          ))
        GROUP BY h.makh
        HAVING SUM(h.tongtien) >= 1000000 OR COUNT(h.mahd) >= 5
    )
    SELECT 
        COUNT(*),
        COALESCE(SUM(tong_chi_tieu), 0)
    INTO v_so_kh_tiem_nang, v_tong_chi_tieu
    FROM khach_hang_stats;
    
    -- Kiểm tra báo cáo đã tồn tại
    SELECT mabaocao INTO v_ma_baocao
    FROM thongkebaocao 
    WHERE loaibaocao = 'KHACH_HANG_TIEM_NANG'
      AND EXTRACT(MONTH FROM thoigiantu) = p_thang
      AND EXTRACT(YEAR FROM thoigiantu) = p_nam
      AND (p_mach IS NULL OR (cuahang.mach = p_mach))
      AND isdeleted = FALSE
    LIMIT 1;
    
    -- Tạo mới hoặc cập nhật
    IF v_ma_baocao IS NULL THEN
        INSERT INTO thongkebaocao (
            mach, manv, loaibaocao, tenbaocao, thoigiantu, thoigianden,
            sotien, soluong, ngaybaocao, noidung, trangthai, isdeleted
        ) VALUES (
            p_mach, v_manv, 'KHACH_HANG_TIEM_NANG', v_ten_baocao,
            v_tu_ngay::timestamp, (v_den_ngay + INTERVAL '1 day')::timestamp,
            v_tong_chi_tieu, v_so_kh_tiem_nang, CURRENT_TIMESTAMP,
            'Số khách hàng tiềm năng: ' || v_so_kh_tiem_nang || 
            ', Tổng chi tiêu: ' || v_tong_chi_tieu || 
            ' (Tiêu chí: Chi tiêu >= 1,000,000 VND hoặc >= 5 hóa đơn)',
            1, FALSE
        );
    ELSE
        UPDATE thongkebaocao 
        SET sotien = v_tong_chi_tieu,
            soluong = v_so_kh_tiem_nang,
            ngaybaocao = CURRENT_TIMESTAMP,
            noidung = 'Số khách hàng tiềm năng: ' || v_so_kh_tiem_nang || 
                     ', Tổng chi tiêu: ' || v_tong_chi_tieu || 
                     ' (Tiêu chí: Chi tiêu >= 1,000,000 VND hoặc >= 5 hóa đơn)'
        WHERE mabaocao = v_ma_baocao;
    END IF;
    
    RAISE NOTICE 'Đã cập nhật thống kê khách hàng tiềm năng tháng %/% - Số lượng: %', p_thang, p_nam, v_so_kh_tiem_nang;
END;
$$ LANGUAGE plpgsql;

-- 3. Trigger function tự động tính toán khi có hóa đơn mới
CREATE OR REPLACE FUNCTION fn_auto_update_thong_ke()
RETURNS TRIGGER AS $$
DECLARE
    v_thang INT;
    v_nam INT;
    v_mach VARCHAR(50);
BEGIN
    -- Lấy thông tin tháng, năm từ hóa đơn
    IF TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN
        v_thang := EXTRACT(MONTH FROM NEW.ngaylap);
        v_nam := EXTRACT(YEAR FROM NEW.ngaylap);
        
        -- Lấy mã cửa hàng từ nhân viên lập hóa đơn
        SELECT nv.mach INTO v_mach 
        FROM nhanvien nv 
        WHERE nv.manv = NEW.manvlap;
        
        -- Chỉ tính toán khi hóa đơn được thanh toán (trangthai = 1)
        IF NEW.trangthai = 1 AND NEW.isdeleted = FALSE THEN
            -- Tính toán doanh thu tháng
            PERFORM sp_tinh_doanh_thu_thang(v_thang, v_nam, v_mach);
            PERFORM sp_tinh_doanh_thu_thang(v_thang, v_nam, NULL); -- Tổng hệ thống
            
            -- Tính toán khách hàng tiềm năng
            PERFORM sp_tinh_khach_hang_tiem_nang(v_thang, v_nam, v_mach);
            PERFORM sp_tinh_khach_hang_tiem_nang(v_thang, v_nam, NULL); -- Tổng hệ thống
        END IF;
        
        RETURN NEW;
    END IF;
    
    -- Trường hợp DELETE
    IF TG_OP = 'DELETE' THEN
        v_thang := EXTRACT(MONTH FROM OLD.ngaylap);
        v_nam := EXTRACT(YEAR FROM OLD.ngaylap);
        
        SELECT nv.mach INTO v_mach 
        FROM nhanvien nv 
        WHERE nv.manv = OLD.manvlap;
        
        -- Tính lại thống kê khi xóa hóa đơn
        PERFORM sp_tinh_doanh_thu_thang(v_thang, v_nam, v_mach);
        PERFORM sp_tinh_doanh_thu_thang(v_thang, v_nam, NULL);
        PERFORM sp_tinh_khach_hang_tiem_nang(v_thang, v_nam, v_mach);
        PERFORM sp_tinh_khach_hang_tiem_nang(v_thang, v_nam, NULL);
        
        RETURN OLD;
    END IF;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 4. Tạo trigger tự động
DROP TRIGGER IF EXISTS trg_auto_thong_ke_hoa_don ON hoadon;
CREATE TRIGGER trg_auto_thong_ke_hoa_don
    AFTER INSERT OR UPDATE OR DELETE ON hoadon
    FOR EACH ROW
    EXECUTE FUNCTION fn_auto_update_thong_ke();

-- 5. Stored Procedure tính toán lại toàn bộ thống kê cho một tháng
CREATE OR REPLACE FUNCTION sp_tinh_lai_thong_ke_thang(
    p_thang INT,
    p_nam INT
)
RETURNS VOID AS $$
DECLARE
    rec RECORD;
BEGIN
    -- Tính cho từng cửa hàng
    FOR rec IN 
        SELECT DISTINCT mach 
        FROM cuahang 
        WHERE isdeleted = FALSE AND trangthai = 1
    LOOP
        PERFORM sp_tinh_doanh_thu_thang(p_thang, p_nam, rec.mach);
        PERFORM sp_tinh_khach_hang_tiem_nang(p_thang, p_nam, rec.mach);
    END LOOP;
    
    -- Tính tổng hệ thống
    PERFORM sp_tinh_doanh_thu_thang(p_thang, p_nam, NULL);
    PERFORM sp_tinh_khach_hang_tiem_nang(p_thang, p_nam, NULL);
    
    RAISE NOTICE 'Đã tính lại toàn bộ thống kê cho tháng %/%', p_thang, p_nam;
END;
$$ LANGUAGE plpgsql;

-- 6. Tạo job tự động chạy cuối mỗi tháng (cần extension pg_cron)
-- SELECT cron.schedule('tinh-thong-ke-thang', '0 1 1 * *', 'SELECT sp_tinh_lai_thong_ke_thang(EXTRACT(MONTH FROM CURRENT_DATE - INTERVAL ''1 month'')::INT, EXTRACT(YEAR FROM CURRENT_DATE - INTERVAL ''1 month'')::INT);');

-- ===================================
-- CÁC QUERY MẪU ĐỂ XEM THỐNG KÊ
-- ===================================

-- Xem thống kê doanh thu theo tháng
/*
SELECT 
    t.tenbaocao,
    t.sotien as doanh_thu,
    t.soluong as so_hoa_don,
    t.thoigiantu,
    t.thoigianden,
    t.noidung,
    c.tench as ten_cua_hang
FROM thongkebaocao t
LEFT JOIN cuahang c ON t.mach = c.mach
WHERE t.loaibaocao = 'DOANH_THU_THANG'
  AND t.isdeleted = FALSE
ORDER BY t.thoigiantu DESC;
*/

-- Xem thống kê khách hàng tiềm năng
/*
SELECT 
    t.tenbaocao,
    t.soluong as so_khach_hang_tiem_nang,
    t.sotien as tong_chi_tieu,
    t.thoigiantu,
    t.thoigianden,
    t.noidung,
    c.tench as ten_cua_hang
FROM thongkebaocao t
LEFT JOIN cuahang c ON t.mach = c.mach
WHERE t.loaibaocao = 'KHACH_HANG_TIEM_NANG'
  AND t.isdeleted = FALSE
ORDER BY t.thoigiantu DESC;
*/

-- So sánh doanh thu giữa các tháng
/*
SELECT 
    EXTRACT(MONTH FROM t.thoigiantu) as thang,
    EXTRACT(YEAR FROM t.thoigiantu) as nam,
    t.sotien as doanh_thu,
    LAG(t.sotien) OVER (ORDER BY t.thoigiantu) as doanh_thu_thang_truoc,
    CASE 
        WHEN LAG(t.sotien) OVER (ORDER BY t.thoigiantu) > 0 THEN
            ROUND(((t.sotien - LAG(t.sotien) OVER (ORDER BY t.thoigiantu)) / LAG(t.sotien) OVER (ORDER BY t.thoigiantu) * 100)::numeric, 2)
        ELSE NULL
    END as tang_truong_phan_tram
FROM thongkebaocao t
WHERE t.loaibaocao = 'DOANH_THU_THANG'
  AND t.mach IS NULL -- Chỉ lấy tổng hệ thống
  AND t.isdeleted = FALSE
ORDER BY t.thoigiantu DESC;
*/