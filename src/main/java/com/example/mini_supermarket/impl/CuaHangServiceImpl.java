package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.CuaHangRepository;
import com.example.mini_supermarket.entity.CuaHang;
import com.example.mini_supermarket.service.CuaHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CuaHangServiceImpl implements CuaHangService {
    private CuaHangRepository cuaHangRepository;

    @Autowired
    public CuaHangServiceImpl(CuaHangRepository cuaHangRepository) {
        this.cuaHangRepository = cuaHangRepository;
    }

    @Override
    public List<CuaHang> findAll() {
        return cuaHangRepository.findAll();
    }

    @Override
    public CuaHang findById(String theId) {
        Optional<CuaHang> result = cuaHangRepository.findById(theId);
        CuaHang theCuaHang = null;

        if (result.isPresent()) {
            theCuaHang = result.get();
        } else {
            throw new RuntimeException("Did not find CuaHang id - " + theId);
        }
        return theCuaHang;
    }

    @Override
    public CuaHang save(CuaHang theCuaHang) {
        return cuaHangRepository.save(theCuaHang);
    }

    @Override
    public void deleteById(String theId) {
        cuaHangRepository.deleteById(theId);
    }

    @Override
    public CuaHang update(CuaHang cuaHang) {
        Optional<CuaHang> existingCuaHang = cuaHangRepository.findById(cuaHang.getMaCH());

        if (!existingCuaHang.isPresent()) {
            throw new RuntimeException("Không tìm thấy cửa hàng với ID - " + cuaHang.getMaCH());
        }

        return cuaHangRepository.save(cuaHang);
    }

    @Override
    public List<CuaHang> findAllActive() {
        return cuaHangRepository.findAllActive();
    }

    @Override
    public CuaHang findActiveById(String id) {
        Optional<CuaHang> result = cuaHangRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    public void softDeleteById(String id) {
        Optional<CuaHang> cuaHangOpt = cuaHangRepository.findActiveById(id);
        if (cuaHangOpt.isPresent()) {
            CuaHang cuaHang = cuaHangOpt.get();
            cuaHang.setIsDeleted(true);
            cuaHangRepository.save(cuaHang);
        }
    }
} 