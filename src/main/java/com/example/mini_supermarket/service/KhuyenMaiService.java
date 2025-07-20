package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.KhuyenMai;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface KhuyenMaiService {
    List<KhuyenMai> findAll();

    List<KhuyenMai> findAllActive(); // Chỉ lấy các record chưa bị xóa

    KhuyenMai findById(String id);

    KhuyenMai findActiveById(String id); // Chỉ lấy record chưa bị xóa

    KhuyenMai save(KhuyenMai khuyenMai);

    void deleteById(String id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(String id); // Soft delete - set isDeleted = true

    KhuyenMai update(KhuyenMai khuyenMai);
} 