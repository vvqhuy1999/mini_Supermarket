package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.KhuyenMaiSanPhamRepository;
import com.example.mini_supermarket.entity.KhuyenMaiSanPham;
import com.example.mini_supermarket.service.KhuyenMaiSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KhuyenMaiSanPhamServiceImpl implements KhuyenMaiSanPhamService {

    @Autowired
    private KhuyenMaiSanPhamRepository khuyenMaiSanPhamRepository;

    @Override
    public List<KhuyenMaiSanPham> findAll() {
        return khuyenMaiSanPhamRepository.findAll();
    }

    @Override
    public KhuyenMaiSanPham findById(Integer id) {
        Optional<KhuyenMaiSanPham> khuyenMaiSanPham = khuyenMaiSanPhamRepository.findById(id);
        if (khuyenMaiSanPham.isPresent()) {
            return khuyenMaiSanPham.get();
        } else {
            throw new RuntimeException("Không tìm thấy khuyến mãi sản phẩm có id: " + id);
        }
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
    public KhuyenMaiSanPham update(KhuyenMaiSanPham khuyenMaiSanPham) {
        if (khuyenMaiSanPhamRepository.existsById(khuyenMaiSanPham.getMaKMSP())) {
            return khuyenMaiSanPhamRepository.save(khuyenMaiSanPham);
        } else {
            throw new RuntimeException("Không tìm thấy khuyến mãi sản phẩm có id: " + khuyenMaiSanPham.getMaKMSP());
        }
    }
} 