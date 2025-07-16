package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.CuaHang;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CuaHangService {
    List<CuaHang> findAll();

    CuaHang findById(String id);

    CuaHang save(CuaHang cuaHang);

    void deleteById(String id);

    CuaHang update(CuaHang cuaHang);
} 