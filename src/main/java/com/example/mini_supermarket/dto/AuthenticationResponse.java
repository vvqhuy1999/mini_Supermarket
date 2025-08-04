package com.example.mini_supermarket.dto;

import com.example.mini_supermarket.entity.NguoiDung;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    private boolean authenticated;
    private String message;
    private String token; // Có thể thêm JWT token sau
    private NguoiDung user; // Thông tin người dùng
    private String role; // Vai trò người dùng
} 