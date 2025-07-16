package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.PhieuNhapHang;
import com.example.mini_supermarket.service.PhieuNhapHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phieunhaphang")
public class PhieuNhapHangRestController {

    @Autowired
    private PhieuNhapHangService phieuNhapHangService;

    @GetMapping
    public ResponseEntity<List<PhieuNhapHang>> getAllPhieuNhapHang() {
        List<PhieuNhapHang> phieuNhapHangs = phieuNhapHangService.findAll();
        return ResponseEntity.ok(phieuNhapHangs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhieuNhapHang> getPhieuNhapHangById(@PathVariable Integer id) {
        PhieuNhapHang phieuNhapHang = phieuNhapHangService.findById(id);
        if (phieuNhapHang != null) {
            return ResponseEntity.ok(phieuNhapHang);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PhieuNhapHang> createPhieuNhapHang(@RequestBody PhieuNhapHang phieuNhapHang) {
        PhieuNhapHang savedPhieuNhapHang = phieuNhapHangService.save(phieuNhapHang);
        return ResponseEntity.ok(savedPhieuNhapHang);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhieuNhapHang> updatePhieuNhapHang(@PathVariable Integer id, @RequestBody PhieuNhapHang phieuNhapHang) {
        PhieuNhapHang existingPhieuNhapHang = phieuNhapHangService.findById(id);
        if (existingPhieuNhapHang != null) {
            phieuNhapHang.setMaPN(id);
            PhieuNhapHang updatedPhieuNhapHang = phieuNhapHangService.save(phieuNhapHang);
            return ResponseEntity.ok(updatedPhieuNhapHang);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhieuNhapHang(@PathVariable Integer id) {
        PhieuNhapHang phieuNhapHang = phieuNhapHangService.findById(id);
        if (phieuNhapHang != null) {
            phieuNhapHangService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 