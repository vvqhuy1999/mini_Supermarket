package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.PhuongThucThanhToan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PhuongThucThanhToanService {
    List<PhuongThucThanhToan> findAll();

    PhuongThucThanhToan findById(String id);

    PhuongThucThanhToan save(PhuongThucThanhToan phuongThucThanhToan);

    void deleteById(String id);

    PhuongThucThanhToan update(PhuongThucThanhToan phuongThucThanhToan);
} 