package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.LichLamViec;
import com.example.mini_supermarket.service.LichLamViecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lichlamviec")
public class LichLamViecRestController {

    @Autowired
    private LichLamViecService lichLamViecService;

    @GetMapping
    public ResponseEntity<List<LichLamViec>> getAllLichLamViec() {
        List<LichLamViec> lichLamViecs = lichLamViecService.findAll();
        return ResponseEntity.ok(lichLamViecs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LichLamViec> getLichLamViecById(@PathVariable Integer id) {
        LichLamViec lichLamViec = lichLamViecService.findById(id);
        if (lichLamViec != null) {
            return ResponseEntity.ok(lichLamViec);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<LichLamViec> createLichLamViec(@RequestBody LichLamViec lichLamViec) {
        LichLamViec savedLichLamViec = lichLamViecService.save(lichLamViec);
        return ResponseEntity.ok(savedLichLamViec);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LichLamViec> updateLichLamViec(@PathVariable Integer id, @RequestBody LichLamViec lichLamViec) {
        LichLamViec existingLichLamViec = lichLamViecService.findById(id);
        if (existingLichLamViec != null) {
            lichLamViec.setMaLich(id);
            LichLamViec updatedLichLamViec = lichLamViecService.save(lichLamViec);
            return ResponseEntity.ok(updatedLichLamViec);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLichLamViec(@PathVariable Integer id) {
        LichLamViec lichLamViec = lichLamViecService.findById(id);
        if (lichLamViec != null) {
            lichLamViecService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 