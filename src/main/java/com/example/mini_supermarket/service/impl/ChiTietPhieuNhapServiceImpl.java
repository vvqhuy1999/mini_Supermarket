package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.ChiTietPhieuNhapRepository;
import com.example.mini_supermarket.entity.ChiTietPhieuNhap;
import com.example.mini_supermarket.service.ChiTietPhieuNhapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChiTietPhieuNhapServiceImpl implements ChiTietPhieuNhapService {

    @Autowired
    private ChiTietPhieuNhapRepository chiTietPhieuNhapRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChiTietPhieuNhap> findAll() {
        return chiTietPhieuNhapRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChiTietPhieuNhap> findAllActive() {
        return chiTietPhieuNhapRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietPhieuNhap findById(Integer id) {
        return chiTietPhieuNhapRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietPhieuNhap findActiveById(Integer id) {
        return chiTietPhieuNhapRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public ChiTietPhieuNhap save(ChiTietPhieuNhap chiTietPhieuNhap) {
        return chiTietPhieuNhapRepository.save(chiTietPhieuNhap);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        chiTietPhieuNhapRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        ChiTietPhieuNhap chiTietPhieuNhap = findActiveById(id);
        if (chiTietPhieuNhap != null) {
            chiTietPhieuNhap.setIsDeleted(true);
            chiTietPhieuNhapRepository.save(chiTietPhieuNhap);
        }
    }
} 
