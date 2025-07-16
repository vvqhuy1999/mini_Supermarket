package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.NhanVien;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NhanVienService {
    List<NhanVien> findAll();

    NhanVien findById(String id);

    NhanVien save(NhanVien nhanVien);

    void deleteById(String id);

    NhanVien update(NhanVien nhanVien);
} 