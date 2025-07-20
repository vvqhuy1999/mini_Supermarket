package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.ChiTietHoaDon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChiTietHoaDonService {
    List<ChiTietHoaDon> findAll();
    List<ChiTietHoaDon> findAllActive();
    ChiTietHoaDon findById(Integer id);
    ChiTietHoaDon findActiveById(Integer id);
    ChiTietHoaDon save(ChiTietHoaDon chiTietHoaDon);
    void deleteById(Integer id);
    void softDeleteById(Integer id);
} 