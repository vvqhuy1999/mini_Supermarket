package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.KhachHang;
import com.example.mini_supermarket.service.KhachHangService;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/khachhang")
@CrossOrigin(origins = "*")
@Tag(name = "Khách hàng", description = "API quản lý khách hàng")
public class KhachHangRestController {

    @Autowired
    private KhachHangService khachHangService;

    @Operation(summary = "Lấy tất cả khách hàng", description = "Trả về danh sách tất cả khách hàng chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhachHang.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<KhachHang>> getAllKhachHang() {
        try {
            List<KhachHang> khachHangs = khachHangService.findAllActive();
            return new ResponseEntity<>(khachHangs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy khách hàng theo ID", description = "Trả về thông tin khách hàng theo ID (chỉ lấy khách hàng chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy khách hàng", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhachHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<KhachHang> getKhachHangById(
            @Parameter(description = "ID của khách hàng", required = true) @PathVariable String id) {
        try {
            KhachHang khachHang = khachHangService.findActiveById(id);
            if (khachHang != null) {
                return new ResponseEntity<>(khachHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm khách hàng mới", description = "Tạo một khách hàng mới trong hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo khách hàng thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhachHang.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "409", description = "Mã khách hàng đã tồn tại"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<?> createKhachHang(@Valid @RequestBody KhachHang khachHang, BindingResult bindingResult) {
        try {
            // Kiểm tra validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error -> 
                    errors.put(error.getField(), error.getDefaultMessage()));
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }

            // Kiểm tra mã khách hàng đã tồn tại chưa (bao gồm cả đã xóa)
            try {
                KhachHang existing = khachHangService.findById(khachHang.getMaKH());
                if (existing != null) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Mã khách hàng đã tồn tại");
                    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
                }
            } catch (RuntimeException e) {
                // Mã khách hàng chưa tồn tại - OK
            }

            // Thiết lập giá trị mặc định
            if (khachHang.getNgayDangKy() == null) {
                khachHang.setNgayDangKy(LocalDateTime.now());
            }
            if (khachHang.getDiemTichLuy() == null) {
                khachHang.setDiemTichLuy(0);
            }
            if (khachHang.getLoaiKhachHang() == null || khachHang.getLoaiKhachHang().trim().isEmpty()) {
                khachHang.setLoaiKhachHang("Thường");
            }
            khachHang.setIsDeleted(false);

            KhachHang savedKhachHang = khachHangService.save(khachHang);
            return new ResponseEntity<>(savedKhachHang, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi tạo khách hàng: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật khách hàng", description = "Cập nhật thông tin khách hàng theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhachHang.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateKhachHang(
            @Parameter(description = "ID của khách hàng", required = true) @PathVariable String id, 
            @Valid @RequestBody KhachHang khachHang, BindingResult bindingResult) {
        try {
            // Kiểm tra validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error -> 
                    errors.put(error.getField(), error.getDefaultMessage()));
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }

            KhachHang existingKhachHang = khachHangService.findActiveById(id);
            if (existingKhachHang != null) {
                khachHang.setMaKH(id);
                khachHang.setIsDeleted(false);
                
                // Giữ nguyên ngày đăng ký nếu không được cung cấp
                if (khachHang.getNgayDangKy() == null) {
                    khachHang.setNgayDangKy(existingKhachHang.getNgayDangKy());
                }
                
                // Giữ nguyên điểm tích lũy nếu không được cung cấp
                if (khachHang.getDiemTichLuy() == null) {
                    khachHang.setDiemTichLuy(existingKhachHang.getDiemTichLuy());
                }

                KhachHang updatedKhachHang = khachHangService.update(khachHang);
                return new ResponseEntity<>(updatedKhachHang, HttpStatus.OK);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Không tìm thấy khách hàng với ID: " + id);
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi cập nhật khách hàng: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa khách hàng", description = "Xóa mềm khách hàng (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteKhachHang(
            @Parameter(description = "ID của khách hàng", required = true) @PathVariable String id) {
        try {
            KhachHang existingKhachHang = khachHangService.findActiveById(id);
            if (existingKhachHang != null) {
                khachHangService.softDeleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Không tìm thấy khách hàng với ID: " + id);
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi xóa khách hàng: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}