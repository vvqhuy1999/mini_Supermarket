package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.ThanhToan;
import org.springframework.stereotype.Service;

import java.util.List;

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
} 