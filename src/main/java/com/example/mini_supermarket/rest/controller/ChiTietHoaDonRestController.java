package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.ChiTietHoaDon;
import com.example.mini_supermarket.service.ChiTietHoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chitiethoadon")
public class ChiTietHoaDonRestController {

    @Autowired
    private ChiTietHoaDonService chiTietHoaDonService;

    @GetMapping
    public ResponseEntity<List<ChiTietHoaDon>> getAllChiTietHoaDon() {
        List<ChiTietHoaDon> chiTietHoaDons = chiTietHoaDonService.findAll();
        return ResponseEntity.ok(chiTietHoaDons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChiTietHoaDon> getChiTietHoaDonById(@PathVariable Integer id) {
        ChiTietHoaDon chiTietHoaDon = chiTietHoaDonService.findById(id);
        if (chiTietHoaDon != null) {
            return ResponseEntity.ok(chiTietHoaDon);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ChiTietHoaDon> createChiTietHoaDon(@RequestBody ChiTietHoaDon chiTietHoaDon) {
        ChiTietHoaDon savedChiTietHoaDon = chiTietHoaDonService.save(chiTietHoaDon);
        return ResponseEntity.ok(savedChiTietHoaDon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChiTietHoaDon> updateChiTietHoaDon(@PathVariable Integer id, @RequestBody ChiTietHoaDon chiTietHoaDon) {
        ChiTietHoaDon existingChiTietHoaDon = chiTietHoaDonService.findById(id);
        if (existingChiTietHoaDon != null) {
            chiTietHoaDon.setMaCTHD(id);
            ChiTietHoaDon updatedChiTietHoaDon = chiTietHoaDonService.save(chiTietHoaDon);
            return ResponseEntity.ok(updatedChiTietHoaDon);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChiTietHoaDon(@PathVariable Integer id) {
        ChiTietHoaDon chiTietHoaDon = chiTietHoaDonService.findById(id);
        if (chiTietHoaDon != null) {
            chiTietHoaDonService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 