package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.CaLamViec;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CaLamViecService {
    List<CaLamViec> findAll();
    List<CaLamViec> findAllActive();
    CaLamViec findById(Integer id);
    CaLamViec findActiveById(Integer id);
    CaLamViec save(CaLamViec caLamViec);
    void deleteById(Integer id);
    void softDeleteById(Integer id);
} 