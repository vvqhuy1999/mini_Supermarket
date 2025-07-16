package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.ChiTietGioHang;
import com.example.mini_supermarket.service.ChiTietGioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chitietgiohang")
public class ChiTietGioHangRestController {

    @Autowired
    private ChiTietGioHangService chiTietGioHangService;

    @GetMapping
    public ResponseEntity<List<ChiTietGioHang>> getAllChiTietGioHang() {
        List<ChiTietGioHang> chiTietGioHangs = chiTietGioHangService.findAll();
        return ResponseEntity.ok(chiTietGioHangs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChiTietGioHang> getChiTietGioHangById(@PathVariable Integer id) {
        ChiTietGioHang chiTietGioHang = chiTietGioHangService.findById(id);
        if (chiTietGioHang != null) {
            return ResponseEntity.ok(chiTietGioHang);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ChiTietGioHang> createChiTietGioHang(@RequestBody ChiTietGioHang chiTietGioHang) {
        ChiTietGioHang savedChiTietGioHang = chiTietGioHangService.save(chiTietGioHang);
        return ResponseEntity.ok(savedChiTietGioHang);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChiTietGioHang> updateChiTietGioHang(@PathVariable Integer id, @RequestBody ChiTietGioHang chiTietGioHang) {
        ChiTietGioHang existingChiTietGioHang = chiTietGioHangService.findById(id);
        if (existingChiTietGioHang != null) {
            chiTietGioHang.setMaCTGH(id);
            ChiTietGioHang updatedChiTietGioHang = chiTietGioHangService.save(chiTietGioHang);
            return ResponseEntity.ok(updatedChiTietGioHang);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChiTietGioHang(@PathVariable Integer id) {
        ChiTietGioHang chiTietGioHang = chiTietGioHangService.findById(id);
        if (chiTietGioHang != null) {
            chiTietGioHangService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 