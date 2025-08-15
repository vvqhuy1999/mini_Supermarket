package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.TonKhoChiTietRepository;
import com.example.mini_supermarket.entity.TonKhoChiTiet;
import com.example.mini_supermarket.service.TonKhoChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TonKhoChiTietServiceImpl implements TonKhoChiTietService {

    @Autowired
    private TonKhoChiTietRepository tonKhoChiTietRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TonKhoChiTiet> findAll() {
        return tonKhoChiTietRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TonKhoChiTiet> findAllActive() {
        return tonKhoChiTietRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public TonKhoChiTiet findById(Integer id) {
        return tonKhoChiTietRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public TonKhoChiTiet findActiveById(Integer id) {
        return tonKhoChiTietRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public TonKhoChiTiet save(TonKhoChiTiet tonKhoChiTiet) {
        return tonKhoChiTietRepository.save(tonKhoChiTiet);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        tonKhoChiTietRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        TonKhoChiTiet tonKhoChiTiet = findActiveById(id);
        if (tonKhoChiTiet != null) {
            tonKhoChiTiet.setIsDeleted(true);
            tonKhoChiTietRepository.save(tonKhoChiTiet);
        }
    }
} 
