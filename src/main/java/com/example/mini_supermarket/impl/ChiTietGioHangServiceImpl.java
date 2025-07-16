package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.ChiTietGioHangRepository;
import com.example.mini_supermarket.entity.ChiTietGioHang;
import com.example.mini_supermarket.service.ChiTietGioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChiTietGioHangServiceImpl implements ChiTietGioHangService {
    private ChiTietGioHangRepository chiTietGioHangRepository;

    @Autowired
    public ChiTietGioHangServiceImpl(ChiTietGioHangRepository chiTietGioHangRepository) {
        this.chiTietGioHangRepository = chiTietGioHangRepository;
    }

    @Override
    public List<ChiTietGioHang> findAll() {
        return chiTietGioHangRepository.findAll();
    }

    @Override
    public ChiTietGioHang findById(Integer theId) {
        Optional<ChiTietGioHang> result = chiTietGioHangRepository.findById(theId);
        ChiTietGioHang theChiTietGioHang = null;

        if (result.isPresent()) {
            theChiTietGioHang = result.get();
        } else {
            throw new RuntimeException("Did not find ChiTietGioHang id - " + theId);
        }
        return theChiTietGioHang;
    }

    @Override
    public ChiTietGioHang save(ChiTietGioHang theChiTietGioHang) {
        return chiTietGioHangRepository.save(theChiTietGioHang);
    }

    @Override
    public void deleteById(Integer theId) {
        chiTietGioHangRepository.deleteById(theId);
    }

    @Override
    public ChiTietGioHang update(ChiTietGioHang chiTietGioHang) {
        Optional<ChiTietGioHang> existingChiTietGioHang = chiTietGioHangRepository.findById(chiTietGioHang.getMaCTGH());

        if (!existingChiTietGioHang.isPresent()) {
            throw new RuntimeException("Không tìm thấy chi tiết giỏ hàng với ID - " + chiTietGioHang.getMaCTGH());
        }

        return chiTietGioHangRepository.save(chiTietGioHang);
    }
} 