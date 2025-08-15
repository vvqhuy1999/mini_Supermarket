package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.KhachHang;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface KhachHangService {
    List<KhachHang> findAll();

    List<KhachHang> findAllActive(); // Chỉ lấy các record chưa bị xóa

    KhachHang findById(String id);

    KhachHang findActiveById(String id); // Chỉ lấy record chưa bị xóa

    KhachHang save(KhachHang khachHang);

    void deleteById(String id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(String id); // Soft delete - set isDeleted = true

    KhachHang update(KhachHang khachHang);
    
    /**
     * Đăng ký tài khoản khách hàng mới (bao gồm tạo NguoiDung và KhachHang)
     * @param email Email đăng ký
     * @param matKhau Mật khẩu 
     * @param hoTen Họ tên khách hàng
     * @param sdt Số điện thoại
     * @param diaChi Địa chỉ (optional)
     * @return KhachHang đã được tạo
     */
    KhachHang registerCustomerAccount(String email, String matKhau, String hoTen, String sdt, String diaChi);
    
    /**
     * Tạo khách hàng từ OAuth2 login (Google/Facebook)
     * @param nguoiDung NguoiDung đã được tạo từ OAuth2
     * @param hoTen Họ tên lấy từ OAuth2 provider
     * @return KhachHang đã được tạo, hoặc null nếu đã tồn tại
     */
    KhachHang createCustomerFromOAuth2(com.example.mini_supermarket.entity.NguoiDung nguoiDung, String hoTen);
} 