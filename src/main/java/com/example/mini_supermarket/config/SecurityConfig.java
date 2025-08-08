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
    
    // Các endpoint công khai không cần authentication
    private final String[] PUBLIC_ENDPOINTS = {
        "/swagger-ui/**", 
        "/swagger-ui.html", 
        "/api-docs/**", 
        "/v3/api-docs/**",
        "/api/nguoidung/**",
        "/api/auth/**",
        "/api/oauth2/**",
        "/api/oauth2-test/**",
        "/oauth2/**",
        "/oauth2/authorization/**",
        "/login/oauth2/code/**",
        "/api/sanpham/**",      // Xem sản phẩm, tìm kiếm sản phẩm
        "/api/loaisanpham/**"   // Xem danh mục sản phẩm
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
           
            
            // Cấu hình authorization - sử dụng PUBLIC_ENDPOINTS
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
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