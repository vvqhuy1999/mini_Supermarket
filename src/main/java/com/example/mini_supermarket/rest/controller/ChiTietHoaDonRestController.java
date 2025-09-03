package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.ChiTietHoaDon;
import com.example.mini_supermarket.service.ChiTietHoaDonService;
import com.example.mini_supermarket.dto.ApiResponse;
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

@RestController
@RequestMapping("/api/chitiethoadon")
@CrossOrigin(origins = "*")
@Tag(name = "Chi tiết hóa đơn", description = "API quản lý chi tiết hóa đơn")
public class ChiTietHoaDonRestController {

    @Autowired
    private ChiTietHoaDonService chiTietHoaDonService;

    @Operation(summary = "Lấy tất cả chi tiết hóa đơn", description = "Trả về danh sách tất cả chi tiết hóa đơn chưa bị xóa")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietHoaDon.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<ChiTietHoaDon>> getAllChiTietHoaDon() {
        try {
            List<ChiTietHoaDon> chiTietHoaDons = chiTietHoaDonService.findAllActive();
            return new ResponseEntity<>(chiTietHoaDons, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy chi tiết hóa đơn theo ID", description = "Trả về thông tin chi tiết hóa đơn theo ID (chỉ lấy chi tiết hóa đơn chưa bị xóa)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tìm thấy chi tiết hóa đơn", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietHoaDon.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết hóa đơn"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ChiTietHoaDon> getChiTietHoaDonById(
            @Parameter(description = "ID của chi tiết hóa đơn", required = true) @PathVariable Integer id) {
        try {
            ChiTietHoaDon chiTietHoaDon = chiTietHoaDonService.findActiveById(id);
            if (chiTietHoaDon != null) {
                return new ResponseEntity<>(chiTietHoaDon, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm chi tiết hóa đơn mới", description = "Thêm sản phẩm vào hóa đơn")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Thêm chi tiết hóa đơn thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietHoaDon.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<ChiTietHoaDon> createChiTietHoaDon(@RequestBody ChiTietHoaDon chiTietHoaDon) {
        try {
            // Entity sẽ tự set isDeleted = false mặc định
            ChiTietHoaDon savedChiTietHoaDon = chiTietHoaDonService.save(chiTietHoaDon);
            return new ResponseEntity<>(savedChiTietHoaDon, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật chi tiết hóa đơn", description = "Cập nhật thông tin chi tiết hóa đơn theo ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietHoaDon.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết hóa đơn"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ChiTietHoaDon> updateChiTietHoaDon(
            @Parameter(description = "ID của chi tiết hóa đơn", required = true) @PathVariable Integer id, 
            @RequestBody ChiTietHoaDon chiTietHoaDon) {
        try {
            ChiTietHoaDon existingChiTietHoaDon = chiTietHoaDonService.findActiveById(id);
            if (existingChiTietHoaDon != null) {
                // Copy dữ liệu từ existing entity
                existingChiTietHoaDon.setSoLuong(chiTietHoaDon.getSoLuong());
                existingChiTietHoaDon.setDonGiaBan(chiTietHoaDon.getDonGiaBan());
                existingChiTietHoaDon.setGiamGia(chiTietHoaDon.getGiamGia());
                ChiTietHoaDon updatedChiTietHoaDon = chiTietHoaDonService.save(existingChiTietHoaDon);
                return new ResponseEntity<>(updatedChiTietHoaDon, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa chi tiết hóa đơn", description = "Xóa mềm chi tiết hóa đơn (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết hóa đơn"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteChiTietHoaDon(
            @Parameter(description = "ID của chi tiết hóa đơn", required = true) @PathVariable Integer id) {
        try {
            ChiTietHoaDon chiTietHoaDon = chiTietHoaDonService.findActiveById(id);
            if (chiTietHoaDon != null) {
                chiTietHoaDonService.softDeleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ===== API MỚI - Lấy chi tiết hóa đơn theo mã hóa đơn =====
    
    @Operation(summary = "Lấy chi tiết hóa đơn theo mã hóa đơn", description = "Trả về danh sách chi tiết hóa đơn của một hóa đơn cụ thể")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy hóa đơn"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/hoadon/{maHD}")
    public ResponseEntity<com.example.mini_supermarket.dto.ApiResponse<List<ChiTietHoaDon>>> getChiTietHoaDonByMaHD(
            @Parameter(description = "Mã hóa đơn", required = true) @PathVariable Integer maHD) {
        try {
            List<ChiTietHoaDon> chiTietList = chiTietHoaDonService.findByHoaDonId(maHD);
            return ResponseEntity.ok(ApiResponse.<List<ChiTietHoaDon>>builder()
                    .success(true)
                    .message("Lấy chi tiết hóa đơn thành công")
                    .result(chiTietList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<ChiTietHoaDon>>builder()
                            .success(false)
                            .error("Lỗi khi lấy chi tiết hóa đơn: " + e.getMessage())
                            .build());
        }
    }
} 