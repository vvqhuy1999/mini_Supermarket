package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.LoaiSanPham;
import com.example.mini_supermarket.service.LoaiSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loaisanpham")
public class LoaiSanPhamRestController {

    @Autowired
    private LoaiSanPhamService loaiSanPhamService;

    @GetMapping
    public ResponseEntity<List<LoaiSanPham>> getAllLoaiSanPham() {
        List<LoaiSanPham> loaiSanPhams = loaiSanPhamService.findAll();
        return ResponseEntity.ok(loaiSanPhams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoaiSanPham> getLoaiSanPhamById(@PathVariable String id) {
        LoaiSanPham loaiSanPham = loaiSanPhamService.findById(id);
        if (loaiSanPham != null) {
            return ResponseEntity.ok(loaiSanPham);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<LoaiSanPham> createLoaiSanPham(@RequestBody LoaiSanPham loaiSanPham) {
        LoaiSanPham savedLoaiSanPham = loaiSanPhamService.save(loaiSanPham);
        return ResponseEntity.ok(savedLoaiSanPham);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoaiSanPham> updateLoaiSanPham(@PathVariable String id, @RequestBody LoaiSanPham loaiSanPham) {
        LoaiSanPham existingLoaiSanPham = loaiSanPhamService.findById(id);
        if (existingLoaiSanPham != null) {
            loaiSanPham.setMaLoaiSP(id);
            LoaiSanPham updatedLoaiSanPham = loaiSanPhamService.save(loaiSanPham);
            return ResponseEntity.ok(updatedLoaiSanPham);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoaiSanPham(@PathVariable String id) {
        LoaiSanPham loaiSanPham = loaiSanPhamService.findById(id);
        if (loaiSanPham != null) {
            loaiSanPhamService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 