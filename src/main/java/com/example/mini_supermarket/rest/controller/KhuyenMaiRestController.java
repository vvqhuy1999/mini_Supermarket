package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.KhuyenMai;
import com.example.mini_supermarket.service.KhuyenMaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khuyenmai")
public class KhuyenMaiRestController {

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @GetMapping
    public ResponseEntity<List<KhuyenMai>> getAllKhuyenMai() {
        List<KhuyenMai> khuyenMais = khuyenMaiService.findAll();
        return ResponseEntity.ok(khuyenMais);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KhuyenMai> getKhuyenMaiById(@PathVariable String id) {
        KhuyenMai khuyenMai = khuyenMaiService.findById(id);
        if (khuyenMai != null) {
            return ResponseEntity.ok(khuyenMai);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<KhuyenMai> createKhuyenMai(@RequestBody KhuyenMai khuyenMai) {
        KhuyenMai savedKhuyenMai = khuyenMaiService.save(khuyenMai);
        return ResponseEntity.ok(savedKhuyenMai);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KhuyenMai> updateKhuyenMai(@PathVariable String id, @RequestBody KhuyenMai khuyenMai) {
        KhuyenMai existingKhuyenMai = khuyenMaiService.findById(id);
        if (existingKhuyenMai != null) {
            khuyenMai.setMaKM(id);
            KhuyenMai updatedKhuyenMai = khuyenMaiService.save(khuyenMai);
            return ResponseEntity.ok(updatedKhuyenMai);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKhuyenMai(@PathVariable String id) {
        KhuyenMai khuyenMai = khuyenMaiService.findById(id);
        if (khuyenMai != null) {
            khuyenMaiService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 