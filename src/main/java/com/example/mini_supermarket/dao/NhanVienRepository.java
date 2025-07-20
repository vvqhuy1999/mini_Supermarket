package com.example.mini_supermarket.dao;

import com.example.mini_supermarket.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NhanVienRepository extends JpaRepository<NhanVien, String> {
    
    // Tìm tất cả nhân viên chưa bị xóa (isDeleted = false)
    @Query("SELECT n FROM NhanVien n WHERE n.isDeleted = false")
    List<NhanVien> findAllActive();
    
    // Tìm nhân viên theo ID và chưa bị xóa
    @Query("SELECT n FROM NhanVien n WHERE n.maNV = :id AND n.isDeleted = false")
    Optional<NhanVien> findActiveById(@Param("id") String id);
    
    // Tìm nhân viên theo ID (bao gồm cả đã xóa)
    @Query("SELECT n FROM NhanVien n WHERE n.maNV = :id")
    Optional<NhanVien> findByIdIncludeDeleted(@Param("id") String id);
} 