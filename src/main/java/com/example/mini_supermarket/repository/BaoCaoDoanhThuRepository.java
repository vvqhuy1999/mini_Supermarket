package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.BaoCaoDoanhThu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BaoCaoDoanhThuRepository extends JpaRepository<BaoCaoDoanhThu, Long> {
    
    // ===================================
    // BASIC QUERIES
    // ===================================
    
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.isDeleted = false ORDER BY b.ngayBaoCao DESC")
    List<BaoCaoDoanhThu> findAllActive();
    
    @Query("SELECT b FROM BaoCaoDoanhThu b WHERE b.maBaoCao = :id AND b.isDeleted = false")
    Optional<BaoCaoDoanhThu> findActiveById(@Param("id") Long id);
    
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
}