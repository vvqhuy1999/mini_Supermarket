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
        List<Kho> khos = khoService.findAll();
        return ResponseEntity.ok(khos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kho> getKhoById(@PathVariable Integer id) {
        Kho kho = khoService.findById(id);
        if (kho != null) {
            return ResponseEntity.ok(kho);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Kho> createKho(@RequestBody Kho kho) {
        Kho savedKho = khoService.save(kho);
        return ResponseEntity.ok(savedKho);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Kho> updateKho(@PathVariable Integer id, @RequestBody Kho kho) {
        Kho existingKho = khoService.findById(id);
        if (existingKho != null) {
            kho.setMaKho(id);
            Kho updatedKho = khoService.save(kho);
            return ResponseEntity.ok(updatedKho);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKho(@PathVariable Integer id) {
        Kho kho = khoService.findById(id);
        if (kho != null) {
            khoService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 