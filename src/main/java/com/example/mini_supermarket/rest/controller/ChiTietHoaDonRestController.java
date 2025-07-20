package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.ChiTietHoaDon;
import com.example.mini_supermarket.service.ChiTietHoaDonService;
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
@RequestMapping("/api/chitiethoadon")
@CrossOrigin(origins = "*")
@Tag(name = "Chi tiết hóa đơn", description = "API quản lý chi tiết hóa đơn")
public class ChiTietHoaDonRestController {

    @Autowired
    private ChiTietHoaDonService chiTietHoaDonService;

    @Operation(summary = "Lấy tất cả chi tiết hóa đơn", description = "Trả về danh sách tất cả chi tiết hóa đơn chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietHoaDon.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
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
            @ApiResponse(responseCode = "200", description = "Tìm thấy chi tiết hóa đơn", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietHoaDon.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết hóa đơn"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
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
            @ApiResponse(responseCode = "201", description = "Thêm chi tiết hóa đơn thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietHoaDon.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<ChiTietHoaDon> createChiTietHoaDon(@RequestBody ChiTietHoaDon chiTietHoaDon) {
        try {
            chiTietHoaDon.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            ChiTietHoaDon savedChiTietHoaDon = chiTietHoaDonService.save(chiTietHoaDon);
            return new ResponseEntity<>(savedChiTietHoaDon, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật chi tiết hóa đơn", description = "Cập nhật thông tin chi tiết hóa đơn theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietHoaDon.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết hóa đơn"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ChiTietHoaDon> updateChiTietHoaDon(
            @Parameter(description = "ID của chi tiết hóa đơn", required = true) @PathVariable Integer id, 
            @RequestBody ChiTietHoaDon chiTietHoaDon) {
        try {
            ChiTietHoaDon existingChiTietHoaDon = chiTietHoaDonService.findActiveById(id);
            if (existingChiTietHoaDon != null) {
                chiTietHoaDon.setMaCTHD(id);
                chiTietHoaDon.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                ChiTietHoaDon updatedChiTietHoaDon = chiTietHoaDonService.save(chiTietHoaDon);
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
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết hóa đơn"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
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
} 