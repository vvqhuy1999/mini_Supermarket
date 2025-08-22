package com.example.mini_supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonSummaryDTO {
    
    private Integer maHD;
    private String maKH;
    private String tenKH;
    private String maNV;
    private String tenNV;
    private Timestamp ngayLap;
    private BigDecimal tongTienHang;
    private BigDecimal tienGiamGia;
    private BigDecimal tongTien;
    private Integer trangThai;
    private String tenTrangThai;
    private Integer diemTichLuy;
    
    // Constructor for JPA projection
    public HoaDonSummaryDTO(Integer maHD, String maKH, String tenKH, String maNV, String tenNV, 
                           Timestamp ngayLap, BigDecimal tongTienHang, BigDecimal tienGiamGia, 
                           BigDecimal tongTien, Integer trangThai, Integer diemTichLuy) {
        this.maHD = maHD;
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.ngayLap = ngayLap;
        this.tongTienHang = tongTienHang;
        this.tienGiamGia = tienGiamGia;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.diemTichLuy = diemTichLuy;
        
        // Set tên trạng thái
        switch (trangThai) {
            case 0: this.tenTrangThai = "Chờ thanh toán"; break;
            case 1: this.tenTrangThai = "Đã thanh toán"; break;
            case 2: this.tenTrangThai = "Đang xử lý"; break;
            case 3: this.tenTrangThai = "Đã hủy"; break;
            case 4: this.tenTrangThai = "Hoàn trả"; break;
            default: this.tenTrangThai = "Không xác định"; break;
        }
    }
}
