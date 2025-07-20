package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.ThongKeBaoCaoRepository;
import com.example.mini_supermarket.entity.ThongKeBaoCao;
import com.example.mini_supermarket.service.ThongKeBaoCaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThongKeBaoCaoServiceImpl implements ThongKeBaoCaoService {

    @Autowired
    private ThongKeBaoCaoRepository thongKeBaoCaoRepository;

    @Override
    public List<ThongKeBaoCao> findAll() {
        return thongKeBaoCaoRepository.findAll();
    }

    @Override
    public List<ThongKeBaoCao> findAllActive() {
        return thongKeBaoCaoRepository.findAllActive();
    }

    @Override
    public ThongKeBaoCao findById(Integer id) {
        return thongKeBaoCaoRepository.findById(id).orElse(null);
    }

    @Override
    public ThongKeBaoCao findActiveById(Integer id) {
        return thongKeBaoCaoRepository.findActiveById(id).orElse(null);
    }

    @Override
    public ThongKeBaoCao save(ThongKeBaoCao thongKeBaoCao) {
        return thongKeBaoCaoRepository.save(thongKeBaoCao);
    }

    @Override
    public void deleteById(Integer id) {
        thongKeBaoCaoRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        ThongKeBaoCao thongKeBaoCao = findById(id);
        if (thongKeBaoCao != null) {
            thongKeBaoCao.setIsDeleted(true);
            thongKeBaoCaoRepository.save(thongKeBaoCao);
        }
    }
} 