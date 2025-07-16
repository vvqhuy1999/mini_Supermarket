package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.NhaCungCap;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NhaCungCapService {
    List<NhaCungCap> findAll();

    NhaCungCap findById(String id);

    NhaCungCap save(NhaCungCap nhaCungCap);

    void deleteById(String id);

    NhaCungCap update(NhaCungCap nhaCungCap);
} 