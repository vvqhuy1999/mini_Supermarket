package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.KhuyenMaiSanPhamRepository;
import com.example.mini_supermarket.entity.KhuyenMaiSanPham;
import com.example.mini_supermarket.service.KhuyenMaiSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KhuyenMaiSanPhamServiceImpl implements KhuyenMaiSanPhamService {

    @Autowired
    private KhuyenMaiSanPhamRepository khuyenMaiSanPhamRepository;

    @Override
    public List<KhuyenMaiSanPham> findAll() {
        return khuyenMaiSanPhamRepository.findAll();
    }

    @Override
    public List<KhuyenMaiSanPham> findAllActive() {
        return khuyenMaiSanPhamRepository.findAllActive();
    }

    @Override
    public KhuyenMaiSanPham findById(Integer id) {
        return khuyenMaiSanPhamRepository.findById(id).orElse(null);
    }

    @Override
    public KhuyenMaiSanPham findActiveById(Integer id) {
        return khuyenMaiSanPhamRepository.findActiveById(id).orElse(null);
    }

    @Override
    public KhuyenMaiSanPham save(KhuyenMaiSanPham khuyenMaiSanPham) {
        return khuyenMaiSanPhamRepository.save(khuyenMaiSanPham);
    }

    @Override
    public void deleteById(Integer id) {
        khuyenMaiSanPhamRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        KhuyenMaiSanPham khuyenMaiSanPham = findById(id);
        if (khuyenMaiSanPham != null) {
            khuyenMaiSanPham.setIsDeleted(true);
            khuyenMaiSanPhamRepository.save(khuyenMaiSanPham);
        }
    }
} 
