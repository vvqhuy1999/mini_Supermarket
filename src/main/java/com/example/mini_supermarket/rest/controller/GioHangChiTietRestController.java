package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.GioHangChiTiet;
import com.example.mini_supermarket.service.GioHangChiTietService;
import com.example.mini_supermarket.service.SanPhamService;
import com.example.mini_supermarket.service.KhachHangService;
import com.example.mini_supermarket.service.UserService;
import com.example.mini_supermarket.dto.GioHangWithItemsDto;
import com.example.mini_supermarket.dto.CartSyncRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/giohang")
@CrossOrigin(origins = "*")
@Tag(name = "Giỏ hàng", description = "API quản lý giỏ hàng (merged table)")
public class GioHangChiTietRestController {

    @Autowired
    private GioHangChiTietService gioHangChiTietService;
    
    @Autowired
    private SanPhamService sanPhamService;
    
    @Autowired
    private KhachHangService khachHangService;
    
    @Autowired
    private UserService userService;

    @Value("${oauth2.frontend.base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    // =================== CART MANAGEMENT ===================

    @Operation(summary = "Lấy giỏ hàng theo khách hàng", description = "Trả về danh sách items trong giỏ hàng của khách hàng")
    @GetMapping("/by-khachhang/{maKH}")
    public ResponseEntity<List<GioHangChiTiet>> getCartByCustomer(@PathVariable String maKH) {
        List<GioHangChiTiet> items = gioHangChiTietService.findActiveCartItemsByCustomer(maKH);
        System.out.println("[CART][BY_KH] maKH=" + maKH + " itemsCount=" + items.size());
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Lấy giỏ hàng với thông tin chi tiết", description = "Trả về giỏ hàng với thông tin tổng hợp")
    @GetMapping("/by-khachhang/{maKH}/with-items")
    public ResponseEntity<GioHangWithItemsDto> getCartWithItems(@PathVariable String maKH) {
        List<GioHangChiTiet> items = gioHangChiTietService.findActiveCartItemsByCustomer(maKH);
        System.out.println("[CART][WITH_ITEMS] maKH=" + maKH + " itemsCount=" + items.size());
        
        if (items.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Tính tổng tiền
        BigDecimal tongTien = gioHangChiTietService.calculateTotalByCustomerAndStatus(maKH, 0);
        
        // Lấy thông tin từ item đầu tiên (vì tất cả cùng khách hàng)
        GioHangChiTiet firstItem = items.get(0);
        
        GioHangWithItemsDto dto = GioHangWithItemsDto.builder()
                .maKH(maKH)
                .trangThai(0) // Shopping
                .ngayTao(firstItem.getNgayThem())
                .ngayCapNhat(firstItem.getNgayCapNhat())
                .items(items)
                .tongTien(tongTien)
                .build();
        
        return ResponseEntity.ok(dto);
    }

    // =================== ITEM MANAGEMENT ===================

    @Operation(summary = "Thêm sản phẩm vào giỏ hàng", description = "Thêm hoặc cập nhật số lượng sản phẩm trong giỏ hàng")
    @PostMapping("/items")
    @SuppressWarnings("unchecked")
    public ResponseEntity<GioHangChiTiet> addCartItem(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("[CART][ADD_ITEM] START - payload: " + payload);
            
            if (payload == null || payload.isEmpty()) {
                System.out.println("[CART][ADD_ITEM] ERROR - null or empty payload");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            // Extract parameters
            String maKH = null;
            String maSP = null;
            Integer soLuong = null;
            BigDecimal donGiaHienTai = null;

            // Auto-detect customer from JWT
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                try {
                    var nguoiDungOpt = userService.findByEmail(auth.getName());
                    if (nguoiDungOpt.isPresent()) {
                        var khachHang = khachHangService.findByMaNguoiDung(nguoiDungOpt.get().getMaNguoiDung());
                        if (khachHang != null) {
                            maKH = khachHang.getMaKH();
                            System.out.println("[CART][ADD_ITEM] auto-detected maKH=" + maKH);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[CART][ADD_ITEM] Error finding customer: " + e.getMessage());
                }
            }

            // Fallback to payload
            if (maKH == null && payload.containsKey("maKH")) {
                maKH = (String) payload.get("maKH");
            }

            // Handle nested or flat format for maSP
            if (payload.containsKey("sanPham") && payload.get("sanPham") instanceof Map) {
                Map<String, Object> sanPhamMap = (Map<String, Object>) payload.get("sanPham");
                maSP = (String) sanPhamMap.get("maSP");
            } else if (payload.containsKey("maSP")) {
                maSP = (String) payload.get("maSP");
            }

            if (payload.containsKey("soLuong")) {
                soLuong = (Integer) payload.get("soLuong");
            }

            if (payload.containsKey("donGiaHienTai")) {
                try {
                    donGiaHienTai = new BigDecimal(payload.get("donGiaHienTai").toString());
                } catch (Exception e) {
                    System.out.println("[CART][ADD_ITEM] WARNING - invalid donGiaHienTai");
                }
            }

            // Validation
            if (maKH == null || maKH.trim().isEmpty()) {
                System.out.println("[CART][ADD_ITEM] ERROR - missing maKH");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            if (maSP == null || maSP.trim().isEmpty()) {
                System.out.println("[CART][ADD_ITEM] ERROR - missing maSP");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            if (soLuong == null || soLuong <= 0) {
                System.out.println("[CART][ADD_ITEM] ERROR - invalid soLuong: " + soLuong);
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            // Get current price if not provided
            if (donGiaHienTai == null) {
                donGiaHienTai = sanPhamService.getCurrentPrice(maSP);
                if (donGiaHienTai == null) {
                    donGiaHienTai = BigDecimal.ZERO;
                }
            }

            System.out.println("[CART][ADD_ITEM] validated - maKH=" + maKH + 
                             " maSP=" + maSP + 
                             " soLuong=" + soLuong + 
                             " donGia=" + donGiaHienTai);

            GioHangChiTiet savedItem = gioHangChiTietService.addOrUpdateCartItem(maKH, maSP, soLuong, donGiaHienTai, null);
            System.out.println("[CART][ADD_ITEM] SUCCESS - maGHCT=" + savedItem.getMaGHCT());
            
            return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("[CART][ADD_ITEM] EXCEPTION - " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật số lượng item trong giỏ hàng")
    @PutMapping("/items/{itemId}/quantity")
    public ResponseEntity<GioHangChiTiet> updateItemQuantity(@PathVariable Integer itemId, @RequestParam("value") Integer qty) {
        try {
            GioHangChiTiet existing = gioHangChiTietService.findActiveById(itemId);
            if (existing == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            if (qty <= 0) {
                // Xóa item nếu số lượng <= 0
                gioHangChiTietService.softDeleteById(itemId); // now hard delete
                return ResponseEntity.ok().build();
            }
            
            existing.setSoLuong(qty);
            GioHangChiTiet saved = gioHangChiTietService.save(existing);
            System.out.println("[CART][UPDATE_QTY] itemId=" + itemId + " newQty=" + qty);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa item khỏi giỏ hàng")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Integer itemId) {
        GioHangChiTiet item = gioHangChiTietService.findActiveById(itemId);
        if (item != null) {
            System.out.println("[CART][DELETE_ITEM] delete itemId=" + itemId);
            gioHangChiTietService.softDeleteById(itemId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Xóa tất cả items trong giỏ hàng của khách hàng")
    @DeleteMapping("/by-khachhang/{maKH}/items")
    public ResponseEntity<Void> clearCartItems(@PathVariable String maKH) {
        System.out.println("[CART][CLEAR_ITEMS] maKH=" + maKH);
        gioHangChiTietService.clearCartByCustomerAndStatus(maKH, 0); // 0 = Shopping
        return ResponseEntity.ok().build();
    }

    // =================== CART STATUS MANAGEMENT ===================

    @Operation(summary = "Cập nhật trạng thái giỏ hàng")
    @PutMapping("/by-khachhang/{maKH}/status")
    public ResponseEntity<?> updateCartStatus(@PathVariable String maKH, @RequestParam("value") Integer status) {
        System.out.println("[CART][STATUS] maKH=" + maKH + " -> status=" + status);
        gioHangChiTietService.updateCartItemsStatus(maKH, 0, status); // From Shopping to new status
        
        if (status != null && status == 0) {
            System.out.println("[CART][STATUS] redirect frontend base=" + frontendBaseUrl + " (status==0)");
            return ResponseEntity.status(302).location(URI.create(frontendBaseUrl)).build();
        }
        
        return ResponseEntity.ok(Map.of("message", "Cart status updated successfully"));
    }

    // =================== SYNC OPERATIONS ===================

    @Operation(summary = "Đồng bộ giỏ hàng từ localStorage")
    @PostMapping("/sync")
    public ResponseEntity<?> syncCartFromLocal(@RequestBody CartSyncRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không có dữ liệu giỏ hàng để đồng bộ"));
        }

        // Auto-detect customer from JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = null;
        String maKH = null;

        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            currentUserEmail = authentication.getName();
            System.out.println("[CART][SYNC] authenticated user email=" + currentUserEmail);

            try {
                var nguoiDungOpt = userService.findByEmail(currentUserEmail);
                if (nguoiDungOpt.isPresent()) {
                    var nguoiDung = nguoiDungOpt.get();
                    var khachHang = khachHangService.findByMaNguoiDung(nguoiDung.getMaNguoiDung());
                    if (khachHang != null) {
                        maKH = khachHang.getMaKH();
                        System.out.println("[CART][SYNC] found customer maKH=" + maKH + " for user=" + currentUserEmail);
                    }
                }
            } catch (Exception e) {
                System.out.println("[CART][SYNC] ERROR finding customer for user=" + currentUserEmail + " error=" + e.getMessage());
            }
        }

        // Fallback to request body
        if (maKH == null) {
            maKH = request.getMaKH();
            System.out.println("[CART][SYNC] using maKH from request body=" + maKH);
        }

        // Security check
        if (request.getMaKH() != null && !request.getMaKH().equals(maKH)) {
            System.out.println("[CART][SYNC] SECURITY VIOLATION: user=" + currentUserEmail + 
                             " trying to sync cart for different customer=" + request.getMaKH());
            return ResponseEntity.status(403).body(Map.of("message", "Không được phép truy cập giỏ hàng của khách hàng khác"));
        }

        if (maKH == null) {
            System.out.println("[CART][SYNC] ERROR: no customer ID available");
            return ResponseEntity.badRequest().body(Map.of("message", "Không thể xác định khách hàng để đồng bộ giỏ hàng"));
        }

        System.out.println("[CART][SYNC] START - authenticatedUser=" + currentUserEmail + 
                          " maKH=" + maKH + " itemsCount=" + request.getItems().size());

        // Get existing cart items
        var existingItems = gioHangChiTietService.findActiveCartItemsByCustomer(maKH);
        System.out.println("[CART][SYNC] existing items in cart=" + existingItems.size());

        // Merge each item from localStorage
        for (var it : request.getItems()) {
            if (it.getMaSP() == null || it.getSoLuong() == null || it.getSoLuong() <= 0) continue;

            // Find existing item by product ID
            var existingItem = existingItems.stream()
                    .filter(item -> item.getSanPham() != null && it.getMaSP().equals(item.getSanPham().getMaSP()))
                    .findFirst();

            if (existingItem.isPresent()) {
                // MERGE quantities
                var item = existingItem.get();
                int oldQty = item.getSoLuong();
                int newQty = oldQty + it.getSoLuong();
                item.setSoLuong(newQty);
                gioHangChiTietService.save(item);
                System.out.println("[CART][SYNC] MERGE item maSP=" + it.getMaSP() + 
                                 " localStorage=" + it.getSoLuong() + 
                                 " + existing=" + oldQty + 
                                 " = total=" + newQty);
            } else {
                // CREATE new item
                try {
                    var sanPham = sanPhamService.findActiveById(it.getMaSP());
                    if (sanPham == null) {
                        System.out.println("[CART][SYNC] SKIP item maSP=" + it.getMaSP() + " - product not found or inactive");
                        continue;
                    }

                    var currentPrice = sanPhamService.getCurrentPrice(it.getMaSP());
                    if (currentPrice == null) {
                        System.out.println("[CART][SYNC] WARNING item maSP=" + it.getMaSP() + " - no price found, using 0");
                        currentPrice = BigDecimal.ZERO;
                    }

                    gioHangChiTietService.addOrUpdateCartItem(maKH, it.getMaSP(), it.getSoLuong(), currentPrice, null);
                    System.out.println("[CART][SYNC] CREATE new item maSP=" + it.getMaSP() + 
                                     " soLuong=" + it.getSoLuong() + 
                                     " donGia=" + currentPrice);
                } catch (Exception e) {
                    System.out.println("[CART][SYNC] ERROR creating item maSP=" + it.getMaSP() + " error=" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        System.out.println("[CART][SYNC] COMPLETE - returning cart with items");
        // Return synchronized cart
        return getCartWithItems(maKH);
    }

    // =================== DEBUG & UTILITY ===================

    @Operation(summary = "Lấy thông tin user hiện tại từ JWT")
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            return ResponseEntity.ok(Map.of(
                "authenticated", false,
                "message", "Chưa đăng nhập"
            ));
        }

        String currentUserEmail = authentication.getName();
        String maKH = null;
        String maNguoiDung = null;

        try {
            var nguoiDungOpt = userService.findByEmail(currentUserEmail);
            if (nguoiDungOpt.isPresent()) {
                var nguoiDung = nguoiDungOpt.get();
                maNguoiDung = nguoiDung.getMaNguoiDung();

                var khachHang = khachHangService.findByMaNguoiDung(maNguoiDung);
                if (khachHang != null) {
                    maKH = khachHang.getMaKH();
                }
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] Error finding user info: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of(
            "authenticated", true,
            "email", currentUserEmail,
            "maNguoiDung", maNguoiDung != null ? maNguoiDung : "null",
            "maKH", maKH != null ? maKH : "null",
            "authorities", authentication.getAuthorities().toString()
        ));
    }
}
