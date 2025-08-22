package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.PhuongThucThanhToanRepository;
import com.example.mini_supermarket.entity.PhuongThucThanhToan;
import com.example.mini_supermarket.service.PhuongThucThanhToanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PhuongThucThanhToanServiceImpl implements PhuongThucThanhToanService {

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PhuongThucThanhToan> findAll() {
        return phuongThucThanhToanRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public PhuongThucThanhToan findById(String id) {
        return phuongThucThanhToanRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public PhuongThucThanhToan save(PhuongThucThanhToan phuongThucThanhToan) {
        return phuongThucThanhToanRepository.save(phuongThucThanhToan);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        phuongThucThanhToanRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PhuongThucThanhToan update(PhuongThucThanhToan phuongThucThanhToan) {
        Optional<PhuongThucThanhToan> existingPhuongThucThanhToan = phuongThucThanhToanRepository.findActiveById(phuongThucThanhToan.getMaPTTT());
        if (existingPhuongThucThanhToan.isPresent()) {
            return phuongThucThanhToanRepository.save(phuongThucThanhToan);
        } else {
            throw new RuntimeException("Không tìm thấy phương thức thanh toán có id: " + phuongThucThanhToan.getMaPTTT());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhuongThucThanhToan> findAllActive() {
        return phuongThucThanhToanRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public PhuongThucThanhToan findActiveById(String id) {
        Optional<PhuongThucThanhToan> result = phuongThucThanhToanRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public void softDeleteById(String id) {
        Optional<PhuongThucThanhToan> phuongThucThanhToanOpt = phuongThucThanhToanRepository.findActiveById(id);
        if (phuongThucThanhToanOpt.isPresent()) {
            PhuongThucThanhToan phuongThucThanhToan = phuongThucThanhToanOpt.get();
            phuongThucThanhToan.setIsDeleted(true);
            phuongThucThanhToanRepository.save(phuongThucThanhToan);
        }
    }

    @Override
    public PhuongThucThanhToan findActiveBytenPTTT(String id) {
        Optional<PhuongThucThanhToan> result = phuongThucThanhToanRepository.findActiveByTenPTTT(id);
        return result.orElse(null);
    }
} 
