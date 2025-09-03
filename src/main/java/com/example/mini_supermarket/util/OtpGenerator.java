package com.example.mini_supermarket.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Utility class để tạo mã OTP ngẫu nhiên
 */
public class OtpGenerator {
    private static final String NUMBERS = "0123456789";
    private static final Random RANDOM = new SecureRandom();
    
    /**
     * Tạo mã OTP với độ dài tùy chỉnh
     * @param length Độ dài mã OTP
     * @return Mã OTP
     */
    public static String generateOtp(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("OTP length must be positive");
        }
        
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        }
        return otp.toString();
    }
    
    /**
     * Tạo mã OTP mặc định 6 số
     * @return Mã OTP 6 số
     */
    public static String generateOtp() {
        return generateOtp(6); // Default 6 digits
    }
}
