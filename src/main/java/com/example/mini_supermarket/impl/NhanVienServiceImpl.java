package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.NhanVienRepository;
import com.example.mini_supermarket.entity.NhanVien;
import com.example.mini_supermarket.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NhanVienServiceImpl implements NhanVienService {
    private NhanVienRepository nhanVienRepository;

    @Autowired
    public NhanVienServiceImpl(NhanVienRepository nhanVienRepository) {
        this.nhanVienRepository = nhanVienRepository;
    }

    @Override
    public List<NhanVien> findAll() {
        return nhanVienRepository.findAll();
    }

    @Override
    public NhanVien findById(String theId) {
        Optional<NhanVien> result = nhanVienRepository.findById(theId);
        NhanVien theNhanVien = null;

        if (result.isPresent()) {
            theNhanVien = result.get();
        } else {
            throw new RuntimeException("Did not find NhanVien id - " + theId);
        }
        return theNhanVien;
    }

    @Override
    public NhanVien save(NhanVien theNhanVien) {
        return nhanVienRepository.save(theNhanVien);
    }

    @Override
    public void deleteById(String theId) {
        nhanVienRepository.deleteById(theId);
    }

    @Override
    public NhanVien update(NhanVien nhanVien) {
        Optional<NhanVien> existingNhanVien = nhanVienRepository.findById(nhanVien.getMaNV());

        if (!existingNhanVien.isPresent()) {
            throw new RuntimeException("Không tìm thấy nhân viên với ID - " + nhanVien.getMaNV());
        }

        return nhanVienRepository.save(nhanVien);
    }
} 