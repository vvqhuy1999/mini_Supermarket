package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.GioHangRepository;
import com.example.mini_supermarket.entity.GioHang;
import com.example.mini_supermarket.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GioHangServiceImpl implements GioHangService {
    private GioHangRepository gioHangRepository;

    @Autowired
    public GioHangServiceImpl(GioHangRepository gioHangRepository) {
        this.gioHangRepository = gioHangRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GioHang> findAll() {
        return gioHangRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public GioHang findById(Integer theId) {
        return gioHangRepository.findActiveById(theId).orElse(null);
    }

    @Override
    @Transactional
    public GioHang save(GioHang theGioHang) {
        return gioHangRepository.save(theGioHang);
    }

    @Override
    @Transactional
    public void deleteById(Integer theId) {
        gioHangRepository.deleteById(theId);
    }

    @Override
    @Transactional
    public GioHang update(GioHang gioHang) {
        Optional<GioHang> existingGioHang = gioHangRepository.findActiveById(gioHang.getMaGH());

        if (!existingGioHang.isPresent()) {
            throw new RuntimeException("Không tìm thấy giỏ hàng với ID - " + gioHang.getMaGH());
        }

        return gioHangRepository.save(gioHang);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GioHang> findAllActive() {
        return gioHangRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public GioHang findActiveById(Integer id) {
        Optional<GioHang> result = gioHangRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        Optional<GioHang> gioHangOpt = gioHangRepository.findActiveById(id);
        if (gioHangOpt.isPresent()) {
            GioHang gioHang = gioHangOpt.get();
            gioHang.setIsDeleted(true);
            gioHangRepository.save(gioHang);
        }
    }
} 
