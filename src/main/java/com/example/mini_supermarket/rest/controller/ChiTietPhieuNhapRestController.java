package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.ChiTietPhieuNhap;
import com.example.mini_supermarket.service.ChiTietPhieuNhapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chitietphieunhap")
public class ChiTietPhieuNhapRestController {

    @Autowired
    private ChiTietPhieuNhapService chiTietPhieuNhapService;

    @GetMapping
    public ResponseEntity<List<ChiTietPhieuNhap>> getAllChiTietPhieuNhap() {
        List<ChiTietPhieuNhap> chiTietPhieuNhaps = chiTietPhieuNhapService.findAll();
        return ResponseEntity.ok(chiTietPhieuNhaps);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChiTietPhieuNhap> getChiTietPhieuNhapById(@PathVariable Integer id) {
        ChiTietPhieuNhap chiTietPhieuNhap = chiTietPhieuNhapService.findById(id);
        if (chiTietPhieuNhap != null) {
            return ResponseEntity.ok(chiTietPhieuNhap);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ChiTietPhieuNhap> createChiTietPhieuNhap(@RequestBody ChiTietPhieuNhap chiTietPhieuNhap) {
        ChiTietPhieuNhap savedChiTietPhieuNhap = chiTietPhieuNhapService.save(chiTietPhieuNhap);
        return ResponseEntity.ok(savedChiTietPhieuNhap);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChiTietPhieuNhap> updateChiTietPhieuNhap(@PathVariable Integer id, @RequestBody ChiTietPhieuNhap chiTietPhieuNhap) {
        ChiTietPhieuNhap existingChiTietPhieuNhap = chiTietPhieuNhapService.findById(id);
        if (existingChiTietPhieuNhap != null) {
            chiTietPhieuNhap.setMaCTPN(id);
            ChiTietPhieuNhap updatedChiTietPhieuNhap = chiTietPhieuNhapService.save(chiTietPhieuNhap);
            return ResponseEntity.ok(updatedChiTietPhieuNhap);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChiTietPhieuNhap(@PathVariable Integer id) {
        ChiTietPhieuNhap chiTietPhieuNhap = chiTietPhieuNhapService.findById(id);
        if (chiTietPhieuNhap != null) {
            chiTietPhieuNhapService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 