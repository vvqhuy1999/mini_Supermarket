package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {

    // Lấy tất cả record chưa bị xóa
    @Query("SELECT g FROM GioHang g WHERE g.isDeleted = false")
    List<GioHang> findAllActive();

    // Tìm theo ID và chưa bị xóa
    @Query("SELECT g FROM GioHang g WHERE g.maGH = :id AND g.isDeleted = false")
    Optional<GioHang> findActiveById(@Param("id") Integer id);

    // Tìm theo ID bao gồm cả record đã bị xóa
    @Query("SELECT g FROM GioHang g WHERE g.maGH = :id")
    Optional<GioHang> findByIdIncludeDeleted(@Param("id") Integer id);
}