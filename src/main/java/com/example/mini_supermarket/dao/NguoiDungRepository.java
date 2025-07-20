package com.example.mini_supermarket.dao;

import com.example.mini_supermarket.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, String> {
    
    // Tìm tất cả người dùng chưa bị xóa (isDeleted = false)
    @Query("SELECT n FROM NguoiDung n WHERE n.isDeleted = false")
    List<NguoiDung> findAllActive();
    
    // Tìm người dùng theo ID và chưa bị xóa
    @Query("SELECT n FROM NguoiDung n WHERE n.maNguoiDung = :id AND n.isDeleted = false")
    Optional<NguoiDung> findActiveById(@Param("id") String id);
    
    // Tìm người dùng theo ID (bao gồm cả đã xóa)
    @Query("SELECT n FROM NguoiDung n WHERE n.maNguoiDung = :id")
    Optional<NguoiDung> findByIdIncludeDeleted(@Param("id") String id);
} 