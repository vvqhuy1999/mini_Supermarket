package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.NhaCungCap;
import com.example.mini_supermarket.service.NhaCungCapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nhacungcap")
public class NhaCungCapRestController {

    @Autowired
    private NhaCungCapService nhaCungCapService;

    @GetMapping
    public ResponseEntity<List<NhaCungCap>> getAllNhaCungCap() {
        List<NhaCungCap> nhaCungCaps = nhaCungCapService.findAll();
        return ResponseEntity.ok(nhaCungCaps);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NhaCungCap> getNhaCungCapById(@PathVariable String id) {
        NhaCungCap nhaCungCap = nhaCungCapService.findById(id);
        if (nhaCungCap != null) {
            return ResponseEntity.ok(nhaCungCap);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<NhaCungCap> createNhaCungCap(@RequestBody NhaCungCap nhaCungCap) {
        NhaCungCap savedNhaCungCap = nhaCungCapService.save(nhaCungCap);
        return ResponseEntity.ok(savedNhaCungCap);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NhaCungCap> updateNhaCungCap(@PathVariable String id, @RequestBody NhaCungCap nhaCungCap) {
        NhaCungCap existingNhaCungCap = nhaCungCapService.findById(id);
        if (existingNhaCungCap != null) {
            nhaCungCap.setMaNCC(id);
            NhaCungCap updatedNhaCungCap = nhaCungCapService.save(nhaCungCap);
            return ResponseEntity.ok(updatedNhaCungCap);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNhaCungCap(@PathVariable String id) {
        NhaCungCap nhaCungCap = nhaCungCapService.findById(id);
        if (nhaCungCap != null) {
            nhaCungCapService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 