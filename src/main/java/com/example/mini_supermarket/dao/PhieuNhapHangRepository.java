package com.example.mini_supermarket.dao;

import com.example.mini_supermarket.entity.PhieuNhapHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhieuNhapHangRepository extends JpaRepository<PhieuNhapHang, Integer> {
    
    // Tìm tất cả phiếu nhập hàng chưa bị xóa (isDeleted = false)
    @Query("SELECT p FROM PhieuNhapHang p WHERE p.isDeleted = false")
    List<PhieuNhapHang> findAllActive();
    
    // Tìm phiếu nhập hàng theo ID và chưa bị xóa
    @Query("SELECT p FROM PhieuNhapHang p WHERE p.maPN = :id AND p.isDeleted = false")
    Optional<PhieuNhapHang> findActiveById(@Param("id") Integer id);
    
    // Tìm phiếu nhập hàng theo ID (bao gồm cả đã xóa)
    @Query("SELECT p FROM PhieuNhapHang p WHERE p.maPN = :id")
    Optional<PhieuNhapHang> findByIdIncludeDeleted(@Param("id") Integer id);
} 