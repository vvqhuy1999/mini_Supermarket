package com.example.mini_supermarket.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingInfoRequest {
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String ghiChu;
    private Boolean macDinh; // Địa chỉ mặc định
}
