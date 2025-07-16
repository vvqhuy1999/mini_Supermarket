package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.PhieuXuatKhoRepository;
import com.example.mini_supermarket.entity.PhieuXuatKho;
import com.example.mini_supermarket.service.PhieuXuatKhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhieuXuatKhoServiceImpl implements PhieuXuatKhoService {

    @Autowired
    private PhieuXuatKhoRepository phieuXuatKhoRepository;

    @Override
    public List<PhieuXuatKho> findAll() {
        return phieuXuatKhoRepository.findAll();
    }

    @Override
    public PhieuXuatKho findById(Integer id) {
        Optional<PhieuXuatKho> phieuXuatKho = phieuXuatKhoRepository.findById(id);
        if (phieuXuatKho.isPresent()) {
            return phieuXuatKho.get();
        } else {
            throw new RuntimeException("Không tìm thấy phiếu xuất kho có id: " + id);
        }
    }

    @Override
    public PhieuXuatKho save(PhieuXuatKho phieuXuatKho) {
        return phieuXuatKhoRepository.save(phieuXuatKho);
    }

    @Override
    public void deleteById(Integer id) {
        phieuXuatKhoRepository.deleteById(id);
    }

    @Override
    public PhieuXuatKho update(PhieuXuatKho phieuXuatKho) {
        if (phieuXuatKhoRepository.existsById(phieuXuatKho.getMaPXK())) {
            return phieuXuatKhoRepository.save(phieuXuatKho);
        } else {
            throw new RuntimeException("Không tìm thấy phiếu xuất kho có id: " + phieuXuatKho.getMaPXK());
        }
    }
} 