package com.example.mini_supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeDoanhThuDTO {
    private String tenBaoCao;
    private Integer thang;
    private Integer nam;
    private BigDecimal doanhThu;
    private Integer soHoaDon;
    private Integer soKhachHang;
    private BigDecimal doanhThuThangTruoc;
    private BigDecimal tangTruongPhanTram;
    private LocalDateTime thoiGianTu;
    private LocalDateTime thoiGianDen;
    private String tenCuaHang;
    private String maCuaHang;
    private String noiDung;
    private LocalDateTime ngayBaoCao;
    
    // Constructor để mapping từ query result
    public ThongKeDoanhThuDTO(String tenBaoCao, BigDecimal doanhThu, Integer soHoaDon, 
                             LocalDateTime thoiGianTu, LocalDateTime thoiGianDen, 
                             String noiDung, String tenCuaHang) {
        this.tenBaoCao = tenBaoCao;
        this.doanhThu = doanhThu;
        this.soHoaDon = soHoaDon;
        this.thoiGianTu = thoiGianTu;
        this.thoiGianDen = thoiGianDen;
        this.noiDung = noiDung;
        this.tenCuaHang = tenCuaHang;
        
        // Extract thang, nam từ thoiGianTu
        if (thoiGianTu != null) {
            this.thang = thoiGianTu.getMonthValue();
            this.nam = thoiGianTu.getYear();
        }
    }
}