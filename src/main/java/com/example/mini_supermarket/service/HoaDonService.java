package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.HoaDon;
import com.example.mini_supermarket.dto.CreateInvoiceFromCartRequest;
import com.example.mini_supermarket.dto.InvoiceCreatedResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HoaDonService {
    List<HoaDon> findAll();

    List<HoaDon> findAllActive(); // Chỉ lấy các record chưa bị xóa

    HoaDon findById(Integer id);

    HoaDon findActiveById(Integer id); // Chỉ lấy record chưa bị xóa

    HoaDon save(HoaDon hoaDon);

    void deleteById(Integer id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(Integer id); // Soft delete - set isDeleted = true

    HoaDon update(HoaDon hoaDon);
    
    // === METHODS MỚI - TẠO HÓA ĐƠN TỪ GIỎ HÀNG ===
    
    // Tạo hóa đơn từ các item trong giỏ hàng được chọn
    InvoiceCreatedResponse createInvoiceFromCart(CreateInvoiceFromCartRequest request);
    
    // Tạo hóa đơn mới và xóa giỏ hàng
    HoaDon createInvoiceAndClearCart(HoaDon hoaDon, List<Integer> cartItemIds);
    
    // Cập nhật trạng thái hóa đơn
    HoaDon updateTrangThai(Integer maHD, Integer trangThaiMoi);

    // Lấy danh sách hóa đơn theo khách hàng
    List<HoaDon> findActiveByCustomer(String maKH);
} 