package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.BaoCaoDoanhThuHangNgay;
import com.example.mini_supermarket.service.BaoCaoDoanhThuHangNgayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/baocao-doanhthu")
@Tag(name = "Báo Cáo Doanh Thu Hàng Ngày", description = "Quản lý báo cáo doanh thu và lợi nhuận hàng ngày")
public class BaoCaoDoanhThuHangNgayController {
    
    @Autowired
    private BaoCaoDoanhThuHangNgayService baoCaoService;
    
    // ===== CRUD CƠ BẢN =====
    
    @GetMapping
    @Operation(summary = "Lấy tất cả báo cáo doanh thu", description = "Lấy danh sách tất cả báo cáo doanh thu hàng ngày")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = BaoCaoDoanhThuHangNgay.class))),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<List<BaoCaoDoanhThuHangNgay>> getAllBaoCao() {
        try {
            List<BaoCaoDoanhThuHangNgay> baoCaos = baoCaoService.findAll();
            return new ResponseEntity<>(baoCaos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Lấy báo cáo theo ID", description = "Lấy thông tin chi tiết báo cáo doanh thu theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = BaoCaoDoanhThuHangNgay.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<BaoCaoDoanhThuHangNgay> getBaoCaoById(
            @Parameter(description = "ID của báo cáo") @PathVariable Integer id) {
        try {
            BaoCaoDoanhThuHangNgay baoCao = baoCaoService.findById(id);
            if (baoCao != null) {
                return new ResponseEntity<>(baoCao, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping
    @Operation(summary = "Tạo báo cáo mới", description = "Tạo một báo cáo doanh thu mới")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo thành công", content = @Content(schema = @Schema(implementation = BaoCaoDoanhThuHangNgay.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<BaoCaoDoanhThuHangNgay> createBaoCao(
            @Parameter(description = "Thông tin báo cáo") @RequestBody BaoCaoDoanhThuHangNgay baoCao) {
        try {
            BaoCaoDoanhThuHangNgay savedBaoCao = baoCaoService.save(baoCao);
            return new ResponseEntity<>(savedBaoCao, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật báo cáo", description = "Cập nhật thông tin báo cáo doanh thu theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công", content = @Content(schema = @Schema(implementation = BaoCaoDoanhThuHangNgay.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<BaoCaoDoanhThuHangNgay> updateBaoCao(
            @Parameter(description = "ID của báo cáo") @PathVariable Integer id,
            @Parameter(description = "Thông tin cập nhật") @RequestBody BaoCaoDoanhThuHangNgay baoCao) {
        try {
            baoCao.setId(id);
            BaoCaoDoanhThuHangNgay updatedBaoCao = baoCaoService.save(baoCao);
            return new ResponseEntity<>(updatedBaoCao, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa báo cáo", description = "Xóa báo cáo doanh thu theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Xóa thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<Void> deleteBaoCao(
            @Parameter(description = "ID của báo cáo") @PathVariable Integer id) {
        try {
            baoCaoService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ===== TÌM KIẾM THEO NGÀY =====
    
    @GetMapping("/ngay/{ngayBan}")
    @Operation(summary = "Tìm báo cáo theo ngày", description = "Lấy danh sách báo cáo doanh thu theo ngày cụ thể")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<List<BaoCaoDoanhThuHangNgay>> getBaoCaoTheoNgay(
            @Parameter(description = "Ngày bán (yyyy-MM-dd)") 
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBan) {
        try {
            List<BaoCaoDoanhThuHangNgay> baoCaos = baoCaoService.findByNgayBan(ngayBan);
            return new ResponseEntity<>(baoCaos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/ngay/{ngayBan}/cuahang/{maCH}")
    @Operation(summary = "Tìm báo cáo theo ngày và cửa hàng", description = "Lấy danh sách báo cáo doanh thu theo ngày và mã cửa hàng")
    public ResponseEntity<List<BaoCaoDoanhThuHangNgay>> getBaoCaoTheoNgayVaCuaHang(
            @Parameter(description = "Ngày bán (yyyy-MM-dd)") 
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBan,
            @Parameter(description = "Mã cửa hàng") @PathVariable String maCH) {
        try {
            List<BaoCaoDoanhThuHangNgay> baoCaos = baoCaoService.findByNgayBanAndMaCH(ngayBan, maCH);
            return new ResponseEntity<>(baoCaos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/ngay/{ngayBan}/sanpham/{maSP}")
    @Operation(summary = "Tìm báo cáo theo ngày và sản phẩm", description = "Lấy danh sách báo cáo doanh thu theo ngày và mã sản phẩm")
    public ResponseEntity<List<BaoCaoDoanhThuHangNgay>> getBaoCaoTheoNgayVaSanPham(
            @Parameter(description = "Ngày bán (yyyy-MM-dd)") 
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBan,
            @Parameter(description = "Mã sản phẩm") @PathVariable String maSP) {
        try {
            List<BaoCaoDoanhThuHangNgay> baoCaos = baoCaoService.findByNgayBanAndMaSP(ngayBan, maSP);
            return new ResponseEntity<>(baoCaos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ===== TÌM KIẾM THEO KHOẢNG THỜI GIAN =====
    
    @GetMapping("/khoang-thoi-gian")
    @Operation(summary = "Tìm báo cáo theo khoảng thời gian", description = "Lấy danh sách báo cáo doanh thu theo khoảng thời gian")
    public ResponseEntity<List<BaoCaoDoanhThuHangNgay>> getBaoCaoTheoKhoangThoiGian(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBatDau,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayKetThuc) {
        try {
            List<BaoCaoDoanhThuHangNgay> baoCaos = baoCaoService.findByKhoangThoiGian(ngayBatDau, ngayKetThuc);
            return new ResponseEntity<>(baoCaos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/khoang-thoi-gian/cuahang/{maCH}")
    @Operation(summary = "Tìm báo cáo theo khoảng thời gian và cửa hàng", description = "Lấy danh sách báo cáo doanh thu theo khoảng thời gian và mã cửa hàng")
    public ResponseEntity<List<BaoCaoDoanhThuHangNgay>> getBaoCaoTheoKhoangThoiGianVaCuaHang(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBatDau,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayKetThuc,
            @Parameter(description = "Mã cửa hàng") @PathVariable String maCH) {
        try {
            List<BaoCaoDoanhThuHangNgay> baoCaos = baoCaoService.findByKhoangThoiGianAndMaCH(ngayBatDau, ngayKetThuc, maCH);
            return new ResponseEntity<>(baoCaos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ===== TỔNG HỢP DOANH THU =====
    
    @GetMapping("/tong-doanh-thu/ngay/{ngayBan}")
    @Operation(summary = "Tổng doanh thu theo ngày", description = "Lấy tổng doanh thu của một ngày cụ thể")
    public ResponseEntity<Double> getTongDoanhThuTheoNgay(
            @Parameter(description = "Ngày bán (yyyy-MM-dd)") 
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBan) {
        try {
            Double tongDoanhThu = baoCaoService.tongDoanhThuTheoNgay(ngayBan);
            return new ResponseEntity<>(tongDoanhThu, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/tong-doanh-thu/khoang-thoi-gian")
    @Operation(summary = "Tổng doanh thu theo khoảng thời gian", description = "Lấy tổng doanh thu theo khoảng thời gian")
    public ResponseEntity<Double> getTongDoanhThuTheoKhoangThoiGian(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBatDau,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayKetThuc) {
        try {
            Double tongDoanhThu = baoCaoService.tongDoanhThuTheoKhoangThoiGian(ngayBatDau, ngayKetThuc);
            return new ResponseEntity<>(tongDoanhThu, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ===== TỔNG HỢP LỢI NHUẬN =====
    
    @GetMapping("/tong-loi-nhuan/ngay/{ngayBan}")
    @Operation(summary = "Tổng lợi nhuận theo ngày", description = "Lấy tổng lợi nhuận của một ngày cụ thể")
    public ResponseEntity<Double> getTongLoiNhuanTheoNgay(
            @Parameter(description = "Ngày bán (yyyy-MM-dd)") 
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBan) {
        try {
            Double tongLoiNhuan = baoCaoService.tongLoiNhuanTheoNgay(ngayBan);
            return new ResponseEntity<>(tongLoiNhuan, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/tong-loi-nhuan/khoang-thoi-gian")
    @Operation(summary = "Tổng lợi nhuận theo khoảng thời gian", description = "Lấy tổng lợi nhuận theo khoảng thời gian")
    public ResponseEntity<Double> getTongLoiNhuanTheoKhoangThoiGian(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBatDau,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayKetThuc) {
        try {
            Double tongLoiNhuan = baoCaoService.tongLoiNhuanTheoKhoangThoiGian(ngayBatDau, ngayKetThuc);
            return new ResponseEntity<>(tongLoiNhuan, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ===== TOP SẢN PHẨM =====
    
    @GetMapping("/top-san-pham/ban-chay")
    @Operation(summary = "Top sản phẩm bán chạy", description = "Lấy danh sách top sản phẩm bán chạy theo khoảng thời gian")
    public ResponseEntity<List<Map<String, Object>>> getTopSanPhamBanChay(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBatDau,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayKetThuc,
            @Parameter(description = "Số lượng sản phẩm trả về") @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> topSanPham = baoCaoService.topSanPhamBanChay(ngayBatDau, ngayKetThuc, limit);
            return new ResponseEntity<>(topSanPham, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/top-san-pham/loi-nhuan-cao")
    @Operation(summary = "Top sản phẩm lợi nhuận cao", description = "Lấy danh sách top sản phẩm có lợi nhuận cao theo khoảng thời gian")
    public ResponseEntity<List<Map<String, Object>>> getTopSanPhamLoiNhuanCao(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBatDau,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayKetThuc,
            @Parameter(description = "Số lượng sản phẩm trả về") @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> topSanPham = baoCaoService.topSanPhamLoiNhuanCao(ngayBatDau, ngayKetThuc, limit);
            return new ResponseEntity<>(topSanPham, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ===== BÁO CÁO THEO CỬA HÀNG =====
    
    @GetMapping("/cuahang/doanh-thu")
    @Operation(summary = "Doanh thu theo cửa hàng", description = "Lấy doanh thu theo cửa hàng và khoảng thời gian")
    public ResponseEntity<List<Map<String, Object>>> getDoanhThuTheoCuaHang(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBatDau,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayKetThuc) {
        try {
            List<Map<String, Object>> doanhThuTheoCuaHang = baoCaoService.doanhThuTheoCuaHang(ngayBatDau, ngayKetThuc);
            return new ResponseEntity<>(doanhThuTheoCuaHang, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/cuahang/loi-nhuan")
    @Operation(summary = "Lợi nhuận theo cửa hàng", description = "Lấy lợi nhuận theo cửa hàng và khoảng thời gian")
    public ResponseEntity<List<Map<String, Object>>> getLoiNhuanTheoCuaHang(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBatDau,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayKetThuc) {
        try {
            List<Map<String, Object>> loiNhuanTheoCuaHang = baoCaoService.loiNhuanTheoCuaHang(ngayBatDau, ngayKetThuc);
            return new ResponseEntity<>(loiNhuanTheoCuaHang, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ===== CẬP NHẬT BÁO CÁO (GỌI FUNCTION SQL) =====
    
    @PostMapping("/cap-nhat/ngay/{ngayCapNhat}")
    @Operation(summary = "Cập nhật báo cáo theo ngày", description = "Gọi function SQL để cập nhật báo cáo doanh thu cho một ngày cụ thể")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<String> capNhatBaoCaoTheoNgay(
            @Parameter(description = "Ngày cập nhật (yyyy-MM-dd)") 
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayCapNhat) {
        try {
            baoCaoService.capNhatBaoCaoDoanhThu(ngayCapNhat);
            return new ResponseEntity<>("Cập nhật báo cáo thành công cho ngày: " + ngayCapNhat, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Lỗi cập nhật báo cáo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/cap-nhat/hom-nay")
    @Operation(summary = "Cập nhật báo cáo hôm nay", description = "Gọi function SQL để cập nhật báo cáo doanh thu cho ngày hôm nay")
    public ResponseEntity<String> capNhatBaoCaoHomNay() {
        try {
            baoCaoService.capNhatBaoCaoDoanhThuHomNay();
            return new ResponseEntity<>("Cập nhật báo cáo thành công cho ngày hôm nay", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Lỗi cập nhật báo cáo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/cap-nhat/hom-qua")
    @Operation(summary = "Cập nhật báo cáo hôm qua", description = "Gọi function SQL để cập nhật báo cáo doanh thu cho ngày hôm qua")
    public ResponseEntity<String> capNhatBaoCaoHomQua() {
        try {
            baoCaoService.capNhatBaoCaoDoanhThuHomQua();
            return new ResponseEntity<>("Cập nhật báo cáo thành công cho ngày hôm qua", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Lỗi cập nhật báo cáo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/cap-nhat/khoang-thoi-gian")
    @Operation(summary = "Cập nhật báo cáo theo khoảng thời gian", description = "Gọi function SQL để cập nhật báo cáo doanh thu cho một khoảng thời gian")
    public ResponseEntity<String> capNhatBaoCaoTheoKhoangThoiGian(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBatDau,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayKetThuc) {
        try {
            baoCaoService.capNhatBaoCaoDoanhThuTheoKhoangThoiGian(ngayBatDau, ngayKetThuc);
            return new ResponseEntity<>("Cập nhật báo cáo thành công từ " + ngayBatDau + " đến " + ngayKetThuc, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Lỗi cập nhật báo cáo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ===== THỐNG KÊ TỔNG QUAN =====
    
    @GetMapping("/thong-ke/tong-quan")
    @Operation(summary = "Thống kê tổng quan", description = "Lấy thống kê tổng quan theo khoảng thời gian")
    public ResponseEntity<Map<String, Object>> getThongKeTongQuan(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBatDau,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayKetThuc) {
        try {
            Map<String, Object> thongKe = baoCaoService.thongKeTongQuan(ngayBatDau, ngayKetThuc);
            return new ResponseEntity<>(thongKe, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/thong-ke/tong-quan/cuahang/{maCH}")
    @Operation(summary = "Thống kê tổng quan theo cửa hàng", description = "Lấy thống kê tổng quan theo khoảng thời gian và cửa hàng")
    public ResponseEntity<Map<String, Object>> getThongKeTongQuanTheoCuaHang(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayBatDau,
            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayKetThuc,
            @Parameter(description = "Mã cửa hàng") @PathVariable String maCH) {
        try {
            Map<String, Object> thongKe = baoCaoService.thongKeTongQuanTheoCuaHang(ngayBatDau, ngayKetThuc, maCH);
            return new ResponseEntity<>(thongKe, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
