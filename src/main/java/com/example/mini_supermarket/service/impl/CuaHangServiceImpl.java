package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.CuaHangRepository;
import com.example.mini_supermarket.entity.CuaHang;
import com.example.mini_supermarket.service.CuaHangService;
import com.example.mini_supermarket.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CuaHangServiceImpl implements CuaHangService {
    private CuaHangRepository cuaHangRepository;

    @Autowired
    public CuaHangServiceImpl(CuaHangRepository cuaHangRepository) {
        this.cuaHangRepository = cuaHangRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuaHang> findAll() {
        return cuaHangRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public CuaHang findById(String theId) {
        return cuaHangRepository.findActiveById(theId).orElse(null);
    }

    @Override
    @Transactional
    public CuaHang save(CuaHang theCuaHang) {
        // Tự động generate mã cửa hàng nếu chưa có
        if (theCuaHang.getMaCH() == null || theCuaHang.getMaCH().trim().isEmpty()) {
            String maCH = generateMaCuaHang();
            theCuaHang.setMaCH(maCH);
        }
        
        // Đặt giá trị mặc định
        if (theCuaHang.getIsDeleted() == null) {
            theCuaHang.setIsDeleted(false);
        }
        
        return cuaHangRepository.save(theCuaHang);
    }
    
    /**
     * Tạo mã cửa hàng tự động
     * @return Mã cửa hàng duy nhất
     */
    private String generateMaCuaHang() {
        String maCH;
        
        // Lặp để đảm bảo mã không trùng
        do {
            maCH = CodeGenerator.generateMaCuaHang();
        } while (cuaHangRepository.existsByMaCH(maCH));
        
        return maCH;
    }

    @Override
    @Transactional
    public void deleteById(String theId) {
        cuaHangRepository.deleteById(theId);
    }

    @Override
    @Transactional
    public CuaHang update(CuaHang cuaHang) {
        Optional<CuaHang> existingCuaHang = cuaHangRepository.findActiveById(cuaHang.getMaCH());

        if (!existingCuaHang.isPresent()) {
            throw new RuntimeException("Không tìm thấy cửa hàng với ID - " + cuaHang.getMaCH());
        }

        return cuaHangRepository.save(cuaHang);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuaHang> findAllActive() {
        return cuaHangRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public CuaHang findActiveById(String id) {
        Optional<CuaHang> result = cuaHangRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public void softDeleteById(String id) {
        Optional<CuaHang> cuaHangOpt = cuaHangRepository.findActiveById(id);
        if (cuaHangOpt.isPresent()) {
            CuaHang cuaHang = cuaHangOpt.get();
            cuaHang.setIsDeleted(true);
            cuaHangRepository.save(cuaHang);
        }
    }
} 
