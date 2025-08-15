package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.entity.GioHang;
import com.example.mini_supermarket.repository.GioHangRepository;
import com.example.mini_supermarket.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GioHangServiceImpl implements GioHangService {

    @Autowired
    private GioHangRepository gioHangRepository;

    @Override
    public List<GioHang> findAll() {
        return gioHangRepository.findAll();
    }

    @Override
    public List<GioHang> findAllActive() {
        return gioHangRepository.findAllActive();
    }

    @Override
    public GioHang findById(Integer id) {
        Optional<GioHang> gioHang = gioHangRepository.findByIdIncludeDeleted(id);
        return gioHang.orElse(null);
    }

    @Override
    public GioHang findActiveById(Integer id) {
        Optional<GioHang> gioHang = gioHangRepository.findActiveById(id);
        return gioHang.orElse(null);
    }

    @Override
    public GioHang save(GioHang gioHang) {
        if (gioHang.getNgayTao() == null) {
            gioHang.setNgayTao(LocalDateTime.now());
        }
        if (gioHang.getTrangThai() == null) {
            gioHang.setTrangThai(0);
        }
        if (gioHang.getIsDeleted() == null) {
            gioHang.setIsDeleted(false);
        }
        return gioHangRepository.save(gioHang);
    }

    @Override
    public void deleteById(Integer id) {
        gioHangRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        Optional<GioHang> gioHangOpt = gioHangRepository.findById(id);
        if (gioHangOpt.isPresent()) {
            GioHang gioHang = gioHangOpt.get();
            gioHang.setIsDeleted(true);
            gioHang.setNgayCapNhat(LocalDateTime.now());
            gioHangRepository.save(gioHang);
        }
    }

    @Override
    public GioHang update(GioHang gioHang) {
        if (gioHang.getMaGH() != null) {
            Optional<GioHang> existingGioHang = gioHangRepository.findById(gioHang.getMaGH());
            if (existingGioHang.isPresent()) {
                gioHang.setNgayCapNhat(LocalDateTime.now());
                if (gioHang.getNgayTao() == null) {
                    gioHang.setNgayTao(existingGioHang.get().getNgayTao());
                }
                if (gioHang.getIsDeleted() == null) {
                    gioHang.setIsDeleted(false);
                }
                return gioHangRepository.save(gioHang);
            }
        }
        return null;
    }
}