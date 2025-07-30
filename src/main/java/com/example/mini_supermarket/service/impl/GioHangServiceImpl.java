package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.GioHangRepository;
import com.example.mini_supermarket.entity.GioHang;
import com.example.mini_supermarket.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<GioHang> findAll() {
        return gioHangRepository.findAll();
    }

    @Override
    public GioHang findById(Integer theId) {
        Optional<GioHang> result = gioHangRepository.findById(theId);
        GioHang theGioHang = null;

        if (result.isPresent()) {
            theGioHang = result.get();
        } else {
            throw new RuntimeException("Did not find GioHang id - " + theId);
        }
        return theGioHang;
    }

    @Override
    public GioHang save(GioHang theGioHang) {
        return gioHangRepository.save(theGioHang);
    }

    @Override
    public void deleteById(Integer theId) {
        gioHangRepository.deleteById(theId);
    }

    @Override
    public GioHang update(GioHang gioHang) {
        Optional<GioHang> existingGioHang = gioHangRepository.findById(gioHang.getMaGH());

        if (!existingGioHang.isPresent()) {
            throw new RuntimeException("Không tìm thấy giỏ hàng với ID - " + gioHang.getMaGH());
        }

        return gioHangRepository.save(gioHang);
    }

    @Override
    public List<GioHang> findAllActive() {
        return gioHangRepository.findAllActive();
    }

    @Override
    public GioHang findActiveById(Integer id) {
        Optional<GioHang> result = gioHangRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    public void softDeleteById(Integer id) {
        Optional<GioHang> gioHangOpt = gioHangRepository.findActiveById(id);
        if (gioHangOpt.isPresent()) {
            GioHang gioHang = gioHangOpt.get();
            gioHang.setIsDeleted(true);
            gioHangRepository.save(gioHang);
        }
    }
} 
