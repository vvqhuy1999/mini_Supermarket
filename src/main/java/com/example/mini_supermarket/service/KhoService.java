package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.Kho;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface KhoService {
    List<Kho> findAll();

    Kho findById(Integer id);

    Kho save(Kho kho);

    void deleteById(Integer id);

    Kho update(Kho kho);
} 