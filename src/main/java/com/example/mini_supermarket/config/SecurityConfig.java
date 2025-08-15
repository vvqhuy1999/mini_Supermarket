package com.example.mini_supermarket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "spring.security.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig {
    
    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;
    
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    
    @Value("${oauth2.frontend.base-url:http://localhost:3000}")
    private String frontendBaseUrl;
    
    @Value("${oauth2.frontend.success-path:/oauth2/success}")
    private String frontendSuccessPath;
    
    @Value("${oauth2.frontend.failure-path:/login?error=oauth2_failed}")
    private String frontendFailurePath;
    
    // ===== CẤU HÌNH BẢO MẬT CHO TESTING =====
    // 
    // ⚠️ LƯU Ý: Đây là cấu hình để TEST - TẤT CẢ API ĐỀU ĐƯỢC MỞ
    // 
    // 🔓 QUY TẮC PHÂN QUYỀN:
    // 1. PUBLIC_ENDPOINTS: Tất cả API chính đều được mở (permitAll)
    // 2. anyRequest(): Tất cả request khác cũng được mở (permitAll)
    // 3. Không có kiểm tra role hay authentication
    // 
    // 📋 DANH SÁCH API ĐÃ MỞ:
    // - Tất cả controller trong package rest.controller
    // - Swagger & API Documentation
    // - OAuth2 & Authentication
    // - Core Business APIs (sản phẩm, khách hàng, nhân viên, v.v.)
    // - Order Management (đơn hàng, chi tiết đơn hàng)
    // - Inventory & Stock Management
    // - Shopping Cart & Order Details
    // - Promotions & Marketing
    // - Work Management
    // - Media & Images
    // - Reports & Statistics
    // - Health check & Monitoring
    //
    // ⚠️ CẢNH BÁO: Chỉ sử dụng cấu hình này để TEST, KHÔNG dùng cho PRODUCTION!
    private final String[] PUBLIC_ENDPOINTS = {
        // Swagger & API Documentation
        "/swagger-ui/**", 
        "/swagger-ui.html", 
        "/api-docs/**", 
        "/v3/api-docs/**",
        
        // OAuth2 & Authentication
        "/api/nguoidung/**",
        "/api/auth/**",
        "/api/oauth2/**",
        "/api/oauth2-test/**",
        "/oauth2/**",
        "/oauth2/authorization/**",
        "/login/oauth2/code/**",
        
        // Core Business APIs
        "/api/sanpham/**",              // Quản lý sản phẩm
        "/api/loaisanpham/**",          // Quản lý loại sản phẩm
        "/api/khachhang/**",            // Quản lý khách hàng
        "/api/nhanvien/**",             // Quản lý nhân viên
        "/api/nhacungcap/**",           // Quản lý nhà cung cấp
        "/api/cuahang/**",              // Quản lý cửa hàng
        "/api/kho/**",                  // Quản lý kho
        "/api/giohang/**",              // Quản lý giỏ hàng
        "/api/hoadon/**",               // Quản lý hóa đơn
        "/api/thanhtoan/**",            // Quản lý thanh toán
        "/api/phuongthucthanhtoan/**",  // Quản lý phương thức thanh toán
        
        // Order Management APIs
        "/api/donhang/**",              // Quản lý đơn hàng
        "/api/chitietdonhang/**",       // Quản lý chi tiết đơn hàng
        
        // Inventory & Stock Management
        "/api/phieunhaphang/**",        // Quản lý phiếu nhập hàng
        "/api/phieuxuatkho/**",         // Quản lý phiếu xuất kho
        "/api/chitietphieunhap/**",     // Quản lý chi tiết phiếu nhập
        "/api/chitietphieuxuat/**",     // Quản lý chi tiết phiếu xuất
        "/api/tonkhochitiet/**",        // Quản lý tồn kho chi tiết
        "/api/giasanpham/**",           // Quản lý giá sản phẩm
        
        // Shopping Cart & Order Details
        "/api/chitietgiohang/**",       // Quản lý chi tiết giỏ hàng
        "/api/chitiethoadon/**",        // Quản lý chi tiết hóa đơn
        
        // Promotions & Marketing
        "/api/khuyenmai/**",            // Quản lý khuyến mãi
        "/api/khuyenmaisanpham/**",     // Quản lý khuyến mãi sản phẩm
        "/api/khuyenmaikhachhang/**",   // Quản lý khuyến mãi khách hàng
        
        // Work Management
        "/api/calamviec/**",            // Quản lý ca làm việc
        "/api/lichlamviec/**",          // Quản lý lịch làm việc
        "/api/bangluong/**",            // Quản lý bảng lương
        
        // Media & Images
        "/api/hinhanh/**",              // Quản lý hình ảnh
        "/api/upload/**",               // API upload ảnh sản phẩm
        "/images/**",                   // Truy cập ảnh từ uploads/images
        "/uploads/**",                  // Truy cập trực tiếp từ thư mục uploads
        
        // Reports & Statistics
        "/api/thongkebaocao/**",        // Quản lý thống kê báo cáo
        
        // Health check & Monitoring
        "/actuator/**",                 // Spring Boot Actuator
        "/health",                      // Health check endpoint
        "/info"                         // Application info
    };
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Độ mạnh 12
    }
    

    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Tắt CSRF cho API REST
            .csrf(AbstractHttpConfigurer::disable)
            
            // Tắt HTTP Basic Authentication
            .httpBasic(AbstractHttpConfigurer::disable)
            
            // Tắt form login
            .formLogin(AbstractHttpConfigurer::disable)
            
            // Tắt logout
            .logout(AbstractHttpConfigurer::disable)
            
            // Cấu hình CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Cấu hình OAuth2 - Simple Google login
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2SuccessHandler)
                .failureUrl(frontendBaseUrl + frontendFailurePath)
            )
           
            
            // Cấu hình authorization - MỞ TẤT CẢ API ĐỂ TEST
            .authorizeHttpRequests(authz -> authz
                // Tất cả API đều được mở để test
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                
                // Mở tất cả request khác để test
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Cho phép frontend domain và backend domain
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",    // Frontend (React/Vue/Angular)
            "http://localhost:8080",    // Backend (Spring Boot)
            "http://127.0.0.1:3000",    // Alternative localhost
            "http://127.0.0.1:8080",    // Alternative localhost
            frontendBaseUrl             // Dynamic frontend URL từ config
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 