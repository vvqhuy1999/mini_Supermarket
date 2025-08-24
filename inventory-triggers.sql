-- ===== TRIGGERS QUẢN LÝ TỒN KHO TỰ ĐỘNG =====
-- File: inventory-triggers.sql
-- Mô tả: Các trigger tự động cập nhật tồn kho khi có thay đổi về hóa đơn

-- ===== FUNCTION: Cập nhật tồn kho khi thay đổi trạng thái hóa đơn =====
-- Chỉ xử lý khi trạng thái thay đổi từ 0 (chưa thanh toán) sang 1 (đã thanh toán)
CREATE OR REPLACE FUNCTION update_inventory_on_invoice_status_change()
RETURNS TRIGGER AS $$
DECLARE
    v_mahd INTEGER;
    v_trangthai_old INTEGER;
    v_trangthai_new INTEGER;
    v_masp VARCHAR(50);
    v_soluong INTEGER;
    v_makho INTEGER;
    v_soluong_ton_hientai INTEGER;
    v_chi_tiet_record RECORD;
BEGIN
    v_mahd := NEW.mahd;
    v_trangthai_old := COALESCE(OLD.trangthai, 0);
    v_trangthai_new := NEW.trangthai;
    
    -- Chỉ xử lý khi trạng thái thay đổi
    IF v_trangthai_old = v_trangthai_new THEN
        RETURN NEW;
    END IF;
    
    RAISE NOTICE 'Hóa đơn % thay đổi trạng thái từ % thành %', v_mahd, v_trangthai_old, v_trangthai_new;
    
    -- 🔑 CHỈ XỬ LÝ KHI CHUYỂN TỪ CHƯA THANH TOÁN (0) SANG ĐÃ THANH TOÁN (1)
    IF v_trangthai_new = 1 AND v_trangthai_old = 0 THEN
        RAISE NOTICE 'Hóa đơn % đã thanh toán, bắt đầu trừ tồn kho...', v_mahd;
        
        -- Duyệt qua tất cả chi tiết hóa đơn để trừ tồn kho
        FOR v_chi_tiet_record IN 
            SELECT masp, soluong 
            FROM chitiethoadon 
            WHERE mahd = v_mahd AND isdeleted = FALSE
        LOOP
            v_masp := v_chi_tiet_record.masp;
            v_soluong := v_chi_tiet_record.soluong;
            
            -- Tìm kho có sản phẩm này
            SELECT k.makho, tk.soluongton 
            INTO v_makho, v_soluong_ton_hientai
            FROM tonkhochitiet tk
            JOIN kho k ON tk.makho = k.makho
            WHERE tk.masp = v_masp AND tk.isdeleted = FALSE
            LIMIT 1;
            
            IF v_makho IS NOT NULL THEN
                -- Kiểm tra tồn kho có đủ không
                IF v_soluong_ton_hientai < v_soluong THEN
                    RAISE EXCEPTION 'Sản phẩm % chỉ còn % trong kho %, không đủ để bán %', 
                        v_masp, v_soluong_ton_hientai, v_makho, v_soluong;
                END IF;
                
                -- Trừ tồn kho
                UPDATE tonkhochitiet 
                SET soluongton = soluongton - v_soluong,
                    ngaycapnhat = CURRENT_TIMESTAMP
                WHERE makho = v_makho AND masp = v_masp AND isdeleted = FALSE;
                
                RAISE NOTICE 'Đã trừ tồn kho: Sản phẩm % - Số lượng: % - Kho: % - Tồn kho mới: %', 
                    v_masp, v_soluong, v_makho, (v_soluong_ton_hientai - v_soluong);
            ELSE
                RAISE WARNING 'Không tìm thấy tồn kho cho sản phẩm % trong hóa đơn %', v_masp, v_mahd;
            END IF;
        END LOOP;
        
        RAISE NOTICE 'Hoàn thành trừ tồn kho cho hóa đơn %', v_mahd;
    
    -- 🔑 KHI CHUYỂN TỪ ĐÃ THANH TOÁN (1) SANG TRẠNG THÁI KHÁC (hủy, hoàn trả)
    ELSIF v_trangthai_old = 1 AND v_trangthai_new != 1 THEN
        RAISE NOTICE 'Hóa đơn % đã hủy/hoàn trả, bắt đầu hoàn trả tồn kho...', v_mahd;
        
        -- Duyệt qua tất cả chi tiết hóa đơn để hoàn trả tồn kho
        FOR v_chi_tiet_record IN 
            SELECT masp, soluong 
            FROM chitiethoadon 
            WHERE mahd = v_mahd AND isdeleted = FALSE
        LOOP
            v_masp := v_chi_tiet_record.masp;
            v_soluong := v_chi_tiet_record.soluong;
            
            -- Tìm kho có sản phẩm này
            SELECT k.makho, tk.soluongton 
            INTO v_makho, v_soluong_ton_hientai
            FROM tonkhochitiet tk
            JOIN kho k ON tk.makho = k.makho
            WHERE tk.masp = v_masp AND tk.isdeleted = FALSE
            LIMIT 1;
            
            IF v_makho IS NOT NULL THEN
                -- Hoàn trả tồn kho
                UPDATE tonkhochitiet 
                SET soluongton = soluongton + v_soluong,
                    ngaycapnhat = CURRENT_TIMESTAMP
                WHERE makho = v_makho AND masp = v_masp AND isdeleted = FALSE;
                
                RAISE NOTICE 'Đã hoàn trả tồn kho: Sản phẩm % - Số lượng: % - Kho: % - Tồn kho mới: %', 
                    v_masp, v_soluong, v_makho, (v_soluong_ton_hientai + v_soluong);
            ELSE
                RAISE WARNING 'Không tìm thấy tồn kho cho sản phẩm % trong hóa đơn %', v_masp, v_mahd;
            END IF;
        END LOOP;
        
        RAISE NOTICE 'Hoàn thành hoàn trả tồn kho cho hóa đơn %', v_mahd;
    
    -- 🔑 CÁC TRƯỜNG HỢP KHÁC: Không làm gì
    ELSE
        RAISE NOTICE 'Hóa đơn % thay đổi trạng thái từ % sang % - Không ảnh hưởng tồn kho', 
            v_mahd, v_trangthai_old, v_trangthai_new;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ===== TRIGGER: Tự động cập nhật tồn kho khi thay đổi trạng thái hóa đơn =====
DROP TRIGGER IF EXISTS trigger_update_inventory_on_invoice_status ON hoadon;

CREATE TRIGGER trigger_update_inventory_on_invoice_status
    AFTER UPDATE ON hoadon
    FOR EACH ROW
    EXECUTE FUNCTION update_inventory_on_invoice_status_change();

-- ===== COMMENT VÀ MÔ TẢ =====
COMMENT ON FUNCTION update_inventory_on_invoice_status_change() IS 
'Function tự động cập nhật tồn kho khi thay đổi trạng thái hóa đơn. 
- Chỉ trừ tồn kho khi chuyển từ trạng thái 0 (chưa thanh toán) sang 1 (đã thanh toán)
- Hoàn trả tồn kho khi chuyển từ trạng thái 1 sang trạng thái khác (hủy, hoàn trả)
- Không xử lý khi thêm/sửa/xóa chi tiết hóa đơn (chỉ xử lý khi thay đổi trạng thái)';

COMMENT ON TRIGGER trigger_update_inventory_on_invoice_status ON hoadon IS 
'Trigger tự động cập nhật tồn kho khi thay đổi trạng thái hóa đơn. 
Chỉ hoạt động khi trạng thái thay đổi, không hoạt động khi thêm/sửa/xóa chi tiết hóa đơn.';

-- ===== FUNCTION TEST: Kiểm tra trigger có hoạt động không =====
CREATE OR REPLACE FUNCTION test_inventory_trigger()
RETURNS TEXT AS $$
DECLARE
    v_result TEXT := '';
    v_trigger_count INTEGER;
    v_function_count INTEGER;
    v_trigger_info RECORD;
    v_function_info RECORD;
BEGIN
    -- Kiểm tra trigger đã được tạo
    SELECT COUNT(*) INTO v_trigger_count
    FROM information_schema.triggers 
    WHERE trigger_name LIKE '%inventory%';
    
    -- Kiểm tra function đã được tạo
    SELECT COUNT(*) INTO v_function_count
    FROM information_schema.routines 
    WHERE routine_name LIKE '%inventory%';
    
    v_result := '=== KIỂM TRA TRIGGER INVENTORY ===' || E'\n';
    v_result := v_result || 'Số lượng trigger: ' || v_trigger_count || E'\n';
    v_result := v_result || 'Số lượng function: ' || v_function_count || E'\n';
    
    -- Liệt kê các trigger
    v_result := v_result || E'\n=== DANH SÁCH TRIGGER ===' || E'\n';
    FOR v_trigger_info IN 
        SELECT 'Trigger: ' || trigger_name || ' trên bảng ' || event_object_table || ' - Event: ' || event_manipulation AS info
        FROM information_schema.triggers 
        WHERE trigger_name LIKE '%inventory%'
        ORDER BY trigger_name
    LOOP
        v_result := v_result || v_trigger_info.info || E'\n';
    END LOOP;
    
    -- Liệt kê các function
    v_result := v_result || E'\n=== DANH SÁCH FUNCTION ===' || E'\n';
    FOR v_function_info IN 
        SELECT 'Function: ' || routine_name || ' - Type: ' || routine_type AS info
        FROM information_schema.routines 
        WHERE routine_name LIKE '%inventory%'
        ORDER BY routine_name
    LOOP
        v_result := v_result || v_function_info.info || E'\n';
    END LOOP;
    
    RETURN v_result;
END;
$$ LANGUAGE plpgsql;

-- ===== FUNCTION: Kiểm tra tồn kho của một sản phẩm =====
CREATE OR REPLACE FUNCTION check_product_inventory(p_masp VARCHAR(50))
RETURNS TABLE(
    masp VARCHAR(50),
    makho INTEGER,
    soluongton INTEGER,
    ngaycapnhat TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT tk.masp, tk.makho, tk.soluongton, tk.ngaycapnhat
    FROM tonkhochitiet tk
    WHERE tk.masp = p_masp AND tk.isdeleted = FALSE
    ORDER BY tk.makho;
END;
$$ LANGUAGE plpgsql;

-- ===== FUNCTION: Kiểm tra chi tiết hóa đơn =====
CREATE OR REPLACE FUNCTION check_invoice_details(p_mahd INTEGER)
RETURNS TABLE(
    mahd INTEGER,
    masp VARCHAR(50),
    soluong INTEGER,
    dongiaban DECIMAL(10,2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT cthd.mahd, cthd.masp, cthd.soluong, cthd.dongiaban
    FROM chitiethoadon cthd
    WHERE cthd.mahd = p_mahd AND cthd.isdeleted = FALSE
    ORDER BY cthd.masp;
END;
$$ LANGUAGE plpgsql;

-- ===== HƯỚNG DẪN TEST =====
-- 1. Chạy function test để kiểm tra trigger:
-- SELECT test_inventory_trigger();
--
-- 2. Kiểm tra tồn kho trước khi thay đổi trạng thái:
-- SELECT * FROM check_product_inventory('SP001');
--
-- 3. Kiểm tra chi tiết hóa đơn:
-- SELECT * FROM check_invoice_details(1);
--
-- 4. Cập nhật trạng thái hóa đơn qua API:
-- PUT /api/hoadon/1/trangthai/1
--
-- 5. Kiểm tra tồn kho sau khi thay đổi:
-- SELECT * FROM check_product_inventory('SP001');
--
-- 6. Kiểm tra log trong PostgreSQL:
-- SELECT * FROM pg_stat_activity WHERE application_name LIKE '%psql%';

-- ===== KIỂM TRA TRIGGER ĐÃ TẠO =====
SELECT 
    trigger_name,
    event_manipulation,
    event_object_table,
    action_statement
FROM information_schema.triggers 
WHERE trigger_name LIKE '%inventory%'
ORDER BY trigger_name;
