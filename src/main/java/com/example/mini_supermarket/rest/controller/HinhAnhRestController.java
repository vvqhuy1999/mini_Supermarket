package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.HinhAnh;
import com.example.mini_supermarket.service.HinhAnhService;
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
@RequestMapping("/api/hinhanh")
@CrossOrigin(origins = "*")
@Tag(name = "Hình ảnh", description = "API quản lý hình ảnh sản phẩm")
public class HinhAnhRestController {

    @Autowired
    private HinhAnhService hinhAnhService;

    @Operation(summary = "Lấy tất cả hình ảnh", description = "Trả về danh sách tất cả hình ảnh chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = HinhAnh.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<HinhAnh>> getAllHinhAnh() {
        try {
            List<HinhAnh> hinhAnhs = hinhAnhService.findAllActive();
            return new ResponseEntity<>(hinhAnhs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy hình ảnh theo ID", description = "Trả về thông tin hình ảnh theo ID (chỉ lấy hình ảnh chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy hình ảnh", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = HinhAnh.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy hình ảnh"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<HinhAnh> getHinhAnhById(
            @Parameter(description = "ID của hình ảnh", required = true) @PathVariable Integer id) {
        try {
            HinhAnh hinhAnh = hinhAnhService.findActiveById(id);
            if (hinhAnh != null) {
                return new ResponseEntity<>(hinhAnh, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm hình ảnh mới", description = "Upload và thêm hình ảnh mới cho sản phẩm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo hình ảnh thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = HinhAnh.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<HinhAnh> createHinhAnh(@RequestBody HinhAnh hinhAnh) {
        try {
            hinhAnh.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            HinhAnh savedHinhAnh = hinhAnhService.save(hinhAnh);
            return new ResponseEntity<>(savedHinhAnh, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật hình ảnh", description = "Cập nhật thông tin hình ảnh theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = HinhAnh.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy hình ảnh"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<HinhAnh> updateHinhAnh(
            @Parameter(description = "ID của hình ảnh", required = true) @PathVariable Integer id, 
            @RequestBody HinhAnh hinhAnh) {
        try {
            HinhAnh existingHinhAnh = hinhAnhService.findActiveById(id);
            if (existingHinhAnh != null) {
                hinhAnh.setMaHinh(id);
                hinhAnh.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                HinhAnh updatedHinhAnh = hinhAnhService.save(hinhAnh);
                return new ResponseEntity<>(updatedHinhAnh, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa hình ảnh", description = "Xóa mềm hình ảnh (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy hình ảnh"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteHinhAnh(
            @Parameter(description = "ID của hình ảnh", required = true) @PathVariable Integer id) {
        try {
            HinhAnh hinhAnh = hinhAnhService.findActiveById(id);
            if (hinhAnh != null) {
                hinhAnhService.softDeleteById(id);
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