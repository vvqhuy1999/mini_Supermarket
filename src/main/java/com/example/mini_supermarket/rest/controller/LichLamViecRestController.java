package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.LichLamViec;
import com.example.mini_supermarket.service.LichLamViecService;
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
@RequestMapping("/api/lichlamviec")
@CrossOrigin(origins = "*")
@Tag(name = "Lịch làm việc", description = "API quản lý lịch làm việc")
public class LichLamViecRestController {

    @Autowired
    private LichLamViecService lichLamViecService;

    @Operation(summary = "Lấy tất cả lịch làm việc", description = "Trả về danh sách tất cả lịch làm việc chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = LichLamViec.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<LichLamViec>> getAllLichLamViec() {
        try {
            List<LichLamViec> lichLamViecs = lichLamViecService.findAllActive();
            return new ResponseEntity<>(lichLamViecs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy lịch làm việc theo ID", description = "Trả về thông tin lịch làm việc theo ID (chỉ lấy lịch làm việc chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy lịch làm việc", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = LichLamViec.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy lịch làm việc"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LichLamViec> getLichLamViecById(
            @Parameter(description = "ID của lịch làm việc", required = true) @PathVariable Integer id) {
        try {
            LichLamViec lichLamViec = lichLamViecService.findActiveById(id);
            if (lichLamViec != null) {
                return new ResponseEntity<>(lichLamViec, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Tạo lịch làm việc mới", description = "Tạo một lịch làm việc mới cho nhân viên")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo lịch làm việc thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = LichLamViec.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<LichLamViec> createLichLamViec(@RequestBody LichLamViec lichLamViec) {
        try {
            lichLamViec.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            LichLamViec savedLichLamViec = lichLamViecService.save(lichLamViec);
            return new ResponseEntity<>(savedLichLamViec, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật lịch làm việc", description = "Cập nhật thông tin lịch làm việc theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = LichLamViec.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy lịch làm việc"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LichLamViec> updateLichLamViec(
            @Parameter(description = "ID của lịch làm việc", required = true) @PathVariable Integer id, 
            @RequestBody LichLamViec lichLamViec) {
        try {
            LichLamViec existingLichLamViec = lichLamViecService.findActiveById(id);
            if (existingLichLamViec != null) {
                lichLamViec.setMaLich(id);
                lichLamViec.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                LichLamViec updatedLichLamViec = lichLamViecService.save(lichLamViec);
                return new ResponseEntity<>(updatedLichLamViec, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa lịch làm việc", description = "Xóa mềm lịch làm việc (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy lịch làm việc"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteLichLamViec(
            @Parameter(description = "ID của lịch làm việc", required = true) @PathVariable Integer id) {
        try {
            LichLamViec lichLamViec = lichLamViecService.findActiveById(id);
            if (lichLamViec != null) {
                lichLamViecService.softDeleteById(id);
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