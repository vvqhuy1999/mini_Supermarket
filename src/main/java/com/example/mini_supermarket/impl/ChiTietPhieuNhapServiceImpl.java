package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.ChiTietPhieuNhapRepository;
import com.example.mini_supermarket.entity.ChiTietPhieuNhap;
import com.example.mini_supermarket.service.ChiTietPhieuNhapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChiTietPhieuNhapServiceImpl implements ChiTietPhieuNhapService {

    @Autowired
    private ChiTietPhieuNhapRepository chiTietPhieuNhapRepository;

    @Override
    public List<ChiTietPhieuNhap> findAll() {
        return chiTietPhieuNhapRepository.findAll();
    }

    @Override
    public List<ChiTietPhieuNhap> findAllActive() {
        return chiTietPhieuNhapRepository.findAllActive();
    }

    @Override
    public ChiTietPhieuNhap findById(Integer id) {
        return chiTietPhieuNhapRepository.findById(id).orElse(null);
    }

    @Override
    public ChiTietPhieuNhap findActiveById(Integer id) {
        return chiTietPhieuNhapRepository.findActiveById(id).orElse(null);
    }

    @Override
    public ChiTietPhieuNhap save(ChiTietPhieuNhap chiTietPhieuNhap) {
        return chiTietPhieuNhapRepository.save(chiTietPhieuNhap);
    }

    @Override
    public void deleteById(Integer id) {
        chiTietPhieuNhapRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        ChiTietPhieuNhap chiTietPhieuNhap = findById(id);
        if (chiTietPhieuNhap != null) {
            chiTietPhieuNhap.setIsDeleted(true);
            chiTietPhieuNhapRepository.save(chiTietPhieuNhap);
        }
    }
} 