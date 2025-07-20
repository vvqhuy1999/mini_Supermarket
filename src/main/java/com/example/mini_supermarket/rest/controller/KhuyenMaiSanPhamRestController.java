package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.KhuyenMaiSanPham;
import com.example.mini_supermarket.service.KhuyenMaiSanPhamService;
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
@RequestMapping("/api/khuyenmaisanpham")
@CrossOrigin(origins = "*")
@Tag(name = "Khuyến mãi sản phẩm", description = "API quản lý khuyến mãi cho sản phẩm")
public class KhuyenMaiSanPhamRestController {

    @Autowired
    private KhuyenMaiSanPhamService khuyenMaiSanPhamService;

    @Operation(summary = "Lấy tất cả khuyến mãi sản phẩm", description = "Trả về danh sách tất cả khuyến mãi sản phẩm chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhuyenMaiSanPham.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<KhuyenMaiSanPham>> getAllKhuyenMaiSanPham() {
        try {
            List<KhuyenMaiSanPham> khuyenMaiSanPhams = khuyenMaiSanPhamService.findAllActive();
            return new ResponseEntity<>(khuyenMaiSanPhams, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy khuyến mãi sản phẩm theo ID", description = "Trả về thông tin khuyến mãi sản phẩm theo ID (chỉ lấy khuyến mãi sản phẩm chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy khuyến mãi sản phẩm", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhuyenMaiSanPham.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi sản phẩm"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<KhuyenMaiSanPham> getKhuyenMaiSanPhamById(
            @Parameter(description = "ID của khuyến mãi sản phẩm", required = true) @PathVariable Integer id) {
        try {
            KhuyenMaiSanPham khuyenMaiSanPham = khuyenMaiSanPhamService.findActiveById(id);
            if (khuyenMaiSanPham != null) {
                return new ResponseEntity<>(khuyenMaiSanPham, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm khuyến mãi sản phẩm mới", description = "Áp dụng khuyến mãi cho sản phẩm cụ thể")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Áp dụng khuyến mãi thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhuyenMaiSanPham.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<KhuyenMaiSanPham> createKhuyenMaiSanPham(@RequestBody KhuyenMaiSanPham khuyenMaiSanPham) {
        try {
            khuyenMaiSanPham.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            KhuyenMaiSanPham savedKhuyenMaiSanPham = khuyenMaiSanPhamService.save(khuyenMaiSanPham);
            return new ResponseEntity<>(savedKhuyenMaiSanPham, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật khuyến mãi sản phẩm", description = "Cập nhật thông tin khuyến mãi sản phẩm theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhuyenMaiSanPham.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi sản phẩm"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<KhuyenMaiSanPham> updateKhuyenMaiSanPham(
            @Parameter(description = "ID của khuyến mãi sản phẩm", required = true) @PathVariable Integer id, 
            @RequestBody KhuyenMaiSanPham khuyenMaiSanPham) {
        try {
            KhuyenMaiSanPham existingKhuyenMaiSanPham = khuyenMaiSanPhamService.findActiveById(id);
            if (existingKhuyenMaiSanPham != null) {
                khuyenMaiSanPham.setMaKMSP(id);
                khuyenMaiSanPham.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                KhuyenMaiSanPham updatedKhuyenMaiSanPham = khuyenMaiSanPhamService.save(khuyenMaiSanPham);
                return new ResponseEntity<>(updatedKhuyenMaiSanPham, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa khuyến mãi sản phẩm", description = "Xóa mềm khuyến mãi sản phẩm (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi sản phẩm"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteKhuyenMaiSanPham(
            @Parameter(description = "ID của khuyến mãi sản phẩm", required = true) @PathVariable Integer id) {
        try {
            KhuyenMaiSanPham khuyenMaiSanPham = khuyenMaiSanPhamService.findActiveById(id);
            if (khuyenMaiSanPham != null) {
                khuyenMaiSanPhamService.softDeleteById(id);
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