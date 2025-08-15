package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.GioHang;
import com.example.mini_supermarket.service.GioHangService;
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

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/giohang")
@CrossOrigin(origins = "*")
@Tag(name = "GioHang API", description = "API quản lý Giỏ hàng")
public class GioHangRestController {

    @Autowired
    private GioHangService gioHangService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả giỏ hàng", description = "Trả về danh sách tất cả giỏ hàng chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GioHang.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<List<GioHang>> getAllGioHang() {
        try {
            List<GioHang> gioHangs = gioHangService.findAllActive();
            return new ResponseEntity<>(gioHangs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy giỏ hàng theo ID", description = "Trả về thông tin giỏ hàng theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy giỏ hàng",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GioHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giỏ hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<GioHang> getGioHangById(
            @Parameter(description = "ID của giỏ hàng", required = true) @PathVariable("id") Integer id) {
        try {
            GioHang gioHang = gioHangService.findActiveById(id);
            if (gioHang != null) {
                return new ResponseEntity<>(gioHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @Operation(summary = "Tạo giỏ hàng mới", description = "Tạo một giỏ hàng mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo giỏ hàng thành công",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GioHang.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<GioHang> createGioHang(@RequestBody GioHang gioHang) {
        try {
            // Set default values if not provided
            if (gioHang.getNgayTao() == null) {
                gioHang.setNgayTao(LocalDateTime.now());
            }
            if (gioHang.getTrangThai() == null) {
                gioHang.setTrangThai(0); // Đang chọn hàng
            }
            gioHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa

            GioHang savedGioHang = gioHangService.save(gioHang);
            return new ResponseEntity<>(savedGioHang, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật giỏ hàng", description = "Cập nhật thông tin giỏ hàng theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GioHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giỏ hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<GioHang> updateGioHang(
            @Parameter(description = "ID của giỏ hàng", required = true) @PathVariable("id") Integer id,
            @RequestBody GioHang gioHang) {
        try {
            GioHang existingGioHang = gioHangService.findActiveById(id);
            if (existingGioHang != null) {
                gioHang.setMaGH(id);
                gioHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa

                // Preserve original creation date if not provided
                if (gioHang.getNgayTao() == null) {
                    gioHang.setNgayTao(existingGioHang.getNgayTao());
                }

                gioHang.setNgayCapNhat(LocalDateTime.now());

                GioHang updatedGioHang = gioHangService.save(gioHang);
                return new ResponseEntity<>(updatedGioHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa giỏ hàng", description = "Xóa mềm giỏ hàng theo ID (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giỏ hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<HttpStatus> deleteGioHang(
            @Parameter(description = "ID của giỏ hàng", required = true) @PathVariable("id") Integer id) {
        try {
            GioHang gioHang = gioHangService.findActiveById(id);
            if (gioHang != null) {
                gioHangService.softDeleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}