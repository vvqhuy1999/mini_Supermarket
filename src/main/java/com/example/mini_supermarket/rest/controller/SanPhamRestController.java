package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.SanPham;
import com.example.mini_supermarket.service.SanPhamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sanpham")
@CrossOrigin(origins = "*")
@Tag(name = "Sản phẩm", description = "API quản lý sản phẩm")
public class SanPhamRestController {

    @Autowired
    private SanPhamService sanPhamService;

    @Operation(summary = "Lấy tất cả sản phẩm", description = "Trả về danh sách tất cả sản phẩm chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = SanPham.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<SanPham>> getAllSanPham() {
        try {
            List<SanPham> sanPhams = sanPhamService.findAllActive();
            return new ResponseEntity<>(sanPhams, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy sản phẩm theo ID", description = "Trả về thông tin sản phẩm theo ID (chỉ lấy sản phẩm chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy sản phẩm", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = SanPham.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SanPham> getSanPhamById(
            @Parameter(description = "ID của sản phẩm", required = true) @PathVariable String id) {
        try {
            SanPham sanPham = sanPhamService.findActiveById(id);
            if (sanPham != null) {
                return new ResponseEntity<>(sanPham, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Thêm sản phẩm mới
    @PostMapping
    public ResponseEntity<SanPham> createSanPham(@RequestBody SanPham sanPham) {
        try {
            SanPham savedSanPham = sanPhamService.save(sanPham);
            return new ResponseEntity<>(savedSanPham, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<SanPham> updateSanPham(@PathVariable String id, @RequestBody SanPham sanPham) {
        try {
            SanPham existingSanPham = sanPhamService.findActiveById(id);
            if (existingSanPham != null) {
                sanPham.setMaSP(id); // Đảm bảo ID không thay đổi
                sanPham.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                SanPham updatedSanPham = sanPhamService.update(sanPham);
                return new ResponseEntity<>(updatedSanPham, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Xóa sản phẩm (soft delete - chỉ đánh dấu isDeleted = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSanPham(@PathVariable String id) {
        try {
            SanPham existingSanPham = sanPhamService.findActiveById(id);
            if (existingSanPham != null) {
                sanPhamService.softDeleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 