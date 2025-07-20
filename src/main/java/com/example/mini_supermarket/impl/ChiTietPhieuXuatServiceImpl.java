package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.ChiTietPhieuXuatRepository;
import com.example.mini_supermarket.entity.ChiTietPhieuXuat;
import com.example.mini_supermarket.service.ChiTietPhieuXuatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChiTietPhieuXuatServiceImpl implements ChiTietPhieuXuatService {

    @Autowired
    private ChiTietPhieuXuatRepository chiTietPhieuXuatRepository;

    @Override
    public List<ChiTietPhieuXuat> findAll() {
        return chiTietPhieuXuatRepository.findAll();
    }

    @Override
    public List<ChiTietPhieuXuat> findAllActive() {
        return chiTietPhieuXuatRepository.findAllActive();
    }

    @Override
    public ChiTietPhieuXuat findById(Integer id) {
        return chiTietPhieuXuatRepository.findById(id).orElse(null);
    }

    @Override
    public ChiTietPhieuXuat findActiveById(Integer id) {
        return chiTietPhieuXuatRepository.findActiveById(id).orElse(null);
    }

    @Override
    public ChiTietPhieuXuat save(ChiTietPhieuXuat chiTietPhieuXuat) {
        return chiTietPhieuXuatRepository.save(chiTietPhieuXuat);
    }

    @Override
    public void deleteById(Integer id) {
        chiTietPhieuXuatRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        ChiTietPhieuXuat chiTietPhieuXuat = findById(id);
        if (chiTietPhieuXuat != null) {
            chiTietPhieuXuat.setIsDeleted(true);
            chiTietPhieuXuatRepository.save(chiTietPhieuXuat);
        }
    }
} 