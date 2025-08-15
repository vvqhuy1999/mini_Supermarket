package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.BangLuongRepository;
import com.example.mini_supermarket.entity.BangLuong;
import com.example.mini_supermarket.service.BangLuongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BangLuongServiceImpl implements BangLuongService {

    @Autowired
    private BangLuongRepository bangLuongRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BangLuong> findAll() {
        return bangLuongRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BangLuong> findAllActive() {
        return bangLuongRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public BangLuong findById(Integer id) {
        return bangLuongRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public BangLuong findActiveById(Integer id) {
        return bangLuongRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public BangLuong save(BangLuong bangLuong) {
        return bangLuongRepository.save(bangLuong);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        bangLuongRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        BangLuong bangLuong = findActiveById(id);
        if (bangLuong != null) {
            bangLuong.setIsDeleted(true);
            bangLuongRepository.save(bangLuong);
        }
    }
} 