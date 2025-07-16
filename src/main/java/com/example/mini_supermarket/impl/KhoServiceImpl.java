package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.KhoRepository;
import com.example.mini_supermarket.entity.Kho;
import com.example.mini_supermarket.service.KhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KhoServiceImpl implements KhoService {

    @Autowired
    private KhoRepository khoRepository;

    @Override
    public List<Kho> findAll() {
        return khoRepository.findAll();
    }

    @Override
    public Kho findById(Integer id) {
        Optional<Kho> kho = khoRepository.findById(id);
        if (kho.isPresent()) {
            return kho.get();
        } else {
            throw new RuntimeException("Không tìm thấy kho có id: " + id);
        }
    }

    @Override
    public Kho save(Kho kho) {
        return khoRepository.save(kho);
    }

    @Override
    public void deleteById(Integer id) {
        khoRepository.deleteById(id);
    }

    @Override
    public Kho update(Kho kho) {
        if (khoRepository.existsById(kho.getMaKho())) {
            return khoRepository.save(kho);
        } else {
            throw new RuntimeException("Không tìm thấy kho có id: " + kho.getMaKho());
        }
    }
} 