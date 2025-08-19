package com.miniSupermarket.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.mini_supermarket.entity.HoaDon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    
    // Tìm tất cả hóa đơn chưa bị xóa (isDeleted = false)
    @Query("SELECT h FROM HoaDon h WHERE h.isDeleted = false")
    List<HoaDon> findAllActive();
    
    // Tìm hóa đơn theo ID và chưa bị xóa
    @Query("SELECT h FROM HoaDon h WHERE h.maHD = :id AND h.isDeleted = false")
    Optional<HoaDon> findActiveById(@Param("id") Integer id);
    
    // Tìm hóa đơn theo ID (bao gồm cả đã xóa)
    @Query("SELECT h FROM HoaDon h WHERE h.maHD = :id")
    Optional<HoaDon> findByIdIncludeDeleted(@Param("id") Integer id);
    
    // Tìm hóa đơn theo khoảng thời gian và trạng thái (cho báo cáo doanh thu)
    @Query("SELECT h FROM HoaDon h WHERE h.ngayLap BETWEEN :startDate AND :endDate " +
           "AND h.trangThai = :trangThai AND h.isDeleted = false")
    List<HoaDon> findByNgayLapBetweenAndTrangThai(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate, 
        @Param("trangThai") Integer trangThai
    );
    
    // Tìm hóa đơn theo khách hàng trong khoảng thời gian
    @Query("SELECT h FROM HoaDon h WHERE h.maKH = :maKH " +
           "AND h.ngayLap BETWEEN :startDate AND :endDate " +
           "AND h.trangThai = :trangThai AND h.isDeleted = false")
    List<HoaDon> findByCustomerAndDateRange(
        @Param("maKH") String maKH,
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate, 
        @Param("trangThai") Integer trangThai
    );
    
    // Tìm hóa đơn theo nhân viên trong khoảng thời gian
    @Query("SELECT h FROM HoaDon h WHERE h.maNVLap = :maNV " +
           "AND h.ngayLap BETWEEN :startDate AND :endDate " +
           "AND h.isDeleted = false")
    List<HoaDon> findByEmployeeAndDateRange(
        @Param("maNV") String maNV,
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
}