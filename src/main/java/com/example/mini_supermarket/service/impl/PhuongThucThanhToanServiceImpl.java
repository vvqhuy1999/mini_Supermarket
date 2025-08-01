package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.PhuongThucThanhToanRepository;
import com.example.mini_supermarket.entity.PhuongThucThanhToan;
import com.example.mini_supermarket.service.PhuongThucThanhToanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhuongThucThanhToanServiceImpl implements PhuongThucThanhToanService {

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Override
    public List<PhuongThucThanhToan> findAll() {
        return phuongThucThanhToanRepository.findAll();
    }

    @Override
    public PhuongThucThanhToan findById(String id) {
        Optional<PhuongThucThanhToan> phuongThucThanhToan = phuongThucThanhToanRepository.findById(id);
        if (phuongThucThanhToan.isPresent()) {
            return phuongThucThanhToan.get();
        } else {
            throw new RuntimeException("Không tìm thấy phương thức thanh toán có id: " + id);
        }
    }

    @Override
    public PhuongThucThanhToan save(PhuongThucThanhToan phuongThucThanhToan) {
        return phuongThucThanhToanRepository.save(phuongThucThanhToan);
    }

    @Override
    public void deleteById(String id) {
        phuongThucThanhToanRepository.deleteById(id);
    }

    @Override
    public PhuongThucThanhToan update(PhuongThucThanhToan phuongThucThanhToan) {
        if (phuongThucThanhToanRepository.existsById(phuongThucThanhToan.getMaPTTT())) {
            return phuongThucThanhToanRepository.save(phuongThucThanhToan);
        } else {
            throw new RuntimeException("Không tìm thấy phương thức thanh toán có id: " + phuongThucThanhToan.getMaPTTT());
        }
    }

    @Override
    public List<PhuongThucThanhToan> findAllActive() {
        return phuongThucThanhToanRepository.findAllActive();
    }

    @Override
    public PhuongThucThanhToan findActiveById(String id) {
        Optional<PhuongThucThanhToan> result = phuongThucThanhToanRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    public void softDeleteById(String id) {
        Optional<PhuongThucThanhToan> phuongThucThanhToanOpt = phuongThucThanhToanRepository.findActiveById(id);
        if (phuongThucThanhToanOpt.isPresent()) {
            PhuongThucThanhToan phuongThucThanhToan = phuongThucThanhToanOpt.get();
            phuongThucThanhToan.setIsDeleted(true);
            phuongThucThanhToanRepository.save(phuongThucThanhToan);
        }
    }
} 
