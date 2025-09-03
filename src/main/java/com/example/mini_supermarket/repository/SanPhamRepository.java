package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.SanPham;
import com.example.mini_supermarket.dto.SanPhamOptimizedDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SanPhamRepository extends JpaRepository<SanPham, String> {
    
    // === QUERY CƠ BẢN - TRẢ VỀ ENTITY ĐẦY ĐỦ ===
    
    // Tìm tất cả sản phẩm chưa bị xóa (isDeleted = false)
    @Query("SELECT s FROM SanPham s WHERE s.isDeleted = false")
    List<SanPham> findAllActive();
    
    // Tìm sản phẩm theo ID và chưa bị xóa
    @Query("SELECT s FROM SanPham s WHERE s.maSP = :id AND s.isDeleted = false")
    Optional<SanPham> findActiveById(@Param("id") String id);
    
    // Tìm sản phẩm theo ID (bao gồm cả đã xóa)
    @Query("SELECT s FROM SanPham s WHERE s.maSP = :id")
    Optional<SanPham> findByIdIncludeDeleted(@Param("id") String id);
    
    // === QUERY TỐI ƯU - SỬ DỤNG DTO VỚI @BUILDER ===
    
    // Tìm tất cả sản phẩm active - sử dụng DTO tối ưu
    @Query("SELECT new com.example.mini_supermarket.dto.SanPhamOptimizedDto(" +
           "s.maSP, s.loaiSanPham, s.tenSP, s.moTa, " +
           "s.donViTinh, s.trongLuong, s.kichThuoc, s.hanSuDung) " +
           "FROM SanPham s WHERE s.isDeleted = false")
    List<SanPhamOptimizedDto> findAllActiveOptimized();
    
    // Tìm sản phẩm theo ID - sử dụng DTO tối ưu
    @Query("SELECT new com.example.mini_supermarket.dto.SanPhamOptimizedDto(" +
           "s.maSP, s.loaiSanPham, s.tenSP, s.moTa, " +
           "s.donViTinh, s.trongLuong, s.kichThuoc, s.hanSuDung) " +
           "FROM SanPham s WHERE s.maSP = :id AND s.isDeleted = false")
    Optional<SanPhamOptimizedDto> findActiveByIdOptimized(@Param("id") String id);
    
    // Tìm sản phẩm theo category - sử dụng DTO tối ưu
    @Query("SELECT new com.example.mini_supermarket.dto.SanPhamOptimizedDto(" +
           "s.maSP, s.loaiSanPham, s.tenSP, s.moTa, " +
           "s.donViTinh, s.trongLuong, s.kichThuoc, s.hanSuDung) " +
           "FROM SanPham s WHERE s.loaiSanPham.maLoaiSP = :maLoaiSP AND s.isDeleted = false")
    List<SanPhamOptimizedDto> findByCategoryOptimized(@Param("maLoaiSP") String maLoaiSP);
    
    // Tìm sản phẩm theo category và trạng thái kinh doanh - sử dụng DTO tối ưu
    @Query("SELECT new com.example.mini_supermarket.dto.SanPhamOptimizedDto(" +
           "s.maSP, s.loaiSanPham, s.tenSP, s.moTa, " +
           "s.donViTinh, s.trongLuong, s.kichThuoc, s.hanSuDung) " +
           "FROM SanPham s WHERE s.loaiSanPham.maLoaiSP = :maLoaiSP AND s.isDeleted = false AND s.trangThai = 1")
    List<SanPhamOptimizedDto> findByCategoryAndActiveOptimized(@Param("maLoaiSP") String maLoaiSP);

    // Lấy sản phẩm với số lượng tồn kho tổng từ tất cả các kho
    @Query("SELECT new com.example.mini_supermarket.dto.SanPhamOptimizedDto(" +
           "s.maSP, s.loaiSanPham, s.tenSP, s.moTa, " +
           "s.donViTinh, s.trongLuong, s.kichThuoc, s.hanSuDung, " +
           "COALESCE(SUM(CASE WHEN t.isDeleted = false THEN t.soLuongTon ELSE 0 END), 0L)) " +
           "FROM SanPham s LEFT JOIN s.tonKhoChiTiets t " +
           "WHERE s.isDeleted = false " +
           "GROUP BY s.maSP, s.loaiSanPham, s.tenSP, s.moTa, s.donViTinh, s.trongLuong, s.kichThuoc, s.hanSuDung")
    List<SanPhamOptimizedDto> findAllActiveWithTonKho();

    // Lấy sản phẩm theo ID với số lượng tồn kho
    @Query("SELECT new com.example.mini_supermarket.dto.SanPhamOptimizedDto(" +
           "s.maSP, s.loaiSanPham, s.tenSP, s.moTa, " +
           "s.donViTinh, s.trongLuong, s.kichThuoc, s.hanSuDung, " +
           "COALESCE(SUM(CASE WHEN t.isDeleted = false THEN t.soLuongTon ELSE 0 END), 0L)) " +
           "FROM SanPham s LEFT JOIN s.tonKhoChiTiets t " +
           "WHERE s.maSP = :id AND s.isDeleted = false " +
           "GROUP BY s.maSP, s.loaiSanPham, s.tenSP, s.moTa, s.donViTinh, s.trongLuong, s.kichThuoc, s.hanSuDung")
    Optional<SanPhamOptimizedDto> findActiveByIdWithTonKho(@Param("id") String id);

    // Lấy sản phẩm với số lượng tồn kho theo mã kho (lọc theo kho)
    @Query("SELECT new com.example.mini_supermarket.dto.SanPhamOptimizedDto(" +
           "s.maSP, s.loaiSanPham, s.tenSP, s.moTa, " +
           "s.donViTinh, s.trongLuong, s.kichThuoc, s.hanSuDung, " +
           "COALESCE(SUM(CASE WHEN t.isDeleted = false AND t.kho.maKho = :maKho THEN t.soLuongTon ELSE 0 END), 0L)) " +
           "FROM SanPham s LEFT JOIN s.tonKhoChiTiets t " +
           "WHERE s.isDeleted = false " +
           "GROUP BY s.maSP, s.loaiSanPham, s.tenSP, s.moTa, s.donViTinh, s.trongLuong, s.kichThuoc, s.hanSuDung")
    List<SanPhamOptimizedDto> findAllActiveWithTonKhoByKho(@Param("maKho") String maKho);

    // Lấy sản phẩm theo ID với số lượng tồn kho theo mã kho
    @Query("SELECT new com.example.mini_supermarket.dto.SanPhamOptimizedDto(" +
           "s.maSP, s.loaiSanPham, s.tenSP, s.moTa, " +
           "s.donViTinh, s.trongLuong, s.kichThuoc, s.hanSuDung, " +
           "COALESCE(SUM(CASE WHEN t.isDeleted = false AND t.kho.maKho = :maKho THEN t.soLuongTon ELSE 0 END), 0L)) " +
           "FROM SanPham s LEFT JOIN s.tonKhoChiTiets t " +
           "WHERE s.maSP = :id AND s.isDeleted = false " +
           "GROUP BY s.maSP, s.loaiSanPham, s.tenSP, s.moTa, s.donViTinh, s.trongLuong, s.kichThuoc, s.hanSuDung")
    Optional<SanPhamOptimizedDto> findActiveByIdWithTonKhoByKho(@Param("id") String id, @Param("maKho") String maKho);
} 
