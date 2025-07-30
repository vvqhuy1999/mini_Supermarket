package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.PhieuNhapHangRepository;
import com.example.mini_supermarket.entity.PhieuNhapHang;
import com.example.mini_supermarket.service.PhieuNhapHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhieuNhapHangServiceImpl implements PhieuNhapHangService {

    @Autowired
    private PhieuNhapHangRepository phieuNhapHangRepository;

    @Override
    public List<PhieuNhapHang> findAll() {
        return phieuNhapHangRepository.findAll();
    }

    @Override
    public List<PhieuNhapHang> findAllActive() {
        return phieuNhapHangRepository.findAllActive();
    }

    @Override
    public PhieuNhapHang findById(Integer id) {
        return phieuNhapHangRepository.findById(id).orElse(null);
    }

    @Override
    public PhieuNhapHang findActiveById(Integer id) {
        return phieuNhapHangRepository.findActiveById(id).orElse(null);
    }

    @Override
    public PhieuNhapHang save(PhieuNhapHang phieuNhapHang) {
        return phieuNhapHangRepository.save(phieuNhapHang);
    }

    @Override
    public void deleteById(Integer id) {
        phieuNhapHangRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        PhieuNhapHang phieuNhapHang = findById(id);
        if (phieuNhapHang != null) {
            phieuNhapHang.setIsDeleted(true);
            phieuNhapHangRepository.save(phieuNhapHang);
        }
    }
} 
