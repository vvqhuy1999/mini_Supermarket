package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.PhieuXuatKhoRepository;
import com.example.mini_supermarket.entity.PhieuXuatKho;
import com.example.mini_supermarket.service.PhieuXuatKhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhieuXuatKhoServiceImpl implements PhieuXuatKhoService {

    @Autowired
    private PhieuXuatKhoRepository phieuXuatKhoRepository;

    @Override
    public List<PhieuXuatKho> findAll() {
        return phieuXuatKhoRepository.findAll();
    }

    @Override
    public List<PhieuXuatKho> findAllActive() {
        return phieuXuatKhoRepository.findAllActive();
    }

    @Override
    public PhieuXuatKho findById(Integer id) {
        return phieuXuatKhoRepository.findById(id).orElse(null);
    }

    @Override
    public PhieuXuatKho findActiveById(Integer id) {
        return phieuXuatKhoRepository.findActiveById(id).orElse(null);
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
    public void softDeleteById(Integer id) {
        PhieuXuatKho phieuXuatKho = findById(id);
        if (phieuXuatKho != null) {
            phieuXuatKho.setIsDeleted(true);
            phieuXuatKhoRepository.save(phieuXuatKho);
        }
    }
} 