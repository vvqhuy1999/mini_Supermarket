package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.dto.AuthenticationResponse;
import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.repository.NguoiDungRepository;
import com.example.mini_supermarket.service.OAuth2Service;
import com.example.mini_supermarket.service.UserService;
import com.example.mini_supermarket.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
public class OAuth2ServiceImpl implements OAuth2Service {
    
    private final NguoiDungRepository nguoiDungRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;
    
    @Value("${spring.security.oauth2.client.registration.facebook.client-id:}")
    private String facebookClientId;
    
    @Autowired
    public OAuth2ServiceImpl(NguoiDungRepository nguoiDungRepository, UserService userService, JwtUtil jwtUtil) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    public AuthenticationResponse processGoogleLogin(OAuth2User oauth2User) {
        try {
            Map<String, Object> attributes = oauth2User.getAttributes();
            
            // Lấy thông tin từ Google
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String picture = (String) attributes.get("picture");
            String sub = (String) attributes.get("sub"); // Google user ID
            
            if (email == null || email.trim().isEmpty()) {
                AuthenticationResponse response = new AuthenticationResponse();
                response.setAuthenticated(false);
                response.setMessage("Email không được cung cấp từ Google!");
                return response;
            }
            
            // Chuẩn hóa email
            email = email.trim().toLowerCase();
            
            // VẤN ĐỀ 2: Kiểm tra CẢ sub VÀ email trong database
            Optional<NguoiDung> nguoiDungByEmail = nguoiDungRepository.findByEmail(email);
            Optional<NguoiDung> nguoiDungBySub = nguoiDungRepository.findBySub(sub);
            
            NguoiDung nguoiDung;
            boolean isNewUser = false;
            
            // Logic kiểm tra: ưu tiên sub trước, sau đó đến email
            if (nguoiDungBySub.isPresent()) {
                // Đã tồn tại tài khoản với sub này -> đăng nhập
                nguoiDung = nguoiDungBySub.get();
                System.out.println("🔑 Tìm thấy tài khoản theo sub: " + sub + " cho email: " + email);
                
                // Kiểm tra tài khoản có bị xóa không
                if (nguoiDung.getIsDeleted()) {
                    AuthenticationResponse response = new AuthenticationResponse();
                    response.setAuthenticated(false);
                    response.setMessage("Tài khoản đã bị xóa!");
                    return response;
                }
                
            } else if (nguoiDungByEmail.isPresent()) {
                // Có tài khoản với email này nhưng chưa có sub -> cập nhật sub cho tài khoản thường
                nguoiDung = nguoiDungByEmail.get();
                System.out.println("📧 Tìm thấy tài khoản theo email: " + email + ", cập nhật sub: " + sub);
                
                // Kiểm tra tài khoản có bị xóa không
                if (nguoiDung.getIsDeleted()) {
                    AuthenticationResponse response = new AuthenticationResponse();
                    response.setAuthenticated(false);
                    response.setMessage("Tài khoản đã bị xóa!");
                    return response;
                }
                
                // Cập nhật sub cho tài khoản thường để liên kết với Google
                nguoiDung.setSub(sub);
                nguoiDungRepository.save(nguoiDung);
                System.out.println("🔗 Liên kết tài khoản thường với Google OAuth2");
                
            } else {
                // VẤN ĐỀ 1: Tài khoản hoàn toàn mới -> TỰ ĐỘNG ĐĂNG KÝ
                nguoiDung = new NguoiDung();
                nguoiDung.setEmail(email);
                nguoiDung.setVaiTro(3); // Mặc định là khách hàng (3)
                nguoiDung.setSub(sub); // Lưu Google ID vào trường sub
                
                // Tạo mật khẩu ngẫu nhiên cho OAuth2 user
                String randomPassword = UUID.randomUUID().toString();
                nguoiDung.setMatKhau(randomPassword); // UserService sẽ tự động mã hóa
                
                // Sử dụng UserService để đăng ký (tự động tạo maNguoiDung)
                nguoiDung = userService.registerUser(nguoiDung);
                isNewUser = true;
                
                System.out.println("✅ TỰ ĐỘNG ĐĂNG KÝ tài khoản mới cho Google OAuth2: " + email + " với sub: " + sub + " và maNguoiDung: " + nguoiDung.getMaNguoiDung());
            }
            
            // VẤN ĐỀ 3: Sử dụng JwtUtil để tạo token thật
            String role = getRoleName(nguoiDung.getVaiTro());
            String token = jwtUtil.generateToken(email, role);
            
            String message = isNewUser ? 
                    "Đăng nhập Google thành công! Tài khoản mới đã được tạo." : 
                    "Đăng nhập Google thành công!";
            
            AuthenticationResponse response = new AuthenticationResponse();
            response.setAuthenticated(true);
            response.setMessage(message);
            response.setUser(nguoiDung);
            response.setRole(role);
            response.setToken(token);
            return response;
                    
        } catch (Exception e) {
            System.err.println("Google OAuth2 Error: " + e.getMessage());
            e.printStackTrace();
            
            AuthenticationResponse response = new AuthenticationResponse();
            response.setAuthenticated(false);
            response.setMessage("Lỗi xử lý đăng nhập Google: " + e.getMessage());
            return response;
        }
    }
    
    @Override
    public AuthenticationResponse processFacebookLogin(OAuth2User oauth2User) {
        try {
            Map<String, Object> attributes = oauth2User.getAttributes();
            
            // Debug: Log tất cả attributes để kiểm tra
            System.out.println("Facebook OAuth2 Attributes: " + attributes);
            
            // Lấy thông tin từ Facebook - thử nhiều cách khác nhau
            String email = null;
            String name = null;
            String id = null;
            
            // Thử lấy email từ các trường khác nhau
            if (attributes.get("email") != null) {
                email = (String) attributes.get("email");
            } else if (attributes.get("mail") != null) {
                email = (String) attributes.get("mail");
            }
            
            // Thử lấy name từ các trường khác nhau
            if (attributes.get("name") != null) {
                name = (String) attributes.get("name");
            } else if (attributes.get("displayName") != null) {
                name = (String) attributes.get("displayName");
            }
            
            // Thử lấy ID từ các trường khác nhau
            if (attributes.get("id") != null) {
                id = attributes.get("id").toString();
            } else if (attributes.get("sub") != null) {
                id = attributes.get("sub").toString();
            }
            
            // Nếu không có email, thử tạo email từ ID
            if (email == null || email.trim().isEmpty()) {
                if (id != null) {
                    email = "fb_" + id + "@facebook.com";
                } else {
                    AuthenticationResponse response = new AuthenticationResponse();
                    response.setAuthenticated(false);
                    response.setMessage("Không thể lấy thông tin email từ Facebook! Vui lòng kiểm tra quyền truy cập email.");
                    return response;
                }
            }
            
            // Chuẩn hóa email
            email = email.trim().toLowerCase();
            
            // VẤN ĐỀ 2: Kiểm tra CẢ sub VÀ email trong database
            Optional<NguoiDung> nguoiDungByEmail = nguoiDungRepository.findByEmail(email);
            Optional<NguoiDung> nguoiDungBySub = nguoiDungRepository.findBySub(id);
            
            NguoiDung nguoiDung;
            boolean isNewUser = false;
            
            // Logic kiểm tra: ưu tiên sub trước, sau đó đến email
            if (nguoiDungBySub.isPresent()) {
                // Đã tồn tại tài khoản với sub này -> đăng nhập
                nguoiDung = nguoiDungBySub.get();
                System.out.println("🔑 Tìm thấy tài khoản theo sub: " + id + " cho email: " + email);
                
                // Kiểm tra tài khoản có bị xóa không
                if (nguoiDung.getIsDeleted()) {
                    AuthenticationResponse response = new AuthenticationResponse();
                    response.setAuthenticated(false);
                    response.setMessage("Tài khoản đã bị xóa!");
                    return response;
                }
                
            } else if (nguoiDungByEmail.isPresent()) {
                // Có tài khoản với email này nhưng chưa có sub -> cập nhật sub cho tài khoản thường
                nguoiDung = nguoiDungByEmail.get();
                System.out.println("📧 Tìm thấy tài khoản theo email: " + email + ", cập nhật sub: " + id);
                
                // Kiểm tra tài khoản có bị xóa không
                if (nguoiDung.getIsDeleted()) {
                    AuthenticationResponse response = new AuthenticationResponse();
                    response.setAuthenticated(false);
                    response.setMessage("Tài khoản đã bị xóa!");
                    return response;
                }
                
                // Cập nhật sub cho tài khoản thường để liên kết với Facebook
                nguoiDung.setSub(id);
                nguoiDungRepository.save(nguoiDung);
                System.out.println("🔗 Liên kết tài khoản thường với Facebook OAuth2");
                
            } else {
                // VẤN ĐỀ 1: Tài khoản hoàn toàn mới -> TỰ ĐỘNG ĐĂNG KÝ
                nguoiDung = new NguoiDung();
                nguoiDung.setEmail(email);
                nguoiDung.setVaiTro(3); // Mặc định là khách hàng (3)
                nguoiDung.setSub(id); // Lưu Facebook ID vào trường sub
                
                // Tạo mật khẩu ngẫu nhiên cho OAuth2 user
                String randomPassword = UUID.randomUUID().toString();
                nguoiDung.setMatKhau(randomPassword); // UserService sẽ tự động mã hóa
                
                // Sử dụng UserService để đăng ký (tự động tạo maNguoiDung)
                nguoiDung = userService.registerUser(nguoiDung);
                isNewUser = true;
                
                System.out.println("✅ TỰ ĐỘNG ĐĂNG KÝ tài khoản mới cho Facebook OAuth2: " + email + " với sub: " + id + " và maNguoiDung: " + nguoiDung.getMaNguoiDung());
            }
            
            // VẤN ĐỀ 3: Sử dụng JwtUtil để tạo token thật
            String role = getRoleName(nguoiDung.getVaiTro());
            String token = jwtUtil.generateToken(email, role);
            
            String message = isNewUser ? 
                    "Đăng nhập Facebook thành công! Tài khoản mới đã được tạo." : 
                    "Đăng nhập Facebook thành công!";
            
            AuthenticationResponse response = new AuthenticationResponse();
            response.setAuthenticated(true);
            response.setMessage(message);
            response.setUser(nguoiDung);
            response.setRole(role);
            response.setToken(token);
            return response;
                    
        } catch (Exception e) {
            System.err.println("Facebook OAuth2 Error: " + e.getMessage());
            e.printStackTrace();
            
            AuthenticationResponse response = new AuthenticationResponse();
            response.setAuthenticated(false);
            response.setMessage("Lỗi xử lý đăng nhập Facebook: " + e.getMessage());
            return response;
        }
    }
    
    @Override
    public Map<String, String> getOAuth2Configuration() {
        Map<String, String> config = new HashMap<>();
        
        // Kiểm tra cấu hình Google
        boolean googleConfigured = googleClientId != null && !googleClientId.trim().isEmpty();
        config.put("google_configured", String.valueOf(googleConfigured));
        config.put("google_client_id", googleClientId != null ? googleClientId : "not_configured");
        
        // Kiểm tra cấu hình Facebook
        boolean facebookConfigured = facebookClientId != null && !facebookClientId.trim().isEmpty();
        config.put("facebook_configured", String.valueOf(facebookConfigured));
        config.put("facebook_client_id", facebookClientId != null ? facebookClientId : "not_configured");
        
        config.put("message", "OAuth2 endpoints are available");
        config.put("status", "OAuth2 Controller is working");
        config.put("google_redirect_uri", "http://localhost:8080/login/oauth2/code/google");
        config.put("facebook_redirect_uri", "http://localhost:8080/login/oauth2/code/facebook");
        
        return config;
    }
    
    /**
     * Chuyển đổi vai trò từ database sang role name
     */
    private String getRoleName(Integer vaiTro) {
        if (vaiTro == null) {
            return "KHACH_HANG";
        }
        
        switch (vaiTro) {
            case 0:
                return "ADMIN";
            case 1:
                return "QUAN_LY";
            case 2:
                return "NHAN_VIEN";
            case 3:
            default:
                return "KHACH_HANG";
        }
    }
} 