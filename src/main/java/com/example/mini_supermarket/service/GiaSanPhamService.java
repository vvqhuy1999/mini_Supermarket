package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.GiaSanPham;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GiaSanPhamService {
    List<GiaSanPham> findAll();

    GiaSanPham findById(Integer id);

    GiaSanPham save(GiaSanPham giaSanPham);

    void deleteById(Integer id);

    GiaSanPham update(GiaSanPham giaSanPham);
} 