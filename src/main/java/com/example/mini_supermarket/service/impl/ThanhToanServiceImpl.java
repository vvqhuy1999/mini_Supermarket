package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.ThanhToanRepository;
import com.example.mini_supermarket.entity.ThanhToan;
import com.example.mini_supermarket.service.ThanhToanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThanhToanServiceImpl implements ThanhToanService {
    private ThanhToanRepository thanhToanRepository;

    @Autowired
    public ThanhToanServiceImpl(ThanhToanRepository thanhToanRepository) {
        this.thanhToanRepository = thanhToanRepository;
    }

    @Override
    public List<ThanhToan> findAll() {
        return thanhToanRepository.findAll();
    }

    @Override
    public ThanhToan findById(Integer theId) {
        Optional<ThanhToan> result = thanhToanRepository.findById(theId);
        ThanhToan theThanhToan = null;

        if (result.isPresent()) {
            theThanhToan = result.get();
        } else {
            throw new RuntimeException("Did not find ThanhToan id - " + theId);
        }
        return theThanhToan;
    }

    @Override
    public ThanhToan save(ThanhToan theThanhToan) {
        return thanhToanRepository.save(theThanhToan);
    }

    @Override
    public void deleteById(Integer theId) {
        thanhToanRepository.deleteById(theId);
    }

    @Override
    public ThanhToan update(ThanhToan thanhToan) {
        Optional<ThanhToan> existingThanhToan = thanhToanRepository.findById(thanhToan.getMaTT());

        if (!existingThanhToan.isPresent()) {
            throw new RuntimeException("Không tìm thấy thanh toán với ID - " + thanhToan.getMaTT());
        }

        return thanhToanRepository.save(thanhToan);
    }

    @Override
    public List<ThanhToan> findAllActive() {
        return thanhToanRepository.findAllActive();
    }

    @Override
    public ThanhToan findActiveById(Integer id) {
        Optional<ThanhToan> result = thanhToanRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    public void softDeleteById(Integer id) {
        Optional<ThanhToan> thanhToanOpt = thanhToanRepository.findActiveById(id);
        if (thanhToanOpt.isPresent()) {
            ThanhToan thanhToan = thanhToanOpt.get();
            thanhToan.setIsDeleted(true);
            thanhToanRepository.save(thanhToan);
        }
    }
} 
