package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.ChiTietPhieuXuat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChiTietPhieuXuatRepository extends JpaRepository<ChiTietPhieuXuat, Integer> {
    
    // Tìm tất cả chi tiết phiếu xuất chưa bị xóa (isDeleted = false)
    @Query("SELECT c FROM ChiTietPhieuXuat c WHERE c.isDeleted = false")
    List<ChiTietPhieuXuat> findAllActive();
    
    // Tìm chi tiết phiếu xuất theo ID và chưa bị xóa
    @Query("SELECT c FROM ChiTietPhieuXuat c WHERE c.maCTPXK = :id AND c.isDeleted = false")
    Optional<ChiTietPhieuXuat> findActiveById(@Param("id") Integer id);
    
    // Tìm chi tiết phiếu xuất theo ID (bao gồm cả đã xóa)
    @Query("SELECT c FROM ChiTietPhieuXuat c WHERE c.maCTPXK = :id")
    Optional<ChiTietPhieuXuat> findByIdIncludeDeleted(@Param("id") Integer id);
} 
