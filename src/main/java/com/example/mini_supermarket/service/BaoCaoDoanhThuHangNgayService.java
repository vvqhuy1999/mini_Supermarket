package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.BaoCaoDoanhThuHangNgay;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public interface BaoCaoDoanhThuHangNgayService {
    
    // ===== CRUD CƠ BẢN =====
    
    List<BaoCaoDoanhThuHangNgay> findAll();
    
    BaoCaoDoanhThuHangNgay findById(Integer id);
    
    BaoCaoDoanhThuHangNgay save(BaoCaoDoanhThuHangNgay baoCao);
    
    void deleteById(Integer id);
    
    // ===== TÌM KIẾM THEO NGÀY =====
    
    List<BaoCaoDoanhThuHangNgay> findByNgayBan(LocalDate ngayBan);
    
    List<BaoCaoDoanhThuHangNgay> findByNgayBanAndMaCH(LocalDate ngayBan, String maCH);
    
    List<BaoCaoDoanhThuHangNgay> findByNgayBanAndMaSP(LocalDate ngayBan, String maSP);
    
    // ===== TÌM KIẾM THEO KHOẢNG THỜI GIAN =====
    
    List<BaoCaoDoanhThuHangNgay> findByKhoangThoiGian(LocalDate ngayBatDau, LocalDate ngayKetThuc);
    
    List<BaoCaoDoanhThuHangNgay> findByKhoangThoiGianAndMaCH(LocalDate ngayBatDau, LocalDate ngayKetThuc, String maCH);
    
    List<BaoCaoDoanhThuHangNgay> findByKhoangThoiGianAndMaSP(LocalDate ngayBatDau, LocalDate ngayKetThuc, String maSP);
    
    // ===== TỔNG HỢP DOANH THU =====
    
    Double tongDoanhThuTheoNgay(LocalDate ngayBan);
    
    Double tongDoanhThuTheoKhoangThoiGian(LocalDate ngayBatDau, LocalDate ngayKetThuc);
    
    Double tongDoanhThuTheoKhoangThoiGianAndMaCH(LocalDate ngayBatDau, LocalDate ngayKetThuc, String maCH);
    
    // ===== TỔNG HỢP LỢI NHUẬN =====
    
    Double tongLoiNhuanTheoNgay(LocalDate ngayBan);
    
    Double tongLoiNhuanTheoKhoangThoiGian(LocalDate ngayBatDau, LocalDate ngayKetThuc);
    
    Double tongLoiNhuanTheoKhoangThoiGianAndMaCH(LocalDate ngayBatDau, LocalDate ngayKetThuc, String maCH);
    
    // ===== TOP SẢN PHẨM =====
    
    List<Map<String, Object>> topSanPhamBanChay(LocalDate ngayBatDau, LocalDate ngayKetThuc, int limit);
    
    List<Map<String, Object>> topSanPhamLoiNhuanCao(LocalDate ngayBatDau, LocalDate ngayKetThuc, int limit);
    
    // ===== BÁO CÁO THEO CỬA HÀNG =====
    
    List<Map<String, Object>> doanhThuTheoCuaHang(LocalDate ngayBatDau, LocalDate ngayKetThuc);
    
    List<Map<String, Object>> loiNhuanTheoCuaHang(LocalDate ngayBatDau, LocalDate ngayKetThuc);
    
    // ===== CẬP NHẬT BÁO CÁO (GỌI FUNCTION SQL) =====
    
    /**
     * Cập nhật báo cáo doanh thu cho một ngày cụ thể
     * Gọi function SQL: CapNhatBaoCaoDoanhThu(ngay_cap_nhat DATE)
     */
    void capNhatBaoCaoDoanhThu(LocalDate ngayCapNhat);
    
    /**
     * Cập nhật báo cáo doanh thu cho ngày hôm nay
     */
    void capNhatBaoCaoDoanhThuHomNay();
    
    /**
     * Cập nhật báo cáo doanh thu cho ngày hôm qua
     */
    void capNhatBaoCaoDoanhThuHomQua();
    
    /**
     * Cập nhật báo cáo doanh thu cho một khoảng thời gian
     */
    void capNhatBaoCaoDoanhThuTheoKhoangThoiGian(LocalDate ngayBatDau, LocalDate ngayKetThuc);
    
    // ===== THỐNG KÊ TỔNG QUAN =====
    
    /**
     * Thống kê tổng quan theo khoảng thời gian
     */
    Map<String, Object> thongKeTongQuan(LocalDate ngayBatDau, LocalDate ngayKetThuc);
    
    /**
     * Thống kê tổng quan theo khoảng thời gian và cửa hàng
     */
    Map<String, Object> thongKeTongQuanTheoCuaHang(LocalDate ngayBatDau, LocalDate ngayKetThuc, String maCH);
}
