package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.KhuyenMaiRepository;
import com.example.mini_supermarket.entity.KhuyenMai;
import com.example.mini_supermarket.service.KhuyenMaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KhuyenMaiServiceImpl implements KhuyenMaiService {
    private KhuyenMaiRepository khuyenMaiRepository;

    @Autowired
    public KhuyenMaiServiceImpl(KhuyenMaiRepository khuyenMaiRepository) {
        this.khuyenMaiRepository = khuyenMaiRepository;
    }

    @Override
    public List<KhuyenMai> findAll() {
        return khuyenMaiRepository.findAll();
    }

    @Override
    public KhuyenMai findById(String theId) {
        Optional<KhuyenMai> result = khuyenMaiRepository.findById(theId);
        KhuyenMai theKhuyenMai = null;

        if (result.isPresent()) {
            theKhuyenMai = result.get();
        } else {
            throw new RuntimeException("Did not find KhuyenMai id - " + theId);
        }
        return theKhuyenMai;
    }

    @Override
    public KhuyenMai save(KhuyenMai theKhuyenMai) {
        return khuyenMaiRepository.save(theKhuyenMai);
    }

    @Override
    public void deleteById(String theId) {
        khuyenMaiRepository.deleteById(theId);
    }

    @Override
    public KhuyenMai update(KhuyenMai khuyenMai) {
        Optional<KhuyenMai> existingKhuyenMai = khuyenMaiRepository.findById(khuyenMai.getMaKM());

        if (!existingKhuyenMai.isPresent()) {
            throw new RuntimeException("Không tìm thấy khuyến mãi với ID - " + khuyenMai.getMaKM());
        }

        return khuyenMaiRepository.save(khuyenMai);
    }

    @Override
    public List<KhuyenMai> findAllActive() {
        return khuyenMaiRepository.findAllActive();
    }

    @Override
    public KhuyenMai findActiveById(String id) {
        Optional<KhuyenMai> result = khuyenMaiRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    public void softDeleteById(String id) {
        Optional<KhuyenMai> khuyenMaiOpt = khuyenMaiRepository.findActiveById(id);
        if (khuyenMaiOpt.isPresent()) {
            KhuyenMai khuyenMai = khuyenMaiOpt.get();
            khuyenMai.setIsDeleted(true);
            khuyenMaiRepository.save(khuyenMai);
        }
    }
} 