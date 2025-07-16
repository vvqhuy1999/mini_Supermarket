package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.PhieuXuatKho;
import com.example.mini_supermarket.service.PhieuXuatKhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phieuxuatkho")
public class PhieuXuatKhoRestController {

    @Autowired
    private PhieuXuatKhoService phieuXuatKhoService;

    @GetMapping
    public ResponseEntity<List<PhieuXuatKho>> getAllPhieuXuatKho() {
        List<PhieuXuatKho> phieuXuatKhos = phieuXuatKhoService.findAll();
        return ResponseEntity.ok(phieuXuatKhos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhieuXuatKho> getPhieuXuatKhoById(@PathVariable Integer id) {
        PhieuXuatKho phieuXuatKho = phieuXuatKhoService.findById(id);
        if (phieuXuatKho != null) {
            return ResponseEntity.ok(phieuXuatKho);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PhieuXuatKho> createPhieuXuatKho(@RequestBody PhieuXuatKho phieuXuatKho) {
        PhieuXuatKho savedPhieuXuatKho = phieuXuatKhoService.save(phieuXuatKho);
        return ResponseEntity.ok(savedPhieuXuatKho);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhieuXuatKho> updatePhieuXuatKho(@PathVariable Integer id, @RequestBody PhieuXuatKho phieuXuatKho) {
        PhieuXuatKho existingPhieuXuatKho = phieuXuatKhoService.findById(id);
        if (existingPhieuXuatKho != null) {
            phieuXuatKho.setMaPXK(id);
            PhieuXuatKho updatedPhieuXuatKho = phieuXuatKhoService.save(phieuXuatKho);
            return ResponseEntity.ok(updatedPhieuXuatKho);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhieuXuatKho(@PathVariable Integer id) {
        PhieuXuatKho phieuXuatKho = phieuXuatKhoService.findById(id);
        if (phieuXuatKho != null) {
            phieuXuatKhoService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 