package com.example.mini_supermarket.dao;

import com.example.mini_supermarket.entity.ChiTietPhieuNhap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChiTietPhieuNhapRepository extends JpaRepository<ChiTietPhieuNhap, Integer> {
    
    // Tìm tất cả chi tiết phiếu nhập chưa bị xóa (isDeleted = false)
    @Query("SELECT c FROM ChiTietPhieuNhap c WHERE c.isDeleted = false")
    List<ChiTietPhieuNhap> findAllActive();
    
    // Tìm chi tiết phiếu nhập theo ID và chưa bị xóa
    @Query("SELECT c FROM ChiTietPhieuNhap c WHERE c.maCTPN = :id AND c.isDeleted = false")
    Optional<ChiTietPhieuNhap> findActiveById(@Param("id") Integer id);
    
    // Tìm chi tiết phiếu nhập theo ID (bao gồm cả đã xóa)
    @Query("SELECT c FROM ChiTietPhieuNhap c WHERE c.maCTPN = :id")
    Optional<ChiTietPhieuNhap> findByIdIncludeDeleted(@Param("id") Integer id);
} 