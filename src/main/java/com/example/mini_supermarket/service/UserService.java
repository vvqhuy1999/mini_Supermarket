package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    
    private final BCryptPasswordEncoder passwordEncoder;
    private final NguoiDungRepository nguoiDungRepository;
    
    @Autowired
    public UserService(BCryptPasswordEncoder passwordEncoder, NguoiDungRepository nguoiDungRepository) {
        this.passwordEncoder = passwordEncoder;
        this.nguoiDungRepository = nguoiDungRepository;
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
}
