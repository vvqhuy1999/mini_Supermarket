package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.dto.ApiResponse;
import com.example.mini_supermarket.dto.CreateInvoiceFromCartRequest;
import com.example.mini_supermarket.dto.InvoiceCreatedResponse;
import com.example.mini_supermarket.dto.HoaDonFullDetailsDTO;
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
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE}, allowedHeaders = "*")
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
    
    // ===== OPTIMIZED APIs - Temporarily disabled =====
    
    /*
    // Lấy danh sách hóa đơn tối ưu với pagination
    @GetMapping("/optimized")
    @Operation(summary = "Lấy danh sách hóa đơn tối ưu", description = "Lấy danh sách hóa đơn với pagination và chỉ trả về thông tin cần thiết")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<Page<HoaDonSummaryDTO>>> getAllHoaDonOptimized(
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng item mỗi trang", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sắp xếp theo trường", example = "ngayLap") @RequestParam(defaultValue = "ngayLap") String sortBy,
            @Parameter(description = "Hướng sắp xếp", example = "desc") @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<HoaDonSummaryDTO> danhSachHoaDon = hoaDonService.findAllActiveSummary(pageable);
            
            return ResponseEntity.ok(ApiResponse.<Page<HoaDonSummaryDTO>>builder()
                    .success(true)
                    .message("Lấy danh sách hóa đơn tối ưu thành công")
                    .result(danhSachHoaDon)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Page<HoaDonSummaryDTO>>builder()
                            .success(false)
                            .error("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage())
                            .build());
        }
    }
    
    // Lấy hóa đơn theo khách hàng tối ưu với pagination
    @GetMapping("/by-khachhang/{maKH}/optimized")
    @Operation(summary = "Lấy hóa đơn theo khách hàng tối ưu", description = "Lấy hóa đơn theo khách hàng với pagination và chỉ trả về thông tin cần thiết")
    public ResponseEntity<ApiResponse<Page<HoaDonSummaryDTO>>> getHoaDonByCustomerOptimized(
            @Parameter(description = "Mã khách hàng", required = true) @PathVariable String maKH,
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng item mỗi trang", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sắp xếp theo trường", example = "ngayLap") @RequestParam(defaultValue = "ngayLap") String sortBy,
            @Parameter(description = "Hướng sắp xếp", example = "desc") @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<HoaDonSummaryDTO> danhSachHoaDon = hoaDonService.findActiveByCustomerSummary(maKH, pageable);
            
            return ResponseEntity.ok(ApiResponse.<Page<HoaDonSummaryDTO>>builder()
                    .success(true)
                    .message("Lấy danh sách hóa đơn theo khách hàng tối ưu thành công")
                    .result(danhSachHoaDon)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Page<HoaDonSummaryDTO>>builder()
                            .success(false)
                            .error("Lỗi khi lấy hóa đơn theo khách hàng: " + e.getMessage())
                            .build());
        }
    }
    
    // Lấy hóa đơn theo khách hàng tối ưu không pagination (fast API)
    @GetMapping("/by-khachhang/{maKH}/summary")
    @Operation(summary = "Lấy hóa đơn theo khách hàng nhanh", description = "Lấy hóa đơn theo khách hàng chỉ thông tin cần thiết, không pagination")
    public ResponseEntity<ApiResponse<List<HoaDonSummaryDTO>>> getHoaDonByCustomerSummary(
            @Parameter(description = "Mã khách hàng", required = true) @PathVariable String maKH) {
        try {
            List<HoaDonSummaryDTO> danhSachHoaDon = hoaDonService.findActiveByCustomerSummaryList(maKH);
            return ResponseEntity.ok(ApiResponse.<List<HoaDonSummaryDTO>>builder()
                    .success(true)
                    .message("Lấy danh sách hóa đơn theo khách hàng nhanh thành công")
                    .result(danhSachHoaDon)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<List<HoaDonSummaryDTO>>builder()
                            .success(false)
                            .error("Lỗi khi lấy hóa đơn theo khách hàng: " + e.getMessage())
                            .build());
        }
    }
    */
    
    // API đếm số lượng hóa đơn theo trạng thái
    @GetMapping("/count/trangthai/{trangThai}")
    @Operation(summary = "Đếm hóa đơn theo trạng thái", description = "Đếm số lượng hóa đơn theo trạng thái cụ thể")
    public ResponseEntity<ApiResponse<Long>> countByTrangThai(
            @Parameter(description = "Trạng thái hóa đơn", required = true) @PathVariable Integer trangThai) {
        try {
            Long count = hoaDonService.countByTrangThai(trangThai);
            return ResponseEntity.ok(ApiResponse.<Long>builder()
                    .success(true)
                    .message("Đếm hóa đơn theo trạng thái thành công")
                    .result(count)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Long>builder()
                            .success(false)
                            .error("Lỗi khi đếm hóa đơn: " + e.getMessage())
                            .build());
        }
    }
    
    // API đếm số lượng hóa đơn của khách hàng
    @GetMapping("/count/khachhang/{maKH}")
    @Operation(summary = "Đếm hóa đơn theo khách hàng", description = "Đếm số lượng hóa đơn của khách hàng cụ thể")
    public ResponseEntity<ApiResponse<Long>> countByCustomer(
            @Parameter(description = "Mã khách hàng", required = true) @PathVariable String maKH) {
        try {
            Long count = hoaDonService.countByCustomer(maKH);
            return ResponseEntity.ok(ApiResponse.<Long>builder()
                    .success(true)
                    .message("Đếm hóa đơn theo khách hàng thành công")
                    .result(count)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Long>builder()
                            .success(false)
                            .error("Lỗi khi đếm hóa đơn: " + e.getMessage())
                            .build());
        }
    }
    
    // ===== ENHANCED APIs - Tìm kiếm và lọc =====
    
    // API lấy hóa đơn theo trạng thái
    @GetMapping("/status/{trangThai}")
    @Operation(summary = "Lấy hóa đơn theo trạng thái", description = "Lấy danh sách hóa đơn theo trạng thái cụ thể")
    public ResponseEntity<ApiResponse<List<HoaDon>>> getHoaDonByStatus(
            @Parameter(description = "Trạng thái hóa đơn", required = true) @PathVariable Integer trangThai) {
        try {
            List<HoaDon> danhSachHoaDon = hoaDonService.findByTrangThai(trangThai);
            return ResponseEntity.ok(ApiResponse.<List<HoaDon>>builder()
                    .success(true)
                    .message("Lấy hóa đơn theo trạng thái thành công")
                    .result(danhSachHoaDon)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<HoaDon>>builder()
                            .success(false)
                            .error("Lỗi khi lấy hóa đơn theo trạng thái: " + e.getMessage())
                            .build());
        }
    }
    
    // API lấy hóa đơn theo khách hàng và trạng thái
    @GetMapping("/by-khachhang/{maKH}/status/{trangThai}")
    @Operation(summary = "Lấy hóa đơn theo khách hàng và trạng thái", description = "Lấy hóa đơn của khách hàng theo trạng thái cụ thể")
    public ResponseEntity<ApiResponse<List<HoaDon>>> getHoaDonByCustomerAndStatus(
            @Parameter(description = "Mã khách hàng", required = true) @PathVariable String maKH,
            @Parameter(description = "Trạng thái hóa đơn", required = true) @PathVariable Integer trangThai) {
        try {
            List<HoaDon> danhSachHoaDon = hoaDonService.findByCustomerAndStatus(maKH, trangThai);
            return ResponseEntity.ok(ApiResponse.<List<HoaDon>>builder()
                    .success(true)
                    .message("Lấy hóa đơn theo khách hàng và trạng thái thành công")
                    .result(danhSachHoaDon)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<List<HoaDon>>builder()
                            .success(false)
                            .error("Lỗi khi lấy hóa đơn: " + e.getMessage())
                            .build());
        }
    }
    
    // API lấy hóa đơn theo khoảng ngày
    @GetMapping("/date-range")
    @Operation(summary = "Lấy hóa đơn theo khoảng ngày", description = "Lấy hóa đơn trong khoảng thời gian từ ngày đến ngày")
    public ResponseEntity<ApiResponse<List<HoaDon>>> getHoaDonByDateRange(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)", required = true) @RequestParam String fromDate,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)", required = true) @RequestParam String toDate) {
        try {
            List<HoaDon> danhSachHoaDon = hoaDonService.findByDateRange(fromDate, toDate);
            return ResponseEntity.ok(ApiResponse.<List<HoaDon>>builder()
                    .success(true)
                    .message("Lấy hóa đơn theo khoảng ngày thành công")
                    .result(danhSachHoaDon)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<List<HoaDon>>builder()
                            .success(false)
                            .error("Lỗi khi lấy hóa đơn theo ngày: " + e.getMessage())
                            .build());
        }
    }
    
    // API lấy hóa đơn theo khách hàng và khoảng ngày
    @GetMapping("/by-khachhang/{maKH}/date-range")
    @Operation(summary = "Lấy hóa đơn theo khách hàng và khoảng ngày", description = "Lấy hóa đơn của khách hàng trong khoảng thời gian")
    public ResponseEntity<ApiResponse<List<HoaDon>>> getHoaDonByCustomerAndDateRange(
            @Parameter(description = "Mã khách hàng", required = true) @PathVariable String maKH,
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)", required = true) @RequestParam String fromDate,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)", required = true) @RequestParam String toDate) {
        try {
            List<HoaDon> danhSachHoaDon = hoaDonService.findByCustomerAndDateRange(maKH, fromDate, toDate);
            return ResponseEntity.ok(ApiResponse.<List<HoaDon>>builder()
                    .success(true)
                    .message("Lấy hóa đơn theo khách hàng và khoảng ngày thành công")
                    .result(danhSachHoaDon)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<List<HoaDon>>builder()
                            .success(false)
                            .error("Lỗi khi lấy hóa đơn: " + e.getMessage())
                            .build());
        }
    }
    
    // API hủy hóa đơn - PATCH method
    @PatchMapping("/{maHD}/cancel")
    @Operation(summary = "Hủy hóa đơn", description = "Hủy hóa đơn (chuyển trạng thái thành 3)")
    @CrossOrigin(origins = "*", methods = {RequestMethod.PATCH}, allowedHeaders = "*")
    public ResponseEntity<ApiResponse<HoaDon>> cancelHoaDon(
            @Parameter(description = "Mã hóa đơn", required = true) @PathVariable Integer maHD,
            @Parameter(description = "Lý do hủy", required = false) @RequestParam(required = false) String lyDoHuy) {
        try {
            HoaDon hoaDon = hoaDonService.cancelHoaDon(maHD, lyDoHuy);
            return ResponseEntity.ok(ApiResponse.<HoaDon>builder()
                    .success(true)
                    .message("Hủy hóa đơn thành công")
                    .result(hoaDon)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<HoaDon>builder()
                            .success(false)
                            .error("Lỗi khi hủy hóa đơn: " + e.getMessage())
                            .build());
        }
    }
    
    // ===== FALLBACK API - Cập nhật trạng thái (POST method cho CORS compatibility) =====
    
    @PostMapping("/{maHD}/status")
    @Operation(summary = "Cập nhật trạng thái hóa đơn", description = "Cập nhật trạng thái hóa đơn (0=Chờ thanh toán, 1=Đã thanh toán, 2=Đang xử lý, 3=Đã hủy, 4=Hoàn trả)")
    @CrossOrigin(origins = "*", methods = {RequestMethod.POST}, allowedHeaders = "*")
    public ResponseEntity<ApiResponse<HoaDon>> updateHoaDonStatus(
            @Parameter(description = "Mã hóa đơn", required = true) @PathVariable Integer maHD,
            @Parameter(description = "Trạng thái mới", required = true) @RequestParam Integer trangThai,
            @Parameter(description = "Lý do thay đổi", required = false) @RequestParam(required = false) String lyDo) {
        try {
            // Kiểm tra trạng thái hợp lệ
            if (trangThai < 0 || trangThai > 4) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.<HoaDon>builder()
                                .success(false)
                                .error("Trạng thái không hợp lệ. Phải từ 0-4")
                                .build());
            }
            
            HoaDon hoaDon;
            
            // Nếu là hủy đơn (trạng thái = 3), sử dụng method hủy chuyên dụng
            if (trangThai == 3) {
                hoaDon = hoaDonService.cancelHoaDon(maHD, lyDo);
            } else {
                // Sử dụng method cập nhật trạng thái thông thường
                hoaDon = hoaDonService.updateTrangThai(maHD, trangThai);
                
                // Thêm ghi chú nếu có lý do
                if (lyDo != null && !lyDo.trim().isEmpty()) {
                    String ghiChuMoi = hoaDon.getGhiChu() != null ? 
                        hoaDon.getGhiChu() + "\n[Cập nhật trạng thái]: " + lyDo : 
                        "[Cập nhật trạng thái]: " + lyDo;
                    hoaDon.setGhiChu(ghiChuMoi);
                    hoaDon.setNgaySua(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                    hoaDon = hoaDonService.save(hoaDon);
                }
            }
            
            // Tạo message phù hợp với trạng thái
            String message = switch (trangThai) {
                case 0 -> "Chuyển trạng thái thành 'Chờ thanh toán' thành công";
                case 1 -> "Chuyển trạng thái thành 'Đã thanh toán' thành công";
                case 2 -> "Chuyển trạng thái thành 'Đang xử lý' thành công";
                case 3 -> "Hủy hóa đơn thành công";
                case 4 -> "Chuyển trạng thái thành 'Hoàn trả' thành công";
                default -> "Cập nhật trạng thái thành công";
            };
            
            return ResponseEntity.ok(ApiResponse.<HoaDon>builder()
                    .success(true)
                    .message(message)
                    .result(hoaDon)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<HoaDon>builder()
                            .success(false)
                            .error("Lỗi khi cập nhật trạng thái: " + e.getMessage())
                            .build());
        }
    }
    
    // API cập nhật trạng thái - PUT method (alternative)
    @PutMapping("/{maHD}/trangthai/{trangThai}")
    @Operation(summary = "Cập nhật trạng thái hóa đơn (PUT)", description = "Cập nhật trạng thái hóa đơn qua URL path")
    @CrossOrigin(origins = "*", methods = {RequestMethod.PUT}, allowedHeaders = "*")
    public ResponseEntity<ApiResponse<HoaDon>> updateTrangThaiByPath(
            @Parameter(description = "Mã hóa đơn", required = true) @PathVariable Integer maHD,
            @Parameter(description = "Trạng thái mới", required = true) @PathVariable Integer trangThai,
            @Parameter(description = "Lý do thay đổi", required = false) @RequestParam(required = false) String lyDo) {
        try {
            HoaDon hoaDon = hoaDonService.updateTrangThai(maHD, trangThai);
            
            // Thêm ghi chú nếu có lý do
            if (lyDo != null && !lyDo.trim().isEmpty()) {
                String ghiChuMoi = hoaDon.getGhiChu() != null ? 
                    hoaDon.getGhiChu() + "\n[Cập nhật trạng thái]: " + lyDo : 
                    "[Cập nhật trạng thái]: " + lyDo;
                hoaDon.setGhiChu(ghiChuMoi);
                hoaDon.setNgaySua(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                hoaDon = hoaDonService.save(hoaDon);
            }
            
            return ResponseEntity.ok(ApiResponse.<HoaDon>builder()
                    .success(true)
                    .message("Cập nhật trạng thái hóa đơn thành công")
                    .result(hoaDon)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<HoaDon>builder()
                            .success(false)
                            .error("Lỗi khi cập nhật trạng thái: " + e.getMessage())
                            .build());
        }
    }
    
    // API thống kê hóa đơn theo khách hàng
    @GetMapping("/by-khachhang/{maKH}/statistics")
    @Operation(summary = "Thống kê hóa đơn theo khách hàng", description = "Thống kê tổng quan hóa đơn của khách hàng")
    public ResponseEntity<ApiResponse<Object>> getHoaDonStatisticsByCustomer(
            @Parameter(description = "Mã khách hàng", required = true) @PathVariable String maKH) {
        try {
            Object statistics = hoaDonService.getStatisticsByCustomer(maKH);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Lấy thống kê hóa đơn theo khách hàng thành công")
                    .result(statistics)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .success(false)
                            .error("Lỗi khi lấy thống kê: " + e.getMessage())
                            .build());
        }
    }
    
    // API đếm hóa đơn theo khách hàng và trạng thái
    @GetMapping("/by-khachhang/{maKH}/count-by-status")
    @Operation(summary = "Đếm hóa đơn theo khách hàng và trạng thái", description = "Đếm số lượng hóa đơn của khách hàng theo từng trạng thái")
    public ResponseEntity<ApiResponse<Object>> countByCustomerAndStatus(
            @Parameter(description = "Mã khách hàng", required = true) @PathVariable String maKH) {
        try {
            Object countByStatus = hoaDonService.countByCustomerAndStatus(maKH);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Đếm hóa đơn theo trạng thái thành công")
                    .result(countByStatus)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .success(false)
                            .error("Lỗi khi đếm hóa đơn: " + e.getMessage())
                            .build());
        }
    }
    
    // ===== FULL DETAILS APIs =====
    
    // API lấy hóa đơn với chi tiết đầy đủ
    @GetMapping("/{maHD}/full-details")
    @Operation(summary = "Lấy hóa đơn với chi tiết đầy đủ", description = "Lấy hóa đơn kèm theo tất cả chi tiết sản phẩm và thông tin liên quan")
    public ResponseEntity<ApiResponse<HoaDonFullDetailsDTO>> getHoaDonFullDetails(
            @Parameter(description = "Mã hóa đơn", required = true) @PathVariable Integer maHD) {
        try {
            HoaDonFullDetailsDTO hoaDonDetails = hoaDonService.getHoaDonFullDetails(maHD);
            return ResponseEntity.ok(ApiResponse.<HoaDonFullDetailsDTO>builder()
                    .success(true)
                    .message("Lấy chi tiết hóa đơn thành công")
                    .result(hoaDonDetails)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<HoaDonFullDetailsDTO>builder()
                            .success(false)
                            .error("Lỗi khi lấy chi tiết hóa đơn: " + e.getMessage())
                            .build());
        }
    }
    
    // API lấy danh sách hóa đơn của khách hàng với chi tiết đầy đủ
    @GetMapping("/by-khachhang/{maKH}/full-details")
    @Operation(summary = "Lấy hóa đơn khách hàng với chi tiết đầy đủ", description = "Lấy danh sách hóa đơn của khách hàng kèm theo chi tiết sản phẩm")
    public ResponseEntity<ApiResponse<List<HoaDonFullDetailsDTO>>> getHoaDonFullDetailsByCustomer(
            @Parameter(description = "Mã khách hàng", required = true) @PathVariable String maKH) {
        try {
            List<HoaDonFullDetailsDTO> hoaDonDetailsList = hoaDonService.getHoaDonFullDetailsByCustomer(maKH);
            return ResponseEntity.ok(ApiResponse.<List<HoaDonFullDetailsDTO>>builder()
                    .success(true)
                    .message("Lấy danh sách hóa đơn với chi tiết thành công")
                    .result(hoaDonDetailsList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<List<HoaDonFullDetailsDTO>>builder()
                            .success(false)
                            .error("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage())
                            .build());
        }
    }
} 