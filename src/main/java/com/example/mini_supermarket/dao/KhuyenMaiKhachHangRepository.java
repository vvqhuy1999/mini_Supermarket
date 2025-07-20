package com.example.mini_supermarket.dao;

import com.example.mini_supermarket.entity.KhuyenMaiKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KhuyenMaiKhachHangRepository extends JpaRepository<KhuyenMaiKhachHang, Integer> {
    
    // Tìm tất cả khuyến mãi khách hàng chưa bị xóa (isDeleted = false)
    @Query("SELECT k FROM KhuyenMaiKhachHang k WHERE k.isDeleted = false")
    List<KhuyenMaiKhachHang> findAllActive();
    
    // Tìm khuyến mãi khách hàng theo ID và chưa bị xóa
    @Query("SELECT k FROM KhuyenMaiKhachHang k WHERE k.maKMKH = :id AND k.isDeleted = false")
    Optional<KhuyenMaiKhachHang> findActiveById(@Param("id") Integer id);
    
    // Tìm khuyến mãi khách hàng theo ID (bao gồm cả đã xóa)
    @Query("SELECT k FROM KhuyenMaiKhachHang k WHERE k.maKMKH = :id")
    Optional<KhuyenMaiKhachHang> findByIdIncludeDeleted(@Param("id") Integer id);
} 