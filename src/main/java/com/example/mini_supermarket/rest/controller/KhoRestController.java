package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.Kho;
import com.example.mini_supermarket.service.KhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kho")
public class KhoRestController {

    @Autowired
    private KhoService khoService;

    @GetMapping
    public ResponseEntity<List<Kho>> getAllKho() {
        List<Kho> khos = khoService.findAllActive();
        return ResponseEntity.ok(khos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kho> getKhoById(@PathVariable Integer id) {
        Kho kho = khoService.findActiveById(id);
        if (kho != null) {
            return ResponseEntity.ok(kho);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Kho> createKho(@RequestBody Kho kho) {
        kho.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
        Kho savedKho = khoService.save(kho);
        return ResponseEntity.ok(savedKho);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Kho> updateKho(@PathVariable Integer id, @RequestBody Kho kho) {
        Kho existingKho = khoService.findActiveById(id);
        if (existingKho != null) {
            kho.setMaKho(id);
            kho.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            Kho updatedKho = khoService.save(kho);
            return ResponseEntity.ok(updatedKho);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKho(@PathVariable Integer id) {
        Kho kho = khoService.findActiveById(id);
        if (kho != null) {
            khoService.softDeleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 