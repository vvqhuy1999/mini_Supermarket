package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.KhachHang;
import com.example.mini_supermarket.service.KhachHangService;
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
@RequestMapping("/api/khachhang")
@CrossOrigin(origins = "*")
@Tag(name = "Khách hàng", description = "API quản lý khách hàng")
public class KhachHangRestController {

    @Autowired
    private KhachHangService khachHangService;

    @Operation(summary = "Lấy tất cả khách hàng", description = "Trả về danh sách tất cả khách hàng chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhachHang.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<KhachHang>> getAllKhachHang() {
        try {
            List<KhachHang> khachHangs = khachHangService.findAllActive();
            return new ResponseEntity<>(khachHangs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy khách hàng theo ID", description = "Trả về thông tin khách hàng theo ID (chỉ lấy khách hàng chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy khách hàng", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhachHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<KhachHang> getKhachHangById(
            @Parameter(description = "ID của khách hàng", required = true) @PathVariable String id) {
        try {
            KhachHang khachHang = khachHangService.findActiveById(id);
            if (khachHang != null) {
                return new ResponseEntity<>(khachHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Thêm khách hàng mới
    @PostMapping
    public ResponseEntity<KhachHang> createKhachHang(@RequestBody KhachHang khachHang) {
        try {
            KhachHang savedKhachHang = khachHangService.save(khachHang);
            return new ResponseEntity<>(savedKhachHang, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Cập nhật khách hàng
    @PutMapping("/{id}")
    public ResponseEntity<KhachHang> updateKhachHang(@PathVariable String id, @RequestBody KhachHang khachHang) {
        try {
            KhachHang existingKhachHang = khachHangService.findActiveById(id);
            if (existingKhachHang != null) {
                khachHang.setMaKH(id); // Đảm bảo ID không thay đổi
                khachHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                KhachHang updatedKhachHang = khachHangService.update(khachHang);
                return new ResponseEntity<>(updatedKhachHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Xóa khách hàng (soft delete - chỉ đánh dấu isDeleted = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteKhachHang(@PathVariable String id) {
        try {
            KhachHang existingKhachHang = khachHangService.findActiveById(id);
            if (existingKhachHang != null) {
                khachHangService.softDeleteById(id);
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