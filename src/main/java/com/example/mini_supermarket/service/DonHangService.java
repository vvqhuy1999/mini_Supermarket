package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.DonHang;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface DonHangService {
    
    // Lưu đơn hàng
    DonHang saveDonHang(DonHang donHang);
    
    // Tìm đơn hàng theo mã
    Optional<DonHang> findDonHangByMaDH(String maDH);
    
    // Lấy tất cả đơn hàng
    List<DonHang> getAllDonHang();
    
    // Tìm đơn hàng theo khách hàng
    List<DonHang> findDonHangByKhachHang(String maKH);
    
    // Tìm đơn hàng theo nhân viên
    List<DonHang> findDonHangByNhanVien(String maNV);
    
    // Tìm đơn hàng theo trạng thái
    List<DonHang> findDonHangByTrangThai(String trangThai);
    
    // Tìm đơn hàng theo khoảng thời gian
    List<DonHang> findDonHangByThoiGian(Timestamp tuNgay, Timestamp denNgay);
    
    // Tìm đơn hàng chưa giao hàng
    List<DonHang> findDonHangChuaGiao();
    
    // Cập nhật trạng thái đơn hàng
    DonHang updateTrangThaiDonHang(String maDH, String trangThaiMoi);
    
    // Cập nhật ngày giao hàng
    DonHang updateNgayGiaoHang(String maDH, Timestamp ngayGiaoHang);
    
    // Xóa đơn hàng
    void deleteDonHang(String maDH);
    
    // Đếm số đơn hàng theo trạng thái
    long countDonHangByTrangThai(String trangThai);
    
    // Tìm đơn hàng theo khách hàng và khoảng thời gian
    List<DonHang> findDonHangByKhachHangAndThoiGian(String maKH, Timestamp tuNgay, Timestamp denNgay);
    
    // Soft delete đơn hàng (đánh dấu xóa thay vì xóa thật)
    void softDeleteById(String maDH);
}
