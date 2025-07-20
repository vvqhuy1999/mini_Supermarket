package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.LoaiSanPham;
import com.example.mini_supermarket.service.LoaiSanPhamService;
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
@RequestMapping("/api/loaisanpham")
@CrossOrigin(origins = "*")
@Tag(name = "Loại sản phẩm", description = "API quản lý loại sản phẩm")
public class LoaiSanPhamRestController {

    @Autowired
    private LoaiSanPhamService loaiSanPhamService;

    @Operation(summary = "Lấy tất cả loại sản phẩm", description = "Trả về danh sách tất cả loại sản phẩm chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = LoaiSanPham.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<LoaiSanPham>> getAllLoaiSanPham() {
        try {
            List<LoaiSanPham> loaiSanPhams = loaiSanPhamService.findAllActive();
            return new ResponseEntity<>(loaiSanPhams, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy loại sản phẩm theo ID", description = "Trả về thông tin loại sản phẩm theo ID (chỉ lấy loại sản phẩm chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy loại sản phẩm", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = LoaiSanPham.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy loại sản phẩm"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LoaiSanPham> getLoaiSanPhamById(
            @Parameter(description = "ID của loại sản phẩm", required = true) @PathVariable String id) {
        try {
            LoaiSanPham loaiSanPham = loaiSanPhamService.findActiveById(id);
            if (loaiSanPham != null) {
                return new ResponseEntity<>(loaiSanPham, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm loại sản phẩm mới", description = "Tạo một loại sản phẩm mới trong hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo loại sản phẩm thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = LoaiSanPham.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<LoaiSanPham> createLoaiSanPham(@RequestBody LoaiSanPham loaiSanPham) {
        try {
            loaiSanPham.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            LoaiSanPham savedLoaiSanPham = loaiSanPhamService.save(loaiSanPham);
            return new ResponseEntity<>(savedLoaiSanPham, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật loại sản phẩm", description = "Cập nhật thông tin loại sản phẩm theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = LoaiSanPham.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy loại sản phẩm"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LoaiSanPham> updateLoaiSanPham(
            @Parameter(description = "ID của loại sản phẩm", required = true) @PathVariable String id, 
            @RequestBody LoaiSanPham loaiSanPham) {
        try {
            LoaiSanPham existingLoaiSanPham = loaiSanPhamService.findActiveById(id);
            if (existingLoaiSanPham != null) {
                loaiSanPham.setMaLoaiSP(id);
                loaiSanPham.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                LoaiSanPham updatedLoaiSanPham = loaiSanPhamService.save(loaiSanPham);
                return new ResponseEntity<>(updatedLoaiSanPham, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa loại sản phẩm", description = "Xóa mềm loại sản phẩm (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy loại sản phẩm"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteLoaiSanPham(
            @Parameter(description = "ID của loại sản phẩm", required = true) @PathVariable String id) {
        try {
            LoaiSanPham loaiSanPham = loaiSanPhamService.findActiveById(id);
            if (loaiSanPham != null) {
                loaiSanPhamService.softDeleteById(id);
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