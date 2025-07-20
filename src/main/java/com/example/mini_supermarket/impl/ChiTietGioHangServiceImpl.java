package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.ChiTietGioHangRepository;
import com.example.mini_supermarket.entity.ChiTietGioHang;
import com.example.mini_supermarket.service.ChiTietGioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChiTietGioHangServiceImpl implements ChiTietGioHangService {

    @Autowired
    private ChiTietGioHangRepository chiTietGioHangRepository;

    @Override
    public List<ChiTietGioHang> findAll() {
        return chiTietGioHangRepository.findAll();
    }

    @Override
    public List<ChiTietGioHang> findAllActive() {
        return chiTietGioHangRepository.findAllActive();
    }

    @Override
    public ChiTietGioHang findById(Integer id) {
        return chiTietGioHangRepository.findById(id).orElse(null);
    }

    @Override
    public ChiTietGioHang findActiveById(Integer id) {
        return chiTietGioHangRepository.findActiveById(id).orElse(null);
    }

    @Override
    public ChiTietGioHang save(ChiTietGioHang chiTietGioHang) {
        return chiTietGioHangRepository.save(chiTietGioHang);
    }

    @Override
    public void deleteById(Integer id) {
        chiTietGioHangRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        ChiTietGioHang chiTietGioHang = findById(id);
        if (chiTietGioHang != null) {
            chiTietGioHang.setIsDeleted(true);
            chiTietGioHangRepository.save(chiTietGioHang);
        }
    }
} 