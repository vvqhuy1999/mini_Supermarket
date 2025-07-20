package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.service.NguoiDungService;
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
@RequestMapping("/api/nguoidung")
@CrossOrigin(origins = "*")
@Tag(name = "Người dùng", description = "API quản lý người dùng")
public class NguoiDungRestController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @Operation(summary = "Lấy tất cả người dùng", description = "Trả về danh sách tất cả người dùng chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NguoiDung.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<NguoiDung>> getAllNguoiDung() {
        try {
            List<NguoiDung> nguoiDungs = nguoiDungService.findAllActive();
            return new ResponseEntity<>(nguoiDungs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy người dùng theo ID", description = "Trả về thông tin người dùng theo ID (chỉ lấy người dùng chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy người dùng", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NguoiDung.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NguoiDung> getNguoiDungById(
            @Parameter(description = "ID của người dùng", required = true) @PathVariable String id) {
        try {
            NguoiDung nguoiDung = nguoiDungService.findActiveById(id);
            if (nguoiDung != null) {
                return new ResponseEntity<>(nguoiDung, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm người dùng mới", description = "Tạo một tài khoản người dùng mới trong hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo người dùng thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NguoiDung.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<NguoiDung> createNguoiDung(@RequestBody NguoiDung nguoiDung) {
        try {
            nguoiDung.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            NguoiDung savedNguoiDung = nguoiDungService.save(nguoiDung);
            return new ResponseEntity<>(savedNguoiDung, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật người dùng", description = "Cập nhật thông tin người dùng theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NguoiDung.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<NguoiDung> updateNguoiDung(
            @Parameter(description = "ID của người dùng", required = true) @PathVariable String id, 
            @RequestBody NguoiDung nguoiDung) {
        try {
            NguoiDung existingNguoiDung = nguoiDungService.findActiveById(id);
            if (existingNguoiDung != null) {
                nguoiDung.setMaNguoiDung(id);
                nguoiDung.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                NguoiDung updatedNguoiDung = nguoiDungService.save(nguoiDung);
                return new ResponseEntity<>(updatedNguoiDung, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa người dùng", description = "Xóa mềm người dùng (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteNguoiDung(
            @Parameter(description = "ID của người dùng", required = true) @PathVariable String id) {
        try {
            NguoiDung nguoiDung = nguoiDungService.findActiveById(id);
            if (nguoiDung != null) {
                nguoiDungService.softDeleteById(id);
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