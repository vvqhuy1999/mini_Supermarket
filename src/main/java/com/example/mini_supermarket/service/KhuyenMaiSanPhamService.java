package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.KhuyenMaiSanPham;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface KhuyenMaiSanPhamService {
    List<KhuyenMaiSanPham> findAll();
    List<KhuyenMaiSanPham> findAllActive();
    KhuyenMaiSanPham findById(Integer id);
    KhuyenMaiSanPham findActiveById(Integer id);
    KhuyenMaiSanPham save(KhuyenMaiSanPham khuyenMaiSanPham);
    void deleteById(Integer id);
    void softDeleteById(Integer id);
} 