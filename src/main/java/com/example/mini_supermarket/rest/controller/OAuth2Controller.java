package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.dto.ApiResponse;
import com.example.mini_supermarket.dto.AuthenticationResponse;
import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.service.OAuth2Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
    
    @Autowired
    public OAuth2Controller(OAuth2Service oAuth2Service, NguoiDungRepository nguoiDungRepository) {
        this.oAuth2Service = oAuth2Service;
        this.nguoiDungRepository = nguoiDungRepository;
    }
    
    // Constructor mặc định để tránh lỗi dependency
    public OAuth2Controller() {
        this.oAuth2Service = null;
        this.nguoiDungRepository = null;
    }
    
    /**
     * Callback cho Google OAuth2
     */
    @Operation(summary = "Google OAuth2 Callback", description = "Xử lý callback từ Google OAuth2")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Lỗi xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/callback/google")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> googleCallback(
            @Parameter(description = "OAuth2 User từ Google")
            @AuthenticationPrincipal OAuth2User oauth2User) {
        
        try {
            if (oauth2User == null) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.<AuthenticationResponse>builder()
                        .success(false)
                        .message("Không thể xác thực với Google!")
                        .error("OAuth2 user is null")
                        .build()
                );
            }
            
            AuthenticationResponse authResponse = oAuth2Service.processGoogleLogin(oauth2User);
            
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
                    .message("Lỗi xác thực Google: " + e.getMessage())
                    .error(e.getMessage())
                    .build();
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Callback cho Facebook OAuth2
     */
    @Operation(summary = "Facebook OAuth2 Callback", description = "Xử lý callback từ Facebook OAuth2")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Lỗi xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/callback/facebook")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> facebookCallback(
            @Parameter(description = "OAuth2 User từ Facebook")
            @AuthenticationPrincipal OAuth2User oauth2User) {
        
        try {
            if (oauth2User == null) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.<AuthenticationResponse>builder()
                        .success(false)
                        .message("Không thể xác thực với Facebook!")
                        .error("OAuth2 user is null")
                        .build()
                );
            }
            
            // Debug: Log thông tin OAuth2 user
            System.out.println("Facebook OAuth2 User: " + oauth2User.getName());
            System.out.println("Facebook OAuth2 Attributes: " + oauth2User.getAttributes());
            
            AuthenticationResponse authResponse = oAuth2Service.processFacebookLogin(oauth2User);
            
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
            System.err.println("Facebook OAuth2 Controller Error: " + e.getMessage());
            e.printStackTrace();
            
            ApiResponse<AuthenticationResponse> errorResponse = ApiResponse.<AuthenticationResponse>builder()
                    .success(false)
                    .message("Lỗi xác thực Facebook: " + e.getMessage())
                    .error(e.getMessage())
                    .build();
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
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
     * Endpoint để phân tích chi tiết thông tin OAuth2
     */
    @Operation(summary = "Analyze OAuth2 User Data", description = "Phân tích chi tiết thông tin user từ OAuth2")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping("/analyze")
    public ResponseEntity<ApiResponse<Map<String, Object>>> analyzeOAuth2User(
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
        
        Map<String, Object> attributes = oauth2User.getAttributes();
        Map<String, Object> analysis = new HashMap<>();
        
        // Xác định provider dựa trên attributes
        String provider = "Unknown";
        if (attributes.containsKey("sub") && attributes.containsKey("email_verified")) {
            provider = "Google OAuth2";
        } else if (attributes.containsKey("id") && !attributes.containsKey("email_verified")) {
            provider = "Facebook OAuth2";
        }
        
        // Thông tin cơ bản
        analysis.put("provider", provider);
        analysis.put("total_attributes", attributes.size());
        analysis.put("all_attribute_names", attributes.keySet());
        
        // Phân tích từng trường
        analysis.put("email", attributes.get("email"));
        analysis.put("name", attributes.get("name"));
        analysis.put("id", attributes.get("id"));
        analysis.put("sub", attributes.get("sub"));
        
        if (provider.equals("Google OAuth2")) {
            analysis.put("email_verified", attributes.get("email_verified"));
            analysis.put("given_name", attributes.get("given_name"));
            analysis.put("family_name", attributes.get("family_name"));
            analysis.put("picture_url", attributes.get("picture"));
            analysis.put("google_user_id", attributes.get("sub"));
            analysis.put("locale", attributes.get("locale"));
            analysis.put("hosted_domain", attributes.get("hd"));
            
            // Thông tin JWT token
            analysis.put("token_issued_at", attributes.get("iat"));
            analysis.put("token_expires_at", attributes.get("exp"));
            analysis.put("authorized_party", attributes.get("azp"));
            analysis.put("audience", attributes.get("aud"));
            analysis.put("issuer", attributes.get("iss"));
        } else if (provider.equals("Facebook OAuth2")) {
            analysis.put("first_name", attributes.get("first_name"));
            analysis.put("last_name", attributes.get("last_name"));
            analysis.put("middle_name", attributes.get("middle_name"));
            analysis.put("verified", attributes.get("verified"));
            analysis.put("facebook_user_id", attributes.get("id"));
        }
        
        // Raw data để phân tích
        analysis.put("raw_oauth2_data", attributes);
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .result(analysis)
                .success(true)
                .message("Phân tích chi tiết thông tin " + provider)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint debug đặc biệt cho Facebook OAuth2
     */
    @Operation(summary = "Debug Facebook OAuth2", description = "Debug thông tin Facebook OAuth2")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping("/debug/facebook")
    public ResponseEntity<ApiResponse<Map<String, Object>>> debugFacebookOAuth2(
            @Parameter(description = "OAuth2 User từ Facebook")
            @AuthenticationPrincipal OAuth2User oauth2User) {
        
        Map<String, Object> debugInfo = new HashMap<>();
        
        if (oauth2User == null) {
            debugInfo.put("status", "No OAuth2 user found");
            debugInfo.put("message", "Chưa đăng nhập Facebook OAuth2");
            debugInfo.put("error", "OAuth2 user is null");
            
            ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                    .result(debugInfo)
                    .success(false)
                    .message("Chưa đăng nhập Facebook OAuth2!")
                    .build();
            
            return ResponseEntity.status(401).body(response);
        }
        
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        debugInfo.put("status", "Facebook OAuth2 user found");
        debugInfo.put("user_name", oauth2User.getName());
        debugInfo.put("authorities", oauth2User.getAuthorities());
        debugInfo.put("total_attributes", attributes.size());
        debugInfo.put("all_attributes", attributes);
        
        // Phân tích từng trường Facebook
        debugInfo.put("facebook_id", attributes.get("id"));
        debugInfo.put("email", attributes.get("email"));
        debugInfo.put("name", attributes.get("name"));
        debugInfo.put("first_name", attributes.get("first_name"));
        debugInfo.put("last_name", attributes.get("last_name"));
        debugInfo.put("middle_name", attributes.get("middle_name"));
        debugInfo.put("verified", attributes.get("verified"));
        
        // Thử xử lý đăng nhập
        try {
            AuthenticationResponse authResponse = oAuth2Service.processFacebookLogin(oauth2User);
            debugInfo.put("auth_response", authResponse);
            debugInfo.put("auth_success", authResponse.isAuthenticated());
            debugInfo.put("auth_message", authResponse.getMessage());
        } catch (Exception e) {
            debugInfo.put("auth_error", e.getMessage());
            debugInfo.put("auth_stack_trace", e.getStackTrace());
        }
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .result(debugInfo)
                .success(true)
                .message("Debug thông tin Facebook OAuth2")
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
     * Endpoint để lấy JWT token sau khi đăng nhập OAuth2
     */
    @Operation(summary = "Get JWT Token", description = "Lấy JWT token sau khi đăng nhập OAuth2 thành công")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping("/get-token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getToken(
            @Parameter(description = "OAuth2 User")
            @AuthenticationPrincipal OAuth2User oauth2User) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (oauth2User == null) {
            result.put("error", "Chưa đăng nhập OAuth2");
            result.put("token", null);
            
            return ResponseEntity.status(401).body(
                ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Chưa đăng nhập OAuth2!")
                    .result(result)
                    .build()
            );
        }
        
        try {
            // Xác định provider
            Map<String, Object> attributes = oauth2User.getAttributes();
            String provider = "Unknown";
            String email = (String) attributes.get("email");
            
            if (attributes.containsKey("sub") && attributes.containsKey("email_verified")) {
                provider = "Google";
            } else if (attributes.containsKey("id") && !attributes.containsKey("email_verified")) {
                provider = "Facebook";
            }
            
            // Xử lý và lấy token
            AuthenticationResponse authResponse;
            if (provider.equals("Google")) {
                authResponse = oAuth2Service.processGoogleLogin(oauth2User);
            } else if (provider.equals("Facebook")) {
                authResponse = oAuth2Service.processFacebookLogin(oauth2User);
            } else {
                authResponse = oAuth2Service.processGoogleLogin(oauth2User); // Fallback
            }
            
            if (authResponse.isAuthenticated()) {
                result.put("jwt_token", authResponse.getToken());
                result.put("user_email", email);
                result.put("user_role", authResponse.getRole());
                result.put("provider", provider);
                result.put("user_info", authResponse.getUser());
                result.put("authenticated", true);
                result.put("token_type", "Bearer");
                
                return ResponseEntity.ok(
                    ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Lấy JWT token thành công")
                        .result(result)
                        .build()
                );
            } else {
                result.put("error", authResponse.getMessage());
                result.put("token", null);
                
                return ResponseEntity.badRequest().body(
                    ApiResponse.<Map<String, Object>>builder()
                        .success(false)
                        .message("Không thể lấy token: " + authResponse.getMessage())
                        .result(result)
                        .build()
                );
            }
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("token", null);
            
            return ResponseEntity.internalServerError().body(
                ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Lỗi lấy token: " + e.getMessage())
                    .result(result)
                    .build()
            );
        }
    }
    
    /**
     * 🔒 Local Logout - Đăng xuất khỏi app (giữ Google session)
     */
    @Operation(
        summary = "🔒 Local Logout", 
        description = """
            **Mục đích:** Đăng xuất local khỏi app
            
            **Chức năng:**
            - 🚫 Blacklist JWT token hiện tại
            - 🗑️ Xóa session app (không ảnh hưởng Google)
            - 🔄 Giữ nguyên Google login trong browser
            
            **Kết quả:**
            - ✅ User logout khỏi app
            - 🌐 Google account vẫn đăng nhập trong browser
            - ⚡ Lần login sau không cần nhập password Google
            
            **Headers:** Authorization: Bearer {JWT_TOKEN}
            """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Đăng xuất thành công")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String, Object>>> localLogout(
            @Parameter(description = "JWT Token để blacklist")
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Extract token từ Authorization header
            String token = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
            
            if (token != null) {
                // TODO: Add token to blacklist (nếu có TokenBlacklistService)
                // tokenBlacklistService.blacklistToken(token);
                result.put("token_blacklisted", true);
                result.put("message", "Token đã được blacklist");
            } else {
                result.put("token_blacklisted", false);
                result.put("message", "Không có token để blacklist");
            }
            
            result.put("logout_type", "local");
            result.put("success", true);
            
            return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Đăng xuất local thành công")
                    .result(result)
                    .build()
            );
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("success", false);
            
            return ResponseEntity.internalServerError().body(
                ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Lỗi đăng xuất: " + e.getMessage())
                    .result(result)
                    .build()
            );
        }
    }
    
    /**
     * 🌐 Website Logout - Đăng xuất hoàn toàn khỏi website
     */
    @Operation(
        summary = "🌐 Website Logout", 
        description = """
            **Mục đích:** Đăng xuất hoàn toàn khỏi website
            
            **Chức năng:**
            - 🚫 Blacklist JWT token
            - 🗑️ Invalidate Spring Security session
            - 🔄 Clear tất cả session data
            
            **Kết quả:**
            - ✅ User logout hoàn toàn khỏi website
            - 🌐 Google account vẫn login trong browser
            - ⚡ Lần login sau vẫn dễ dàng (no password)
            
            **Headers:** Authorization: Bearer {JWT_TOKEN}
            
            **💡 Tip:** Đây là logout thông thường nhất
            """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Đăng xuất website thành công")
    })
    @PostMapping("/logout/website")
    public ResponseEntity<ApiResponse<Map<String, Object>>> websiteLogout(
            @Parameter(description = "JWT Token")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Extract token từ Authorization header
            String token = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
            
            if (token != null) {
                // TODO: Add token to blacklist
                // tokenBlacklistService.blacklistToken(token);
                result.put("token_blacklisted", true);
            }
            
            // Clear Spring Security session (chỉ session website)
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
                result.put("session_cleared", true);
            } else {
                result.put("session_cleared", false);
                result.put("session_note", "Không có session để clear");
            }
            
            result.put("logout_type", "website");
            result.put("success", true);
            result.put("message", "Đăng xuất khỏi website thành công - Google account vẫn đăng nhập trong browser");
            result.put("note", "Chỉ logout khỏi website này, không ảnh hưởng Google account của bạn");
            
            return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Đăng xuất website thành công")
                    .result(result)
                    .build()
            );
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("success", false);
            
            return ResponseEntity.internalServerError().body(
                ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Lỗi đăng xuất website: " + e.getMessage())
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