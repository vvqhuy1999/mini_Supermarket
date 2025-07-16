package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.CaLamViecRepository;
import com.example.mini_supermarket.entity.CaLamViec;
import com.example.mini_supermarket.service.CaLamViecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CaLamViecServiceImpl implements CaLamViecService {

    @Autowired
    private CaLamViecRepository caLamViecRepository;

    @Override
    public List<CaLamViec> findAll() {
        return caLamViecRepository.findAll();
    }

    @Override
    public CaLamViec findById(Integer id) {
        Optional<CaLamViec> caLamViec = caLamViecRepository.findById(id);
        if (caLamViec.isPresent()) {
            return caLamViec.get();
        } else {
            throw new RuntimeException("Không tìm thấy ca làm việc có id: " + id);
        }
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
    public CaLamViec update(CaLamViec caLamViec) {
        if (caLamViecRepository.existsById(caLamViec.getMaCa())) {
            return caLamViecRepository.save(caLamViec);
        } else {
            throw new RuntimeException("Không tìm thấy ca làm việc có id: " + caLamViec.getMaCa());
        }
    }
} 