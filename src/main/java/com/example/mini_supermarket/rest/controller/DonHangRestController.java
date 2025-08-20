package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.dto.ApiResponse;
import com.example.mini_supermarket.entity.DonHang;
import com.example.mini_supermarket.service.DonHangService;
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

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import com.example.mini_supermarket.dto.OrderCreatedResponse;
import com.example.mini_supermarket.dto.CreateOrderFromCartRequest;

@RestController
@RequestMapping("/api/donhang")
@CrossOrigin(origins = "*")
@Tag(name = "Đơn Hàng", description = "Quản lý đơn hàng")
public class DonHangRestController {

    @Autowired
    private DonHangService donHangService;

    // Lấy tất cả đơn hàng
    @GetMapping
    @Operation(summary = "Lấy tất cả đơn hàng", description = "Lấy danh sách tất cả đơn hàng trong hệ thống")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<List<DonHang>>> getAllDonHang() {
        try {
            List<DonHang> danhSachDonHang = donHangService.getAllDonHang();
            return ResponseEntity.ok(ApiResponse.<List<DonHang>>builder()
                    .success(true)
                    .message("Lấy danh sách đơn hàng thành công")
                    .result(danhSachDonHang)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<DonHang>>builder()
                            .success(false)
                            .error("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Lấy đơn hàng theo mã
    @GetMapping("/{maDH}")
    @Operation(summary = "Lấy đơn hàng theo mã", description = "Lấy thông tin đơn hàng dựa trên mã đơn hàng")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy đơn hàng"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<DonHang>> getDonHangByMaDH(
            @Parameter(description = "Mã đơn hàng", required = true) @PathVariable String maDH) {
        try {
            Optional<DonHang> donHang = donHangService.findDonHangByMaDH(maDH);
            if (donHang.isPresent()) {
                return ResponseEntity.ok(ApiResponse.<DonHang>builder()
                        .success(true)
                        .message("Lấy đơn hàng thành công")
                        .result(donHang.get())
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<DonHang>builder()
                                .success(false)
                                .error("Không tìm thấy đơn hàng với mã: " + maDH)
                                .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<DonHang>builder()
                            .success(false)
                            .error("Lỗi khi lấy đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Tạo đơn hàng mới
    @PostMapping
    @Operation(summary = "Tạo đơn hàng mới", description = "Tạo một đơn hàng mới trong hệ thống")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<DonHang>> createDonHang(
            @Parameter(description = "Thông tin đơn hàng", required = true) @RequestBody DonHang donHang) {
        try {
            DonHang donHangMoi = donHangService.saveDonHang(donHang);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<DonHang>builder()
                            .success(true)
                            .message("Tạo đơn hàng thành công")
                            .result(donHangMoi)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<DonHang>builder()
                            .success(false)
                            .error("Lỗi khi tạo đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Cập nhật đơn hàng
    @PutMapping("/{maDH}")
    @Operation(summary = "Cập nhật đơn hàng", description = "Cập nhật thông tin đơn hàng")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<DonHang>> updateDonHang(
            @Parameter(description = "Mã đơn hàng", required = true) @PathVariable String maDH, 
            @Parameter(description = "Thông tin đơn hàng cập nhật", required = true) @RequestBody DonHang donHang) {
        try {
            if (!maDH.equals(donHang.getMaDH())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.<DonHang>builder()
                                .success(false)
                                .error("Mã đơn hàng không khớp")
                                .build());
            }
            DonHang donHangCapNhat = donHangService.saveDonHang(donHang);
            return ResponseEntity.ok(ApiResponse.<DonHang>builder()
                    .success(true)
                    .message("Cập nhật đơn hàng thành công")
                    .result(donHangCapNhat)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<DonHang>builder()
                            .success(false)
                            .error("Lỗi khi cập nhật đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Xóa đơn hàng
    @DeleteMapping("/{maDH}")
    @Operation(summary = "Xóa đơn hàng", description = "Xóa đơn hàng theo mã")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xóa thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<Void>> deleteDonHang(
            @Parameter(description = "Mã đơn hàng", required = true) @PathVariable String maDH) {
        try {
            donHangService.deleteDonHang(maDH);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Xóa đơn hàng thành công")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .error("Lỗi khi xóa đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Tìm đơn hàng theo khách hàng
    @GetMapping("/khachhang/{maKH}")
    @Operation(summary = "Tìm đơn hàng theo khách hàng", description = "Lấy danh sách đơn hàng của một khách hàng")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<List<DonHang>>> getDonHangByKhachHang(
            @Parameter(description = "Mã khách hàng", required = true) @PathVariable String maKH) {
        try {
            List<DonHang> danhSachDonHang = donHangService.findDonHangByKhachHang(maKH);
            return ResponseEntity.ok(ApiResponse.<List<DonHang>>builder()
                    .success(true)
                    .message("Lấy danh sách đơn hàng theo khách hàng thành công")
                    .result(danhSachDonHang)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<DonHang>>builder()
                            .success(false)
                            .error("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Tìm đơn hàng theo nhân viên
    @GetMapping("/nhanvien/{maNV}")
    @Operation(summary = "Tìm đơn hàng theo nhân viên", description = "Lấy danh sách đơn hàng của một nhân viên")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<List<DonHang>>> getDonHangByNhanVien(
            @Parameter(description = "Mã nhân viên", required = true) @PathVariable String maNV) {
        try {
            List<DonHang> danhSachDonHang = donHangService.findDonHangByNhanVien(maNV);
            return ResponseEntity.ok(ApiResponse.<List<DonHang>>builder()
                    .success(true)
                    .message("Lấy danh sách đơn hàng theo nhân viên thành công")
                    .result(danhSachDonHang)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<DonHang>>builder()
                            .success(false)
                            .error("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Tìm đơn hàng theo trạng thái
    @GetMapping("/trangthai/{trangThai}")
    @Operation(summary = "Tìm đơn hàng theo trạng thái", description = "Lấy danh sách đơn hàng theo trạng thái")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<List<DonHang>>> getDonHangByTrangThai(
            @Parameter(description = "Trạng thái đơn hàng", required = true) @PathVariable String trangThai) {
        try {
            List<DonHang> danhSachDonHang = donHangService.findDonHangByTrangThai(trangThai);
            return ResponseEntity.ok(ApiResponse.<List<DonHang>>builder()
                    .success(true)
                    .message("Lấy danh sách đơn hàng theo trạng thái thành công")
                    .result(danhSachDonHang)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<DonHang>>builder()
                            .success(false)
                            .error("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Cập nhật trạng thái đơn hàng
    @PatchMapping("/{maDH}/trangthai")
    @Operation(summary = "Cập nhật trạng thái đơn hàng", description = "Cập nhật trạng thái của đơn hàng")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<DonHang>> updateTrangThaiDonHang(
            @Parameter(description = "Mã đơn hàng", required = true) @PathVariable String maDH, 
            @Parameter(description = "Trạng thái mới", required = true) @RequestParam String trangThaiMoi) {
        try {
            DonHang donHang = donHangService.updateTrangThaiDonHang(maDH, trangThaiMoi);
            return ResponseEntity.ok(ApiResponse.<DonHang>builder()
                    .success(true)
                    .message("Cập nhật trạng thái đơn hàng thành công")
                    .result(donHang)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<DonHang>builder()
                            .success(false)
                            .error("Lỗi khi cập nhật trạng thái: " + e.getMessage())
                            .build());
        }
    }

    // Cập nhật ngày giao hàng
    @PatchMapping("/{maDH}/ngaygiaohang")
    @Operation(summary = "Cập nhật ngày giao hàng", description = "Cập nhật ngày giao hàng của đơn hàng")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<DonHang>> updateNgayGiaoHang(
            @Parameter(description = "Mã đơn hàng", required = true) @PathVariable String maDH, 
            @Parameter(description = "Ngày giao hàng mới (yyyy-MM-dd HH:mm:ss)", required = true) @RequestParam String ngayGiaoHang) {
        try {
            Timestamp ngayGiao = Timestamp.valueOf(ngayGiaoHang);
            DonHang donHang = donHangService.updateNgayGiaoHang(maDH, ngayGiao);
            return ResponseEntity.ok(ApiResponse.<DonHang>builder()
                    .success(true)
                    .message("Cập nhật ngày giao hàng thành công")
                    .result(donHang)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<DonHang>builder()
                            .success(false)
                            .error("Lỗi khi cập nhật ngày giao hàng: " + e.getMessage())
                            .build());
        }
    }

    // Tìm đơn hàng chưa giao hàng
    @GetMapping("/chuagiao")
    @Operation(summary = "Tìm đơn hàng chưa giao", description = "Lấy danh sách đơn hàng chưa giao hàng")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<List<DonHang>>> getDonHangChuaGiao() {
        try {
            List<DonHang> danhSachDonHang = donHangService.findDonHangChuaGiao();
            return ResponseEntity.ok(ApiResponse.<List<DonHang>>builder()
                    .success(true)
                    .message("Lấy danh sách đơn hàng chưa giao thành công")
                    .result(danhSachDonHang)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<DonHang>>builder()
                            .success(false)
                            .error("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Đếm số đơn hàng theo trạng thái
    @GetMapping("/count/trangthai/{trangThai}")
    @Operation(summary = "Đếm đơn hàng theo trạng thái", description = "Đếm số lượng đơn hàng theo trạng thái")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<Long>> countDonHangByTrangThai(
            @Parameter(description = "Trạng thái đơn hàng", required = true) @PathVariable String trangThai) {
        try {
            long soLuong = donHangService.countDonHangByTrangThai(trangThai);
            return ResponseEntity.ok(ApiResponse.<Long>builder()
                    .success(true)
                    .message("Đếm số đơn hàng thành công")
                    .result(soLuong)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Long>builder()
                            .success(false)
                            .error("Lỗi khi đếm đơn hàng: " + e.getMessage())
                            .build());
        }
    }

    // Tạo đơn hàng từ giỏ hàng
    @PostMapping("/from-cart")
    @Operation(summary = "Tạo đơn hàng từ giỏ hàng", description = "Tạo đơn hàng từ các item được chọn trong giỏ hàng")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo đơn hàng thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponse<OrderCreatedResponse>> createOrderFromCart(
            @Parameter(description = "Thông tin tạo đơn hàng từ giỏ hàng", required = true) 
            @RequestBody CreateOrderFromCartRequest request) {
        try {
            OrderCreatedResponse orderResponse = donHangService.createOrderFromCart(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<OrderCreatedResponse>builder()
                            .success(true)
                            .message("Tạo đơn hàng từ giỏ hàng thành công")
                            .result(orderResponse)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<OrderCreatedResponse>builder()
                            .success(false)
                            .error("Lỗi khi tạo đơn hàng từ giỏ hàng: " + e.getMessage())
                            .build());
        }
    }
}
