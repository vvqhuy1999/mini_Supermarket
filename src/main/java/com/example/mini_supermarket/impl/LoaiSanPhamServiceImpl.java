package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.LoaiSanPhamRepository;
import com.example.mini_supermarket.entity.LoaiSanPham;
import com.example.mini_supermarket.service.LoaiSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoaiSanPhamServiceImpl implements LoaiSanPhamService {

    @Autowired
    private LoaiSanPhamRepository loaiSanPhamRepository;

    @Override
    public List<LoaiSanPham> findAll() {
        return loaiSanPhamRepository.findAll();
    }

    @Override
    public LoaiSanPham findById(String id) {
        Optional<LoaiSanPham> loaiSanPham = loaiSanPhamRepository.findById(id);
        if (loaiSanPham.isPresent()) {
            return loaiSanPham.get();
        } else {
            throw new RuntimeException("Không tìm thấy loại sản phẩm có id: " + id);
        }
    }

    @Override
    public LoaiSanPham save(LoaiSanPham loaiSanPham) {
        return loaiSanPhamRepository.save(loaiSanPham);
    }

    @Override
    public void deleteById(String id) {
        loaiSanPhamRepository.deleteById(id);
    }

    @Override
    public LoaiSanPham update(LoaiSanPham loaiSanPham) {
        if (loaiSanPhamRepository.existsById(loaiSanPham.getMaLoaiSP())) {
            return loaiSanPhamRepository.save(loaiSanPham);
        } else {
            throw new RuntimeException("Không tìm thấy loại sản phẩm có id: " + loaiSanPham.getMaLoaiSP());
        }
    }
} 