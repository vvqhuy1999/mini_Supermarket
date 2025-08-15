package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.CuaHangRepository;
import com.example.mini_supermarket.entity.CuaHang;
import com.example.mini_supermarket.service.CuaHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<CuaHang> findAll() {
        return cuaHangRepository.findAll();
    }

    @Override
    public CuaHang findById(String theId) {
        Optional<CuaHang> result = cuaHangRepository.findById(theId);
        return result.orElse(null); // Trả về null thay vì throw exception
    }

    @Override
    public CuaHang save(CuaHang theCuaHang) {
        return cuaHangRepository.save(theCuaHang);
    }

    @Override
    public void deleteById(String theId) {
        cuaHangRepository.deleteById(theId);
    }

    @Override
    public String generateMaCuaHang() {
        // Sử dụng UUID hoặc random alphanumeric cho 8 ký tự
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder("CH");
        
        // Tạo loop để đảm bảo mã không trùng
        String newCode;
        int maxAttempts = 100; // Giới hạn số lần thử
        int attempts = 0;
        
        do {
            StringBuilder randomPart = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                int index = (int) (Math.random() * characters.length());
                randomPart.append(characters.charAt(index));
            }
            newCode = "CH" + randomPart.toString();
            attempts++;
        } while (cuaHangRepository.findById(newCode).isPresent() && attempts < maxAttempts);
        
        if (attempts >= maxAttempts) {
            // Fallback: sử dụng timestamp nếu không tìm được mã unique
            long timestamp = System.currentTimeMillis();
            String timestampStr = String.valueOf(timestamp);
            String suffix = timestampStr.substring(timestampStr.length() - 8);
            newCode = "CH" + suffix;
        }
        
        return newCode;
    }

    @Override
    public CuaHang update(CuaHang cuaHang) {
        Optional<CuaHang> existingCuaHang = cuaHangRepository.findById(cuaHang.getMaCH());

        if (!existingCuaHang.isPresent()) {
            throw new RuntimeException("Không tìm thấy cửa hàng với ID - " + cuaHang.getMaCH());
        }

        return cuaHangRepository.save(cuaHang);
    }

    @Override
    public List<CuaHang> findAllActive() {
        return cuaHangRepository.findAllActive();
    }

    @Override
    public CuaHang findActiveById(String id) {
        Optional<CuaHang> result = cuaHangRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    public void softDeleteById(String id) {
        Optional<CuaHang> cuaHangOpt = cuaHangRepository.findActiveById(id);
        if (cuaHangOpt.isPresent()) {
            CuaHang cuaHang = cuaHangOpt.get();
            cuaHang.setIsDeleted(true);
            cuaHangRepository.save(cuaHang);
        }
    }
}