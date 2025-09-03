package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.PhieuXuatKhoRepository;
import com.example.mini_supermarket.entity.PhieuXuatKho;
import com.example.mini_supermarket.service.PhieuXuatKhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PhieuXuatKhoServiceImpl implements PhieuXuatKhoService {

    @Autowired
    private PhieuXuatKhoRepository phieuXuatKhoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PhieuXuatKho> findAll() {
        return phieuXuatKhoRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhieuXuatKho> findAllActive() {
        return phieuXuatKhoRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public PhieuXuatKho findById(Integer id) {
        return phieuXuatKhoRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public PhieuXuatKho findActiveById(Integer id) {
        return phieuXuatKhoRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public PhieuXuatKho save(PhieuXuatKho phieuXuatKho) {
        return phieuXuatKhoRepository.save(phieuXuatKho);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        phieuXuatKhoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        PhieuXuatKho phieuXuatKho = findActiveById(id);
        if (phieuXuatKho != null) {
            phieuXuatKho.setIsDeleted(true);
            phieuXuatKhoRepository.save(phieuXuatKho);
        }
    }
} 
