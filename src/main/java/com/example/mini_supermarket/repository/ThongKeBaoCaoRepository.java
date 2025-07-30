package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.ThongKeBaoCao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ThongKeBaoCaoRepository extends JpaRepository<ThongKeBaoCao, Integer> {
    
    // Tìm tất cả thống kê báo cáo chưa bị xóa (isDeleted = false)
    @Query("SELECT t FROM ThongKeBaoCao t WHERE t.isDeleted = false")
    List<ThongKeBaoCao> findAllActive();
    
    // Tìm thống kê báo cáo theo ID và chưa bị xóa
    @Query("SELECT t FROM ThongKeBaoCao t WHERE t.maBaoCao = :id AND t.isDeleted = false")
    Optional<ThongKeBaoCao> findActiveById(@Param("id") Integer id);
    
    // Tìm thống kê báo cáo theo ID (bao gồm cả đã xóa)
    @Query("SELECT t FROM ThongKeBaoCao t WHERE t.maBaoCao = :id")
    Optional<ThongKeBaoCao> findByIdIncludeDeleted(@Param("id") Integer id);
} 
