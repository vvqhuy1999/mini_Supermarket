package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.dto.ApiResponse;
import com.example.mini_supermarket.entity.ChiTietDonHang;
import com.example.mini_supermarket.service.ChiTietDonHangService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chitietdonhang")
@CrossOrigin(origins = "*")
@Tag(name = "Chi Tiết Đơn Hàng", description = "Quản lý chi tiết đơn hàng")
public class ChiTietDonHangRestController {

    @Autowired
    private ChiTietDonHangService chiTietDonHangService;

    // Lấy tất cả chi tiết đơn hàng
    @GetMapping
    @Operation(summary = "Lấy tất cả chi tiết đơn hàng", description = "Lấy danh sách tất cả chi tiết đơn hàng trong hệ thống")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<List<ChiTietDonHang>>> getAllChiTietDonHang() {
        try {
            List<ChiTietDonHang> danhSachChiTiet = chiTietDonHangService.getAllChiTietDonHang();
            return ResponseEntity.ok(ApiResponse.<List<ChiTietDonHang>>builder()
                    .success(true)
                    .message("Lấy danh sách chi tiết đơn hàng thành công")
                    .result(danhSachChiTiet)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<ChiTietDonHang>>builder()
                            .success(false)
                            .error("Lỗi khi lấy danh sách chi tiết đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Lấy chi tiết đơn hàng theo mã
    @GetMapping("/{maCTHD}")
    @Operation(summary = "Lấy chi tiết đơn hàng theo mã", description = "Lấy thông tin chi tiết đơn hàng dựa trên mã")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết đơn hàng"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<ChiTietDonHang>> getChiTietDonHangByMaCTHD(
            @Parameter(description = "Mã chi tiết đơn hàng", required = true) @PathVariable Integer maCTHD) {
        try {
            Optional<ChiTietDonHang> chiTiet = chiTietDonHangService.findChiTietDonHangByMaCTHD(maCTHD);
            if (chiTiet.isPresent()) {
                return ResponseEntity.ok(ApiResponse.<ChiTietDonHang>builder()
                        .success(true)
                        .message("Lấy chi tiết đơn hàng thành công")
                        .result(chiTiet.get())
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<ChiTietDonHang>builder()
                                .success(false)
                                .error("Không tìm thấy chi tiết đơn hàng với mã: " + maCTHD)
                                .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChiTietDonHang>builder()
                            .success(false)
                            .error("Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Tạo chi tiết đơn hàng mới
    @PostMapping
    @Operation(summary = "Tạo chi tiết đơn hàng mới", description = "Tạo một chi tiết đơn hàng mới trong hệ thống")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<ChiTietDonHang>> createChiTietDonHang(
            @Parameter(description = "Thông tin chi tiết đơn hàng", required = true) @RequestBody ChiTietDonHang chiTietDonHang) {
        try {
            ChiTietDonHang chiTietMoi = chiTietDonHangService.saveChiTietDonHang(chiTietDonHang);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<ChiTietDonHang>builder()
                            .success(true)
                            .message("Tạo chi tiết đơn hàng thành công")
                            .result(chiTietMoi)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChiTietDonHang>builder()
                            .success(false)
                            .error("Lỗi khi tạo chi tiết đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Cập nhật chi tiết đơn hàng
    @PutMapping("/{maCTHD}")
    @Operation(summary = "Cập nhật chi tiết đơn hàng", description = "Cập nhật thông tin chi tiết đơn hàng")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<ChiTietDonHang>> updateChiTietDonHang(
            @Parameter(description = "Mã chi tiết đơn hàng", required = true) @PathVariable Integer maCTHD, 
            @Parameter(description = "Thông tin chi tiết đơn hàng cập nhật", required = true) @RequestBody ChiTietDonHang chiTietDonHang) {
        try {
            if (!maCTHD.equals(chiTietDonHang.getMaCTHD())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.<ChiTietDonHang>builder()
                                .success(false)
                                .error("Mã chi tiết đơn hàng không khớp")
                                .build());
            }
            ChiTietDonHang chiTietCapNhat = chiTietDonHangService.saveChiTietDonHang(chiTietDonHang);
            return ResponseEntity.ok(ApiResponse.<ChiTietDonHang>builder()
                    .success(true)
                    .message("Cập nhật chi tiết đơn hàng thành công")
                    .result(chiTietCapNhat)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChiTietDonHang>builder()
                            .success(false)
                            .error("Lỗi khi cập nhật chi tiết đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Xóa chi tiết đơn hàng
    @DeleteMapping("/{maCTHD}")
    @Operation(summary = "Xóa chi tiết đơn hàng", description = "Xóa chi tiết đơn hàng theo mã")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xóa thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<Void>> deleteChiTietDonHang(
            @Parameter(description = "Mã chi tiết đơn hàng", required = true) @PathVariable Integer maCTHD) {
        try {
            chiTietDonHangService.deleteChiTietDonHang(maCTHD);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Xóa chi tiết đơn hàng thành công")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .error("Lỗi khi xóa chi tiết đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Tìm chi tiết đơn hàng theo mã đơn hàng
    @GetMapping("/donhang/{maDH}")
    @Operation(summary = "Tìm chi tiết đơn hàng theo mã đơn hàng", description = "Lấy danh sách chi tiết đơn hàng của một đơn hàng")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<List<ChiTietDonHang>>> getChiTietDonHangByDonHang(
            @Parameter(description = "Mã đơn hàng", required = true) @PathVariable String maDH) {
        try {
            List<ChiTietDonHang> danhSachChiTiet = chiTietDonHangService.findChiTietDonHangByDonHang(maDH);
            return ResponseEntity.ok(ApiResponse.<List<ChiTietDonHang>>builder()
                    .success(true)
                    .message("Lấy danh sách chi tiết đơn hàng thành công")
                    .result(danhSachChiTiet)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<ChiTietDonHang>>builder()
                            .success(false)
                            .error("Lỗi khi lấy danh sách chi tiết đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Tìm chi tiết đơn hàng theo sản phẩm
    @GetMapping("/sanpham/{maSP}")
    @Operation(summary = "Tìm chi tiết đơn hàng theo sản phẩm", description = "Lấy danh sách chi tiết đơn hàng của một sản phẩm")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<List<ChiTietDonHang>>> getChiTietDonHangBySanPham(
            @Parameter(description = "Mã sản phẩm", required = true) @PathVariable String maSP) {
        try {
            List<ChiTietDonHang> danhSachChiTiet = chiTietDonHangService.findChiTietDonHangBySanPham(maSP);
            return ResponseEntity.ok(ApiResponse.<List<ChiTietDonHang>>builder()
                    .success(true)
                    .message("Lấy danh sách chi tiết đơn hàng theo sản phẩm thành công")
                    .result(danhSachChiTiet)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<ChiTietDonHang>>builder()
                            .success(false)
                            .error("Lỗi khi lấy danh sách chi tiết đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Cập nhật số lượng sản phẩm
    @PatchMapping("/{maCTHD}/soluong")
    @Operation(summary = "Cập nhật số lượng sản phẩm", description = "Cập nhật số lượng sản phẩm trong chi tiết đơn hàng")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<ChiTietDonHang>> updateSoLuong(
            @Parameter(description = "Mã chi tiết đơn hàng", required = true) @PathVariable Integer maCTHD, 
            @Parameter(description = "Số lượng mới", required = true) @RequestParam Integer soLuongMoi) {
        try {
            ChiTietDonHang chiTiet = chiTietDonHangService.updateSoLuong(maCTHD, soLuongMoi);
            return ResponseEntity.ok(ApiResponse.<ChiTietDonHang>builder()
                    .success(true)
                    .message("Cập nhật số lượng thành công")
                    .result(chiTiet)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChiTietDonHang>builder()
                            .success(false)
                            .error("Lỗi khi cập nhật số lượng: " + e.getMessage())
                            .build());
        }
    }

    // Cập nhật giảm giá
    @PatchMapping("/{maCTHD}/giamgia")
    @Operation(summary = "Cập nhật giảm giá", description = "Cập nhật giảm giá cho chi tiết đơn hàng")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<ChiTietDonHang>> updateGiamGia(
            @Parameter(description = "Mã chi tiết đơn hàng", required = true) @PathVariable Integer maCTHD, 
            @Parameter(description = "Giảm giá mới", required = true) @RequestParam BigDecimal giamGiaMoi) {
        try {
            ChiTietDonHang chiTiet = chiTietDonHangService.updateGiamGia(maCTHD, giamGiaMoi);
            return ResponseEntity.ok(ApiResponse.<ChiTietDonHang>builder()
                    .success(true)
                    .message("Cập nhật giảm giá thành công")
                    .result(chiTiet)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ChiTietDonHang>builder()
                            .success(false)
                            .error("Lỗi khi cập nhật giảm giá: " + e.getMessage())
                            .build());
        }
    }

    // Đếm số lượng sản phẩm trong đơn hàng
    @GetMapping("/count/donhang/{maDH}")
    @Operation(summary = "Đếm số lượng sản phẩm trong đơn hàng", description = "Đếm tổng số lượng sản phẩm trong một đơn hàng")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<Long>> countChiTietDonHangByDonHang(
            @Parameter(description = "Mã đơn hàng", required = true) @PathVariable String maDH) {
        try {
            long soLuong = chiTietDonHangService.countChiTietDonHangByDonHang(maDH);
            return ResponseEntity.ok(ApiResponse.<Long>builder()
                    .success(true)
                    .message("Đếm số lượng sản phẩm thành công")
                    .result(soLuong)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Long>builder()
                            .success(false)
                            .error("Lỗi khi đếm số lượng sản phẩm: " + e.getMessage())
                            .build());
        }
    }

    // Tìm chi tiết đơn hàng có giảm giá
    @GetMapping("/cogiamgia")
    @Operation(summary = "Tìm chi tiết đơn hàng có giảm giá", description = "Lấy danh sách chi tiết đơn hàng có áp dụng giảm giá")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<List<ChiTietDonHang>>> getChiTietDonHangCoGiamGia() {
        try {
            List<ChiTietDonHang> danhSachChiTiet = chiTietDonHangService.findChiTietDonHangCoGiamGia();
            return ResponseEntity.ok(ApiResponse.<List<ChiTietDonHang>>builder()
                    .success(true)
                    .message("Lấy danh sách chi tiết đơn hàng có giảm giá thành công")
                    .result(danhSachChiTiet)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<ChiTietDonHang>>builder()
                            .success(false)
                            .error("Lỗi khi lấy danh sách chi tiết đơn hàng: " + e.getMessage())
                            .build());
        }
    }
}
