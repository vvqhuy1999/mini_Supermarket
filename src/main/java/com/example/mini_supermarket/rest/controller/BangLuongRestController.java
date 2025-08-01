package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.BangLuong;
import com.example.mini_supermarket.service.BangLuongService;
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
@RequestMapping("/api/bangluong")
@CrossOrigin(origins = "*")
@Tag(name = "Bảng lương", description = "API quản lý bảng lương nhân viên")
public class BangLuongRestController {

    @Autowired
    private BangLuongService bangLuongService;

    @Operation(summary = "Lấy tất cả bảng lương", description = "Trả về danh sách tất cả bảng lương chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = BangLuong.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<BangLuong>> getAllBangLuong() {
        try {
            List<BangLuong> bangLuongs = bangLuongService.findAllActive();
            return new ResponseEntity<>(bangLuongs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy bảng lương theo ID", description = "Trả về thông tin bảng lương theo ID (chỉ lấy bảng lương chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy bảng lương", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = BangLuong.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bảng lương"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BangLuong> getBangLuongById(
            @Parameter(description = "ID của bảng lương", required = true) @PathVariable Integer id) {
        try {
            BangLuong bangLuong = bangLuongService.findActiveById(id);
            if (bangLuong != null) {
                return new ResponseEntity<>(bangLuong, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm bảng lương mới", description = "Tạo bảng lương mới cho nhân viên")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo bảng lương thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = BangLuong.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<BangLuong> createBangLuong(@RequestBody BangLuong bangLuong) {
        try {
            bangLuong.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            BangLuong savedBangLuong = bangLuongService.save(bangLuong);
            return new ResponseEntity<>(savedBangLuong, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật bảng lương", description = "Cập nhật thông tin bảng lương")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = BangLuong.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bảng lương"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BangLuong> updateBangLuong(
            @Parameter(description = "ID của bảng lương", required = true) @PathVariable Integer id, 
            @RequestBody BangLuong bangLuong) {
        try {
            BangLuong existingBangLuong = bangLuongService.findActiveById(id);
            if (existingBangLuong != null) {
                bangLuong.setMaLuong(id); // Đảm bảo ID không bị thay đổi
                bangLuong.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                BangLuong updatedBangLuong = bangLuongService.save(bangLuong);
                return new ResponseEntity<>(updatedBangLuong, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa bảng lương", description = "Xóa bảng lương (xóa mềm)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bảng lương"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteBangLuong(
            @Parameter(description = "ID của bảng lương", required = true) @PathVariable Integer id) {
        try {
            BangLuong existingBangLuong = bangLuongService.findActiveById(id);
            if (existingBangLuong != null) {
                bangLuongService.softDeleteById(id);
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