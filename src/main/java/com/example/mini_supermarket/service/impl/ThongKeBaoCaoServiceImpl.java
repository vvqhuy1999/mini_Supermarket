package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.ThongKeBaoCaoRepository;
import com.example.mini_supermarket.entity.ThongKeBaoCao;
import com.example.mini_supermarket.service.ThongKeBaoCaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ThongKeBaoCaoServiceImpl implements ThongKeBaoCaoService {

    @Autowired
    private ThongKeBaoCaoRepository thongKeBaoCaoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ThongKeBaoCao> findAll() {
        return thongKeBaoCaoRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ThongKeBaoCao> findAllActive() {
        return thongKeBaoCaoRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public ThongKeBaoCao findById(Integer id) {
        return thongKeBaoCaoRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ThongKeBaoCao findActiveById(Integer id) {
        return thongKeBaoCaoRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public ThongKeBaoCao save(ThongKeBaoCao thongKeBaoCao) {
        return thongKeBaoCaoRepository.save(thongKeBaoCao);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        thongKeBaoCaoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        ThongKeBaoCao thongKeBaoCao = findActiveById(id);
        if (thongKeBaoCao != null) {
            thongKeBaoCao.setIsDeleted(true);
            thongKeBaoCaoRepository.save(thongKeBaoCao);
        }
    }
} 
