package com.example.mini_supermarket.service.impl;


import com.example.mini_supermarket.entity.ThanhToan;
import com.example.mini_supermarket.repository.ThanhToanRepository;
import com.example.mini_supermarket.service.ThanhToanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ThanhToanServiceImpl implements ThanhToanService {

    @Autowired
    private ThanhToanRepository thanhToanRepository;

    @Override
    public List<ThanhToan> findAll() {
        return thanhToanRepository.findAll();
    }

    @Override
    public List<ThanhToan> findAllActive() {
        return thanhToanRepository.findAllActive();
    }

    @Override
    public ThanhToan findById(Integer id) {
        return thanhToanRepository.findByIdIncludeDeleted(id).orElse(null);
    }

    @Override
    public ThanhToan findActiveById(Integer id) {
        return thanhToanRepository.findActiveById(id).orElse(null);
    }

    @Override
    public ThanhToan save(ThanhToan thanhToan) {
        return thanhToanRepository.save(thanhToan);
    }

    @Override
    public void deleteById(Integer id) {
        thanhToanRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        Optional<ThanhToan> thanhToan = thanhToanRepository.findByIdIncludeDeleted(id);
        if (thanhToan.isPresent()) {
            ThanhToan tt = thanhToan.get();
            tt.setIsDeleted(true);
            thanhToanRepository.save(tt);
        }
    }

    @Override
    public ThanhToan update(ThanhToan thanhToan) {
        return thanhToanRepository.save(thanhToan);
    }

    @Override
    public ThanhToan createVNPayPayment(ThanhToan thanhToan, Map<String, String> vnpayParams) {
        thanhToan.setMaGiaoDichNganHang(vnpayParams.get("vnp_TxnRef"));
        thanhToan.setTrangThaiTT(0); // Chờ xử lý
        return thanhToanRepository.save(thanhToan);
    }
}