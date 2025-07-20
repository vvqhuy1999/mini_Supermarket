package com.example.mini_supermarket.dao;

import com.example.mini_supermarket.entity.CaLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CaLamViecRepository extends JpaRepository<CaLamViec, Integer> {
    
    // Tìm tất cả ca làm việc chưa bị xóa (isDeleted = false)
    @Query("SELECT c FROM CaLamViec c WHERE c.isDeleted = false")
    List<CaLamViec> findAllActive();
    
    // Tìm ca làm việc theo ID và chưa bị xóa
    @Query("SELECT c FROM CaLamViec c WHERE c.maCa = :id AND c.isDeleted = false")
    Optional<CaLamViec> findActiveById(@Param("id") Integer id);
    
    // Tìm ca làm việc theo ID (bao gồm cả đã xóa)
    @Query("SELECT c FROM CaLamViec c WHERE c.maCa = :id")
    Optional<CaLamViec> findByIdIncludeDeleted(@Param("id") Integer id);
} 