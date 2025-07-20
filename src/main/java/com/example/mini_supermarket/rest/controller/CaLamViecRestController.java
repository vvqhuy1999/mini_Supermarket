package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.CaLamViec;
import com.example.mini_supermarket.service.CaLamViecService;
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
@RequestMapping("/api/calamviec")
@CrossOrigin(origins = "*")
@Tag(name = "Ca làm việc", description = "API quản lý ca làm việc")
public class CaLamViecRestController {

    @Autowired
    private CaLamViecService caLamViecService;

    @Operation(summary = "Lấy tất cả ca làm việc", description = "Trả về danh sách tất cả ca làm việc chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = CaLamViec.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<CaLamViec>> getAllCaLamViec() {
        try {
            List<CaLamViec> caLamViecs = caLamViecService.findAllActive();
            return new ResponseEntity<>(caLamViecs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy ca làm việc theo ID", description = "Trả về thông tin ca làm việc theo ID (chỉ lấy ca làm việc chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy ca làm việc", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = CaLamViec.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy ca làm việc"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CaLamViec> getCaLamViecById(
            @Parameter(description = "ID của ca làm việc", required = true) @PathVariable Integer id) {
        try {
            CaLamViec caLamViec = caLamViecService.findActiveById(id);
            if (caLamViec != null) {
                return new ResponseEntity<>(caLamViec, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm ca làm việc mới", description = "Tạo một ca làm việc mới trong hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo ca làm việc thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = CaLamViec.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<CaLamViec> createCaLamViec(@RequestBody CaLamViec caLamViec) {
        try {
            caLamViec.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            CaLamViec savedCaLamViec = caLamViecService.save(caLamViec);
            return new ResponseEntity<>(savedCaLamViec, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật ca làm việc", description = "Cập nhật thông tin ca làm việc theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = CaLamViec.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy ca làm việc"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CaLamViec> updateCaLamViec(
            @Parameter(description = "ID của ca làm việc", required = true) @PathVariable Integer id, 
            @RequestBody CaLamViec caLamViec) {
        try {
            CaLamViec existingCaLamViec = caLamViecService.findActiveById(id);
            if (existingCaLamViec != null) {
                caLamViec.setMaCa(id);
                caLamViec.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                CaLamViec updatedCaLamViec = caLamViecService.save(caLamViec);
                return new ResponseEntity<>(updatedCaLamViec, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa ca làm việc", description = "Xóa mềm ca làm việc (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy ca làm việc"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCaLamViec(
            @Parameter(description = "ID của ca làm việc", required = true) @PathVariable Integer id) {
        try {
            CaLamViec caLamViec = caLamViecService.findActiveById(id);
            if (caLamViec != null) {
                caLamViecService.softDeleteById(id);
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