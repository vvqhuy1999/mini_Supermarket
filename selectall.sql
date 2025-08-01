-- ===================================
-- SELECT ALL TABLES - KIỂM TRA DỮ LIỆU
-- ===================================

USE QuanLySieuThi;

-- ===================================
-- 1. BẢNG NGƯỜI DÙNG
-- ===================================
SELECT '=== BẢNG NGUOIDUNG ===' as Table_Info;
SELECT COUNT(*) as Total_Users FROM NguoiDung;
SELECT * FROM NguoiDung LIMIT 10;

-- ===================================
-- 2. BẢNG CỬA HÀNG
-- ===================================
SELECT '=== BẢNG CUAHANG ===' as Table_Info;
SELECT COUNT(*) as Total_Stores FROM CuaHang;
SELECT * FROM CuaHang;

-- ===================================
-- 3. BẢNG NHÀ CUNG CẤP
-- ===================================
SELECT '=== BẢNG NHACUNGCAP ===' as Table_Info;
SELECT COUNT(*) as Total_Suppliers FROM NhaCungCap;
SELECT * FROM NhaCungCap;

-- ===================================
-- 4. BẢNG NHÂN VIÊN
-- ===================================
SELECT '=== BẢNG NHANVIEN ===' as Table_Info;
SELECT COUNT(*) as Total_Employees FROM NhanVien;
SELECT * FROM NhanVien;

-- ===================================
-- 5. BẢNG KHÁCH HÀNG
-- ===================================
SELECT '=== BẢNG KHACHHANG ===' as Table_Info;
SELECT COUNT(*) as Total_Customers FROM KhachHang;
SELECT * FROM KhachHang;

-- ===================================
-- 6. BẢNG LOẠI SẢN PHẨM
-- ===================================
SELECT '=== BẢNG LOAISANPHAM ===' as Table_Info;
SELECT COUNT(*) as Total_Product_Categories FROM LoaiSanPham;
SELECT * FROM LoaiSanPham;

-- ===================================
-- 7. BẢNG SẢN PHẨM
-- ===================================
SELECT '=== BẢNG SANPHAM ===' as Table_Info;
SELECT COUNT(*) as Total_Products FROM SanPham;
SELECT * FROM SanPham;

-- ===================================
-- 8. BẢNG KHUYẾN MÃI
-- ===================================
SELECT '=== BẢNG KHUYENMAI ===' as Table_Info;
SELECT COUNT(*) as Total_Promotions FROM KhuyenMai;
SELECT * FROM KhuyenMai;

-- ===================================
-- 9. BẢNG PHƯƠNG THỨC THANH TOÁN
-- ===================================
SELECT '=== BẢNG PHUONGTHUCTHANHTOAN ===' as Table_Info;
SELECT COUNT(*) as Total_Payment_Methods FROM PhuongThucThanhToan;
SELECT * FROM PhuongThucThanhToan;

-- ===================================
-- 10. BẢNG KHO
-- ===================================
SELECT '=== BẢNG KHO ===' as Table_Info;
SELECT COUNT(*) as Total_Warehouses FROM Kho;
SELECT * FROM Kho;

-- ===================================
-- 11. BẢNG CA LÀM VIỆC
-- ===================================
SELECT '=== BẢNG CALAMVIEC ===' as Table_Info;
SELECT COUNT(*) as Total_Work_Shifts FROM CaLamViec;
SELECT * FROM CaLamViec;

-- ===================================
-- 12. BẢNG LỊCH LÀM VIỆC
-- ===================================
SELECT '=== BẢNG LICHLAMVIEC ===' as Table_Info;
SELECT COUNT(*) as Total_Work_Schedules FROM LichLamViec;
SELECT * FROM LichLamViec;

-- ===================================
-- 13. BẢNG BẢNG LƯƠNG
-- ===================================
SELECT '=== BẢNG BANGLUONG ===' as Table_Info;
SELECT COUNT(*) as Total_Salary_Records FROM BangLuong;
SELECT * FROM BangLuong;

-- ===================================
-- 14. BẢNG HÌNH ẢNH
-- ===================================
SELECT '=== BẢNG HINHANH ===' as Table_Info;
SELECT COUNT(*) as Total_Images FROM HinhAnh;
SELECT * FROM HinhAnh LIMIT 10;

-- ===================================
-- 15. BẢNG GIÁ SẢN PHẨM
-- ===================================
SELECT '=== BẢNG GIASANPHAM ===' as Table_Info;
SELECT COUNT(*) as Total_Price_Records FROM GiaSanPham;
SELECT * FROM GiaSanPham;

-- ===================================
-- 16. BẢNG TỒN KHO CHI TIẾT
-- ===================================
SELECT '=== BẢNG TONKHOCHITIET ===' as Table_Info;
SELECT COUNT(*) as Total_Inventory_Records FROM TonKhoChiTiet;
SELECT * FROM TonKhoChiTiet;

-- ===================================
-- 17. BẢNG PHIẾU NHẬP HÀNG
-- ===================================
SELECT '=== BẢNG PHIEUNHAPHANG ===' as Table_Info;
SELECT COUNT(*) as Total_Import_Orders FROM PhieuNhapHang;
SELECT * FROM PhieuNhapHang;

-- ===================================
-- 18. BẢNG CHI TIẾT PHIẾU NHẬP
-- ===================================
SELECT '=== BẢNG CHITIETPHIEUNHAP ===' as Table_Info;
SELECT COUNT(*) as Total_Import_Details FROM ChiTietPhieuNhap;
SELECT * FROM ChiTietPhieuNhap;

-- ===================================
-- 19. BẢNG PHIẾU XUẤT KHO
-- ===================================
SELECT '=== BẢNG PHIEUXUATKHO ===' as Table_Info;
SELECT COUNT(*) as Total_Export_Orders FROM PhieuXuatKho;
SELECT * FROM PhieuXuatKho;

-- ===================================
-- 20. BẢNG CHI TIẾT PHIẾU XUẤT
-- ===================================
SELECT '=== BẢNG CHITIETPHIEUXUAT ===' as Table_Info;
SELECT COUNT(*) as Total_Export_Details FROM ChiTietPhieuXuat;
SELECT * FROM ChiTietPhieuXuat;

-- ===================================
-- 21. BẢNG HÓA ĐƠN
-- ===================================
SELECT '=== BẢNG HOADON ===' as Table_Info;
SELECT COUNT(*) as Total_Invoices FROM HoaDon;
SELECT * FROM HoaDon;

-- ===================================
-- 22. BẢNG CHI TIẾT HÓA ĐƠN
-- ===================================
SELECT '=== BẢNG CHITIETHOADON ===' as Table_Info;
SELECT COUNT(*) as Total_Invoice_Details FROM ChiTietHoaDon;
SELECT * FROM ChiTietHoaDon;

-- ===================================
-- 23. BẢNG KHUYẾN MÃI SẢN PHẨM
-- ===================================
SELECT '=== BẢNG KHUYENMAISANPHAM ===' as Table_Info;
SELECT COUNT(*) as Total_Product_Promotions FROM KhuyenMaiSanPham;
SELECT * FROM KhuyenMaiSanPham;

-- ===================================
-- 24. BẢNG KHUYẾN MÃI KHÁCH HÀNG
-- ===================================
SELECT '=== BẢNG KHUYENMAIKHACHHANG ===' as Table_Info;
SELECT COUNT(*) as Total_Customer_Promotions FROM KhuyenMaiKhachHang;
SELECT * FROM KhuyenMaiKhachHang;

-- ===================================
-- 25. BẢNG THANH TOÁN
-- ===================================
SELECT '=== BẢNG THANHTOAN ===' as Table_Info;
SELECT COUNT(*) as Total_Payments FROM ThanhToan;
SELECT * FROM ThanhToan;

-- ===================================
-- 26. BẢNG GIỎ HÀNG
-- ===================================
SELECT '=== BẢNG GIOHANG ===' as Table_Info;
SELECT COUNT(*) as Total_Shopping_Carts FROM GioHang;
SELECT * FROM GioHang;

-- ===================================
-- 27. BẢNG CHI TIẾT GIỎ HÀNG
-- ===================================
SELECT '=== BẢNG CHITIETGIOHANG ===' as Table_Info;
SELECT COUNT(*) as Total_Cart_Items FROM ChiTietGioHang;
SELECT * FROM ChiTietGioHang;

-- ===================================
-- 28. BẢNG THỐNG KÊ BÁO CÁO
-- ===================================
SELECT '=== BẢNG THONGKEBAOCAO ===' as Table_Info;
SELECT COUNT(*) as Total_Reports FROM ThongKeBaoCao;
SELECT * FROM ThongKeBaoCao;

-- ===================================
-- TỔNG KẾT SỐ LƯỢNG BẢNG VÀ DỮ LIỆU
-- ===================================
SELECT '=== TỔNG KẾT ===' as Summary;

SELECT 
    'NguoiDung' as Table_Name,
    COUNT(*) as Record_Count
FROM NguoiDung
UNION ALL
SELECT 
    'CuaHang' as Table_Name,
    COUNT(*) as Record_Count
FROM CuaHang
UNION ALL
SELECT 
    'NhaCungCap' as Table_Name,
    COUNT(*) as Record_Count
FROM NhaCungCap
UNION ALL
SELECT 
    'NhanVien' as Table_Name,
    COUNT(*) as Record_Count
FROM NhanVien
UNION ALL
SELECT 
    'KhachHang' as Table_Name,
    COUNT(*) as Record_Count
FROM KhachHang
UNION ALL
SELECT 
    'LoaiSanPham' as Table_Name,
    COUNT(*) as Record_Count
FROM LoaiSanPham
UNION ALL
SELECT 
    'SanPham' as Table_Name,
    COUNT(*) as Record_Count
FROM SanPham
UNION ALL
SELECT 
    'KhuyenMai' as Table_Name,
    COUNT(*) as Record_Count
FROM KhuyenMai
UNION ALL
SELECT 
    'PhuongThucThanhToan' as Table_Name,
    COUNT(*) as Record_Count
FROM PhuongThucThanhToan
UNION ALL
SELECT 
    'Kho' as Table_Name,
    COUNT(*) as Record_Count
FROM Kho
UNION ALL
SELECT 
    'CaLamViec' as Table_Name,
    COUNT(*) as Record_Count
FROM CaLamViec
UNION ALL
SELECT 
    'LichLamViec' as Table_Name,
    COUNT(*) as Record_Count
FROM LichLamViec
UNION ALL
SELECT 
    'BangLuong' as Table_Name,
    COUNT(*) as Record_Count
FROM BangLuong
UNION ALL
SELECT 
    'HinhAnh' as Table_Name,
    COUNT(*) as Record_Count
FROM HinhAnh
UNION ALL
SELECT 
    'GiaSanPham' as Table_Name,
    COUNT(*) as Record_Count
FROM GiaSanPham
UNION ALL
SELECT 
    'TonKhoChiTiet' as Table_Name,
    COUNT(*) as Record_Count
FROM TonKhoChiTiet
UNION ALL
SELECT 
    'PhieuNhapHang' as Table_Name,
    COUNT(*) as Record_Count
FROM PhieuNhapHang
UNION ALL
SELECT 
    'ChiTietPhieuNhap' as Table_Name,
    COUNT(*) as Record_Count
FROM ChiTietPhieuNhap
UNION ALL
SELECT 
    'PhieuXuatKho' as Table_Name,
    COUNT(*) as Record_Count
FROM PhieuXuatKho
UNION ALL
SELECT 
    'ChiTietPhieuXuat' as Table_Name,
    COUNT(*) as Record_Count
FROM ChiTietPhieuXuat
UNION ALL
SELECT 
    'HoaDon' as Table_Name,
    COUNT(*) as Record_Count
FROM HoaDon
UNION ALL
SELECT 
    'ChiTietHoaDon' as Table_Name,
    COUNT(*) as Record_Count
FROM ChiTietHoaDon
UNION ALL
SELECT 
    'KhuyenMaiSanPham' as Table_Name,
    COUNT(*) as Record_Count
FROM KhuyenMaiSanPham
UNION ALL
SELECT 
    'KhuyenMaiKhachHang' as Table_Name,
    COUNT(*) as Record_Count
FROM KhuyenMaiKhachHang
UNION ALL
SELECT 
    'ThanhToan' as Table_Name,
    COUNT(*) as Record_Count
FROM ThanhToan
UNION ALL
SELECT 
    'GioHang' as Table_Name,
    COUNT(*) as Record_Count
FROM GioHang
UNION ALL
SELECT 
    'ChiTietGioHang' as Table_Name,
    COUNT(*) as Record_Count
FROM ChiTietGioHang
UNION ALL
SELECT 
    'ThongKeBaoCao' as Table_Name,
    COUNT(*) as Record_Count
FROM ThongKeBaoCao
ORDER BY Table_Name; 