package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.PhuongThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhuongThucThanhToanRepository extends JpaRepository<PhuongThucThanhToan, String> {
    
    // Tìm tất cả phương thức thanh toán chưa bị xóa (isDeleted = false)
    @Query("SELECT p FROM PhuongThucThanhToan p WHERE p.isDeleted = false")
    List<PhuongThucThanhToan> findAllActive();
    
    // Tìm phương thức thanh toán theo ID và chưa bị xóa
    @Query("SELECT p FROM PhuongThucThanhToan p WHERE p.maPTTT = :id AND p.isDeleted = false")
    Optional<PhuongThucThanhToan> findActiveById(@Param("id") String id);
    
    // Tìm phương thức thanh toán theo ID (bao gồm cả đã xóa)
    @Query("SELECT p FROM PhuongThucThanhToan p WHERE p.maPTTT = :id")
    Optional<PhuongThucThanhToan> findByIdIncludeDeleted(@Param("id") String id);
} 
