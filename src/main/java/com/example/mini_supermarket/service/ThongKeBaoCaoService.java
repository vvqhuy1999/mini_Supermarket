package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.ThongKeBaoCao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ThongKeBaoCaoService {
    List<ThongKeBaoCao> findAll();

    ThongKeBaoCao findById(Integer id);

    ThongKeBaoCao save(ThongKeBaoCao thongKeBaoCao);

    void deleteById(Integer id);

    ThongKeBaoCao update(ThongKeBaoCao thongKeBaoCao);
} 