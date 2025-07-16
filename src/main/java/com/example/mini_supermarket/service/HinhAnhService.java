package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.HinhAnh;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HinhAnhService {
    List<HinhAnh> findAll();

    HinhAnh findById(Integer id);

    HinhAnh save(HinhAnh hinhAnh);

    void deleteById(Integer id);

    HinhAnh update(HinhAnh hinhAnh);
} 