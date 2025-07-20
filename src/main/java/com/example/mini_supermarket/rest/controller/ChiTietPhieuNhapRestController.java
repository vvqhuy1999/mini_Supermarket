package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.ChiTietPhieuNhap;
import com.example.mini_supermarket.service.ChiTietPhieuNhapService;
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
@RequestMapping("/api/chitietphieunhap")
@CrossOrigin(origins = "*")
@Tag(name = "Chi tiết phiếu nhập", description = "API quản lý chi tiết phiếu nhập hàng")
public class ChiTietPhieuNhapRestController {

    @Autowired
    private ChiTietPhieuNhapService chiTietPhieuNhapService;

    @Operation(summary = "Lấy tất cả chi tiết phiếu nhập", description = "Trả về danh sách tất cả chi tiết phiếu nhập chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietPhieuNhap.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<ChiTietPhieuNhap>> getAllChiTietPhieuNhap() {
        try {
            List<ChiTietPhieuNhap> chiTietPhieuNhaps = chiTietPhieuNhapService.findAllActive();
            return new ResponseEntity<>(chiTietPhieuNhaps, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy chi tiết phiếu nhập theo ID", description = "Trả về thông tin chi tiết phiếu nhập theo ID (chỉ lấy chi tiết phiếu nhập chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy chi tiết phiếu nhập", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietPhieuNhap.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết phiếu nhập"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ChiTietPhieuNhap> getChiTietPhieuNhapById(
            @Parameter(description = "ID của chi tiết phiếu nhập", required = true) @PathVariable Integer id) {
        try {
            ChiTietPhieuNhap chiTietPhieuNhap = chiTietPhieuNhapService.findActiveById(id);
            if (chiTietPhieuNhap != null) {
                return new ResponseEntity<>(chiTietPhieuNhap, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm chi tiết phiếu nhập mới", description = "Thêm sản phẩm vào phiếu nhập hàng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thêm chi tiết phiếu nhập thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietPhieuNhap.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<ChiTietPhieuNhap> createChiTietPhieuNhap(@RequestBody ChiTietPhieuNhap chiTietPhieuNhap) {
        try {
            chiTietPhieuNhap.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            ChiTietPhieuNhap savedChiTietPhieuNhap = chiTietPhieuNhapService.save(chiTietPhieuNhap);
            return new ResponseEntity<>(savedChiTietPhieuNhap, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật chi tiết phiếu nhập", description = "Cập nhật thông tin chi tiết phiếu nhập theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietPhieuNhap.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết phiếu nhập"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ChiTietPhieuNhap> updateChiTietPhieuNhap(
            @Parameter(description = "ID của chi tiết phiếu nhập", required = true) @PathVariable Integer id, 
            @RequestBody ChiTietPhieuNhap chiTietPhieuNhap) {
        try {
            ChiTietPhieuNhap existingChiTietPhieuNhap = chiTietPhieuNhapService.findActiveById(id);
            if (existingChiTietPhieuNhap != null) {
                chiTietPhieuNhap.setMaCTPN(id);
                chiTietPhieuNhap.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                ChiTietPhieuNhap updatedChiTietPhieuNhap = chiTietPhieuNhapService.save(chiTietPhieuNhap);
                return new ResponseEntity<>(updatedChiTietPhieuNhap, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa chi tiết phiếu nhập", description = "Xóa mềm chi tiết phiếu nhập (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết phiếu nhập"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteChiTietPhieuNhap(
            @Parameter(description = "ID của chi tiết phiếu nhập", required = true) @PathVariable Integer id) {
        try {
            ChiTietPhieuNhap chiTietPhieuNhap = chiTietPhieuNhapService.findActiveById(id);
            if (chiTietPhieuNhap != null) {
                chiTietPhieuNhapService.softDeleteById(id);
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