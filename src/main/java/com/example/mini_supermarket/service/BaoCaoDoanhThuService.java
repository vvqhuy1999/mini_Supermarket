package com.example.mini_supermarket.service;

import com.example.mini_supermarket.dto.ThongKeKhachHangDTO;
import com.example.mini_supermarket.dto.ThongKeSanPhamDTO;
import com.example.mini_supermarket.entity.BaoCaoDoanhThu;
import com.example.mini_supermarket.entity.HoaDon;
import com.example.mini_supermarket.entity.PhieuNhapHang;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface BaoCaoDoanhThuService {
    
    // CRUD operations
    List<BaoCaoDoanhThu> findAll();
    List<BaoCaoDoanhThu> findAllActive();
    BaoCaoDoanhThu findById(String id);
    BaoCaoDoanhThu findActiveById(String id);
    BaoCaoDoanhThu save(BaoCaoDoanhThu baoCaoDoanhThu);
    BaoCaoDoanhThu update(BaoCaoDoanhThu baoCaoDoanhThu);
    void deleteById(String id);
    void softDeleteById(String id);
    
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
    List<Map<String, Object>> getTopSanPhamFromJson(String maBaoCao);
    List<Map<String, Object>> getTopKhachHangFromJson(String maBaoCao);
    List<Map<String, Object>> getThongKeLoaiSanPhamFromJson(String maBaoCao);
    Map<String, Object> getPhanTichTangTruongFromJson(String maBaoCao);
    List<Map<String, Object>> getChiTietSanPhamFromJson(String maBaoCao);
    List<Map<String, Object>> getChiTietKhachHangFromJson(String maBaoCao);
    List<Map<String, Object>> getChiTietLoaiSanPhamFromJson(String maBaoCao);
    
    // JSON search
    List<BaoCaoDoanhThu> findByTopSanPhamContains(String maSP);
    List<BaoCaoDoanhThu> findByTopKhachHangContains(String maKH);
    List<BaoCaoDoanhThu> findByChiTietSanPhamContains(String maSP);
    List<BaoCaoDoanhThu> findByChiTietKhachHangContains(String maKH);
    List<BaoCaoDoanhThu> findByLoaiSanPhamContains(String maLoaiSP);
    
    // Utility methods
    boolean kiemTraBaoCaoTonTai(String loai, LocalDate tuNgay, LocalDate denNgay);
    BaoCaoDoanhThu layBaoCaoKyTruoc(String loai, LocalDate tuNgay);
    
    // HoaDon relationship methods
    List<BaoCaoDoanhThu> findByHoaDonId(Integer maHD);
    List<HoaDon> findHoaDonsByBaoCaoId(String maBaoCao);
    Long countHoaDonsByBaoCaoId(String maBaoCao);
    BigDecimal sumDoanhThuFromHoaDons(String maBaoCao);
    void linkHoaDonsToBaoCao(String maBaoCao, List<Integer> hoaDonIds);
    void unlinkHoaDonsFromBaoCao(String maBaoCao, List<Integer> hoaDonIds);
    
    
    // ===================================
    // NEW RELATIONSHIP METHODS
    // ===================================
    
    // NhanVien relationship methods
    List<BaoCaoDoanhThu> findByNhanVienTao(String maNV);
    long countByNhanVienTao(String maNV);
    
    // CuaHang relationship methods
    List<BaoCaoDoanhThu> findByCuaHang(String maCH);
    long countByCuaHang(String maCH);
    BigDecimal sumDoanhThuByCuaHang(String maCH);
    
    // PhieuNhapHang relationship methods
    List<BaoCaoDoanhThu> findByPhieuNhapHang(Integer maPN);
    List<PhieuNhapHang> findPhieuNhapHangsByBaoCaoId(Long maBaoCao);
    long countPhieuNhapHangsByBaoCaoId(Long maBaoCao);
    BigDecimal sumChiPhiFromPhieuNhapHangs(Long maBaoCao);
    
    // Link PhieuNhapHangs to BaoCao
    BaoCaoDoanhThu linkPhieuNhapHangsToBaoCao(String maBaoCao, List<Integer> phieuNhapIds);
    BaoCaoDoanhThu unlinkPhieuNhapHangsFromBaoCao(String maBaoCao, List<Integer> phieuNhapIds);
    
    // Combined search methods
    List<BaoCaoDoanhThu> findByCuaHangAndNhanVienAndDateRange(String maCH, String maNV, LocalDate tuNgay, LocalDate denNgay);
    List<BaoCaoDoanhThu> findTopPerformingReportsByCuaHang(String maCH, LocalDate tuNgay, LocalDate denNgay, int limit);
    
    // Business logic methods
    BaoCaoDoanhThu capNhatThongKeToanDien(Long maBaoCao);
    BigDecimal tinhLoiNhuan(Long maBaoCao);
    
    // Date range search method
    List<BaoCaoDoanhThu> findByHoaDonDateRange(LocalDateTime tuNgay, LocalDateTime denNgay);
}