package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.service.UserService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/nguoidung")
@CrossOrigin(origins = "*")
@Tag(name = "Người dùng", description = "API quản lý người dùng - Đăng ký, tìm kiếm, cập nhật, xóa")
public class NguoiDungRestController {
    
    private final UserService userService;
    
    @Autowired
    public NguoiDungRestController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Lấy tất cả người dùng
     */
    @Operation(summary = "Lấy tất cả người dùng", description = "Trả về danh sách tất cả người dùng trong hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NguoiDung.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<NguoiDung>> getAllNguoiDung() {
        try {
            // TODO: Implement getAllNguoiDung in UserService
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lấy người dùng theo ID
     */
    @Operation(summary = "Lấy người dùng theo ID", description = "Trả về thông tin người dùng theo mã người dùng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy người dùng", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NguoiDung.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{maNguoiDung}")
    public ResponseEntity<NguoiDung> getNguoiDungById(
            @Parameter(description = "Mã người dùng", required = true) 
            @PathVariable String maNguoiDung) {
        try {
            Optional<NguoiDung> nguoiDung = userService.findByMaNguoiDung(maNguoiDung);
            if (nguoiDung.isPresent()) {
                return ResponseEntity.ok(nguoiDung.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lấy người dùng theo email
     */
    @Operation(summary = "Lấy người dùng theo email", description = "Trả về thông tin người dùng theo email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy người dùng", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NguoiDung.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<NguoiDung> getNguoiDungByEmail(
            @Parameter(description = "Email người dùng", required = true) 
            @PathVariable String email) {
        try {
            Optional<NguoiDung> nguoiDung = userService.findByEmail(email);
            if (nguoiDung.isPresent()) {
                return ResponseEntity.ok(nguoiDung.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Đăng ký người dùng mới
     */
    @Operation(summary = "Đăng ký người dùng mới", description = "Tạo tài khoản người dùng mới với kiểm tra email và mã người dùng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Đăng ký thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = NguoiDung.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc email/mã người dùng đã tồn tại"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerNguoiDung(
            @Parameter(description = "Thông tin người dùng cần đăng ký", required = true)
            @RequestBody NguoiDung nguoiDung) {
        try {
            // Kiểm tra email hợp lệ
            if (!userService.isValidEmail(nguoiDung.getEmail())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email không hợp lệ!");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Kiểm tra email đã tồn tại
            if (userService.isEmailExists(nguoiDung.getEmail())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email đã tồn tại trong hệ thống!");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Kiểm tra mã người dùng nếu có
            if (nguoiDung.getMaNguoiDung() != null && !nguoiDung.getMaNguoiDung().isEmpty()) {
                if (!userService.isValidMaNguoiDung(nguoiDung.getMaNguoiDung())) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Mã người dùng không hợp lệ!");
                    return ResponseEntity.badRequest().body(error);
                }
                
                if (userService.isMaNguoiDungExistsIncludeDeleted(nguoiDung.getMaNguoiDung())) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Mã người dùng đã tồn tại trong hệ thống!");
                    return ResponseEntity.badRequest().body(error);
                }
            }
            
            NguoiDung registeredUser = userService.registerUser(nguoiDung);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Cập nhật thông tin người dùng
     */
    @Operation(summary = "Cập nhật thông tin người dùng", description = "Cập nhật thông tin người dùng theo mã người dùng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{maNguoiDung}")
    public ResponseEntity<?> updateNguoiDung(
            @Parameter(description = "Mã người dùng", required = true) 
            @PathVariable String maNguoiDung,
            @Parameter(description = "Thông tin người dùng cần cập nhật", required = true)
            @RequestBody NguoiDung nguoiDung) {
        try {
            // TODO: Implement updateNguoiDung in UserService
            Map<String, String> message = new HashMap<>();
            message.put("message", "Cập nhật người dùng thành công!");
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi cập nhật: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Xóa người dùng (soft delete)
     */
    @Operation(summary = "Xóa người dùng", description = "Xóa mềm người dùng (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{maNguoiDung}")
    public ResponseEntity<?> deleteNguoiDung(
            @Parameter(description = "Mã người dùng", required = true) 
            @PathVariable String maNguoiDung) {
        try {
            // TODO: Implement deleteNguoiDung in UserService
            Map<String, String> message = new HashMap<>();
            message.put("message", "Xóa người dùng thành công!");
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi xóa: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Kiểm tra email tồn tại
     */
    @Operation(summary = "Kiểm tra email tồn tại", description = "Kiểm tra email có hợp lệ và đã tồn tại trong hệ thống chưa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kiểm tra thành công"),
            @ApiResponse(responseCode = "400", description = "Email không hợp lệ")
    })
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Object>> checkEmailExists(
            @Parameter(description = "Email cần kiểm tra", required = true) 
            @PathVariable String email) {
        Map<String, Object> response = new HashMap<>();
        
        if (!userService.isValidEmail(email)) {
            response.put("valid", false);
            response.put("message", "Email không hợp lệ!");
            return ResponseEntity.badRequest().body(response);
        }
        
        boolean exists = userService.isEmailExists(email);
        response.put("exists", exists);
        response.put("valid", true);
        response.put("message", exists ? "Email đã tồn tại!" : "Email có thể sử dụng!");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Kiểm tra mã người dùng tồn tại
     */
    @Operation(summary = "Kiểm tra mã người dùng tồn tại", description = "Kiểm tra mã người dùng có hợp lệ và đã tồn tại trong hệ thống chưa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kiểm tra thành công"),
            @ApiResponse(responseCode = "400", description = "Mã người dùng không hợp lệ")
    })
    @GetMapping("/check-id/{maNguoiDung}")
    public ResponseEntity<Map<String, Object>> checkMaNguoiDungExists(
            @Parameter(description = "Mã người dùng cần kiểm tra", required = true) 
            @PathVariable String maNguoiDung) {
        Map<String, Object> response = new HashMap<>();
        
        if (!userService.isValidMaNguoiDung(maNguoiDung)) {
            response.put("valid", false);
            response.put("message", "Mã người dùng không hợp lệ!");
            return ResponseEntity.badRequest().body(response);
        }
        
        boolean exists = userService.isMaNguoiDungExists(maNguoiDung);
        response.put("exists", exists);
        response.put("valid", true);
        response.put("message", exists ? "Mã người dùng đã tồn tại!" : "Mã người dùng có thể sử dụng!");
        
        return ResponseEntity.ok(response);
    }
} 