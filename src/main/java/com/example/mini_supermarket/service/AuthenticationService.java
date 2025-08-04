package com.example.mini_supermarket.service;

import com.example.mini_supermarket.dto.AuthenticationResponse;
import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.repository.NguoiDungRepository;
import com.example.mini_supermarket.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthenticationService implements UserDetailsService {
    
    private final NguoiDungRepository nguoiDungRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @Autowired
    public AuthenticationService(NguoiDungRepository nguoiDungRepository, UserService userService, JwtUtil jwtUtil) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * Xác thực người dùng từ username và password
     * @param username Email hoặc mã người dùng
     * @param password Mật khẩu
     * @return AuthenticationResponse với thông tin xác thực
     */
    public AuthenticationResponse authenticate(String username, String password) {
        try {
            // Tìm người dùng theo email hoặc mã người dùng
            Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByEmail(username);
            
            if (nguoiDungOpt.isEmpty()) {
                // Nếu không tìm thấy theo email, thử tìm theo mã người dùng
                nguoiDungOpt = nguoiDungRepository.findActiveById(username);
            }
            
            if (nguoiDungOpt.isEmpty()) {
                return AuthenticationResponse.builder()
                        .authenticated(false)
                        .message("Tài khoản không tồn tại!")
                        .build();
            }
            
            NguoiDung nguoiDung = nguoiDungOpt.get();
            
            // Kiểm tra tài khoản có bị xóa không
            if (nguoiDung.getIsDeleted()) {
                return AuthenticationResponse.builder()
                        .authenticated(false)
                        .message("Tài khoản đã bị xóa!")
                        .build();
            }
            
            // Kiểm tra mật khẩu
            if (!userService.matchesPassword(password, nguoiDung.getMatKhau())) {
                return AuthenticationResponse.builder()
                        .authenticated(false)
                        .message("Mật khẩu không chính xác!")
                        .build();
            }
            
            // Xác thực thành công - tạo JWT token
            String role = getRoleName(nguoiDung.getVaiTro());
            String token = jwtUtil.generateToken(username, role);
            
            return AuthenticationResponse.builder()
                    .authenticated(true)
                    .message("Đăng nhập thành công!")
                    .user(nguoiDung)
                    .role(role)
                    .token(token)
                    .build();
                    
        } catch (Exception e) {
            return AuthenticationResponse.builder()
                    .authenticated(false)
                    .message("Lỗi xác thực: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Xác thực người dùng theo email và mật khẩu
     * @param email Email người dùng
     * @param password Mật khẩu
     * @return NguoiDung nếu xác thực thành công
     * @throws RuntimeException nếu xác thực thất bại
     */
    public NguoiDung authenticateUser(String email, String password) {
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByEmail(email);
        
        if (nguoiDungOpt.isEmpty()) {
            throw new RuntimeException("Email không tồn tại trong hệ thống!");
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        
        // Kiểm tra mật khẩu
        if (!userService.matchesPassword(password, nguoiDung.getMatKhau())) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }
        
        // Kiểm tra tài khoản có bị xóa không
        if (nguoiDung.getIsDeleted()) {
            throw new RuntimeException("Tài khoản đã bị xóa!");
        }
        
        return nguoiDung;
    }
    
    /**
     * Xác thực người dùng theo mã người dùng và mật khẩu
     * @param maNguoiDung Mã người dùng
     * @param password Mật khẩu
     * @return NguoiDung nếu xác thực thành công
     * @throws RuntimeException nếu xác thực thất bại
     */
    public NguoiDung authenticateUserByMaNguoiDung(String maNguoiDung, String password) {
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findActiveById(maNguoiDung);
        
        if (nguoiDungOpt.isEmpty()) {
            throw new RuntimeException("Mã người dùng không tồn tại trong hệ thống!");
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        
        // Kiểm tra mật khẩu
        if (!userService.matchesPassword(password, nguoiDung.getMatKhau())) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }
        
        return nguoiDung;
    }
    
    /**
     * Lấy thông tin vai trò dựa trên mã vai trò
     * @param vaiTro Mã vai trò
     * @return Tên vai trò
     */
    public String getRoleName(Integer vaiTro) {
        if (vaiTro == null) {
            return "CUSTOMER";
        }
        
        switch (vaiTro) {
            case 0:
                return "ADMIN";
            case 1:
                return "MANAGER";
            case 2:
                return "STAFF";
            case 3:
                return "CUSTOMER";
            default:
                return "CUSTOMER";
        }
    }
    
    /**
     * Lấy mô tả vai trò
     * @param vaiTro Mã vai trò
     * @return Mô tả vai trò
     */
    public String getRoleDescription(Integer vaiTro) {
        if (vaiTro == null) {
            return "Khách hàng";
        }
        
        switch (vaiTro) {
            case 0:
                return "Quản trị viên";
            case 1:
                return "Quản lý";
            case 2:
                return "Nhân viên";
            case 3:
                return "Khách hàng";
            default:
                return "Khách hàng";
        }
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm người dùng theo email
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByEmail(username);
        
        if (nguoiDungOpt.isEmpty()) {
            // Nếu không tìm thấy theo email, thử tìm theo mã người dùng
            nguoiDungOpt = nguoiDungRepository.findActiveById(username);
        }
        
        if (nguoiDungOpt.isEmpty()) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng với username: " + username);
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        
        // Kiểm tra tài khoản có bị xóa không
        if (nguoiDung.getIsDeleted()) {
            throw new UsernameNotFoundException("Tài khoản đã bị xóa: " + username);
        }
        
        // Tạo UserDetails với vai trò tương ứng
        String role = getRoleName(nguoiDung.getVaiTro());
        
        return User.builder()
                .username(nguoiDung.getEmail())
                .password(nguoiDung.getMatKhau())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
} 