package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.ThanhToan;
import com.example.mini_supermarket.service.ThanhToanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thanhtoan")
public class ThanhToanRestController {

    @Autowired
    private ThanhToanService thanhToanService;

    @GetMapping
    public ResponseEntity<List<ThanhToan>> getAllThanhToan() {
        List<ThanhToan> thanhToans = thanhToanService.findAll();
        return ResponseEntity.ok(thanhToans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThanhToan> getThanhToanById(@PathVariable Integer id) {
        ThanhToan thanhToan = thanhToanService.findById(id);
        if (thanhToan != null) {
            return ResponseEntity.ok(thanhToan);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ThanhToan> createThanhToan(@RequestBody ThanhToan thanhToan) {
        ThanhToan savedThanhToan = thanhToanService.save(thanhToan);
        return ResponseEntity.ok(savedThanhToan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThanhToan> updateThanhToan(@PathVariable Integer id, @RequestBody ThanhToan thanhToan) {
        ThanhToan existingThanhToan = thanhToanService.findById(id);
        if (existingThanhToan != null) {
            thanhToan.setMaTT(id);
            ThanhToan updatedThanhToan = thanhToanService.save(thanhToan);
            return ResponseEntity.ok(updatedThanhToan);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThanhToan(@PathVariable Integer id) {
        ThanhToan thanhToan = thanhToanService.findById(id);
        if (thanhToan != null) {
            thanhToanService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 