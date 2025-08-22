package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.ThanhToanRepository;
import com.example.mini_supermarket.entity.ThanhToan;
import com.example.mini_supermarket.service.ThanhToanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ThanhToanServiceImpl implements ThanhToanService {
    private ThanhToanRepository thanhToanRepository;

    @Autowired
    public ThanhToanServiceImpl(ThanhToanRepository thanhToanRepository) {
        this.thanhToanRepository = thanhToanRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ThanhToan> findAll() {
        return thanhToanRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public ThanhToan findById(Integer theId) {
        return thanhToanRepository.findActiveById(theId).orElse(null);
    }

    @Override
    @Transactional
    public ThanhToan save(ThanhToan theThanhToan) {
        return thanhToanRepository.save(theThanhToan);
    }

    @Override
    @Transactional
    public void deleteById(Integer theId) {
        thanhToanRepository.deleteById(theId);
    }

    @Override
    @Transactional
    public ThanhToan update(ThanhToan thanhToan) {
        Optional<ThanhToan> existingThanhToan = thanhToanRepository.findActiveById(thanhToan.getMaTT());

        if (!existingThanhToan.isPresent()) {
            throw new RuntimeException("Không tìm thấy thanh toán với ID - " + thanhToan.getMaTT());
        }

        return thanhToanRepository.save(thanhToan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ThanhToan> findAllActive() {
        return thanhToanRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public ThanhToan findActiveById(Integer id) {
        Optional<ThanhToan> result = thanhToanRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        Optional<ThanhToan> thanhToanOpt = thanhToanRepository.findActiveById(id);
        if (thanhToanOpt.isPresent()) {
            ThanhToan thanhToan = thanhToanOpt.get();
            thanhToan.setIsDeleted(true);
            thanhToanRepository.save(thanhToan);
        }
    }

    @Override
    public ThanhToan createVNPayPayment(ThanhToan thanhToan, Map<String, String> vnpayParams) {
        thanhToan.setMaGiaoDichNganHang(vnpayParams.get("vnp_TxnRef"));
        thanhToan.setTrangThaiTT(0); // Chờ xử lý
        return thanhToanRepository.save(thanhToan);
    }


} 
