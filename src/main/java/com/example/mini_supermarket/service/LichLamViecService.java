package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.LichLamViec;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LichLamViecService {
    List<LichLamViec> findAll();

    LichLamViec findById(Integer id);

    LichLamViec save(LichLamViec lichLamViec);

    void deleteById(Integer id);

    LichLamViec update(LichLamViec lichLamViec);
} 