package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.BangLuong;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BangLuongService {
    List<BangLuong> findAll();
    List<BangLuong> findAllActive();
    BangLuong findById(Integer id);
    BangLuong findActiveById(Integer id);
    BangLuong save(BangLuong bangLuong);
    void deleteById(Integer id);
    void softDeleteById(Integer id);
} 