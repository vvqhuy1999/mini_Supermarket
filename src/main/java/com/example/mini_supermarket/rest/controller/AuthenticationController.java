package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.dto.ApiResponse;
import com.example.mini_supermarket.dto.AuthenticationResponse;
import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.service.AuthenticationService;
import com.example.mini_supermarket.service.TokenBlacklistService;
import com.example.mini_supermarket.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Xác thực", description = "API đăng nhập và xác thực người dùng")
public class AuthenticationController {
    
    AuthenticationService authenticationService;
    JwtUtil jwtUtil;
    TokenBlacklistService tokenBlacklistService;
    
    public AuthenticationController(AuthenticationService authenticationService, JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }
    
    /**
     * Đăng nhập người dùng
     */
    @Operation(summary = "Đăng nhập người dùng", description = "Xác thực người dùng bằng email/mã người dùng và mật khẩu")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Đăng nhập thành công", 
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin đăng nhập không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Xác thực thất bại"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping("/log-in")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @Parameter(description = "Thông tin đăng nhập (email hoặc maNguoiDung + matKhau)", required = true)
            @RequestBody NguoiDung loginRequest) {
        
        try {
            // Validation
            if (loginRequest == null) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.<AuthenticationResponse>builder()
                        .success(false)
                        .message("Request body không được để trống!")
                        .error("Null request")
                        .build()
                );
            }
            
            // Tạo username từ email hoặc mã người dùng
            String username = loginRequest.getEmail() != null ? loginRequest.getEmail() : loginRequest.getMaNguoiDung();
            String password = loginRequest.getMatKhau();
            
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.<AuthenticationResponse>builder()
                        .success(false)
                        .message("Email hoặc mã người dùng không được để trống!")
                        .error("Missing username")
                        .build()
                );
            }
            
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.<AuthenticationResponse>builder()
                        .success(false)
                        .message("Mật khẩu không được để trống!")
                        .error("Missing password")
                        .build()
                );
            }
            
            AuthenticationResponse authResponse = authenticationService.authenticate(username, password);
            
            ApiResponse<AuthenticationResponse> response = ApiResponse.<AuthenticationResponse>builder()
                    .result(authResponse)
                    .success(authResponse.isAuthenticated())
                    .message(authResponse.getMessage())
                    .build();
            
            if (authResponse.isAuthenticated()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            ApiResponse<AuthenticationResponse> errorResponse = ApiResponse.<AuthenticationResponse>builder()
                    .success(false)
                    .message("Lỗi xác thực: " + e.getMessage())
                    .error(e.getMessage())
                    .build();
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Kiểm tra trạng thái đăng nhập
     */
    @Operation(summary = "Kiểm tra trạng thái đăng nhập", description = "Kiểm tra xem người dùng có đang đăng nhập hay không")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Kiểm tra thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<String>> checkAuthStatus(
            @Parameter(description = "JWT token để kiểm tra", required = false)
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(
                ApiResponse.<String>builder()
                    .success(false)
                    .message("Token không hợp lệ hoặc thiếu!")
                    .error("Missing or invalid token")
                    .build()
            );
        }
        
        String token = authHeader.substring(7); // Loại bỏ "Bearer "
        
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(
                ApiResponse.<String>builder()
                    .success(false)
                    .message("Token đã hết hạn hoặc không hợp lệ!")
                    .error("Token expired or invalid")
                    .build()
            );
        }
        
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        
        ApiResponse<String> response = ApiResponse.<String>builder()
                .result("Authenticated")
                .success(true)
                .message("Người dùng đã đăng nhập: " + username + " - " + role)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Validate JWT token
     */
    @Operation(summary = "Validate JWT token", description = "Kiểm tra tính hợp lệ của JWT token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token không hợp lệ")
    })
    @PostMapping("/validate-token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateToken(
            @Parameter(description = "JWT token cần validate", required = true)
            @RequestBody Map<String, String> request) {
        
        String token = request.get("token");
        
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Token không được để trống!")
                    .error("Missing token")
                    .build()
            );
        }
        
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(
                ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Token không hợp lệ hoặc đã hết hạn!")
                    .error("Invalid or expired token")
                    .build()
            );
        }
        
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        Date expiration = jwtUtil.getExpirationFromToken(token);
        
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("username", username);
        tokenInfo.put("role", role);
        tokenInfo.put("expiration", expiration);
        tokenInfo.put("valid", true);
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .result(tokenInfo)
                .success(true)
                .message("Token hợp lệ!")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Đăng xuất tổng hợp - Hỗ trợ cả OAuth2 và tài khoản thường
     */
    @Operation(
        summary = "🔐 Đăng xuất tổng hợp", 
        description = """
            **Chức năng:** Đăng xuất cho tất cả loại tài khoản
            
            **Hỗ trợ:**
            - 👤 Tài khoản thường (email/password)
            - 🌐 Tài khoản Google OAuth2
            - 📘 Tài khoản Facebook OAuth2
            
            **Hoạt động:**
            - 🚫 Blacklist JWT token hiện tại
            - 🗑️ Clear session data
            - ✅ Logout thành công cho mọi loại tài khoản
            
            **Lưu ý:**
            - Chỉ logout khỏi ứng dụng này
            - Google/Facebook account vẫn đăng nhập trong browser
            - Lần đăng nhập sau với OAuth2 sẽ dễ dàng hơn
            
            **Headers:** Authorization: Bearer {JWT_TOKEN}
            """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "✅ Đăng xuất thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "❌ Token không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "❌ Lỗi server")
    })
    @PostMapping("/log-out")
    public ResponseEntity<ApiResponse<Map<String, Object>>> logout(
            @Parameter(description = "JWT token cần logout", required = false)
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Kiểm tra Authorization header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                result.put("token_provided", false);
                result.put("logout_type", "no_token");
                result.put("message", "Không có token - đăng xuất cơ bản");
                
                return ResponseEntity.ok().body(
                    ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Đăng xuất thành công (không có token)")
                        .result(result)
                        .build()
                );
            }
            
            String token = authHeader.substring(7); // Loại bỏ "Bearer "
            result.put("token_provided", true);
            
            // Kiểm tra format token cơ bản trước khi validate
            if (token == null || token.trim().isEmpty()) {
                result.put("token_valid", false);
                result.put("logout_type", "empty_token");
                result.put("message", "Token rỗng - đăng xuất cơ bản");
                
                return ResponseEntity.ok().body(
                    ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Đăng xuất thành công (token rỗng)")
                        .result(result)
                        .build()
                );
            }
            
            // Kiểm tra format JWT cơ bản (phải có 3 phần)
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length != 3) {
                result.put("token_valid", false);
                result.put("logout_type", "malformed_token");
                result.put("token_parts", tokenParts.length);
                result.put("message", "Token không đúng định dạng JWT - đăng xuất cơ bản");
                
                return ResponseEntity.ok().body(
                    ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Đăng xuất thành công (token không đúng định dạng)")
                        .result(result)
                        .build()
                );
            }
            
            // Validate token chi tiết
            boolean isTokenValid = false;
            try {
                isTokenValid = jwtUtil.validateToken(token);
            } catch (Exception e) {
                // Log lỗi nhưng vẫn cho phép logout thành công
                result.put("validation_error", e.getMessage());
            }
            
            if (!isTokenValid) {
                result.put("token_valid", false);
                result.put("logout_type", "invalid_token");
                result.put("message", "Token không hợp lệ hoặc đã hết hạn - đăng xuất cơ bản");
                
                return ResponseEntity.ok().body(
                    ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Đăng xuất thành công (token không hợp lệ)")
                        .result(result)
                        .build()
                );
            }
            
            result.put("token_valid", true);
            
            // Lấy thông tin từ token
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            result.put("username", username);
            result.put("role", role);
            
            // Xác định loại tài khoản
            String accountType = "REGULAR";
            if (username != null && username.contains("@")) {
                // Có thể là OAuth2 account (thường dùng email)
                accountType = "OAUTH2_LIKELY";
            }
            result.put("account_type", accountType);
            
            // Thêm token vào blacklist để vô hiệu hóa (nếu có thể)
            try {
                tokenBlacklistService.blacklistToken(token);
                result.put("token_blacklisted", true);
            } catch (Exception e) {
                // Nếu không thể blacklist, vẫn tiếp tục logout
                result.put("token_blacklisted", false);
                result.put("blacklist_error", e.getMessage());
            }
            result.put("logout_type", "complete");
            
            // Thông báo phù hợp với loại tài khoản
            String message;
            if (accountType.equals("OAUTH2_LIKELY")) {
                message = "Đăng xuất thành công! Token đã được vô hiệu hóa. " +
                         "Lưu ý: Google/Facebook account vẫn đăng nhập trong browser.";
                result.put("oauth2_note", "Google/Facebook session vẫn hoạt động trong browser");
            } else {
                message = "Đăng xuất thành công! Token đã được vô hiệu hóa.";
            }
            
            result.put("success", true);
            result.put("message", message);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                    .result(result)
                    .success(true)
                    .message(message)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("success", false);
            
            ApiResponse<Map<String, Object>> errorResponse = ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Lỗi đăng xuất: " + e.getMessage())
                    .error(e.getMessage())
                    .result(result)
                    .build();
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Lấy thông tin blacklist (cho admin)
     */
    @Operation(summary = "Lấy thông tin blacklist", description = "Lấy thông tin chi tiết về các token bị blacklist (chỉ dành cho admin)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    @GetMapping("/blacklist-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBlacklistInfo() {
        try {
            Map<String, Object> blacklistDetails = tokenBlacklistService.getBlacklistDetails();
            
            ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                    .result(blacklistDetails)
                    .success(true)
                    .message("Lấy thông tin blacklist thành công")
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<Map<String, Object>> errorResponse = ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Lỗi lấy thông tin blacklist: " + e.getMessage())
                    .error(e.getMessage())
                    .build();
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Kiểm tra token có trong blacklist không
     */
    @Operation(summary = "Kiểm tra token trong blacklist", description = "Kiểm tra một token cụ thể có trong blacklist không")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Token không hợp lệ")
    })
    @PostMapping("/check-blacklist")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkTokenInBlacklist(
            @Parameter(description = "JWT token cần kiểm tra", required = true)
            @RequestBody Map<String, String> request) {
        
        try {
            String token = request.get("token");
            
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.<Map<String, Object>>builder()
                        .success(false)
                        .message("Token không được để trống!")
                        .error("Missing token")
                        .build()
                );
            }
            
            Map<String, Object> tokenDetails = tokenBlacklistService.getTokenDetails(token);
            
            if (tokenDetails == null) {
                // Token không có trong blacklist
                Map<String, Object> result = new HashMap<>();
                result.put("token", token);
                result.put("isBlacklisted", false);
                result.put("message", "Token không có trong blacklist");
                
                ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                        .result(result)
                        .success(true)
                        .message("Token không có trong blacklist")
                        .build();
                
                return ResponseEntity.ok(response);
            } else {
                // Token có trong blacklist
                tokenDetails.put("isBlacklisted", true);
                tokenDetails.put("message", "Token đã bị blacklist");
                
                ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                        .result(tokenDetails)
                        .success(true)
                        .message("Token đã bị blacklist")
                        .build();
                
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception e) {
            ApiResponse<Map<String, Object>> errorResponse = ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Lỗi kiểm tra blacklist: " + e.getMessage())
                    .error(e.getMessage())
                    .build();
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    

} 