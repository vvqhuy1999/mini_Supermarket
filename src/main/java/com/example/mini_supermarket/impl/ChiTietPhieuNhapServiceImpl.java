package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.ChiTietPhieuNhapRepository;
import com.example.mini_supermarket.entity.ChiTietPhieuNhap;
import com.example.mini_supermarket.service.ChiTietPhieuNhapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChiTietPhieuNhapServiceImpl implements ChiTietPhieuNhapService {

    @Autowired
    private ChiTietPhieuNhapRepository chiTietPhieuNhapRepository;

    @Override
    public List<ChiTietPhieuNhap> findAll() {
        return chiTietPhieuNhapRepository.findAll();
    }

    @Override
    public ChiTietPhieuNhap findById(Integer id) {
        Optional<ChiTietPhieuNhap> chiTietPhieuNhap = chiTietPhieuNhapRepository.findById(id);
        if (chiTietPhieuNhap.isPresent()) {
            return chiTietPhieuNhap.get();
        } else {
            throw new RuntimeException("Không tìm thấy chi tiết phiếu nhập có id: " + id);
        }
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
    public ChiTietPhieuNhap update(ChiTietPhieuNhap chiTietPhieuNhap) {
        if (chiTietPhieuNhapRepository.existsById(chiTietPhieuNhap.getMaCTPN())) {
            return chiTietPhieuNhapRepository.save(chiTietPhieuNhap);
        } else {
            throw new RuntimeException("Không tìm thấy chi tiết phiếu nhập có id: " + chiTietPhieuNhap.getMaCTPN());
        }
    }
} 