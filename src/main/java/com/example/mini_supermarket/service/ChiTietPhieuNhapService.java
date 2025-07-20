package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.ChiTietPhieuNhap;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChiTietPhieuNhapService {
    List<ChiTietPhieuNhap> findAll();
    List<ChiTietPhieuNhap> findAllActive();
    ChiTietPhieuNhap findById(Integer id);
    ChiTietPhieuNhap findActiveById(Integer id);
    ChiTietPhieuNhap save(ChiTietPhieuNhap chiTietPhieuNhap);
    void deleteById(Integer id);
    void softDeleteById(Integer id);
} 