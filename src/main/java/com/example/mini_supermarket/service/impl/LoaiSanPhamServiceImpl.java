package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.LoaiSanPhamRepository;
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

    @Override
    public List<LoaiSanPham> findAllActive() {
        return loaiSanPhamRepository.findAllActive();
    }

    @Override
    public LoaiSanPham findActiveById(String id) {
        Optional<LoaiSanPham> result = loaiSanPhamRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    public String generateMaLoaiSanPham() {
        // Sử dụng random alphanumeric cho 7 ký tự
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        // Tạo loop để đảm bảo mã không trùng
        String newCode;
        int maxAttempts = 100; // Giới hạn số lần thử
        int attempts = 0;
        
        do {
            StringBuilder randomPart = new StringBuilder();
            for (int i = 0; i < 7; i++) {
                int index = (int) (Math.random() * characters.length());
                randomPart.append(characters.charAt(index));
            }
            newCode = "LSP" + randomPart.toString();
            attempts++;
        } while (loaiSanPhamRepository.findById(newCode).isPresent() && attempts < maxAttempts);
        
        if (attempts >= maxAttempts) {
            // Fallback: sử dụng timestamp nếu không tìm được mã unique
            long timestamp = System.currentTimeMillis();
            String timestampStr = String.valueOf(timestamp);
            String suffix = timestampStr.substring(Math.max(0, timestampStr.length() - 7));
            newCode = "LSP" + suffix;
        }
        
        return newCode;
    }

    @Override
    public void softDeleteById(String id) {
        Optional<LoaiSanPham> loaiSanPhamOpt = loaiSanPhamRepository.findActiveById(id);
        if (loaiSanPhamOpt.isPresent()) {
            LoaiSanPham loaiSanPham = loaiSanPhamOpt.get();
            loaiSanPham.setIsDeleted(true);
            loaiSanPhamRepository.save(loaiSanPham);
        }
    }
} 