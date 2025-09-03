package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.SanPham;
import com.example.mini_supermarket.dto.SanPhamOptimizedDto;
import com.example.mini_supermarket.service.SanPhamService;
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
@RequestMapping("/api/sanpham")
@CrossOrigin(origins = "*")
@Tag(name = "Sản phẩm", description = "API quản lý sản phẩm")
public class SanPhamRestController {

    @Autowired
    private SanPhamService sanPhamService;

    // === ENDPOINTS CƠ BẢN - TRẢ VỀ ENTITY ĐẦY ĐỦ ===
    
    @Operation(summary = "Lấy tất cả sản phẩm", description = "Trả về danh sách tất cả sản phẩm chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = SanPham.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<SanPham>> getAllSanPham() {
        try {
            List<SanPham> sanPhams = sanPhamService.findAllActive();
            // Populate current price for each product
            if (sanPhams != null && !sanPhams.isEmpty()) {
                for (SanPham sp : sanPhams) {
                    sp.setGiaHienTai(sanPhamService.getCurrentPrice(sp.getMaSP()));
                }
            }
            return new ResponseEntity<>(sanPhams, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy sản phẩm theo ID", description = "Trả về thông tin sản phẩm theo ID (chỉ lấy sản phẩm chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy sản phẩm", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = SanPham.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SanPham> getSanPhamById(
            @Parameter(description = "ID của sản phẩm", required = true) @PathVariable String id) {
        try {
            SanPham sanPham = sanPhamService.findActiveById(id);
            // Giá hiện tại đã được set trong service, nhưng đảm bảo nếu null thì set từ service
            if (sanPham.getGiaHienTai() == null) {
                sanPham.setGiaHienTai(sanPhamService.getCurrentPrice(id));
            }
            return new ResponseEntity<>(sanPham, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // === ENDPOINTS TỐI ƯU - SỬ DỤNG DTO VỚI @BUILDER ===
    
    @Operation(summary = "Lấy tất cả sản phẩm (tối ưu)", description = "Trả về danh sách sản phẩm với ít trường hơn (không có trangThai, ngayTao, isDeleted)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = SanPhamOptimizedDto.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/optimized")
    public ResponseEntity<List<SanPhamOptimizedDto>> getAllSanPhamOptimized() {
        try {
            List<SanPhamOptimizedDto> sanPhams = sanPhamService.findAllActiveOptimized();
            return new ResponseEntity<>(sanPhams, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "Lấy sản phẩm theo ID (tối ưu)", description = "Trả về thông tin sản phẩm với ít trường hơn (không có trangThai, ngayTao, isDeleted)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy sản phẩm", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = SanPhamOptimizedDto.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}/optimized")
    public ResponseEntity<SanPhamOptimizedDto> getSanPhamByIdOptimized(
            @Parameter(description = "ID của sản phẩm", required = true) @PathVariable String id) {
        try {
            SanPhamOptimizedDto sanPham = sanPhamService.findActiveByIdOptimized(id);
            if (sanPham != null) {
                return new ResponseEntity<>(sanPham, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // === ENDPOINTS THEO CATEGORY - SỬ DỤNG DTO TỐI ƯU ===
    
    @Operation(summary = "Lấy sản phẩm theo category (tối ưu)", description = "Trả về danh sách sản phẩm theo loại với ít trường hơn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = SanPhamOptimizedDto.class))),
            @ApiResponse(responseCode = "400", description = "Mã loại sản phẩm không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/category/{maLoaiSP}")
    public ResponseEntity<List<SanPhamOptimizedDto>> getSanPhamByCategory(
            @Parameter(description = "Mã loại sản phẩm (VD: LSP001)", required = true) 
            @PathVariable String maLoaiSP) {
        try {
            if (maLoaiSP == null || maLoaiSP.trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            List<SanPhamOptimizedDto> sanPhams = sanPhamService.findByCategoryOptimized(maLoaiSP.trim());
            return new ResponseEntity<>(sanPhams, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "Lấy sản phẩm theo category và trạng thái kinh doanh (tối ưu)", description = "Trả về danh sách sản phẩm theo loại và đang kinh doanh với ít trường hơn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = SanPhamOptimizedDto.class))),
            @ApiResponse(responseCode = "400", description = "Mã loại sản phẩm không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/category/{maLoaiSP}/active")
    public ResponseEntity<List<SanPhamOptimizedDto>> getActiveSanPhamByCategory(
            @Parameter(description = "Mã loại sản phẩm (VD: LSP001)", required = true) 
            @PathVariable String maLoaiSP) {
        try {
            if (maLoaiSP == null || maLoaiSP.trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            List<SanPhamOptimizedDto> sanPhams = sanPhamService.findByCategoryAndActiveOptimized(maLoaiSP.trim());
            return new ResponseEntity<>(sanPhams, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy sản phẩm theo category và trạng thái kinh doanh (tối ưu)", description = "Trả về danh sách sản phẩm theo category với ít trường hơn và chỉ lấy sản phẩm đang kinh doanh")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = SanPhamOptimizedDto.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/optimized/category/{maLoaiSP}/active")
    public ResponseEntity<List<SanPhamOptimizedDto>> getSanPhamByCategoryAndActiveOptimized(
            @Parameter(description = "Mã loại sản phẩm", required = true) @PathVariable String maLoaiSP) {
        try {
            List<SanPhamOptimizedDto> sanPhams = sanPhamService.findByCategoryAndActiveOptimized(maLoaiSP);
            return new ResponseEntity<>(sanPhams, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // === ENDPOINTS MỚI - VỚI SỐ LƯỢNG TỒN KHO ===
    
    @Operation(summary = "Lấy tất cả sản phẩm với số lượng tồn kho", description = "Trả về danh sách sản phẩm kèm số lượng tồn kho tổng từ tất cả các kho hoặc theo mã kho nếu truyền vào")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = SanPhamOptimizedDto.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/with-tonkho")
    public ResponseEntity<List<SanPhamOptimizedDto>> getAllSanPhamWithTonKho(
            @Parameter(description = "Mã kho cần lọc (tùy chọn)", required = false)
            @RequestParam(value = "maKho", required = false) String maKho
    ) {
        try {
            List<SanPhamOptimizedDto> sanPhams = (maKho == null || maKho.isEmpty())
                    ? sanPhamService.findAllActiveWithTonKho()
                    : sanPhamService.findAllActiveWithTonKhoByKho(maKho);
            return new ResponseEntity<>(sanPhams, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "Lấy sản phẩm theo ID với số lượng tồn kho", description = "Trả về thông tin sản phẩm kèm số lượng tồn kho tổng từ tất cả các kho hoặc theo mã kho nếu truyền vào")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = SanPhamOptimizedDto.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}/with-tonkho")
    public ResponseEntity<SanPhamOptimizedDto> getSanPhamByIdWithTonKho(
            @Parameter(description = "ID của sản phẩm", required = true) @PathVariable String id,
            @Parameter(description = "Mã kho cần lọc (tùy chọn)", required = false)
            @RequestParam(value = "maKho", required = false) String maKho
    ) {
        try {
            SanPhamOptimizedDto sanPham = (maKho == null || maKho.isEmpty())
                    ? sanPhamService.findActiveByIdWithTonKho(id)
                    : sanPhamService.findActiveByIdWithTonKhoByKho(id, maKho);
            return new ResponseEntity<>(sanPham, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // === ENDPOINTS TÌM KIẾM ===
    
    @Operation(summary = "Tìm kiếm sản phẩm theo tên và mô tả", description = "Tìm kiếm sản phẩm theo tên sản phẩm và mô tả (tìm kiếm mờ - LIKE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = SanPhamOptimizedDto.class))),
            @ApiResponse(responseCode = "400", description = "Tham số tìm kiếm không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/search")
    public ResponseEntity<List<SanPhamOptimizedDto>> searchSanPham(
            @Parameter(description = "Từ khóa tìm kiếm (tên sản phẩm hoặc mô tả)", required = false)
            @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(description = "Tìm kiếm theo tên sản phẩm", required = false)
            @RequestParam(value = "tensp", required = false) String tensp,
            @Parameter(description = "Tìm kiếm theo mô tả", required = false)
            @RequestParam(value = "mota", required = false) String mota,
            @Parameter(description = "Chỉ lấy sản phẩm đang kinh doanh", required = false)
            @RequestParam(value = "activeOnly", defaultValue = "true") boolean activeOnly
    ) {
        try {
            // Lấy tất cả sản phẩm active để filter
            List<SanPhamOptimizedDto> allSanPhams = activeOnly 
                ? sanPhamService.findAllActiveOptimized() 
                : sanPhamService.findAllActiveOptimized(); // Tạm thời dùng findAllActiveOptimized
            
            List<SanPhamOptimizedDto> filteredSanPhams = allSanPhams;
            
            // Nếu có keyword, tìm kiếm theo cả tên và mô tả
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchTerm = keyword.trim().toLowerCase();
                filteredSanPhams = allSanPhams.stream()
                    .filter(sp -> (sp.getTenSP() != null && sp.getTenSP().toLowerCase().contains(searchTerm)) ||
                                 (sp.getMoTa() != null && sp.getMoTa().toLowerCase().contains(searchTerm)))
                    .toList();
            }
            
            // Nếu có tensp, tìm kiếm theo tên sản phẩm
            if (tensp != null && !tensp.trim().isEmpty()) {
                String searchTerm = tensp.trim().toLowerCase();
                filteredSanPhams = allSanPhams.stream()
                    .filter(sp -> sp.getTenSP() != null && sp.getTenSP().toLowerCase().contains(searchTerm))
                    .toList();
            }
            
            // Nếu có mota, tìm kiếm theo mô tả
            if (mota != null && !mota.trim().isEmpty()) {
                String searchTerm = mota.trim().toLowerCase();
                filteredSanPhams = allSanPhams.stream()
                    .filter(sp -> sp.getMoTa() != null && sp.getMoTa().toLowerCase().contains(searchTerm))
                    .toList();
            }
            
            return new ResponseEntity<>(filteredSanPhams, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "Tìm kiếm sản phẩm nâng cao", description = "Tìm kiếm sản phẩm với nhiều tiêu chí kết hợp")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = SanPhamOptimizedDto.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/search/advanced")
    public ResponseEntity<List<SanPhamOptimizedDto>> advancedSearchSanPham(
            @Parameter(description = "Từ khóa tìm kiếm", required = false)
            @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(description = "Mã loại sản phẩm", required = false)
            @RequestParam(value = "maLoaiSP", required = false) String maLoaiSP,
            @Parameter(description = "Chỉ lấy sản phẩm đang kinh doanh", required = false)
            @RequestParam(value = "activeOnly", defaultValue = "true") boolean activeOnly
    ) {
        try {
            List<SanPhamOptimizedDto> allSanPhams = activeOnly 
                ? sanPhamService.findAllActiveOptimized() 
                : sanPhamService.findAllActiveOptimized(); // Tạm thời dùng findAllActiveOptimized
            
            List<SanPhamOptimizedDto> filteredSanPhams = allSanPhams;
            
            // Filter theo từ khóa
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchTerm = keyword.trim().toLowerCase();
                filteredSanPhams = filteredSanPhams.stream()
                    .filter(sp -> (sp.getTenSP() != null && sp.getTenSP().toLowerCase().contains(searchTerm)) ||
                                 (sp.getMoTa() != null && sp.getMoTa().toLowerCase().contains(searchTerm)))
                    .toList();
            }
            
            // Filter theo loại sản phẩm
            if (maLoaiSP != null && !maLoaiSP.trim().isEmpty()) {
                String searchTerm = maLoaiSP.trim();
                filteredSanPhams = filteredSanPhams.stream()
                    .filter(sp -> sp.getLoaiSanPham() != null && 
                                sp.getLoaiSanPham().getMaLoaiSP() != null &&
                                sp.getLoaiSanPham().getMaLoaiSP().equals(searchTerm))
                    .toList();
            }
            
            return new ResponseEntity<>(filteredSanPhams, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // === ENDPOINTS QUẢN LÝ - FULL CRUD ===

    // Thêm sản phẩm mới
    @PostMapping
    public ResponseEntity<SanPham> createSanPham(@RequestBody SanPham sanPham) {
        try {
            SanPham savedSanPham = sanPhamService.save(sanPham);
            return new ResponseEntity<>(savedSanPham, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<SanPham> updateSanPham(@PathVariable String id, @RequestBody SanPham sanPham) {
        try {
            SanPham existingSanPham = sanPhamService.findActiveById(id);
            if (existingSanPham != null) {
                sanPham.setMaSP(id); // Đảm bảo ID không thay đổi
                sanPham.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                SanPham updatedSanPham = sanPhamService.update(sanPham);
                return new ResponseEntity<>(updatedSanPham, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Xóa sản phẩm (soft delete - chỉ đánh dấu isDeleted = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSanPham(@PathVariable String id) {
        try {
            SanPham existingSanPham = sanPhamService.findActiveById(id);
            if (existingSanPham != null) {
                sanPhamService.softDeleteById(id);
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