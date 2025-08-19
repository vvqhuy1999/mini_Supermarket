package com.example.mini_supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeKhachHangDTO {
    private String maKH;
    private String tenKH;
    private String loaiKhachHang;
    private Integer diemTichLuy;
    private Integer soHoaDon;
    private BigDecimal tongChiTieu;
    private BigDecimal giaTriTrungBinh;
    private BigDecimal tangTruong;
    private Integer thuTuXepHang;
}