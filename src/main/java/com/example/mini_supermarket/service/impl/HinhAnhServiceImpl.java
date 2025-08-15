package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.HinhAnhRepository;
import com.example.mini_supermarket.entity.HinhAnh;
import com.example.mini_supermarket.service.HinhAnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HinhAnhServiceImpl implements HinhAnhService {

    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HinhAnh> findAll() {
        return hinhAnhRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public HinhAnh findById(Integer id) {
        return hinhAnhRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public HinhAnh save(HinhAnh hinhAnh) {
        return hinhAnhRepository.save(hinhAnh);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        hinhAnhRepository.deleteById(id);
    }

    @Override
    @Transactional
    public HinhAnh update(HinhAnh hinhAnh) {
        Optional<HinhAnh> existingHinhAnh = hinhAnhRepository.findActiveById(hinhAnh.getMaHinh());
        if (existingHinhAnh.isPresent()) {
            return hinhAnhRepository.save(hinhAnh);
        } else {
            throw new RuntimeException("Không tìm thấy hình ảnh có id: " + hinhAnh.getMaHinh());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<HinhAnh> findAllActive() {
        return hinhAnhRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public HinhAnh findActiveById(Integer id) {
        Optional<HinhAnh> result = hinhAnhRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        Optional<HinhAnh> hinhAnhOpt = hinhAnhRepository.findActiveById(id);
        if (hinhAnhOpt.isPresent()) {
            HinhAnh hinhAnh = hinhAnhOpt.get();
            hinhAnh.setIsDeleted(true);
            hinhAnhRepository.save(hinhAnh);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HinhAnh> findBySanPhamAndNotDeleted(com.example.mini_supermarket.entity.SanPham sanPham) {
        return hinhAnhRepository.findBySanPhamAndIsDeletedFalse(sanPham);
    }
} 
