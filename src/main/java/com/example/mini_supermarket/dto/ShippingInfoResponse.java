package com.example.mini_supermarket.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingInfoResponse {
    private String maKH;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String ghiChu;
    private Boolean macDinh;
    private String maNguoiDung;
}
