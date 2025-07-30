package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.ChiTietHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChiTietHoaDonRepository extends JpaRepository<ChiTietHoaDon, Integer> {
    
    // Tìm tất cả chi tiết hóa đơn chưa bị xóa (isDeleted = false)
    @Query("SELECT c FROM ChiTietHoaDon c WHERE c.isDeleted = false")
    List<ChiTietHoaDon> findAllActive();
    
    // Tìm chi tiết hóa đơn theo ID và chưa bị xóa
    @Query("SELECT c FROM ChiTietHoaDon c WHERE c.maCTHD = :id AND c.isDeleted = false")
    Optional<ChiTietHoaDon> findActiveById(@Param("id") Integer id);
    
    // Tìm chi tiết hóa đơn theo ID (bao gồm cả đã xóa)
    @Query("SELECT c FROM ChiTietHoaDon c WHERE c.maCTHD = :id")
    Optional<ChiTietHoaDon> findByIdIncludeDeleted(@Param("id") Integer id);
} 
