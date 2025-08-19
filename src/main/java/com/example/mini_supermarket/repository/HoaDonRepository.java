package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    
    // Tìm tất cả hóa đơn chưa bị xóa (isDeleted = false)
    @Query("SELECT h FROM HoaDon h WHERE h.isDeleted = false")
    List<HoaDon> findAllActive();
    
    // Tìm hóa đơn theo ID và chưa bị xóa
    @Query("SELECT h FROM HoaDon h WHERE h.maHD = :id AND h.isDeleted = false")
    Optional<HoaDon> findActiveById(@Param("id") Integer id);
    
    // Tìm hóa đơn theo ID (bao gồm cả đã xóa)
    @Query("SELECT h FROM HoaDon h WHERE h.maHD = :id")
    Optional<HoaDon> findByIdIncludeDeleted(@Param("id") Integer id);
    
    // Thống kê doanh thu tổng quan
    @Query("SELECT " +
           "COALESCE(SUM(h.tongTien), 0) as tongDoanhThu, " +
           "COUNT(h) as soLuongHoaDon, " +
           "COUNT(DISTINCT h.khachHang.maKH) as soLuongKhachHang " +
           "FROM HoaDon h " +
           "WHERE h.ngayLap BETWEEN :tuNgay AND :denNgay " +
           "AND h.isDeleted = false " +
           "AND h.trangThai = 1 " +
           "AND (:maCH IS NULL OR h.nhanVienLap.cuaHang.maCH = :maCH)")
    List<Object[]> thongKeDoanhThuTongQuan(@Param("tuNgay") LocalDateTime tuNgay, 
                                           @Param("denNgay") LocalDateTime denNgay, 
                                           @Param("maCH") String maCH);
    
    // Tìm hóa đơn theo khoảng thời gian
    @Query("SELECT h FROM HoaDon h " +
           "WHERE h.ngayLap BETWEEN :tuNgay AND :denNgay " +
           "AND h.isDeleted = false " +
           "AND h.trangThai = 1 " +
           "AND (:maCH IS NULL OR h.nhanVienLap.cuaHang.maCH = :maCH) " +
           "ORDER BY h.ngayLap DESC")
    List<HoaDon> findByDateRangeAndStore(@Param("tuNgay") LocalDateTime tuNgay,
                                         @Param("denNgay") LocalDateTime denNgay,
                                         @Param("maCH") String maCH);
    
    // Thống kê khách hàng tiềm năng
    @Query("SELECT " +
           "h.khachHang.maKH as maKH, " +
           "h.khachHang.tenKH as tenKH, " +
           "COUNT(h) as soLanMua, " +
           "SUM(h.tongTien) as tongChiTieu, " +
           "AVG(h.tongTien) as chiTieuTrungBinh, " +
           "MAX(h.ngayLap) as lanMuaCuoi " +
           "FROM HoaDon h " +
           "WHERE h.ngayLap BETWEEN :tuNgay AND :denNgay " +
           "AND h.isDeleted = false " +
           "AND h.trangThai = 1 " +
           "AND h.khachHang IS NOT NULL " +
           "AND (:maCH IS NULL OR h.nhanVienLap.cuaHang.maCH = :maCH) " +
           "GROUP BY h.khachHang.maKH, h.khachHang.tenKH " +
           "ORDER BY tongChiTieu DESC " +
           "LIMIT :limit")
    List<Object[]> thongKeKhachHangTiemNang(@Param("tuNgay") LocalDateTime tuNgay,
                                            @Param("denNgay") LocalDateTime denNgay,
                                            @Param("maCH") String maCH,
                                            @Param("limit") int limit);
    
    // Tính tổng doanh thu theo khoảng thời gian
    @Query("SELECT COALESCE(SUM(h.tongTien), 0) FROM HoaDon h " +
           "WHERE h.ngayLap BETWEEN :tuNgay AND :denNgay " +
           "AND h.isDeleted = false " +
           "AND h.trangThai = 1 " +
           "AND (:maCH IS NULL OR h.nhanVienLap.cuaHang.maCH = :maCH)")
    BigDecimal sumDoanhThuByDateRange(@Param("tuNgay") LocalDateTime tuNgay,
                                      @Param("denNgay") LocalDateTime denNgay,
                                      @Param("maCH") String maCH);
    
    // Đếm số lượng hóa đơn theo khoảng thời gian
    @Query("SELECT COUNT(h) FROM HoaDon h " +
           "WHERE h.ngayLap BETWEEN :tuNgay AND :denNgay " +
           "AND h.isDeleted = false " +
           "AND h.trangThai = 1 " +
           "AND (:maCH IS NULL OR h.nhanVienLap.cuaHang.maCH = :maCH)")
    Long countByDateRange(@Param("tuNgay") LocalDateTime tuNgay,
                          @Param("denNgay") LocalDateTime denNgay,
                          @Param("maCH") String maCH);
}