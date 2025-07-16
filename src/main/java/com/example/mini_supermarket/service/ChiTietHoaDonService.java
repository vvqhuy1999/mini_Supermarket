package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.ChiTietHoaDon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChiTietHoaDonService {
    List<ChiTietHoaDon> findAll();

    ChiTietHoaDon findById(Integer id);

    ChiTietHoaDon save(ChiTietHoaDon chiTietHoaDon);

    void deleteById(Integer id);

    ChiTietHoaDon update(ChiTietHoaDon chiTietHoaDon);
} 