package com.example.mini_supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response cho API quên mật khẩu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordResponse {
    private boolean success;
    private String message;
    private String error;
    private Object data;
    
    public ForgotPasswordResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.error = null;
        this.data = null;
    }
    
    public ForgotPasswordResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.error = null;
        this.data = data;
    }
    
    public ForgotPasswordResponse(boolean success, String message, String error) {
        this.success = success;
        this.message = message;
        this.error = error;
        this.data = null;
    }
}
