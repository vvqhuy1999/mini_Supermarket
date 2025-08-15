package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.KhuyenMaiRepository;
import com.example.mini_supermarket.entity.KhuyenMai;
import com.example.mini_supermarket.service.KhuyenMaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public List<KhuyenMai> findAll() {
        return khuyenMaiRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public KhuyenMai findById(String theId) {
        return khuyenMaiRepository.findActiveById(theId).orElse(null);
    }

    @Override
    @Transactional
    public KhuyenMai save(KhuyenMai theKhuyenMai) {
        return khuyenMaiRepository.save(theKhuyenMai);
    }

    @Override
    @Transactional
    public void deleteById(String theId) {
        khuyenMaiRepository.deleteById(theId);
    }

    @Override
    @Transactional
    public KhuyenMai update(KhuyenMai khuyenMai) {
        Optional<KhuyenMai> existingKhuyenMai = khuyenMaiRepository.findActiveById(khuyenMai.getMaKM());

        if (!existingKhuyenMai.isPresent()) {
            throw new RuntimeException("Không tìm thấy khuyến mãi với ID - " + khuyenMai.getMaKM());
        }

        return khuyenMaiRepository.save(khuyenMai);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhuyenMai> findAllActive() {
        return khuyenMaiRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public KhuyenMai findActiveById(String id) {
        Optional<KhuyenMai> result = khuyenMaiRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public void softDeleteById(String id) {
        Optional<KhuyenMai> khuyenMaiOpt = khuyenMaiRepository.findActiveById(id);
        if (khuyenMaiOpt.isPresent()) {
            KhuyenMai khuyenMai = khuyenMaiOpt.get();
            khuyenMai.setIsDeleted(true);
            khuyenMaiRepository.save(khuyenMai);
        }
    }
} 
