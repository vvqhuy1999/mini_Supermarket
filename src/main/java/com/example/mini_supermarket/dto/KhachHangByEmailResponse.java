package com.example.mini_supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.sql.Timestamp;

/**
 * DTO trả về thông tin khách hàng khi tìm kiếm theo email
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangByEmailResponse {
    
    private String maKH;                    // Mã khách hàng
    private String hoTen;                   // Họ tên
    private String email;                   // Email
    private String sdt;                     // Số điện thoại
    private String diaChi;                  // Địa chỉ
    private LocalDate ngaySinh;             // Ngày sinh
    private Integer diemTichLuy;            // Điểm tích lũy
    private String loaiKhachHang;           // Loại khách hàng
    private Timestamp ngayDangKy;           // Ngày đăng ký
    private String maNguoiDung;             // Mã người dùng
    private Integer vaiTro;                 // Vai trò (0=Quản trị, 1=Quản lý, 2=Nhân viên, 3=Khách hàng)
    
    // Thông tin bổ sung
    private Boolean isActive;               // Trạng thái hoạt động
    private String message;                 // Thông báo kết quả tìm kiếm
}
