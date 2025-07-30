package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.ChiTietGioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, Integer> {
    
    // Tìm tất cả chi tiết giỏ hàng chưa bị xóa (isDeleted = false)
    @Query("SELECT c FROM ChiTietGioHang c WHERE c.isDeleted = false")
    List<ChiTietGioHang> findAllActive();
    
    // Tìm chi tiết giỏ hàng theo ID và chưa bị xóa
    @Query("SELECT c FROM ChiTietGioHang c WHERE c.maCTGH = :id AND c.isDeleted = false")
    Optional<ChiTietGioHang> findActiveById(@Param("id") Integer id);
    
    // Tìm chi tiết giỏ hàng theo ID (bao gồm cả đã xóa)
    @Query("SELECT c FROM ChiTietGioHang c WHERE c.maCTGH = :id")
    Optional<ChiTietGioHang> findByIdIncludeDeleted(@Param("id") Integer id);
} 
