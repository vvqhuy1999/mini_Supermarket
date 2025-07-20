package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.ThongKeBaoCao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ThongKeBaoCaoService {
    List<ThongKeBaoCao> findAll();
    List<ThongKeBaoCao> findAllActive();
    ThongKeBaoCao findById(Integer id);
    ThongKeBaoCao findActiveById(Integer id);
    ThongKeBaoCao save(ThongKeBaoCao thongKeBaoCao);
    void deleteById(Integer id);
    void softDeleteById(Integer id);
} 