package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.KhuyenMaiSanPhamRepository;
import com.example.mini_supermarket.entity.KhuyenMaiSanPham;
import com.example.mini_supermarket.service.KhuyenMaiSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KhuyenMaiSanPhamServiceImpl implements KhuyenMaiSanPhamService {

    @Autowired
    private KhuyenMaiSanPhamRepository khuyenMaiSanPhamRepository;

    @Override
    @Transactional(readOnly = true)
    public List<KhuyenMaiSanPham> findAll() {
        return khuyenMaiSanPhamRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhuyenMaiSanPham> findAllActive() {
        return khuyenMaiSanPhamRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public KhuyenMaiSanPham findById(Integer id) {
        return khuyenMaiSanPhamRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public KhuyenMaiSanPham findActiveById(Integer id) {
        return khuyenMaiSanPhamRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public KhuyenMaiSanPham save(KhuyenMaiSanPham khuyenMaiSanPham) {
        return khuyenMaiSanPhamRepository.save(khuyenMaiSanPham);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        khuyenMaiSanPhamRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        KhuyenMaiSanPham khuyenMaiSanPham = findActiveById(id);
        if (khuyenMaiSanPham != null) {
            khuyenMaiSanPham.setIsDeleted(true);
            khuyenMaiSanPhamRepository.save(khuyenMaiSanPham);
        }
    }
} 
