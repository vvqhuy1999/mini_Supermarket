package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.ThongKeBaoCaoRepository;
import com.example.mini_supermarket.entity.ThongKeBaoCao;
import com.example.mini_supermarket.service.ThongKeBaoCaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThongKeBaoCaoServiceImpl implements ThongKeBaoCaoService {
    private ThongKeBaoCaoRepository thongKeBaoCaoRepository;

    @Autowired
    public ThongKeBaoCaoServiceImpl(ThongKeBaoCaoRepository thongKeBaoCaoRepository) {
        this.thongKeBaoCaoRepository = thongKeBaoCaoRepository;
    }

    @Override
    public List<ThongKeBaoCao> findAll() {
        return thongKeBaoCaoRepository.findAll();
    }

    @Override
    public ThongKeBaoCao findById(Integer theId) {
        Optional<ThongKeBaoCao> result = thongKeBaoCaoRepository.findById(theId);
        ThongKeBaoCao theThongKeBaoCao = null;

        if (result.isPresent()) {
            theThongKeBaoCao = result.get();
        } else {
            throw new RuntimeException("Did not find ThongKeBaoCao id - " + theId);
        }
        return theThongKeBaoCao;
    }

    @Override
    public ThongKeBaoCao save(ThongKeBaoCao theThongKeBaoCao) {
        return thongKeBaoCaoRepository.save(theThongKeBaoCao);
    }

    @Override
    public void deleteById(Integer theId) {
        thongKeBaoCaoRepository.deleteById(theId);
    }

    @Override
    public ThongKeBaoCao update(ThongKeBaoCao thongKeBaoCao) {
        Optional<ThongKeBaoCao> existingThongKeBaoCao = thongKeBaoCaoRepository.findById(thongKeBaoCao.getMaBaoCao());

        if (!existingThongKeBaoCao.isPresent()) {
            throw new RuntimeException("Không tìm thấy thống kê báo cáo với ID - " + thongKeBaoCao.getMaBaoCao());
        }

        return thongKeBaoCaoRepository.save(thongKeBaoCao);
    }
} 