package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.ChiTietDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Integer> {
    
    // Tìm chi tiết đơn hàng theo mã đơn hàng (chỉ lấy chưa bị xóa)
    @Query("SELECT ctdh FROM ChiTietDonHang ctdh WHERE ctdh.donHang.maDH = :maDH AND ctdh.isdeleted = false")
    List<ChiTietDonHang> findByDonHang_MaDH(@Param("maDH") String maDH);
    
    // Tìm chi tiết đơn hàng theo sản phẩm (chỉ lấy chưa bị xóa)
    @Query("SELECT ctdh FROM ChiTietDonHang ctdh WHERE ctdh.sanPham.maSP = :maSP AND ctdh.isdeleted = false")
    List<ChiTietDonHang> findBySanPham_MaSP(@Param("maSP") String maSP);
    
    // Tìm chi tiết đơn hàng theo đơn hàng và sản phẩm (chỉ lấy chưa bị xóa)
    @Query("SELECT ctdh FROM ChiTietDonHang ctdh WHERE ctdh.donHang.maDH = :maDH AND ctdh.sanPham.maSP = :maSP AND ctdh.isdeleted = false")
    ChiTietDonHang findByDonHang_MaDHAndSanPham_MaSP(@Param("maDH") String maDH, @Param("maSP") String maSP);
    
    // Đếm số lượng sản phẩm trong đơn hàng (chỉ đếm chưa bị xóa)
    @Query("SELECT COUNT(ctdh) FROM ChiTietDonHang ctdh WHERE ctdh.donHang.maDH = :maDH AND ctdh.isdeleted = false")
    long countByDonHang_MaDH(@Param("maDH") String maDH);
    
    // Tìm tất cả chi tiết đơn hàng theo danh sách mã đơn hàng (chỉ lấy chưa bị xóa)
    @Query("SELECT ctdh FROM ChiTietDonHang ctdh WHERE ctdh.donHang.maDH IN :danhSachMaDH AND ctdh.isdeleted = false")
    List<ChiTietDonHang> findByDanhSachMaDonHang(@Param("danhSachMaDH") List<String> danhSachMaDH);
    
    // Tìm chi tiết đơn hàng có giảm giá (chỉ lấy chưa bị xóa)
    @Query("SELECT ctdh FROM ChiTietDonHang ctdh WHERE ctdh.giamGia > 0 AND ctdh.isdeleted = false")
    List<ChiTietDonHang> findChiTietCoGiamGia();
    
    // Override findAll để chỉ lấy chưa bị xóa
    @Override
    @Query("SELECT ctdh FROM ChiTietDonHang ctdh WHERE ctdh.isdeleted = false")
    List<ChiTietDonHang> findAll();
    
    // Override findById để chỉ lấy chưa bị xóa
    @Override
    @Query("SELECT ctdh FROM ChiTietDonHang ctdh WHERE ctdh.maCTHD = :id AND ctdh.isdeleted = false")
    java.util.Optional<ChiTietDonHang> findById(@Param("id") Integer id);
}
