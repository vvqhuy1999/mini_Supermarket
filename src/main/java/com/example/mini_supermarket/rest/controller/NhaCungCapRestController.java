package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.NhaCungCap;
import com.example.mini_supermarket.service.NhaCungCapService;
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
@RequestMapping("/api/nhacungcap")
@CrossOrigin(origins = "*")
@Tag(name = "Nhà cung cấp", description = "API quản lý nhà cung cấp")
public class NhaCungCapRestController {

    @Autowired
    private NhaCungCapService nhaCungCapService;

    @Operation(summary = "Lấy tất cả nhà cung cấp", description = "Trả về danh sách tất cả nhà cung cấp chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NhaCungCap.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<NhaCungCap>> getAllNhaCungCap() {
        try {
            List<NhaCungCap> nhaCungCaps = nhaCungCapService.findAllActive();
            return new ResponseEntity<>(nhaCungCaps, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy nhà cung cấp theo ID", description = "Trả về thông tin nhà cung cấp theo ID (chỉ lấy nhà cung cấp chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy nhà cung cấp", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NhaCungCap.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhà cung cấp"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NhaCungCap> getNhaCungCapById(
            @Parameter(description = "ID của nhà cung cấp", required = true) @PathVariable String id) {
        try {
            NhaCungCap nhaCungCap = nhaCungCapService.findActiveById(id);
            if (nhaCungCap != null) {
                return new ResponseEntity<>(nhaCungCap, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm nhà cung cấp mới", description = "Tạo một nhà cung cấp mới trong hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo nhà cung cấp thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NhaCungCap.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<NhaCungCap> createNhaCungCap(@RequestBody NhaCungCap nhaCungCap) {
        try {
            nhaCungCap.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            NhaCungCap savedNhaCungCap = nhaCungCapService.save(nhaCungCap);
            return new ResponseEntity<>(savedNhaCungCap, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật nhà cung cấp", description = "Cập nhật thông tin nhà cung cấp theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NhaCungCap.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhà cung cấp"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<NhaCungCap> updateNhaCungCap(
            @Parameter(description = "ID của nhà cung cấp", required = true) @PathVariable String id, 
            @RequestBody NhaCungCap nhaCungCap) {
        try {
            NhaCungCap existingNhaCungCap = nhaCungCapService.findActiveById(id);
            if (existingNhaCungCap != null) {
                nhaCungCap.setMaNCC(id);
                nhaCungCap.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                NhaCungCap updatedNhaCungCap = nhaCungCapService.save(nhaCungCap);
                return new ResponseEntity<>(updatedNhaCungCap, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa nhà cung cấp", description = "Xóa mềm nhà cung cấp (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhà cung cấp"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteNhaCungCap(
            @Parameter(description = "ID của nhà cung cấp", required = true) @PathVariable String id) {
        try {
            NhaCungCap nhaCungCap = nhaCungCapService.findActiveById(id);
            if (nhaCungCap != null) {
                nhaCungCapService.softDeleteById(id);
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