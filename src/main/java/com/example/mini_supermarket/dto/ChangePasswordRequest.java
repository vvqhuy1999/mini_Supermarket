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
@Schema(description = "Request đổi mật khẩu")
public class ChangePasswordRequest {
    
    @Schema(description = "Mật khẩu cũ", example = "123456", required = true)
    @NotBlank(message = "Mật khẩu cũ không được để trống")
    private String oldPassword;
    
    @Schema(description = "Mật khẩu mới", example = "newpassword123", required = true)
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    private String newPassword;
    
    @Schema(description = "Xác nhận mật khẩu mới", example = "newpassword123", required = true)
    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;
}
