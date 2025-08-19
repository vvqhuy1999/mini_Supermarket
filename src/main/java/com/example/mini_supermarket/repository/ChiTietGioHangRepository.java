package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.ChiTietGioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, Integer> {
    
    // Tìm tất cả chi tiết giỏ hàng chưa bị xóa (isDeleted = false)
    @Query("SELECT c FROM ChiTietGioHang c WHERE c.isDeleted = false")
    List<ChiTietGioHang> findAllActive();
    
    // Tìm chi tiết giỏ hàng theo ID và chưa bị xóa
    @Query("SELECT c FROM ChiTietGioHang c WHERE c.maCTGH = :id AND c.isDeleted = false")
    Optional<ChiTietGioHang> findActiveById(@Param("id") Integer id);
    
    // Tìm chi tiết giỏ hàng theo ID (bao gồm cả đã xóa)
    @Query("SELECT c FROM ChiTietGioHang c WHERE c.maCTGH = :id")
    Optional<ChiTietGioHang> findByIdIncludeDeleted(@Param("id") Integer id);

    // Lấy danh sách chi tiết theo giỏ hàng và chưa bị xóa, chỉ lấy sản phẩm đang hiển thị (SanPham.trangThai = 0)
    @Query("SELECT c FROM ChiTietGioHang c WHERE c.gioHang.maGH = :maGH AND c.isDeleted = false AND c.sanPham.trangThai = 0")
    List<ChiTietGioHang> findByGioHang_MaGH_Active(@Param("maGH") Integer maGH);

    // Tìm theo chi tiết ID và cập nhật số lượng
    // Sử dụng service để load entity rồi lưu; không cần custom query update để đảm bảo trigger/computed
} 
