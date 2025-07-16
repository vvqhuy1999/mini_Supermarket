package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.GiaSanPham;
import com.example.mini_supermarket.service.GiaSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/giasanpham")
public class GiaSanPhamRestController {

    @Autowired
    private GiaSanPhamService giaSanPhamService;

    @GetMapping
    public ResponseEntity<List<GiaSanPham>> getAllGiaSanPham() {
        List<GiaSanPham> giaSanPhams = giaSanPhamService.findAll();
        return ResponseEntity.ok(giaSanPhams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GiaSanPham> getGiaSanPhamById(@PathVariable Integer id) {
        GiaSanPham giaSanPham = giaSanPhamService.findById(id);
        if (giaSanPham != null) {
            return ResponseEntity.ok(giaSanPham);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<GiaSanPham> createGiaSanPham(@RequestBody GiaSanPham giaSanPham) {
        GiaSanPham savedGiaSanPham = giaSanPhamService.save(giaSanPham);
        return ResponseEntity.ok(savedGiaSanPham);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GiaSanPham> updateGiaSanPham(@PathVariable Integer id, @RequestBody GiaSanPham giaSanPham) {
        GiaSanPham existingGiaSanPham = giaSanPhamService.findById(id);
        if (existingGiaSanPham != null) {
            giaSanPham.setMaGia(id);
            GiaSanPham updatedGiaSanPham = giaSanPhamService.save(giaSanPham);
            return ResponseEntity.ok(updatedGiaSanPham);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGiaSanPham(@PathVariable Integer id) {
        GiaSanPham giaSanPham = giaSanPhamService.findById(id);
        if (giaSanPham != null) {
            giaSanPhamService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 