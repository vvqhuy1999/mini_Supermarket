package com.example.mini_supermarket.util;

import com.example.mini_supermarket.service.TokenBlacklistService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret:your-secret-key-here-make-it-long-enough-for-hs256}")
    private String secret;

    @Value("${jwt.expiration:86400}") // 24 hours default
    private long expiration;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    /**
     * Tạo JWT token cho người dùng
     * @param username Email hoặc mã người dùng
     * @param role Vai trò người dùng
     * @return JWT token string
     */
    public String generateToken(String username, String role) {
        try {
            // Đảm bảo secret key có độ dài đủ cho HS512 (ít nhất 512 bits = 64 bytes)
            byte[] secretBytes = getSecretBytes();
            
            // Tạo JWT claims
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .claim("role", role)
                    .issuer("mini-supermarket")
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plus(expiration, ChronoUnit.SECONDS)))
                    .build();

            // Tạo JWS header
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

            // Tạo signed JWT
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);

            // Ký JWT với secret key 
            JWSSigner signer = new MACSigner(secretBytes);
            signedJWT.sign(signer);

            String token = signedJWT.serialize();
            logger.debug("Generated JWT token for user: {} with role: {}", username, role);
            
            return token;

        } catch (JOSEException e) {
            logger.error("Lỗi tạo JWT token: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi tạo JWT token: " + e.getMessage());
        }
    }

    /**
     * Validate JWT token
     * @param token JWT token string
     * @return true nếu token hợp lệ, false nếu không
     */
    public boolean validateToken(String token) {
        try {
            // Kiểm tra token null hoặc rỗng
            if (token == null || token.trim().isEmpty()) {
                logger.debug("Token is null or empty");
                return false;
            }
            
            // Kiểm tra format cơ bản của JWT (phải có ít nhất 2 dấu chấm)
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                logger.warn("Token không đúng định dạng JWT. Cần 3 phần, nhưng có: {}", parts.length);
                return false;
            }
            
            // Kiểm tra token có trong blacklist không
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                logger.warn("Token đã bị blacklist");
                return false;
            }
            
            // Parse JWT token
            SignedJWT signedJWT = SignedJWT.parse(token);
            
            // Đảm bảo secret key có độ dài đúng
            byte[] secretBytes = getSecretBytes();
            
            JWSVerifier verifier = new MACVerifier(secretBytes);
            boolean isValid = signedJWT.verify(verifier);
            boolean isNotExpired = !isTokenExpired(signedJWT);
            
            logger.debug("Token validation - Valid signature: {}, Not expired: {}", isValid, isNotExpired);
            
            return isValid && isNotExpired;
        } catch (ParseException e) {
            logger.warn("Token không thể parse: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Lỗi validate token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Lấy username từ JWT token
     * @param token JWT token string
     * @return username hoặc null nếu không hợp lệ
     */
    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String username = signedJWT.getJWTClaimsSet().getSubject();
            logger.debug("Extracted username from token: {}", username);
            return username;
        } catch (ParseException e) {
            logger.error("Lỗi parse token để lấy username: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Lấy role từ JWT token
     * @param token JWT token string
     * @return role hoặc null nếu không hợp lệ
     */
    public String getRoleFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String role = (String) signedJWT.getJWTClaimsSet().getClaim("role");
            logger.debug("Extracted role from token: {}", role);
            return role;
        } catch (ParseException e) {
            logger.error("Lỗi parse token để lấy role: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Kiểm tra token có hết hạn chưa
     * @param signedJWT SignedJWT object
     * @return true nếu đã hết hạn, false nếu chưa
     */
    private boolean isTokenExpired(SignedJWT signedJWT) {
        try {
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            boolean expired = expirationTime != null && expirationTime.before(new Date());
            if (expired) {
                logger.debug("Token đã hết hạn. Expiration time: {}", expirationTime);
            }
            return expired;
        } catch (ParseException e) {
            logger.error("Lỗi kiểm tra expiration time: {}", e.getMessage(), e);
            return true;
        }
    }

    /**
     * Lấy thời gian hết hạn của token
     * @param token JWT token string
     * @return Date object hoặc null nếu không hợp lệ
     */
    public Date getExpirationFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            logger.debug("Extracted expiration time from token: {}", expiration);
            return expiration;
        } catch (ParseException e) {
            logger.error("Lỗi parse token để lấy expiration time: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Decode và hiển thị thông tin payload của JWT token
     * @param token JWT token string
     * @return Map chứa thông tin payload hoặc null nếu không hợp lệ
     */
    public Map<String, Object> decodeTokenPayload(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            
            Map<String, Object> payloadInfo = new HashMap<>();
            payloadInfo.put("subject", claimsSet.getSubject());
            payloadInfo.put("role", claimsSet.getClaim("role"));
            payloadInfo.put("issuer", claimsSet.getIssuer());
            payloadInfo.put("issueTime", claimsSet.getIssueTime());
            payloadInfo.put("expirationTime", claimsSet.getExpirationTime());
            payloadInfo.put("allClaims", claimsSet.getClaims());
            

            
            return payloadInfo;
        } catch (ParseException e) {
            logger.error("Lỗi decode token payload: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Lấy secret key với độ dài đúng để đảm bảo tính nhất quán
     */
    private String getActualSecret() {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 64) {
            return secret + "additional-padding-to-make-it-long-enough-for-hs512-algorithm";
        }
        return secret;
    }
    
    /**
     * Lấy secret bytes với độ dài đúng cho HS512
     */
    private byte[] getSecretBytes() {
        return getActualSecret().getBytes(StandardCharsets.UTF_8);
    }
} 