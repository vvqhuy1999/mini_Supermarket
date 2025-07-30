package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.HoaDonRepository;
import com.example.mini_supermarket.entity.HoaDon;
import com.example.mini_supermarket.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HoaDonServiceImpl implements HoaDonService {
    private HoaDonRepository hoaDonRepository;

    @Autowired
    public HoaDonServiceImpl(HoaDonRepository hoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
    }

    @Override
    public List<HoaDon> findAll() {
        return hoaDonRepository.findAll();
    }

    @Override
    public HoaDon findById(Integer theId) {
        Optional<HoaDon> result = hoaDonRepository.findById(theId);
        HoaDon theHoaDon = null;

        if (result.isPresent()) {
            theHoaDon = result.get();
        } else {
            throw new RuntimeException("Did not find HoaDon id - " + theId);
        }
        return theHoaDon;
    }

    @Override
    public HoaDon save(HoaDon theHoaDon) {
        return hoaDonRepository.save(theHoaDon);
    }

    @Override
    public void deleteById(Integer theId) {
        hoaDonRepository.deleteById(theId);
    }

    @Override
    public HoaDon update(HoaDon hoaDon) {
        Optional<HoaDon> existingHoaDon = hoaDonRepository.findById(hoaDon.getMaHD());

        if (!existingHoaDon.isPresent()) {
            throw new RuntimeException("Không tìm thấy hóa đơn với ID - " + hoaDon.getMaHD());
        }

        return hoaDonRepository.save(hoaDon);
    }

    @Override
    public List<HoaDon> findAllActive() {
        return hoaDonRepository.findAllActive();
    }

    @Override
    public HoaDon findActiveById(Integer id) {
        Optional<HoaDon> result = hoaDonRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    public void softDeleteById(Integer id) {
        Optional<HoaDon> hoaDonOpt = hoaDonRepository.findActiveById(id);
        if (hoaDonOpt.isPresent()) {
            HoaDon hoaDon = hoaDonOpt.get();
            hoaDon.setIsDeleted(true);
            hoaDonRepository.save(hoaDon);
        }
    }
} 
