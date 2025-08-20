package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.ChiTietHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChiTietHoaDonRepository extends JpaRepository<ChiTietHoaDon, Integer> {
    
    // Tìm tất cả chi tiết hóa đơn chưa bị xóa (isDeleted = false)
    @Query("SELECT c FROM ChiTietHoaDon c WHERE c.isDeleted = false")
    List<ChiTietHoaDon> findAllActive();
    
    // Tìm chi tiết hóa đơn theo ID và chưa bị xóa
    @Query("SELECT c FROM ChiTietHoaDon c WHERE c.maCTHD = :id AND c.isDeleted = false")
    Optional<ChiTietHoaDon> findActiveById(@Param("id") Integer id);
    
    // Tìm chi tiết hóa đơn theo ID (bao gồm cả đã xóa)
    @Query("SELECT c FROM ChiTietHoaDon c WHERE c.maCTHD = :id")
    Optional<ChiTietHoaDon> findByIdIncludeDeleted(@Param("id") Integer id);
    
    // Thống kê sản phẩm bán chạy
    @Query("SELECT " +
           "sp.maSP as maSP, " +
           "sp.tenSP as tenSP, " +
           "COALESCE(lsp.tenLoai, 'Chưa phân loại') as tenLoaiSP, " +
           "SUM(c.soLuong) as soLuongBan, " +
           "SUM(c.soLuong * c.donGiaBan) as doanhThu, " +
           "COUNT(DISTINCT c.hoaDon.maHD) as soLanBan, " +
           "AVG(c.soLuong * c.donGiaBan) as giaTriTrungBinh " +
           "FROM ChiTietHoaDon c " +
           "JOIN c.sanPham sp " +
           "LEFT JOIN sp.loaiSanPham lsp " +
           "JOIN c.hoaDon h " +
           "WHERE h.ngayLap BETWEEN :tuNgay AND :denNgay " +
           "AND c.isDeleted = false " +
           "AND h.isDeleted = false " +
           "AND h.trangThai = 1 " +
           "AND sp.trangThai = 1 " +
           "AND (:maCH IS NULL OR h.nhanVienLap.cuaHang.maCH = :maCH) " +
           "GROUP BY sp.maSP, sp.tenSP, lsp.tenLoai " +
           "ORDER BY doanhThu DESC " +
           "LIMIT :limit")
    List<Object[]> thongKeSanPhamBanChay(@Param("tuNgay") LocalDateTime tuNgay,
                                         @Param("denNgay") LocalDateTime denNgay,
                                         @Param("maCH") String maCH,
                                         @Param("limit") int limit);
    
    // Tính tổng số lượng sản phẩm bán
    @Query("SELECT COALESCE(SUM(c.soLuong), 0) FROM ChiTietHoaDon c " +
           "JOIN c.hoaDon h " +
           "WHERE h.ngayLap BETWEEN :tuNgay AND :denNgay " +
           "AND c.isDeleted = false " +
           "AND h.isDeleted = false " +
           "AND h.trangThai = 1 " +
           "AND (:maCH IS NULL OR h.nhanVienLap.cuaHang.maCH = :maCH)")
    Long sumSoLuongSanPhamBan(@Param("tuNgay") LocalDateTime tuNgay,
                              @Param("denNgay") LocalDateTime denNgay,
                              @Param("maCH") String maCH);
    
    // Đếm số lượng sản phẩm khác nhau được bán
    @Query("SELECT COUNT(DISTINCT c.sanPham.maSP) FROM ChiTietHoaDon c " +
           "JOIN c.hoaDon h " +
           "WHERE h.ngayLap BETWEEN :tuNgay AND :denNgay " +
           "AND c.isDeleted = false " +
           "AND h.isDeleted = false " +
           "AND h.trangThai = 1 " +
           "AND (:maCH IS NULL OR h.nhanVienLap.cuaHang.maCH = :maCH)")
    Long countDistinctSanPham(@Param("tuNgay") LocalDateTime tuNgay,
                              @Param("denNgay") LocalDateTime denNgay,
                              @Param("maCH") String maCH);
    
    // Đếm tổng số sản phẩm đã bán (alias method cho compatibility)
    @Query("SELECT COUNT(DISTINCT c.sanPham.maSP) FROM ChiTietHoaDon c " +
           "JOIN c.hoaDon h " +
           "WHERE h.ngayLap BETWEEN :tuNgay AND :denNgay " +
           "AND c.isDeleted = false " +
           "AND h.isDeleted = false " +
           "AND h.trangThai = 1 " +
           "AND (:maCH IS NULL OR h.nhanVienLap.cuaHang.maCH = :maCH)")
    Integer demTongSoSanPhamDaBan(@Param("tuNgay") LocalDateTime tuNgay,
                                  @Param("denNgay") LocalDateTime denNgay,
                                  @Param("maCH") String maCH);
    
    // Tìm chi tiết hóa đơn theo khoảng thời gian
    @Query("SELECT c FROM ChiTietHoaDon c " +
           "JOIN c.hoaDon h " +
           "WHERE h.ngayLap BETWEEN :tuNgay AND :denNgay " +
           "AND c.isDeleted = false " +
           "AND h.isDeleted = false " +
           "AND h.trangThai = 1 " +
           "AND (:maCH IS NULL OR h.nhanVienLap.cuaHang.maCH = :maCH) " +
           "ORDER BY h.ngayLap DESC, c.maCTHD DESC")
    List<ChiTietHoaDon> findByDateRangeAndStore(@Param("tuNgay") LocalDateTime tuNgay,
                                                @Param("denNgay") LocalDateTime denNgay,
                                                @Param("maCH") String maCH);
}