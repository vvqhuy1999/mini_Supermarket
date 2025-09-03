package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.GiaSanPhamRepository;
import com.example.mini_supermarket.entity.GiaSanPham;
import com.example.mini_supermarket.service.GiaSanPhamService;
import com.example.mini_supermarket.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GiaSanPhamServiceImpl implements GiaSanPhamService {

    @Autowired
    private GiaSanPhamRepository giaSanPhamRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GiaSanPham> findAll() {
        return giaSanPhamRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiaSanPham> findAllActive() {
        return giaSanPhamRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public GiaSanPham findById(Integer id) {
        return giaSanPhamRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public GiaSanPham findActiveById(Integer id) {
        return giaSanPhamRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public GiaSanPham save(GiaSanPham giaSanPham) {
        // Đặt giá trị mặc định
        if (giaSanPham.getIsDeleted() == null) {
            giaSanPham.setIsDeleted(false);
        }
        
        // MaGia được tự động generate bởi @GeneratedValue
        return giaSanPhamRepository.save(giaSanPham);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        giaSanPhamRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        GiaSanPham giaSanPham = findActiveById(id);
        if (giaSanPham != null) {
            giaSanPham.setIsDeleted(true);
            giaSanPhamRepository.save(giaSanPham);
        }
    }
} 
