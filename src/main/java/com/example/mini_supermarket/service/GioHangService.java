package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.GioHang;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GioHangService {
    List<GioHang> findAll();

    List<GioHang> findAllActive(); // Chỉ lấy các record chưa bị xóa

    GioHang findById(Integer id);

    GioHang findActiveById(Integer id); // Chỉ lấy record chưa bị xóa

    GioHang save(GioHang gioHang);

    void deleteById(Integer id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(Integer id); // Soft delete - set isDeleted = true

    GioHang update(GioHang gioHang);
} 