package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.GiaSanPhamRepository;
import com.example.mini_supermarket.entity.GiaSanPham;
import com.example.mini_supermarket.service.GiaSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiaSanPhamServiceImpl implements GiaSanPhamService {

    @Autowired
    private GiaSanPhamRepository giaSanPhamRepository;

    @Override
    public List<GiaSanPham> findAll() {
        return giaSanPhamRepository.findAll();
    }

    @Override
    public List<GiaSanPham> findAllActive() {
        return giaSanPhamRepository.findAllActive();
    }

    @Override
    public GiaSanPham findById(Integer id) {
        return giaSanPhamRepository.findById(id).orElse(null);
    }

    @Override
    public GiaSanPham findActiveById(Integer id) {
        return giaSanPhamRepository.findActiveById(id).orElse(null);
    }

    @Override
    public GiaSanPham save(GiaSanPham giaSanPham) {
        return giaSanPhamRepository.save(giaSanPham);
    }

    @Override
    public void deleteById(Integer id) {
        giaSanPhamRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        GiaSanPham giaSanPham = findById(id);
        if (giaSanPham != null) {
            giaSanPham.setIsDeleted(true);
            giaSanPhamRepository.save(giaSanPham);
        }
    }
} 
