package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    
    // Tìm tất cả khách hàng chưa bị xóa (isDeleted = false)
    @Query("SELECT k FROM KhachHang k WHERE k.isDeleted = false")
    List<KhachHang> findAllActive();
    
    // Tìm khách hàng theo ID và chưa bị xóa
    @Query("SELECT k FROM KhachHang k WHERE k.maKH = :id AND k.isDeleted = false")
    Optional<KhachHang> findActiveById(@Param("id") String id);
    
    // Tìm khách hàng theo ID (bao gồm cả đã xóa)
    @Query("SELECT k FROM KhachHang k WHERE k.maKH = :id")
    Optional<KhachHang> findByIdIncludeDeleted(@Param("id") String id);
} 
