package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.PhieuNhapHang;
import com.example.mini_supermarket.service.PhieuNhapHangService;
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
@RequestMapping("/api/phieunhaphang")
@CrossOrigin(origins = "*")
@Tag(name = "Phiếu nhập hàng", description = "API quản lý phiếu nhập hàng")
public class PhieuNhapHangRestController {

    @Autowired
    private PhieuNhapHangService phieuNhapHangService;

    @Operation(summary = "Lấy tất cả phiếu nhập hàng", description = "Trả về danh sách tất cả phiếu nhập hàng chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = PhieuNhapHang.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<PhieuNhapHang>> getAllPhieuNhapHang() {
        try {
            List<PhieuNhapHang> phieuNhapHangs = phieuNhapHangService.findAllActive();
            return new ResponseEntity<>(phieuNhapHangs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy phiếu nhập hàng theo ID", description = "Trả về thông tin phiếu nhập hàng theo ID (chỉ lấy phiếu nhập hàng chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy phiếu nhập hàng", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = PhieuNhapHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu nhập hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PhieuNhapHang> getPhieuNhapHangById(
            @Parameter(description = "ID của phiếu nhập hàng", required = true) @PathVariable Integer id) {
        try {
            PhieuNhapHang phieuNhapHang = phieuNhapHangService.findActiveById(id);
            if (phieuNhapHang != null) {
                return new ResponseEntity<>(phieuNhapHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Tạo phiếu nhập hàng mới", description = "Tạo một phiếu nhập hàng mới cho kho")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo phiếu nhập hàng thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = PhieuNhapHang.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<PhieuNhapHang> createPhieuNhapHang(@RequestBody PhieuNhapHang phieuNhapHang) {
        try {
            phieuNhapHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            PhieuNhapHang savedPhieuNhapHang = phieuNhapHangService.save(phieuNhapHang);
            return new ResponseEntity<>(savedPhieuNhapHang, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật phiếu nhập hàng", description = "Cập nhật thông tin phiếu nhập hàng theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = PhieuNhapHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu nhập hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PhieuNhapHang> updatePhieuNhapHang(
            @Parameter(description = "ID của phiếu nhập hàng", required = true) @PathVariable Integer id, 
            @RequestBody PhieuNhapHang phieuNhapHang) {
        try {
            PhieuNhapHang existingPhieuNhapHang = phieuNhapHangService.findActiveById(id);
            if (existingPhieuNhapHang != null) {
                phieuNhapHang.setMaPN(id);
                phieuNhapHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                PhieuNhapHang updatedPhieuNhapHang = phieuNhapHangService.save(phieuNhapHang);
                return new ResponseEntity<>(updatedPhieuNhapHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa phiếu nhập hàng", description = "Xóa mềm phiếu nhập hàng (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu nhập hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePhieuNhapHang(
            @Parameter(description = "ID của phiếu nhập hàng", required = true) @PathVariable Integer id) {
        try {
            PhieuNhapHang phieuNhapHang = phieuNhapHangService.findActiveById(id);
            if (phieuNhapHang != null) {
                phieuNhapHangService.softDeleteById(id);
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