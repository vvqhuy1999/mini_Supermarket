package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.ThanhToan;
import com.example.mini_supermarket.service.ThanhToanService;
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
@RequestMapping("/api/thanhtoan")
@CrossOrigin(origins = "*")
@Tag(name = "Thanh toán", description = "API quản lý thanh toán")
public class ThanhToanRestController {

    @Autowired
    private ThanhToanService thanhToanService;

    @Operation(summary = "Lấy tất cả thanh toán", description = "Trả về danh sách tất cả thanh toán chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ThanhToan.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<ThanhToan>> getAllThanhToan() {
        try {
            List<ThanhToan> thanhToans = thanhToanService.findAllActive();
            return new ResponseEntity<>(thanhToans, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy thanh toán theo ID", description = "Trả về thông tin thanh toán theo ID (chỉ lấy thanh toán chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy thanh toán", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ThanhToan.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thanh toán"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ThanhToan> getThanhToanById(
            @Parameter(description = "ID của thanh toán", required = true) @PathVariable Integer id) {
        try {
            ThanhToan thanhToan = thanhToanService.findActiveById(id);
            if (thanhToan != null) {
                return new ResponseEntity<>(thanhToan, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Tạo thanh toán mới", description = "Tạo một giao dịch thanh toán mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo thanh toán thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ThanhToan.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<ThanhToan> createThanhToan(@RequestBody ThanhToan thanhToan) {
        try {
            thanhToan.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            ThanhToan savedThanhToan = thanhToanService.save(thanhToan);
            return new ResponseEntity<>(savedThanhToan, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật thanh toán", description = "Cập nhật thông tin thanh toán theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ThanhToan.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thanh toán"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ThanhToan> updateThanhToan(
            @Parameter(description = "ID của thanh toán", required = true) @PathVariable Integer id, 
            @RequestBody ThanhToan thanhToan) {
        try {
            ThanhToan existingThanhToan = thanhToanService.findActiveById(id);
            if (existingThanhToan != null) {
                thanhToan.setMaTT(id);
                thanhToan.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                ThanhToan updatedThanhToan = thanhToanService.save(thanhToan);
                return new ResponseEntity<>(updatedThanhToan, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa thanh toán", description = "Xóa mềm thanh toán (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thanh toán"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteThanhToan(
            @Parameter(description = "ID của thanh toán", required = true) @PathVariable Integer id) {
        try {
            ThanhToan thanhToan = thanhToanService.findActiveById(id);
            if (thanhToan != null) {
                thanhToanService.softDeleteById(id);
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