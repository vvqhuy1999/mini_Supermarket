
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

// ===== MAIN SECURITY CONFIGURATION =====
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "spring.security.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig {
    
    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;
    
    // Removed unused ClientRegistrationRepository bean
    
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
        
        // API sản phẩm công khai - CHỈ ĐỌC - ✅ SỬA: Đảm bảo pattern đúng
        "/api/sanpham", // GET danh sách
        "/api/sanpham/SP*", // GET chi tiết sản phẩm theo ID
        "/api/sanpham/optimized",
        "/api/sanpham/category/**",
        "/api/sanpham/with-tonkho",
        "/api/sanpham/*/with-tonkho",
        "/api/sanpham/search/**",
        
        // API loại sản phẩm công khai - ✅ SỬA: Đảm bảo pattern đúng
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
        
        // Media & Images - ✅ SỬA: Đảm bảo tất cả image endpoints đều public
        "/api/hinhanh/**",
        "/api/upload/serve-image/**",
        "/api/upload/serve-image-by-id/**",
        "/api/upload/product-images/**",
        "/api/upload/product-image/**",
        "/api/upload/product-main-image/**",
        "/images/**",
        "/uploads/**",
        
        // VNPay callback - ✅ SỬA: Đảm bảo VNPay endpoints public
        "/api/thanhtoan/vnpay/return",
        "/api/thanhtoan/vnpay/ipn",
        "/api/phuongthucthanhtoan",
        
        // Health check
        "/actuator/health/**",
        "/health",
        
        // Favicon và static resources
        "/favicon.ico",
        "/static/**",
        "/public/**"
    };
    
    // ===== CÁC ENDPOINT DÀNH CHO CUSTOMER - ƯU TIÊN CAO NHẤT =====
    private final String[] CUSTOMER_SPECIFIC_ENDPOINTS = {
        // Thông tin khách hàng cá nhân
        "/api/khachhang/by-nguoidung/**", // GET thông tin theo người dùng
        "/api/khachhang/*/shipping-info", // GET/PUT thông tin giao hàng
        "/api/khachhang/*/update-info", // PUT cập nhật thông tin cá nhân
        "/api/khachhang/*/profile", // GET/PUT profile cá nhân
        "/api/khachhang/*/change-password", // PUT đổi mật khẩu
        
        // Giỏ hàng - CHỈ CUSTOMER
        "/api/giohang/**",
        
        // ✅ HÓA ĐƠN CỦA KHÁCH HÀNG CÁ NHÂN - CHỈ CUSTOMER - SỬA: Pattern cụ thể hơn
        // 🛒 Checkout Flow
        "/api/hoadon/from-cart", // POST - Tạo hóa đơn từ giỏ hàng
        "/api/hoadon/by-khachhang/**", // GET - Lấy hóa đơn theo khách hàng
        "/api/hoadon/*/full-details", // GET - Chi tiết hóa đơn cá nhân (full-details)
        "/api/hoadon/by-khachhang/*/full-details", // GET - Chi tiết đầy đủ theo khách hàng
        
        // 📊 Statistics & Counting cho khách hàng cá nhân
        "/api/hoadon/count/**", // GET - Đếm hóa đơn cá nhân
        "/api/hoadon/count/trangthai/**", // GET - Đếm theo trạng thái
        "/api/hoadon/count/khachhang/**", // GET - Đếm theo khách hàng
        
        // 🔍 Filtering & Searching cho khách hàng cá nhân
        "/api/hoadon/status/**", // GET - Lấy hóa đơn theo trạng thái
        "/api/hoadon/by-khachhang/*/status/**", // GET - Lấy hóa đơn theo khách hàng và trạng thái
        "/api/hoadon/date-range", // GET - Lấy hóa đơn theo khoảng ngày
        "/api/hoadon/by-khachhang/*/date-range", // GET - Lấy hóa đơn theo khách hàng và khoảng ngày
        "/api/hoadon/search", // GET - Tìm kiếm hóa đơn
        
        // 📈 Statistics cho khách hàng cá nhân
        "/api/hoadon/by-khachhang/*/statistics", // GET - Thống kê hóa đơn cá nhân
        "/api/hoadon/by-khachhang/*/count-by-status", // GET - Đếm hóa đơn theo trạng thái cá nhân
        
        // ❌ Cancellation & Status Update cho khách hàng cá nhân - SỬA: Pattern cụ thể hơn
        "/api/hoadon/*/cancel", // PATCH - Hủy hóa đơn cá nhân
        "/api/hoadon/*/trangthai", // PATCH - Cập nhật trạng thái cá nhân
        
        // ✅ THÊM: Endpoint cập nhật trạng thái hóa đơn cho customer (cụ thể hơn)
        "/api/hoadon/*/trangthai/*", // PUT - Cập nhật trạng thái hóa đơn cá nhân
        
        // ✅ THÊM: Endpoint cập nhật hóa đơn cá nhân (cụ thể hơn)
        "/api/hoadon/HD*", // PUT - Cập nhật hóa đơn cá nhân (chỉ HD* pattern)
        
        // Chi tiết hóa đơn của khách hàng
        "/api/chitiethoadon/hoadon/**", // GET - Chi tiết theo hóa đơn
        
        // Thanh toán VNPay - CHỈ CUSTOMER
        "/api/thanhtoan/vnpay", // POST - Tạo URL thanh toán VNPay
        "/api/thanhtoan/vnpay/**", // Tất cả VNPay endpoints cho customer
        
        // Quản lý tài khoản cá nhân
        "/api/nguoidung/change-password",
        "/api/nguoidung/update-profile",
        "/api/nguoidung/profile", // GET - Lấy thông tin profile cá nhân
        
        // Địa chỉ giao hàng cá nhân
        "/api/diachigiaohang/**",
        
        // Yêu thích cá nhân
        "/api/yeuthich/**",
        
        // Đánh giá cá nhân
        "/api/danhgia/**",
        
        // Khuyến mãi coupon
        "/api/khuyenmai/coupon/**"
        
        // ❌ SỬA: Bỏ "/api/hoadon/*" vì quá rộng và gây xung đột với STAFF_AND_MANAGER_ENDPOINTS
    };
    
    // ===== CÁC ENDPOINT CHỈ DÀNH CHO MANAGER - ƯU TIÊN THỨ 2 =====
    private final String[] MANAGER_ONLY_ENDPOINTS = {
        "/api/cuahang/**",
        "/api/nhacungcap/**",
        // ✅ SỬA: Upload file chỉ MANAGER - Tránh xung đột với public image serving
        "/api/upload/upload-image/**", // POST - Upload ảnh mới
        "/api/upload/update-image/**", // PUT - Cập nhật ảnh
        "/api/upload/delete-image/**", // DELETE - Xóa ảnh
        "/api/upload/admin/**", // Admin quản lý upload
        "/api/thongkebaocao/**",
        "/api/baocao-doanhthu/**"
    };
    
    // ===== ✅ SỬA: CÁC ENDPOINT CHO CẢ EMPLOYEE VÀ MANAGER - THỨ TỰ 3 =====
    private final String[] CUSTOMER_AND_MANAGER_ENDPOINTS = {
        // ✅ QUAN TRỌNG: Quản lý nhân viên cho EMPLOYEE và MANAGER
        "/api/nhanvien/**",
        
        // ✅ QUAN TRỌNG: Quản lý kho và phiếu xuất nhập cho EMPLOYEE và MANAGER
        "/api/kho/**",
        "/api/phieunhaphang/**",
        "/api/phieuxuatkho/**",
        "/api/chitietphieunhap/**",
        "/api/chitietphieuxuat/**",
        
        // Khuyến mãi sản phẩm và khách hàng
        "/api/khuyenmaisanpham/**",
        "/api/khuyenmaikhachhang/**",
        
        // CRUD sản phẩm cho EMPLOYEE và MANAGER - ✅ SỬA: Tránh xung đột với public endpoints
        "/api/sanpham/create", // POST - Tạo sản phẩm mới
        "/api/sanpham/update/**", // PUT - Cập nhật sản phẩm
        "/api/sanpham/delete/**", // DELETE - Xóa sản phẩm
        "/api/sanpham/admin/**", // Admin quản lý sản phẩm
        "/api/loaisanpham/create", // POST - Tạo loại sản phẩm mới
        "/api/loaisanpham/update/**", // PUT - Cập nhật loại sản phẩm
        "/api/loaisanpham/delete/**", // DELETE - Xóa loại sản phẩm
        "/api/loaisanpham/admin/**", // Admin quản lý loại sản phẩm
        
        // Quản lý khách hàng - CHỈ các endpoint quản lý ADMIN
        "/api/khachhang", // GET danh sách tất cả khách hàng
        "/api/khachhang/KH*", // GET/PUT/DELETE khách hàng theo ID hệ thống
        "/api/khachhang/search/**", // Tìm kiếm khách hàng
        "/api/khachhang/admin/**", // Các endpoint admin quản lý khách hàng
        
        // ✅ QUAN TRỌNG: Quản lý hóa đơn cho EMPLOYEE và MANAGER - SỬA: Pattern cụ thể hơn
        "/api/hoadon/employee/**", // API dành cho employee
        "/api/hoadon", // GET tất cả hóa đơn (quản lý)
        "/api/hoadon/HD*", // GET/PUT/DELETE hóa đơn theo ID (quản lý) - SỬA: Pattern cụ thể hơn
        "/api/hoadon/admin/**", // Admin quản lý hóa đơn
        "/api/hoadon/complete-data", // ✅ THÊM: API lấy dữ liệu đầy đủ đơn hàng
        "/api/hoadon/with-details/**", // ✅ THÊM: Hóa đơn với chi tiết
        
        // ✅ THÊM: Endpoint quản lý tổng quan cho staff/manager (không trùng với customer)
        "/api/hoadon/management/**", // API quản lý tổng quan
        "/api/hoadon/reports/**", // API báo cáo
        "/api/hoadon/analytics/**", // API phân tích dữ liệu
        
        // ✅ LƯU Ý: Các endpoint hóa đơn được phân tách như sau:
        // - CUSTOMER: Chỉ truy cập hóa đơn của chính mình (by-khachhang, full-details, cancel, status cá nhân)
        // - EMPLOYEE/MANAGER: Truy cập tất cả hóa đơn để quản lý (GET all, CRUD, thống kê, báo cáo)
        // - Pattern matching: 
        //   + /api/hoadon/*/by-khachhang/** = Customer (GET)
        //   + /api/hoadon/*/trangthai/* = Customer (PUT status)
        //   + /api/hoadon/HD* = Staff/Manager (CRUD)
        //   + /api/hoadon/management/** = Staff/Manager (quản lý)
        // - TRÁNH XUNG ĐỘT: Không để endpoint trùng lặp giữa CUSTOMER và STAFF/MANAGER
        // Chi tiết hóa đơn
        "/api/chitiethoadon", // GET tất cả
        "/api/chitiethoadon/CTH*", // GET/PUT/DELETE theo ID
        "/api/chitiethoadon/admin/**", // Admin quản lý chi tiết hóa đơn
        
        // Quản lý thanh toán
        "/api/thanhtoan", // GET tất cả thanh toán
        "/api/thanhtoan/TT*", // GET/PUT/DELETE theo ID
        "/api/thanhtoan/employee/**",
        "/api/thanhtoan/admin/**", // Admin quản lý thanh toán
        
        // Quản lý kho và giá
        "/api/tonkhochitiet/**", // Tất cả CRUD tồn kho
        "/api/giasanpham/**", // Tất cả CRUD giá sản phẩm
        
        // Khuyến mãi
        "/api/khuyenmai/**", // Tất cả CRUD khuyến mãi
        
        // Quản lý ca làm việc
        "/api/calamviec/**",
        "/api/lichlamviec/**",
        "/api/bangluong/**"
    };
    
    // ===== CÁC ENDPOINT KẾT HỢP - CUSTOMER VÀ EMPLOYEE CÙNG SỬ DỤNG =====
    private final String[] COMBINED_CUSTOMER_EMPLOYEE_ENDPOINTS = {
       
    };
    
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }
    
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
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
            
            // ===== ✅ SỬA: THỨ TỰ ƯU TIÊN VÀ ROLE MATCHING =====
            .authorizeHttpRequests(authz -> authz
                // 1. API công khai - KHÔNG cần authentication
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                
                // 2. API CUSTOMER phải đặt TRƯỚC để tránh bị chặn
                .requestMatchers(CUSTOMER_SPECIFIC_ENDPOINTS).hasAnyRole("KHACH_HANG", "CUSTOMER")
                
                // 3. API kết hợp - CUSTOMER và EMPLOYEE cùng sử dụng
                .requestMatchers(COMBINED_CUSTOMER_EMPLOYEE_ENDPOINTS).hasAnyRole("KHACH_HANG", "CUSTOMER", "EMPLOYEE", "MANAGER")
                
                // 4. API chỉ dành cho MANAGER
                .requestMatchers(MANAGER_ONLY_ENDPOINTS).hasRole("MANAGER")
                
                // 5. ✅ QUAN TRỌNG: API cho cả EMPLOYEE và MANAGER
                .requestMatchers(CUSTOMER_AND_MANAGER_ENDPOINTS).hasAnyRole("EMPLOYEE", "MANAGER")
                
                // 6. Tất cả request khác - Cần authentication
                .anyRequest().authenticated()
            )
            
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2SuccessHandler)
                .failureUrl(customerFrontendUrl + frontendFailurePath)
            )
            
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // ✅ SỬA: Debug filter với thông tin role EMPLOYEE chi tiết
            .addFilterBefore((request, response, chain) -> {
                HttpServletRequest req = (HttpServletRequest) request;
                String uri = req.getRequestURI();
                String method = req.getMethod();
                
                // Log các API employee quan trọng
                if ((uri.contains("/api/hoadon") && !uri.contains("by-khachhang")) || 
                    uri.contains("/api/phieuxuatkho") || 
                    uri.contains("/api/chitietphieuxuat") ||
                    uri.contains("/api/nhanvien") ||
                    uri.contains("/api/kho")) {
                    System.out.println("🔍 === EMPLOYEE API REQUEST ===");
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
                
                // Log kết quả authentication cho các API employee quan trọng
                if ((uri.contains("/api/hoadon") && !uri.contains("by-khachhang")) || 
                    uri.contains("/api/phieuxuatkho") || 
                    uri.contains("/api/chitietphieuxuat") ||
                    uri.contains("/api/nhanvien") ||
                    uri.contains("/api/kho")) {
                    System.out.println("🔍 === EMPLOYEE AUTH RESULT ===");
                    System.out.println("🔍 " + method + " " + uri);
                    var auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null) {
                        System.out.println("🔍 ✅ User: " + auth.getName());
                        System.out.println("🔍 ✅ All Roles: " + auth.getAuthorities());
                        System.out.println("🔍 ✅ Has EMPLOYEE role: " + 
                            auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"))
                        );
                        System.out.println("🔍 ✅ Has MANAGER role: " + 
                            auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))
                        );
                        System.out.println("🔍 ✅ Has CUSTOMER role: " + 
                            auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER") || a.getAuthority().equals("ROLE_KHACH_HANG"))
                        );
                    } else {
                        System.out.println("🔍 ❌ NO AUTHENTICATION!");
                    }
                    System.out.println("🔍 === END EMPLOYEE AUTH RESULT ===");
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
                    System.out.println("🚫 Expected: ROLE_EMPLOYEE or ROLE_MANAGER");
                } else {
                    System.out.println("🚫 No authentication found");
                }
                
                if (requestURI.startsWith("/api/")) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    
                    String currentRoles = auth != null ? auth.getAuthorities().toString() : "No authentication";
                    String jsonResponse = String.format(
                        "{\"error\":\"Không đủ quyền\",\"message\":\"Bạn không có quyền truy cập tài nguyên này\",\"status\":403,\"path\":\"%s\",\"method\":\"%s\",\"currentRoles\":\"%s\",\"requiredRoles\":\"ROLE_EMPLOYEE or ROLE_MANAGER\"}",
                        requestURI, method, currentRoles
                    );
                    
                    response.getWriter().write(jsonResponse);
                } else {
                    response.sendError(HttpStatus.FORBIDDEN.value(), "Truy cập bị từ chối");
                }
            }
        }
    }
