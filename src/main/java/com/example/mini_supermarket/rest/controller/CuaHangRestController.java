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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuahang")
@CrossOrigin(origins = "*")
@Tag(name = "Cửa hàng", description = "API quản lý cửa hàng")
public class CuaHangRestController {

    @Autowired
    private CuaHangService cuaHangService;

    @Operation(summary = "Lấy mã cửa hàng mới", description = "Tạo và trả về mã cửa hàng mới theo format CH + 8 ký tự chữ và số ngẫu nhiên")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/generate-code")
    public ResponseEntity<Map<String, String>> generateMaCuaHang() {
        try {
            String newCode = cuaHangService.generateMaCuaHang();
            Map<String, String> response = new HashMap<>();
            response.put("maCH", newCode);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi tạo mã cửa hàng: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "409", description = "Mã cửa hàng đã tồn tại"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<?> createCuaHang(@Valid @RequestBody CuaHang cuaHang, BindingResult bindingResult) {
        try {
            // Kiểm tra validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error -> 
                    errors.put(error.getField(), error.getDefaultMessage()));
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }

            // Tự động generate mã cửa hàng
            String generatedMaCH = cuaHangService.generateMaCuaHang();
            cuaHang.setMaCH(generatedMaCH);

            // Thiết lập giá trị mặc định
            if (cuaHang.getNgayThanhLap() == null) {
                cuaHang.setNgayThanhLap(LocalDate.now());
            }
            if (cuaHang.getTrangThai() == null) {
                cuaHang.setTrangThai(1);
            }
            cuaHang.setIsDeleted(false);

            CuaHang savedCuaHang = cuaHangService.save(cuaHang);
            return new ResponseEntity<>(savedCuaHang, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi tạo cửa hàng: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật cửa hàng", description = "Cập nhật thông tin cửa hàng theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = CuaHang.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy cửa hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCuaHang(
            @Parameter(description = "ID của cửa hàng", required = true) @PathVariable String id, 
            @Valid @RequestBody CuaHang cuaHang, BindingResult bindingResult) {
        try {
            // Kiểm tra validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error -> 
                    errors.put(error.getField(), error.getDefaultMessage()));
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }

            CuaHang existingCuaHang = cuaHangService.findActiveById(id);
            if (existingCuaHang != null) {
                cuaHang.setMaCH(id);
                cuaHang.setIsDeleted(false);
                
                // Giữ nguyên ngày thành lập nếu không được cung cấp
                if (cuaHang.getNgayThanhLap() == null) {
                    cuaHang.setNgayThanhLap(existingCuaHang.getNgayThanhLap());
                }
                
                CuaHang updatedCuaHang = cuaHangService.save(cuaHang);
                return new ResponseEntity<>(updatedCuaHang, HttpStatus.OK);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Không tìm thấy cửa hàng với ID: " + id);
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi cập nhật cửa hàng: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa cửa hàng", description = "Xóa mềm cửa hàng (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy cửa hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCuaHang(
            @Parameter(description = "ID của cửa hàng", required = true) @PathVariable String id) {
        try {
            CuaHang cuaHang = cuaHangService.findActiveById(id);
            if (cuaHang != null) {
                cuaHangService.softDeleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Không tìm thấy cửa hàng với ID: " + id);
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi xóa cửa hàng: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}