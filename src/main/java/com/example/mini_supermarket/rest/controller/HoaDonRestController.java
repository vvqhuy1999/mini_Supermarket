package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.HoaDon;
import com.example.mini_supermarket.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hoadon")
public class HoaDonRestController {

    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping
    public ResponseEntity<List<HoaDon>> getAllHoaDon() {
        List<HoaDon> hoaDons = hoaDonService.findAllActive();
        return ResponseEntity.ok(hoaDons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HoaDon> getHoaDonById(@PathVariable Integer id) {
        HoaDon hoaDon = hoaDonService.findActiveById(id);
        if (hoaDon != null) {
            return ResponseEntity.ok(hoaDon);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<HoaDon> createHoaDon(@RequestBody HoaDon hoaDon) {
        hoaDon.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
        HoaDon savedHoaDon = hoaDonService.save(hoaDon);
        return ResponseEntity.ok(savedHoaDon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HoaDon> updateHoaDon(@PathVariable Integer id, @RequestBody HoaDon hoaDon) {
        HoaDon existingHoaDon = hoaDonService.findActiveById(id);
        if (existingHoaDon != null) {
            hoaDon.setMaHD(id);
            hoaDon.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            HoaDon updatedHoaDon = hoaDonService.save(hoaDon);
            return ResponseEntity.ok(updatedHoaDon);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHoaDon(@PathVariable Integer id) {
        HoaDon hoaDon = hoaDonService.findActiveById(id);
        if (hoaDon != null) {
            hoaDonService.softDeleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 