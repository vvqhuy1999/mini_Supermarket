package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.PhieuNhapHang;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PhieuNhapHangService {
    List<PhieuNhapHang> findAll();
    List<PhieuNhapHang> findAllActive();
    PhieuNhapHang findById(Integer id);
    PhieuNhapHang findActiveById(Integer id);
    PhieuNhapHang save(PhieuNhapHang phieuNhapHang);
    void deleteById(Integer id);
    void softDeleteById(Integer id);
} 