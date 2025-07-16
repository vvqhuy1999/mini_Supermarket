package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.GiaSanPhamRepository;
import com.example.mini_supermarket.entity.GiaSanPham;
import com.example.mini_supermarket.service.GiaSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GiaSanPhamServiceImpl implements GiaSanPhamService {

    @Autowired
    private GiaSanPhamRepository giaSanPhamRepository;

    @Override
    public List<GiaSanPham> findAll() {
        return giaSanPhamRepository.findAll();
    }

    @Override
    public GiaSanPham findById(Integer id) {
        Optional<GiaSanPham> giaSanPham = giaSanPhamRepository.findById(id);
        if (giaSanPham.isPresent()) {
            return giaSanPham.get();
        } else {
            throw new RuntimeException("Không tìm thấy giá sản phẩm có id: " + id);
        }
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
    public GiaSanPham update(GiaSanPham giaSanPham) {
        if (giaSanPhamRepository.existsById(giaSanPham.getMaGia())) {
            return giaSanPhamRepository.save(giaSanPham);
        } else {
            throw new RuntimeException("Không tìm thấy giá sản phẩm có id: " + giaSanPham.getMaGia());
        }
    }
} 