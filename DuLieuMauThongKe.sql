-- ===================================
-- DỮ LIỆU MẪU ĐỂ TEST THỐNG KÊ TỰ ĐỘNG
-- ===================================

-- Thêm dữ liệu mẫu cho các bảng cần thiết (nếu chưa có)

-- 1. Thêm cửa hàng mẫu
INSERT INTO cuahang (mach, tench, diachi, sdt, ngaythanhlap, trangthai, isdeleted) VALUES
('CH001', 'Siêu thị Mini Quận 1', '123 Nguyễn Huệ, Quận 1, TP.HCM', '0901234567', '2023-01-01', 1, false),
('CH002', 'Siêu thị Mini Quận 2', '456 Đông Khởi, Quận 2, TP.HCM', '0901234568', '2023-02-01', 1, false)
ON CONFLICT (mach) DO NOTHING;

-- 2. Thêm nhân viên mẫu
INSERT INTO nhanvien (manv, hoten, sdt, diachi, ngaysinh, ngayvaolam, chucvu, mach, trangthai, isdeleted) VALUES
('NV001', 'Nguyễn Văn Admin', '0987654321', 'TP.HCM', '1990-01-01', '2023-01-01', 'Quản lý', 'CH001', 1, false),
('NV002', 'Trần Thị Thu Nhân', '0987654322', 'TP.HCM', '1992-05-15', '2023-01-15', 'Nhân viên bán hàng', 'CH001', 1, false),
('NV003', 'Lê Văn Bình', '0987654323', 'TP.HCM', '1991-08-20', '2023-02-01', 'Nhân viên bán hàng', 'CH002', 1, false)
ON CONFLICT (manv) DO NOTHING;

-- 3. Thêm khách hàng mẫu
INSERT INTO khachhang (makh, hoten, sdt, diachi, ngaysinh, diemtichluy, loaikhachhang, ngaydangky, isdeleted) VALUES
('KH001', 'Nguyễn Thị Lan', '0123456789', 'Quận 1, TP.HCM', '1985-03-15', 1500, 'VIP', '2023-01-01', false),
('KH002', 'Trần Văn Hùng', '0123456790', 'Quận 2, TP.HCM', '1988-07-22', 800, 'Thường', '2023-01-15', false),
('KH003', 'Lê Thị Mai', '0123456791', 'Quận 3, TP.HCM', '1990-12-10', 2000, 'VIP', '2023-02-01', false),
('KH004', 'Phạm Văn Đức', '0123456792', 'Quận 4, TP.HCM', '1987-04-05', 500, 'Thường', '2023-02-15', false),
('KH005', 'Hoàng Thị Nga', '0123456793', 'Quận 5, TP.HCM', '1992-09-18', 1200, 'Silver', '2023-03-01', false)
ON CONFLICT (makh) DO NOTHING;

-- 4. Thêm loại sản phẩm mẫu
INSERT INTO loaisanpham (maloaisp, tenloai, mota, thutuhienthi, isdeleted) VALUES
('LSP001', 'Thực phẩm tươi sống', 'Rau củ, thịt cá, trái cây tươi', 1, false),
('LSP002', 'Đồ uống', 'Nước ngọt, bia, nước suối', 2, false),
('LSP003', 'Bánh kẹo', 'Bánh quy, kẹo, chocolate', 3, false),
('LSP004', 'Gia vị', 'Muối, đường, nước mắm, dầu ăn', 4, false)
ON CONFLICT (maloaisp) DO NOTHING;

-- 5. Thêm sản phẩm mẫu
INSERT INTO sanpham (masp, maloaisp, tensp, mota, donvitinh, trongluong, trangthai, ngaytao, isdeleted) VALUES
('SP001', 'LSP001', 'Thịt heo ba chỉ', 'Thịt heo ba chỉ tươi ngon', 'kg', 1.0, 1, CURRENT_TIMESTAMP, false),
('SP002', 'LSP002', 'Coca Cola 330ml', 'Nước ngọt Coca Cola lon 330ml', 'lon', 0.33, 1, CURRENT_TIMESTAMP, false),
('SP003', 'LSP003', 'Bánh Oreo', 'Bánh quy Oreo nhân kem', 'gói', 0.15, 1, CURRENT_TIMESTAMP, false),
('SP004', 'LSP004', 'Dầu ăn Simply', 'Dầu ăn Simply chai 1L', 'chai', 1.0, 1, CURRENT_TIMESTAMP, false),
('SP005', 'LSP001', 'Cá thu', 'Cá thu tươi', 'kg', 1.0, 1, CURRENT_TIMESTAMP, false)
ON CONFLICT (masp) DO NOTHING;

-- 6. Thêm giá sản phẩm mẫu
INSERT INTO giasanpham (masp, gia, ngaybatdau, ngayketthuc, lydothaydoi, nguoithaydoi, isdeleted) VALUES
('SP001', 180000.00, '2023-01-01', NULL, 'Giá khởi điểm', 'NV001', false),
('SP002', 15000.00, '2023-01-01', NULL, 'Giá khởi điểm', 'NV001', false),
('SP003', 25000.00, '2023-01-01', NULL, 'Giá khởi điểm', 'NV001', false),
('SP004', 45000.00, '2023-01-01', NULL, 'Giá khởi điểm', 'NV001', false),
('SP005', 120000.00, '2023-01-01', NULL, 'Giá khởi điểm', 'NV001', false)
ON CONFLICT DO NOTHING;

-- 7. Thêm phương thức thanh toán
INSERT INTO phuongthucthanhtoan (mapttt, tenpttt, mota, phigiaodich, trangthai, isdeleted) VALUES
('CASH', 'Tiền mặt', 'Thanh toán bằng tiền mặt', 0.0000, 1, false),
('CARD', 'Thẻ tín dụng', 'Thanh toán bằng thẻ tín dụng', 0.0200, 1, false),
('MOMO', 'MoMo', 'Thanh toán qua ví MoMo', 0.0100, 1, false)
ON CONFLICT (mapttt) DO NOTHING;

-- ===================================
-- TẠO HÓA ĐƠN MẪU ĐỂ TEST THỐNG KÊ
-- ===================================

-- Tạo hóa đơn cho tháng 1/2024
INSERT INTO hoadon (makh, manvlap, ngaylap, tongtienhang, tiengiamgia, mapttt, trangthai, diemtichluy, ghichu, nguoitao, isdeleted) VALUES
-- Tháng 1/2024
('KH001', 'NV001', '2024-01-05 10:30:00', 500000.00, 0.00, 'CASH', 1, 50, 'Hóa đơn mua thực phẩm', 'NV001', false),
('KH002', 'NV002', '2024-01-10 14:15:00', 350000.00, 20000.00, 'MOMO', 1, 35, 'Hóa đơn mua đồ uống', 'NV002', false),
('KH003', 'NV001', '2024-01-15 16:45:00', 1200000.00, 50000.00, 'CARD', 1, 120, 'Hóa đơn mua sắm lớn', 'NV001', false),
('KH001', 'NV002', '2024-01-20 09:20:00', 800000.00, 0.00, 'CASH', 1, 80, 'Hóa đơn mua thịt cá', 'NV002', false),
('KH004', 'NV003', '2024-01-25 11:10:00', 450000.00, 30000.00, 'MOMO', 1, 45, 'Hóa đơn CH002', 'NV003', false),

-- Tháng 2/2024
('KH002', 'NV001', '2024-02-03 13:25:00', 650000.00, 25000.00, 'CARD', 1, 65, 'Hóa đơn tháng 2', 'NV001', false),
('KH005', 'NV002', '2024-02-08 15:40:00', 900000.00, 0.00, 'CASH', 1, 90, 'Khách hàng VIP', 'NV002', false),
('KH003', 'NV003', '2024-02-12 10:15:00', 750000.00, 40000.00, 'MOMO', 1, 75, 'Hóa đơn CH002', 'NV003', false),
('KH001', 'NV001', '2024-02-18 17:30:00', 1500000.00, 100000.00, 'CARD', 1, 150, 'Mua sắm lớn tháng 2', 'NV001', false),
('KH004', 'NV002', '2024-02-22 12:50:00', 380000.00, 0.00, 'CASH', 1, 38, 'Hóa đơn nhỏ', 'NV002', false),

-- Tháng 3/2024
('KH005', 'NV001', '2024-03-02 09:45:00', 1100000.00, 80000.00, 'CARD', 1, 110, 'Hóa đơn tháng 3', 'NV001', false),
('KH002', 'NV003', '2024-03-07 14:20:00', 420000.00, 20000.00, 'MOMO', 1, 42, 'Hóa đơn CH002', 'NV003', false),
('KH001', 'NV002', '2024-03-15 16:10:00', 850000.00, 0.00, 'CASH', 1, 85, 'Khách quen', 'NV002', false),
('KH003', 'NV001', '2024-03-20 11:35:00', 2000000.00, 150000.00, 'CARD', 1, 200, 'Hóa đơn lớn nhất', 'NV001', false),
('KH004', 'NV003', '2024-03-25 13:55:00', 320000.00, 10000.00, 'MOMO', 1, 32, 'Hóa đơn cuối tháng', 'NV003', false);

-- Lấy ID của các hóa đơn vừa tạo để tạo chi tiết
-- (Trong thực tế, bạn sẽ cần lấy mahd thực tế từ database)

-- Tạo chi tiết hóa đơn mẫu (giả sử mahd từ 1 đến 15)
INSERT INTO chitiethoadon (mahd, masp, soluong, dongiaban, giamgia, isdeleted) VALUES
-- Chi tiết cho hóa đơn 1 (500000)
(1, 'SP001', 2, 180000.00, 0.00, false),
(1, 'SP002', 10, 15000.00, 10000.00, false),

-- Chi tiết cho hóa đơn 2 (350000)
(2, 'SP003', 5, 25000.00, 0.00, false),
(2, 'SP004', 5, 45000.00, 0.00, false),

-- Chi tiết cho hóa đơn 3 (1200000)
(3, 'SP001', 5, 180000.00, 0.00, false),
(3, 'SP005', 3, 120000.00, 0.00, false),
(3, 'SP004', 2, 45000.00, 0.00, false),

-- Thêm chi tiết cho các hóa đơn khác tương tự...
(4, 'SP001', 3, 180000.00, 0.00, false),
(4, 'SP002', 15, 15000.00, 0.00, false),

(5, 'SP003', 8, 25000.00, 0.00, false),
(5, 'SP004', 5, 45000.00, 0.00, false);

-- ===================================
-- CHẠY CÁC STORED PROCEDURE ĐỂ TÍNH THỐNG KÊ
-- ===================================

-- Tính thống kê doanh thu cho các tháng có dữ liệu
SELECT sp_tinh_doanh_thu_thang(1, 2024, 'CH001');
SELECT sp_tinh_doanh_thu_thang(1, 2024, 'CH002');
SELECT sp_tinh_doanh_thu_thang(1, 2024, NULL); -- Tổng hệ thống

SELECT sp_tinh_doanh_thu_thang(2, 2024, 'CH001');
SELECT sp_tinh_doanh_thu_thang(2, 2024, 'CH002');
SELECT sp_tinh_doanh_thu_thang(2, 2024, NULL);

SELECT sp_tinh_doanh_thu_thang(3, 2024, 'CH001');
SELECT sp_tinh_doanh_thu_thang(3, 2024, 'CH002');
SELECT sp_tinh_doanh_thu_thang(3, 2024, NULL);

-- Tính thống kê khách hàng tiềm năng
SELECT sp_tinh_khach_hang_tiem_nang(1, 2024, 'CH001');
SELECT sp_tinh_khach_hang_tiem_nang(1, 2024, 'CH002');
SELECT sp_tinh_khach_hang_tiem_nang(1, 2024, NULL);

SELECT sp_tinh_khach_hang_tiem_nang(2, 2024, 'CH001');
SELECT sp_tinh_khach_hang_tiem_nang(2, 2024, 'CH002');
SELECT sp_tinh_khach_hang_tiem_nang(2, 2024, NULL);

SELECT sp_tinh_khach_hang_tiem_nang(3, 2024, 'CH001');
SELECT sp_tinh_khach_hang_tiem_nang(3, 2024, 'CH002');
SELECT sp_tinh_khach_hang_tiem_nang(3, 2024, NULL);

-- ===================================
-- QUERY KIỂM TRA KẾT QUẢ
-- ===================================

-- Xem thống kê doanh thu đã được tính
SELECT 
    t.tenbaocao,
    t.sotien as doanh_thu,
    t.soluong as so_hoa_don,
    t.thoigiantu,
    t.thoigianden,
    COALESCE(c.tench, 'Tổng hệ thống') as ten_cua_hang,
    t.noidung
FROM thongkebaocao t
LEFT JOIN cuahang c ON t.mach = c.mach
WHERE t.loaibaocao = 'DOANH_THU_THANG'
  AND t.isdeleted = FALSE
ORDER BY t.thoigiantu DESC, c.tench;

-- Xem thống kê khách hàng tiềm năng
SELECT 
    t.tenbaocao,
    t.soluong as so_khach_hang_tiem_nang,
    t.sotien as tong_chi_tieu,
    t.thoigiantu,
    t.thoigianden,
    COALESCE(c.tench, 'Tổng hệ thống') as ten_cua_hang,
    t.noidung
FROM thongkebaocao t
LEFT JOIN cuahang c ON t.mach = c.mach
WHERE t.loaibaocao = 'KHACH_HANG_TIEM_NANG'
  AND t.isdeleted = FALSE
ORDER BY t.thoigiantu DESC, c.tench;

-- Kiểm tra trigger hoạt động - tạo hóa đơn mới
INSERT INTO hoadon (makh, manvlap, ngaylap, tongtienhang, tiengiamgia, mapttt, trangthai, diemtichluy, ghichu, nguoitao, isdeleted) VALUES
('KH001', 'NV001', CURRENT_TIMESTAMP, 600000.00, 0.00, 'CASH', 1, 60, 'Test trigger tự động', 'NV001', false);

-- Kiểm tra xem thống kê có được cập nhật tự động không
SELECT 
    t.tenbaocao,
    t.sotien,
    t.soluong,
    t.ngaybaocao
FROM thongkebaocao t
WHERE t.loaibaocao IN ('DOANH_THU_THANG', 'KHACH_HANG_TIEM_NANG')
  AND EXTRACT(MONTH FROM t.thoigiantu) = EXTRACT(MONTH FROM CURRENT_DATE)
  AND EXTRACT(YEAR FROM t.thoigiantu) = EXTRACT(YEAR FROM CURRENT_DATE)
ORDER BY t.ngaybaocao DESC;