package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.KhuyenMaiKhachHangRepository;
import com.example.mini_supermarket.entity.KhuyenMaiKhachHang;
import com.example.mini_supermarket.service.KhuyenMaiKhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KhuyenMaiKhachHangServiceImpl implements KhuyenMaiKhachHangService {

    @Autowired
    private KhuyenMaiKhachHangRepository khuyenMaiKhachHangRepository;

    @Override
    @Transactional(readOnly = true)
    public List<KhuyenMaiKhachHang> findAll() {
        return khuyenMaiKhachHangRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhuyenMaiKhachHang> findAllActive() {
        return khuyenMaiKhachHangRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public KhuyenMaiKhachHang findById(Integer id) {
        return khuyenMaiKhachHangRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public KhuyenMaiKhachHang findActiveById(Integer id) {
        return khuyenMaiKhachHangRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public KhuyenMaiKhachHang save(KhuyenMaiKhachHang khuyenMaiKhachHang) {
        return khuyenMaiKhachHangRepository.save(khuyenMaiKhachHang);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        khuyenMaiKhachHangRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        KhuyenMaiKhachHang khuyenMaiKhachHang = findActiveById(id);
        if (khuyenMaiKhachHang != null) {
            khuyenMaiKhachHang.setIsDeleted(true);
            khuyenMaiKhachHangRepository.save(khuyenMaiKhachHang);
        }
    }
} 
