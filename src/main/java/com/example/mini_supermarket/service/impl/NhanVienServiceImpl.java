package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.NhanVienRepository;
import com.example.mini_supermarket.entity.NhanVien;
import com.example.mini_supermarket.service.NhanVienService;
import com.example.mini_supermarket.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public List<NhanVien> findAll() {
        return nhanVienRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional
    public NhanVien save(NhanVien theNhanVien) {
        // Tự động generate mã nhân viên nếu chưa có
        if (theNhanVien.getMaNV() == null || theNhanVien.getMaNV().trim().isEmpty()) {
            String maNV = generateMaNhanVien();
            theNhanVien.setMaNV(maNV);
        }
        
        // Đặt giá trị mặc định
        if (theNhanVien.getIsDeleted() == null) {
            theNhanVien.setIsDeleted(false);
        }
        
        return nhanVienRepository.save(theNhanVien);
    }
    
    /**
     * Tạo mã nhân viên tự động
     * @return Mã nhân viên duy nhất
     */
    private String generateMaNhanVien() {
        String maNV;
        
        // Lặp để đảm bảo mã không trùng
        do {
            maNV = CodeGenerator.generateMaNhanVien();
        } while (nhanVienRepository.existsByMaNV(maNV));
        
        return maNV;
    }

    @Override
    @Transactional
    public void deleteById(String theId) {
        nhanVienRepository.deleteById(theId);
    }

    @Override
    @Transactional
    public NhanVien update(NhanVien nhanVien) {
        Optional<NhanVien> existingNhanVien = nhanVienRepository.findById(nhanVien.getMaNV());

        if (!existingNhanVien.isPresent()) {
            throw new RuntimeException("Không tìm thấy nhân viên với ID - " + nhanVien.getMaNV());
        }

        return nhanVienRepository.save(nhanVien);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NhanVien> findAllActive() {
        return nhanVienRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public NhanVien findActiveById(String id) {
        Optional<NhanVien> result = nhanVienRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public void softDeleteById(String id) {
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findActiveById(id);
        if (nhanVienOpt.isPresent()) {
            NhanVien nhanVien = nhanVienOpt.get();
            nhanVien.setIsDeleted(true);
            nhanVienRepository.save(nhanVien);
        }
    }
} 
