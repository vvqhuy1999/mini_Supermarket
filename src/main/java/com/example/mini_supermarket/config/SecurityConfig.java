
package com.example.mini_supermarket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    // JWT filter sẽ xử lý JWT token từ header Authorization: Bearer <token>
    // OAuth2SuccessHandler sẽ xử lý JWT sau khi OAuth2 thành công
    
    @Value("${oauth2.frontend.base-url:http://localhost:3000}")
    private String frontendBaseUrl;
    
    @Value("${oauth2.frontend.success-path:/oauth2/success}")
    private String frontendSuccessPath;
    
    @Value("${oauth2.frontend.failure-path:/login?error=oauth2_failed}")
    private String frontendFailurePath;
    
    // ===== PHÂN QUYỀN THEO ROLE =====
    // 
    // 🔐 CÁCH HOẠT ĐỘNG:
    // - Khách hàng (authenticated): Truy cập được API khách hàng + API công khai
    // - hasRole("EMPLOYEE"): Truy cập được API nhân viên + API chung + API khách hàng + API công khai
    // - hasRole("MANAGER"): Truy cập được API nhân viên + API chung + API quản lý + API khách hàng + API công khai  
    // - hasRole("ADMIN"): Hiện tại chưa cần dùng (đã comment)
    //
    // 📋 QUY TẮC PHÂN QUYỀN:
    // 1. PUBLIC_ENDPOINTS: Không cần authentication (permitAll) - Chỉ xem thông tin cơ bản
    // 2. CUSTOMER_ENDPOINTS: Cần authentication (không cần role cụ thể) - Quản lý cá nhân khách hàng
    // 3. EMPLOYEE_ENDPOINTS: Cần role EMPLOYEE - Quản lý nghiệp vụ cơ bản
    // 4. SHARED_ENDPOINTS: Cần role EMPLOYEE HOẶC MANAGER (hasAnyRole) - Quản lý cá nhân nhân viên
    // 5. MANAGER_ENDPOINTS: Cần role MANAGER - Quản lý toàn hệ thống
    // 6. ADMIN_ENDPOINTS: Hiện tại chưa cần dùng (đã comment)
    // 7. anyRequest(): Cần authentication (không cần role cụ thể)
    //
    // 🛍️ PHÂN QUYỀN SẢN PHẨM:
    // - PUBLIC: GET /api/sanpham/* (xem sản phẩm, category, optimized) - CHỈ XEM
    // - MANAGER: POST /api/sanpham (tạo), PUT /api/sanpham/* (cập nhật), DELETE /api/sanpham/* (xóa)
    // - EMPLOYEE: Không có quyền trực tiếp với sản phẩm (chỉ xem qua PUBLIC)
    //
    // ⚠️ LƯU Ý: 
    // - Spring Security tự động thêm prefix "ROLE_" 
    // - hasRole("EMPLOYEE") = hasAuthority("ROLE_EMPLOYEE")
    // - hasRole("MANAGER") = hasAuthority("ROLE_MANAGER")
    // - hasRole("ADMIN") = hasAuthority("ROLE_ADMIN")
    // - hasAnyRole("EMPLOYEE", "MANAGER") = có ít nhất 1 trong 2 role
    // - OAuth2 login được giữ nguyên để đăng nhập
    // - PUBLIC_ENDPOINTS sẽ không redirect về trang login
    //
    // 🧪 HƯỚNG DẪN TEST:
    // 1. PUBLIC_ENDPOINTS: Có thể truy cập trực tiếp (không cần authentication)
    // 2. CUSTOMER_ENDPOINTS: Cần OAuth2 login hoặc JWT token (không cần role cụ thể)
    // 3. EMPLOYEE_ENDPOINTS: Cần OAuth2 login + role EMPLOYEE
    // 4. MANAGER_ENDPOINTS: Cần OAuth2 login + role MANAGER
    // 5. Swagger UI: http://localhost:8080/swagger-ui/ (có thể test với OAuth2)
    
    // ===== API CÔNG KHAI (Public) - Không cần authentication =====
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
        // Logout và check-auth được xử lý bởi AuthenticationController
        
        // Quên mật khẩu và OTP (không cần authentication)
        "/api/forgot-password/**",    // POST: Gửi OTP, xác thực OTP, reset mật khẩu
        
        // API cơ bản cho khách hàng - CHỈ XEM (READ)
        "/api/sanpham",                 // GET: Xem danh sách sản phẩm
        "/api/sanpham/*",               // GET: Xem chi tiết sản phẩm (không phải POST/PUT/DELETE)
        "/api/sanpham/optimized",       // GET: Xem danh sách sản phẩm (tối ưu)
        "/api/sanpham/*/optimized",     // GET: Xem chi tiết sản phẩm (tối ưu)
        "/api/sanpham/category/*",      // GET: Xem sản phẩm theo category
        "/api/sanpham/category/*/active", // GET: Xem sản phẩm theo category + active
        "/api/sanpham/with-tonkho",     // GET: Xem danh sách sản phẩm với số lượng tồn kho
        "/api/sanpham/*/with-tonkho",   // GET: Xem chi tiết sản phẩm với số lượng tồn kho
        "/api/loaisanpham",             // GET: Xem danh sách loại sản phẩm
        "/api/loaisanpham/*",           // GET: Xem chi tiết loại sản phẩm
        "/api/khuyenmai",               // GET: Xem danh sách khuyến mãi
        "/api/khuyenmai/*",             // GET: Xem chi tiết khuyến mãi (KHÔNG bao gồm /coupon/{couponCode})
        "/api/giasanpham",              // GET: Xem giá sản phẩm
        "/api/giasanpham/*",            // GET: Xem chi tiết giá sản phẩm
        "/api/tonkhochitiet",           // GET: Xem tồn kho
        "/api/tonkhochitiet/*",         // GET: Xem chi tiết tồn kho
        
        // Khách hàng - CHỈ XEM thông tin cơ bản (không cần authentication)
        "/api/khachhang/register",      // POST: Đăng ký tài khoản khách hàng mới
        "/api/khachhang/by-email/*",    // GET: Tìm khách hàng theo email
        
        // Người dùng - CHỈ XEM thông tin cơ bản (không cần authentication)
        "/api/nguoidung/email/*",       // GET: Lấy thông tin người dùng theo email


        // Media & Images
        "/api/hinhanh/**",              // Xem hình ảnh
        "/api/upload/serve-image/**",   // Serve ảnh trực tiếp từ server
        "/api/upload/serve-image-by-id/**", // Serve ảnh theo ID
        "/api/upload/product-images/**",    // Xem danh sách ảnh sản phẩm
        "/api/upload/product-image/**",     // Xem ảnh sản phẩm theo ID
        "/api/upload/product-main-image/**", // Xem ảnh chính sản phẩm
        "/images/**",                   // Truy cập ảnh từ uploads/images
        "/uploads/**",                   // Truy cập trực tiếp từ thư mục uploads

        // VNPay callback - Không cần authentication
        "/api/thanhtoan/vnpay/return",  // Callback URL từ VNPay sau khi thanh toán

        "/api/phuongthucthanhtoan",


        // Health check
        "/actuator/health/**",
        "/health"
    };
    
    // ===== API DÀNH CHO KHÁCH HÀNG (Customer) - Cần authentication =====
    private final String[] CUSTOMER_ENDPOINTS = {
        "/api/thanhtoan/vnpay",

        // Giỏ hàng cá nhân - FULL CRUD (merged controller)
        "/api/giohang/**",              // FULL CRUD giỏ hàng và items, sync, status
        
        // Khách hàng - Quản lý cá nhân (cần authentication)
        "/api/khachhang/by-nguoidung/*", // GET: Xem profile, PUT: Cập nhật profile
        "/api/khachhang/*",             // GET: Xem thông tin cá nhân, PUT: Cập nhật thông tin
        "/api/khachhang/*/shipping-info", // GET: Xem thông tin giao hàng, PUT: Cập nhật thông tin giao hàng (cho Checkout.vue)

        // Tạo hóa đơn từ giỏ hàng - thay thế đơn hàng
        "/api/hoadon/from-cart",       // POST: Tạo hóa đơn từ giỏ hàng
        
        // Hóa đơn cá nhân - CHỈ XEM VÀ HỦY
        "/api/hoadon",                  // GET: Xem hóa đơn cá nhân
        "/api/hoadon/*",                // GET: Xem chi tiết hóa đơn cá nhân
        "/api/hoadon/by-khachhang/**",  // GET: Xem hóa đơn theo khách hàng (tất cả sub-paths)
        "/api/hoadon/*/full-details",   // GET: Xem hóa đơn với chi tiết đầy đủ
        "/api/hoadon/status/*",         // GET: Xem hóa đơn theo trạng thái (chỉ của mình)
        "/api/hoadon/date-range",       // GET: Xem hóa đơn theo ngày (chỉ của mình)
        "/api/hoadon/optimized",        // GET: Xem hóa đơn tối ưu với pagination
        "/api/hoadon/count/**",         // GET: Đếm hóa đơn
        "/api/hoadon/*/cancel",         // PATCH: Hủy hóa đơn của mình
        "/api/hoadon/*/status",         // POST: Cập nhật trạng thái hóa đơn
        "/api/hoadon/*/trangthai/*",    // PUT: Cập nhật trạng thái hóa đơn (alternative)
        "/api/chitiethoadon",           // GET: Xem chi tiết hóa đơn cá nhân
        "/api/chitiethoadon/*",         // GET: Xem chi tiết cụ thể
        "/api/chitiethoadon/hoadon/*",  // GET: Xem chi tiết theo mã hóa đơn

        // Thanh toán cá nhân
        "/api/thanhtoan",               // POST: Tạo thanh toán
        "/api/thanhtoan/*",             // GET: Xem trạng thái thanh toán
             // GET: Xem phương thức thanh toán
        
        // Profile khách hàng - Xem và cập nhật thông tin cá nhân (cần authentication)
        "/api/khachhang/by-nguoidung/*", // GET: Xem profile, PUT: Cập nhật profile
        // Lưu ý: /api/nguoidung/email/* đã được chuyển sang PUBLIC_ENDPOINTS
        
        // Đổi mật khẩu
        "/api/nguoidung/change-password", // POST: Đổi mật khẩu
        
        // Quản lý địa chỉ giao hàng (nếu có)
        "/api/diachigiaohang",          // GET: Xem địa chỉ giao hàng, POST: Thêm địa chỉ
        "/api/diachigiaohang/*",        // GET: Xem chi tiết, PUT: Cập nhật, DELETE: Xóa
        
        // Quản lý yêu thích (nếu có)
        "/api/yeuthich",                // GET: Xem danh sách yêu thích, POST: Thêm yêu thích
        "/api/yeuthich/*",              // DELETE: Xóa yêu thích
        
        // Đánh giá sản phẩm (nếu có)
        "/api/danhgia",                 // GET: Xem đánh giá cá nhân, POST: Tạo đánh giá
        "/api/danhgia/*",               // PUT: Cập nhật đánh giá, DELETE: Xóa đánh giá
        "/api/khuyenmai/coupon/**", // GET: Kiểm tra coupon code
    };
    
    // ===== API DÀNH CHO NHÂN VIÊN (Employee) - Cần role EMPLOYEE =====
    private final String[] EMPLOYEE_ENDPOINTS = {
        // Quản lý khách hàng - FULL CRUD (nhân viên quản lý tất cả khách hàng)
        "/api/khachhang",               // GET: Xem danh sách tất cả khách hàng
        "/api/khachhang/*",        // GET: Xem chi tiết khách hàng, PUT: Cập nhật khách hàng
        "/api/khachhang/admin/**",      // Quản lý khách hàng (nếu có sub-path admin)
        
        // Quản lý kho cơ bản - CHỈ XEM
        "/api/tonkhochitiet",           // GET: Xem tồn kho
        "/api/tonkhochitiet/*",         // GET: Xem chi tiết tồn kho
        "/api/giasanpham",              // GET: Xem giá sản phẩm
        "/api/giasanpham/*",            // GET: Xem chi tiết giá
        
        // Tạo hóa đơn từ giỏ hàng - thay thế đơn hàng
        "/api/hoadon/from-cart",       // POST: Tạo hóa đơn từ giỏ hàng
        
        // Quản lý hóa đơn - FULL CRUD (nhân viên quản lý tất cả hóa đơn)
        "/api/hoadon",                  // GET: Xem danh sách tất cả hóa đơn
        "/api/hoadon/*",           // GET: Xem chi tiết hóa đơn, PUT: Cập nhật hóa đơn
        "/api/hoadon/employee/**",      // Quản lý hóa đơn (nếu có sub-path employee)
        "/api/chitiethoadon",           // GET: Xem danh sách chi tiết hóa đơn
        "/api/chitiethoadon/*",  // GET: Xem chi tiết cụ thể, PUT: Cập nhật chi tiết
        
        // Quản lý thanh toán - XỬ LÝ
        "/api/thanhtoan",               // GET: Xem danh sách thanh toán
        "/api/thanhtoan/*",        // GET: Xem chi tiết thanh toán, PUT: Cập nhật thanh toán
        "/api/thanhtoan/employee/**",   // Xử lý thanh toán (nếu có sub-path employee)
        
        // Quản lý khuyến mãi cơ bản - CHỈ XEM VÀ CẬP NHẬT
        "/api/khuyenmai",      // GET: Xem khuyến mãi, PUT: Cập nhật
        "/api/khuyenmai/*",    // GET: Xem chi tiết, PUT: Cập nhật
        "/api/khuyenmai/coupon/**", // GET: Kiểm tra coupon code (yêu cầu authentication)
    };
    
    // ===== API CHUNG CHO CẢ EMPLOYEE VÀ MANAGER =====
    private final String[] SHARED_ENDPOINTS = {
        // Quản lý ca làm việc cá nhân - CHỈ XEM
        "/api/calamviec",               // GET: Xem ca làm việc cá nhân
        "/api/calamviec/*",             // GET: Xem chi tiết ca làm việc
        "/api/lichlamviec",             // GET: Xem lịch làm việc cá nhân
        "/api/lichlamviec/*",           // GET: Xem chi tiết lịch làm việc
        "/api/bangluong",               // GET: Xem lương cá nhân
        "/api/bangluong/*",             // GET: Xem chi tiết lương
    };
    
    // ===== API DÀNH CHO QUẢN LÝ (Manager) - Cần role MANAGER =====
    private final String[] MANAGER_ENDPOINTS = {
        // Quản lý nhân viên - FULL CRUD
        "/api/nhanvien/**",             // Quản lý tất cả nhân viên
        
        // Quản lý cửa hàng - FULL CRUD
        "/api/cuahang/**",              // Quản lý cửa hàng
        
        // Quản lý nhà cung cấp - FULL CRUD
        "/api/nhacungcap/**",           // Quản lý nhà cung cấp
        
        // Quản lý kho nâng cao - FULL CRUD
        "/api/kho/**",                  // Quản lý kho
        "/api/phieunhaphang/**",        // Quản lý phiếu nhập hàng
        "/api/phieuxuatkho/**",         // Quản lý phiếu xuất kho
        "/api/chitietphieunhap/**",     // Quản lý chi tiết phiếu nhập
        "/api/chitietphieuxuat/**",     // Quản lý chi tiết phiếu xuất
        
        // Quản lý khuyến mãi nâng cao - FULL CRUD
        "/api/khuyenmaisanpham/**",     // Quản lý khuyến mãi sản phẩm
        "/api/khuyenmaikhachhang/**",   // Quản lý khuyến mãi khách hàng
        
        // Quản lý nhân sự (tất cả) - FULL CRUD
        "/api/calamviec/**",    // Quản lý ca làm việc (tất cả)
        "/api/lichlamviec/**",  // Quản lý lịch làm việc (tất cả)
        "/api/bangluong/**",    // Quản lý bảng lương (tất cả)
        
        // Quản lý sản phẩm nâng cao - FULL CRUD
        "/api/sanpham/**",      // Quản lý sản phẩm (quản lý) - FULL CRUD
        "/api/loaisanpham/**",  // Quản lý loại sản phẩm (quản lý) - FULL CRUD
        
        // Quản lý khuyến mãi cơ bản - FULL CRUD
        "/api/khuyenmai/**",    // Quản lý khuyến mãi (quản lý)
        
        // Quản lý hóa đơn nâng cao - FULL CRUD
        "/api/hoadon/**",       // Quản lý hóa đơn (quản lý)
        "/api/chitiethoadon/**", // Quản lý chi tiết hóa đơn (quản lý)
        
        // Tạo hóa đơn từ giỏ hàng - thay thế đơn hàng
        "/api/hoadon/from-cart",       // POST: Tạo hóa đơn từ giỏ hàng
        
        // Upload & Media management - FULL CRUD
        "/api/upload/**",               // API upload ảnh sản phẩm
        
        // Reports & Statistics - FULL CRUD
        "/api/thongkebaocao/**"         // Quản lý thống kê báo cáo
    };
    
    // ===== API DÀNH CHO ADMIN (Admin) - Cần role ADMIN =====
    private final String[] ADMIN_ENDPOINTS = {
        // Quản lý hệ thống
        "/api/admin/**",                // Tất cả API admin
        "/api/system/**",               // Quản lý hệ thống
        "/api/config/**",               // Cấu hình hệ thống
        
        // Quản lý người dùng toàn hệ thống
        "/api/nguoidung/admin/**",      // Quản lý tất cả người dùng
        
        // Backup & Restore
        "/api/backup/**",               // Backup dữ liệu
        "/api/restore/**",              // Restore dữ liệu
        
        // Audit logs
        "/api/audit/**",                // Nhật ký kiểm toán
        "/api/logs/**",                 // Nhật ký hệ thống
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
            
            // Cấu hình CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Tắt HTTP Basic Authentication
            .httpBasic(AbstractHttpConfigurer::disable)
            
            // Tắt form login
            .formLogin(AbstractHttpConfigurer::disable)
            
            // Cấu hình session policy - Stateless cho JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Không cần cấu hình logout ở đây
            // AuthenticationController sẽ xử lý logout thông qua endpoint /api/auth/log-out
            
            // Thêm JWT filter để xử lý JWT token từ header Authorization
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // Cấu hình OAuth2 - Simple Google login
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2SuccessHandler)
                .failureUrl(frontendBaseUrl + frontendFailurePath)
            )
            
            // Cấu hình authorization - Phân quyền theo role cụ thể
            .authorizeHttpRequests(authz -> authz
                // 🔓 TEST MODE: Mở toàn bộ API để test
                .anyRequest().permitAll()
                
                // ⚠️ COMMENT TẤT CẢ PHÂN QUYỀN ĐỂ TEST:
                // // API công khai - Không cần authentication
                // .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                // 
                // // API dành cho khách hàng - Cần authentication
                // .requestMatchers(CUSTOMER_ENDPOINTS).authenticated()
                // 
                // // API dành cho nhân viên - Cần role EMPLOYEE
                // .requestMatchers(EMPLOYEE_ENDPOINTS).hasRole("EMPLOYEE")
                // 
                // // API chung cho cả EMPLOYEE và MANAGER
                // .requestMatchers(SHARED_ENDPOINTS).hasAnyRole("EMPLOYEE", "MANAGER")
                // 
                // // API dành cho quản lý - Cần role MANAGER
                // .requestMatchers(MANAGER_ENDPOINTS).hasRole("MANAGER")
                // 
                // // API dành cho admin - Cần role ADMIN (đã comment - chưa cần dùng)
                // // .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                // 
                // // Tất cả request khác - Cần authentication
                // .anyRequest().authenticated()
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
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 
