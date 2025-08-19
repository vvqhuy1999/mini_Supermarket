package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.ChiTietGioHang;
import com.example.mini_supermarket.entity.GioHang;
import com.example.mini_supermarket.entity.SanPham;
import com.example.mini_supermarket.service.ChiTietGioHangService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chitietgiohang")
@CrossOrigin(origins = "*")
@Tag(name = "Chi tiết giỏ hàng", description = "API quản lý chi tiết giỏ hàng")
public class ChiTietGioHangRestController {

    @Autowired
    private ChiTietGioHangService chiTietGioHangService;

    @Operation(summary = "Lấy tất cả chi tiết giỏ hàng", description = "Trả về danh sách tất cả chi tiết giỏ hàng chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietGioHang.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<ChiTietGioHang>> getAllChiTietGioHang() {
        try {
            List<ChiTietGioHang> chiTietGioHangs = chiTietGioHangService.findAllActive();
            return new ResponseEntity<>(chiTietGioHangs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/chitietgiohang/by-giohang/{maGH}
    @GetMapping("/by-giohang/{maGH}")
    public ResponseEntity<List<ChiTietGioHang>> getByGioHang(@PathVariable Integer maGH) {
        try {
            List<ChiTietGioHang> items = chiTietGioHangService.findByGioHangIdActive(maGH);
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy chi tiết giỏ hàng theo ID", description = "Trả về thông tin chi tiết giỏ hàng theo ID (chỉ lấy chi tiết giỏ hàng chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy chi tiết giỏ hàng", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietGioHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết giỏ hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ChiTietGioHang> getChiTietGioHangById(
            @Parameter(description = "ID của chi tiết giỏ hàng", required = true) @PathVariable Integer id) {
        try {
            ChiTietGioHang chiTietGioHang = chiTietGioHangService.findActiveById(id);
            if (chiTietGioHang != null) {
                return new ResponseEntity<>(chiTietGioHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Thêm chi tiết giỏ hàng mới", description = "Thêm sản phẩm vào giỏ hàng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thêm vào giỏ hàng thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietGioHang.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    @SuppressWarnings("unchecked")
    public ResponseEntity<ChiTietGioHang> createChiTietGioHang(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("[CHITIET][CREATE] START - payload: " + payload);
            
            if (payload == null || payload.isEmpty()) {
                System.out.println("[CHITIET][CREATE] ERROR - null or empty payload");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            
            ChiTietGioHang chiTietGioHang = new ChiTietGioHang();
            Integer maGH = null;
            String maSP = null;
            
            // Handle nested format: { gioHang: { maGH }, sanPham: { maSP }, ... }
            if (payload.containsKey("gioHang") && payload.containsKey("sanPham")) {
                System.out.println("[CHITIET][CREATE] detected NESTED format");
                
                Map<String, Object> gioHangMap = (Map<String, Object>) payload.get("gioHang");
                Map<String, Object> sanPhamMap = (Map<String, Object>) payload.get("sanPham");
                
                if (gioHangMap != null && gioHangMap.containsKey("maGH")) {
                    maGH = (Integer) gioHangMap.get("maGH");
                }
                if (sanPhamMap != null && sanPhamMap.containsKey("maSP")) {
                    maSP = (String) sanPhamMap.get("maSP");
                }
            }
            // Handle flat format: { maGH, maSP, ... }
            else if (payload.containsKey("maGH") && payload.containsKey("maSP")) {
                System.out.println("[CHITIET][CREATE] detected FLAT format");
                
                maGH = (Integer) payload.get("maGH");
                maSP = (String) payload.get("maSP");
            } else {
                System.out.println("[CHITIET][CREATE] ERROR - invalid payload structure. Expected nested {gioHang:{maGH}, sanPham:{maSP}} or flat {maGH, maSP}");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            
            // Tạo final variables cho lambda expression
            final Integer finalMaGH = maGH;
            final String finalMaSP = maSP;
            
            // Validation
            if (maGH == null) {
                System.out.println("[CHITIET][CREATE] ERROR - missing maGH");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            if (maSP == null || maSP.trim().isEmpty()) {
                System.out.println("[CHITIET][CREATE] ERROR - missing maSP");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            
            Object soLuongObj = payload.get("soLuong");
            if (soLuongObj == null) {
                System.out.println("[CHITIET][CREATE] ERROR - missing soLuong");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            
            Integer soLuong = (Integer) soLuongObj;
            if (soLuong <= 0) {
                System.out.println("[CHITIET][CREATE] ERROR - invalid soLuong: " + soLuong);
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            
            // Build entity objects
            GioHang gioHang = new GioHang();
            gioHang.setMaGH(maGH);
            chiTietGioHang.setGioHang(gioHang);
            
            SanPham sanPham = new SanPham();
            sanPham.setMaSP(maSP);
            chiTietGioHang.setSanPham(sanPham);
            
            chiTietGioHang.setSoLuong(soLuong);
            
            // Handle donGiaHienTai (optional)
            if (payload.containsKey("donGiaHienTai") && payload.get("donGiaHienTai") != null) {
                try {
                    BigDecimal donGia = new BigDecimal(payload.get("donGiaHienTai").toString());
                    chiTietGioHang.setDonGiaHienTai(donGia);
                } catch (Exception e) {
                    System.out.println("[CHITIET][CREATE] WARNING - invalid donGiaHienTai, setting to 0");
                    chiTietGioHang.setDonGiaHienTai(BigDecimal.ZERO);
                }
            } else {
                chiTietGioHang.setDonGiaHienTai(BigDecimal.ZERO);
            }
            
            System.out.println("[CHITIET][CREATE] validated - maGH=" + maGH + 
                             " maSP=" + maSP + 
                             " soLuong=" + soLuong + 
                             " donGia=" + chiTietGioHang.getDonGiaHienTai());
            
            // KIỂM TRA sản phẩm đã tồn tại trong giỏ chưa (tránh duplicate key)
            List<ChiTietGioHang> existingItems = chiTietGioHangService.findByGioHangIdActive(finalMaGH);
            ChiTietGioHang existingItem = existingItems.stream()
                    .filter(item -> item.getSanPham() != null && finalMaSP.equals(item.getSanPham().getMaSP()))
                    .findFirst()
                    .orElse(null);
            
            ChiTietGioHang savedChiTietGioHang;
            
            if (existingItem != null) {
                // SẢN PHẨM ĐÃ TỒN TẠI → UPDATE số lượng (cộng dồn)
                int oldQty = existingItem.getSoLuong();
                int newQty = oldQty + soLuong;
                existingItem.setSoLuong(newQty);
                
                // Cập nhật giá nếu có
                if (chiTietGioHang.getDonGiaHienTai().compareTo(BigDecimal.ZERO) > 0) {
                    existingItem.setDonGiaHienTai(chiTietGioHang.getDonGiaHienTai());
                }
                
                savedChiTietGioHang = chiTietGioHangService.save(existingItem);
                System.out.println("[CHITIET][CREATE] UPDATED existing item - maCTGH=" + savedChiTietGioHang.getMaCTGH() + 
                                 " oldQty=" + oldQty + " + addQty=" + soLuong + " = newQty=" + newQty);
            } else {
                // SẢN PHẨM CHƯA TỒN TẠI → CREATE mới
                chiTietGioHang.setIsDeleted(false);
                savedChiTietGioHang = chiTietGioHangService.save(chiTietGioHang);
                System.out.println("[CHITIET][CREATE] CREATED new item - maCTGH=" + savedChiTietGioHang.getMaCTGH());
            }
            
            return new ResponseEntity<>(savedChiTietGioHang, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("[CHITIET][CREATE] EXCEPTION - " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật chi tiết giỏ hàng", description = "Cập nhật số lượng sản phẩm trong giỏ hàng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ChiTietGioHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết giỏ hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ChiTietGioHang> updateChiTietGioHang(
            @Parameter(description = "ID của chi tiết giỏ hàng", required = true) @PathVariable Integer id, 
            @RequestBody ChiTietGioHang chiTietGioHang) {
        try {
            ChiTietGioHang existingChiTietGioHang = chiTietGioHangService.findActiveById(id);
            if (existingChiTietGioHang != null) {
                chiTietGioHang.setMaCTGH(id);
                chiTietGioHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                ChiTietGioHang updatedChiTietGioHang = chiTietGioHangService.save(chiTietGioHang);
                return new ResponseEntity<>(updatedChiTietGioHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT /api/chitietgiohang/{maCTGH}/quantity?value=3
    @PutMapping("/{maCTGH}/quantity")
    public ResponseEntity<ChiTietGioHang> updateQuantity(@PathVariable Integer maCTGH, @RequestParam("value") Integer qty) {
        try {
            ChiTietGioHang existing = chiTietGioHangService.findActiveById(maCTGH);
            if (existing == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            existing.setSoLuong(qty);
            ChiTietGioHang saved = chiTietGioHangService.save(existing);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa chi tiết giỏ hàng", description = "Xóa sản phẩm khỏi giỏ hàng (xóa mềm)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chi tiết giỏ hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteChiTietGioHang(
            @Parameter(description = "ID của chi tiết giỏ hàng", required = true) @PathVariable Integer id) {
        try {
            ChiTietGioHang chiTietGioHang = chiTietGioHangService.findActiveById(id);
            if (chiTietGioHang != null) {
                chiTietGioHangService.softDeleteById(id);
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