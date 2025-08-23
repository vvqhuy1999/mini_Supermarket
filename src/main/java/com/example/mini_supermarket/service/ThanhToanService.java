package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.ThanhToan;

import java.util.List;
import java.util.Map;

public interface ThanhToanService {
    List<ThanhToan> findAll();

    List<ThanhToan> findAllActive();

    ThanhToan findById(Integer id);

    ThanhToan findActiveById(Integer id);

    ThanhToan save(ThanhToan thanhToan);

    void deleteById(Integer id);

    void softDeleteById(Integer id);

    ThanhToan update(ThanhToan thanhToan);
    ThanhToan findByMaGiaoDichNganHang(String maGiaoDichNganHang);

    ThanhToan createVNPayPayment(ThanhToan thanhToan, Map<String, String> vnpayParams);
}