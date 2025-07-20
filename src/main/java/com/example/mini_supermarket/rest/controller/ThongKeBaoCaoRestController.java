package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.ThongKeBaoCao;
import com.example.mini_supermarket.service.ThongKeBaoCaoService;
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
@RequestMapping("/api/thongkebaocao")
@CrossOrigin(origins = "*")
@Tag(name = "Thống kê báo cáo", description = "API quản lý thống kê và báo cáo")
public class ThongKeBaoCaoRestController {

    @Autowired
    private ThongKeBaoCaoService thongKeBaoCaoService;

    @Operation(summary = "Lấy tất cả thống kê báo cáo", description = "Trả về danh sách tất cả thống kê báo cáo chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ThongKeBaoCao.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<ThongKeBaoCao>> getAllThongKeBaoCao() {
        try {
            List<ThongKeBaoCao> thongKeBaoCaos = thongKeBaoCaoService.findAllActive();
            return new ResponseEntity<>(thongKeBaoCaos, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy thống kê báo cáo theo ID", description = "Trả về thông tin thống kê báo cáo theo ID (chỉ lấy thống kê báo cáo chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy thống kê báo cáo", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ThongKeBaoCao.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thống kê báo cáo"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ThongKeBaoCao> getThongKeBaoCaoById(
            @Parameter(description = "ID của thống kê báo cáo", required = true) @PathVariable Integer id) {
        try {
            ThongKeBaoCao thongKeBaoCao = thongKeBaoCaoService.findActiveById(id);
            if (thongKeBaoCao != null) {
                return new ResponseEntity<>(thongKeBaoCao, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Tạo thống kê báo cáo mới", description = "Tạo một báo cáo thống kê mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo thống kê báo cáo thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ThongKeBaoCao.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<ThongKeBaoCao> createThongKeBaoCao(@RequestBody ThongKeBaoCao thongKeBaoCao) {
        try {
            thongKeBaoCao.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            ThongKeBaoCao savedThongKeBaoCao = thongKeBaoCaoService.save(thongKeBaoCao);
            return new ResponseEntity<>(savedThongKeBaoCao, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật thống kê báo cáo", description = "Cập nhật thông tin thống kê báo cáo theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ThongKeBaoCao.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thống kê báo cáo"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ThongKeBaoCao> updateThongKeBaoCao(
            @Parameter(description = "ID của thống kê báo cáo", required = true) @PathVariable Integer id, 
            @RequestBody ThongKeBaoCao thongKeBaoCao) {
        try {
            ThongKeBaoCao existingThongKeBaoCao = thongKeBaoCaoService.findActiveById(id);
            if (existingThongKeBaoCao != null) {
                thongKeBaoCao.setMaBaoCao(id);
                thongKeBaoCao.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                ThongKeBaoCao updatedThongKeBaoCao = thongKeBaoCaoService.save(thongKeBaoCao);
                return new ResponseEntity<>(updatedThongKeBaoCao, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa thống kê báo cáo", description = "Xóa mềm thống kê báo cáo (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thống kê báo cáo"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteThongKeBaoCao(
            @Parameter(description = "ID của thống kê báo cáo", required = true) @PathVariable Integer id) {
        try {
            ThongKeBaoCao thongKeBaoCao = thongKeBaoCaoService.findActiveById(id);
            if (thongKeBaoCao != null) {
                thongKeBaoCaoService.softDeleteById(id);
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