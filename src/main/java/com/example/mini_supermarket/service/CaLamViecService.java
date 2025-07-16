package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.CaLamViec;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CaLamViecService {
    List<CaLamViec> findAll();

    CaLamViec findById(Integer id);

    CaLamViec save(CaLamViec caLamViec);

    void deleteById(Integer id);

    CaLamViec update(CaLamViec caLamViec);
} 