package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.CaLamViecRepository;
import com.example.mini_supermarket.entity.CaLamViec;
import com.example.mini_supermarket.service.CaLamViecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CaLamViecServiceImpl implements CaLamViecService {

    @Autowired
    private CaLamViecRepository caLamViecRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CaLamViec> findAll() {
        return caLamViecRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaLamViec> findAllActive() {
        return caLamViecRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public CaLamViec findById(Integer id) {
        return caLamViecRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public CaLamViec findActiveById(Integer id) {
        return caLamViecRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public CaLamViec save(CaLamViec caLamViec) {
        return caLamViecRepository.save(caLamViec);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        caLamViecRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        CaLamViec caLamViec = findActiveById(id);
        if (caLamViec != null) {
            caLamViec.setIsDeleted(true);
            caLamViecRepository.save(caLamViec);
        }
    }
} 
