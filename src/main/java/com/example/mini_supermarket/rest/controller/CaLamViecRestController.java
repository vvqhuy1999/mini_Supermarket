package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.CaLamViec;
import com.example.mini_supermarket.service.CaLamViecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calamviec")
public class CaLamViecRestController {

    @Autowired
    private CaLamViecService caLamViecService;

    @GetMapping
    public ResponseEntity<List<CaLamViec>> getAllCaLamViec() {
        List<CaLamViec> caLamViecs = caLamViecService.findAll();
        return ResponseEntity.ok(caLamViecs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaLamViec> getCaLamViecById(@PathVariable Integer id) {
        CaLamViec caLamViec = caLamViecService.findById(id);
        if (caLamViec != null) {
            return ResponseEntity.ok(caLamViec);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<CaLamViec> createCaLamViec(@RequestBody CaLamViec caLamViec) {
        CaLamViec savedCaLamViec = caLamViecService.save(caLamViec);
        return ResponseEntity.ok(savedCaLamViec);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaLamViec> updateCaLamViec(@PathVariable Integer id, @RequestBody CaLamViec caLamViec) {
        CaLamViec existingCaLamViec = caLamViecService.findById(id);
        if (existingCaLamViec != null) {
            caLamViec.setMaCa(id);
            CaLamViec updatedCaLamViec = caLamViecService.save(caLamViec);
            return ResponseEntity.ok(updatedCaLamViec);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCaLamViec(@PathVariable Integer id) {
        CaLamViec caLamViec = caLamViecService.findById(id);
        if (caLamViec != null) {
            caLamViecService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 