package com.example.mini_supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO cho việc cập nhật thông tin khách hàng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangUpdateRequest {
    
    private String hoTen;           // Họ tên
    private String sdt;             // Số điện thoại
    private LocalDate ngaySinh;     // Ngày sinh
    private String diaChi;          // Địa chỉ
}
