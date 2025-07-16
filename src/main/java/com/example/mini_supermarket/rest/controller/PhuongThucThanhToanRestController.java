package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.PhuongThucThanhToan;
import com.example.mini_supermarket.service.PhuongThucThanhToanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phuongthucthanhtoan")
public class PhuongThucThanhToanRestController {

    @Autowired
    private PhuongThucThanhToanService phuongThucThanhToanService;

    @GetMapping
    public ResponseEntity<List<PhuongThucThanhToan>> getAllPhuongThucThanhToan() {
        List<PhuongThucThanhToan> phuongThucThanhToans = phuongThucThanhToanService.findAll();
        return ResponseEntity.ok(phuongThucThanhToans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhuongThucThanhToan> getPhuongThucThanhToanById(@PathVariable String id) {
        PhuongThucThanhToan phuongThucThanhToan = phuongThucThanhToanService.findById(id);
        if (phuongThucThanhToan != null) {
            return ResponseEntity.ok(phuongThucThanhToan);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PhuongThucThanhToan> createPhuongThucThanhToan(@RequestBody PhuongThucThanhToan phuongThucThanhToan) {
        PhuongThucThanhToan savedPhuongThucThanhToan = phuongThucThanhToanService.save(phuongThucThanhToan);
        return ResponseEntity.ok(savedPhuongThucThanhToan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhuongThucThanhToan> updatePhuongThucThanhToan(@PathVariable String id, @RequestBody PhuongThucThanhToan phuongThucThanhToan) {
        PhuongThucThanhToan existingPhuongThucThanhToan = phuongThucThanhToanService.findById(id);
        if (existingPhuongThucThanhToan != null) {
            phuongThucThanhToan.setMaPTTT(id);
            PhuongThucThanhToan updatedPhuongThucThanhToan = phuongThucThanhToanService.save(phuongThucThanhToan);
            return ResponseEntity.ok(updatedPhuongThucThanhToan);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhuongThucThanhToan(@PathVariable String id) {
        PhuongThucThanhToan phuongThucThanhToan = phuongThucThanhToanService.findById(id);
        if (phuongThucThanhToan != null) {
            phuongThucThanhToanService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 