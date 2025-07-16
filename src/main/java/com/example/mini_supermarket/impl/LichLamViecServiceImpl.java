package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.LichLamViecRepository;
import com.example.mini_supermarket.entity.LichLamViec;
import com.example.mini_supermarket.service.LichLamViecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LichLamViecServiceImpl implements LichLamViecService {

    @Autowired
    private LichLamViecRepository lichLamViecRepository;

    @Override
    public List<LichLamViec> findAll() {
        return lichLamViecRepository.findAll();
    }

    @Override
    public LichLamViec findById(Integer id) {
        Optional<LichLamViec> lichLamViec = lichLamViecRepository.findById(id);
        if (lichLamViec.isPresent()) {
            return lichLamViec.get();
        } else {
            throw new RuntimeException("Không tìm thấy lịch làm việc có id: " + id);
        }
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
    public LichLamViec update(LichLamViec lichLamViec) {
        if (lichLamViecRepository.existsById(lichLamViec.getMaLich())) {
            return lichLamViecRepository.save(lichLamViec);
        } else {
            throw new RuntimeException("Không tìm thấy lịch làm việc có id: " + lichLamViec.getMaLich());
        }
    }
} 