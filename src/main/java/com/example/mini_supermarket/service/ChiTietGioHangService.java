package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.ChiTietGioHang;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChiTietGioHangService {
    List<ChiTietGioHang> findAll();

    ChiTietGioHang findById(Integer id);

    ChiTietGioHang save(ChiTietGioHang chiTietGioHang);

    void deleteById(Integer id);

    ChiTietGioHang update(ChiTietGioHang chiTietGioHang);
} 