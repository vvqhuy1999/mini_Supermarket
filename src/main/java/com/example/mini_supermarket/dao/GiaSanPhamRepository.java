package com.example.mini_supermarket.dao;

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
} 