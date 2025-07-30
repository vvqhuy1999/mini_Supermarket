package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.ChiTietHoaDonRepository;
import com.example.mini_supermarket.entity.ChiTietHoaDon;
import com.example.mini_supermarket.service.ChiTietHoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChiTietHoaDonServiceImpl implements ChiTietHoaDonService {

    @Autowired
    private ChiTietHoaDonRepository chiTietHoaDonRepository;

    @Override
    public List<ChiTietHoaDon> findAll() {
        return chiTietHoaDonRepository.findAll();
    }

    @Override
    public List<ChiTietHoaDon> findAllActive() {
        return chiTietHoaDonRepository.findAllActive();
    }

    @Override
    public ChiTietHoaDon findById(Integer id) {
        return chiTietHoaDonRepository.findById(id).orElse(null);
    }

    @Override
    public ChiTietHoaDon findActiveById(Integer id) {
        return chiTietHoaDonRepository.findActiveById(id).orElse(null);
    }

    @Override
    public ChiTietHoaDon save(ChiTietHoaDon chiTietHoaDon) {
        return chiTietHoaDonRepository.save(chiTietHoaDon);
    }

    @Override
    public void deleteById(Integer id) {
        chiTietHoaDonRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        ChiTietHoaDon chiTietHoaDon = findById(id);
        if (chiTietHoaDon != null) {
            chiTietHoaDon.setIsDeleted(true);
            chiTietHoaDonRepository.save(chiTietHoaDon);
        }
    }
} 
