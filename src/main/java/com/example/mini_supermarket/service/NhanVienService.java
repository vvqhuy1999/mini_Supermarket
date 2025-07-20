package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.NhanVien;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NhanVienService {
    List<NhanVien> findAll();

    List<NhanVien> findAllActive(); // Chỉ lấy các record chưa bị xóa

    NhanVien findById(String id);

    NhanVien findActiveById(String id); // Chỉ lấy record chưa bị xóa

    NhanVien save(NhanVien nhanVien);

    void deleteById(String id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(String id); // Soft delete - set isDeleted = true

    NhanVien update(NhanVien nhanVien);
} 