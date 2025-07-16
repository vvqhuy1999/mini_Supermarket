package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.PhieuNhapHang;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PhieuNhapHangService {
    List<PhieuNhapHang> findAll();

    PhieuNhapHang findById(Integer id);

    PhieuNhapHang save(PhieuNhapHang phieuNhapHang);

    void deleteById(Integer id);

    PhieuNhapHang update(PhieuNhapHang phieuNhapHang);
} 