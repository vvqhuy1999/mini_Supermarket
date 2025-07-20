package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.TonKhoChiTiet;
import com.example.mini_supermarket.service.TonKhoChiTietService;
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
@RequestMapping("/api/tonkhochitiet")
@CrossOrigin(origins = "*")
@Tag(name = "Tồn kho chi tiết", description = "API quản lý tồn kho chi tiết")
public class TonKhoChiTietRestController {

    @Autowired
    private TonKhoChiTietService tonKhoChiTietService;

    @Operation(summary = "Lấy tất cả tồn kho chi tiết", description = "Trả về danh sách tất cả tồn kho chi tiết chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = TonKhoChiTiet.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<TonKhoChiTiet>> getAllTonKhoChiTiet() {
        try {
            List<TonKhoChiTiet> tonKhoChiTiets = tonKhoChiTietService.findAllActive();
            return new ResponseEntity<>(tonKhoChiTiets, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy tồn kho chi tiết theo ID", description = "Trả về thông tin tồn kho chi tiết theo ID (chỉ lấy tồn kho chi tiết chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy tồn kho chi tiết", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = TonKhoChiTiet.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tồn kho chi tiết"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TonKhoChiTiet> getTonKhoChiTietById(
            @Parameter(description = "ID của tồn kho chi tiết", required = true) @PathVariable Integer id) {
        try {
            TonKhoChiTiet tonKhoChiTiet = tonKhoChiTietService.findActiveById(id);
            if (tonKhoChiTiet != null) {
                return new ResponseEntity<>(tonKhoChiTiet, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm tồn kho chi tiết mới", description = "Tạo bản ghi tồn kho chi tiết mới cho sản phẩm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo tồn kho chi tiết thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = TonKhoChiTiet.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<TonKhoChiTiet> createTonKhoChiTiet(@RequestBody TonKhoChiTiet tonKhoChiTiet) {
        try {
            tonKhoChiTiet.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            TonKhoChiTiet savedTonKhoChiTiet = tonKhoChiTietService.save(tonKhoChiTiet);
            return new ResponseEntity<>(savedTonKhoChiTiet, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật tồn kho chi tiết", description = "Cập nhật thông tin tồn kho chi tiết theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = TonKhoChiTiet.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tồn kho chi tiết"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TonKhoChiTiet> updateTonKhoChiTiet(
            @Parameter(description = "ID của tồn kho chi tiết", required = true) @PathVariable Integer id, 
            @RequestBody TonKhoChiTiet tonKhoChiTiet) {
        try {
            TonKhoChiTiet existingTonKhoChiTiet = tonKhoChiTietService.findActiveById(id);
            if (existingTonKhoChiTiet != null) {
                tonKhoChiTiet.setMaTKCT(id);
                tonKhoChiTiet.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                TonKhoChiTiet updatedTonKhoChiTiet = tonKhoChiTietService.save(tonKhoChiTiet);
                return new ResponseEntity<>(updatedTonKhoChiTiet, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa tồn kho chi tiết", description = "Xóa mềm tồn kho chi tiết (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tồn kho chi tiết"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTonKhoChiTiet(
            @Parameter(description = "ID của tồn kho chi tiết", required = true) @PathVariable Integer id) {
        try {
            TonKhoChiTiet tonKhoChiTiet = tonKhoChiTietService.findActiveById(id);
            if (tonKhoChiTiet != null) {
                tonKhoChiTietService.softDeleteById(id);
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