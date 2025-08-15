package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.ChiTietDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Integer> {
    
    // Tìm chi tiết đơn hàng theo mã đơn hàng
    List<ChiTietDonHang> findByDonHang_MaDH(String maDH);
    
    // Tìm chi tiết đơn hàng theo sản phẩm
    List<ChiTietDonHang> findBySanPham_MaSP(String maSP);
    
    // Tìm chi tiết đơn hàng theo đơn hàng và sản phẩm
    ChiTietDonHang findByDonHang_MaDHAndSanPham_MaSP(String maDH, String maSP);
    
    // Đếm số lượng sản phẩm trong đơn hàng
    long countByDonHang_MaDH(String maDH);
    
    // Tìm tất cả chi tiết đơn hàng theo danh sách mã đơn hàng
    @Query("SELECT ctdh FROM ChiTietDonHang ctdh WHERE ctdh.donHang.maDH IN :danhSachMaDH")
    List<ChiTietDonHang> findByDanhSachMaDonHang(@Param("danhSachMaDH") List<String> danhSachMaDH);
    
    // Tìm chi tiết đơn hàng có giảm giá
    @Query("SELECT ctdh FROM ChiTietDonHang ctdh WHERE ctdh.giamGia > 0")
    List<ChiTietDonHang> findChiTietCoGiamGia();
}
