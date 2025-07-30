package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.LoaiSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LoaiSanPhamRepository extends JpaRepository<LoaiSanPham, String> {
    
    // Tìm tất cả loại sản phẩm chưa bị xóa (isDeleted = false)
    @Query("SELECT l FROM LoaiSanPham l WHERE l.isDeleted = false")
    List<LoaiSanPham> findAllActive();
    
    // Tìm loại sản phẩm theo ID và chưa bị xóa
    @Query("SELECT l FROM LoaiSanPham l WHERE l.maLoaiSP = :id AND l.isDeleted = false")
    Optional<LoaiSanPham> findActiveById(@Param("id") String id);
    
    // Tìm loại sản phẩm theo ID (bao gồm cả đã xóa)
    @Query("SELECT l FROM LoaiSanPham l WHERE l.maLoaiSP = :id")
    Optional<LoaiSanPham> findByIdIncludeDeleted(@Param("id") String id);
} 
