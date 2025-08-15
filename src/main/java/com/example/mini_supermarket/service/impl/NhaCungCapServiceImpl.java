package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.NhaCungCapRepository;
import com.example.mini_supermarket.entity.NhaCungCap;
import com.example.mini_supermarket.service.NhaCungCapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NhaCungCapServiceImpl implements NhaCungCapService {
    private NhaCungCapRepository nhaCungCapRepository;

    @Autowired
    public NhaCungCapServiceImpl(NhaCungCapRepository nhaCungCapRepository) {
        this.nhaCungCapRepository = nhaCungCapRepository;
    }

    @Override
    public List<NhaCungCap> findAll() {
        return nhaCungCapRepository.findAll();
    }

    @Override
    public NhaCungCap findById(String theId) {
        Optional<NhaCungCap> result = nhaCungCapRepository.findById(theId);
        NhaCungCap theNhaCungCap = null;

        if (result.isPresent()) {
            theNhaCungCap = result.get();
        } else {
            throw new RuntimeException("Did not find NhaCungCap id - " + theId);
        }
        return theNhaCungCap;
    }

    @Override
    public NhaCungCap save(NhaCungCap theNhaCungCap) {
        return nhaCungCapRepository.save(theNhaCungCap);
    }

    @Override
    public void deleteById(String theId) {
        nhaCungCapRepository.deleteById(theId);
    }

    @Override
    public NhaCungCap update(NhaCungCap nhaCungCap) {
        Optional<NhaCungCap> existingNhaCungCap = nhaCungCapRepository.findById(nhaCungCap.getMaNCC());

        if (!existingNhaCungCap.isPresent()) {
            throw new RuntimeException("Không tìm thấy nhà cung cấp với ID - " + nhaCungCap.getMaNCC());
        }

        return nhaCungCapRepository.save(nhaCungCap);
    }

    @Override
    public List<NhaCungCap> findAllActive() {
        return nhaCungCapRepository.findAllActive();
    }

    @Override
    public NhaCungCap findActiveById(String id) {
        Optional<NhaCungCap> result = nhaCungCapRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    public String generateMaNhaCungCap() {
        // Sử dụng random alphanumeric cho 7 ký tự
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        // Tạo loop để đảm bảo mã không trùng
        String newCode;
        int maxAttempts = 100; // Giới hạn số lần thử
        int attempts = 0;
        
        do {
            StringBuilder randomPart = new StringBuilder();
            for (int i = 0; i < 7; i++) {
                int index = (int) (Math.random() * characters.length());
                randomPart.append(characters.charAt(index));
            }
            newCode = "NCC" + randomPart.toString();
            attempts++;
        } while (nhaCungCapRepository.findById(newCode).isPresent() && attempts < maxAttempts);
        
        if (attempts >= maxAttempts) {
            // Fallback: sử dụng timestamp nếu không tìm được mã unique
            long timestamp = System.currentTimeMillis();
            String timestampStr = String.valueOf(timestamp);
            String suffix = timestampStr.substring(Math.max(0, timestampStr.length() - 7));
            newCode = "NCC" + suffix;
        }
        
        return newCode;
    }

    @Override
    public void softDeleteById(String id) {
        Optional<NhaCungCap> nhaCungCapOpt = nhaCungCapRepository.findActiveById(id);
        if (nhaCungCapOpt.isPresent()) {
            NhaCungCap nhaCungCap = nhaCungCapOpt.get();
            nhaCungCap.setIsDeleted(true);
            nhaCungCapRepository.save(nhaCungCap);
        }
    }
} 