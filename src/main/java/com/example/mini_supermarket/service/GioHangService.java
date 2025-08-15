package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.GioHang;

import java.util.List;

public interface GioHangService {
    List<GioHang> findAll();
    
    List<GioHang> findAllActive(); // Chỉ lấy các record chưa bị xóa
    
    GioHang findById(Integer id);
    
    GioHang findActiveById(Integer id); // Chỉ lấy record chưa bị xóa
    
    GioHang save(GioHang gioHang);
    
    void deleteById(Integer id); // Hard delete
    
    void softDeleteById(Integer id); // Soft delete
    
    GioHang update(GioHang gioHang);
}