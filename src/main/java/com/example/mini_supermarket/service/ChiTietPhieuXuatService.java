package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.ChiTietPhieuXuat;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChiTietPhieuXuatService {
    List<ChiTietPhieuXuat> findAll();

    ChiTietPhieuXuat findById(Integer id);

    ChiTietPhieuXuat save(ChiTietPhieuXuat chiTietPhieuXuat);

    void deleteById(Integer id);

    ChiTietPhieuXuat update(ChiTietPhieuXuat chiTietPhieuXuat);
} 