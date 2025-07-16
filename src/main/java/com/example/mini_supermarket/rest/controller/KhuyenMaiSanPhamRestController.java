package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.KhuyenMaiSanPham;
import com.example.mini_supermarket.service.KhuyenMaiSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khuyenmaisanpham")
public class KhuyenMaiSanPhamRestController {

    @Autowired
    private KhuyenMaiSanPhamService khuyenMaiSanPhamService;

    @GetMapping
    public ResponseEntity<List<KhuyenMaiSanPham>> getAllKhuyenMaiSanPham() {
        List<KhuyenMaiSanPham> khuyenMaiSanPhams = khuyenMaiSanPhamService.findAll();
        return ResponseEntity.ok(khuyenMaiSanPhams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KhuyenMaiSanPham> getKhuyenMaiSanPhamById(@PathVariable Integer id) {
        KhuyenMaiSanPham khuyenMaiSanPham = khuyenMaiSanPhamService.findById(id);
        if (khuyenMaiSanPham != null) {
            return ResponseEntity.ok(khuyenMaiSanPham);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<KhuyenMaiSanPham> createKhuyenMaiSanPham(@RequestBody KhuyenMaiSanPham khuyenMaiSanPham) {
        KhuyenMaiSanPham savedKhuyenMaiSanPham = khuyenMaiSanPhamService.save(khuyenMaiSanPham);
        return ResponseEntity.ok(savedKhuyenMaiSanPham);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KhuyenMaiSanPham> updateKhuyenMaiSanPham(@PathVariable Integer id, @RequestBody KhuyenMaiSanPham khuyenMaiSanPham) {
        KhuyenMaiSanPham existingKhuyenMaiSanPham = khuyenMaiSanPhamService.findById(id);
        if (existingKhuyenMaiSanPham != null) {
            khuyenMaiSanPham.setMaKMSP(id);
            KhuyenMaiSanPham updatedKhuyenMaiSanPham = khuyenMaiSanPhamService.save(khuyenMaiSanPham);
            return ResponseEntity.ok(updatedKhuyenMaiSanPham);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKhuyenMaiSanPham(@PathVariable Integer id) {
        KhuyenMaiSanPham khuyenMaiSanPham = khuyenMaiSanPhamService.findById(id);
        if (khuyenMaiSanPham != null) {
            khuyenMaiSanPhamService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 