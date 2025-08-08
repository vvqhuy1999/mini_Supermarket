package com.example.mini_supermarket.config;

import com.example.mini_supermarket.dto.AuthenticationResponse;
import com.example.mini_supermarket.service.OAuth2Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    
    @Autowired
    private OAuth2Service oAuth2Service;
    
    @Value("${oauth2.frontend.base-url:http://localhost:3000}")
    private String frontendBaseUrl;
    
    @Value("${oauth2.frontend.success-path:/oauth2/success}")
    private String frontendSuccessPath;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        
        try {
            // Xác định provider và xử lý login
            AuthenticationResponse authResponse = processOAuth2Login(oauth2User);
            
            if (authResponse.isAuthenticated()) {
                // Tạo redirect URL với token và user info
                String redirectUrl = buildRedirectUrl(authResponse);
                
                System.out.println("✅ OAuth2 Success - Redirecting to frontend: " + redirectUrl);
                response.sendRedirect(redirectUrl);
            } else {
                // Redirect to error page
                String errorUrl = frontendBaseUrl + "/login?error=authentication_failed&message=" + 
                    URLEncoder.encode(authResponse.getMessage(), StandardCharsets.UTF_8);
                response.sendRedirect(errorUrl);
            }
            
        } catch (Exception e) {
            System.err.println("❌ OAuth2 Success Handler Error: " + e.getMessage());
            e.printStackTrace();
            
            // Redirect to error page
            String errorUrl = frontendBaseUrl + "/login?error=server_error&message=" + 
                URLEncoder.encode("Internal server error during OAuth2 processing", StandardCharsets.UTF_8);
            response.sendRedirect(errorUrl);
        }
    }
    
    private AuthenticationResponse processOAuth2Login(OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        // Xác định provider dựa trên attributes
        if (attributes.containsKey("sub") && attributes.containsKey("email_verified")) {
            // Google OAuth2
            return oAuth2Service.processGoogleLogin(oauth2User);
        } else if (attributes.containsKey("id") && !attributes.containsKey("email_verified")) {
            // Facebook OAuth2
            return oAuth2Service.processFacebookLogin(oauth2User);
        } else {
            // Fallback to Google
            return oAuth2Service.processGoogleLogin(oauth2User);
        }
    }
    
    private String buildRedirectUrl(AuthenticationResponse authResponse) {
        StringBuilder url = new StringBuilder();
        url.append(frontendBaseUrl).append(frontendSuccessPath);
        
        // Thêm parameters
        url.append("?success=true");
        url.append("&token=").append(URLEncoder.encode(authResponse.getToken(), StandardCharsets.UTF_8));
        url.append("&email=").append(URLEncoder.encode(authResponse.getUser().getEmail(), StandardCharsets.UTF_8));
        url.append("&role=").append(URLEncoder.encode(authResponse.getRole(), StandardCharsets.UTF_8));
        url.append("&userId=").append(URLEncoder.encode(authResponse.getUser().getMaNguoiDung(), StandardCharsets.UTF_8));
        
        return url.toString();
    }
}
