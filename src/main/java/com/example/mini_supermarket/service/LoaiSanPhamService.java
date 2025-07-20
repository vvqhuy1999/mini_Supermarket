package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.LoaiSanPham;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LoaiSanPhamService {
    List<LoaiSanPham> findAll();

    List<LoaiSanPham> findAllActive(); // Chỉ lấy các record chưa bị xóa

    LoaiSanPham findById(String id);

    LoaiSanPham findActiveById(String id); // Chỉ lấy record chưa bị xóa

    LoaiSanPham save(LoaiSanPham loaiSanPham);

    void deleteById(String id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(String id); // Soft delete - set isDeleted = true

    LoaiSanPham update(LoaiSanPham loaiSanPham);
} 