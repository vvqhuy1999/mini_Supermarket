package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.BaoCaoDoanhThuHangNgay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BaoCaoDoanhThuHangNgayRepository extends JpaRepository<BaoCaoDoanhThuHangNgay, Integer> {
    
    // ===== QUERY CƠ BẢN =====
    
    // Tìm báo cáo theo ngày
    List<BaoCaoDoanhThuHangNgay> findByNgayBan(LocalDate ngayBan);
    
    // Tìm báo cáo theo ngày và mã cửa hàng
    List<BaoCaoDoanhThuHangNgay> findByNgayBanAndCuaHang_MaCH(LocalDate ngayBan, String maCH);
    
    // Tìm báo cáo theo ngày và mã sản phẩm
    List<BaoCaoDoanhThuHangNgay> findByNgayBanAndSanPham_MaSP(LocalDate ngayBan, String maSP);
    
    // Tìm báo cáo theo khoảng thời gian
    List<BaoCaoDoanhThuHangNgay> findByNgayBanBetween(LocalDate ngayBatDau, LocalDate ngayKetThuc);
    
    // Tìm báo cáo theo mã cửa hàng và khoảng thời gian
    List<BaoCaoDoanhThuHangNgay> findByCuaHang_MaCHAndNgayBanBetween(String maCH, LocalDate ngayBatDau, LocalDate ngayKetThuc);
    
    // Tìm báo cáo theo mã sản phẩm và khoảng thời gian
    List<BaoCaoDoanhThuHangNgay> findBySanPham_MaSPAndNgayBanBetween(String maSP, LocalDate ngayBatDau, LocalDate ngayKetThuc);
    
    // ===== QUERY TỔNG HỢP =====
    
    // Tổng doanh thu theo ngày
    @Query("SELECT SUM(b.doanhThu) FROM BaoCaoDoanhThuHangNgay b WHERE b.ngayBan = :ngayBan")
    Optional<Double> tongDoanhThuTheoNgay(@Param("ngayBan") LocalDate ngayBan);
    
    // Tổng doanh thu theo khoảng thời gian
    @Query("SELECT SUM(b.doanhThu) FROM BaoCaoDoanhThuHangNgay b WHERE b.ngayBan BETWEEN :ngayBatDau AND :ngayKetThuc")
    Optional<Double> tongDoanhThuTheoKhoangThoiGian(@Param("ngayBatDau") LocalDate ngayBatDau, @Param("ngayKetThuc") LocalDate ngayKetThuc);
    
    // Tổng lợi nhuận theo ngày
    @Query("SELECT SUM(b.loiNhuan) FROM BaoCaoDoanhThuHangNgay b WHERE b.ngayBan = :ngayBan")
    Optional<Double> tongLoiNhuanTheoNgay(@Param("ngayBan") LocalDate ngayBan);
    
    // Tổng lợi nhuận theo khoảng thời gian
    @Query("SELECT SUM(b.loiNhuan) FROM BaoCaoDoanhThuHangNgay b WHERE b.ngayBan BETWEEN :ngayBatDau AND :ngayKetThuc")
    Optional<Double> tongLoiNhuanTheoKhoangThoiGian(@Param("ngayBatDau") LocalDate ngayBatDau, @Param("ngayKetThuc") LocalDate ngayKetThuc);
    
    // Top sản phẩm bán chạy theo khoảng thời gian
    @Query("SELECT b.sanPham.maSP, SUM(b.soLuongBan) as tongSoLuong " +
           "FROM BaoCaoDoanhThuHangNgay b " +
           "WHERE b.ngayBan BETWEEN :ngayBatDau AND :ngayKetThuc " +
           "GROUP BY b.sanPham.maSP " +
           "ORDER BY tongSoLuong DESC")
    List<Object[]> topSanPhamBanChay(@Param("ngayBatDau") LocalDate ngayBatDau, @Param("ngayKetThuc") LocalDate ngayKetThuc);
    
    // Top sản phẩm có lợi nhuận cao theo khoảng thời gian
    @Query("SELECT b.sanPham.maSP, SUM(b.loiNhuan) as tongLoiNhuan " +
           "FROM BaoCaoDoanhThuHangNgay b " +
           "WHERE b.ngayBan BETWEEN :ngayBatDau AND :ngayKetThuc " +
           "GROUP BY b.sanPham.maSP " +
           "ORDER BY tongLoiNhuan DESC")
    List<Object[]> topSanPhamLoiNhuanCao(@Param("ngayBatDau") LocalDate ngayBatDau, @Param("ngayKetThuc") LocalDate ngayKetThuc);
    
    // ===== QUERY THEO CỬA HÀNG =====
    
    // Doanh thu theo cửa hàng và khoảng thời gian
    @Query("SELECT b.cuaHang.maCH, SUM(b.doanhThu) as tongDoanhThu " +
           "FROM BaoCaoDoanhThuHangNgay b " +
           "WHERE b.ngayBan BETWEEN :ngayBatDau AND :ngayKetThuc " +
           "GROUP BY b.cuaHang.maCH " +
           "ORDER BY tongDoanhThu DESC")
    List<Object[]> doanhThuTheoCuaHang(@Param("ngayBatDau") LocalDate ngayBatDau, @Param("ngayKetThuc") LocalDate ngayKetThuc);
    
    // Lợi nhuận theo cửa hàng và khoảng thời gian
    @Query("SELECT b.cuaHang.maCH, SUM(b.loiNhuan) as tongLoiNhuan " +
           "FROM BaoCaoDoanhThuHangNgay b " +
           "WHERE b.ngayBan BETWEEN :ngayBatDau AND :ngayKetThuc " +
           "GROUP BY b.cuaHang.maCH " +
           "ORDER BY tongLoiNhuan DESC")
    List<Object[]> loiNhuanTheoCuaHang(@Param("ngayBatDau") LocalDate ngayBatDau, @Param("ngayKetThuc") LocalDate ngayKetThuc);
}
