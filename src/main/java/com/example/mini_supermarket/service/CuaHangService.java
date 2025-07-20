package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.CuaHang;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CuaHangService {
    List<CuaHang> findAll();

    List<CuaHang> findAllActive(); // Chỉ lấy các record chưa bị xóa

    CuaHang findById(String id);

    CuaHang findActiveById(String id); // Chỉ lấy record chưa bị xóa

    CuaHang save(CuaHang cuaHang);

    void deleteById(String id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(String id); // Soft delete - set isDeleted = true

    CuaHang update(CuaHang cuaHang);
} 