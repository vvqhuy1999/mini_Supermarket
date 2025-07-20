package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.KhuyenMaiKhachHangRepository;
import com.example.mini_supermarket.entity.KhuyenMaiKhachHang;
import com.example.mini_supermarket.service.KhuyenMaiKhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KhuyenMaiKhachHangServiceImpl implements KhuyenMaiKhachHangService {

    @Autowired
    private KhuyenMaiKhachHangRepository khuyenMaiKhachHangRepository;

    @Override
    public List<KhuyenMaiKhachHang> findAll() {
        return khuyenMaiKhachHangRepository.findAll();
    }

    @Override
    public List<KhuyenMaiKhachHang> findAllActive() {
        return khuyenMaiKhachHangRepository.findAllActive();
    }

    @Override
    public KhuyenMaiKhachHang findById(Integer id) {
        return khuyenMaiKhachHangRepository.findById(id).orElse(null);
    }

    @Override
    public KhuyenMaiKhachHang findActiveById(Integer id) {
        return khuyenMaiKhachHangRepository.findActiveById(id).orElse(null);
    }

    @Override
    public KhuyenMaiKhachHang save(KhuyenMaiKhachHang khuyenMaiKhachHang) {
        return khuyenMaiKhachHangRepository.save(khuyenMaiKhachHang);
    }

    @Override
    public void deleteById(Integer id) {
        khuyenMaiKhachHangRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        KhuyenMaiKhachHang khuyenMaiKhachHang = findById(id);
        if (khuyenMaiKhachHang != null) {
            khuyenMaiKhachHang.setIsDeleted(true);
            khuyenMaiKhachHangRepository.save(khuyenMaiKhachHang);
        }
    }
} 