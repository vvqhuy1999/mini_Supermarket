package com.example.mini_supermarket.config;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.HmacUtils; // Thêm thư viện Apache Commons Codec
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Component
public class VNPayConfig {

    @Value("${vnpay.pay-url}")
    private String vnp_PayUrl; // Loại bỏ static

    @Value("${vnpay.return-url}")
    private String vnp_ReturnUrl; // Loại bỏ static

    @Value("${vnpay.tmn-code}")
    private String vnp_TmnCode; // Loại bỏ static

    @Value("${vnpay.hash-secret}")
    private String vnp_HashSecret; // Loại bỏ static

    // Getter để truy cập các giá trị (nếu cần)


    public String getVnp_PayUrl() {
        return vnp_PayUrl;
    }

    public String getVnp_ReturnUrl() {
        return vnp_ReturnUrl;
    }

    public String getVnp_TmnCode() {
        return vnp_TmnCode;
    }

    public String getVnp_HashSecret() {
        return vnp_HashSecret;
    }

    // Phương thức hỗ trợ tạo mã giao dịch ngẫu nhiên
    public String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Phương thức lấy địa chỉ IP của client
    public String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
                try {
                    ipAddress = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    ipAddress = "127.0.0.1";
                }
            }
        }
        return ipAddress;
    }

    // Phương thức tạo chữ ký bảo mật (HMAC-SHA512) sử dụng Apache Commons Codec
    public String hmacSHA512(String key, String data) {
        try {
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException("Key must not be null or empty");
            }
            if (data == null || data.isEmpty()) {
                throw new IllegalArgumentException("Data must not be null or empty");
            }
            return HmacUtils.hmacSha512Hex(key.getBytes(StandardCharsets.UTF_8), data.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input for HMAC-SHA512: " + e.getMessage());
            throw new RuntimeException("Error creating HMAC-SHA512: Invalid input", e);
        } catch (Exception e) {
            System.err.println("Unexpected error in HMAC-SHA512: " + e.getMessage());
            throw new RuntimeException("Error creating HMAC-SHA512", e);
        }
    }
}