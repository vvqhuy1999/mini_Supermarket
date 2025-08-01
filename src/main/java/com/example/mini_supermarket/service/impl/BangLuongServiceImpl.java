package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.BangLuongRepository;
import com.example.mini_supermarket.entity.BangLuong;
import com.example.mini_supermarket.service.BangLuongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BangLuongServiceImpl implements BangLuongService {

    @Autowired
    private BangLuongRepository bangLuongRepository;

    @Override
    public List<BangLuong> findAll() {
        return bangLuongRepository.findAll();
    }

    @Override
    public List<BangLuong> findAllActive() {
        return bangLuongRepository.findAllActive();
    }

    @Override
    public BangLuong findById(Integer id) {
        return bangLuongRepository.findById(id).orElse(null);
    }

    @Override
    public BangLuong findActiveById(Integer id) {
        return bangLuongRepository.findActiveById(id).orElse(null);
    }

    @Override
    public BangLuong save(BangLuong bangLuong) {
        return bangLuongRepository.save(bangLuong);
    }

    @Override
    public void deleteById(Integer id) {
        bangLuongRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Integer id) {
        BangLuong bangLuong = findById(id);
        if (bangLuong != null) {
            bangLuong.setIsDeleted(true);
            bangLuongRepository.save(bangLuong);
        }
    }
} 