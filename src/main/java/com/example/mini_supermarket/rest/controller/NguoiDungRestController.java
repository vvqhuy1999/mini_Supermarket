package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nguoidung")
public class NguoiDungRestController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping
    public ResponseEntity<List<NguoiDung>> getAllNguoiDung() {
        List<NguoiDung> nguoiDungs = nguoiDungService.findAll();
        return ResponseEntity.ok(nguoiDungs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NguoiDung> getNguoiDungById(@PathVariable String id) {
        NguoiDung nguoiDung = nguoiDungService.findById(id);
        if (nguoiDung != null) {
            return ResponseEntity.ok(nguoiDung);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<NguoiDung> createNguoiDung(@RequestBody NguoiDung nguoiDung) {
        NguoiDung savedNguoiDung = nguoiDungService.save(nguoiDung);
        return ResponseEntity.ok(savedNguoiDung);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NguoiDung> updateNguoiDung(@PathVariable String id, @RequestBody NguoiDung nguoiDung) {
        NguoiDung existingNguoiDung = nguoiDungService.findById(id);
        if (existingNguoiDung != null) {
            nguoiDung.setMaNguoiDung(id);
            NguoiDung updatedNguoiDung = nguoiDungService.save(nguoiDung);
            return ResponseEntity.ok(updatedNguoiDung);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNguoiDung(@PathVariable String id) {
        NguoiDung nguoiDung = nguoiDungService.findById(id);
        if (nguoiDung != null) {
            nguoiDungService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 