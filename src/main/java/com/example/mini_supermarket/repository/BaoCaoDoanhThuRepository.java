package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.BaoCaoDoanhThu;
import com.example.mini_supermarket.entity.HoaDon;
import com.example.mini_supermarket.entity.PhieuNhapHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BaoCaoDoanhThuRepository extends JpaRepository<BaoCaoDoanhThu, String> {
    
    // ===================================
    // BASIC QUERIES
    // ===================================
    
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.isDeleted = false ORDER BY b.ngayBaoCao DESC")
    List<BaoCaoDoanhThu> findAllActive();
    
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.maBaoCao = :id AND b.isDeleted = false")
    Optional<BaoCaoDoanhThu> findActiveById(@Param("id") String id);
    
    // ===================================
    // SEARCH BY CRITERIA
    // ===================================
    
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.loaiBaoCao = :loai AND b.isDeleted = false ORDER BY b.ngayBaoCao DESC")
    List<BaoCaoDoanhThu> findByLoaiBaoCao(@Param("loai") String loai);
    
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.ngayBaoCao BETWEEN :tuNgay AND :denNgay AND b.isDeleted = false ORDER BY b.ngayBaoCao DESC")
    List<BaoCaoDoanhThu> findByDateRange(@Param("tuNgay") LocalDate tuNgay, @Param("denNgay") LocalDate denNgay);
    
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.tuNgay >= :tuNgay AND b.denNgay <= :denNgay AND b.isDeleted = false ORDER BY b.ngayBaoCao DESC")
    List<BaoCaoDoanhThu> findByPeriodRange(@Param("tuNgay") LocalDate tuNgay, @Param("denNgay") LocalDate denNgay);
    
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.loaiBaoCao = :loai AND b.ngayBaoCao BETWEEN :tuNgay AND :denNgay AND b.isDeleted = false ORDER BY b.ngayBaoCao DESC")
    List<BaoCaoDoanhThu> findByLoaiAndDateRange(@Param("loai") String loai, @Param("tuNgay") LocalDate tuNgay, @Param("denNgay") LocalDate denNgay);
    
    // ===================================
    // ADVANCED SEARCH
    // ===================================
    
    @Query("""
        SELECT b FROM BaoCaoDoanhThu b 
        WHERE (:loai IS NULL OR b.loaiBaoCao = :loai)
          AND b.ngayBaoCao BETWEEN :tuNgay AND :denNgay
          AND b.isDeleted = false
        ORDER BY b.ngayBaoCao DESC
    """)
    List<BaoCaoDoanhThu> findByAdvancedCriteria(
        @Param("loai") String loai, 
        @Param("tuNgay") LocalDate tuNgay, 
        @Param("denNgay") LocalDate denNgay
    );
    
    // ===================================
    // EXISTENCE CHECKS
    // ===================================
    
    @Query("""
        SELECT COUNT(b) > 0 FROM BaoCaoDoanhThu b 
        WHERE b.loaiBaoCao = :loai 
          AND b.tuNgay = :tuNgay 
          AND b.denNgay = :denNgay 
          AND b.isDeleted = false
    """)
    boolean existsByLoaiAndDateRange(@Param("loai") String loai, @Param("tuNgay") LocalDate tuNgay, @Param("denNgay") LocalDate denNgay);
    
    // ===================================
    // STATISTICAL QUERIES
    // ===================================
    
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.loaiBaoCao = :loai AND b.isDeleted = false ORDER BY b.ngayBaoCao DESC LIMIT 1")
    Optional<BaoCaoDoanhThu> findLatestByLoai(@Param("loai") String loai);
    
    @Query("""
        SELECT b FROM BaoCaoDoanhThu b 
        WHERE b.loaiBaoCao = :loai 
          AND b.denNgay < :tuNgay 
          AND b.isDeleted = false 
        ORDER BY b.denNgay DESC 
        LIMIT 1
    """)
    Optional<BaoCaoDoanhThu> findPreviousPeriod(@Param("loai") String loai, @Param("tuNgay") LocalDate tuNgay);
    
    @Query("""
        SELECT 
            SUM(b.tongDoanhThu) as tongDoanhThu,
            SUM(b.tongSoHoaDon) as tongSoHoaDon,
            SUM(b.tongSoKhachHang) as tongSoKhachHang,
            AVG(b.doanhThuTrungBinh) as doanhThuTrungBinh
        FROM BaoCaoDoanhThu b 
        WHERE b.loaiBaoCao = :loai 
          AND b.ngayBaoCao BETWEEN :tuNgay AND :denNgay
          AND b.isDeleted = false
    """)
    Object[] getThongKeTongHop(@Param("loai") String loai, @Param("tuNgay") LocalDate tuNgay, @Param("denNgay") LocalDate denNgay);
    
    // ===================================
    // TOP PERFORMING REPORTS
    // ===================================
    
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.isDeleted = false ORDER BY b.tongDoanhThu DESC LIMIT :limit")
    List<BaoCaoDoanhThu> findTopByDoanhThu(@Param("limit") int limit);
    
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.loaiBaoCao = :loai AND b.isDeleted = false ORDER BY b.tongDoanhThu DESC LIMIT :limit")
    List<BaoCaoDoanhThu> findTopByLoaiAndDoanhThu(@Param("loai") String loai, @Param("limit") int limit);
    
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.tyLeTangTruongDoanhThu > 0 AND b.isDeleted = false ORDER BY b.tyLeTangTruongDoanhThu DESC LIMIT :limit")
    List<BaoCaoDoanhThu> findTopByTangTruong(@Param("limit") int limit);
    
    // ===================================
    // JSON SEARCH QUERIES
    // ===================================
    
    @Query(value = """
        SELECT * FROM BaoCaoDoanhThu b
        WHERE JSON_CONTAINS(b.TopSanPhamBanChay, JSON_OBJECT('maSP', ?1))
          AND b.IsDeleted = false
        ORDER BY b.NgayBaoCao DESC
    """, nativeQuery = true)
    List<BaoCaoDoanhThu> findByTopSanPhamContains(String maSP);
    
    @Query(value = """
        SELECT * FROM BaoCaoDoanhThu b
        WHERE JSON_CONTAINS(b.TopKhachHangTiemNang, JSON_OBJECT('maKH', ?1))
          AND b.IsDeleted = false
        ORDER BY b.NgayBaoCao DESC
    """, nativeQuery = true)
    List<BaoCaoDoanhThu> findByTopKhachHangContains(String maKH);

    @Query(value = """
        SELECT * FROM BaoCaoDoanhThu b
        WHERE JSON_CONTAINS(b.ChiTietSanPham, JSON_OBJECT('maSP', ?1))
          AND b.IsDeleted = false
        ORDER BY b.NgayBaoCao DESC
    """, nativeQuery = true)
    List<BaoCaoDoanhThu> findByChiTietSanPhamContains(String maSP);

    @Query(value = """
        SELECT * FROM BaoCaoDoanhThu b
        WHERE JSON_CONTAINS(b.ChiTietKhachHang, JSON_OBJECT('maKH', ?1))
          AND b.IsDeleted = false
        ORDER BY b.NgayBaoCao DESC
    """, nativeQuery = true)
    List<BaoCaoDoanhThu> findByChiTietKhachHangContains(String maKH);

    @Query(value = """
        SELECT * FROM BaoCaoDoanhThu b
        WHERE JSON_CONTAINS(b.ThongKeLoaiSanPham, JSON_OBJECT('maLoaiSP', ?1))
          AND b.IsDeleted = false
        ORDER BY b.NgayBaoCao DESC
    """, nativeQuery = true)
    List<BaoCaoDoanhThu> findByLoaiSanPhamContains(String maLoaiSP);

    // ===================================
    // MAINTENANCE QUERIES
    // ===================================
    
    @Query("SELECT COUNT(b) FROM BaoCaoDoanhThu b WHERE b.isDeleted = false")
    long countActive();
    
    @Query("SELECT COUNT(b) FROM BaoCaoDoanhThu b WHERE b.loaiBaoCao = :loai AND b.isDeleted = false")
    long countByLoai(@Param("loai") String loai);
    
    @Query("SELECT DISTINCT b.loaiBaoCao FROM BaoCaoDoanhThu b WHERE b.isDeleted = false ORDER BY b.loaiBaoCao")
    List<String> findAllLoaiBaoCao();
    
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.ngayTao < :cutoffDate AND b.isDeleted = false")
    List<BaoCaoDoanhThu> findOldReports(@Param("cutoffDate") LocalDate cutoffDate);
    
    // ===================================
    // HOADON RELATIONSHIP QUERIES
    // ===================================
    
    @Query("SELECT b FROM BaoCaoDoanhThu b JOIN b.hoaDons h WHERE h.maHD = :maHD AND b.isDeleted = false")
    List<BaoCaoDoanhThu> findByHoaDonId(@Param("maHD") Integer maHD);
    
    @Query("SELECT h FROM BaoCaoDoanhThu b JOIN b.hoaDons h WHERE b.maBaoCao = :maBaoCao AND h.isDeleted = false ORDER BY h.ngayLap DESC")
    List<HoaDon> findHoaDonsByBaoCaoId(@Param("maBaoCao") String maBaoCao);
    
    @Query("SELECT COUNT(h) FROM BaoCaoDoanhThu b JOIN b.hoaDons h WHERE b.maBaoCao = :maBaoCao AND h.isDeleted = false")
    Long countHoaDonsByBaoCaoId(@Param("maBaoCao") String maBaoCao);
    
    @Query("SELECT SUM(h.tongTien) FROM BaoCaoDoanhThu b JOIN b.hoaDons h WHERE b.maBaoCao = :maBaoCao AND h.isDeleted = false AND h.trangThai = 1")
    BigDecimal sumDoanhThuFromHoaDons(@Param("maBaoCao") String maBaoCao);
    
    @Query("""
        SELECT b FROM BaoCaoDoanhThu b 
        WHERE b.isDeleted = false 
        AND EXISTS (
            SELECT 1 FROM b.hoaDons h 
            WHERE h.ngayLap BETWEEN :tuNgay AND :denNgay 
            AND h.isDeleted = false 
            AND h.trangThai = 1
        )
        ORDER BY b.ngayBaoCao DESC
    """)
    List<BaoCaoDoanhThu> findByHoaDonDateRange(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);

    // ===================================
    // QUERIES FOR NEW RELATIONSHIPS
    // ===================================

    // Queries for NhanVien relationship
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.nhanVienTao.maNV = :maNV AND b.isDeleted = false ORDER BY b.ngayBaoCao DESC")
    List<BaoCaoDoanhThu> findByNhanVienTao(@Param("maNV") String maNV);

    @Query("SELECT COUNT(b) FROM BaoCaoDoanhThu b WHERE b.nhanVienTao.maNV = :maNV AND b.isDeleted = false")
    long countByNhanVienTao(@Param("maNV") String maNV);

    // Queries for CuaHang relationship
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.cuaHang.maCH = :maCH AND b.isDeleted = false ORDER BY b.ngayBaoCao DESC")
    List<BaoCaoDoanhThu> findByCuaHang(@Param("maCH") String maCH);

    @Query("SELECT COUNT(b) FROM BaoCaoDoanhThu b WHERE b.cuaHang.maCH = :maCH AND b.isDeleted = false")
    long countByCuaHang(@Param("maCH") String maCH);

    @Query("SELECT SUM(b.tongDoanhThu) FROM BaoCaoDoanhThu b WHERE b.cuaHang.maCH = :maCH AND b.isDeleted = false")
    BigDecimal sumDoanhThuByCuaHang(@Param("maCH") String maCH);

    // Queries for PhieuNhapHang relationship
    @Query("SELECT b FROM BaoCaoDoanhThu b JOIN b.phieuNhapHangs p WHERE p.maPN = :maPN AND b.isDeleted = false")
    List<BaoCaoDoanhThu> findByPhieuNhapHang(@Param("maPN") Integer maPN);

    @Query("SELECT p FROM BaoCaoDoanhThu b JOIN b.phieuNhapHangs p WHERE b.maBaoCao = :maBaoCao AND p.isDeleted = false")
    List<PhieuNhapHang> findPhieuNhapHangsByBaoCaoId(@Param("maBaoCao") String maBaoCao);

    @Query("SELECT COUNT(p) FROM BaoCaoDoanhThu b JOIN b.phieuNhapHangs p WHERE b.maBaoCao = :maBaoCao AND p.isDeleted = false")
    long countPhieuNhapHangsByBaoCaoId(@Param("maBaoCao") String maBaoCao);

    @Query("SELECT SUM(p.tongTienNhap) FROM BaoCaoDoanhThu b JOIN b.phieuNhapHangs p WHERE b.maBaoCao = :maBaoCao AND p.isDeleted = false")
    BigDecimal sumChiPhiFromPhieuNhapHangs(@Param("maBaoCao") String maBaoCao);

    // Combined queries for comprehensive reporting
    @Query("""
        SELECT b FROM BaoCaoDoanhThu b 
        WHERE b.cuaHang.maCH = :maCH 
        AND b.nhanVienTao.maNV = :maNV 
        AND b.ngayBaoCao BETWEEN :tuNgay AND :denNgay 
        AND b.isDeleted = false 
        ORDER BY b.ngayBaoCao DESC
    """)
    List<BaoCaoDoanhThu> findByCuaHangAndNhanVienAndDateRange(
        @Param("maCH") String maCH,
        @Param("maNV") String maNV,
        @Param("tuNgay") LocalDate tuNgay,
        @Param("denNgay") LocalDate denNgay
    );

    @Query("""
        SELECT b FROM BaoCaoDoanhThu b 
        WHERE b.cuaHang.maCH = :maCH 
        AND b.ngayBaoCao BETWEEN :tuNgay AND :denNgay 
        AND b.isDeleted = false 
        ORDER BY b.tongDoanhThu DESC
    """)
    List<BaoCaoDoanhThu> findTopPerformingReportsByCuaHang(
        @Param("maCH") String maCH,
        @Param("tuNgay") LocalDate tuNgay,
        @Param("denNgay") LocalDate denNgay,
        Pageable pageable
    );
}