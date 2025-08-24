-- ===== TRIGGERS QUẢN LÝ TỒN KHO TỰ ĐỘNG =====
-- File: inventory-triggers.sql
-- Mô tả: Các trigger tự động cập nhật tồn kho khi có thay đổi về hóa đơn

-- ===== FUNCTION: Cập nhật tồn kho khi thêm/sửa chi tiết hóa đơn =====
CREATE OR REPLACE FUNCTION update_inventory_on_invoice_detail()
RETURNS TRIGGER AS $$
DECLARE
    v_trangthai_hoadon INT;
    v_soluong_old INT := 0;
    v_soluong_new INT := 0;
    v_masp VARCHAR(50);
    v_makho VARCHAR(50);
    v_soluong_ton_hientai INT;
BEGIN
    -- Lấy trạng thái hóa đơn
    SELECT trangthai INTO v_trangthai_hoadon 
    FROM hoadon 
    WHERE mahd = NEW.mahd;
    
    -- Chỉ xử lý khi hóa đơn đã thanh toán (trạng thái = 1)
    IF v_trangthai_hoadon != 1 THEN
        RAISE NOTICE 'Hóa đơn % chưa thanh toán (trạng thái: %), không cập nhật tồn kho', NEW.mahd, v_trangthai_hoadon;
        RETURN NEW;
    END IF;
    
    -- Xác định mã sản phẩm
    v_masp := NEW.masp;
    
    -- Xử lý INSERT (thêm mới)
    IF TG_OP = 'INSERT' THEN
        v_soluong_new := NEW.soluong;
        RAISE NOTICE 'INSERT: Thêm sản phẩm % với số lượng %', v_masp, v_soluong_new;
        
        -- Tìm kho có sản phẩm này (lấy kho đầu tiên)
        SELECT k.makho, tk.soluongton 
        INTO v_makho, v_soluong_ton_hientai
        FROM tonkhochitiet tk
        JOIN kho k ON tk.makho = k.makho
        WHERE tk.masp = v_masp AND tk.isdeleted = FALSE
        LIMIT 1;
        
        IF v_makho IS NULL THEN
            RAISE EXCEPTION 'Không tìm thấy tồn kho cho sản phẩm %', v_masp;
        END IF;
        
        -- Kiểm tra số lượng tồn kho có đủ không
        IF v_soluong_ton_hientai < v_soluong_new THEN
            RAISE EXCEPTION 'Sản phẩm % chỉ còn % trong kho %, không đủ để bán %', 
                v_masp, v_soluong_ton_hientai, v_makho, v_soluong_new;
        END IF;
        
        -- Trừ số lượng tồn kho
        UPDATE tonkhochitiet 
        SET soluongton = soluongton - v_soluong_new,
            ngaycapnhat = CURRENT_TIMESTAMP
        WHERE makho = v_makho AND masp = v_masp AND isdeleted = FALSE;
        
        RAISE NOTICE 'Đã trừ tồn kho: Sản phẩm % - Số lượng: % - Kho: % - Tồn kho mới: %', 
            v_masp, v_soluong_new, v_makho, (v_soluong_ton_hientai - v_soluong_new);
    
    -- Xử lý UPDATE (cập nhật)
    ELSIF TG_OP = 'UPDATE' THEN
        v_soluong_old := COALESCE(OLD.soluong, 0);
        v_soluong_new := NEW.soluong;
        
        -- Chỉ xử lý nếu số lượng thay đổi
        IF v_soluong_old != v_soluong_new THEN
            RAISE NOTICE 'UPDATE: Sản phẩm % thay đổi số lượng từ % thành %', v_masp, v_soluong_old, v_soluong_new;
            
            -- Tìm kho có sản phẩm này
            SELECT k.makho, tk.soluongton 
            INTO v_makho, v_soluong_ton_hientai
            FROM tonkhochitiet tk
            JOIN kho k ON tk.makho = k.makho
            WHERE tk.masp = v_masp AND tk.isdeleted = FALSE
            LIMIT 1;
            
            IF v_makho IS NULL THEN
                RAISE EXCEPTION 'Không tìm thấy tồn kho cho sản phẩm %', v_masp;
            END IF;
            
            -- Tính toán số lượng cần điều chỉnh
            DECLARE
                v_dieu_chinh INT;
            BEGIN
                v_dieu_chinh := v_soluong_old - v_soluong_new;
                
                -- Nếu số lượng mới > số lượng cũ: trừ thêm tồn kho
                IF v_dieu_chinh < 0 THEN
                    -- Kiểm tra tồn kho có đủ không
                    IF v_soluong_ton_hientai < ABS(v_dieu_chinh) THEN
                        RAISE EXCEPTION 'Sản phẩm % chỉ còn % trong kho %, không đủ để tăng số lượng từ % lên %', 
                            v_masp, v_soluong_ton_hientai, v_makho, v_soluong_old, v_soluong_new;
                    END IF;
                    
                    -- Trừ thêm tồn kho
                    UPDATE tonkhochitiet 
                    SET soluongton = soluongton - ABS(v_dieu_chinh),
                        ngaycapnhat = CURRENT_TIMESTAMP
                    WHERE makho = v_makho AND masp = v_masp AND isdeleted = FALSE;
                    
                    RAISE NOTICE 'Đã trừ thêm tồn kho: Sản phẩm % - Số lượng điều chỉnh: % - Kho: %', 
                        v_masp, ABS(v_dieu_chinh), v_makho;
                
                -- Nếu số lượng mới < số lượng cũ: hoàn trả tồn kho
                ELSIF v_dieu_chinh > 0 THEN
                    -- Hoàn trả tồn kho
                    UPDATE tonkhochitiet 
                    SET soluongton = soluongton + v_dieu_chinh,
                        ngaycapnhat = CURRENT_TIMESTAMP
                    WHERE makho = v_makho AND masp = v_masp AND isdeleted = FALSE;
                    
                    RAISE NOTICE 'Đã hoàn trả tồn kho: Sản phẩm % - Số lượng điều chỉnh: % - Kho: %', 
                        v_masp, v_dieu_chinh, v_makho;
                END IF;
            END;
        END IF;
    
    -- Xử lý DELETE (xóa)
    ELSIF TG_OP = 'DELETE' THEN
        v_soluong_old := OLD.soluong;
        RAISE NOTICE 'DELETE: Xóa sản phẩm % với số lượng %', v_masp, v_soluong_old;
        
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
            SET soluongton = soluongton + v_soluong_old,
                ngaycapnhat = CURRENT_TIMESTAMP
            WHERE makho = v_makho AND masp = v_masp AND isdeleted = FALSE;
            
            RAISE NOTICE 'Đã hoàn trả tồn kho: Sản phẩm % - Số lượng: % - Kho: %', 
                v_masp, v_soluong_old, v_makho;
        END IF;
        
        RETURN OLD;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ===== TRIGGER: Tự động cập nhật tồn kho khi thay đổi chi tiết hóa đơn =====
DROP TRIGGER IF EXISTS trigger_update_inventory_on_invoice_detail ON chitiethoadon;

CREATE TRIGGER trigger_update_inventory_on_invoice_detail
    AFTER INSERT OR UPDATE OR DELETE ON chitiethoadon
    FOR EACH ROW
    EXECUTE FUNCTION update_inventory_on_invoice_detail();

-- ===== FUNCTION: Cập nhật tồn kho khi thay đổi trạng thái hóa đơn =====
CREATE OR REPLACE FUNCTION update_inventory_on_invoice_status_change()
RETURNS TRIGGER AS $$
DECLARE
    v_mahd INT;
    v_trangthai_old INT;
    v_trangthai_new INT;
    v_masp VARCHAR(50);
    v_soluong INT;
    v_makho VARCHAR(50);
    v_soluong_ton_hientai INT;
    v_chi_tiet_cursor CURSOR FOR
        SELECT masp, soluong 
        FROM chitiethoadon 
        WHERE mahd = v_mahd AND isdeleted = FALSE;
BEGIN
    v_mahd := NEW.mahd;
    v_trangthai_old := COALESCE(OLD.trangthai, 0);
    v_trangthai_new := NEW.trangthai;
    
    -- Chỉ xử lý khi trạng thái thay đổi
    IF v_trangthai_old = v_trangthai_new THEN
        RETURN NEW;
    END IF;
    
    RAISE NOTICE 'Hóa đơn % thay đổi trạng thái từ % thành %', v_mahd, v_trangthai_old, v_trangthai_new;
    
    -- Khi chuyển sang trạng thái đã thanh toán (1)
    IF v_trangthai_new = 1 AND v_trangthai_old != 1 THEN
        RAISE NOTICE 'Hóa đơn % đã thanh toán, bắt đầu trừ tồn kho...', v_mahd;
        
        -- Duyệt qua tất cả chi tiết hóa đơn
        FOR v_chi_tiet_record IN v_chi_tiet_cursor LOOP
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
            END IF;
        END LOOP;
        
        RAISE NOTICE 'Hoàn thành trừ tồn kho cho hóa đơn %', v_mahd;
    
    -- Khi chuyển từ trạng thái đã thanh toán sang trạng thái khác (hủy, hoàn trả)
    ELSIF v_trangthai_old = 1 AND v_trangthai_new != 1 THEN
        RAISE NOTICE 'Hóa đơn % đã hủy/hoàn trả, bắt đầu hoàn trả tồn kho...', v_mahd;
        
        -- Duyệt qua tất cả chi tiết hóa đơn
        FOR v_chi_tiet_record IN v_chi_tiet_cursor LOOP
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
            END IF;
        END LOOP;
        
        RAISE NOTICE 'Hoàn thành hoàn trả tồn kho cho hóa đơn %', v_mahd;
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
COMMENT ON FUNCTION update_inventory_on_invoice_detail() IS 
'Function tự động cập nhật tồn kho khi thêm/sửa/xóa chi tiết hóa đơn. Chỉ xử lý khi hóa đơn đã thanh toán (trạng thái = 1)';

COMMENT ON FUNCTION update_inventory_on_invoice_status_change() IS 
'Function tự động cập nhật tồn kho khi thay đổi trạng thái hóa đơn. Trừ tồn kho khi thanh toán, hoàn trả khi hủy/hoàn trả';

COMMENT ON TRIGGER trigger_update_inventory_on_invoice_detail ON chitiethoadon IS 
'Trigger tự động cập nhật tồn kho khi có thay đổi về chi tiết hóa đơn';

COMMENT ON TRIGGER trigger_update_inventory_on_invoice_status ON hoadon IS 
'Trigger tự động cập nhật tồn kho khi thay đổi trạng thái hóa đơn';

-- ===== KIỂM TRA TRIGGER ĐÃ TẠO =====
SELECT 
    trigger_name,
    event_manipulation,
    event_object_table,
    action_statement
FROM information_schema.triggers 
WHERE trigger_name LIKE '%inventory%'
ORDER BY trigger_name;
