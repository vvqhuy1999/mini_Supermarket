package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.dto.ApiResponse;
import com.example.mini_supermarket.dto.CreateInvoiceFromCartRequest;
import com.example.mini_supermarket.dto.InvoiceCreatedResponse;
import com.example.mini_supermarket.entity.HoaDon;
import com.example.mini_supermarket.service.HoaDonService;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hoadon")
@CrossOrigin(origins = "*")
@Tag(name = "Hóa Đơn", description = "Quản lý hóa đơn")
public class HoaDonRestController {

    @Autowired
    private HoaDonService hoaDonService;

    // Lấy tất cả hóa đơn
    @GetMapping
    @Operation(summary = "Lấy tất cả hóa đơn", description = "Lấy danh sách tất cả hóa đơn trong hệ thống")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<List<HoaDon>>> getAllHoaDon() {
        try {
            List<HoaDon> danhSachHoaDon = hoaDonService.findAllActive();
            return ResponseEntity.ok(ApiResponse.<List<HoaDon>>builder()
                    .success(true)
                    .message("Lấy danh sách hóa đơn thành công")
                    .result(danhSachHoaDon)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<HoaDon>>builder()
                            .success(false)
                            .error("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage())
                            .build());
        }
    }

    // Lấy hóa đơn theo mã
    @GetMapping("/{maHD}")
    @Operation(summary = "Lấy hóa đơn theo mã", description = "Lấy thông tin hóa đơn dựa trên mã hóa đơn")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy hóa đơn"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<HoaDon>> getHoaDonByMaHD(
            @Parameter(description = "Mã hóa đơn", required = true) @PathVariable Integer maHD) {
        try {
            HoaDon hoaDon = hoaDonService.findActiveById(maHD);
            if (hoaDon != null) {
                return ResponseEntity.ok(ApiResponse.<HoaDon>builder()
                        .success(true)
                        .message("Lấy hóa đơn thành công")
                        .result(hoaDon)
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<HoaDon>builder()
                                .success(false)
                                .error("Không tìm thấy hóa đơn với mã: " + maHD)
                                .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<HoaDon>builder()
                            .success(false)
                            .error("Lỗi khi lấy hóa đơn: " + e.getMessage())
                            .build());
        }
    }

    // Tạo hóa đơn mới
    @PostMapping
    @Operation(summary = "Tạo hóa đơn mới", description = "Tạo một hóa đơn mới trong hệ thống")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<HoaDon>> createHoaDon(
            @Parameter(description = "Thông tin hóa đơn", required = true) @RequestBody HoaDon hoaDon) {
        try {
            HoaDon hoaDonMoi = hoaDonService.save(hoaDon);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<HoaDon>builder()
                            .success(true)
                            .message("Tạo hóa đơn thành công")
                            .result(hoaDonMoi)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<HoaDon>builder()
                            .success(false)
                            .error("Lỗi khi tạo hóa đơn: " + e.getMessage())
                            .build());
        }
    }

    // Cập nhật hóa đơn
    @PutMapping("/{maHD}")
    @Operation(summary = "Cập nhật hóa đơn", description = "Cập nhật thông tin hóa đơn")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<HoaDon>> updateHoaDon(
            @Parameter(description = "Mã hóa đơn", required = true) @PathVariable Integer maHD, 
            @Parameter(description = "Thông tin hóa đơn cập nhật", required = true) @RequestBody HoaDon hoaDon) {
        try {
            if (!maHD.equals(hoaDon.getMaHD())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.<HoaDon>builder()
                                .success(false)
                                .error("Mã hóa đơn không khớp")
                                .build());
            }
            HoaDon hoaDonCapNhat = hoaDonService.update(hoaDon);
            return ResponseEntity.ok(ApiResponse.<HoaDon>builder()
                    .success(true)
                    .message("Cập nhật hóa đơn thành công")
                    .result(hoaDonCapNhat)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<HoaDon>builder()
                            .success(false)
                            .error("Lỗi khi cập nhật hóa đơn: " + e.getMessage())
                            .build());
        }
    }

    // Xóa hóa đơn
    @DeleteMapping("/{maHD}")
    @Operation(summary = "Xóa hóa đơn", description = "Xóa hóa đơn theo mã")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xóa thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<Void>> deleteHoaDon(
            @Parameter(description = "Mã hóa đơn", required = true) @PathVariable Integer maHD) {
        try {
            hoaDonService.softDeleteById(maHD);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Xóa hóa đơn thành công")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .error("Lỗi khi xóa hóa đơn: " + e.getMessage())
                            .build());
        }
    }

    // === ENDPOINT MỚI - TẠO HÓA ĐƠN TỪ GIỎ HÀNG ===
    
    // Tạo hóa đơn từ giỏ hàng
    @PostMapping("/from-cart")
    @Operation(summary = "Tạo hóa đơn từ giỏ hàng", description = "Tạo hóa đơn từ các item được chọn trong giỏ hàng và xóa giỏ hàng")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo hóa đơn thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<InvoiceCreatedResponse>> createInvoiceFromCart(
            @Parameter(description = "Thông tin tạo hóa đơn từ giỏ hàng", required = true) 
            @RequestBody CreateInvoiceFromCartRequest request) {
        try {
            InvoiceCreatedResponse invoiceResponse = hoaDonService.createInvoiceFromCart(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<InvoiceCreatedResponse>builder()
                            .success(true)
                            .message("Tạo hóa đơn từ giỏ hàng thành công")
                            .result(invoiceResponse)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<InvoiceCreatedResponse>builder()
                            .success(false)
                            .error("Lỗi khi tạo hóa đơn từ giỏ hàng: " + e.getMessage())
                            .build());
        }
    }
    
    // Lấy danh sách hóa đơn theo khách hàng
    @GetMapping("/by-khachhang/{maKH}")
    @Operation(summary = "Lấy hóa đơn theo khách hàng", description = "Trả về danh sách hóa đơn (active) của một khách hàng")
    public ResponseEntity<ApiResponse<List<HoaDon>>> getHoaDonByCustomer(
            @Parameter(description = "Mã khách hàng", required = true)
            @PathVariable String maKH) {
        try {
            List<HoaDon> danhSachHoaDon = hoaDonService.findActiveByCustomer(maKH);
            return ResponseEntity.ok(ApiResponse.<List<HoaDon>>builder()
                    .success(true)
                    .message("Lấy danh sách hóa đơn theo khách hàng thành công")
                    .result(danhSachHoaDon)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<List<HoaDon>>builder()
                            .success(false)
                            .error("Lỗi khi lấy hóa đơn theo khách hàng: " + e.getMessage())
                            .build());
        }
    }
    
    // Cập nhật trạng thái hóa đơn
    @PatchMapping("/{maHD}/trangthai")
    @Operation(summary = "Cập nhật trạng thái hóa đơn", description = "Cập nhật trạng thái của hóa đơn")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<HoaDon>> updateTrangThaiHoaDon(
            @Parameter(description = "Mã hóa đơn", required = true) @PathVariable Integer maHD, 
            @Parameter(description = "Trạng thái mới", required = true) @RequestParam Integer trangThaiMoi) {
        try {
            HoaDon hoaDon = hoaDonService.updateTrangThai(maHD, trangThaiMoi);
            return ResponseEntity.ok(ApiResponse.<HoaDon>builder()
                    .success(true)
                    .message("Cập nhật trạng thái hóa đơn thành công")
                    .result(hoaDon)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<HoaDon>builder()
                            .success(false)
                            .error("Lỗi khi cập nhật trạng thái: " + e.getMessage())
                            .build());
        }
    }
} 