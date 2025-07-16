package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.HinhAnh;
import com.example.mini_supermarket.service.HinhAnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hinhanh")
public class HinhAnhRestController {

    @Autowired
    private HinhAnhService hinhAnhService;

    @GetMapping
    public ResponseEntity<List<HinhAnh>> getAllHinhAnh() {
        List<HinhAnh> hinhAnhs = hinhAnhService.findAll();
        return ResponseEntity.ok(hinhAnhs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HinhAnh> getHinhAnhById(@PathVariable Integer id) {
        HinhAnh hinhAnh = hinhAnhService.findById(id);
        if (hinhAnh != null) {
            return ResponseEntity.ok(hinhAnh);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<HinhAnh> createHinhAnh(@RequestBody HinhAnh hinhAnh) {
        HinhAnh savedHinhAnh = hinhAnhService.save(hinhAnh);
        return ResponseEntity.ok(savedHinhAnh);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HinhAnh> updateHinhAnh(@PathVariable Integer id, @RequestBody HinhAnh hinhAnh) {
        HinhAnh existingHinhAnh = hinhAnhService.findById(id);
        if (existingHinhAnh != null) {
            hinhAnh.setMaHinh(id);
            HinhAnh updatedHinhAnh = hinhAnhService.save(hinhAnh);
            return ResponseEntity.ok(updatedHinhAnh);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHinhAnh(@PathVariable Integer id) {
        HinhAnh hinhAnh = hinhAnhService.findById(id);
        if (hinhAnh != null) {
            hinhAnhService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 