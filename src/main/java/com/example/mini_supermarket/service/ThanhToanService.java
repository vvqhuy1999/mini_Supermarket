package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.ThanhToan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ThanhToanService {
    List<ThanhToan> findAll();

    ThanhToan findById(Integer id);

    ThanhToan save(ThanhToan thanhToan);

    void deleteById(Integer id);

    ThanhToan update(ThanhToan thanhToan);
} 