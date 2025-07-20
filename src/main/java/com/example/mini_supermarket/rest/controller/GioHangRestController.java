package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.GioHang;
import com.example.mini_supermarket.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/giohang")
public class GioHangRestController {

    @Autowired
    private GioHangService gioHangService;

    @GetMapping
    public ResponseEntity<List<GioHang>> getAllGioHang() {
        List<GioHang> gioHangs = gioHangService.findAllActive();
        return ResponseEntity.ok(gioHangs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GioHang> getGioHangById(@PathVariable Integer id) {
        GioHang gioHang = gioHangService.findActiveById(id);
        if (gioHang != null) {
            return ResponseEntity.ok(gioHang);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<GioHang> createGioHang(@RequestBody GioHang gioHang) {
        gioHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
        GioHang savedGioHang = gioHangService.save(gioHang);
        return ResponseEntity.ok(savedGioHang);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GioHang> updateGioHang(@PathVariable Integer id, @RequestBody GioHang gioHang) {
        GioHang existingGioHang = gioHangService.findActiveById(id);
        if (existingGioHang != null) {
            gioHang.setMaGH(id);
            gioHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            GioHang updatedGioHang = gioHangService.save(gioHang);
            return ResponseEntity.ok(updatedGioHang);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGioHang(@PathVariable Integer id) {
        GioHang gioHang = gioHangService.findActiveById(id);
        if (gioHang != null) {
            gioHangService.softDeleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 