package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.PhieuNhapHangRepository;
import com.example.mini_supermarket.entity.PhieuNhapHang;
import com.example.mini_supermarket.service.PhieuNhapHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PhieuNhapHangServiceImpl implements PhieuNhapHangService {

    @Autowired
    private PhieuNhapHangRepository phieuNhapHangRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PhieuNhapHang> findAll() {
        return phieuNhapHangRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhieuNhapHang> findAllActive() {
        return phieuNhapHangRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public PhieuNhapHang findById(Integer id) {
        return phieuNhapHangRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public PhieuNhapHang findActiveById(Integer id) {
        return phieuNhapHangRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public PhieuNhapHang save(PhieuNhapHang phieuNhapHang) {
        return phieuNhapHangRepository.save(phieuNhapHang);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        phieuNhapHangRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        PhieuNhapHang phieuNhapHang = findActiveById(id);
        if (phieuNhapHang != null) {
            phieuNhapHang.setIsDeleted(true);
            phieuNhapHangRepository.save(phieuNhapHang);
        }
    }
} 
