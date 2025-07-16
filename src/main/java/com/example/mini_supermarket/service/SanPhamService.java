package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.SanPham;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SanPhamService {
    List<SanPham> findAll();

    List<SanPham> findAllActive(); // Chỉ lấy các record chưa bị xóa

    SanPham findById(String id);

    SanPham findActiveById(String id); // Chỉ lấy record chưa bị xóa

    SanPham save(SanPham sanPham);

    void deleteById(String id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(String id); // Soft delete - set isDeleted = true

    SanPham update(SanPham sanPham);
} 