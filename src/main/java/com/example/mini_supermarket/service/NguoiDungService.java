package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.NguoiDung;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NguoiDungService {
    List<NguoiDung> findAll();

    NguoiDung findById(String id);

    NguoiDung save(NguoiDung nguoiDung);

    void deleteById(String id);

    NguoiDung update(NguoiDung nguoiDung);
} 