package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.HinhAnh;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HinhAnhService {
    List<HinhAnh> findAll();

    List<HinhAnh> findAllActive(); // Chỉ lấy các record chưa bị xóa

    HinhAnh findById(Integer id);

    HinhAnh findActiveById(Integer id); // Chỉ lấy record chưa bị xóa

    HinhAnh save(HinhAnh hinhAnh);

    void deleteById(Integer id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(Integer id); // Soft delete - set isDeleted = true

    HinhAnh update(HinhAnh hinhAnh);
    
    // Tìm ảnh theo sản phẩm và chưa bị xóa
    List<HinhAnh> findBySanPhamAndNotDeleted(com.example.mini_supermarket.entity.SanPham sanPham);
    
    // Tìm ảnh theo mã sản phẩm
    List<HinhAnh> findByMaSanPham(String maSP);
    
    // Tìm ảnh chính theo mã sản phẩm
    HinhAnh findHinhAnhChinhByMaSanPham(String maSP);
} 