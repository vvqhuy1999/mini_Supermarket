package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.TonKhoChiTietRepository;
import com.example.mini_supermarket.entity.TonKhoChiTiet;
import com.example.mini_supermarket.service.TonKhoChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TonKhoChiTietServiceImpl implements TonKhoChiTietService {

    @Autowired
    private TonKhoChiTietRepository tonKhoChiTietRepository;

    @Override
    public List<TonKhoChiTiet> findAll() {
        return tonKhoChiTietRepository.findAll();
    }

    @Override
    public List<TonKhoChiTiet> findAllActive() {
        return tonKhoChiTietRepository.findAllActive();
    }

    @Override
    public TonKhoChiTiet findById(Integer id) {
        Optional<TonKhoChiTiet> tonKhoChiTiet = tonKhoChiTietRepository.findById(id);
        if (tonKhoChiTiet.isPresent()) {
            return tonKhoChiTiet.get();
        } else {
            throw new RuntimeException("Không tìm thấy tồn kho chi tiết có id: " + id);
        }
    }

    @Override
    public TonKhoChiTiet findActiveById(Integer id) {
        return tonKhoChiTietRepository.findActiveById(id).orElse(null);
    }

    @Override
    public TonKhoChiTiet save(TonKhoChiTiet tonKhoChiTiet) {
        return tonKhoChiTietRepository.save(tonKhoChiTiet);
    }

    @Override
    public void deleteById(Integer id) {
        tonKhoChiTietRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        TonKhoChiTiet tonKhoChiTiet = findById(id);
        if (tonKhoChiTiet != null) {
            tonKhoChiTiet.setIsDeleted(true);
            tonKhoChiTietRepository.save(tonKhoChiTiet);
        }
    }


} 