package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.KhuyenMaiKhachHang;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface KhuyenMaiKhachHangService {
    List<KhuyenMaiKhachHang> findAll();
    List<KhuyenMaiKhachHang> findAllActive();
    KhuyenMaiKhachHang findById(Integer id);
    KhuyenMaiKhachHang findActiveById(Integer id);
    KhuyenMaiKhachHang save(KhuyenMaiKhachHang khuyenMaiKhachHang);
    void deleteById(Integer id);
    void softDeleteById(Integer id);
} 