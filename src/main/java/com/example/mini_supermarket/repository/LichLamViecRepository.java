package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.LichLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LichLamViecRepository extends JpaRepository<LichLamViec, Integer> {
    
    // Tìm tất cả lịch làm việc chưa bị xóa (isDeleted = false)
    @Query("SELECT l FROM LichLamViec l WHERE l.isDeleted = false")
    List<LichLamViec> findAllActive();
    
    // Tìm lịch làm việc theo ID và chưa bị xóa
    @Query("SELECT l FROM LichLamViec l WHERE l.maLich = :id AND l.isDeleted = false")
    Optional<LichLamViec> findActiveById(@Param("id") Integer id);
    
    // Tìm lịch làm việc theo ID (bao gồm cả đã xóa)
    @Query("SELECT l FROM LichLamViec l WHERE l.maLich = :id")
    Optional<LichLamViec> findByIdIncludeDeleted(@Param("id") Integer id);
} 
