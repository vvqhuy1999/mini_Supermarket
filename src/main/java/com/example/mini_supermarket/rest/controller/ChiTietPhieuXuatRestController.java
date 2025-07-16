package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.ChiTietPhieuXuat;
import com.example.mini_supermarket.service.ChiTietPhieuXuatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chitietphieuxuat")
public class ChiTietPhieuXuatRestController {

    @Autowired
    private ChiTietPhieuXuatService chiTietPhieuXuatService;

    @GetMapping
    public ResponseEntity<List<ChiTietPhieuXuat>> getAllChiTietPhieuXuat() {
        List<ChiTietPhieuXuat> chiTietPhieuXuats = chiTietPhieuXuatService.findAll();
        return ResponseEntity.ok(chiTietPhieuXuats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChiTietPhieuXuat> getChiTietPhieuXuatById(@PathVariable Integer id) {
        ChiTietPhieuXuat chiTietPhieuXuat = chiTietPhieuXuatService.findById(id);
        if (chiTietPhieuXuat != null) {
            return ResponseEntity.ok(chiTietPhieuXuat);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ChiTietPhieuXuat> createChiTietPhieuXuat(@RequestBody ChiTietPhieuXuat chiTietPhieuXuat) {
        ChiTietPhieuXuat savedChiTietPhieuXuat = chiTietPhieuXuatService.save(chiTietPhieuXuat);
        return ResponseEntity.ok(savedChiTietPhieuXuat);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChiTietPhieuXuat> updateChiTietPhieuXuat(@PathVariable Integer id, @RequestBody ChiTietPhieuXuat chiTietPhieuXuat) {
        ChiTietPhieuXuat existingChiTietPhieuXuat = chiTietPhieuXuatService.findById(id);
        if (existingChiTietPhieuXuat != null) {
            chiTietPhieuXuat.setMaCTPXK(id);
            ChiTietPhieuXuat updatedChiTietPhieuXuat = chiTietPhieuXuatService.save(chiTietPhieuXuat);
            return ResponseEntity.ok(updatedChiTietPhieuXuat);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChiTietPhieuXuat(@PathVariable Integer id) {
        ChiTietPhieuXuat chiTietPhieuXuat = chiTietPhieuXuatService.findById(id);
        if (chiTietPhieuXuat != null) {
            chiTietPhieuXuatService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 