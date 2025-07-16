package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.NhanVien;
import com.example.mini_supermarket.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nhanvien")
public class NhanVienRestController {

    @Autowired
    private NhanVienService nhanVienService;

    @GetMapping
    public ResponseEntity<List<NhanVien>> getAllNhanVien() {
        List<NhanVien> nhanViens = nhanVienService.findAll();
        return ResponseEntity.ok(nhanViens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NhanVien> getNhanVienById(@PathVariable String id) {
        NhanVien nhanVien = nhanVienService.findById(id);
        if (nhanVien != null) {
            return ResponseEntity.ok(nhanVien);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<NhanVien> createNhanVien(@RequestBody NhanVien nhanVien) {
        NhanVien savedNhanVien = nhanVienService.save(nhanVien);
        return ResponseEntity.ok(savedNhanVien);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NhanVien> updateNhanVien(@PathVariable String id, @RequestBody NhanVien nhanVien) {
        NhanVien existingNhanVien = nhanVienService.findById(id);
        if (existingNhanVien != null) {
            nhanVien.setMaNV(id);
            NhanVien updatedNhanVien = nhanVienService.save(nhanVien);
            return ResponseEntity.ok(updatedNhanVien);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNhanVien(@PathVariable String id) {
        NhanVien nhanVien = nhanVienService.findById(id);
        if (nhanVien != null) {
            nhanVienService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 