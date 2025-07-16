package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.HoaDon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HoaDonService {
    List<HoaDon> findAll();

    HoaDon findById(Integer id);

    HoaDon save(HoaDon hoaDon);

    void deleteById(Integer id);

    HoaDon update(HoaDon hoaDon);
} 