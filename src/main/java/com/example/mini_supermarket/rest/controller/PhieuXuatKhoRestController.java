package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.PhieuXuatKho;
import com.example.mini_supermarket.service.PhieuXuatKhoService;
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
@RequestMapping("/api/phieuxuatkho")
@CrossOrigin(origins = "*")
@Tag(name = "Phiếu xuất kho", description = "API quản lý phiếu xuất kho")
public class PhieuXuatKhoRestController {

    @Autowired
    private PhieuXuatKhoService phieuXuatKhoService;

    @Operation(summary = "Lấy tất cả phiếu xuất kho", description = "Trả về danh sách tất cả phiếu xuất kho chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = PhieuXuatKho.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<PhieuXuatKho>> getAllPhieuXuatKho() {
        try {
            List<PhieuXuatKho> phieuXuatKhos = phieuXuatKhoService.findAllActive();
            return new ResponseEntity<>(phieuXuatKhos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy phiếu xuất kho theo ID", description = "Trả về thông tin phiếu xuất kho theo ID (chỉ lấy phiếu xuất kho chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy phiếu xuất kho", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = PhieuXuatKho.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu xuất kho"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PhieuXuatKho> getPhieuXuatKhoById(
            @Parameter(description = "ID của phiếu xuất kho", required = true) @PathVariable Integer id) {
        try {
            PhieuXuatKho phieuXuatKho = phieuXuatKhoService.findActiveById(id);
            if (phieuXuatKho != null) {
                return new ResponseEntity<>(phieuXuatKho, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Tạo phiếu xuất kho mới", description = "Tạo một phiếu xuất kho mới cho việc xuất hàng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo phiếu xuất kho thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = PhieuXuatKho.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<PhieuXuatKho> createPhieuXuatKho(@RequestBody PhieuXuatKho phieuXuatKho) {
        try {
            phieuXuatKho.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            PhieuXuatKho savedPhieuXuatKho = phieuXuatKhoService.save(phieuXuatKho);
            return new ResponseEntity<>(savedPhieuXuatKho, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật phiếu xuất kho", description = "Cập nhật thông tin phiếu xuất kho theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = PhieuXuatKho.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu xuất kho"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PhieuXuatKho> updatePhieuXuatKho(
            @Parameter(description = "ID của phiếu xuất kho", required = true) @PathVariable Integer id, 
            @RequestBody PhieuXuatKho phieuXuatKho) {
        try {
            PhieuXuatKho existingPhieuXuatKho = phieuXuatKhoService.findActiveById(id);
            if (existingPhieuXuatKho != null) {
                phieuXuatKho.setMaPXK(id);
                phieuXuatKho.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                PhieuXuatKho updatedPhieuXuatKho = phieuXuatKhoService.save(phieuXuatKho);
                return new ResponseEntity<>(updatedPhieuXuatKho, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa phiếu xuất kho", description = "Xóa mềm phiếu xuất kho (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu xuất kho"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePhieuXuatKho(
            @Parameter(description = "ID của phiếu xuất kho", required = true) @PathVariable Integer id) {
        try {
            PhieuXuatKho phieuXuatKho = phieuXuatKhoService.findActiveById(id);
            if (phieuXuatKho != null) {
                phieuXuatKhoService.softDeleteById(id);
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