package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.NhanVienRepository;
import com.example.mini_supermarket.entity.NhanVien;
import com.example.mini_supermarket.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new RuntimeException("Did not find NhanVien id - " + theId);
        }
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

    @Override
    public List<NhanVien> findAllActive() {
        return nhanVienRepository.findAllActive();
    }

    @Override
    public NhanVien findActiveById(String id) {
        Optional<NhanVien> result = nhanVienRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    public void softDeleteById(String id) {
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findActiveById(id);
        if (nhanVienOpt.isPresent()) {
            NhanVien nhanVien = nhanVienOpt.get();
            nhanVien.setIsDeleted(true);
            nhanVienRepository.save(nhanVien);
        }
    }

    @Override
    public boolean existsByMaNVAndNgaySinh(String maNV, LocalDate ngaySinh) {
        return nhanVienRepository.existsByMaNVAndNgaySinh(maNV, ngaySinh);
    }


}
