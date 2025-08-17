package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.KhachHang;
import com.example.mini_supermarket.service.KhachHangService;
import com.example.mini_supermarket.dto.CustomerRegistrationRequest;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/khachhang")
@CrossOrigin(origins = "*")
@Tag(name = "Khách hàng", description = "API quản lý khách hàng")
public class KhachHangRestController {

    @Autowired
    private KhachHangService khachHangService;

    @Operation(summary = "Lấy tất cả khách hàng", description = "Trả về danh sách tất cả khách hàng chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhachHang.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<KhachHang>> getAllKhachHang() {
        try {
            List<KhachHang> khachHangs = khachHangService.findAllActive();
            return new ResponseEntity<>(khachHangs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy khách hàng theo ID", description = "Trả về thông tin khách hàng theo ID (chỉ lấy khách hàng chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy khách hàng", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhachHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<KhachHang> getKhachHangById(
            @Parameter(description = "ID của khách hàng", required = true) @PathVariable String id) {
        try {
            KhachHang khachHang = khachHangService.findActiveById(id);
            if (khachHang != null) {
                return new ResponseEntity<>(khachHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy khách hàng theo mã người dùng", description = "Trả về thông tin khách hàng theo mã người dùng (chỉ lấy khách hàng chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy khách hàng", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhachHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/by-nguoidung/{maNguoiDung}")
    public ResponseEntity<KhachHang> getKhachHangByMaNguoiDung(
            @Parameter(description = "Mã người dùng", required = true) @PathVariable String maNguoiDung) {
        try {
            KhachHang khachHang = khachHangService.findByMaNguoiDung(maNguoiDung);
            if (khachHang != null) {
                return new ResponseEntity<>(khachHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Thêm khách hàng mới
    @PostMapping
    public ResponseEntity<KhachHang> createKhachHang(@RequestBody KhachHang khachHang) {
        try {
            KhachHang savedKhachHang = khachHangService.save(khachHang);
            return new ResponseEntity<>(savedKhachHang, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Cập nhật khách hàng
    @PutMapping("/{id}")
    public ResponseEntity<KhachHang> updateKhachHang(@PathVariable String id, @RequestBody KhachHang khachHang) {
        try {
            KhachHang existingKhachHang = khachHangService.findActiveById(id);
            if (existingKhachHang != null) {
                khachHang.setMaKH(id); // Đảm bảo ID không thay đổi
                khachHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                KhachHang updatedKhachHang = khachHangService.update(khachHang);
                return new ResponseEntity<>(updatedKhachHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật thông tin khách hàng theo mã người dùng", description = "Cập nhật thông tin khách hàng dựa trên mã người dùng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhachHang.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/by-nguoidung/{maNguoiDung}")
    public ResponseEntity<KhachHang> updateKhachHangByMaNguoiDung(
            @Parameter(description = "Mã người dùng", required = true) @PathVariable String maNguoiDung,
            @Parameter(description = "Thông tin khách hàng cần cập nhật", required = true) @RequestBody KhachHang khachHang) {
        try {
            KhachHang existingKhachHang = khachHangService.findByMaNguoiDung(maNguoiDung);
            if (existingKhachHang != null) {
                // Cập nhật thông tin nhưng giữ nguyên các trường quan trọng
                khachHang.setMaKH(existingKhachHang.getMaKH()); // Giữ nguyên mã khách hàng
                khachHang.setNguoiDung(existingKhachHang.getNguoiDung()); // Giữ nguyên liên kết với NguoiDung
                khachHang.setNgayDangKy(existingKhachHang.getNgayDangKy()); // Giữ nguyên ngày đăng ký
                khachHang.setDiemTichLuy(existingKhachHang.getDiemTichLuy()); // Giữ nguyên điểm tích lũy
                khachHang.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
                
                KhachHang updatedKhachHang = khachHangService.update(khachHang);
                return new ResponseEntity<>(updatedKhachHang, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Xóa khách hàng (soft delete - chỉ đánh dấu isDeleted = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteKhachHang(@PathVariable String id) {
        try {
            KhachHang existingKhachHang = khachHangService.findActiveById(id);
            if (existingKhachHang != null) {
                khachHangService.softDeleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Đăng ký tài khoản khách hàng mới (bao gồm cả tài khoản đăng nhập)
     */
    @Operation(
        summary = "🎯 Đăng ký tài khoản khách hàng mới", 
        description = """
            **Chức năng:** Tạo tài khoản đăng nhập và thông tin khách hàng trong một bước
            
            **Quy trình:**
            1. Tạo tài khoản NguoiDung (email, mật khẩu)
            2. Tạo thông tin KhachHang (họ tên, SĐT, địa chỉ)
            3. Liên kết hai bảng với nhau
            
            **Dữ liệu bắt buộc:**
            - ✅ email: Địa chỉ email dùng để đăng nhập
            - ✅ matKhau: Mật khẩu (sẽ được mã hóa tự động)
            - ✅ hoTen: Họ và tên đầy đủ
            - ✅ sdt: Số điện thoại liên hệ
            
            **Dữ liệu tùy chọn:**
            - diaChi: Địa chỉ khách hàng
            
            **Sau khi đăng ký thành công:**
            - Có thể đăng nhập bằng email/password vừa tạo
            - Mã khách hàng được tạo tự động (KH + 6 ký tự)
            - Vai trò mặc định: Khách hàng (VaiTro = 3)
            - Loại khách hàng mặc định: "Thường"
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "✅ Đăng ký tài khoản thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = KhachHang.class))),
            @ApiResponse(responseCode = "400", description = "❌ Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "409", description = "❌ Email đã tồn tại"),
            @ApiResponse(responseCode = "500", description = "❌ Lỗi server")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomerAccount(
            @Parameter(description = "Thông tin đăng ký tài khoản khách hàng", required = true)
            @RequestBody CustomerRegistrationRequest request) {
        
        try {
            // Validation cơ bản
            Map<String, String> errors = validateRegistrationRequest(request);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Dữ liệu không hợp lệ!",
                    "errors", errors
                ));
            }
            
            // Đăng ký tài khoản khách hàng
            KhachHang newCustomer = khachHangService.registerCustomerAccount(
                request.getEmail(),
                request.getMatKhau(),
                request.getHoTen(),
                request.getSdt(),
                request.getDiaChi()
            );
            
            // Tạo response thành công
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng ký tài khoản khách hàng thành công!");
            response.put("customer", newCustomer);
            response.put("login_info", Map.of(
                "email", request.getEmail(),
                "message", "Có thể đăng nhập ngay bằng email và mật khẩu vừa tạo"
            ));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            // Lỗi từ business logic (email đã tồn tại, validation, etc.)
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error_type", "business_logic");
            
            // Kiểm tra loại lỗi để trả về status code phù hợp
            if (e.getMessage().contains("đã tồn tại")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            } else {
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
        } catch (Exception e) {
            // Lỗi server không mong muốn
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi server: " + e.getMessage());
            errorResponse.put("error_type", "server_error");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Validate dữ liệu đăng ký
     */
    private Map<String, String> validateRegistrationRequest(CustomerRegistrationRequest request) {
        Map<String, String> errors = new HashMap<>();
        
        if (request == null) {
            errors.put("general", "Request body không được null");
            return errors;
        }
        
        // Validate email
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            errors.put("email", "Email không được để trống");
        } else if (!request.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            errors.put("email", "Email không đúng định dạng");
        }
        
        // Validate mật khẩu
        if (request.getMatKhau() == null || request.getMatKhau().trim().isEmpty()) {
            errors.put("matKhau", "Mật khẩu không được để trống");
        } else if (request.getMatKhau().length() < 6) {
            errors.put("matKhau", "Mật khẩu phải có ít nhất 6 ký tự");
        }
        
        // Validate họ tên
        if (request.getHoTen() == null || request.getHoTen().trim().isEmpty()) {
            errors.put("hoTen", "Họ tên không được để trống");
        } else if (request.getHoTen().trim().length() < 2) {
            errors.put("hoTen", "Họ tên phải có ít nhất 2 ký tự");
        }
        
        // Validate số điện thoại
        if (request.getSdt() == null || request.getSdt().trim().isEmpty()) {
            errors.put("sdt", "Số điện thoại không được để trống");
        } else if (!request.getSdt().matches("^[0-9]{10,11}$")) {
            errors.put("sdt", "Số điện thoại phải có 10-11 chữ số");
        }
        
        return errors;
    }
} 