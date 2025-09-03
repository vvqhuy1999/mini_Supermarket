package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.repository.NguoiDungRepository;
import com.example.mini_supermarket.util.OtpGenerator;
import com.example.mini_supermarket.dto.OtpValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    
    private final BCryptPasswordEncoder passwordEncoder;
    private final NguoiDungRepository nguoiDungRepository;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${app.otp.expiration-minutes:5}")
    private int otpExpirationMinutes;
    
    @Value("${app.otp.max-attempts:3}")
    private int maxOtpAttempts;
    
    @Autowired
    public UserService(BCryptPasswordEncoder passwordEncoder, NguoiDungRepository nguoiDungRepository, 
                      JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.passwordEncoder = passwordEncoder;
        this.nguoiDungRepository = nguoiDungRepository;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }
    
    /**
     * Đăng ký người dùng mới với kiểm tra email và mã người dùng
     * @param nguoiDung Đối tượng người dùng cần đăng ký
     * @return NguoiDung đã được đăng ký thành công
     * @throws RuntimeException nếu email hoặc mã người dùng đã tồn tại
     */
    public NguoiDung registerUser(NguoiDung nguoiDung) {
        // Kiểm tra email đã tồn tại chưa
        if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
            throw new RuntimeException("Email đã tồn tại trong hệ thống!");
        }
        
        // Tạo mã người dùng tự động nếu chưa có
        if (nguoiDung.getMaNguoiDung() == null || nguoiDung.getMaNguoiDung().isEmpty()) {
            String maNguoiDung = generateMaNguoiDung();
            nguoiDung.setMaNguoiDung(maNguoiDung);
        } else {
            // Kiểm tra mã người dùng đã tồn tại chưa (bao gồm cả đã xóa)
            if (nguoiDungRepository.existsByMaNguoiDungIncludeDeleted(nguoiDung.getMaNguoiDung())) {
                throw new RuntimeException("Mã người dùng đã tồn tại trong hệ thống!");
            }
        }
        
        // Mã hóa mật khẩu
        nguoiDung = encryptPassword(nguoiDung);
        
        // Đặt giá trị mặc định
        nguoiDung.setIsDeleted(false);
        if (nguoiDung.getVaiTro() == null) {
            nguoiDung.setVaiTro(3); // Mặc định là khách hàng
        }
        
        // Lưu vào database
        return nguoiDungRepository.save(nguoiDung);
    }
    
    /**
     * Kiểm tra mã người dùng có hợp lệ không
     * @param maNguoiDung Mã người dùng cần kiểm tra
     * @return true nếu mã hợp lệ, false nếu không hợp lệ
     */
    public boolean isValidMaNguoiDung(String maNguoiDung) {
        if (maNguoiDung == null || maNguoiDung.isEmpty()) {
            return false;
        }
        
        // Kiểm tra độ dài (tối thiểu 3 ký tự, tối đa 10 ký tự)
        if (maNguoiDung.length() < 3 || maNguoiDung.length() > 10) {
            return false;
        }
        
        // Kiểm tra chỉ chứa chữ cái, số và dấu gạch dưới
        String maNguoiDungRegex = "^[a-zA-Z0-9_]+$";
        return maNguoiDung.matches(maNguoiDungRegex);
    }
    
    /**
     * Kiểm tra mã người dùng đã tồn tại chưa (chưa bị xóa)
     * @param maNguoiDung Mã người dùng cần kiểm tra
     * @return true nếu mã đã tồn tại, false nếu chưa tồn tại
     */
    public boolean isMaNguoiDungExists(String maNguoiDung) {
        return nguoiDungRepository.existsByMaNguoiDung(maNguoiDung);
    }
    
    /**
     * Kiểm tra mã người dùng đã tồn tại chưa (bao gồm cả đã xóa)
     * @param maNguoiDung Mã người dùng cần kiểm tra
     * @return true nếu mã đã tồn tại, false nếu chưa tồn tại
     */
    public boolean isMaNguoiDungExistsIncludeDeleted(String maNguoiDung) {
        return nguoiDungRepository.existsByMaNguoiDungIncludeDeleted(maNguoiDung);
    }
    
    /**
     * Tìm người dùng theo mã người dùng (chưa bị xóa)
     * @param maNguoiDung Mã người dùng cần tìm
     * @return Optional<NguoiDung> nếu tìm thấy, Optional.empty() nếu không tìm thấy
     */
    public Optional<NguoiDung> findByMaNguoiDung(String maNguoiDung) {
        return nguoiDungRepository.findActiveById(maNguoiDung);
    }
    
    /**
     * Tìm người dùng theo mã người dùng (bao gồm cả đã xóa)
     * @param maNguoiDung Mã người dùng cần tìm
     * @return Optional<NguoiDung> nếu tìm thấy, Optional.empty() nếu không tìm thấy
     */
    public Optional<NguoiDung> findByMaNguoiDungIncludeDeleted(String maNguoiDung) {
        return nguoiDungRepository.findByIdIncludeDeleted(maNguoiDung);
    }
    
    /**
     * Kiểm tra email có hợp lệ không
     * @param email Email cần kiểm tra
     * @return true nếu email hợp lệ, false nếu không hợp lệ
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        // Kiểm tra định dạng email cơ bản
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Kiểm tra email đã tồn tại chưa
     * @param email Email cần kiểm tra
     * @return true nếu email đã tồn tại, false nếu chưa tồn tại
     */
    public boolean isEmailExists(String email) {
        return nguoiDungRepository.existsByEmail(email);
    }
    
    /**
     * Tìm người dùng theo email
     * @param email Email cần tìm
     * @return Optional<NguoiDung> nếu tìm thấy, Optional.empty() nếu không tìm thấy
     */
    public Optional<NguoiDung> findByEmail(String email) {
        return nguoiDungRepository.findByEmail(email);
    }
    
    /**
     * Tạo mã người dùng tự động
     * @return Mã người dùng được tạo
     */
    private String generateMaNguoiDung() {
        // Tạo mã người dùng với format: ND + 8 ký tự ngẫu nhiên
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "ND" + uuid.toUpperCase();
    }
    
    /**
     * Mã hóa mật khẩu khi đăng ký
     * @param nguoiDung Đối tượng người dùng cần mã hóa mật khẩu
     * @return NguoiDung với mật khẩu đã được mã hóa
     */
    public NguoiDung encryptPassword(NguoiDung nguoiDung) {
        if (nguoiDung != null && nguoiDung.getMatKhau() != null && !nguoiDung.getMatKhau().isEmpty()) {
            String encryptedPassword = passwordEncoder.encode(nguoiDung.getMatKhau());
            nguoiDung.setMatKhau(encryptedPassword);
        }
        return nguoiDung;
    }
    
    /**
     * Kiểm tra mật khẩu có khớp không
     * @param rawPassword Mật khẩu gốc (chưa mã hóa)
     * @param encodedPassword Mật khẩu đã mã hóa
     * @return true nếu mật khẩu khớp, false nếu không khớp
     */
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * Tạo mật khẩu mã hóa từ mật khẩu gốc
     * @param rawPassword Mật khẩu gốc
     * @return Mật khẩu đã mã hóa
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    /**
     * Đổi mật khẩu cho người dùng
     * @param email Email người dùng
     * @param oldPassword Mật khẩu cũ
     * @param newPassword Mật khẩu mới
     * @return true nếu đổi mật khẩu thành công
     * @throws RuntimeException nếu có lỗi xảy ra
     */
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        try {
            // Tìm người dùng theo email
            Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByEmail(email);
            
            if (nguoiDungOpt.isEmpty()) {
                throw new RuntimeException("Không tìm thấy người dùng với email: " + email);
            }
            
            NguoiDung nguoiDung = nguoiDungOpt.get();
            
            // Kiểm tra tài khoản có bị xóa không
            if (nguoiDung.getIsDeleted()) {
                throw new RuntimeException("Tài khoản đã bị xóa!");
            }
            
            // Kiểm tra mật khẩu cũ có đúng không
            if (!matchesPassword(oldPassword, nguoiDung.getMatKhau())) {
                throw new RuntimeException("Mật khẩu cũ không chính xác!");
            }
            
            // Kiểm tra mật khẩu mới có khác mật khẩu cũ không
            if (matchesPassword(newPassword, nguoiDung.getMatKhau())) {
                throw new RuntimeException("Mật khẩu mới phải khác mật khẩu cũ!");
            }
            
            // Mã hóa mật khẩu mới
            String encodedNewPassword = encodePassword(newPassword);
            nguoiDung.setMatKhau(encodedNewPassword);
            
            // Lưu vào database
            nguoiDungRepository.save(nguoiDung);
            
            return true;
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đổi mật khẩu: " + e.getMessage());
        }
    }
    
    /**
     * Tạo và gửi OTP qua email
     * @param email Email người dùng
     * @return true nếu gửi thành công
     * @throws RuntimeException nếu có lỗi xảy ra
     */
    public boolean generateAndSendOtp(String email) {
        try {
            // Kiểm tra email có tồn tại không
            Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByEmail(email);
            if (nguoiDungOpt.isEmpty()) {
                throw new RuntimeException("Email không tồn tại trong hệ thống!");
            }
            
            NguoiDung nguoiDung = nguoiDungOpt.get();
            
            // Kiểm tra tài khoản có bị xóa không
            if (nguoiDung.getIsDeleted()) {
                throw new RuntimeException("Tài khoản đã bị xóa!");
            }
            
            // Kiểm tra số lần thử OTP
            if (nguoiDung.getOtpAttempts() != null && nguoiDung.getOtpAttempts() >= maxOtpAttempts) {
                throw new RuntimeException("Bạn đã thử OTP quá nhiều lần. Vui lòng thử lại sau!");
            }
            
            // Tạo OTP mới sử dụng OtpGenerator
            String otpCode = OtpGenerator.generateOtp(6);
            java.sql.Timestamp currentTime = new java.sql.Timestamp(System.currentTimeMillis());
            
            // Cập nhật OTP vào database
            nguoiDung.setOtpCode(otpCode);
            nguoiDung.setOtpGeneratedTime(currentTime);
            nguoiDung.setOtpAttempts(0); // Reset số lần thử
            nguoiDungRepository.save(nguoiDung);
            
            // Gửi email OTP
            sendOtpEmail(email, otpCode);
            
            return true;
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo OTP: " + e.getMessage());
        }
    }
    
    /**
     * Xác thực OTP và trả về kết quả chi tiết
     * @param email Email người dùng
     * @param inputOtp Mã OTP người dùng nhập
     * @return OtpValidationResult chứa kết quả validation
     * @throws RuntimeException nếu có lỗi xảy ra
     */
    public OtpValidationResult validateOtp(String email, String inputOtp) {
        try {
            // Tìm người dùng theo email
            Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByEmail(email);
            if (nguoiDungOpt.isEmpty()) {
                return new OtpValidationResult(false, "Người dùng không tồn tại");
            }
            
            NguoiDung nguoiDung = nguoiDungOpt.get();
            
            // Kiểm tra tài khoản có bị xóa không
            if (nguoiDung.getIsDeleted()) {
                return new OtpValidationResult(false, "Tài khoản đã bị xóa");
            }
            
            // Kiểm tra số lần thử
            if (nguoiDung.getOtpAttempts() >= maxOtpAttempts) {
                nguoiDung.resetOtp();
                nguoiDungRepository.save(nguoiDung);
                return new OtpValidationResult(false, "OTP đã bị khóa do thử quá nhiều lần");
            }
            
            // Kiểm tra thời gian hết hạn
            if (nguoiDung.isOtpExpired()) {
                nguoiDung.resetOtp();
                nguoiDungRepository.save(nguoiDung);
                return new OtpValidationResult(false, "OTP đã hết hạn");
            }
            
            // Kiểm tra mã OTP
            if (!nguoiDung.getOtpCode().equals(inputOtp)) {
                nguoiDung.incrementOtpAttempts();
                nguoiDungRepository.save(nguoiDung);
                return new OtpValidationResult(false, "OTP không đúng");
            }
            
            // OTP hợp lệ - tạo reset token
            String resetToken = UUID.randomUUID().toString();
            nguoiDung.setResetPasswordToken(resetToken);
            nguoiDung.setResetPasswordTokenExpiry(new java.sql.Timestamp(
                System.currentTimeMillis() + (24 * 60 * 60 * 1000))); // 24 giờ
            nguoiDung.resetOtp(); // Xóa OTP data
            nguoiDungRepository.save(nguoiDung);
            
            return new OtpValidationResult(true, "OTP hợp lệ", resetToken);
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xác thực OTP: " + e.getMessage());
        }
    }
    
    /**
     * Reset mật khẩu bằng reset token
     * @param resetToken Token reset mật khẩu
     * @param newPassword Mật khẩu mới
     * @return true nếu reset thành công
     * @throws RuntimeException nếu có lỗi xảy ra
     */
    public boolean resetPassword(String resetToken, String newPassword) {
        try {
            // Tìm người dùng theo reset token
            List<NguoiDung> allUsers = nguoiDungRepository.findAll();
            NguoiDung nguoiDung = null;
            
            for (NguoiDung user : allUsers) {
                if (resetToken.equals(user.getResetPasswordToken())) {
                    nguoiDung = user;
                    break;
                }
            }
            
            if (nguoiDung == null) {
                throw new RuntimeException("Token reset không hợp lệ!");
            }
            
            // Kiểm tra token có hết hạn không
            if (nguoiDung.isResetTokenExpired()) {
                throw new RuntimeException("Token reset đã hết hạn!");
            }
            
            // Kiểm tra tài khoản có bị xóa không
            if (nguoiDung.getIsDeleted()) {
                throw new RuntimeException("Tài khoản đã bị xóa!");
            }
            
            // Mã hóa mật khẩu mới
            String encodedNewPassword = encodePassword(newPassword);
            nguoiDung.setMatKhau(encodedNewPassword);
            
            // Xóa reset token
            nguoiDung.setResetPasswordToken(null);
            nguoiDung.setResetPasswordTokenExpiry(null);
            
            // Lưu mật khẩu mới
            nguoiDungRepository.save(nguoiDung);
            
            return true;
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi reset mật khẩu: " + e.getMessage());
        }
    }
    
    /**
     * Gửi email chứa OTP sử dụng Thymeleaf template
     * @param email Email người nhận
     * @param otpCode Mã OTP
     */
    private void sendOtpEmail(String email, String otpCode) {
        try {
            // Tạo context cho Thymeleaf template
            Context context = new Context();
            context.setVariable("userName", extractUserName(email));
            context.setVariable("otpCode", otpCode);
            context.setVariable("expirationTime", otpExpirationMinutes);
            context.setVariable("currentYear", java.time.LocalDateTime.now().getYear());
            
            // Render HTML từ template
            String htmlContent = templateEngine.process("email/otp-template", context);
            
            // Tạo MimeMessage
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(email);
            helper.setSubject("🔐 Mã xác thực OTP - Mini Supermarket");
            helper.setText(htmlContent, true); // true = HTML content
            
            // Gửi email
            javaMailSender.send(message);
            
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi gửi email: " + e.getMessage());
        }
    }
    
    /**
     * Trích xuất tên người dùng từ email
     * @param email Email người dùng
     * @return Tên người dùng
     */
    private String extractUserName(String email) {
        if (email == null || email.isEmpty()) {
            return "Người dùng";
        }
        
        // Lấy phần trước @ trong email
        String[] parts = email.split("@");
        if (parts.length > 0) {
            String username = parts[0];
            // Viết hoa chữ cái đầu
            return username.substring(0, 1).toUpperCase() + username.substring(1);
        }
        
        return "Người dùng";
    }
}
