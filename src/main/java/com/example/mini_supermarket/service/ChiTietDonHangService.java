package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.ChiTietDonHang;

import java.util.List;
import java.util.Optional;

public interface ChiTietDonHangService {
    
    // Lưu chi tiết đơn hàng
    ChiTietDonHang saveChiTietDonHang(ChiTietDonHang chiTietDonHang);
    
    // Tìm chi tiết đơn hàng theo mã
    Optional<ChiTietDonHang> findChiTietDonHangByMaCTHD(Integer maCTHD);
    
    // Lấy tất cả chi tiết đơn hàng
    List<ChiTietDonHang> getAllChiTietDonHang();
    
    // Tìm chi tiết đơn hàng theo mã đơn hàng
    List<ChiTietDonHang> findChiTietDonHangByDonHang(String maDH);
    
    // Tìm chi tiết đơn hàng theo sản phẩm
    List<ChiTietDonHang> findChiTietDonHangBySanPham(String maSP);
    
    // Tìm chi tiết đơn hàng theo đơn hàng và sản phẩm
    Optional<ChiTietDonHang> findChiTietDonHangByDonHangAndSanPham(String maDH, String maSP);
    
    // Lưu danh sách chi tiết đơn hàng
    List<ChiTietDonHang> saveAllChiTietDonHang(List<ChiTietDonHang> danhSachChiTiet);
    
    // Cập nhật số lượng sản phẩm
    ChiTietDonHang updateSoLuong(Integer maCTHD, Integer soLuongMoi);
    
    // Cập nhật giảm giá
    ChiTietDonHang updateGiamGia(Integer maCTHD, java.math.BigDecimal giamGiaMoi);
    
    // Xóa chi tiết đơn hàng
    void deleteChiTietDonHang(Integer maCTHD);
    
    // Xóa tất cả chi tiết đơn hàng theo mã đơn hàng
    void deleteAllChiTietDonHangByDonHang(String maDH);
    
    // Đếm số lượng sản phẩm trong đơn hàng
    long countChiTietDonHangByDonHang(String maDH);
    
    // Tìm chi tiết đơn hàng có giảm giá
    List<ChiTietDonHang> findChiTietDonHangCoGiamGia();
    
    // Soft delete chi tiết đơn hàng (đánh dấu xóa thay vì xóa thật)
    void softDeleteById(Integer maCTHD);
}
