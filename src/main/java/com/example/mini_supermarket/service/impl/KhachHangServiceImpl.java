package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.KhachHangRepository;
import com.example.mini_supermarket.entity.KhachHang;
import com.example.mini_supermarket.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KhachHangServiceImpl implements KhachHangService {
    private KhachHangRepository khachHangRepository;

    @Autowired
    public KhachHangServiceImpl(KhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }

    @Override
    public List<KhachHang> findAll() {
        return khachHangRepository.findAll();
    }

    @Override
    public List<KhachHang> findAllActive() {
        return khachHangRepository.findAllActive();
    }

    @Override
    public KhachHang findById(String theId) {
        Optional<KhachHang> result = khachHangRepository.findById(theId);
        KhachHang theKhachHang = null;

        if (result.isPresent()) {
            theKhachHang = result.get();
        } else {
            throw new RuntimeException("Did not find KhachHang id - " + theId);
        }
        return theKhachHang;
    }

    @Override
    public KhachHang findActiveById(String theId) {
        Optional<KhachHang> result = khachHangRepository.findActiveById(theId);
        KhachHang theKhachHang = null;

        if (result.isPresent()) {
            theKhachHang = result.get();
        } else {
            throw new RuntimeException("Did not find active KhachHang id - " + theId);
        }
        return theKhachHang;
    }

    @Override
    public KhachHang save(KhachHang theKhachHang) {
        return khachHangRepository.save(theKhachHang);
    }

    @Override
    public void deleteById(String theId) {
        khachHangRepository.deleteById(theId);
    }

    @Override
    public void softDeleteById(String theId) {
        Optional<KhachHang> result = khachHangRepository.findByIdIncludeDeleted(theId);
        
        if (result.isPresent()) {
            KhachHang khachHang = result.get();
            khachHang.setIsDeleted(true);
            khachHangRepository.save(khachHang);
        } else {
            throw new RuntimeException("Did not find KhachHang id - " + theId);
        }
    }

    @Override
    public KhachHang update(KhachHang khachHang) {
        Optional<KhachHang> existingKhachHang = khachHangRepository.findById(khachHang.getMaKH());

        if (!existingKhachHang.isPresent()) {
            throw new RuntimeException("Không tìm thấy khách hàng với ID - " + khachHang.getMaKH());
        }

        return khachHangRepository.save(khachHang);
    }
} 
