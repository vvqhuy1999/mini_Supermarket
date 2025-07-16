package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.GioHang;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GioHangService {
    List<GioHang> findAll();

    GioHang findById(Integer id);

    GioHang save(GioHang gioHang);

    void deleteById(Integer id);

    GioHang update(GioHang gioHang);
} 