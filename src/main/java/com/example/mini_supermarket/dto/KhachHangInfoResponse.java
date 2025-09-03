package com.example.mini_supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO response cho thông tin khách hàng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangInfoResponse {
    
    private String maKH;            // Mã khách hàng
    private String maNguoiDung;     // Mã người dùng
    private String email;            // Email
    private String hoTen;            // Họ tên
    private String sdt;              // Số điện thoại
    private LocalDate ngaySinh;      // Ngày sinh
    private String diaChi;           // Địa chỉ
    private Integer diemTichLuy;     // Điểm tích lũy
    private String loaiKhachHang;    // Loại khách hàng
    private String message;          // Thông báo
    private Boolean success;         // Trạng thái thành công
}
