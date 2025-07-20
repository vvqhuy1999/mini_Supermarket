package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.PhuongThucThanhToan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PhuongThucThanhToanService {
    List<PhuongThucThanhToan> findAll();

    List<PhuongThucThanhToan> findAllActive(); // Chỉ lấy các record chưa bị xóa

    PhuongThucThanhToan findById(String id);

    PhuongThucThanhToan findActiveById(String id); // Chỉ lấy record chưa bị xóa

    PhuongThucThanhToan save(PhuongThucThanhToan phuongThucThanhToan);

    void deleteById(String id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(String id); // Soft delete - set isDeleted = true

    PhuongThucThanhToan update(PhuongThucThanhToan phuongThucThanhToan);
} 