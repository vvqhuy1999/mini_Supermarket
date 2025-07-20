package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.Kho;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface KhoService {
    List<Kho> findAll();

    List<Kho> findAllActive(); // Chỉ lấy các record chưa bị xóa

    Kho findById(Integer id);

    Kho findActiveById(Integer id); // Chỉ lấy record chưa bị xóa

    Kho save(Kho kho);

    void deleteById(Integer id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(Integer id); // Soft delete - set isDeleted = true

    Kho update(Kho kho);
} 