package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.LoaiSanPhamRepository;
import com.example.mini_supermarket.entity.LoaiSanPham;
import com.example.mini_supermarket.service.LoaiSanPhamService;
import com.example.mini_supermarket.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LoaiSanPhamServiceImpl implements LoaiSanPhamService {

    @Autowired
    private LoaiSanPhamRepository loaiSanPhamRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LoaiSanPham> findAll() {
        return loaiSanPhamRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public LoaiSanPham findById(String id) {
        return loaiSanPhamRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public LoaiSanPham save(LoaiSanPham loaiSanPham) {
        // Tự động generate mã loại sản phẩm nếu chưa có
        if (loaiSanPham.getMaLoaiSP() == null || loaiSanPham.getMaLoaiSP().trim().isEmpty()) {
            String maLoaiSP = generateMaLoaiSanPham();
            loaiSanPham.setMaLoaiSP(maLoaiSP);
        }
        
        // Đặt giá trị mặc định
        if (loaiSanPham.getIsDeleted() == null) {
            loaiSanPham.setIsDeleted(false);
        }
        
        return loaiSanPhamRepository.save(loaiSanPham);
    }
    
    /**
     * Tạo mã loại sản phẩm tự động
     * @return Mã loại sản phẩm duy nhất
     */
    private String generateMaLoaiSanPham() {
        String maLoaiSP;
        
        // Lặp để đảm bảo mã không trùng
        do {
            maLoaiSP = CodeGenerator.generateMaLoaiSanPham();
        } while (loaiSanPhamRepository.existsByMaLoaiSP(maLoaiSP));
        
        return maLoaiSP;
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        loaiSanPhamRepository.deleteById(id);
    }

    @Override
    @Transactional
    public LoaiSanPham update(LoaiSanPham loaiSanPham) {
        Optional<LoaiSanPham> existingLoaiSanPham = loaiSanPhamRepository.findActiveById(loaiSanPham.getMaLoaiSP());
        if (existingLoaiSanPham.isPresent()) {
            return loaiSanPhamRepository.save(loaiSanPham);
        } else {
            throw new RuntimeException("Không tìm thấy loại sản phẩm có id: " + loaiSanPham.getMaLoaiSP());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoaiSanPham> findAllActive() {
        return loaiSanPhamRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public LoaiSanPham findActiveById(String id) {
        Optional<LoaiSanPham> result = loaiSanPhamRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public void softDeleteById(String id) {
        Optional<LoaiSanPham> loaiSanPhamOpt = loaiSanPhamRepository.findActiveById(id);
        if (loaiSanPhamOpt.isPresent()) {
            LoaiSanPham loaiSanPham = loaiSanPhamOpt.get();
            loaiSanPham.setIsDeleted(true);
            loaiSanPhamRepository.save(loaiSanPham);
        }
    }
} 
