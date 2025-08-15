package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.ChiTietGioHangRepository;
import com.example.mini_supermarket.entity.ChiTietGioHang;
import com.example.mini_supermarket.service.ChiTietGioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChiTietGioHangServiceImpl implements ChiTietGioHangService {

    @Autowired
    private ChiTietGioHangRepository chiTietGioHangRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChiTietGioHang> findAll() {
        return chiTietGioHangRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChiTietGioHang> findAllActive() {
        return chiTietGioHangRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietGioHang findById(Integer id) {
        return chiTietGioHangRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietGioHang findActiveById(Integer id) {
        return chiTietGioHangRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public ChiTietGioHang save(ChiTietGioHang chiTietGioHang) {
        return chiTietGioHangRepository.save(chiTietGioHang);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        chiTietGioHangRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        ChiTietGioHang chiTietGioHang = findActiveById(id);
        if (chiTietGioHang != null) {
            chiTietGioHang.setIsDeleted(true);
            chiTietGioHangRepository.save(chiTietGioHang);
        }
    }
} 
