package com.example.mini_supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho request đăng ký tài khoản khách hàng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegistrationRequest {
    
    private String email;        // Email đăng nhập (bắt buộc)
    private String matKhau;      // Mật khẩu (bắt buộc)
    private String hoTen;        // Họ tên khách hàng (bắt buộc)
    private String sdt;          // Số điện thoại (bắt buộc)
    private String diaChi;       // Địa chỉ (tùy chọn)
}
