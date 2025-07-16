package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.HinhAnhRepository;
import com.example.mini_supermarket.entity.HinhAnh;
import com.example.mini_supermarket.service.HinhAnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HinhAnhServiceImpl implements HinhAnhService {

    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @Override
    public List<HinhAnh> findAll() {
        return hinhAnhRepository.findAll();
    }

    @Override
    public HinhAnh findById(Integer id) {
        Optional<HinhAnh> hinhAnh = hinhAnhRepository.findById(id);
        if (hinhAnh.isPresent()) {
            return hinhAnh.get();
        } else {
            throw new RuntimeException("Không tìm thấy hình ảnh có id: " + id);
        }
    }

    @Override
    public HinhAnh save(HinhAnh hinhAnh) {
        return hinhAnhRepository.save(hinhAnh);
    }

    @Override
    public void deleteById(Integer id) {
        hinhAnhRepository.deleteById(id);
    }

    @Override
    public HinhAnh update(HinhAnh hinhAnh) {
        if (hinhAnhRepository.existsById(hinhAnh.getMaHinh())) {
            return hinhAnhRepository.save(hinhAnh);
        } else {
            throw new RuntimeException("Không tìm thấy hình ảnh có id: " + hinhAnh.getMaHinh());
        }
    }
} 