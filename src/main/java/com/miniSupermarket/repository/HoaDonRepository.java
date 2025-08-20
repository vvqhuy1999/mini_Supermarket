package com.miniSupermarket.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.mini_supermarket.entity.HoaDon;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    
    // ===================================
    // BASIC CRUD OPERATIONS
    // ===================================
    
    // Tìm tất cả hóa đơn chưa bị xóa (isDeleted = false)
    @Query("SELECT h FROM HoaDon h WHERE h.isDeleted = false")
    List<HoaDon> findAllActive();
    
    // Tìm hóa đơn theo ID và chưa bị xóa
    @Query("SELECT h FROM HoaDon h WHERE h.maHD = :id AND h.isDeleted = false")
    Optional<HoaDon> findActiveById(@Param("id") Integer id);
    
    // Tìm hóa đơn theo ID (bao gồm cả đã xóa)
    @Query("SELECT h FROM HoaDon h WHERE h.maHD = :id")
    Optional<HoaDon> findByIdIncludeDeleted(@Param("id") Integer id);
    
    // ===================================
    // SEARCH BY DATE AND STATUS
    // ===================================
    
    // Tìm hóa đơn theo khoảng thời gian và trạng thái (cho báo cáo doanh thu)
    @Query("SELECT h FROM HoaDon h WHERE h.ngayLap BETWEEN :startDate AND :endDate " +
           "AND h.trangThai = :trangThai AND h.isDeleted = false")
    List<HoaDon> findByNgayLapBetweenAndTrangThai(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate, 
        @Param("trangThai") Integer trangThai
    );
    
    // Tìm hóa đơn theo khoảng thời gian (tất cả trạng thái hoàn thành)
    @Query("SELECT h FROM HoaDon h WHERE h.ngayLap BETWEEN :startDate AND :endDate " +
           "AND h.trangThai = 1 AND h.isDeleted = false")
    List<HoaDon> findByDateRangeCompleted(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    // ===================================
    // SEARCH BY CUSTOMER
    // ===================================
    
    // Tìm hóa đơn theo khách hàng trong khoảng thời gian
    @Query("SELECT h FROM HoaDon h WHERE h.maKH = :maKH " +
           "AND h.ngayLap BETWEEN :startDate AND :endDate " +
           "AND h.trangThai = :trangThai AND h.isDeleted = false")
    List<HoaDon> findByCustomerAndDateRange(
        @Param("maKH") String maKH,
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate, 
        @Param("trangThai") Integer trangThai
    );
    
    // ===================================
    // SEARCH BY EMPLOYEE
    // ===================================
    
    // Tìm hóa đơn theo nhân viên trong khoảng thời gian
    @Query("SELECT h FROM HoaDon h WHERE h.maNVLap = :maNV " +
           "AND h.ngayLap BETWEEN :startDate AND :endDate " +
           "AND h.isDeleted = false")
    List<HoaDon> findByEmployeeAndDateRange(
        @Param("maNV") String maNV,
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    // ===================================
    // REVENUE STATISTICS (JPQL)
    // ===================================
    
    // Tổng doanh thu theo khoảng thời gian
    @Query("SELECT COALESCE(SUM(h.tongTien), 0) FROM HoaDon h " +
           "WHERE h.ngayLap BETWEEN :startDate AND :endDate " +
           "AND h.trangThai = 1 AND h.isDeleted = false")
    BigDecimal getTongDoanhThuByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Đếm số hóa đơn theo khoảng thời gian
    @Query("SELECT COUNT(h) FROM HoaDon h " +
           "WHERE h.ngayLap BETWEEN :startDate AND :endDate " +
           "AND h.trangThai = 1 AND h.isDeleted = false")
    Long countHoaDonByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Doanh thu trung bình theo khoảng thời gian
    @Query("SELECT COALESCE(AVG(h.tongTien), 0) FROM HoaDon h " +
           "WHERE h.ngayLap BETWEEN :startDate AND :endDate " +
           "AND h.trangThai = 1 AND h.isDeleted = false")
    BigDecimal getDoanhThuTrungBinhByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Đếm số khách hàng duy nhất trong khoảng thời gian
    @Query("SELECT COUNT(DISTINCT h.maKH) FROM HoaDon h " +
           "WHERE h.ngayLap BETWEEN :startDate AND :endDate " +
           "AND h.trangThai = 1 AND h.isDeleted = false " +
           "AND h.maKH IS NOT NULL")
    Long countDistinctCustomers(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Tìm hóa đơn có giá trị cao nhất trong khoảng thời gian
    @Query("SELECT h FROM HoaDon h " +
           "WHERE h.ngayLap BETWEEN :startDate AND :endDate " +
           "AND h.trangThai = 1 AND h.isDeleted = false " +
           "ORDER BY h.tongTien DESC")
    List<HoaDon> findTopRevenueInvoices(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // ===================================
    // COMPREHENSIVE STATISTICS - THÊM MỚI
    // ===================================
    
    // Thống kê tổng quan - trả về tất cả metrics trong 1 query
    @Query(value = "SELECT " +
           "COALESCE(SUM(h.TongTien), 0) as tongDoanhThu, " +
           "COUNT(h.MaHD) as tongSoHoaDon, " +
           "COUNT(DISTINCT h.MaKH) as tongSoKhachHang, " +
           "COALESCE(AVG(h.TongTien), 0) as doanhThuTrungBinh, " +
           "CASE WHEN COUNT(h.MaHD) > 0 " +
           "     THEN COALESCE(SUM(h.TongTien), 0) / COUNT(h.MaHD) " +
           "     ELSE 0 END as hoaDonTrungBinh " +
           "FROM HoaDon h " +
           "WHERE h.NgayLap BETWEEN ?1 AND ?2 " +
           "AND h.IsDeleted = 0 AND h.TrangThai = 1 " +
           "AND (?3 IS NULL OR h.MaNVLap IN " +
           "    (SELECT nv.MaNV FROM NhanVien nv WHERE nv.MaCH = ?3))",
           nativeQuery = true)
    List<Object[]> thongKeDoanhThuTongQuan(
        LocalDateTime tuNgay,
        LocalDateTime denNgay,
        String maCH
    );
    
    // ===================================
    // CUSTOMER STATISTICS - NATIVE QUERY
    // ===================================
    
    // Thống kê khách hàng tiềm năng - không có LIMIT
    @Query(value = "SELECT h.MaKH as maKH, " +
           "COALESCE(kh.TenKH, kh.Ten, kh.HoTen, 'Khách lẻ') as tenKH, " +
           "COUNT(h.MaHD) as soLanMua, " +
           "SUM(h.TongTien) as tongChiTieu, " +
           "AVG(h.TongTien) as chiTieuTrungBinh, " +
           "MAX(h.NgayLap) as lanMuaCuoi " +
           "FROM HoaDon h " +
           "LEFT JOIN KhachHang kh ON h.MaKH = kh.MaKH " +
           "WHERE h.NgayLap BETWEEN ?1 AND ?2 " +
           "AND h.IsDeleted = 0 AND h.TrangThai = 1 " +
           "AND h.MaKH IS NOT NULL " +
           "AND (?3 IS NULL OR h.MaNVLap IN " +
           "    (SELECT nv.MaNV FROM NhanVien nv WHERE nv.MaCH = ?3)) " +
           "GROUP BY h.MaKH, kh.TenKH, kh.Ten, kh.HoTen " +
           "ORDER BY tongChiTieu DESC", 
           nativeQuery = true)
    List<Object[]> thongKeKhachHangTiemNang(
        LocalDateTime tuNgay,
        LocalDateTime denNgay,
        String maCH
    );
    
    // Thống kê khách hàng tăng trưởng - không có LIMIT
    @Query(value = "SELECT cp.MaKH as maKH, " +
           "COALESCE(kh.TenKH, kh.Ten, kh.HoTen, 'Khách lẻ') as tenKH, " +
           "cp.current_orders as soLanMua, " +
           "cp.current_revenue as tongChiTieu, " +
           "COALESCE(pp.prev_revenue, 0) as doanhThuKyTruoc, " +
           "CASE WHEN COALESCE(pp.prev_revenue, 0) > 0 " +
           "     THEN ROUND(((cp.current_revenue - COALESCE(pp.prev_revenue, 0)) * 100.0 / pp.prev_revenue), 2) " +
           "     ELSE 100.0 END as tyLeTangTruong " +
           "FROM ( " +
           "    SELECT h.MaKH, COUNT(h.MaHD) as current_orders, " +
           "           SUM(h.TongTien) as current_revenue " +
           "    FROM HoaDon h " +
           "    WHERE h.NgayLap BETWEEN ?1 AND ?2 " +
           "    AND h.IsDeleted = 0 AND h.TrangThai = 1 " +
           "    AND h.MaKH IS NOT NULL " +
           "    GROUP BY h.MaKH " +
           ") cp " +
           "LEFT JOIN ( " +
           "    SELECT h.MaKH, COUNT(h.MaHD) as prev_orders, " +
           "           SUM(h.TongTien) as prev_revenue " +
           "    FROM HoaDon h " +
           "    WHERE h.NgayLap BETWEEN ?3 AND ?4 " +
           "    AND h.IsDeleted = 0 AND h.TrangThai = 1 " +
           "    AND h.MaKH IS NOT NULL " +
           "    GROUP BY h.MaKH " +
           ") pp ON cp.MaKH = pp.MaKH " +
           "LEFT JOIN KhachHang kh ON cp.MaKH = kh.MaKH " +
           "WHERE cp.current_revenue > COALESCE(pp.prev_revenue, 0) " +
           "ORDER BY tyLeTangTruong DESC",
           nativeQuery = true)
    List<Object[]> thongKeKhachHangTangTruong(
        LocalDateTime tuNgay,
        LocalDateTime denNgay,
        LocalDateTime tuNgayTruoc,
        LocalDateTime denNgayTruoc
    );
    
    // ===================================
    // PRODUCT STATISTICS - NATIVE QUERY
    // ===================================
    
    // Thống kê sản phẩm bán chạy - không có LIMIT
    @Query(value = "SELECT sp.MaSP as maSP, " +
           "sp.TenSP as tenSP, " +
           "sp.MaLoaiSP as maLoaiSP, " +
           "SUM(ct.SoLuong) as soLuongBan, " +
           "SUM(ct.SoLuong * ct.DonGia) as doanhThu, " +
           "COUNT(DISTINCT ct.MaHD) as soHoaDon, " +
           "AVG(ct.DonGia) as giaTriTrungBinh " +
           "FROM ChiTietHoaDon ct " +
           "INNER JOIN HoaDon h ON ct.MaHD = h.MaHD " +
           "INNER JOIN SanPham sp ON ct.MaSP = sp.MaSP " +
           "WHERE h.NgayLap BETWEEN ?1 AND ?2 " +
           "AND h.IsDeleted = 0 AND h.TrangThai = 1 " +
           "AND (?3 IS NULL OR h.MaNVLap IN " +
           "    (SELECT nv.MaNV FROM NhanVien nv WHERE nv.MaCH = ?3)) " +
           "GROUP BY sp.MaSP, sp.TenSP, sp.MaLoaiSP " +
           "ORDER BY soLuongBan DESC",
           nativeQuery = true)
    List<Object[]> thongKeSanPhamBanChay(
        LocalDateTime tuNgay,
        LocalDateTime denNgay,
        String maCH
    );
    
    // Thống kê sản phẩm tăng trưởng - không có LIMIT
    @Query(value = "SELECT sp.MaSP as maSP, " +
           "sp.TenSP as tenSP, " +
           "cp.current_qty as soLuongBan, " +
           "cp.current_revenue as doanhThu, " +
           "COALESCE(pp.prev_revenue, 0) as doanhThuKyTruoc, " +
           "CASE WHEN COALESCE(pp.prev_revenue, 0) > 0 " +
           "     THEN ROUND(((cp.current_revenue - COALESCE(pp.prev_revenue, 0)) * 100.0 / pp.prev_revenue), 2) " +
           "     ELSE 100.0 END as tyLeTangTruong " +
           "FROM ( " +
           "    SELECT ct.MaSP, SUM(ct.SoLuong) as current_qty, " +
           "           SUM(ct.SoLuong * ct.DonGia) as current_revenue " +
           "    FROM ChiTietHoaDon ct " +
           "    INNER JOIN HoaDon h ON ct.MaHD = h.MaHD " +
           "    WHERE h.NgayLap BETWEEN ?1 AND ?2 " +
           "    AND h.IsDeleted = 0 AND h.TrangThai = 1 " +
           "    GROUP BY ct.MaSP " +
           ") cp " +
           "LEFT JOIN ( " +
           "    SELECT ct.MaSP, SUM(ct.SoLuong) as prev_qty, " +
           "           SUM(ct.SoLuong * ct.DonGia) as prev_revenue " +
           "    FROM ChiTietHoaDon ct " +
           "    INNER JOIN HoaDon h ON ct.MaHD = h.MaHD " +
           "    WHERE h.NgayLap BETWEEN ?3 AND ?4 " +
           "    AND h.IsDeleted = 0 AND h.TrangThai = 1 " +
           "    GROUP BY ct.MaSP " +
           ") pp ON cp.MaSP = pp.MaSP " +
           "INNER JOIN SanPham sp ON cp.MaSP = sp.MaSP " +
           "WHERE cp.current_revenue > COALESCE(pp.prev_revenue, 0) " +
           "ORDER BY tyLeTangTruong DESC",
           nativeQuery = true)
    List<Object[]> thongKeSanPhamTangTruong(
        LocalDateTime tuNgay,
        LocalDateTime denNgay,
        LocalDateTime tuNgayTruoc,
        LocalDateTime denNgayTruoc
    );
    
    // ===================================
    // CATEGORY STATISTICS
    // ===================================
    
    // Thống kê theo loại sản phẩm
    @Query(value = "SELECT lsp.MaLoaiSP as maLoaiSP, " +
           "lsp.TenLoaiSP as tenLoaiSP, " +
           "SUM(ct.SoLuong) as soLuongBan, " +
           "SUM(ct.SoLuong * ct.DonGia) as doanhThu, " +
           "COUNT(DISTINCT ct.MaHD) as soHoaDon, " +
           "COUNT(DISTINCT ct.MaSP) as soSanPham " +
           "FROM ChiTietHoaDon ct " +
           "INNER JOIN HoaDon h ON ct.MaHD = h.MaHD " +
           "INNER JOIN SanPham sp ON ct.MaSP = sp.MaSP " +
           "INNER JOIN LoaiSanPham lsp ON sp.MaLoaiSP = lsp.MaLoaiSP " +
           "WHERE h.NgayLap BETWEEN ?1 AND ?2 " +
           "AND h.IsDeleted = 0 AND h.TrangThai = 1 " +
           "GROUP BY lsp.MaLoaiSP, lsp.TenLoaiSP " +
           "ORDER BY doanhThu DESC",
           nativeQuery = true)
    List<Object[]> thongKeLoaiSanPham(
        LocalDateTime tuNgay,
        LocalDateTime denNgay
    );
    
    // ===================================
    // STORE BRANCH STATISTICS
    // ===================================
    
    // Thống kê theo cửa hàng
    @Query(value = "SELECT ch.MaCH as maCH, " +
           "ch.TenCH as tenCH, " +
           "COUNT(h.MaHD) as soHoaDon, " +
           "SUM(h.TongTien) as tongDoanhThu, " +
           "AVG(h.TongTien) as doanhThuTrungBinh, " +
           "COUNT(DISTINCT h.MaKH) as soKhachHang " +
           "FROM HoaDon h " +
           "INNER JOIN NhanVien nv ON h.MaNVLap = nv.MaNV " +
           "INNER JOIN CuaHang ch ON nv.MaCH = ch.MaCH " +
           "WHERE h.NgayLap BETWEEN ?1 AND ?2 " +
           "AND h.IsDeleted = 0 AND h.TrangThai = 1 " +
           "GROUP BY ch.MaCH, ch.TenCH " +
           "ORDER BY tongDoanhThu DESC",
           nativeQuery = true)
    List<Object[]> thongKeTheoCuaHang(
        LocalDateTime tuNgay,
        LocalDateTime denNgay
    );
    
    // ===================================
    // DAILY/MONTHLY TREND ANALYSIS
    // ===================================
    
    // Thống kê doanh thu theo ngày
    @Query(value = "SELECT DATE(h.NgayLap) as ngay, " +
           "COUNT(h.MaHD) as soHoaDon, " +
           "SUM(h.TongTien) as tongDoanhThu, " +
           "AVG(h.TongTien) as doanhThuTrungBinh, " +
           "COUNT(DISTINCT h.MaKH) as soKhachHang " +
           "FROM HoaDon h " +
           "WHERE h.NgayLap BETWEEN ?1 AND ?2 " +
           "AND h.IsDeleted = 0 AND h.TrangThai = 1 " +
           "GROUP BY DATE(h.NgayLap) " +
           "ORDER BY ngay",
           nativeQuery = true)
    List<Object[]> thongKeDoanhThuTheoNgay(
        LocalDateTime tuNgay,
        LocalDateTime denNgay
    );
    
    // Thống kê doanh thu theo tháng
    @Query(value = "SELECT YEAR(h.NgayLap) as nam, MONTH(h.NgayLap) as thang, " +
           "COUNT(h.MaHD) as soHoaDon, " +
           "SUM(h.TongTien) as tongDoanhThu, " +
           "AVG(h.TongTien) as doanhThuTrungBinh, " +
           "COUNT(DISTINCT h.MaKH) as soKhachHang " +
           "FROM HoaDon h " +
           "WHERE h.NgayLap BETWEEN ?1 AND ?2 " +
           "AND h.IsDeleted = 0 AND h.TrangThai = 1 " +
           "GROUP BY YEAR(h.NgayLap), MONTH(h.NgayLap) " +
           "ORDER BY nam, thang",
           nativeQuery = true)
    List<Object[]> thongKeDoanhThuTheoThang(
        LocalDateTime tuNgay,
        LocalDateTime denNgay
    );
    
    // ===================================
    // UTILITY QUERIES
    // ===================================
    
    // Đếm số sản phẩm duy nhất được bán trong khoảng thời gian
    @Query(value = "SELECT COUNT(DISTINCT ct.MaSP) " +
           "FROM ChiTietHoaDon ct " +
           "INNER JOIN HoaDon h ON ct.MaHD = h.MaHD " +
           "WHERE h.NgayLap BETWEEN ?1 AND ?2 " +
           "AND h.IsDeleted = 0 AND h.TrangThai = 1",
           nativeQuery = true)
    Long countDistinctProducts(
        LocalDateTime tuNgay,
        LocalDateTime denNgay
    );
    
    // ===================================
    // ALTERNATIVE METHODS WITH HARDCODED LIMITS
    // ===================================
    
    // Thống kê top 10 khách hàng tiềm năng
    @Query(value = "SELECT h.MaKH as maKH, " +
           "COALESCE(kh.TenKH, kh.Ten, kh.HoTen, 'Khách lẻ') as tenKH, " +
           "COUNT(h.MaHD) as soLanMua, " +
           "SUM(h.TongTien) as tongChiTieu, " +
           "AVG(h.TongTien) as chiTieuTrungBinh, " +
           "MAX(h.NgayLap) as lanMuaCuoi " +
           "FROM HoaDon h " +
           "LEFT JOIN KhachHang kh ON h.MaKH = kh.MaKH " +
           "WHERE h.NgayLap BETWEEN ?1 AND ?2 " +
           "AND h.IsDeleted = 0 AND h.TrangThai = 1 " +
           "AND h.MaKH IS NOT NULL " +
           "AND (?3 IS NULL OR h.MaNVLap IN " +
           "    (SELECT nv.MaNV FROM NhanVien nv WHERE nv.MaCH = ?3)) " +
           "GROUP BY h.MaKH, kh.TenKH, kh.Ten, kh.HoTen " +
           "ORDER BY tongChiTieu DESC " +
           "LIMIT 10", 
           nativeQuery = true)
    List<Object[]> thongKeTop10KhachHang(
        LocalDateTime tuNgay,
        LocalDateTime denNgay,
        String maCH
    );
    
    // Thống kê top 10 sản phẩm bán chạy
    @Query(value = "SELECT sp.MaSP as maSP, " +
           "sp.TenSP as tenSP, " +
           "sp.MaLoaiSP as maLoaiSP, " +
           "SUM(ct.SoLuong) as soLuongBan, " +
           "SUM(ct.SoLuong * ct.DonGia) as doanhThu, " +
           "COUNT(DISTINCT ct.MaHD) as soHoaDon, " +
           "AVG(ct.DonGia) as giaTriTrungBinh " +
           "FROM ChiTietHoaDon ct " +
           "INNER JOIN HoaDon h ON ct.MaHD = h.MaHD " +
           "INNER JOIN SanPham sp ON ct.MaSP = sp.MaSP " +
           "WHERE h.NgayLap BETWEEN ?1 AND ?2 " +
           "AND h.IsDeleted = 0 AND h.TrangThai = 1 " +
           "AND (?3 IS NULL OR h.MaNVLap IN " +
           "    (SELECT nv.MaNV FROM NhanVien nv WHERE nv.MaCH = ?3)) " +
           "GROUP BY sp.MaSP, sp.TenSP, sp.MaLoaiSP " +
           "ORDER BY soLuongBan DESC " +
           "LIMIT 10",
           nativeQuery = true)
    List<Object[]> thongKeTop10SanPham(
        LocalDateTime tuNgay,
        LocalDateTime denNgay,
        String maCH
    );
}