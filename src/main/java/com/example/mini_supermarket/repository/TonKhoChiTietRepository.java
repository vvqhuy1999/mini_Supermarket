package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.TonKhoChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TonKhoChiTietRepository extends JpaRepository<TonKhoChiTiet, Integer> {
    
    // Tìm tất cả tồn kho chi tiết chưa bị xóa (isDeleted = false)
    @Query("SELECT t FROM TonKhoChiTiet t WHERE t.isDeleted = false")
    List<TonKhoChiTiet> findAllActive();
    
    // Tìm tồn kho chi tiết theo ID và chưa bị xóa
    @Query("SELECT t FROM TonKhoChiTiet t WHERE t.maTKCT = :id AND t.isDeleted = false")
    Optional<TonKhoChiTiet> findActiveById(@Param("id") Integer id);
    
    // Tìm tồn kho chi tiết theo ID (bao gồm cả đã xóa)
    @Query("SELECT t FROM TonKhoChiTiet t WHERE t.maTKCT = :id")
    Optional<TonKhoChiTiet> findByIdIncludeDeleted(@Param("id") Integer id);
} 
