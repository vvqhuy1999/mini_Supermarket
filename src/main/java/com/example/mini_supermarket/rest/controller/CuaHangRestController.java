package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.CuaHang;
import com.example.mini_supermarket.service.CuaHangService;
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
@RequestMapping("/api/cuahang")
@CrossOrigin(origins = "*")
@Tag(name = "Cửa hàng", description = "API quản lý cửa hàng")
public class CuaHangRestController {

    @Autowired
    private CuaHangService cuaHangService;

    @Operation(summary = "Lấy tất cả cửa hàng", description = "Trả về danh sách tất cả cửa hàng chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = CuaHang.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<CuaHang>> getAllCuaHang() {
        try {
            List<CuaHang> cuaHangs = cuaHangService.findAllActive();
            return new ResponseEntity<>(cuaHangs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy cửa hàng theo ID", description = "Trả về thông tin cửa hàng theo ID (chỉ lấy cửa hàng chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy cửa hàng", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = CuaHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy cửa hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CuaHang> getCuaHangById(
            @Parameter(description = "ID của cửa hàng", required = true) @PathVariable String id) {
        try {
            CuaHang cuaHang = cuaHangService.findActiveById(id);
            if (cuaHang != null) {
                return new ResponseEntity<>(cuaHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm cửa hàng mới", description = "Tạo một cửa hàng mới trong hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo cửa hàng thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = CuaHang.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<CuaHang> createCuaHang(@RequestBody CuaHang cuaHang) {
        try {
            cuaHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            CuaHang savedCuaHang = cuaHangService.save(cuaHang);
            return new ResponseEntity<>(savedCuaHang, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật cửa hàng", description = "Cập nhật thông tin cửa hàng theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = CuaHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy cửa hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CuaHang> updateCuaHang(
            @Parameter(description = "ID của cửa hàng", required = true) @PathVariable String id, 
            @RequestBody CuaHang cuaHang) {
        try {
            CuaHang existingCuaHang = cuaHangService.findActiveById(id);
            if (existingCuaHang != null) {
                cuaHang.setMaCH(id);
                cuaHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                CuaHang updatedCuaHang = cuaHangService.save(cuaHang);
                return new ResponseEntity<>(updatedCuaHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa cửa hàng", description = "Xóa mềm cửa hàng (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy cửa hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCuaHang(
            @Parameter(description = "ID của cửa hàng", required = true) @PathVariable String id) {
        try {
            CuaHang cuaHang = cuaHangService.findActiveById(id);
            if (cuaHang != null) {
                cuaHangService.softDeleteById(id);
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