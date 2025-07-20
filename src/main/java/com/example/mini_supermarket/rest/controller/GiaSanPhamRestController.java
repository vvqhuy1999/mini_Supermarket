package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.GiaSanPham;
import com.example.mini_supermarket.service.GiaSanPhamService;
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
@RequestMapping("/api/giasanpham")
@CrossOrigin(origins = "*")
@Tag(name = "Giá sản phẩm", description = "API quản lý giá sản phẩm")
public class GiaSanPhamRestController {

    @Autowired
    private GiaSanPhamService giaSanPhamService;

    @Operation(summary = "Lấy tất cả giá sản phẩm", description = "Trả về danh sách tất cả giá sản phẩm chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = GiaSanPham.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<GiaSanPham>> getAllGiaSanPham() {
        try {
            List<GiaSanPham> giaSanPhams = giaSanPhamService.findAllActive();
            return new ResponseEntity<>(giaSanPhams, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy giá sản phẩm theo ID", description = "Trả về thông tin giá sản phẩm theo ID (chỉ lấy giá sản phẩm chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy giá sản phẩm", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = GiaSanPham.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giá sản phẩm"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GiaSanPham> getGiaSanPhamById(
            @Parameter(description = "ID của giá sản phẩm", required = true) @PathVariable Integer id) {
        try {
            GiaSanPham giaSanPham = giaSanPhamService.findActiveById(id);
            if (giaSanPham != null) {
                return new ResponseEntity<>(giaSanPham, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm giá sản phẩm mới", description = "Tạo một mức giá mới cho sản phẩm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo giá sản phẩm thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = GiaSanPham.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<GiaSanPham> createGiaSanPham(@RequestBody GiaSanPham giaSanPham) {
        try {
            giaSanPham.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            GiaSanPham savedGiaSanPham = giaSanPhamService.save(giaSanPham);
            return new ResponseEntity<>(savedGiaSanPham, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật giá sản phẩm", description = "Cập nhật thông tin giá sản phẩm theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = GiaSanPham.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giá sản phẩm"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GiaSanPham> updateGiaSanPham(
            @Parameter(description = "ID của giá sản phẩm", required = true) @PathVariable Integer id, 
            @RequestBody GiaSanPham giaSanPham) {
        try {
            GiaSanPham existingGiaSanPham = giaSanPhamService.findActiveById(id);
            if (existingGiaSanPham != null) {
                giaSanPham.setMaGia(id);
                giaSanPham.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                GiaSanPham updatedGiaSanPham = giaSanPhamService.save(giaSanPham);
                return new ResponseEntity<>(updatedGiaSanPham, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa giá sản phẩm", description = "Xóa mềm giá sản phẩm (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giá sản phẩm"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteGiaSanPham(
            @Parameter(description = "ID của giá sản phẩm", required = true) @PathVariable Integer id) {
        try {
            GiaSanPham giaSanPham = giaSanPhamService.findActiveById(id);
            if (giaSanPham != null) {
                giaSanPhamService.softDeleteById(id);
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