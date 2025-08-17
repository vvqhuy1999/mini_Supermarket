package com.example.mini_supermarket.dto;

import lombok.Data;

/**
 * DTO chứa kết quả validation OTP
 */
@Data
public class OtpValidationResult {
    private boolean valid;
    private String message;
    private String resetToken;
    
    /**
     * Constructor cho trường hợp validation thất bại
     */
    public OtpValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
        this.resetToken = null;
    }
    
    /**
     * Constructor cho trường hợp validation thành công
     */
    public OtpValidationResult(boolean valid, String message, String resetToken) {
        this.valid = valid;
        this.message = message;
        this.resetToken = resetToken;
    }
}
