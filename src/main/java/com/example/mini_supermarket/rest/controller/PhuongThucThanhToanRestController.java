package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.PhuongThucThanhToan;
import com.example.mini_supermarket.service.PhuongThucThanhToanService;
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
@RequestMapping("/api/phuongthucthanhtoan")
@CrossOrigin(origins = "*")
@Tag(name = "Phương thức thanh toán", description = "API quản lý phương thức thanh toán")
public class PhuongThucThanhToanRestController {

    @Autowired
    private PhuongThucThanhToanService phuongThucThanhToanService;

    @Operation(summary = "Lấy tất cả phương thức thanh toán", description = "Trả về danh sách tất cả phương thức thanh toán chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = PhuongThucThanhToan.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<PhuongThucThanhToan>> getAllPhuongThucThanhToan() {
        try {
            List<PhuongThucThanhToan> phuongThucThanhToans = phuongThucThanhToanService.findAllActive();
            return new ResponseEntity<>(phuongThucThanhToans, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy phương thức thanh toán theo ID", description = "Trả về thông tin phương thức thanh toán theo ID (chỉ lấy phương thức thanh toán chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy phương thức thanh toán", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = PhuongThucThanhToan.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phương thức thanh toán"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PhuongThucThanhToan> getPhuongThucThanhToanById(
            @Parameter(description = "ID của phương thức thanh toán", required = true) @PathVariable String id) {
        try {
            PhuongThucThanhToan phuongThucThanhToan = phuongThucThanhToanService.findActiveById(id);
            if (phuongThucThanhToan != null) {
                return new ResponseEntity<>(phuongThucThanhToan, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm phương thức thanh toán mới", description = "Tạo một phương thức thanh toán mới trong hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo phương thức thanh toán thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = PhuongThucThanhToan.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<PhuongThucThanhToan> createPhuongThucThanhToan(@RequestBody PhuongThucThanhToan phuongThucThanhToan) {
        try {
            phuongThucThanhToan.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            PhuongThucThanhToan savedPhuongThucThanhToan = phuongThucThanhToanService.save(phuongThucThanhToan);
            return new ResponseEntity<>(savedPhuongThucThanhToan, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật phương thức thanh toán", description = "Cập nhật thông tin phương thức thanh toán theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = PhuongThucThanhToan.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phương thức thanh toán"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PhuongThucThanhToan> updatePhuongThucThanhToan(
            @Parameter(description = "ID của phương thức thanh toán", required = true) @PathVariable String id, 
            @RequestBody PhuongThucThanhToan phuongThucThanhToan) {
        try {
            PhuongThucThanhToan existingPhuongThucThanhToan = phuongThucThanhToanService.findActiveById(id);
            if (existingPhuongThucThanhToan != null) {
                phuongThucThanhToan.setMaPTTT(id);
                phuongThucThanhToan.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                PhuongThucThanhToan updatedPhuongThucThanhToan = phuongThucThanhToanService.save(phuongThucThanhToan);
                return new ResponseEntity<>(updatedPhuongThucThanhToan, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa phương thức thanh toán", description = "Xóa mềm phương thức thanh toán (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phương thức thanh toán"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePhuongThucThanhToan(
            @Parameter(description = "ID của phương thức thanh toán", required = true) @PathVariable String id) {
        try {
            PhuongThucThanhToan phuongThucThanhToan = phuongThucThanhToanService.findActiveById(id);
            if (phuongThucThanhToan != null) {
                phuongThucThanhToanService.softDeleteById(id);
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