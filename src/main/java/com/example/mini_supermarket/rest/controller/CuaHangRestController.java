package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.CuaHang;
import com.example.mini_supermarket.service.CuaHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuahang")
public class CuaHangRestController {

    @Autowired
    private CuaHangService cuaHangService;

    @GetMapping
    public ResponseEntity<List<CuaHang>> getAllCuaHang() {
        List<CuaHang> cuaHangs = cuaHangService.findAll();
        return ResponseEntity.ok(cuaHangs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuaHang> getCuaHangById(@PathVariable String id) {
        CuaHang cuaHang = cuaHangService.findById(id);
        if (cuaHang != null) {
            return ResponseEntity.ok(cuaHang);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<CuaHang> createCuaHang(@RequestBody CuaHang cuaHang) {
        CuaHang savedCuaHang = cuaHangService.save(cuaHang);
        return ResponseEntity.ok(savedCuaHang);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuaHang> updateCuaHang(@PathVariable String id, @RequestBody CuaHang cuaHang) {
        CuaHang existingCuaHang = cuaHangService.findById(id);
        if (existingCuaHang != null) {
            cuaHang.setMaCH(id);
            CuaHang updatedCuaHang = cuaHangService.save(cuaHang);
            return ResponseEntity.ok(updatedCuaHang);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCuaHang(@PathVariable String id) {
        CuaHang cuaHang = cuaHangService.findById(id);
        if (cuaHang != null) {
            cuaHangService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 