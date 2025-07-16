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
        List<GioHang> gioHangs = gioHangService.findAll();
        return ResponseEntity.ok(gioHangs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GioHang> getGioHangById(@PathVariable Integer id) {
        GioHang gioHang = gioHangService.findById(id);
        if (gioHang != null) {
            return ResponseEntity.ok(gioHang);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<GioHang> createGioHang(@RequestBody GioHang gioHang) {
        GioHang savedGioHang = gioHangService.save(gioHang);
        return ResponseEntity.ok(savedGioHang);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GioHang> updateGioHang(@PathVariable Integer id, @RequestBody GioHang gioHang) {
        GioHang existingGioHang = gioHangService.findById(id);
        if (existingGioHang != null) {
            gioHang.setMaGH(id);
            GioHang updatedGioHang = gioHangService.save(gioHang);
            return ResponseEntity.ok(updatedGioHang);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGioHang(@PathVariable Integer id) {
        GioHang gioHang = gioHangService.findById(id);
        if (gioHang != null) {
            gioHangService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 