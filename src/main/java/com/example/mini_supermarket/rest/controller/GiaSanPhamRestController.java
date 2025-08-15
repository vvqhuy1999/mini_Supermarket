package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.GiaSanPham;
import com.example.mini_supermarket.entity.NhanVien;
import com.example.mini_supermarket.service.GiaSanPhamService;
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
import java.util.Random;

@RestController
@RequestMapping("/api/giasanpham")
@CrossOrigin(origins = "*")
@Tag(name = "Giá sản phẩm", description = "API quản lý giá sản phẩm")
public class GiaSanPhamRestController {

    @Autowired
    private GiaSanPhamService giaSanPhamService;

    @Operation(summary = "Lấy tất cả giá sản phẩm", description = "Trả về danh sách tất cả giá sản phẩm chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = GiaSanPham.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<GiaSanPham>> getAllGiaSanPham() {
        try {
            List<GiaSanPham> giaSanPhams = giaSanPhamService.findAllActive();
            return new ResponseEntity<>(giaSanPhams, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy giá sản phẩm theo ID", description = "Trả về thông tin giá sản phẩm theo ID (chỉ lấy giá sản phẩm chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy giá sản phẩm", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = GiaSanPham.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giá sản phẩm"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GiaSanPham> getGiaSanPhamById(
            @Parameter(description = "ID của giá sản phẩm", required = true) @PathVariable Integer id) {
        try {
            GiaSanPham giaSanPham = giaSanPhamService.findActiveById(id);
            if (giaSanPham != null) {
                return new ResponseEntity<>(giaSanPham, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm giá sản phẩm mới", description = "Tạo một mức giá mới cho sản phẩm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo giá sản phẩm thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = GiaSanPham.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<GiaSanPham> createGiaSanPham(@RequestBody GiaSanPham giaSanPham) {
        try {
            // Thêm dòng này để tạo mã ngẫu nhiên 5 chữ số
            int randomMa = new Random().nextInt(90000) + 10000;

            giaSanPham.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            GiaSanPham savedGiaSanPham = giaSanPhamService.save(giaSanPham);
            return new ResponseEntity<>(savedGiaSanPham, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GiaSanPham> updateGiaSanPham(
            @PathVariable Integer id,
            @RequestBody GiaSanPham giaSanPham) {
        try {
            GiaSanPham existing = giaSanPhamService.findActiveById(id);
            if (existing == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            // Chỉ cập nhật các trường cần thiết
            if (giaSanPham.getGia() != null) existing.setGia(giaSanPham.getGia());
            if (giaSanPham.getNgayBatDau() != null) existing.setNgayBatDau(giaSanPham.getNgayBatDau());
            existing.setNgayKetThuc(giaSanPham.getNgayKetThuc()); // có thể null
            existing.setLyDoThayDoi(giaSanPham.getLyDoThayDoi());

//            // Nếu muốn cập nhật người thay đổi, cần kiểm tra mã nhân viên có tồn tại
//            if (giaSanPham.getNguoiThayDoi() != null && giaSanPham.getNguoiThayDoi().getMaNV() != null) {
////                NhanVien nv = nhanVienService.findById(giaSanPham.getNguoiThayDoi().getMaNV());
//                if (nv != null) existing.setNguoiThayDoi(nv);
//            }

            existing.setIsDeleted(false); // giữ trạng thái không bị xóa

            GiaSanPham updated = giaSanPhamService.save(existing);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteGiaSanPham(
            @Parameter(description = "ID của giá sản phẩm", required = true) @PathVariable Integer id) {
        try {
            GiaSanPham giaSanPham = giaSanPhamService.findActiveById(id);
            if (giaSanPham != null) {
                giaSanPhamService.softDeleteById(id);
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