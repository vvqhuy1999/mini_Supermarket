package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.PhieuXuatKho;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PhieuXuatKhoService {
    List<PhieuXuatKho> findAll();
    List<PhieuXuatKho> findAllActive();
    PhieuXuatKho findById(Integer id);
    PhieuXuatKho findActiveById(Integer id);
    PhieuXuatKho save(PhieuXuatKho phieuXuatKho);
    void deleteById(Integer id);
    void softDeleteById(Integer id);
} 