package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.dto.*;
import com.example.mini_supermarket.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.repository.NguoiDungRepository;
import com.example.mini_supermarket.dto.OtpValidationResult;

@RestController
@RequestMapping("/api/forgot-password")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Forgot Password", description = "API quên mật khẩu và xác thực OTP")
public class ForgotPasswordController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    
    /**
     * Gửi OTP qua email
     */
    @Operation(summary = "Gửi OTP", description = "Gửi mã OTP qua email để xác thực quên mật khẩu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gửi OTP thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc lỗi gửi OTP"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping("/send-otp")
    public ResponseEntity<ForgotPasswordResponse> sendOtp(
            @Parameter(description = "Request gửi OTP", required = true)
            @Valid @RequestBody SendOtpRequest request) {
        try {
            boolean success = userService.generateAndSendOtp(request.getEmail());
            
            if (success) {
                ForgotPasswordResponse response = new ForgotPasswordResponse(true, 
                    "OTP đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư.");
                return ResponseEntity.ok(response);
            } else {
                ForgotPasswordResponse response = new ForgotPasswordResponse(false, null, 
                    "Không thể gửi OTP. Vui lòng kiểm tra email và thử lại.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (RuntimeException e) {
            ForgotPasswordResponse response = new ForgotPasswordResponse(false, null, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ForgotPasswordResponse response = new ForgotPasswordResponse(false, null, "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Xác thực OTP
     */
    @Operation(summary = "Xác thực OTP", description = "Xác thực mã OTP và tạo reset token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xác thực OTP thành công"),
            @ApiResponse(responseCode = "400", description = "OTP không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping("/verify-otp")
    public ResponseEntity<ForgotPasswordResponse> verifyOtp(
            @Parameter(description = "Request xác thực OTP", required = true)
            @RequestBody SendOtpRequest request) {
        
        try {
            OtpValidationResult result = userService.validateOtp(request.getEmail(), request.getOtpCode());
            
            if (result.isValid()) {
                Map<String, String> data = new HashMap<>();
                data.put("resetToken", result.getResetToken());
                ForgotPasswordResponse response = new ForgotPasswordResponse(true, result.getMessage(), data);
                return ResponseEntity.ok(response);
            } else {
                ForgotPasswordResponse response = new ForgotPasswordResponse(false, null, result.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
        } catch (RuntimeException e) {
            ForgotPasswordResponse response = new ForgotPasswordResponse(false, null, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ForgotPasswordResponse response = new ForgotPasswordResponse(false, null, "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Reset mật khẩu - Endpoint này xử lý cả 2 trường hợp:
     * 1. Reset bằng reset token (truyền thống)
     * 2. Reset bằng OTP + mật khẩu mới (từ frontend)
     */
    @Operation(summary = "Reset mật khẩu", description = "Đặt lại mật khẩu bằng reset token hoặc OTP + mật khẩu mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset mật khẩu thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<ForgotPasswordResponse> resetPassword(
            @Parameter(description = "Request reset mật khẩu", required = true)
            @RequestBody ResetPasswordRequest request) {
        
        try {
            // Validation cơ bản
            if (request.getNewPassword() == null || !request.getNewPassword().equals(request.getConfirmPassword())) {
                ForgotPasswordResponse response = new ForgotPasswordResponse(false, null, 
                    "Mật khẩu mới và xác nhận mật khẩu không khớp!");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Trường hợp 1: Có resetToken (cách truyền thống)
            if (request.getResetToken() != null && !request.getResetToken().trim().isEmpty()) {
                boolean success = userService.resetPassword(request.getResetToken(), request.getNewPassword());
                
                if (success) {
                    ForgotPasswordResponse response = new ForgotPasswordResponse(true, 
                        "Mật khẩu đã được đặt lại thành công.");
                    return ResponseEntity.ok(response);
                } else {
                    ForgotPasswordResponse response = new ForgotPasswordResponse(false, null, 
                        "Token không hợp lệ hoặc đã hết hạn.");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            // Trường hợp 2: Có OTP (từ frontend)
            else if (request.getEmail() != null && request.getOtpCode() != null) {
                // Kiểm tra xem có resetToken nào đang active không
                Optional<NguoiDung> userWithToken = nguoiDungRepository.findByEmail(request.getEmail());
                if (userWithToken.isPresent() && userWithToken.get().getResetPasswordToken() != null) {
                    // Nếu đã có resetToken, sử dụng luôn
                    boolean success = userService.resetPassword(userWithToken.get().getResetPasswordToken(), request.getNewPassword());
                    
                    if (success) {
                        ForgotPasswordResponse response = new ForgotPasswordResponse(true, 
                            "Mật khẩu đã được đặt lại thành công.");
                        return ResponseEntity.ok(response);
                    } else {
                        ForgotPasswordResponse response = new ForgotPasswordResponse(false, null, 
                            "Không thể đặt lại mật khẩu. Vui lòng thử lại.");
                        return ResponseEntity.badRequest().body(response);
                    }
                } else {
                    // Nếu chưa có resetToken, xác thực OTP trước
                    OtpValidationResult otpResult = userService.validateOtp(request.getEmail(), request.getOtpCode());
                    
                    if (!otpResult.isValid()) {
                        ForgotPasswordResponse response = new ForgotPasswordResponse(false, null, otpResult.getMessage());
                        return ResponseEntity.badRequest().body(response);
                    }
                    
                    // Reset mật khẩu bằng reset token từ OTP
                    boolean success = userService.resetPassword(otpResult.getResetToken(), request.getNewPassword());
                    
                    if (success) {
                        ForgotPasswordResponse response = new ForgotPasswordResponse(true, 
                            "Mật khẩu đã được đặt lại thành công.");
                        return ResponseEntity.ok(response);
                    } else {
                        ForgotPasswordResponse response = new ForgotPasswordResponse(false, null, 
                            "Không thể đặt lại mật khẩu. Vui lòng thử lại.");
                        return ResponseEntity.badRequest().body(response);
                    }
                }
            }
            // Trường hợp 3: Không có gì
            else {
                ForgotPasswordResponse response = new ForgotPasswordResponse(false, null, 
                    "Vui lòng cung cấp resetToken hoặc email + OTP để reset mật khẩu.");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (RuntimeException e) {
            ForgotPasswordResponse response = new ForgotPasswordResponse(false, null, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ForgotPasswordResponse response = new ForgotPasswordResponse(false, null, "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
