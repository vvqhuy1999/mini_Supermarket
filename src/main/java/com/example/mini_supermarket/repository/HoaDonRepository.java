package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Tìm hóa đơn theo mã khách hàng và chưa bị xóa
    @Query("SELECT h FROM HoaDon h WHERE h.khachHang.maKH = :maKH AND h.isDeleted = false ORDER BY h.ngayLap DESC")
    List<HoaDon> findActiveByCustomer(@Param("maKH") String maKH);
    
    // ===== OPTIMIZED QUERIES - COMPLETELY DISABLED =====
    
    // Tạm thời disable hoàn toàn tất cả query tối ưu để tránh lỗi startup
    // Sẽ enable lại sau khi fix xong và test kỹ
    
    /*
    // Lấy danh sách hóa đơn tối ưu với pagination
    Page<HoaDonSummaryDTO> findAllActiveSummary(Pageable pageable);
    
    // Lấy hóa đơn theo khách hàng tối ưu với pagination
    Page<HoaDonSummaryDTO> findActiveByCustomerSummary(String maKH, Pageable pageable);
    
    // Lấy hóa đơn theo khách hàng không pagination
    List<HoaDonSummaryDTO> findActiveByCustomerSummaryList(String maKH);
    */
    
    // Đếm số lượng hóa đơn theo trạng thái
    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.trangThai = :trangThai AND h.isDeleted = false")
    Long countByTrangThai(@Param("trangThai") Integer trangThai);
    
    // Đếm số lượng hóa đơn của khách hàng
    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.khachHang.maKH = :maKH AND h.isDeleted = false")
    Long countByCustomer(@Param("maKH") String maKH);
} 
