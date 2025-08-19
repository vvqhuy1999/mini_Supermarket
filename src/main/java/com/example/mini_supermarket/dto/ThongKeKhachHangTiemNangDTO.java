package com.example.mini_supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeKhachHangTiemNangDTO {
    private String maKH;
    private String tenKH;
    private String sdt;
    private String loaiKhachHang;
    private Integer diemTichLuy;
    private Integer soHoaDon;
    private BigDecimal tongChiTieu;
    private BigDecimal giaTriTrungBinh;
    private Integer thang;
    private Integer nam;
    private String tenCuaHang;
    private String maCuaHang;
    private LocalDateTime ngayBaoCao;
    private String ghiChu;
    
    // Constructor cho thống kê tổng quan
    public ThongKeKhachHangTiemNangDTO(String tenBaoCao, Integer soKhachHangTiemNang, 
                                      BigDecimal tongChiTieu, LocalDateTime thoiGianTu, 
                                      LocalDateTime thoiGianDen, String noiDung, String tenCuaHang) {
        this.tenKH = tenBaoCao; // Tạm dùng để lưu tên báo cáo
        this.soHoaDon = soKhachHangTiemNang; // Tạm dùng để lưu số khách hàng tiềm năng
        this.tongChiTieu = tongChiTieu;
        this.ghiChu = noiDung;
        this.tenCuaHang = tenCuaHang;
        
        if (thoiGianTu != null) {
            this.thang = thoiGianTu.getMonthValue();
            this.nam = thoiGianTu.getYear();
        }
    }
    
    // Constructor cho chi tiết khách hàng
    public ThongKeKhachHangTiemNangDTO(String maKH, String tenKH, String sdt, 
                                      String loaiKhachHang, Integer diemTichLuy,
                                      Integer soHoaDon, BigDecimal tongChiTieu, 
                                      BigDecimal giaTriTrungBinh) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.sdt = sdt;
        this.loaiKhachHang = loaiKhachHang;
        this.diemTichLuy = diemTichLuy;
        this.soHoaDon = soHoaDon;
        this.tongChiTieu = tongChiTieu;
        this.giaTriTrungBinh = giaTriTrungBinh;
    }
}