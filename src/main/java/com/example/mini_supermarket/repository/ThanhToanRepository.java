package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ThanhToanRepository extends JpaRepository<ThanhToan, Integer> {
    
    // Tìm tất cả thanh toán chưa bị xóa (isDeleted = false)
    @Query("SELECT t FROM ThanhToan t WHERE t.isDeleted = false")
    List<ThanhToan> findAllActive();
    
    // Tìm thanh toán theo ID và chưa bị xóa
    @Query("SELECT t FROM ThanhToan t WHERE t.maTT = :id AND t.isDeleted = false")
    Optional<ThanhToan> findActiveById(@Param("id") Integer id);
    
    // Tìm thanh toán theo ID (bao gồm cả đã xóa)
    @Query("SELECT t FROM ThanhToan t WHERE t.maTT = :id")
    Optional<ThanhToan> findByIdIncludeDeleted(@Param("id") Integer id);
} 
