package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.TonKhoChiTiet;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TonKhoChiTietService {
    List<TonKhoChiTiet> findAll();
    List<TonKhoChiTiet> findAllActive();
    TonKhoChiTiet findById(Integer id);
    TonKhoChiTiet findActiveById(Integer id);
    TonKhoChiTiet save(TonKhoChiTiet tonKhoChiTiet);
    void deleteById(Integer id);
    void softDeleteById(Integer id);
} 