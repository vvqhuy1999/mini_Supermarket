package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.BangLuong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BangLuongRepository extends JpaRepository<BangLuong, Integer> {
    
    // Tìm tất cả bảng lương chưa bị xóa (isDeleted = false)
    @Query("SELECT b FROM BangLuong b WHERE b.isDeleted = false")
    List<BangLuong> findAllActive();
    
    // Tìm bảng lương theo ID và chưa bị xóa
    @Query("SELECT b FROM BangLuong b WHERE b.maLuong = :id AND b.isDeleted = false")
    Optional<BangLuong> findActiveById(@Param("id") Integer id);
    
    // Tìm bảng lương theo ID (bao gồm cả đã xóa)
    @Query("SELECT b FROM BangLuong b WHERE b.maLuong = :id")
    Optional<BangLuong> findByIdIncludeDeleted(@Param("id") Integer id);
} 