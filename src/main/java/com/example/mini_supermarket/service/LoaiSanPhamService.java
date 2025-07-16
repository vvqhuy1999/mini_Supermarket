package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.LoaiSanPham;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LoaiSanPhamService {
    List<LoaiSanPham> findAll();

    LoaiSanPham findById(String id);

    LoaiSanPham save(LoaiSanPham loaiSanPham);

    void deleteById(String id);

    LoaiSanPham update(LoaiSanPham loaiSanPham);
} 