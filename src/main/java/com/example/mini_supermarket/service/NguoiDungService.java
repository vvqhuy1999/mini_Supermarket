package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.NguoiDung;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NguoiDungService {
    List<NguoiDung> findAll();

    List<NguoiDung> findAllActive(); // Chỉ lấy các record chưa bị xóa

    NguoiDung findById(String id);

    NguoiDung findActiveById(String id); // Chỉ lấy record chưa bị xóa

    NguoiDung save(NguoiDung nguoiDung);

    void deleteById(String id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(String id); // Soft delete - set isDeleted = true

    NguoiDung update(NguoiDung nguoiDung);
} 