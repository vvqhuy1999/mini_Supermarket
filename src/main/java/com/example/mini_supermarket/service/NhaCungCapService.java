package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.NhaCungCap;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NhaCungCapService {
    List<NhaCungCap> findAll();

    List<NhaCungCap> findAllActive(); // Chỉ lấy các record chưa bị xóa

    NhaCungCap findById(String id);

    NhaCungCap findActiveById(String id); // Chỉ lấy record chưa bị xóa

    NhaCungCap save(NhaCungCap nhaCungCap);

    void deleteById(String id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(String id); // Soft delete - set isDeleted = true

    NhaCungCap update(NhaCungCap nhaCungCap);
} 