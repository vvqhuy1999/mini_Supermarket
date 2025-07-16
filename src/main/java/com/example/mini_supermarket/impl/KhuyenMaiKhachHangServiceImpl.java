package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.KhuyenMaiKhachHangRepository;
import com.example.mini_supermarket.entity.KhuyenMaiKhachHang;
import com.example.mini_supermarket.service.KhuyenMaiKhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KhuyenMaiKhachHangServiceImpl implements KhuyenMaiKhachHangService {
    private KhuyenMaiKhachHangRepository khuyenMaiKhachHangRepository;

    @Autowired
    public KhuyenMaiKhachHangServiceImpl(KhuyenMaiKhachHangRepository khuyenMaiKhachHangRepository) {
        this.khuyenMaiKhachHangRepository = khuyenMaiKhachHangRepository;
    }

    @Override
    public List<KhuyenMaiKhachHang> findAll() {
        return khuyenMaiKhachHangRepository.findAll();
    }

    @Override
    public KhuyenMaiKhachHang findById(Integer theId) {
        Optional<KhuyenMaiKhachHang> result = khuyenMaiKhachHangRepository.findById(theId);
        KhuyenMaiKhachHang theKhuyenMaiKhachHang = null;

        if (result.isPresent()) {
            theKhuyenMaiKhachHang = result.get();
        } else {
            throw new RuntimeException("Did not find KhuyenMaiKhachHang id - " + theId);
        }
        return theKhuyenMaiKhachHang;
    }

    @Override
    public KhuyenMaiKhachHang save(KhuyenMaiKhachHang theKhuyenMaiKhachHang) {
        return khuyenMaiKhachHangRepository.save(theKhuyenMaiKhachHang);
    }

    @Override
    public void deleteById(Integer theId) {
        khuyenMaiKhachHangRepository.deleteById(theId);
    }

    @Override
    public KhuyenMaiKhachHang update(KhuyenMaiKhachHang khuyenMaiKhachHang) {
        Optional<KhuyenMaiKhachHang> existingKhuyenMaiKhachHang = khuyenMaiKhachHangRepository.findById(khuyenMaiKhachHang.getMaKMKH());

        if (!existingKhuyenMaiKhachHang.isPresent()) {
            throw new RuntimeException("Không tìm thấy khuyến mãi khách hàng với ID - " + khuyenMaiKhachHang.getMaKMKH());
        }

        return khuyenMaiKhachHangRepository.save(khuyenMaiKhachHang);
    }
} 