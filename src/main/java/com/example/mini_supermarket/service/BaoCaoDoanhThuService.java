package com.example.mini_supermarket.service;

import com.example.mini_supermarket.dto.ThongKeKhachHangDTO;
import com.example.mini_supermarket.dto.ThongKeSanPhamDTO;
import com.example.mini_supermarket.entity.BaoCaoDoanhThu;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BaoCaoDoanhThuService {
    
    // CRUD operations
    List<BaoCaoDoanhThu> findAll();
    List<BaoCaoDoanhThu> findAllActive();
    BaoCaoDoanhThu findById(Long id);
    BaoCaoDoanhThu findActiveById(Long id);
    BaoCaoDoanhThu save(BaoCaoDoanhThu baoCaoDoanhThu);
    BaoCaoDoanhThu update(BaoCaoDoanhThu baoCaoDoanhThu);
    void deleteById(Long id);
    void softDeleteById(Long id);
    
    // Tìm kiếm báo cáo
    List<BaoCaoDoanhThu> findByLoaiBaoCao(String loai);
    List<BaoCaoDoanhThu> findByDateRange(LocalDate tuNgay, LocalDate denNgay);
    List<BaoCaoDoanhThu> findByLoaiAndDateRange(String loai, LocalDate tuNgay, LocalDate denNgay);
    
    // Tạo báo cáo tự động
    BaoCaoDoanhThu taoBaoCaoDoanhThu(String loaiBaoCao, LocalDate tuNgay, LocalDate denNgay);
    BaoCaoDoanhThu taoOrCapNhatBaoCao(String loaiBaoCao, LocalDate tuNgay, LocalDate denNgay);
    
    // Thống kê sản phẩm
    List<ThongKeSanPhamDTO> thongKeSanPhamBanChay(LocalDate tuNgay, LocalDate denNgay, int limit);
    List<ThongKeSanPhamDTO> thongKeSanPhamTangTruong(LocalDate tuNgay, LocalDate denNgay, int limit);
    
    // Thống kê khách hàng
    List<ThongKeKhachHangDTO> thongKeKhachHangTiemNang(LocalDate tuNgay, LocalDate denNgay, int limit);
    List<ThongKeKhachHangDTO> thongKeKhachHangTangTruong(LocalDate tuNgay, LocalDate denNgay, int limit);
    
    // Truy xuất JSON data
    List<Map<String, Object>> getTopSanPhamFromJson(Long maBaoCao);
    List<Map<String, Object>> getTopKhachHangFromJson(Long maBaoCao);
    List<Map<String, Object>> getThongKeLoaiSanPhamFromJson(Long maBaoCao);
    Map<String, Object> getPhanTichTangTruongFromJson(Long maBaoCao);
    List<Map<String, Object>> getChiTietSanPhamFromJson(Long maBaoCao);
    List<Map<String, Object>> getChiTietKhachHangFromJson(Long maBaoCao);
    List<Map<String, Object>> getChiTietLoaiSanPhamFromJson(Long maBaoCao);
    
    // JSON search
    List<BaoCaoDoanhThu> findByTopSanPhamContains(String maSP);
    List<BaoCaoDoanhThu> findByTopKhachHangContains(String maKH);
    List<BaoCaoDoanhThu> findByChiTietSanPhamContains(String maSP);
    List<BaoCaoDoanhThu> findByChiTietKhachHangContains(String maKH);
    List<BaoCaoDoanhThu> findByLoaiSanPhamContains(String maLoaiSP);
    
    // Utility methods
    boolean kiemTraBaoCaoTonTai(String loai, LocalDate tuNgay, LocalDate denNgay);
    BaoCaoDoanhThu layBaoCaoKyTruoc(String loai, LocalDate tuNgay);
}