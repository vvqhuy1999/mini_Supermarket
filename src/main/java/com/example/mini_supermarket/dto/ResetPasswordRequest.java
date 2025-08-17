package com.example.mini_supermarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request reset mật khẩu - có thể dùng resetToken hoặc OTP")
public class ResetPasswordRequest {
    
    @Schema(description = "Token reset mật khẩu (nếu có)", example = "abc123def456", required = false)
    private String resetToken;
    
    @Schema(description = "Email người dùng (nếu dùng OTP)", example = "user@example.com", required = false)
    private String email;
    
    @Schema(description = "Mã OTP (nếu dùng OTP)", example = "123456", required = false)
    private String otpCode;
    
    @Schema(description = "Mật khẩu mới", example = "newpassword123", required = true)
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    private String newPassword;
    
    @Schema(description = "Xác nhận mật khẩu mới", example = "newpassword123", required = true)
    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;
}
