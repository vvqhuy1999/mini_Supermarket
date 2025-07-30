package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.CaLamViecRepository;
import com.example.mini_supermarket.entity.CaLamViec;
import com.example.mini_supermarket.service.CaLamViecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaLamViecServiceImpl implements CaLamViecService {

    @Autowired
    private CaLamViecRepository caLamViecRepository;

    @Override
    public List<CaLamViec> findAll() {
        return caLamViecRepository.findAll();
    }

    @Override
    public List<CaLamViec> findAllActive() {
        return caLamViecRepository.findAllActive();
    }

    @Override
    public CaLamViec findById(Integer id) {
        return caLamViecRepository.findById(id).orElse(null);
    }

    @Override
    public CaLamViec findActiveById(Integer id) {
        return caLamViecRepository.findActiveById(id).orElse(null);
    }

    @Override
    public CaLamViec save(CaLamViec caLamViec) {
        return caLamViecRepository.save(caLamViec);
    }

    @Override
    public void deleteById(Integer id) {
        caLamViecRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        CaLamViec caLamViec = findById(id);
        if (caLamViec != null) {
            caLamViec.setIsDeleted(true);
            caLamViecRepository.save(caLamViec);
        }
    }
} 
