package com.example.mini_supermarket.service;

import com.example.mini_supermarket.dto.AuthenticationResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public interface OAuth2Service {
    
    /**
     * Xử lý đăng nhập Google OAuth2
     */
    AuthenticationResponse processGoogleLogin(OAuth2User oauth2User);
    
    /**
     * Xử lý đăng nhập Facebook OAuth2
     */
    AuthenticationResponse processFacebookLogin(OAuth2User oauth2User);
    
    /**
     * Lấy cấu hình OAuth2
     */
    Map<String, String> getOAuth2Configuration();
} 