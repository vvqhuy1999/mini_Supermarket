package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, String> {
    
    // Tìm tất cả khuyến mãi chưa bị xóa (isDeleted = false)
    @Query("SELECT k FROM KhuyenMai k WHERE k.isDeleted = false")
    List<KhuyenMai> findAllActive();
    
    // Tìm khuyến mãi theo ID và chưa bị xóa
    @Query("SELECT k FROM KhuyenMai k WHERE k.maKM = :id AND k.isDeleted = false")
    Optional<KhuyenMai> findActiveById(@Param("id") String id);
    
    // Tìm khuyến mãi theo ID (bao gồm cả đã xóa)
    @Query("SELECT k FROM KhuyenMai k WHERE k.maKM = :id")
    Optional<KhuyenMai> findByIdIncludeDeleted(@Param("id") String id);
} 
