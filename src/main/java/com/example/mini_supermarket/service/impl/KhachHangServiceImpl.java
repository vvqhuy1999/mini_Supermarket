package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.KhachHangRepository;
import com.example.mini_supermarket.entity.KhachHang;
import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.service.KhachHangService;
import com.example.mini_supermarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KhachHangServiceImpl implements KhachHangService {
    private final KhachHangRepository khachHangRepository;
    private final UserService userService;

    public KhachHangServiceImpl(KhachHangRepository khachHangRepository, UserService userService) {
        this.khachHangRepository = khachHangRepository;
        this.userService = userService;
    }

    @Override
    public List<KhachHang> findAll() {
        return khachHangRepository.findAll();
    }

    @Override
    public List<KhachHang> findAllActive() {
        return khachHangRepository.findAllActive();
    }

    @Override
    public KhachHang findById(String theId) {
        Optional<KhachHang> result = khachHangRepository.findById(theId);
        KhachHang theKhachHang = null;

        if (result.isPresent()) {
            theKhachHang = result.get();
        } else {
            throw new RuntimeException("Did not find KhachHang id - " + theId);
        }
        return theKhachHang;
    }

    @Override
    public KhachHang findActiveById(String theId) {
        Optional<KhachHang> result = khachHangRepository.findActiveById(theId);
        KhachHang theKhachHang = null;

        if (result.isPresent()) {
            theKhachHang = result.get();
        } else {
            throw new RuntimeException("Did not find active KhachHang id - " + theId);
        }
        return theKhachHang;
    }

    @Override
    public KhachHang save(KhachHang theKhachHang) {
        return khachHangRepository.save(theKhachHang);
    }

    @Override
    public void deleteById(String theId) {
        khachHangRepository.deleteById(theId);
    }

    @Override
    public void softDeleteById(String theId) {
        Optional<KhachHang> result = khachHangRepository.findByIdIncludeDeleted(theId);
        
        if (result.isPresent()) {
            KhachHang khachHang = result.get();
            khachHang.setIsDeleted(true);
            khachHangRepository.save(khachHang);
        } else {
            throw new RuntimeException("Did not find KhachHang id - " + theId);
        }
    }

    @Override
    public KhachHang update(KhachHang khachHang) {
        Optional<KhachHang> existingKhachHang = khachHangRepository.findById(khachHang.getMaKH());

        if (!existingKhachHang.isPresent()) {
            throw new RuntimeException("Không tìm thấy khách hàng với ID - " + khachHang.getMaKH());
        }

        return khachHangRepository.save(khachHang);
    }

    @Override
    public KhachHang registerCustomerAccount(String email, String matKhau, String hoTen, String sdt, String diaChi) {
        try {
            // 1. Tạo tài khoản NguoiDung trước
            NguoiDung nguoiDung = new NguoiDung();
            nguoiDung.setEmail(email);
            nguoiDung.setMatKhau(matKhau);
            nguoiDung.setVaiTro(3); // Vai trò khách hàng
            
            // Đăng ký người dùng (UserService sẽ handle validation và mã hóa password)
            NguoiDung registeredUser = userService.registerUser(nguoiDung);
            
            // 2. Tạo thông tin KhachHang
            KhachHang khachHang = new KhachHang();
            khachHang.setMaKH(generateMaKhachHang()); // Tạo mã khách hàng tự động
            khachHang.setNguoiDung(registeredUser); // Liên kết với NguoiDung
            khachHang.setHoTen(hoTen);
            khachHang.setSdt(sdt);
            khachHang.setDiaChi(diaChi);
            khachHang.setDiemTichLuy(0); // Mặc định 0 điểm
            khachHang.setLoaiKhachHang("Thường"); // Mặc định loại thường
            khachHang.setNgayDangKy(LocalDateTime.now());
            khachHang.setIsDeleted(false);
            
            // 3. Lưu thông tin khách hàng
            return khachHangRepository.save(khachHang);
            
        } catch (RuntimeException e) {
            // Ném lại runtime exception từ UserService
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo tài khoản khách hàng: " + e.getMessage());
        }
    }
    
    /**
     * Tạo mã khách hàng tự động
     * @return Mã khách hàng duy nhất
     */
    private String generateMaKhachHang() {
        String prefix = "KH";
        String uuid;
        String maKH;
        
        // Lặp để đảm bảo mã không trùng
        do {
            uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            maKH = prefix + uuid.toUpperCase();
        } while (khachHangRepository.existsByMaKH(maKH));
        
        return maKH;
    }
} 
