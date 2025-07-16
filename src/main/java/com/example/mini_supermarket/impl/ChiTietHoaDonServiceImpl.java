package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.ChiTietHoaDonRepository;
import com.example.mini_supermarket.entity.ChiTietHoaDon;
import com.example.mini_supermarket.service.ChiTietHoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChiTietHoaDonServiceImpl implements ChiTietHoaDonService {
    private ChiTietHoaDonRepository chiTietHoaDonRepository;

    @Autowired
    public ChiTietHoaDonServiceImpl(ChiTietHoaDonRepository chiTietHoaDonRepository) {
        this.chiTietHoaDonRepository = chiTietHoaDonRepository;
    }

    @Override
    public List<ChiTietHoaDon> findAll() {
        return chiTietHoaDonRepository.findAll();
    }

    @Override
    public ChiTietHoaDon findById(Integer theId) {
        Optional<ChiTietHoaDon> result = chiTietHoaDonRepository.findById(theId);
        ChiTietHoaDon theChiTietHoaDon = null;

        if (result.isPresent()) {
            theChiTietHoaDon = result.get();
        } else {
            throw new RuntimeException("Did not find ChiTietHoaDon id - " + theId);
        }
        return theChiTietHoaDon;
    }

    @Override
    public ChiTietHoaDon save(ChiTietHoaDon theChiTietHoaDon) {
        return chiTietHoaDonRepository.save(theChiTietHoaDon);
    }

    @Override
    public void deleteById(Integer theId) {
        chiTietHoaDonRepository.deleteById(theId);
    }

    @Override
    public ChiTietHoaDon update(ChiTietHoaDon chiTietHoaDon) {
        Optional<ChiTietHoaDon> existingChiTietHoaDon = chiTietHoaDonRepository.findById(chiTietHoaDon.getMaCTHD());

        if (!existingChiTietHoaDon.isPresent()) {
            throw new RuntimeException("Không tìm thấy chi tiết hóa đơn với ID - " + chiTietHoaDon.getMaCTHD());
        }

        return chiTietHoaDonRepository.save(chiTietHoaDon);
    }
} 