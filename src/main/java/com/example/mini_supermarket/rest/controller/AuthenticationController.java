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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
            @Parameter(description = "JWT token từ Authorization header", required = false)
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "HTTP Request để đọc cookies", required = false)
            HttpServletRequest request) {
        
        String token = null;
        String tokenSource = null;
        
        // 1. Ưu tiên lấy từ Authorization header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Loại bỏ "Bearer "
            tokenSource = "header";
        }
        // 2. Nếu không có header, thử lấy từ cookie
        else if (request != null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName()) || "token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    tokenSource = "cookie:" + cookie.getName();
                    break;
                }
            }
        }
        
        if (token == null) {
            return ResponseEntity.status(401).body(
                ApiResponse.<String>builder()
                    .success(false)
                    .message("Token không hợp lệ hoặc thiếu trong cả header và cookie!")
                    .error("Missing token in both header and cookie")
                    .build()
            );
        }
        
        System.out.println("🍪 Status check - Token found from: " + tokenSource);
        
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
                .message("Người dùng đã đăng nhập: " + username + " - " + role + " (Token từ: " + tokenSource + ")")
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
            @Parameter(description = "JWT token cần validate", required = false)
            @RequestBody(required = false) Map<String, String> request,
            @Parameter(description = "JWT token từ Authorization header", required = false)
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "HTTP Request để đọc cookies", required = false)
            HttpServletRequest httpRequest) {
        
        String token = null;
        String tokenSource = null;
        
        // 1. Ưu tiên lấy từ request body
        if (request != null && request.get("token") != null) {
            token = request.get("token");
            tokenSource = "request_body";
        }
        // 2. Thử lấy từ Authorization header
        else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Loại bỏ "Bearer "
            tokenSource = "header";
        }
        // 3. Thử lấy từ cookie
        else if (httpRequest != null && httpRequest.getCookies() != null) {
            for (Cookie cookie : httpRequest.getCookies()) {
                if ("jwt_token".equals(cookie.getName()) || "token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    tokenSource = "cookie:" + cookie.getName();
                    break;
                }
            }
        }
        
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
        tokenInfo.put("token_source", tokenSource);
        
        System.out.println("🍪 Token validation - Token found from: " + tokenSource);
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .result(tokenInfo)
                .success(true)
                .message("Token hợp lệ!")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    // ===== JWT LOGOUT ENDPOINT =====
    @PostMapping("/log-out")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest request) {
        try {
            String token = null;
            String tokenSource = null;
            
            // 1. Ưu tiên lấy từ Authorization header
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                tokenSource = "header";
            }
            // 2. Nếu không có header, thử lấy từ cookie
            else if (request != null && request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("jwt_token".equals(cookie.getName()) || "token".equals(cookie.getName())) {
                        token = cookie.getValue();
                        tokenSource = "cookie:" + cookie.getName();
                        break;
                    }
                }
            }
            
            if (token == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>builder()
                        .success(false)
                        .message("Token không hợp lệ hoặc thiếu trong cả header và cookie!")
                        .error("Missing token in both header and cookie")
                        .build());
            }
            
            // Thêm token vào blacklist để invalidate
            tokenBlacklistService.blacklistToken(token);
            
            System.out.println("🍪 User logged out successfully. Token invalidated from: " + tokenSource);
            
            return ResponseEntity.ok()
                .body(ApiResponse.<String>builder()
                    .success(true)
                    .message("Đăng xuất thành công")
                    .result("Logout successful")
                    .build());
                
        } catch (Exception e) {
            System.err.println("❌ Logout error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Lỗi đăng xuất: " + e.getMessage())
                    .error(e.getMessage())
                    .build());
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
    
    /**
     * 🍪 Test endpoint để kiểm tra cookie JWT
     */
    @Operation(summary = "Test Cookie JWT", description = "Kiểm tra JWT token từ cookie")
    @GetMapping("/test-cookie")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testCookieAuth(HttpServletRequest request) {
        
        Map<String, Object> tokenValidation = jwtUtil.validateTokenFromRequest(request);
        
        if ((Boolean) tokenValidation.get("valid")) {
            return ResponseEntity.ok().body(
                ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("🍪 Cookie JWT authentication thành công!")
                    .result(tokenValidation)
                    .build()
            );
        } else {
            return ResponseEntity.status(401).body(
                ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("🍪 Cookie JWT authentication thất bại!")
                    .result(tokenValidation)
                    .build()
            );
        }
    }

} 