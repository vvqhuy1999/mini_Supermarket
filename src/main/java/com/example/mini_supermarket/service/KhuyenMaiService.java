package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.KhuyenMai;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface KhuyenMaiService {
    List<KhuyenMai> findAll();

    KhuyenMai findById(String id);

    KhuyenMai save(KhuyenMai khuyenMai);

    void deleteById(String id);

    KhuyenMai update(KhuyenMai khuyenMai);
} 