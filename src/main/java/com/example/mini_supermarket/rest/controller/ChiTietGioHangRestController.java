package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.ChiTietGioHang;
import com.example.mini_supermarket.service.ChiTietGioHangService;
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
@RequestMapping("/api/chitietgiohang")
@CrossOrigin(origins = "*")
@Tag(name = "Chi tiết giỏ hàng", description = "API quản lý chi tiết giỏ hàng")
public class ChiTietGioHangRestController {

    @Autowired
    private ChiTietGioHangService chiTietGioHangService;

    @Operation(summary = "Lấy tất cả chi tiết giỏ hàng", description = "Trả về danh sách tất cả chi tiết giỏ hàng chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietGioHang.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<ChiTietGioHang>> getAllChiTietGioHang() {
        try {
            List<ChiTietGioHang> chiTietGioHangs = chiTietGioHangService.findAllActive();
            return new ResponseEntity<>(chiTietGioHangs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy chi tiết giỏ hàng theo ID", description = "Trả về thông tin chi tiết giỏ hàng theo ID (chỉ lấy chi tiết giỏ hàng chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy chi tiết giỏ hàng", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietGioHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết giỏ hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ChiTietGioHang> getChiTietGioHangById(
            @Parameter(description = "ID của chi tiết giỏ hàng", required = true) @PathVariable Integer id) {
        try {
            ChiTietGioHang chiTietGioHang = chiTietGioHangService.findActiveById(id);
            if (chiTietGioHang != null) {
                return new ResponseEntity<>(chiTietGioHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm chi tiết giỏ hàng mới", description = "Thêm sản phẩm vào giỏ hàng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thêm vào giỏ hàng thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietGioHang.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<ChiTietGioHang> createChiTietGioHang(@RequestBody ChiTietGioHang chiTietGioHang) {
        try {
            chiTietGioHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            ChiTietGioHang savedChiTietGioHang = chiTietGioHangService.save(chiTietGioHang);
            return new ResponseEntity<>(savedChiTietGioHang, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật chi tiết giỏ hàng", description = "Cập nhật số lượng sản phẩm trong giỏ hàng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietGioHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết giỏ hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ChiTietGioHang> updateChiTietGioHang(
            @Parameter(description = "ID của chi tiết giỏ hàng", required = true) @PathVariable Integer id, 
            @RequestBody ChiTietGioHang chiTietGioHang) {
        try {
            ChiTietGioHang existingChiTietGioHang = chiTietGioHangService.findActiveById(id);
            if (existingChiTietGioHang != null) {
                chiTietGioHang.setMaCTGH(id);
                chiTietGioHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                ChiTietGioHang updatedChiTietGioHang = chiTietGioHangService.save(chiTietGioHang);
                return new ResponseEntity<>(updatedChiTietGioHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa chi tiết giỏ hàng", description = "Xóa sản phẩm khỏi giỏ hàng (xóa mềm)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết giỏ hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteChiTietGioHang(
            @Parameter(description = "ID của chi tiết giỏ hàng", required = true) @PathVariable Integer id) {
        try {
            ChiTietGioHang chiTietGioHang = chiTietGioHangService.findActiveById(id);
            if (chiTietGioHang != null) {
                chiTietGioHangService.softDeleteById(id);
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