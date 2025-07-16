package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.KhuyenMaiKhachHang;
import com.example.mini_supermarket.service.KhuyenMaiKhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khuyenmaikhachhang")
public class KhuyenMaiKhachHangRestController {

    @Autowired
    private KhuyenMaiKhachHangService khuyenMaiKhachHangService;

    @GetMapping
    public ResponseEntity<List<KhuyenMaiKhachHang>> getAllKhuyenMaiKhachHang() {
        List<KhuyenMaiKhachHang> khuyenMaiKhachHangs = khuyenMaiKhachHangService.findAll();
        return ResponseEntity.ok(khuyenMaiKhachHangs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KhuyenMaiKhachHang> getKhuyenMaiKhachHangById(@PathVariable Integer id) {
        KhuyenMaiKhachHang khuyenMaiKhachHang = khuyenMaiKhachHangService.findById(id);
        if (khuyenMaiKhachHang != null) {
            return ResponseEntity.ok(khuyenMaiKhachHang);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<KhuyenMaiKhachHang> createKhuyenMaiKhachHang(@RequestBody KhuyenMaiKhachHang khuyenMaiKhachHang) {
        KhuyenMaiKhachHang savedKhuyenMaiKhachHang = khuyenMaiKhachHangService.save(khuyenMaiKhachHang);
        return ResponseEntity.ok(savedKhuyenMaiKhachHang);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KhuyenMaiKhachHang> updateKhuyenMaiKhachHang(@PathVariable Integer id, @RequestBody KhuyenMaiKhachHang khuyenMaiKhachHang) {
        KhuyenMaiKhachHang existingKhuyenMaiKhachHang = khuyenMaiKhachHangService.findById(id);
        if (existingKhuyenMaiKhachHang != null) {
            khuyenMaiKhachHang.setMaKMKH(id);
            KhuyenMaiKhachHang updatedKhuyenMaiKhachHang = khuyenMaiKhachHangService.save(khuyenMaiKhachHang);
            return ResponseEntity.ok(updatedKhuyenMaiKhachHang);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKhuyenMaiKhachHang(@PathVariable Integer id) {
        KhuyenMaiKhachHang khuyenMaiKhachHang = khuyenMaiKhachHangService.findById(id);
        if (khuyenMaiKhachHang != null) {
            khuyenMaiKhachHangService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 