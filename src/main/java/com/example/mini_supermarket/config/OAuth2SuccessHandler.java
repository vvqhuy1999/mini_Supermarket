package com.example.mini_supermarket.config;

import com.example.mini_supermarket.dto.AuthenticationResponse;
import com.example.mini_supermarket.service.OAuth2Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
    
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    
    @Value("${oauth2.frontend.customer.base-url:http://localhost:3000}")
    private String customerFrontendUrl;
    
    @Value("${oauth2.frontend.employee.base-url:http://localhost:5173}")
    private String employeeFrontendUrl;
    
    @Value("${oauth2.frontend.success-path:/oauth2/success}")
    private String frontendSuccessPath;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        
        try {
            // Log OAuth2 details để debug
            logOAuth2Details(authentication, oauth2User);
            
            // Xác định provider và xử lý login
            AuthenticationResponse authResponse = processOAuth2Login(oauth2User);
            
            if (authResponse.isAuthenticated()) {
                // Xác định role của user để redirect về đúng frontend
                String baseUrl = determineFrontendUrl(authentication);
                String redirectUrl = buildRedirectUrl(authResponse, baseUrl);
                
                System.out.println("✅ OAuth2 Success - Redirecting to frontend: " + redirectUrl);
                response.sendRedirect(redirectUrl);
            } else {
                // Redirect to error page (mặc định về customer frontend)
                String errorUrl = customerFrontendUrl + "/login?error=authentication_failed&message=" + 
                    URLEncoder.encode(authResponse.getMessage(), StandardCharsets.UTF_8);
                response.sendRedirect(errorUrl);
            }
            
        } catch (Exception e) {
            System.err.println("❌ OAuth2 Success Handler Error: " + e.getMessage());
            e.printStackTrace();
            
            // Redirect to error page (mặc định về customer frontend)
            String errorUrl = customerFrontendUrl + "/login?error=server_error&message=" + 
                URLEncoder.encode("Internal server error during OAuth2 processing", StandardCharsets.UTF_8);
            response.sendRedirect(errorUrl);
        }
    }
    
    private void logOAuth2Details(Authentication authentication, OAuth2User oauth2User) {
        System.out.println("🔍 === OAuth2 DEBUG INFO ===");
        System.out.println("🔍 Authentication Type: " + authentication.getClass().getSimpleName());
        System.out.println("🔍 OAuth2User Name: " + oauth2User.getName());
        System.out.println("🔍 OAuth2User Attributes: " + oauth2User.getAttributes());
        
        // Lấy access token nếu có
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken) authentication;
            String clientRegistrationId = oauth2Auth.getAuthorizedClientRegistrationId();
            
            System.out.println("🔍 Client Registration ID: " + clientRegistrationId);
            
            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                clientRegistrationId, 
                oauth2Auth.getName()
            );
            
            if (authorizedClient != null) {
                String oauth2AccessToken = authorizedClient.getAccessToken().getTokenValue();
                System.out.println("🔍 OAuth2 Access Token (Google/Facebook): " + oauth2AccessToken);
                System.out.println("🔍 OAuth2 Token Type: " + authorizedClient.getAccessToken().getTokenType().getValue());
                System.out.println("🔍 OAuth2 Expires At: " + authorizedClient.getAccessToken().getExpiresAt());
                
                // Phân tích format token
                String[] tokenParts = oauth2AccessToken.split("\\.");
                System.out.println("🔍 OAuth2 Token Parts: " + tokenParts.length + " (should NOT be 3 for OAuth2 access token)");
                
                if (authorizedClient.getRefreshToken() != null) {
                    System.out.println("🔍 OAuth2 Refresh Token: " + authorizedClient.getRefreshToken().getTokenValue());
                }
                
                System.out.println("⚠️ IMPORTANT: OAuth2 Access Token above is for Google/Facebook API calls only!");
                System.out.println("⚠️ Do NOT use it for our app authentication - use JWT token from redirect URL instead!");
            } else {
                System.out.println("🔍 No authorized client found");
            }
        }
        
        System.out.println("🔍 === END OAuth2 DEBUG INFO ===");
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
    
    private String buildRedirectUrl(AuthenticationResponse authResponse, String baseUrl) {
        StringBuilder url = new StringBuilder();
        url.append(baseUrl).append(frontendSuccessPath);
        
        String jwtToken = authResponse.getToken();
        
        // Log JWT token info để debug
        System.out.println("🎫 === JWT TOKEN INFO ===");
        System.out.println("🎫 JWT Token: " + jwtToken);
        String[] jwtParts = jwtToken.split("\\.");
        System.out.println("🎫 JWT Parts: " + jwtParts.length + " (should be 3 for valid JWT)");
        if (jwtParts.length == 3) {
            System.out.println("✅ CORRECT: This is a valid JWT token format");
        } else {
            System.out.println("❌ ERROR: This is NOT a valid JWT token format!");
        }
        System.out.println("🎫 Use this JWT token for app authentication and logout");
        System.out.println("🎫 === END JWT TOKEN INFO ===");
        
        // Thêm parameters
        url.append("?success=true");
        url.append("&token=").append(URLEncoder.encode(jwtToken, StandardCharsets.UTF_8));
        url.append("&jwt_token=").append(URLEncoder.encode(jwtToken, StandardCharsets.UTF_8)); // Duplicate để rõ ràng
        url.append("&token_type=JWT");
        url.append("&email=").append(URLEncoder.encode(authResponse.getUser().getEmail(), StandardCharsets.UTF_8));
        url.append("&role=").append(URLEncoder.encode(authResponse.getRole(), StandardCharsets.UTF_8));
        url.append("&userId=").append(URLEncoder.encode(authResponse.getUser().getMaNguoiDung(), StandardCharsets.UTF_8));
        url.append("&message=").append(URLEncoder.encode(authResponse.getMessage(), StandardCharsets.UTF_8)); // Thêm message có chứa tên
        
        System.out.println("🔗 Redirect URL: " + url.toString());
        
        return url.toString();
    }
    
    /**
     * Xác định frontend URL dựa trên role của user
     * @param authentication Thông tin authentication của user
     * @return URL của frontend tương ứng
     */
    private String determineFrontendUrl(Authentication authentication) {
        // Kiểm tra role của user
        boolean isEmployee = authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_EMPLOYEE") || 
                                 authority.getAuthority().equals("ROLE_MANAGER"));
        
        if (isEmployee) {
            System.out.println("👨‍💼 User is Employee/Manager - Redirecting to employee frontend: " + employeeFrontendUrl);
            return employeeFrontendUrl;
        } else {
            System.out.println("🛒 User is Customer - Redirecting to customer frontend: " + customerFrontendUrl);
            return customerFrontendUrl;
        }
    }
}
