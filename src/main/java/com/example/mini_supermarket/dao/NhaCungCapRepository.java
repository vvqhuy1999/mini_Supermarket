package com.example.mini_supermarket.dao;

import com.example.mini_supermarket.entity.NhaCungCap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NhaCungCapRepository extends JpaRepository<NhaCungCap, String> {
    
    // Tìm tất cả nhà cung cấp chưa bị xóa (isDeleted = false)
    @Query("SELECT n FROM NhaCungCap n WHERE n.isDeleted = false")
    List<NhaCungCap> findAllActive();
    
    // Tìm nhà cung cấp theo ID và chưa bị xóa
    @Query("SELECT n FROM NhaCungCap n WHERE n.maNCC = :id AND n.isDeleted = false")
    Optional<NhaCungCap> findActiveById(@Param("id") String id);
    
    // Tìm nhà cung cấp theo ID (bao gồm cả đã xóa)
    @Query("SELECT n FROM NhaCungCap n WHERE n.maNCC = :id")
    Optional<NhaCungCap> findByIdIncludeDeleted(@Param("id") String id);
} 