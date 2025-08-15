package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.ChiTietPhieuXuatRepository;
import com.example.mini_supermarket.entity.ChiTietPhieuXuat;
import com.example.mini_supermarket.service.ChiTietPhieuXuatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChiTietPhieuXuatServiceImpl implements ChiTietPhieuXuatService {

    @Autowired
    private ChiTietPhieuXuatRepository chiTietPhieuXuatRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChiTietPhieuXuat> findAll() {
        return chiTietPhieuXuatRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChiTietPhieuXuat> findAllActive() {
        return chiTietPhieuXuatRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietPhieuXuat findById(Integer id) {
        return chiTietPhieuXuatRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietPhieuXuat findActiveById(Integer id) {
        return chiTietPhieuXuatRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public ChiTietPhieuXuat save(ChiTietPhieuXuat chiTietPhieuXuat) {
        return chiTietPhieuXuatRepository.save(chiTietPhieuXuat);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        chiTietPhieuXuatRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        ChiTietPhieuXuat chiTietPhieuXuat = findActiveById(id);
        if (chiTietPhieuXuat != null) {
            chiTietPhieuXuat.setIsDeleted(true);
            chiTietPhieuXuatRepository.save(chiTietPhieuXuat);
        }
    }
} 
