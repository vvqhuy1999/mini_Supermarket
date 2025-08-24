package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.KhachHang;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    
    /**
     * Tìm khách hàng theo mã người dùng
     * @param maNguoiDung Mã người dùng cần tìm
     * @return KhachHang nếu tìm thấy, null nếu không tìm thấy
     */
    KhachHang findByMaNguoiDung(String maNguoiDung);
    
    /**
     * Tìm khách hàng theo email
     * @param email Email cần tìm
     * @return KhachHang nếu tìm thấy, null nếu không tìm thấy
     */
    KhachHang findByEmail(String email);
    
    /**
     * Cập nhật thông tin khách hàng theo mã khách hàng
     * @param maKH Mã khách hàng cần cập nhật
     * @param hoTen Họ tên mới
     * @param sdt Số điện thoại mới
     * @param ngaySinh Ngày sinh mới
     * @param diaChi Địa chỉ mới
     * @return KhachHang đã được cập nhật
     */
    KhachHang updateCustomerInfo(String maKH, String hoTen, String sdt, LocalDate ngaySinh, String diaChi);
    
    /**
     * Lấy thông tin chi tiết khách hàng theo mã khách hàng
     * @param maKH Mã khách hàng cần lấy thông tin
     * @return KhachHang với thông tin đầy đủ
     */
    KhachHang getCustomerInfo(String maKH);
    
    /**
     * Lấy thông tin giao hàng của khách hàng
     * @param maKH Mã khách hàng cần lấy thông tin giao hàng
     * @return ShippingInfoResponse với thông tin giao hàng
     */
    com.example.mini_supermarket.dto.ShippingInfoResponse getShippingInfo(String maKH);
    
    /**
     * Cập nhật thông tin giao hàng của khách hàng
     * @param maKH Mã khách hàng cần cập nhật
     * @param request Thông tin giao hàng mới
     * @return ShippingInfoResponse đã được cập nhật
     */
    com.example.mini_supermarket.dto.ShippingInfoResponse updateShippingInfo(String maKH, com.example.mini_supermarket.dto.ShippingInfoRequest request);
} 