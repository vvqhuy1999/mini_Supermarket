package com.example.mini_supermarket.dao;

import com.example.mini_supermarket.entity.KhuyenMaiSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KhuyenMaiSanPhamRepository extends JpaRepository<KhuyenMaiSanPham, Integer> {
    
    // Tìm tất cả khuyến mãi sản phẩm chưa bị xóa (isDeleted = false)
    @Query("SELECT k FROM KhuyenMaiSanPham k WHERE k.isDeleted = false")
    List<KhuyenMaiSanPham> findAllActive();
    
    // Tìm khuyến mãi sản phẩm theo ID và chưa bị xóa
    @Query("SELECT k FROM KhuyenMaiSanPham k WHERE k.maKMSP = :id AND k.isDeleted = false")
    Optional<KhuyenMaiSanPham> findActiveById(@Param("id") Integer id);
    
    // Tìm khuyến mãi sản phẩm theo ID (bao gồm cả đã xóa)
    @Query("SELECT k FROM KhuyenMaiSanPham k WHERE k.maKMSP = :id")
    Optional<KhuyenMaiSanPham> findByIdIncludeDeleted(@Param("id") Integer id);
} 