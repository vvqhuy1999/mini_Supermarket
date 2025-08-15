package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.HoaDonRepository;
import com.example.mini_supermarket.entity.HoaDon;
import com.example.mini_supermarket.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public List<HoaDon> findAll() {
        return hoaDonRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional
    public HoaDon save(HoaDon theHoaDon) {
        return hoaDonRepository.save(theHoaDon);
    }

    @Override
    @Transactional
    public void deleteById(Integer theId) {
        hoaDonRepository.deleteById(theId);
    }

    @Override
    @Transactional
    public HoaDon update(HoaDon hoaDon) {
        Optional<HoaDon> existingHoaDon = hoaDonRepository.findById(hoaDon.getMaHD());

        if (!existingHoaDon.isPresent()) {
            throw new RuntimeException("Không tìm thấy hóa đơn với ID - " + hoaDon.getMaHD());
        }

        return hoaDonRepository.save(hoaDon);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDon> findAllActive() {
        return hoaDonRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDon findActiveById(Integer id) {
        Optional<HoaDon> result = hoaDonRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        Optional<HoaDon> hoaDonOpt = hoaDonRepository.findActiveById(id);
        if (hoaDonOpt.isPresent()) {
            HoaDon hoaDon = hoaDonOpt.get();
            hoaDon.setIsDeleted(true);
            hoaDonRepository.save(hoaDon);
        }
    }
} 
