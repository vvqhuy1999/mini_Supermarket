package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.PhieuNhapHangRepository;
import com.example.mini_supermarket.entity.PhieuNhapHang;
import com.example.mini_supermarket.service.PhieuNhapHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhieuNhapHangServiceImpl implements PhieuNhapHangService {

    @Autowired
    private PhieuNhapHangRepository phieuNhapHangRepository;

    @Override
    public List<PhieuNhapHang> findAll() {
        return phieuNhapHangRepository.findAll();
    }

    @Override
    public PhieuNhapHang findById(Integer id) {
        Optional<PhieuNhapHang> phieuNhapHang = phieuNhapHangRepository.findById(id);
        if (phieuNhapHang.isPresent()) {
            return phieuNhapHang.get();
        } else {
            throw new RuntimeException("Không tìm thấy phiếu nhập hàng có id: " + id);
        }
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
    public PhieuNhapHang update(PhieuNhapHang phieuNhapHang) {
        if (phieuNhapHangRepository.existsById(phieuNhapHang.getMaPN())) {
            return phieuNhapHangRepository.save(phieuNhapHang);
        } else {
            throw new RuntimeException("Không tìm thấy phiếu nhập hàng có id: " + phieuNhapHang.getMaPN());
        }
    }
} 