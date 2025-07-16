package com.example.mini_supermarket.dao;

import com.example.mini_supermarket.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhanVienRepository extends JpaRepository<NhanVien, String> {
} 