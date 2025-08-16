package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.SanPhamRepository;
import com.example.mini_supermarket.entity.SanPham;
import com.example.mini_supermarket.dto.SanPhamOptimizedDto;
import com.example.mini_supermarket.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SanPhamServiceImpl implements SanPhamService {
    private SanPhamRepository sanPhamRepository;

    @Autowired
    public SanPhamServiceImpl(SanPhamRepository sanPhamRepository) {
        this.sanPhamRepository = sanPhamRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanPham> findAll() {
        return sanPhamRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanPham> findAllActive() {
        return sanPhamRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public SanPham findById(String theId) {
        Optional<SanPham> result = sanPhamRepository.findById(theId);
        SanPham theSanPham = null;

        if (result.isPresent()) {
            theSanPham = result.get();
        } else {
            throw new RuntimeException("Did not find SanPham id - " + theId);
        }
        return theSanPham;
    }

    @Override
    @Transactional(readOnly = true)
    public SanPham findActiveById(String theId) {
        Optional<SanPham> result = sanPhamRepository.findActiveById(theId);
        SanPham theSanPham = null;

        if (result.isPresent()) {
            theSanPham = result.get();
        } else {
            throw new RuntimeException("Did not find active SanPham id - " + theId);
        }
        return theSanPham;
    }

    @Override
    @Transactional
    public SanPham save(SanPham theSanPham) {
        return sanPhamRepository.save(theSanPham);
    }

    @Override
    @Transactional
    public void deleteById(String theId) {
        sanPhamRepository.deleteById(theId);
    }

    @Override
    @Transactional
    public void softDeleteById(String theId) {
        Optional<SanPham> result = sanPhamRepository.findByIdIncludeDeleted(theId);
        
        if (result.isPresent()) {
            SanPham sanPham = result.get();
            sanPham.setIsDeleted(true);
            sanPhamRepository.save(sanPham);
        } else {
            throw new RuntimeException("Did not find SanPham id - " + theId);
        }
    }

    @Override
    @Transactional
    public SanPham update(SanPham sanPham) {
        Optional<SanPham> existingSanPham = sanPhamRepository.findById(sanPham.getMaSP());

        if (!existingSanPham.isPresent()) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID - " + sanPham.getMaSP());
        }

        return sanPhamRepository.save(sanPham);
    }
    
    // === IMPLEMENTATION CHO METHODS TỐI ƯU - SỬ DỤNG DTO ===
    
    @Override
    @Transactional(readOnly = true)
    public List<SanPhamOptimizedDto> findAllActiveOptimized() {
        return sanPhamRepository.findAllActiveOptimized();
    }
    
    @Override
    @Transactional(readOnly = true)
    public SanPhamOptimizedDto findActiveByIdOptimized(String id) {
        Optional<SanPhamOptimizedDto> result = sanPhamRepository.findActiveByIdOptimized(id);
        SanPhamOptimizedDto theSanPham = null;

        if (result.isPresent()) {
            theSanPham = result.get();
        } else {
            throw new RuntimeException("Did not find active SanPham id - " + id);
        }
        return theSanPham;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SanPhamOptimizedDto> findByCategoryOptimized(String maLoaiSP) {
        return sanPhamRepository.findByCategoryOptimized(maLoaiSP);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SanPhamOptimizedDto> findByCategoryAndActiveOptimized(String maLoaiSP) {
        return sanPhamRepository.findByCategoryAndActiveOptimized(maLoaiSP);
    }
} 
