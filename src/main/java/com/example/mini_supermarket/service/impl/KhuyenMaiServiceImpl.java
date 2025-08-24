package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.KhuyenMaiRepository;
import com.example.mini_supermarket.entity.KhuyenMai;
import com.example.mini_supermarket.service.KhuyenMaiService;
import com.example.mini_supermarket.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class KhuyenMaiServiceImpl implements KhuyenMaiService {
    private KhuyenMaiRepository khuyenMaiRepository;

    @Autowired
    public KhuyenMaiServiceImpl(KhuyenMaiRepository khuyenMaiRepository) {
        this.khuyenMaiRepository = khuyenMaiRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhuyenMai> findAll() {
        return khuyenMaiRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public KhuyenMai findById(String theId) {
        return khuyenMaiRepository.findActiveById(theId).orElse(null);
    }

    @Override
    @Transactional
    public KhuyenMai save(KhuyenMai theKhuyenMai) {
        // Tự động generate mã khuyến mãi nếu chưa có
        if (theKhuyenMai.getMaKM() == null || theKhuyenMai.getMaKM().trim().isEmpty()) {
            String maKM = generateMaKhuyenMai();
            theKhuyenMai.setMaKM(maKM);
        }
        
        // Tự động generate coupon code nếu chưa có
        if (theKhuyenMai.getCouponCode() == null || theKhuyenMai.getCouponCode().trim().isEmpty()) {
            String couponCode = generateCouponCode();
            theKhuyenMai.setCouponCode(couponCode);
        }
        
        // Đặt giá trị mặc định
        if (theKhuyenMai.getIsDeleted() == null) {
            theKhuyenMai.setIsDeleted(false);
        }
        
        if (theKhuyenMai.getTrangThai() == null) {
            theKhuyenMai.setTrangThai(1); // Mặc định active
        }
        
        if (theKhuyenMai.getDaSuDung() == null) {
            theKhuyenMai.setDaSuDung(0); // Mặc định chưa sử dụng
        }
        
        return khuyenMaiRepository.save(theKhuyenMai);
    }
    
    /**
     * Tạo mã khuyến mãi tự động
     * @return Mã khuyến mãi duy nhất
     */
    private String generateMaKhuyenMai() {
        String maKM;
        
        // Lặp để đảm bảo mã không trùng
        do {
            maKM = CodeGenerator.generateMaKhuyenMai();
        } while (khuyenMaiRepository.existsByMaKM(maKM));
        
        return maKM;
    }
    
    /**
     * Tạo coupon code tự động
     * @return Coupon code duy nhất
     */
    private String generateCouponCode() {
        String couponCode;
        
        // Lặp để đảm bảo coupon code không trùng
        do {
            couponCode = CodeGenerator.generateCustomCode("COUPON", 8);
        } while (khuyenMaiRepository.existsByCouponCode(couponCode));
        
        return couponCode;
    }

    @Override
    @Transactional
    public void deleteById(String theId) {
        khuyenMaiRepository.deleteById(theId);
    }

    @Override
    @Transactional
    public KhuyenMai update(KhuyenMai khuyenMai) {
        Optional<KhuyenMai> existingKhuyenMai = khuyenMaiRepository.findActiveById(khuyenMai.getMaKM());

        if (!existingKhuyenMai.isPresent()) {
            throw new RuntimeException("Không tìm thấy khuyến mãi với ID - " + khuyenMai.getMaKM());
        }

        return khuyenMaiRepository.save(khuyenMai);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhuyenMai> findAllActive() {
        return khuyenMaiRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public KhuyenMai findActiveById(String id) {
        Optional<KhuyenMai> result = khuyenMaiRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public KhuyenMai findByCouponCode(String couponCode) {
        Optional<KhuyenMai> result = khuyenMaiRepository.findByCouponCode(couponCode);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public void softDeleteById(String id) {
        Optional<KhuyenMai> khuyenMaiOpt = khuyenMaiRepository.findActiveById(id);
        if (khuyenMaiOpt.isPresent()) {
            KhuyenMai khuyenMai = khuyenMaiOpt.get();
            khuyenMai.setIsDeleted(true);
            khuyenMaiRepository.save(khuyenMai);
        }
    }
} 
