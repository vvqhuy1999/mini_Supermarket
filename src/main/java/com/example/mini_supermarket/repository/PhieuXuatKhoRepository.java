package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.PhieuXuatKho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhieuXuatKhoRepository extends JpaRepository<PhieuXuatKho, Integer> {
    
    // Tìm tất cả phiếu xuất kho chưa bị xóa (isDeleted = false)
    @Query("SELECT p FROM PhieuXuatKho p WHERE p.isDeleted = false")
    List<PhieuXuatKho> findAllActive();
    
    // Tìm phiếu xuất kho theo ID và chưa bị xóa
    @Query("SELECT p FROM PhieuXuatKho p WHERE p.maPXK = :id AND p.isDeleted = false")
    Optional<PhieuXuatKho> findActiveById(@Param("id") Integer id);
    
    // Tìm phiếu xuất kho theo ID (bao gồm cả đã xóa)
    @Query("SELECT p FROM PhieuXuatKho p WHERE p.maPXK = :id")
    Optional<PhieuXuatKho> findByIdIncludeDeleted(@Param("id") Integer id);
} 
