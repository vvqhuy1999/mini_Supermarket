package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.HinhAnh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HinhAnhRepository extends JpaRepository<HinhAnh, Integer> {
    
    // Tìm tất cả hình ảnh chưa bị xóa (isDeleted = false)
    @Query("SELECT h FROM HinhAnh h WHERE h.isDeleted = false")
    List<HinhAnh> findAllActive();
    
    // Tìm hình ảnh theo ID và chưa bị xóa
    @Query("SELECT h FROM HinhAnh h WHERE h.maHinh = :id AND h.isDeleted = false")
    Optional<HinhAnh> findActiveById(@Param("id") Integer id);
    
    // Tìm hình ảnh theo ID (bao gồm cả đã xóa)
    @Query("SELECT h FROM HinhAnh h WHERE h.maHinh = :id")
    Optional<HinhAnh> findByIdIncludeDeleted(@Param("id") Integer id);
} 
