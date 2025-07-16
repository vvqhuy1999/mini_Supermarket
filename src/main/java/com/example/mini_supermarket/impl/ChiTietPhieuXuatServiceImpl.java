package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.ChiTietPhieuXuatRepository;
import com.example.mini_supermarket.entity.ChiTietPhieuXuat;
import com.example.mini_supermarket.service.ChiTietPhieuXuatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChiTietPhieuXuatServiceImpl implements ChiTietPhieuXuatService {

    @Autowired
    private ChiTietPhieuXuatRepository chiTietPhieuXuatRepository;

    @Override
    public List<ChiTietPhieuXuat> findAll() {
        return chiTietPhieuXuatRepository.findAll();
    }

    @Override
    public ChiTietPhieuXuat findById(Integer id) {
        Optional<ChiTietPhieuXuat> chiTietPhieuXuat = chiTietPhieuXuatRepository.findById(id);
        if (chiTietPhieuXuat.isPresent()) {
            return chiTietPhieuXuat.get();
        } else {
            throw new RuntimeException("Không tìm thấy chi tiết phiếu xuất có id: " + id);
        }
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
    public ChiTietPhieuXuat update(ChiTietPhieuXuat chiTietPhieuXuat) {
        if (chiTietPhieuXuatRepository.existsById(chiTietPhieuXuat.getMaCTPXK())) {
            return chiTietPhieuXuatRepository.save(chiTietPhieuXuat);
        } else {
            throw new RuntimeException("Không tìm thấy chi tiết phiếu xuất có id: " + chiTietPhieuXuat.getMaCTPXK());
        }
    }
} 