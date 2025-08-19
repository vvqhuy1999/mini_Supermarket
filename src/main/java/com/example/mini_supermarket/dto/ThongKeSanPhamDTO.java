package com.example.mini_supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeSanPhamDTO {
    private String maSP;
    private String tenSP;
    private String tenLoaiSP;
    private Integer soLuongBan;
    private BigDecimal doanhThu;
    private Integer soHoaDon;
    private BigDecimal giaTriTrungBinh;
    private BigDecimal tangTruong;
    private Integer thuTuXepHang;
}