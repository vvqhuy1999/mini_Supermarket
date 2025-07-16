package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.ChiTietPhieuNhap;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChiTietPhieuNhapService {
    List<ChiTietPhieuNhap> findAll();

    ChiTietPhieuNhap findById(Integer id);

    ChiTietPhieuNhap save(ChiTietPhieuNhap chiTietPhieuNhap);

    void deleteById(Integer id);

    ChiTietPhieuNhap update(ChiTietPhieuNhap chiTietPhieuNhap);
} 