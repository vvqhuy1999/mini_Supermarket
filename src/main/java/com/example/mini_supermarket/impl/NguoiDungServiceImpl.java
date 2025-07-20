package com.example.mini_supermarket.impl;

import com.example.mini_supermarket.dao.NguoiDungRepository;
import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<NguoiDung> findAll() {
        return nguoiDungRepository.findAll();
    }

    @Override
    public NguoiDung findById(String theId) {
        Optional<NguoiDung> result = nguoiDungRepository.findById(theId);
        NguoiDung theNguoiDung = null;

        if (result.isPresent()) {
            theNguoiDung = result.get();
        } else {
            throw new RuntimeException("Did not find NguoiDung id - " + theId);
        }
        return theNguoiDung;
    }

    @Override
    public NguoiDung save(NguoiDung theNguoiDung) {
        return nguoiDungRepository.save(theNguoiDung);
    }

    @Override
    public void deleteById(String theId) {
        nguoiDungRepository.deleteById(theId);
    }

    @Override
    public NguoiDung update(NguoiDung nguoiDung) {
        Optional<NguoiDung> existingNguoiDung = nguoiDungRepository.findById(nguoiDung.getMaNguoiDung());

        if (!existingNguoiDung.isPresent()) {
            throw new RuntimeException("Không tìm thấy người dùng với ID - " + nguoiDung.getMaNguoiDung());
        }

        return nguoiDungRepository.save(nguoiDung);
    }

    @Override
    public List<NguoiDung> findAllActive() {
        return nguoiDungRepository.findAllActive();
    }

    @Override
    public NguoiDung findActiveById(String id) {
        Optional<NguoiDung> result = nguoiDungRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    public void softDeleteById(String id) {
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findActiveById(id);
        if (nguoiDungOpt.isPresent()) {
            NguoiDung nguoiDung = nguoiDungOpt.get();
            nguoiDung.setIsDeleted(true);
            nguoiDungRepository.save(nguoiDung);
        }
    }
} 