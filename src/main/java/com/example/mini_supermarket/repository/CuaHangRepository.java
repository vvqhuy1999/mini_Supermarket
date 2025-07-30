package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.CuaHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CuaHangRepository extends JpaRepository<CuaHang, String> {
    
    // Tìm tất cả cửa hàng chưa bị xóa (isDeleted = false)
    @Query("SELECT c FROM CuaHang c WHERE c.isDeleted = false")
    List<CuaHang> findAllActive();
    
    // Tìm cửa hàng theo ID và chưa bị xóa
    @Query("SELECT c FROM CuaHang c WHERE c.maCH = :id AND c.isDeleted = false")
    Optional<CuaHang> findActiveById(@Param("id") String id);
    
    // Tìm cửa hàng theo ID (bao gồm cả đã xóa)
    @Query("SELECT c FROM CuaHang c WHERE c.maCH = :id")
    Optional<CuaHang> findByIdIncludeDeleted(@Param("id") String id);
} 
