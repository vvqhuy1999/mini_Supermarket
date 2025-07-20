package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.NhanVien;
import com.example.mini_supermarket.service.NhanVienService;
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
@RequestMapping("/api/nhanvien")
@CrossOrigin(origins = "*")
@Tag(name = "Nhân viên", description = "API quản lý nhân viên")
public class NhanVienRestController {

    @Autowired
    private NhanVienService nhanVienService;

    @Operation(summary = "Lấy tất cả nhân viên", description = "Trả về danh sách tất cả nhân viên chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NhanVien.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<NhanVien>> getAllNhanVien() {
        try {
            List<NhanVien> nhanViens = nhanVienService.findAllActive();
            return new ResponseEntity<>(nhanViens, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy nhân viên theo ID", description = "Trả về thông tin nhân viên theo ID (chỉ lấy nhân viên chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy nhân viên", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NhanVien.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NhanVien> getNhanVienById(
            @Parameter(description = "ID của nhân viên", required = true) @PathVariable String id) {
        try {
            NhanVien nhanVien = nhanVienService.findActiveById(id);
            if (nhanVien != null) {
                return new ResponseEntity<>(nhanVien, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm nhân viên mới", description = "Tạo một nhân viên mới trong hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo nhân viên thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NhanVien.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<NhanVien> createNhanVien(@RequestBody NhanVien nhanVien) {
        try {
            nhanVien.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            NhanVien savedNhanVien = nhanVienService.save(nhanVien);
            return new ResponseEntity<>(savedNhanVien, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật nhân viên", description = "Cập nhật thông tin nhân viên theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NhanVien.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<NhanVien> updateNhanVien(
            @Parameter(description = "ID của nhân viên", required = true) @PathVariable String id, 
            @RequestBody NhanVien nhanVien) {
        try {
            NhanVien existingNhanVien = nhanVienService.findActiveById(id);
            if (existingNhanVien != null) {
                nhanVien.setMaNV(id);
                nhanVien.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                NhanVien updatedNhanVien = nhanVienService.save(nhanVien);
                return new ResponseEntity<>(updatedNhanVien, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa nhân viên", description = "Xóa mềm nhân viên (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteNhanVien(
            @Parameter(description = "ID của nhân viên", required = true) @PathVariable String id) {
        try {
            NhanVien nhanVien = nhanVienService.findActiveById(id);
            if (nhanVien != null) {
                nhanVienService.softDeleteById(id);
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