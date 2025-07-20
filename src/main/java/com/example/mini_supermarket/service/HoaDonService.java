package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.HoaDon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HoaDonService {
    List<HoaDon> findAll();

    List<HoaDon> findAllActive(); // Chỉ lấy các record chưa bị xóa

    HoaDon findById(Integer id);

    HoaDon findActiveById(Integer id); // Chỉ lấy record chưa bị xóa

    HoaDon save(HoaDon hoaDon);

    void deleteById(Integer id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(Integer id); // Soft delete - set isDeleted = true

    HoaDon update(HoaDon hoaDon);
} 