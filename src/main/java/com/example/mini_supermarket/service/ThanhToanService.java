package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.ThanhToan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ThanhToanService {
    List<ThanhToan> findAll();

    List<ThanhToan> findAllActive(); // Chỉ lấy các record chưa bị xóa

    ThanhToan findById(Integer id);

    ThanhToan findActiveById(Integer id); // Chỉ lấy record chưa bị xóa

    ThanhToan save(ThanhToan thanhToan);

    void deleteById(Integer id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(Integer id); // Soft delete - set isDeleted = true

    ThanhToan update(ThanhToan thanhToan);

    ThanhToan createVNPayPayment(ThanhToan thanhToan, Map<String, String> vnpayParams);

    // Tìm thanh toán theo mã giao dịch ngân hàng
    ThanhToan findByMaGiaoDichNganHang(String maGiaoDichNganHang);

} 