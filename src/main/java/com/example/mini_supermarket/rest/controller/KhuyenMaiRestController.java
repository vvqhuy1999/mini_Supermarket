package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.KhuyenMai;
import com.example.mini_supermarket.service.KhuyenMaiService;
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
@RequestMapping("/api/khuyenmai")
@CrossOrigin(origins = "*")
@Tag(name = "Khuyến mãi", description = "API quản lý khuyến mãi")
public class KhuyenMaiRestController {

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @Operation(summary = "Lấy tất cả khuyến mãi", description = "Trả về danh sách tất cả khuyến mãi chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhuyenMai.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<KhuyenMai>> getAllKhuyenMai() {
        try {
            List<KhuyenMai> khuyenMais = khuyenMaiService.findAllActive();
            return new ResponseEntity<>(khuyenMais, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy khuyến mãi theo ID", description = "Trả về thông tin khuyến mãi theo ID (chỉ lấy khuyến mãi chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy khuyến mãi", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhuyenMai.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<KhuyenMai> getKhuyenMaiById(
            @Parameter(description = "ID của khuyến mãi", required = true) @PathVariable String id) {
        try {
            KhuyenMai khuyenMai = khuyenMaiService.findActiveById(id);
            if (khuyenMai != null) {
                return new ResponseEntity<>(khuyenMai, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm khuyến mãi mới", description = "Tạo một chương trình khuyến mãi mới trong hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo khuyến mãi thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhuyenMai.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<KhuyenMai> createKhuyenMai(@RequestBody KhuyenMai khuyenMai) {
        try {
            khuyenMai.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            KhuyenMai savedKhuyenMai = khuyenMaiService.save(khuyenMai);
            return new ResponseEntity<>(savedKhuyenMai, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật khuyến mãi", description = "Cập nhật thông tin khuyến mãi theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhuyenMai.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<KhuyenMai> updateKhuyenMai(
            @Parameter(description = "ID của khuyến mãi", required = true) @PathVariable String id, 
            @RequestBody KhuyenMai khuyenMai) {
        try {
            KhuyenMai existingKhuyenMai = khuyenMaiService.findActiveById(id);
            if (existingKhuyenMai != null) {
                khuyenMai.setMaKM(id);
                khuyenMai.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                KhuyenMai updatedKhuyenMai = khuyenMaiService.save(khuyenMai);
                return new ResponseEntity<>(updatedKhuyenMai, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa khuyến mãi", description = "Xóa mềm khuyến mãi (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteKhuyenMai(
            @Parameter(description = "ID của khuyến mãi", required = true) @PathVariable String id) {
        try {
            KhuyenMai khuyenMai = khuyenMaiService.findActiveById(id);
            if (khuyenMai != null) {
                khuyenMaiService.softDeleteById(id);
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