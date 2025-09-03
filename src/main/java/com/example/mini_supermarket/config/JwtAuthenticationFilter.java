package com.example.mini_supermarket.config;

import com.example.mini_supermarket.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        System.out.println("🔍 === JWT FILTER DEBUG ===");
        System.out.println("🔍 Request URI: " + request.getRequestURI());
        System.out.println("🔍 Request Method: " + request.getMethod());
        
        try {
            // Lấy JWT token từ Authorization header
            String jwt = getJwtFromRequest(request);
            System.out.println("🔍 JWT Token found: " + (jwt != null ? "YES" : "NO"));
            
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                System.out.println("🔍 JWT Token is valid");
                
                // Lấy thông tin từ token
                String username = jwtUtil.getUsernameFromToken(jwt);
                String role = jwtUtil.getRoleFromToken(jwt);
                
                System.out.println("🔍 Username from token: " + username);
                System.out.println("🔍 Role from token: " + role);
                
                if (StringUtils.hasText(username) && StringUtils.hasText(role)) {
                    // Tạo authentication object
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                    
                    // Set authentication vào SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    System.out.println("✅ JWT Filter - Successfully authenticated user: " + username + " with role: " + role);
                    System.out.println("✅ SecurityContext authentication set: " + SecurityContextHolder.getContext().getAuthentication());
                } else {
                    System.out.println("❌ JWT Filter - Username or role is empty");
                }
            } else {
                System.out.println("❌ JWT Filter - Token is empty or invalid");
            }
        } catch (Exception e) {
            System.err.println("❌ JWT Filter error: " + e.getMessage());
            e.printStackTrace();
            // Không set authentication nếu có lỗi
        }
        
        System.out.println("🔍 === END JWT FILTER DEBUG ===");
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
