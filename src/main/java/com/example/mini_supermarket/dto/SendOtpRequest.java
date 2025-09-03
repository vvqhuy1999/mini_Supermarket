package com.example.mini_supermarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request gửi và xác thực OTP")
public class SendOtpRequest {
    
    @Schema(description = "Email người dùng", example = "user@example.com", required = true)
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @Schema(description = "Mã OTP (chỉ cần khi xác thực)", example = "123456", required = false)
    @Size(min = 6, max = 6, message = "Mã OTP phải có đúng 6 số")
    private String otpCode;
}
