package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.TonKhoChiTiet;
import com.example.mini_supermarket.service.TonKhoChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tonkhochitiet")
public class TonKhoChiTietRestController {

    @Autowired
    private TonKhoChiTietService tonKhoChiTietService;

    @GetMapping
    public ResponseEntity<List<TonKhoChiTiet>> getAllTonKhoChiTiet() {
        List<TonKhoChiTiet> tonKhoChiTiets = tonKhoChiTietService.findAll();
        return ResponseEntity.ok(tonKhoChiTiets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TonKhoChiTiet> getTonKhoChiTietById(@PathVariable Integer id) {
        TonKhoChiTiet tonKhoChiTiet = tonKhoChiTietService.findById(id);
        if (tonKhoChiTiet != null) {
            return ResponseEntity.ok(tonKhoChiTiet);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TonKhoChiTiet> createTonKhoChiTiet(@RequestBody TonKhoChiTiet tonKhoChiTiet) {
        TonKhoChiTiet savedTonKhoChiTiet = tonKhoChiTietService.save(tonKhoChiTiet);
        return ResponseEntity.ok(savedTonKhoChiTiet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TonKhoChiTiet> updateTonKhoChiTiet(@PathVariable Integer id, @RequestBody TonKhoChiTiet tonKhoChiTiet) {
        TonKhoChiTiet existingTonKhoChiTiet = tonKhoChiTietService.findById(id);
        if (existingTonKhoChiTiet != null) {
            tonKhoChiTiet.setMaTKCT(id);
            TonKhoChiTiet updatedTonKhoChiTiet = tonKhoChiTietService.save(tonKhoChiTiet);
            return ResponseEntity.ok(updatedTonKhoChiTiet);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTonKhoChiTiet(@PathVariable Integer id) {
        TonKhoChiTiet tonKhoChiTiet = tonKhoChiTietService.findById(id);
        if (tonKhoChiTiet != null) {
            tonKhoChiTietService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 