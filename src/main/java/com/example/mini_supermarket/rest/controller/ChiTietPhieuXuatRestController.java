package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.ChiTietPhieuXuat;
import com.example.mini_supermarket.service.ChiTietPhieuXuatService;
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
@RequestMapping("/api/chitietphieuxuat")
@CrossOrigin(origins = "*")
@Tag(name = "Chi tiết phiếu xuất", description = "API quản lý chi tiết phiếu xuất kho")
public class ChiTietPhieuXuatRestController {

    @Autowired
    private ChiTietPhieuXuatService chiTietPhieuXuatService;

    @Operation(summary = "Lấy tất cả chi tiết phiếu xuất", description = "Trả về danh sách tất cả chi tiết phiếu xuất chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietPhieuXuat.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<ChiTietPhieuXuat>> getAllChiTietPhieuXuat() {
        try {
            List<ChiTietPhieuXuat> chiTietPhieuXuats = chiTietPhieuXuatService.findAllActive();
            return new ResponseEntity<>(chiTietPhieuXuats, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy chi tiết phiếu xuất theo ID", description = "Trả về thông tin chi tiết phiếu xuất theo ID (chỉ lấy chi tiết phiếu xuất chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy chi tiết phiếu xuất", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietPhieuXuat.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết phiếu xuất"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ChiTietPhieuXuat> getChiTietPhieuXuatById(
            @Parameter(description = "ID của chi tiết phiếu xuất", required = true) @PathVariable Integer id) {
        try {
            ChiTietPhieuXuat chiTietPhieuXuat = chiTietPhieuXuatService.findActiveById(id);
            if (chiTietPhieuXuat != null) {
                return new ResponseEntity<>(chiTietPhieuXuat, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm chi tiết phiếu xuất mới", description = "Thêm sản phẩm vào phiếu xuất kho")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thêm chi tiết phiếu xuất thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietPhieuXuat.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<ChiTietPhieuXuat> createChiTietPhieuXuat(@RequestBody ChiTietPhieuXuat chiTietPhieuXuat) {
        try {
            chiTietPhieuXuat.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            ChiTietPhieuXuat savedChiTietPhieuXuat = chiTietPhieuXuatService.save(chiTietPhieuXuat);
            return new ResponseEntity<>(savedChiTietPhieuXuat, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật chi tiết phiếu xuất", description = "Cập nhật thông tin chi tiết phiếu xuất theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietPhieuXuat.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết phiếu xuất"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ChiTietPhieuXuat> updateChiTietPhieuXuat(
            @Parameter(description = "ID của chi tiết phiếu xuất", required = true) @PathVariable Integer id, 
            @RequestBody ChiTietPhieuXuat chiTietPhieuXuat) {
        try {
            ChiTietPhieuXuat existingChiTietPhieuXuat = chiTietPhieuXuatService.findActiveById(id);
            if (existingChiTietPhieuXuat != null) {
                chiTietPhieuXuat.setMaCTPXK(id);
                chiTietPhieuXuat.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                ChiTietPhieuXuat updatedChiTietPhieuXuat = chiTietPhieuXuatService.save(chiTietPhieuXuat);
                return new ResponseEntity<>(updatedChiTietPhieuXuat, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa chi tiết phiếu xuất", description = "Xóa mềm chi tiết phiếu xuất (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết phiếu xuất"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteChiTietPhieuXuat(
            @Parameter(description = "ID của chi tiết phiếu xuất", required = true) @PathVariable Integer id) {
        try {
            ChiTietPhieuXuat chiTietPhieuXuat = chiTietPhieuXuatService.findActiveById(id);
            if (chiTietPhieuXuat != null) {
                chiTietPhieuXuatService.softDeleteById(id);
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