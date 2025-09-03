package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "NguoiDung")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDung implements Serializable {
    @Id
    @Column(name = "MaNguoiDung", length = 50)
    private String maNguoiDung;

    @Column(name = "Email", length = 50, unique = true, nullable = false)
    private String email;

    @Column(name = "MatKhau", length = 255, nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Cho phép ghi từ JSON request, không trả về trong response
    private String matKhau;

    @Column(name = "Sub", length = 255)
    private String sub;

    @Column(name = "VaiTro", nullable = false)
    private Integer vaiTro = 3; // 0=Quản trị, 1=Quản lý, 2=Nhân viên, 3=Khách hàng

    @Column(name = "NgayTao")
    @Temporal(TemporalType.TIMESTAMP)
    private java.sql.Timestamp ngayTao;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KhachHang> khachHangs;

    @JsonIgnore
    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NhanVien> nhanViens;
    
    // OTP và Reset Password fields
    @Column(name = "otp_code", length = 6)
    private String otpCode;
    
    @Column(name = "otp_generated_time")
    @Temporal(TemporalType.TIMESTAMP)
    private java.sql.Timestamp otpGeneratedTime;
    
    @Column(name = "otp_attempts")
    private Integer otpAttempts = 0;
    
    @Column(name = "reset_password_token", length = 255)
    private String resetPasswordToken;
    
    @Column(name = "reset_password_token_expiry")
    @Temporal(TemporalType.TIMESTAMP)
    private java.sql.Timestamp resetPasswordTokenExpiry;
    
    // Helper methods for OTP
    public boolean isOtpExpired() {
        if (otpGeneratedTime == null) return true;
        LocalDateTime generatedTime = otpGeneratedTime.toLocalDateTime();
        LocalDateTime expiryTime = generatedTime.plusMinutes(5); // 5 phút theo config
        return LocalDateTime.now().isAfter(expiryTime);
    }
    
    public boolean isOtpValid() {
        return otpCode != null && !otpCode.isEmpty() && !isOtpExpired() && otpAttempts < 3;
    }
    
    public void incrementOtpAttempts() {
        this.otpAttempts = (this.otpAttempts == null) ? 1 : this.otpAttempts + 1;
    }
    
    public void resetOtp() {
        this.otpCode = null;
        this.otpGeneratedTime = null;
        this.otpAttempts = 0;
    }
    
    public boolean isResetTokenExpired() {
        if (resetPasswordTokenExpiry == null) return true;
        LocalDateTime expiryTime = resetPasswordTokenExpiry.toLocalDateTime();
        return LocalDateTime.now().isAfter(expiryTime);
    }
    
    public boolean isResetTokenValid() {
        return resetPasswordToken != null && !resetPasswordToken.isEmpty() && !isResetTokenExpired();
    }
} 