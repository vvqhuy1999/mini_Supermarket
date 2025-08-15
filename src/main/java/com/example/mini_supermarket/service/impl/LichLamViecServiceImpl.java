package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.LichLamViecRepository;
import com.example.mini_supermarket.entity.LichLamViec;
import com.example.mini_supermarket.service.LichLamViecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LichLamViecServiceImpl implements LichLamViecService {

    @Autowired
    private LichLamViecRepository lichLamViecRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LichLamViec> findAll() {
        return lichLamViecRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LichLamViec> findAllActive() {
        return lichLamViecRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public LichLamViec findById(Integer id) {
        return lichLamViecRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public LichLamViec findActiveById(Integer id) {
        return lichLamViecRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public LichLamViec save(LichLamViec lichLamViec) {
        return lichLamViecRepository.save(lichLamViec);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        lichLamViecRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        LichLamViec lichLamViec = findActiveById(id);
        if (lichLamViec != null) {
            lichLamViec.setIsDeleted(true);
            lichLamViecRepository.save(lichLamViec);
        }
    }
} 
