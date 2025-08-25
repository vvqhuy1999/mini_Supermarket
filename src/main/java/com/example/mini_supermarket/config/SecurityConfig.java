
package com.example.mini_supermarket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "spring.security.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig {
    
    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;
    
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Value("${oauth2.frontend.customer.base-url:http://localhost:3000}")
    private String customerFrontendUrl;
    
    @Value("${oauth2.frontend.employee.base-url:http://localhost:5173}")
    private String employeeFrontendUrl;
    
    @Value("${oauth2.frontend.success-path:/oauth2/success}")
    private String frontendSuccessPath;
    
    @Value("${oauth2.frontend.failure-path:/login?error=oauth2_failed}")
    private String frontendFailurePath;
    
    // ===== CẤU HÌNH ENDPOINT ĐÃ SỬA - PHÂN TÁCH RÕ RÀNG =====
    
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
        
        // Quên mật khẩu và OTP
        "/api/forgot-password/**",
        
        // API sản phẩm công khai - CHỈ ĐỌC
        "/api/sanpham",
        "/api/sanpham/*/",  // SỬA: thêm / ở cuối để tránh xung đột với /api/sanpham/**
        "/api/sanpham/optimized",
        "/api/sanpham/category/**",
        "/api/sanpham/with-tonkho",
        "/api/sanpham/*/with-tonkho",
        "/api/sanpham/search/**",
        "/api/loaisanpham",
        "/api/loaisanpham/*/",  // SỬA: tương tự
        "/api/khuyenmai",
        "/api/giasanpham",
        "/api/giasanpham/*/",
        "/api/tonkhochitiet",
        "/api/tonkhochitiet/*/",
        
        // Endpoint khách hàng công khai
        "/api/khachhang/register",
        "/api/khachhang/by-email/*",
        "/api/nguoidung/email/*",
        
        // Media & Images
        "/api/hinhanh/**",
        "/api/upload/serve-image/**",
        "/api/upload/serve-image-by-id/**",
        "/api/upload/product-images/**",
        "/api/upload/product-image/**",
        "/api/upload/product-main-image/**",
        "/images/**",
        "/uploads/**",
        
        // VNPay callback
        "/api/thanhtoan/vnpay/return",
        "/api/phuongthucthanhtoan",
        
        // Health check
        "/actuator/health/**",
        "/health"
    };
    
    // ===== CÁC ENDPOINT CHỈ DÀNH CHO MANAGER - ƯU TIÊN CAO NHẤT =====
    private final String[] MANAGER_ONLY_ENDPOINTS = {
        "/api/nhanvien/**",
        "/api/cuahang/**",
        "/api/nhacungcap/**",
        "/api/kho/**",
        "/api/phieunhaphang/**",
        "/api/phieuxuatkho/**",
        "/api/chitietphieunhap/**",  // ✅ QUAN TRỌNG: API này cần role MANAGER
        "/api/chitietphieuxuat/**",
        "/api/khuyenmaisanpham/**",
        "/api/khuyenmaikhachhang/**",
        "/api/sanpham/**",           // ✅ QUAN TRỌNG: CRUD sản phẩm chỉ MANAGER
        "/api/loaisanpham/**",       // ✅ QUAN TRỌNG: CRUD loại sản phẩm chỉ MANAGER  
        "/api/upload/**",
        "/api/thongkebaocao/**"
    };
    
    // ===== CÁC ENDPOINT CHO CẢ EMPLOYEE VÀ MANAGER =====
    private final String[] EMPLOYEE_AND_MANAGER_ENDPOINTS = {
        // Quản lý khách hàng - CẢ EMPLOYEE VÀ MANAGER
        "/api/khachhang",
        "/api/khachhang/*/",  // SỬA: GET chi tiết khách hàng với / ở cuối
        
        // Quản lý hóa đơn - CẢ EMPLOYEE VÀ MANAGER
        "/api/hoadon/employee/**",
        "/api/chitiethoadon",
        "/api/chitiethoadon/*/",  // SỬA: thêm / để tránh xung đột
        
        // Quản lý thanh toán - CẢ EMPLOYEE VÀ MANAGER
        "/api/thanhtoan",
        "/api/thanhtoan/*/",  // SỬA: thêm / để tránh xung đột
        "/api/thanhtoan/employee/**",
        
        // Xem kho và giá - CẢ EMPLOYEE VÀ MANAGER (CHỈ ĐỌC)
        "/api/tonkhochitiet",
        "/api/tonkhochitiet/*/",
        "/api/giasanpham", 
        "/api/giasanpham/*/",
        
        // Khuyến mãi cơ bản - CẢ EMPLOYEE VÀ MANAGER (CHỈ ĐỌC)
        "/api/khuyenmai",
        "/api/khuyenmai/*/",  // SỬA: Không bao gồm /** để tránh xung đột với MANAGER_ONLY
        "/api/khuyenmai/coupon/**",
        
        // Quản lý ca làm việc cá nhân
        "/api/calamviec",
        "/api/calamviec/*/",
        "/api/lichlamviec",
        "/api/lichlamviec/*/",
        "/api/bangluong",
        "/api/bangluong/*/"
    };
    
    // ===== CÁC ENDPOINT CHỈ DÀNH CHO EMPLOYEE =====
    private final String[] EMPLOYEE_ONLY_ENDPOINTS = {
        // Hiện tại không có endpoint chỉ dành cho EMPLOYEE
        // Tất cả endpoint EMPLOYEE đều được MANAGER kế thừa
    };
    
    // ===== CÁC ENDPOINT DÀNH CHO CUSTOMER =====
    private final String[] CUSTOMER_ENDPOINTS = {
        "/api/thanhtoan/vnpay",
        "/api/giohang/**",
        "/api/khachhang/by-nguoidung/*",
        "/api/khachhang/*/shipping-info",
        "/api/hoadon/from-cart",
        "/api/hoadon/by-khachhang/**",
        "/api/hoadon/*/full-details",
        "/api/hoadon/status/*",
        "/api/hoadon/date-range",
        "/api/hoadon/optimized",
        "/api/hoadon/count/**",
        "/api/hoadon/*/cancel",
        "/api/hoadon/*/status",
        "/api/hoadon/*/trangthai/*",
        "/api/chitiethoadon/hoadon/*",
        "/api/nguoidung/change-password",
        "/api/diachigiaohang",
        "/api/diachigiaohang/*",
        "/api/yeuthich",
        "/api/yeuthich/*",
        "/api/danhgia",
        "/api/danhgia/*",
        "/api/khuyenmai/coupon/**"
    };
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }
    
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("🔧 === SECURITY CONFIG DEBUG ===");
        System.out.println("🔧 JWT Filter: " + jwtAuthenticationFilter.getClass().getSimpleName());
        System.out.println("🔧 OAuth2 Success Handler: " + oAuth2SuccessHandler.getClass().getSimpleName());
        System.out.println("🔧 Customer Frontend URL: " + customerFrontendUrl);
        System.out.println("🔧 Employee Frontend URL: " + employeeFrontendUrl);
        
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
            )
            
            // ===== THỨ TỰ AUTHORIZATION QUAN TRỌNG! =====
            .authorizeHttpRequests(authz -> authz
                // 1. API công khai - KHÔNG cần authentication
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                
                // 2. API chỉ dành cho MANAGER - ƯU TIÊN CAO NHẤT
                .requestMatchers(MANAGER_ONLY_ENDPOINTS).hasRole("MANAGER")
                
                // 3. API cho cả EMPLOYEE và MANAGER - THỨ TỰ 2
                .requestMatchers(EMPLOYEE_AND_MANAGER_ENDPOINTS).hasAnyRole("EMPLOYEE", "MANAGER")
                
                // 4. API chỉ dành cho EMPLOYEE (hiện tại rỗng)
                .requestMatchers(EMPLOYEE_ONLY_ENDPOINTS).hasRole("EMPLOYEE")
                
                // 5. API dành cho CUSTOMER - THỨ TỰ 3
                .requestMatchers(CUSTOMER_ENDPOINTS).hasAnyRole("CUSTOMER", "EMPLOYEE", "MANAGER")  // ✅ SỬA: cho phép tất cả role authenticated truy cập
                
                // 6. Tất cả request khác - Cần authentication
                .anyRequest().authenticated()
            )
            
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2SuccessHandler)
                .failureUrl(customerFrontendUrl + frontendFailurePath)
            )
            
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // ✅ THÊM: Debug filter để check JWT token
            .addFilterBefore((request, response, chain) -> {
                HttpServletRequest req = (HttpServletRequest) request;
                String authHeader = req.getHeader("Authorization");
                System.out.println("🔍 === JWT TOKEN DEBUG ===");
                System.out.println("🔍 Request URI: " + req.getRequestURI());
                System.out.println("🔍 Authorization Header: " + authHeader);
                System.out.println("🔍 Has Bearer Token: " + (authHeader != null && authHeader.startsWith("Bearer ")));
                System.out.println("🔍 === END JWT TOKEN DEBUG ===");
                chain.doFilter(request, response);
            }, jwtAuthenticationFilter.getClass())
            
            // Debug filters
            .addFilterAfter((request, response, chain) -> {
                System.out.println("🔍 === JWT FILTER STATUS DEBUG - NGAY SAU JWT ===");
                System.out.println("🔍 Request URI: " + ((HttpServletRequest) request).getRequestURI());
                System.out.println("🔍 SecurityContext Authentication: " + SecurityContextHolder.getContext().getAuthentication());
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    System.out.println("🔍 ✅ JWT Filter đã set authentication thành công!");
                    System.out.println("🔍 Role: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                } else {
                    System.out.println("🔍 ❌ JWT Filter KHÔNG set được authentication!");
                }
                System.out.println("🔍 === END JWT FILTER STATUS DEBUG - NGAY SAU JWT ===");
                chain.doFilter(request, response);
            }, jwtAuthenticationFilter.getClass())
            
            .addFilterAfter((request, response, chain) -> {
                System.out.println("🔍 === AUTHORIZATION DEBUG - TRƯỚC CONTROLLER ===");
                System.out.println("🔍 Request URI: " + ((HttpServletRequest) request).getRequestURI());
                System.out.println("🔍 Authentication: " + SecurityContextHolder.getContext().getAuthentication());
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    System.out.println("🔍 Principal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
                    System.out.println("🔍 Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                    System.out.println("🔍 Is Authenticated: " + SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
                    System.out.println("🔍 Has Role MANAGER: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER")));
                    System.out.println("🔍 Has Role EMPLOYEE: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_EMPLOYEE")));
                } else {
                    System.out.println("🔍 ❌ NO AUTHENTICATION FOUND!");
                }
                System.out.println("🔍 === END AUTHORIZATION DEBUG - TRƯỚC CONTROLLER ===");
                chain.doFilter(request, response);
            }, UsernamePasswordAuthenticationFilter.class);
        
        System.out.println("🔧 === END SECURITY CONFIG DEBUG ===");
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173",
            "http://localhost:8080",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:5173",
            "http://127.0.0.1:8080",
            customerFrontendUrl,
            employeeFrontendUrl
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    // ✅ SỬA: Custom AuthenticationEntryPoint với debug chi tiết hơn
    private static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                           AuthenticationException authException) throws IOException, ServletException {
            
            String requestURI = request.getRequestURI();
            String authHeader = request.getHeader("Authorization");
            
            // ✅ THÊM: Debug thông tin chi tiết
            System.out.println("🚨 === AUTHENTICATION ENTRY POINT DEBUG ===");
            System.out.println("🚨 Request URI: " + requestURI);
            System.out.println("🚨 Auth Header: " + authHeader);
            System.out.println("🚨 Exception: " + authException.getMessage());
            System.out.println("🚨 === END AUTHENTICATION ENTRY POINT DEBUG ===");
            
            if (requestURI.startsWith("/api/")) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                
                String jsonResponse = String.format(
                    "{\"error\":\"Chưa xác thực\",\"message\":\"Yêu cầu xác thực để truy cập endpoint này\",\"status\":401,\"path\":\"%s\",\"debug\":\"Token không hợp lệ hoặc đã hết hạn\"}",
                    requestURI
                );
                
                response.getWriter().write(jsonResponse);
            } else {
                response.sendRedirect("/oauth2/authorization/google");
            }
        }
    }
    
    // ✅ SỬA: Custom AccessDeniedHandler với debug chi tiết hơn
    private static class CustomAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                         org.springframework.security.access.AccessDeniedException accessDeniedException) 
                         throws IOException, ServletException {
            
            String requestURI = request.getRequestURI();
            var auth = SecurityContextHolder.getContext().getAuthentication();
            
            // ✅ THÊM: Debug thông tin chi tiết
            System.out.println("🚫 === ACCESS DENIED HANDLER DEBUG ===");
            System.out.println("🚫 Request URI: " + requestURI);
            System.out.println("🚫 Current Authentication: " + auth);
            if (auth != null) {
                System.out.println("🚫 Current Authorities: " + auth.getAuthorities());
            }
            System.out.println("🚫 Exception: " + accessDeniedException.getMessage());
            System.out.println("🚫 === END ACCESS DENIED HANDLER DEBUG ===");
            
            if (requestURI.startsWith("/api/")) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                
                String currentRoles = auth != null ? auth.getAuthorities().toString() : "null";
                String jsonResponse = String.format(
                    "{\"error\":\"Bị cấm\",\"message\":\"Không đủ quyền để truy cập endpoint này\",\"status\":403,\"path\":\"%s\",\"debug\":\"Current roles: %s. Endpoint yêu cầu role MANAGER\"}",
                    requestURI,
                    currentRoles
                );
                
                response.getWriter().write(jsonResponse);
            } else {
                response.sendError(HttpStatus.FORBIDDEN.value(), "Truy cập bị từ chối");
            }
        }
    }
}
