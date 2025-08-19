package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.GioHang;
import com.example.mini_supermarket.service.GioHangService;
import com.example.mini_supermarket.service.ChiTietGioHangService;
import com.example.mini_supermarket.service.SanPhamService;
import com.example.mini_supermarket.service.KhachHangService;
import com.example.mini_supermarket.service.UserService;
import com.example.mini_supermarket.dto.GioHangWithItemsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/giohang")
public class GioHangRestController {

    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private ChiTietGioHangService chiTietGioHangService;
    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private KhachHangService khachHangService;
    @Autowired
    private UserService userService;

    @Value("${oauth2.frontend.base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    @GetMapping
    public ResponseEntity<List<GioHang>> getAllGioHang() {
        List<GioHang> gioHangs = gioHangService.findAllActive();
        System.out.println("[GIOHANG][LIST] count=" + gioHangs.size() +
                " ids=" + gioHangs.stream().map(GioHang::getMaGH).toList());
        return ResponseEntity.ok(gioHangs);
    }

    // GET /api/giohang/by-khachhang/{maKH}
    @GetMapping("/by-khachhang/{maKH}")
    public ResponseEntity<List<GioHang>> getGioHangByKhachHang(@PathVariable String maKH) {
        List<GioHang> gioHangs = gioHangService.findByMaKhachHang(maKH);
        System.out.println("[GIOHANG][BY_KH] maKH=" + maKH + " count=" + gioHangs.size() +
                " ids=" + gioHangs.stream().map(GioHang::getMaGH).toList());
        return ResponseEntity.ok(gioHangs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GioHang> getGioHangById(@PathVariable Integer id) {
        GioHang gioHang = gioHangService.findActiveById(id);
        System.out.println("[GIOHANG][GET] maGH=" + id + " found=" + (gioHang != null));
        if (gioHang != null) {
            return ResponseEntity.ok(gioHang);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<GioHang> createGioHang(@RequestBody(required = false) GioHang gioHang) {
        System.out.println("[GIOHANG][CREATE] START - payload: " + gioHang);
        
        // Auto-detect customer from JWT (như frontend mong đợi)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String maKH = null;
        
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            try {
                var nguoiDungOpt = userService.findByEmail(auth.getName());
                if (nguoiDungOpt.isPresent()) {
                    var khachHang = khachHangService.findByMaNguoiDung(nguoiDungOpt.get().getMaNguoiDung());
                    if (khachHang != null) {
                        maKH = khachHang.getMaKH();
                        System.out.println("[GIOHANG][CREATE] auto-detected maKH=" + maKH);
                    }
                }
            } catch (Exception e) {
                System.out.println("[GIOHANG][CREATE] Error finding customer: " + e.getMessage());
            }
        }
        
        // Tạo giỏ hàng mới nếu payload rỗng
        if (gioHang == null) {
            gioHang = new GioHang();
            System.out.println("[GIOHANG][CREATE] created empty cart object");
        }
        
        // Set defaults
        if (gioHang.getTrangThai() == null) {
            gioHang.setTrangThai(0);
        }
        if (gioHang.getGhiChu() == null || gioHang.getGhiChu().trim().isEmpty()) {
            gioHang.setGhiChu("Đang chọn hàng");
        }
        
        // Auto-assign customer from JWT
        if (maKH != null && gioHang.getKhachHang() == null) {
            try {
                var khachHang = khachHangService.findById(maKH);
                gioHang.setKhachHang(khachHang);
                System.out.println("[GIOHANG][CREATE] auto-assigned customer");
            } catch (Exception e) {
                System.out.println("[GIOHANG][CREATE] ERROR: customer not found maKH=" + maKH);
                return ResponseEntity.badRequest().body(null);
            }
        }
        
        gioHang.setIsDeleted(false);
        GioHang savedGioHang = gioHangService.save(gioHang);
        System.out.println("[GIOHANG][CREATE] SUCCESS - maGH=" + savedGioHang.getMaGH() + 
                          " maKH=" + (savedGioHang.getKhachHang() != null ? savedGioHang.getKhachHang().getMaKH() : "null"));
        return ResponseEntity.ok(savedGioHang);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GioHang> updateGioHang(@PathVariable Integer id, @RequestBody GioHang gioHang) {
        GioHang existingGioHang = gioHangService.findActiveById(id);
        if (existingGioHang != null) {
            System.out.println("[GIOHANG][UPDATE] maGH=" + id +
                    " payloadTrangThai=" + gioHang.getTrangThai() +
                    " payloadGhiChu=" + gioHang.getGhiChu());
            gioHang.setMaGH(id);
            gioHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
            GioHang updatedGioHang = gioHangService.save(gioHang);
            return ResponseEntity.ok(updatedGioHang);
        }
        return ResponseEntity.notFound().build();
    }

    // PUT /api/giohang/{maGH}/status?value=1
    @PutMapping("/{maGH}/status")
    public ResponseEntity<GioHang> updateGioHangStatus(@PathVariable Integer maGH, @RequestParam("value") Integer status) {
        GioHang existing = gioHangService.findActiveById(maGH);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        System.out.println("[GIOHANG][STATUS] maGH=" + maGH + " -> status=" + status);
        existing.setTrangThai(status);
        existing.setNgayCapNhat(new java.sql.Timestamp(System.currentTimeMillis()));
        GioHang saved = gioHangService.save(existing);
        if (status != null && status == 0) {
            System.out.println("[GIOHANG][STATUS] redirect frontend base=" + frontendBaseUrl + " (status==0)");
            return ResponseEntity.status(302).location(URI.create(frontendBaseUrl)).build();
        }
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGioHang(@PathVariable Integer id) {
        GioHang gioHang = gioHangService.findActiveById(id);
        if (gioHang != null) {
            System.out.println("[GIOHANG][DELETE] soft-delete maGH=" + id);
            gioHangService.softDeleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /api/giohang/{maGH}/items -> clear items
    @DeleteMapping("/{maGH}/items")
    public ResponseEntity<Void> clearCartItems(@PathVariable Integer maGH) {
        GioHang gioHang = gioHangService.findActiveById(maGH);
        if (gioHang == null) {
            return ResponseEntity.notFound().build();
        }
        System.out.println("[GIOHANG][CLEAR_ITEMS] maGH=" + maGH);
        if (gioHang.getChiTietGioHangs() != null) {
            gioHang.getChiTietGioHangs().forEach(item -> item.setIsDeleted(true));
        }
        gioHang.setNgayCapNhat(new java.sql.Timestamp(System.currentTimeMillis()));
        gioHangService.save(gioHang);
        return ResponseEntity.ok().build();
    }

    // DTO: GET /api/giohang/{maGH}/with-items
    @GetMapping("/{maGH}/with-items")
    public ResponseEntity<GioHangWithItemsDto> getCartWithItems(@PathVariable Integer maGH) {
        GioHang gioHang = gioHangService.findActiveById(maGH);
        if (gioHang == null) return ResponseEntity.notFound().build();
        var items = chiTietGioHangService.findByGioHangIdActive(maGH);
        System.out.println("[GIOHANG][WITH_ITEMS] maGH=" + maGH + " itemsCount=" + items.size());
        GioHangWithItemsDto dto = GioHangWithItemsDto.builder()
                .maGH(gioHang.getMaGH())
                .maKH(gioHang.getKhachHang() != null ? gioHang.getKhachHang().getMaKH() : null)
                .trangThai(gioHang.getTrangThai())
                .ghiChu(gioHang.getGhiChu())
                .ngayTao(gioHang.getNgayTao())
                .ngayCapNhat(gioHang.getNgayCapNhat())
                .items(items)
                .build();
        return ResponseEntity.ok(dto);
    }

    // DTO: GET /api/giohang/by-khachhang/{maKH}/with-items (giỏ đang hoạt động gần nhất)
    @GetMapping("/by-khachhang/{maKH}/with-items")
    public ResponseEntity<GioHangWithItemsDto> getLatestCartWithItemsByCustomer(@PathVariable String maKH) {
        GioHang gioHang = gioHangService.findLatestActiveCartByMaKhachHang(maKH);
        System.out.println("[GIOHANG][LATEST_BY_KH] maKH=" + maKH +
                " foundCart=" + (gioHang != null) +
                " maGH=" + (gioHang != null ? gioHang.getMaGH() : null));
        if (gioHang == null) return ResponseEntity.notFound().build();
        return getCartWithItems(gioHang.getMaGH());
    }

    // Đồng bộ giỏ hàng từ localStorage vào DB sau khi đăng nhập (MERGE LOGIC)
    // POST /api/giohang/sync - Hoạt động với cả đăng nhập thường và Google OAuth2
    @PostMapping("/sync")
    public ResponseEntity<?> syncCartFromLocal(@RequestBody com.example.mini_supermarket.dto.CartSyncRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không có dữ liệu giỏ hàng để đồng bộ"));
        }
        
        // 1. Lấy thông tin user hiện tại từ JWT token (hoạt động cho cả 2 loại đăng nhập)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = null;
        String maKH = null;
        
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            currentUserEmail = authentication.getName(); // Email từ JWT token
            System.out.println("[GIOHANG][SYNC] authenticated user email=" + currentUserEmail);
            
            try {
                // Tìm NguoiDung theo email
                var nguoiDungOpt = userService.findByEmail(currentUserEmail);
                if (nguoiDungOpt.isPresent()) {
                    var nguoiDung = nguoiDungOpt.get();
                    // Tìm KhachHang theo maNguoiDung
                    var khachHang = khachHangService.findByMaNguoiDung(nguoiDung.getMaNguoiDung());
                    if (khachHang != null) {
                        maKH = khachHang.getMaKH();
                        System.out.println("[GIOHANG][SYNC] found customer maKH=" + maKH + " for user=" + currentUserEmail);
                    } else {
                        System.out.println("[GIOHANG][SYNC] WARNING: no customer found for user=" + currentUserEmail);
                    }
                } else {
                    System.out.println("[GIOHANG][SYNC] WARNING: user not found in database=" + currentUserEmail);
                }
            } catch (Exception e) {
                System.out.println("[GIOHANG][SYNC] ERROR finding customer for user=" + currentUserEmail + " error=" + e.getMessage());
            }
        }
        
        // Fallback: sử dụng maKH từ request body nếu không tìm thấy từ JWT
        if (maKH == null) {
            maKH = request.getMaKH();
            System.out.println("[GIOHANG][SYNC] using maKH from request body=" + maKH);
        }
        
        // Security check: đảm bảo user chỉ có thể sync giỏ hàng của chính mình
        if (request.getMaKH() != null && !request.getMaKH().equals(maKH)) {
            System.out.println("[GIOHANG][SYNC] SECURITY VIOLATION: user=" + currentUserEmail + 
                             " trying to sync cart for different customer=" + request.getMaKH());
            return ResponseEntity.status(403).body(Map.of("message", "Không được phép truy cập giỏ hàng của khách hàng khác"));
        }
        
        System.out.println("[GIOHANG][SYNC] START - authenticatedUser=" + currentUserEmail + 
                          " maKH=" + maKH + " itemsCount=" + request.getItems().size());
        
        // 2. Xác định giỏ hàng hiện tại (trạng thái 0) hoặc tạo mới
        if (maKH == null) {
            System.out.println("[GIOHANG][SYNC] ERROR: no customer ID available");
            return ResponseEntity.badRequest().body(Map.of("message", "Không thể xác định khách hàng để đồng bộ giỏ hàng"));
        }
        
        GioHang cart = gioHangService.findLatestActiveCartByMaKhachHang(maKH);
        System.out.println("[GIOHANG][SYNC] existing cart found=" + (cart != null) + 
                         (cart != null ? " maGH=" + cart.getMaGH() + " trangThai=" + cart.getTrangThai() : ""));
        
        if (cart == null) {
            System.out.println("[GIOHANG][SYNC] create new cart for maKH=" + maKH + " (trangThai=0)");
            cart = new GioHang();
            cart.setTrangThai(0);
            cart.setIsDeleted(false);
            // Gán khách hàng
            try {
                var khachHang = khachHangService.findById(maKH);
                cart.setKhachHang(khachHang);
                System.out.println("[GIOHANG][SYNC] assigned customer to new cart");
            } catch (Exception e) {
                System.out.println("[GIOHANG][SYNC] ERROR: customer not found maKH=" + maKH);
                return ResponseEntity.badRequest().body(Map.of("message", "Khách hàng không tồn tại: " + maKH));
            }
            cart = gioHangService.save(cart);
            System.out.println("[GIOHANG][SYNC] new cart created maGH=" + cart.getMaGH());
        } else {
            System.out.println("[GIOHANG][SYNC] using existing cart maGH=" + cart.getMaGH());
        }
        
        // 3. Lấy danh sách item hiện có trong giỏ
        var existingItems = chiTietGioHangService.findByGioHangIdActive(cart.getMaGH());
        System.out.println("[GIOHANG][SYNC] existing items in cart=" + existingItems.size());
        
        // 4. Merge từng item localStorage: nếu đã có sản phẩm thì cộng dồn, nếu chưa thì tạo mới
        for (var it : request.getItems()) {
            if (it.getMaSP() == null || it.getSoLuong() == null || it.getSoLuong() <= 0) continue;
            
            // Tìm item hiện có theo maSP
            var existingItem = existingItems.stream()
                    .filter(item -> item.getSanPham() != null && it.getMaSP().equals(item.getSanPham().getMaSP()))
                    .findFirst();
                    
            if (existingItem.isPresent()) {
                // CỘNG DỒN số lượng
                var item = existingItem.get();
                int oldQty = item.getSoLuong();
                int newQty = oldQty + it.getSoLuong();
                item.setSoLuong(newQty);
                chiTietGioHangService.save(item);
                System.out.println("[GIOHANG][SYNC] MERGE item maSP=" + it.getMaSP() + 
                                 " localStorage=" + it.getSoLuong() + 
                                 " + existing=" + oldQty + 
                                 " = total=" + newQty);
            } else {
                // TẠO MỚI item
                try {
                    var sanPham = sanPhamService.findActiveById(it.getMaSP());
                    if (sanPham == null) {
                        System.out.println("[GIOHANG][SYNC] SKIP item maSP=" + it.getMaSP() + " - product not found or inactive");
                        continue;
                    }
                    
                    var currentPrice = sanPhamService.getCurrentPrice(it.getMaSP());
                    if (currentPrice == null) {
                        System.out.println("[GIOHANG][SYNC] WARNING item maSP=" + it.getMaSP() + " - no price found, using 0");
                        currentPrice = java.math.BigDecimal.ZERO;
                    }
                    
                    var newItem = new com.example.mini_supermarket.entity.ChiTietGioHang();
                    newItem.setGioHang(cart);
                    newItem.setSanPham(sanPham);
                    newItem.setSoLuong(it.getSoLuong());
                    newItem.setDonGiaHienTai(currentPrice);
                    newItem.setIsDeleted(false);
                    
                    chiTietGioHangService.save(newItem);
                    System.out.println("[GIOHANG][SYNC] CREATE new item maSP=" + it.getMaSP() + 
                                     " soLuong=" + it.getSoLuong() + 
                                     " donGia=" + newItem.getDonGiaHienTai());
                } catch (Exception e) {
                    System.out.println("[GIOHANG][SYNC] ERROR creating item maSP=" + it.getMaSP() + " error=" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        // 5. Cập nhật thời gian giỏ hàng
        cart.setNgayCapNhat(new java.sql.Timestamp(System.currentTimeMillis()));
        gioHangService.save(cart);
        
        System.out.println("[GIOHANG][SYNC] COMPLETE - returning cart with items");
        // Trả lại giỏ hàng đã đồng bộ
        return getCartWithItems(cart.getMaGH());
    }
    
    // DEBUG ENDPOINT: Kiểm tra thông tin user hiện tại từ JWT
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