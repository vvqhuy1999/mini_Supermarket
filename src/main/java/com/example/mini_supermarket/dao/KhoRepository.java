package com.example.mini_supermarket.dao;

import com.example.mini_supermarket.entity.Kho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KhoRepository extends JpaRepository<Kho, Integer> {
    
    // Tìm tất cả kho chưa bị xóa (isDeleted = false)
    @Query("SELECT k FROM Kho k WHERE k.isDeleted = false")
    List<Kho> findAllActive();
    
    // Tìm kho theo ID và chưa bị xóa
    @Query("SELECT k FROM Kho k WHERE k.maKho = :id AND k.isDeleted = false")
    Optional<Kho> findActiveById(@Param("id") Integer id);
    
    // Tìm kho theo ID (bao gồm cả đã xóa)
    @Query("SELECT k FROM Kho k WHERE k.maKho = :id")
    Optional<Kho> findByIdIncludeDeleted(@Param("id") Integer id);
} 