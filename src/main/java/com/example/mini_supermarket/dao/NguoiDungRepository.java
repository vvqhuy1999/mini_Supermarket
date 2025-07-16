package com.example.mini_supermarket.dao;

import com.example.mini_supermarket.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, String> {
} 