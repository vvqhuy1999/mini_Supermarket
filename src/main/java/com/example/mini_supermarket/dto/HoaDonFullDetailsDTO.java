package com.example.mini_supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonFullDetailsDTO {
    
    // Thông tin hóa đơn
    private Integer maHD;
    private String maKH;
    private String tenKH;
    private String maNV;
    private String tenNV;
    private String maKM;
    private String tenKM;
    private Timestamp ngayLap;
    private BigDecimal tongTienHang;
    private BigDecimal tienGiamGia;
    private BigDecimal tongTien;
    private Integer trangThai;
    private String tenTrangThai;
    private Integer diemTichLuy;
    private String ghiChu;
    
    // Chi tiết hóa đơn
    private List<ChiTietHoaDonDTO> chiTietList;
    
    // Thông tin thống kê
    private Integer soLuongSanPham;
    private BigDecimal trungBinhGiaTriSanPham;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChiTietHoaDonDTO {
        private Integer maCTHD;
        private String maSP;
        private String tenSP;
        private Integer soLuong;
        private BigDecimal donGiaBan;
        private BigDecimal thanhTien;
        private BigDecimal giamGia;
        private BigDecimal thanhTienSauGiam;
        private String hinhAnh; // URL hình ảnh sản phẩm
    }
    
    // Helper method để set tên trạng thái
    public void setTrangThai(Integer trangThai) {
        this.trangThai = trangThai;
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
