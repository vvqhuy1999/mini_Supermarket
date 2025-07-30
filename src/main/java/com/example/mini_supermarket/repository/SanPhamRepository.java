package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SanPhamRepository extends JpaRepository<SanPham, String> {
    
    // Tìm tất cả sản phẩm chưa bị xóa (isDeleted = false)
    @Query("SELECT s FROM SanPham s WHERE s.isDeleted = false")
    List<SanPham> findAllActive();
    
    // Tìm sản phẩm theo ID và chưa bị xóa
    @Query("SELECT s FROM SanPham s WHERE s.maSP = :id AND s.isDeleted = false")
    Optional<SanPham> findActiveById(@Param("id") String id);
    
    // Tìm sản phẩm theo ID (bao gồm cả đã xóa)
    @Query("SELECT s FROM SanPham s WHERE s.maSP = :id")
    Optional<SanPham> findByIdIncludeDeleted(@Param("id") String id);
} 
