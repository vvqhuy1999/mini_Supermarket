
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
    
    // ===== CẤU HÌNH ENDPOINT - PHÂN TÁCH RÕ RÀNG =====
    
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
        "/api/sanpham", // GET danh sách
        "/api/sanpham/SP*", // GET chi tiết sản phẩm theo ID
        "/api/sanpham/optimized",
        "/api/sanpham/category/**",
        "/api/sanpham/with-tonkho",
        "/api/sanpham/*/with-tonkho",
        "/api/sanpham/search/**",
        
        // API loại sản phẩm công khai
        "/api/loaisanpham", // GET danh sách
        "/api/loaisanpham/LSP*", // GET chi tiết loại sản phẩm
        
        // API khuyến mãi công khai (chỉ đọc)
        "/api/khuyenmai", // GET danh sách
        "/api/khuyenmai/KM*", // GET chi tiết khuyến mãi
        
        // API giá sản phẩm công khai
        "/api/giasanpham", // GET danh sách
        "/api/giasanpham/GSP*", // GET chi tiết giá
        
        // API tồn kho công khai (chỉ đọc)
        "/api/tonkhochitiet", // GET danh sách
        "/api/tonkhochitiet/TK*", // GET chi tiết tồn kho
        
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
    
    // ===== ✅ SỬA: CÁC ENDPOINT DÀNH CHO CUSTOMER - ƯU TIÊN CAO NHẤT =====
    private final String[] CUSTOMER_SPECIFIC_ENDPOINTS = {
        // ✅ QUAN TRỌNG: Thông tin khách hàng cá nhân
        "/api/khachhang/by-nguoidung/**", // GET thông tin theo người dùng
        "/api/khachhang/*/shipping-info", // GET/PUT thông tin giao hàng
        "/api/khachhang/*/update-info", // ✅ THÊM: PUT cập nhật thông tin cá nhân
        "/api/khachhang/*/profile", // ✅ THÊM: GET/PUT profile cá nhân (nếu có)
        "/api/khachhang/*/change-password", // ✅ THÊM: PUT đổi mật khẩu (nếu có)
        
        // Giỏ hàng - CHỈ CUSTOMER
        "/api/giohang/**",
        
        // Hóa đơn của khách hàng cá nhân
        "/api/hoadon/from-cart",
        "/api/hoadon/by-khachhang/**",
        "/api/hoadon/*/full-details",
        "/api/hoadon/status/**",
        "/api/hoadon/date-range",
        "/api/hoadon/optimized",
        "/api/hoadon/count/**",
        "/api/hoadon/*/cancel",
        "/api/hoadon/*/status",
        "/api/hoadon/*/trangthai/**",
        
        // Chi tiết hóa đơn của khách hàng
        "/api/chitiethoadon/hoadon/**",
        
        // Thanh toán VNPay
        "/api/thanhtoan/vnpay/**",
        
        // Quản lý tài khoản cá nhân
        "/api/nguoidung/change-password",
        "/api/nguoidung/update-profile", // ✅ THÊM: Cập nhật profile người dùng
        
        // Địa chỉ giao hàng cá nhân
        "/api/diachigiaohang/**",
        
        // Yêu thích cá nhân
        "/api/yeuthich/**",
        
        // Đánh giá cá nhân
        "/api/danhgia/**",
        
        // Khuyến mãi coupon
        "/api/khuyenmai/coupon/**"
    };
    
    // ===== CÁC ENDPOINT CHỈ DÀNH CHO MANAGER - ƯU TIÊN THỨ 2 =====
    private final String[] MANAGER_ONLY_ENDPOINTS = {
        "/api/nhanvien/**",
        "/api/cuahang/**",
        "/api/nhacungcap/**",
        "/api/kho/**",
        "/api/phieunhaphang/**",
        "/api/phieuxuatkho/**",
        "/api/chitietphieunhap/**",
        "/api/chitietphieuxuat/**",
        "/api/khuyenmaisanpham/**",
        "/api/khuyenmaikhachhang/**",
        "/api/upload/**", // Upload file chỉ MANAGER
        "/api/thongkebaocao/**",
        "/api/baocao-doanhthu/**"
    };
    
    // ===== CÁC ENDPOINT CHO CẢ EMPLOYEE VÀ MANAGER - THỨ TỰ 3 =====
    private final String[] EMPLOYEE_AND_MANAGER_ENDPOINTS = {
        // CRUD sản phẩm cho EMPLOYEE và MANAGER
        "/api/sanpham/**", // POST, PUT, DELETE sản phẩm
        "/api/loaisanpham/**", // POST, PUT, DELETE loại sản phẩm
        
        // ✅ SỬA: Quản lý khách hàng - CHỈ các endpoint quản lý ADMIN
        "/api/khachhang", // GET danh sách tất cả khách hàng
        "/api/khachhang/KH*", // GET/PUT/DELETE khách hàng theo ID hệ thống (KH001, KH002, v.v.)
        "/api/khachhang/search/**", // Tìm kiếm khách hàng
        "/api/khachhang/admin/**", // ✅ THÊM: Các endpoint admin quản lý khách hàng
        // ❌ KHÔNG BAO GỒM: "/api/khachhang/**" để tránh xung đột với customer endpoints
        
        // Quản lý hóa đơn
        "/api/hoadon/employee/**",
        "/api/hoadon", // GET tất cả hóa đơn
        "/api/hoadon/HD*", // GET/PUT/DELETE hóa đơn theo ID
        "/api/hoadon/admin/**", // ✅ THÊM: Admin quản lý hóa đơn
        
        // Chi tiết hóa đơn
        "/api/chitiethoadon", // GET tất cả
        "/api/chitiethoadon/CTH*", // GET/PUT/DELETE theo ID
        "/api/chitiethoadon/admin/**", // ✅ THÊM: Admin quản lý chi tiết hóa đơn
        
        // Quản lý thanh toán
        "/api/thanhtoan", // GET tất cả thanh toán
        "/api/thanhtoan/TT*", // GET/PUT/DELETE theo ID
        "/api/thanhtoan/employee/**",
        "/api/thanhtoan/admin/**", // ✅ THÊM: Admin quản lý thanh toán
        
        // Quản lý kho và giá
        "/api/tonkhochitiet/**", // Tất cả CRUD tồn kho
        "/api/giasanpham/**", // Tất cả CRUD giá sản phẩm
        
        // Khuyến mãi
        "/api/khuyenmai/**", // Tất cả CRUD khuyến mãi (trừ coupon đã định nghĩa ở customer)
        
        // Quản lý ca làm việc
        "/api/calamviec/**",
        "/api/lichlamviec/**",
        "/api/bangluong/**"
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
            
            // ===== THỨ TỰ ƯU TIÊN QUAN TRỌNG! =====
            .authorizeHttpRequests(authz -> authz
                // 1. API công khai - KHÔNG cần authentication
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                
                // 2. ✅ QUAN TRỌNG: API CUSTOMER phải đặt TRƯỚC để tránh bị chặn
                .requestMatchers(CUSTOMER_SPECIFIC_ENDPOINTS).hasAnyRole("KHACH_HANG", "CUSTOMER")
                
                // 3. API chỉ dành cho MANAGER
                .requestMatchers(MANAGER_ONLY_ENDPOINTS).hasRole("MANAGER")
                
                // 4. API cho cả EMPLOYEE và MANAGER
                .requestMatchers(EMPLOYEE_AND_MANAGER_ENDPOINTS).hasAnyRole("EMPLOYEE", "MANAGER")
                
                // 5. Tất cả request khác - Cần authentication
                .anyRequest().authenticated()
            )
            
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2SuccessHandler)
                .failureUrl(customerFrontendUrl + frontendFailurePath)
            )
            
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // ✅ SỬA: Debug filter chỉ log các endpoint quan trọng
            .addFilterBefore((request, response, chain) -> {
                HttpServletRequest req = (HttpServletRequest) request;
                String uri = req.getRequestURI();
                String method = req.getMethod();
                
                // Log các API customer quan trọng
                if (uri.contains("/api/khachhang") && (uri.contains("update-info") || uri.contains("by-nguoidung"))) {
                    System.out.println("🔍 === CUSTOMER API REQUEST ===");
                    System.out.println("🔍 " + method + " " + uri);
                    String authHeader = req.getHeader("Authorization");
                    System.out.println("🔍 Has Auth: " + (authHeader != null && authHeader.startsWith("Bearer ")));
                }
                
                chain.doFilter(request, response);
            }, jwtAuthenticationFilter.getClass())
            
            .addFilterAfter((request, response, chain) -> {
                HttpServletRequest req = (HttpServletRequest) request;
                String uri = req.getRequestURI();
                String method = req.getMethod();
                
                // Log kết quả authentication cho các API customer quan trọng
                if (uri.contains("/api/khachhang") && (uri.contains("update-info") || uri.contains("by-nguoidung"))) {
                    System.out.println("🔍 === CUSTOMER AUTH RESULT ===");
                    System.out.println("🔍 " + method + " " + uri);
                    var auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null) {
                        System.out.println("🔍 ✅ User: " + auth.getName());
                        System.out.println("🔍 ✅ Roles: " + auth.getAuthorities());
                        System.out.println("🔍 ✅ Has KHACH_HANG role: " + 
                            auth.getAuthorities().stream().anyMatch(a -> 
                                a.getAuthority().equals("ROLE_KHACH_HANG") || 
                                a.getAuthority().equals("ROLE_CUSTOMER")
                            )
                        );
                    } else {
                        System.out.println("🔍 ❌ NO AUTHENTICATION!");
                    }
                    System.out.println("🔍 === END CUSTOMER AUTH RESULT ===");
                }
                
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
    
    private static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                           AuthenticationException authException) throws IOException, ServletException {
            
            String requestURI = request.getRequestURI();
            String method = request.getMethod();
            
            System.out.println("🚨 === AUTHENTICATION REQUIRED ===");
            System.out.println("🚨 " + method + " " + requestURI);
            System.out.println("🚨 Reason: " + authException.getMessage());
            
            if (requestURI.startsWith("/api/")) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                
                String jsonResponse = String.format(
                    "{\"error\":\"Chưa xác thực\",\"message\":\"Vui lòng đăng nhập để tiếp tục\",\"status\":401,\"path\":\"%s\",\"method\":\"%s\"}",
                    requestURI, method
                );
                
                response.getWriter().write(jsonResponse);
            } else {
                response.sendRedirect("/oauth2/authorization/google");
            }
        }
    }
    
    private static class CustomAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                         org.springframework.security.access.AccessDeniedException accessDeniedException) 
                         throws IOException, ServletException {
            
            String requestURI = request.getRequestURI();
            String method = request.getMethod();
            var auth = SecurityContextHolder.getContext().getAuthentication();
            
            System.out.println("🚫 === ACCESS DENIED ===");
            System.out.println("🚫 " + method + " " + requestURI);
            if (auth != null) {
                System.out.println("🚫 User: " + auth.getName());
                System.out.println("🚫 Current Roles: " + auth.getAuthorities());
                System.out.println("🚫 Expected: ROLE_KHACH_HANG or ROLE_CUSTOMER");
            } else {
                System.out.println("🚫 No authentication found");
            }
            
            if (requestURI.startsWith("/api/")) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                
                String currentRoles = auth != null ? auth.getAuthorities().toString() : "No authentication";
                String jsonResponse = String.format(
                    "{\"error\":\"Không đủ quyền\",\"message\":\"Bạn không có quyền cập nhật thông tin này\",\"status\":403,\"path\":\"%s\",\"method\":\"%s\",\"currentRoles\":\"%s\",\"requiredRoles\":\"ROLE_KHACH_HANG or ROLE_CUSTOMER\"}",
                    requestURI, method, currentRoles
                );
                
                response.getWriter().write(jsonResponse);
            } else {
                response.sendError(HttpStatus.FORBIDDEN.value(), "Truy cập bị từ chối");
            }
        }
    }
}
