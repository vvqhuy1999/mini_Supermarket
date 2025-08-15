package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.dto.ApiResponse;
import com.example.mini_supermarket.dto.AuthenticationResponse;
import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.service.OAuth2Service;
import com.example.mini_supermarket.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import com.example.mini_supermarket.repository.NguoiDungRepository;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/oauth2")
@CrossOrigin(origins = "*")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "🔐 OAuth2 Authentication", description = "Complete OAuth2 Google & Facebook Login System with JWT Token Management")
public class OAuth2Controller {
    
    private final OAuth2Service oAuth2Service;
    private final NguoiDungRepository nguoiDungRepository;
    private final JwtUtil jwtUtil;
    
    @Autowired
    public OAuth2Controller(OAuth2Service oAuth2Service, NguoiDungRepository nguoiDungRepository, JwtUtil jwtUtil) {
        this.oAuth2Service = oAuth2Service;
        this.nguoiDungRepository = nguoiDungRepository;
        this.jwtUtil = jwtUtil;
    }
    
    // Constructor mặc định để tránh lỗi dependency
    public OAuth2Controller() {
        this.oAuth2Service = null;
        this.nguoiDungRepository = null;
        this.jwtUtil = null;
    }
    
    

    
    /**
     * Test endpoint để kiểm tra OAuth2 configuration
     */
    @Operation(summary = "Test OAuth2 Configuration", description = "Kiểm tra cấu hình OAuth2")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/test-config")
    public ResponseEntity<ApiResponse<Map<String, String>>> testOAuth2Config() {
        
        Map<String, String> config = new HashMap<>();
        config.put("status", "OAuth2 Controller is running");
        config.put("message", "OAuth2 endpoints are available");
        config.put("google_configured", "true");
        config.put("facebook_configured", "true");
        config.put("google_client_id", "1096787831677-f9nhpe4jc0be40sc5s910qihfv22etrl.apps.googleusercontent.com");
        config.put("facebook_client_id", "1793399401565005");
        config.put("google_redirect_uri", "http://localhost:8080/login/oauth2/code/google");
        config.put("facebook_redirect_uri", "http://localhost:8080/login/oauth2/code/facebook");
        config.put("test_urls", "http://localhost:8080/oauth2/authorization/google, http://localhost:8080/oauth2/authorization/facebook");
        
        if (oAuth2Service != null) {
            Map<String, String> serviceConfig = oAuth2Service.getOAuth2Configuration();
            config.putAll(serviceConfig);
        }
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .result(config)
                .success(true)
                .message("OAuth2 Controller is running")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint để lấy thông tin user khi đã đăng nhập OAuth2
     */
    @Operation(summary = "Get OAuth2 User Info", description = "Lấy thông tin user từ OAuth2 khi đã đăng nhập")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping("/user-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserInfo(
            @Parameter(description = "OAuth2 User")
            @AuthenticationPrincipal OAuth2User oauth2User) {
        
        if (oauth2User == null) {
            return ResponseEntity.status(401).body(
                ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Chưa đăng nhập OAuth2!")
                    .error("Not authenticated")
                    .build()
            );
        }
        
        Map<String, Object> userInfo = oauth2User.getAttributes();
        
        // Thêm thông tin phân tích chi tiết
        Map<String, Object> detailedInfo = new HashMap<>();
        detailedInfo.put("raw_attributes", userInfo); // Tất cả thông tin gốc từ Google
        
        // Phân tích từng trường thông tin
        detailedInfo.put("email", userInfo.get("email"));
        detailedInfo.put("name", userInfo.get("name"));
        detailedInfo.put("given_name", userInfo.get("given_name"));
        detailedInfo.put("family_name", userInfo.get("family_name"));
        detailedInfo.put("picture", userInfo.get("picture"));
        detailedInfo.put("sub", userInfo.get("sub")); // Google User ID
        detailedInfo.put("locale", userInfo.get("locale"));
        detailedInfo.put("email_verified", userInfo.get("email_verified"));
        detailedInfo.put("hd", userInfo.get("hd")); // Hosted domain
        detailedInfo.put("iat", userInfo.get("iat")); // Issued at
        detailedInfo.put("exp", userInfo.get("exp")); // Expires at
        detailedInfo.put("azp", userInfo.get("azp")); // Authorized party
        detailedInfo.put("aud", userInfo.get("aud")); // Audience
        detailedInfo.put("iss", userInfo.get("iss")); // Issuer
        
        // Thống kê thông tin
        detailedInfo.put("total_attributes", userInfo.size());
        detailedInfo.put("attribute_names", userInfo.keySet());
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .result(detailedInfo)
                .success(true)
                .message("Thông tin chi tiết OAuth2 user từ Google")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ✅ OAuth2 Success Handler - Chính thức xử lý thành công đăng nhập
     */
    @Operation(
        summary = "✅ OAuth2 Success Handler", 
        description = """
            🎯 **Main OAuth2 Success Endpoint**
            
            **Chức năng chính:**
            - 🔍 Phân tích dữ liệu OAuth2 User (Google/Facebook)
            - 💾 Tự động lưu/cập nhật user vào database
            - 🎫 Tạo JWT token cho session
            - 📊 Trả về thông tin user và token
            
            **Flow hoạt động:**
            1. User đăng nhập Google/Facebook thành công
            2. OAuth2 provider redirect về endpoint này
            3. System xử lý và lưu user data
            4. Tạo JWT token và trả về response
            
            **Provider hỗ trợ:** Google, Facebook
            
            **⚠️ Lưu ý:** Chỉ gọi được sau khi đăng nhập OAuth2 thành công
            """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", 
                description = "✅ Success - User authenticated và JWT token được tạo"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", 
                description = "❌ Unauthorized - Chưa đăng nhập OAuth2"
            )
    })
    @GetMapping("/success")
    public ResponseEntity<ApiResponse<Map<String, Object>>> oauth2Success(
            @AuthenticationPrincipal OAuth2User oauth2User) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (oauth2User == null) {
            result.put("message", "OAuth2 login successful but no user data");
            result.put("user", null);
            result.put("saved_to_database", false);
        } else {
            try {
                // Xác định provider dựa trên attributes
                Map<String, Object> attributes = oauth2User.getAttributes();
                String provider = "Unknown";
                
                if (attributes.containsKey("sub") && attributes.containsKey("email_verified")) {
                    provider = "Google";
                } else if (attributes.containsKey("id") && !attributes.containsKey("email_verified")) {
                    provider = "Facebook";
                }
                
                // Xử lý và lưu vào database
                AuthenticationResponse authResponse;
                if (provider.equals("Google")) {
                    authResponse = oAuth2Service.processGoogleLogin(oauth2User);
                } else if (provider.equals("Facebook")) {
                    authResponse = oAuth2Service.processFacebookLogin(oauth2User);
                } else {
                    // Fallback cho trường hợp không xác định được provider
                    authResponse = oAuth2Service.processGoogleLogin(oauth2User);
                }
                
                result.put("message", "OAuth2 login successful and saved to database");
                result.put("user", oauth2User.getAttributes());
                result.put("provider", provider);
                result.put("saved_to_database", authResponse.isAuthenticated());
                result.put("auth_response", authResponse);
                result.put("jwt_token", authResponse.getToken()); // Thêm token vào response
                
                System.out.println("✅ OAuth2 Success: " + provider + " user saved to database");
                
            } catch (Exception e) {
                System.err.println("❌ OAuth2 Success Error: " + e.getMessage());
                e.printStackTrace();
                
                result.put("message", "OAuth2 login successful but failed to save to database");
                result.put("user", oauth2User.getAttributes());
                result.put("saved_to_database", false);
                result.put("error", e.getMessage());
            }
        }
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .result(result)
                .success(true)
                .message("OAuth2 login successful")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint xử lý OAuth2 failure callback
     */
    @Operation(summary = "OAuth2 Failure Callback", description = "Xử lý callback thất bại từ OAuth2")
    @GetMapping("/failure")
    public ResponseEntity<ApiResponse<Map<String, String>>> oauth2Failure() {
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "OAuth2 login failed");
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .result(result)
                .success(false)
                .message("OAuth2 login failed")
                .build();
        
        return ResponseEntity.ok(response);
    }
    

    
    /**
     * Endpoint để kiểm tra email trùng lặp
     */
    @Operation(summary = "Check Email Duplicate", description = "Kiểm tra email có trùng lặp trong database không")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email không hợp lệ")
    })
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkEmailDuplicate(
            @Parameter(description = "Email cần kiểm tra")
            @RequestParam String email) {
        try {
            // Chuẩn hóa email
            email = email.trim().toLowerCase();
            
            // Kiểm tra format email
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.<Map<String, Object>>builder()
                                .success(false)
                                .message("Email không hợp lệ")
                                .build());
            }
            
            // Tìm người dùng theo email
            Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByEmail(email);
            
            Map<String, Object> result = new HashMap<>();
            result.put("valid", true);
            result.put("exists", nguoiDungOpt.isPresent());
            
            if (nguoiDungOpt.isPresent()) {
                NguoiDung nguoiDung = nguoiDungOpt.get();
                result.put("isDeleted", nguoiDung.getIsDeleted());
                result.put("vaiTro", nguoiDung.getVaiTro());
                result.put("sub", nguoiDung.getSub()); // Thêm thông tin sub
                result.put("accountType", getAccountType(nguoiDung.getMaNguoiDung()));
                
                if (nguoiDung.getIsDeleted()) {
                    result.put("message", "Email đã tồn tại nhưng tài khoản đã bị xóa");
                } else {
                    result.put("message", "Email đã tồn tại trong hệ thống");
                }
            } else {
                result.put("message", "Email chưa tồn tại trong hệ thống");
            }
            
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Kiểm tra email thành công")
                    .result(result)
                    .build());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Map<String, Object>>builder()
                            .success(false)
                            .message("Lỗi kiểm tra email: " + e.getMessage())
                            .build());
        }
    }
    
    @Operation(summary = "Check OAuth2 Sub", description = "Kiểm tra OAuth2 ID (sub) có tồn tại trong database không")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Sub không hợp lệ")
    })
            @GetMapping("/check-sub")
        public ResponseEntity<ApiResponse<Map<String, Object>>> checkSub(
                @Parameter(description = "OAuth2 ID (sub) cần kiểm tra")
                @RequestParam String sub) {
            try {
                // Kiểm tra sub không rỗng
                if (sub == null || sub.trim().isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.<Map<String, Object>>builder()
                                    .success(false)
                                    .message("Sub không được để trống")
                                    .build());
                }
                
                // Tìm người dùng theo sub
                Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findBySub(sub);
                
                Map<String, Object> result = new HashMap<>();
                result.put("exists", nguoiDungOpt.isPresent());
                
                if (nguoiDungOpt.isPresent()) {
                    NguoiDung nguoiDung = nguoiDungOpt.get();
                    result.put("email", nguoiDung.getEmail());
                    result.put("isDeleted", nguoiDung.getIsDeleted());
                    result.put("vaiTro", nguoiDung.getVaiTro());
                    result.put("maNguoiDung", nguoiDung.getMaNguoiDung());
                    result.put("accountType", getAccountType(nguoiDung.getMaNguoiDung()));
                    
                    if (nguoiDung.getIsDeleted()) {
                        result.put("message", "OAuth2 ID đã tồn tại nhưng tài khoản đã bị xóa");
                    } else {
                        result.put("message", "OAuth2 ID đã tồn tại trong hệ thống");
                    }
                } else {
                    result.put("message", "OAuth2 ID chưa tồn tại trong hệ thống");
                }
                
                return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Kiểm tra OAuth2 ID thành công")
                        .result(result)
                        .build());
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.<Map<String, Object>>builder()
                                .success(false)
                                .message("Lỗi kiểm tra OAuth2 ID: " + e.getMessage())
                                .build());
            }
        }
    

    
    /**
     * ⚠️ DEPRECATED: Sử dụng /api/auth/log-out thay thế
     * 
     * Endpoint này đã được chuyển sang AuthenticationController
     * để tối ưu hóa và tránh trùng lặp code.
     * 
     * ➡️ Vui lòng sử dụng: POST /api/auth/log-out
     */
    @Operation(
        summary = "⚠️ DEPRECATED - OAuth2 Logout", 
        description = """
            **🚨 ENDPOINT NÀY ĐÃ BỊ DEPRECATED**
            
            **Lý do:** Tối ưu hóa và tránh trùng lặp code
            
            **✅ Thay thế bằng:**
            ```
            POST /api/auth/log-out
            Authorization: Bearer {JWT_TOKEN}
            ```
            
            **Chức năng tương tự:**
            - 🚫 Blacklist JWT token
            - 👤 Hỗ trợ tài khoản thường
            - 🌐 Hỗ trợ Google OAuth2  
            - 📘 Hỗ trợ Facebook OAuth2
            - 🔄 Giữ nguyên OAuth2 session trong browser
            
            **⏰ Endpoint này sẽ bị xóa trong phiên bản tương lai**
            """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "301", description = "Chuyển hướng đến endpoint mới")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deprecatedLogout() {
        
        Map<String, Object> result = new HashMap<>();
        result.put("deprecated", true);
        result.put("status", "MOVED_PERMANENTLY");
        result.put("new_endpoint", "/api/auth/log-out");
        result.put("method", "POST");
        result.put("message", "Endpoint này đã được chuyển sang /api/auth/log-out");
        result.put("recommendation", "Vui lòng cập nhật code để sử dụng endpoint mới");
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(false)
                .message("⚠️ DEPRECATED: Sử dụng /api/auth/log-out thay thế")
                .result(result)
                .build();
        
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).body(response);
    }
    
    /**
     * 🎯 Get JWT Token - Lấy JWT token từ Google OAuth2 login
     */
    @Operation(
        summary = "🎯 Get JWT Token từ Google OAuth2", 
        description = """
            **Chức năng chính:** Lấy JWT token sau khi đăng nhập Google thành công
            
            **Flow hoạt động:**
            1. User đăng nhập Google OAuth2 thành công
            2. Gọi endpoint này để lấy JWT token
            3. JWT token có format: header.payload.signature (3 phần)
            4. Sử dụng JWT token này cho authentication và logout
            
            **JWT Token đặc điểm:**
            - ✅ Format: 3 phần cách nhau bởi dấu chấm
            - ✅ Tạo bởi JwtUtil.generateToken()
            - ✅ Chứa email, role, expiration
            - ✅ Có thể blacklist khi logout
            - ✅ Hoạt động với tất cả API endpoints
            
            **⚠️ Lưu ý:** Khác với OAuth2 Access Token (chỉ dùng để gọi Google API)
            """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "✅ Lấy JWT token thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "❌ Chưa đăng nhập OAuth2")
    })
    @GetMapping("/get-jwt-token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getJwtToken(
            @Parameter(description = "OAuth2 User từ Google login")
            @AuthenticationPrincipal OAuth2User oauth2User) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (oauth2User == null) {
            result.put("error", "Chưa đăng nhập Google OAuth2");
            result.put("message", "Vui lòng đăng nhập Google trước khi lấy JWT token");
            result.put("login_url", "/oauth2/authorization/google");
            
            return ResponseEntity.status(401).body(
                ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("❌ Chưa đăng nhập Google OAuth2!")
                    .result(result)
                    .build()
            );
        }
        
        try {
            // Xác định provider từ OAuth2 attributes
            Map<String, Object> attributes = oauth2User.getAttributes();
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String sub = (String) attributes.get("sub");
            
            // Kiểm tra đây có phải Google OAuth2 không
            if (!attributes.containsKey("sub") || !attributes.containsKey("email_verified")) {
                result.put("error", "Không phải Google OAuth2");
                result.put("message", "Endpoint này chỉ hỗ trợ Google OAuth2 login");
                
                return ResponseEntity.badRequest().body(
                    ApiResponse.<Map<String, Object>>builder()
                        .success(false)
                        .message("❌ Chỉ hỗ trợ Google OAuth2!")
                        .result(result)
                        .build()
                );
            }
            
            System.out.println("🔐 === GETTING JWT TOKEN FROM GOOGLE OAUTH2 ===");
            System.out.println("🔐 Google Email: " + email);
            System.out.println("🔐 Google Name: " + name);
            System.out.println("🔐 Google Sub: " + sub);
            
            // Tạo JWT token qua OAuth2Service → JwtUtil
            AuthenticationResponse authResponse = oAuth2Service.processGoogleLogin(oauth2User);
            
            if (!authResponse.isAuthenticated()) {
                result.put("error", "Google login failed");
                result.put("message", authResponse.getMessage());
                
                return ResponseEntity.badRequest().body(
                    ApiResponse.<Map<String, Object>>builder()
                        .success(false)
                        .message("❌ Google login thất bại!")
                        .result(result)
                        .build()
                );
            }
            
            String jwtToken = authResponse.getToken();
            
            // Phân tích JWT token chi tiết
            String[] tokenParts = jwtToken.split("\\.");
            boolean isValidJWT = tokenParts.length == 3;
            
            System.out.println("🎫 JWT Token created: " + jwtToken);
            System.out.println("🎫 JWT Parts: " + tokenParts.length);
            System.out.println("🎫 Is valid JWT format: " + isValidJWT);
            
            // Build response
            result.put("provider", "Google");
            result.put("oauth2_user", Map.of(
                "email", email,
                "name", name,
                "sub", sub,
                "email_verified", attributes.get("email_verified")
            ));
            
            // JWT Token info
            result.put("jwt_token", jwtToken);
            result.put("jwt_format", Map.of(
                "parts", tokenParts.length,
                "header", tokenParts.length > 0 ? tokenParts[0] : null,
                "payload", tokenParts.length > 1 ? tokenParts[1] : null,
                "signature", tokenParts.length > 2 ? tokenParts[2] : null,
                "is_valid_format", isValidJWT
            ));
            
            // Auth info
            result.put("auth_info", Map.of(
                "user", authResponse.getUser(),
                "role", authResponse.getRole(),
                "authenticated", authResponse.isAuthenticated(),
                "message", authResponse.getMessage()
            ));
            
            // Instructions
            result.put("instructions", Map.of(
                "how_to_use", "Sử dụng jwt_token cho Authorization header",
                "format", "Authorization: Bearer " + jwtToken.substring(0, 20) + "...",
                "logout_endpoint", "POST /api/auth/log-out",
                "valid_format", isValidJWT ? "✅ JWT có 3 phần - có thể blacklist" : "❌ Không đúng JWT format"
            ));
            
            if (isValidJWT) {
                result.put("status", "✅ SUCCESS");
                result.put("message", "JWT token được tạo thành công từ Google OAuth2");
            } else {
                result.put("status", "❌ ERROR");
                result.put("message", "JWT token KHÔNG đúng format!");
            }
            
            System.out.println("🔐 === END GETTING JWT TOKEN ===");
            
            return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("🎯 Lấy JWT token từ Google OAuth2 thành công")
                    .result(result)
                    .build()
            );
            
        } catch (Exception e) {
            System.err.println("❌ Error getting JWT token: " + e.getMessage());
            e.printStackTrace();
            
            result.put("error", e.getMessage());
            result.put("status", "❌ EXCEPTION");
            result.put("stack_trace", e.getStackTrace());
            
            return ResponseEntity.internalServerError().body(
                ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("❌ Lỗi lấy JWT token: " + e.getMessage())
                    .result(result)
                    .build()
            );
        }
    }
    

    
    /**
     * Xác định loại tài khoản dựa trên mã người dùng
     */
    private String getAccountType(String maNguoiDung) {
        if (maNguoiDung == null) {
            return "UNKNOWN";
        }
        // Kiểm tra dựa vào format maNguoiDung mới (ND + 8 ký tự)
        if (maNguoiDung.matches("^ND[A-Z0-9]{8}$")) {
            return "OAUTH2_ACCOUNT";
        } else {
            return "REGULAR_ACCOUNT";
        }
    }
} 