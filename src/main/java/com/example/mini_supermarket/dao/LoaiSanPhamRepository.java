package com.example.mini_supermarket.dao;

import com.example.mini_supermarket.entity.LoaiSanPham;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoaiSanPhamRepository extends JpaRepository<LoaiSanPham, String> {
} 