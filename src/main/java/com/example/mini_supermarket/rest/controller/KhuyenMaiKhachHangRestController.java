package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.KhuyenMaiKhachHang;
import com.example.mini_supermarket.service.KhuyenMaiKhachHangService;
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
@RequestMapping("/api/khuyenmaikhachhang")
@CrossOrigin(origins = "*")
@Tag(name = "Khuyến mãi khách hàng", description = "API quản lý khuyến mãi dành cho khách hàng")
public class KhuyenMaiKhachHangRestController {

    @Autowired
    private KhuyenMaiKhachHangService khuyenMaiKhachHangService;

    @Operation(summary = "Lấy tất cả khuyến mãi khách hàng", description = "Trả về danh sách tất cả khuyến mãi khách hàng chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhuyenMaiKhachHang.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<KhuyenMaiKhachHang>> getAllKhuyenMaiKhachHang() {
        try {
            List<KhuyenMaiKhachHang> khuyenMaiKhachHangs = khuyenMaiKhachHangService.findAllActive();
            return new ResponseEntity<>(khuyenMaiKhachHangs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy khuyến mãi khách hàng theo ID", description = "Trả về thông tin khuyến mãi khách hàng theo ID (chỉ lấy khuyến mãi khách hàng chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy khuyến mãi khách hàng", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhuyenMaiKhachHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi khách hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<KhuyenMaiKhachHang> getKhuyenMaiKhachHangById(
            @Parameter(description = "ID của khuyến mãi khách hàng", required = true) @PathVariable Integer id) {
        try {
            KhuyenMaiKhachHang khuyenMaiKhachHang = khuyenMaiKhachHangService.findActiveById(id);
            if (khuyenMaiKhachHang != null) {
                return new ResponseEntity<>(khuyenMaiKhachHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm khuyến mãi khách hàng mới", description = "Áp dụng khuyến mãi cho khách hàng cụ thể")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Áp dụng khuyến mãi thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhuyenMaiKhachHang.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<KhuyenMaiKhachHang> createKhuyenMaiKhachHang(@RequestBody KhuyenMaiKhachHang khuyenMaiKhachHang) {
        try {
            khuyenMaiKhachHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            KhuyenMaiKhachHang savedKhuyenMaiKhachHang = khuyenMaiKhachHangService.save(khuyenMaiKhachHang);
            return new ResponseEntity<>(savedKhuyenMaiKhachHang, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật khuyến mãi khách hàng", description = "Cập nhật thông tin khuyến mãi khách hàng theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhuyenMaiKhachHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi khách hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<KhuyenMaiKhachHang> updateKhuyenMaiKhachHang(
            @Parameter(description = "ID của khuyến mãi khách hàng", required = true) @PathVariable Integer id, 
            @RequestBody KhuyenMaiKhachHang khuyenMaiKhachHang) {
        try {
            KhuyenMaiKhachHang existingKhuyenMaiKhachHang = khuyenMaiKhachHangService.findActiveById(id);
            if (existingKhuyenMaiKhachHang != null) {
                khuyenMaiKhachHang.setMaKMKH(id);
                khuyenMaiKhachHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                KhuyenMaiKhachHang updatedKhuyenMaiKhachHang = khuyenMaiKhachHangService.save(khuyenMaiKhachHang);
                return new ResponseEntity<>(updatedKhuyenMaiKhachHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa khuyến mãi khách hàng", description = "Xóa mềm khuyến mãi khách hàng (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi khách hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteKhuyenMaiKhachHang(
            @Parameter(description = "ID của khuyến mãi khách hàng", required = true) @PathVariable Integer id) {
        try {
            KhuyenMaiKhachHang khuyenMaiKhachHang = khuyenMaiKhachHangService.findActiveById(id);
            if (khuyenMaiKhachHang != null) {
                khuyenMaiKhachHangService.softDeleteById(id);
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