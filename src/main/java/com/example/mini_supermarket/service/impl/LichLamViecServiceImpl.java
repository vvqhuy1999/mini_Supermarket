package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.LichLamViecRepository;
import com.example.mini_supermarket.entity.LichLamViec;
import com.example.mini_supermarket.service.LichLamViecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LichLamViecServiceImpl implements LichLamViecService {

    @Autowired
    private LichLamViecRepository lichLamViecRepository;

    @Override
    public List<LichLamViec> findAll() {
        return lichLamViecRepository.findAll();
    }

    @Override
    public List<LichLamViec> findAllActive() {
        return lichLamViecRepository.findAllActive();
    }

    @Override
    public LichLamViec findById(Integer id) {
        return lichLamViecRepository.findById(id).orElse(null);
    }

    @Override
    public LichLamViec findActiveById(Integer id) {
        return lichLamViecRepository.findActiveById(id).orElse(null);
    }

    @Override
    public LichLamViec save(LichLamViec lichLamViec) {
        return lichLamViecRepository.save(lichLamViec);
    }

    @Override
    public void deleteById(Integer id) {
        lichLamViecRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        LichLamViec lichLamViec = findById(id);
        if (lichLamViec != null) {
            lichLamViec.setIsDeleted(true);
            lichLamViecRepository.save(lichLamViec);
        }
    }
} 
