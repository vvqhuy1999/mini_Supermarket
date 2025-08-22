package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.NguoiDungRepository;
import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NguoiDungServiceImpl implements NguoiDungService {
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    public NguoiDungServiceImpl(NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NguoiDung> findAll() {
        return nguoiDungRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public NguoiDung findById(String theId) {
        return nguoiDungRepository.findActiveById(theId).orElse(null);
    }

    @Override
    @Transactional
    public NguoiDung save(NguoiDung theNguoiDung) {
        return nguoiDungRepository.save(theNguoiDung);
    }

    @Override
    @Transactional
    public void deleteById(String theId) {
        nguoiDungRepository.deleteById(theId);
    }

    @Override
    @Transactional
    public NguoiDung update(NguoiDung nguoiDung) {
        Optional<NguoiDung> existingNguoiDung = nguoiDungRepository.findActiveById(nguoiDung.getMaNguoiDung());

        if (!existingNguoiDung.isPresent()) {
            throw new RuntimeException("Không tìm thấy người dùng với ID - " + nguoiDung.getMaNguoiDung());
        }

        return nguoiDungRepository.save(nguoiDung);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NguoiDung> findAllActive() {
        return nguoiDungRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public NguoiDung findActiveById(String id) {
        Optional<NguoiDung> result = nguoiDungRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public void softDeleteById(String id) {
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findActiveById(id);
        if (nguoiDungOpt.isPresent()) {
            NguoiDung nguoiDung = nguoiDungOpt.get();
            nguoiDung.setIsDeleted(true);
            nguoiDungRepository.save(nguoiDung);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public NguoiDung findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email không được để trống");
        }
        return nguoiDungRepository.findByEmail(email.trim()).orElse(null);
    }
} 
