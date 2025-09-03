package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.GiaSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GiaSanPhamRepository extends JpaRepository<GiaSanPham, Integer> {
    
    // Tìm tất cả giá sản phẩm chưa bị xóa (isDeleted = false)
    @Query("SELECT g FROM GiaSanPham g WHERE g.isDeleted = false")
    List<GiaSanPham> findAllActive();
    
    // Tìm giá sản phẩm theo ID và chưa bị xóa
    @Query("SELECT g FROM GiaSanPham g WHERE g.maGia = :id AND g.isDeleted = false")
    Optional<GiaSanPham> findActiveById(@Param("id") Integer id);
    
    // Tìm giá sản phẩm theo ID (bao gồm cả đã xóa)
    @Query("SELECT g FROM GiaSanPham g WHERE g.maGia = :id")
    Optional<GiaSanPham> findByIdIncludeDeleted(@Param("id") Integer id);

    // Giá hiện tại: GiaSanPham có ngayBatDau <= CURRENT_DATE và (ngayKetThuc IS NULL hoặc ngayKetThuc >= CURRENT_DATE)
    @Query("SELECT g FROM GiaSanPham g WHERE g.sanPham.maSP = :maSP AND g.isDeleted = false " +
           "AND g.ngayBatDau <= CURRENT_DATE AND (g.ngayKetThuc IS NULL OR g.ngayKetThuc >= CURRENT_DATE) " +
           "ORDER BY g.ngayBatDau DESC")
    List<GiaSanPham> findApplicablePrices(@Param("maSP") String maSP);

    // Lấy bản ghi giá mới nhất bất kể ngày kết thúc (fallback)
    @Query("SELECT g FROM GiaSanPham g WHERE g.sanPham.maSP = :maSP AND g.isDeleted = false ORDER BY g.ngayBatDau DESC")
    List<GiaSanPham> findLatestPrices(@Param("maSP") String maSP);
} 
