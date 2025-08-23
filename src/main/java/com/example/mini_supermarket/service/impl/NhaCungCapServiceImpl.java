package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.NhaCungCapRepository;
import com.example.mini_supermarket.entity.NhaCungCap;
import com.example.mini_supermarket.service.NhaCungCapService;
import com.example.mini_supermarket.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public List<NhaCungCap> findAll() {
        return nhaCungCapRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public NhaCungCap findById(String theId) {
        return nhaCungCapRepository.findActiveById(theId).orElse(null);
    }

    @Override
    @Transactional
    public NhaCungCap save(NhaCungCap theNhaCungCap) {
        // Tự động generate mã nhà cung cấp nếu chưa có
        if (theNhaCungCap.getMaNCC() == null || theNhaCungCap.getMaNCC().trim().isEmpty()) {
            String maNCC = generateMaNhaCungCap();
            theNhaCungCap.setMaNCC(maNCC);
        }
        
        // Đặt giá trị mặc định
        if (theNhaCungCap.getIsDeleted() == null) {
            theNhaCungCap.setIsDeleted(false);
        }
        
        return nhaCungCapRepository.save(theNhaCungCap);
    }
    
    /**
     * Tạo mã nhà cung cấp tự động
     * @return Mã nhà cung cấp duy nhất
     */
    private String generateMaNhaCungCap() {
        String maNCC;
        
        // Lặp để đảm bảo mã không trùng
        do {
            maNCC = CodeGenerator.generateMaNhaCungCap();
        } while (nhaCungCapRepository.existsByMaNCC(maNCC));
        
        return maNCC;
    }

    @Override
    @Transactional
    public void deleteById(String theId) {
        nhaCungCapRepository.deleteById(theId);
    }

    @Override
    @Transactional
    public NhaCungCap update(NhaCungCap nhaCungCap) {
        Optional<NhaCungCap> existingNhaCungCap = nhaCungCapRepository.findActiveById(nhaCungCap.getMaNCC());

        if (!existingNhaCungCap.isPresent()) {
            throw new RuntimeException("Không tìm thấy nhà cung cấp với ID - " + nhaCungCap.getMaNCC());
        }

        return nhaCungCapRepository.save(nhaCungCap);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NhaCungCap> findAllActive() {
        return nhaCungCapRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public NhaCungCap findActiveById(String id) {
        Optional<NhaCungCap> result = nhaCungCapRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public void softDeleteById(String id) {
        Optional<NhaCungCap> nhaCungCapOpt = nhaCungCapRepository.findActiveById(id);
        if (nhaCungCapOpt.isPresent()) {
            NhaCungCap nhaCungCap = nhaCungCapOpt.get();
            nhaCungCap.setIsDeleted(true);
            nhaCungCapRepository.save(nhaCungCap);
        }
    }
} 
