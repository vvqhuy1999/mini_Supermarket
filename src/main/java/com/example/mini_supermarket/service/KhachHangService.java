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
} 