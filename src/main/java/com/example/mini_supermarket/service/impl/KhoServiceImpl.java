package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.KhoRepository;
import com.example.mini_supermarket.entity.Kho;
import com.example.mini_supermarket.service.KhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class KhoServiceImpl implements KhoService {

    @Autowired
    private KhoRepository khoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Kho> findAll() {
        return khoRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public Kho findById(Integer id) {
        return khoRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public Kho save(Kho kho) {
        return khoRepository.save(kho);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        khoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Kho update(Kho kho) {
        Optional<Kho> existingKho = khoRepository.findActiveById(kho.getMaKho());
        if (existingKho.isPresent()) {
            return khoRepository.save(kho);
        } else {
            throw new RuntimeException("Không tìm thấy kho có id: " + kho.getMaKho());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Kho> findAllActive() {
        return khoRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public Kho findActiveById(Integer id) {
        Optional<Kho> result = khoRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        Optional<Kho> khoOpt = khoRepository.findActiveById(id);
        if (khoOpt.isPresent()) {
            Kho kho = khoOpt.get();
            kho.setIsDeleted(true);
            khoRepository.save(kho);
        }
    }
} 
