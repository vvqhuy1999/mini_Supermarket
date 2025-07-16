package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.ThongKeBaoCao;
import com.example.mini_supermarket.service.ThongKeBaoCaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thongkebaocao")
public class ThongKeBaoCaoRestController {

    @Autowired
    private ThongKeBaoCaoService thongKeBaoCaoService;

    @GetMapping
    public ResponseEntity<List<ThongKeBaoCao>> getAllThongKeBaoCao() {
        List<ThongKeBaoCao> thongKeBaoCaos = thongKeBaoCaoService.findAll();
        return ResponseEntity.ok(thongKeBaoCaos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThongKeBaoCao> getThongKeBaoCaoById(@PathVariable Integer id) {
        ThongKeBaoCao thongKeBaoCao = thongKeBaoCaoService.findById(id);
        if (thongKeBaoCao != null) {
            return ResponseEntity.ok(thongKeBaoCao);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ThongKeBaoCao> createThongKeBaoCao(@RequestBody ThongKeBaoCao thongKeBaoCao) {
        ThongKeBaoCao savedThongKeBaoCao = thongKeBaoCaoService.save(thongKeBaoCao);
        return ResponseEntity.ok(savedThongKeBaoCao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThongKeBaoCao> updateThongKeBaoCao(@PathVariable Integer id, @RequestBody ThongKeBaoCao thongKeBaoCao) {
        ThongKeBaoCao existingThongKeBaoCao = thongKeBaoCaoService.findById(id);
        if (existingThongKeBaoCao != null) {
            thongKeBaoCao.setMaBaoCao(id);
            ThongKeBaoCao updatedThongKeBaoCao = thongKeBaoCaoService.save(thongKeBaoCao);
            return ResponseEntity.ok(updatedThongKeBaoCao);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThongKeBaoCao(@PathVariable Integer id) {
        ThongKeBaoCao thongKeBaoCao = thongKeBaoCaoService.findById(id);
        if (thongKeBaoCao != null) {
            thongKeBaoCaoService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 