package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.KhachHangRepository;
import com.example.mini_supermarket.entity.KhachHang;
import com.example.mini_supermarket.entity.NguoiDung;
import com.example.mini_supermarket.service.KhachHangService;
import com.example.mini_supermarket.service.UserService;
import com.example.mini_supermarket.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.example.mini_supermarket.util.CodeGenerator;

@Service
public class KhachHangServiceImpl implements KhachHangService {
    private final KhachHangRepository khachHangRepository;
    private final UserService userService;
    private final NguoiDungService nguoiDungService;
    private final CacheManager cacheManager;

    public KhachHangServiceImpl(KhachHangRepository khachHangRepository, UserService userService, NguoiDungService nguoiDungService, CacheManager cacheManager) {
        this.khachHangRepository = khachHangRepository;
        this.userService = userService;
        this.nguoiDungService = nguoiDungService;
        this.cacheManager = cacheManager;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhachHang> findAll() {
        return khachHangRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhachHang> findAllActive() {
        return khachHangRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public KhachHang findActiveById(String theId) {
        try {
            // Kiểm tra ID có hợp lệ không
            if (theId == null || theId.trim().isEmpty() || "current".equals(theId.trim())) {
                System.out.println("⚠️ ID khách hàng không hợp lệ: " + theId);
                return null;
            }
            
            // Sử dụng findById thông thường rồi kiểm tra isDeleted
            Optional<KhachHang> result = khachHangRepository.findById(theId);
            KhachHang theKhachHang = null;

            if (result.isPresent()) {
                theKhachHang = result.get();
                // Kiểm tra khách hàng có bị xóa không
                if (theKhachHang.getIsDeleted()) {
                    System.out.println("⚠️ Khách hàng với ID " + theId + " đã bị xóa");
                    return null;
                }
                System.out.println("✅ Tìm thấy khách hàng active với ID: " + theId);
            } else {
                System.out.println("⚠️ Không tìm thấy khách hàng với ID: " + theId);
            }
            return theKhachHang;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm khách hàng với ID " + theId + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public KhachHang save(KhachHang theKhachHang) {
        KhachHang savedKhachHang = khachHangRepository.save(theKhachHang);
        clearKhachHangCache();
        return savedKhachHang;
    }

    @Override
    @Transactional
    public void deleteById(String theId) {
        khachHangRepository.deleteById(theId);
    }

    @Override
    @Transactional
    public void softDeleteById(String theId) {
        Optional<KhachHang> result = khachHangRepository.findByIdIncludeDeleted(theId);
        
        if (result.isPresent()) {
            KhachHang khachHang = result.get();
            khachHang.setIsDeleted(true);
            khachHangRepository.save(khachHang);
            clearKhachHangCache();
        } else {
            throw new RuntimeException("Did not find KhachHang id - " + theId);
        }
    }

    @Override
    @Transactional
    public KhachHang update(KhachHang khachHang) {
        Optional<KhachHang> existingKhachHang = khachHangRepository.findById(khachHang.getMaKH());

        if (!existingKhachHang.isPresent()) {
            throw new RuntimeException("Không tìm thấy khách hàng với ID - " + khachHang.getMaKH());
        }

        KhachHang updatedKhachHang = khachHangRepository.save(khachHang);
        clearKhachHangCache();
        return updatedKhachHang;
    }

    @Override
    @Transactional
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
            khachHang.setNgayDangKy(new Timestamp(System.currentTimeMillis()));
            khachHang.setIsDeleted(false);
            
            // 3. Lưu thông tin khách hàng
            KhachHang savedKhachHang = khachHangRepository.save(khachHang);
            clearKhachHangCache();
            return savedKhachHang;
            
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
        String maKH;
        
        // Lặp để đảm bảo mã không trùng
        do {
            maKH = CodeGenerator.generateMaKhachHang();
        } while (khachHangRepository.existsByMaKH(maKH));
        
        return maKH;
    }
    
    @Override
    @Transactional
    public KhachHang createCustomerFromOAuth2(NguoiDung nguoiDung, String hoTen) {
        try {
            // Kiểm tra xem khách hàng đã tồn tại chưa
            List<KhachHang> existingCustomers = khachHangRepository.findAllActive();
            for (KhachHang kh : existingCustomers) {
                if (kh.getNguoiDung() != null && 
                    kh.getNguoiDung().getMaNguoiDung().equals(nguoiDung.getMaNguoiDung())) {
                    System.out.println("🔍 Khách hàng đã tồn tại cho NguoiDung: " + nguoiDung.getMaNguoiDung());
                    return kh; // Trả về khách hàng đã tồn tại
                }
            }
            
            // Tạo mới khách hàng từ OAuth2
            KhachHang khachHang = new KhachHang();
            khachHang.setMaKH(generateMaKhachHang());
            khachHang.setNguoiDung(nguoiDung);
            khachHang.setHoTen(hoTen != null ? hoTen : "OAuth2 User"); // Sử dụng tên từ OAuth2
            khachHang.setSdt(null); // OAuth2 thường không cung cấp SĐT
            khachHang.setDiaChi(null); // OAuth2 thường không cung cấp địa chỉ
            khachHang.setDiemTichLuy(0);
            khachHang.setLoaiKhachHang("Thường");
            khachHang.setNgayDangKy(new Timestamp(System.currentTimeMillis()));
            khachHang.setIsDeleted(false);
            
            KhachHang savedCustomer = khachHangRepository.save(khachHang);
            clearKhachHangCache();
            
            System.out.println("✅ Tạo khách hàng mới từ OAuth2:");
            System.out.println("   - Mã KH: " + savedCustomer.getMaKH());
            System.out.println("   - Họ tên: " + savedCustomer.getHoTen());
            System.out.println("   - Email: " + nguoiDung.getEmail());
            
            return savedCustomer;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi tạo khách hàng từ OAuth2: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public KhachHang findByMaNguoiDung(String maNguoiDung) {
        try {
            // Khôi phục lại cách cũ để đảm bảo hoạt động
            List<KhachHang> allCustomers = khachHangRepository.findAllActive();
            for (KhachHang kh : allCustomers) {
                if (kh.getNguoiDung() != null && 
                    kh.getNguoiDung().getMaNguoiDung().equals(maNguoiDung)) {
                    System.out.println("✅ Tìm thấy khách hàng cho NguoiDung: " + maNguoiDung);
                    System.out.println("   - Mã KH: " + kh.getMaKH());
                    System.out.println("   - Họ tên: " + kh.getHoTen());
                    return kh;
                }
            }
            System.out.println("❌ Không tìm thấy khách hàng cho NguoiDung: " + maNguoiDung);
            return null; // Không tìm thấy
        } catch (Exception e) {
            System.err.println("❌ Lỗi tìm khách hàng theo maNguoiDung: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public KhachHang findByEmail(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new RuntimeException("Email không được để trống");
            }
            
            // 1. Tìm NguoiDung theo email
            NguoiDung nguoiDung = nguoiDungService.findByEmail(email.trim());
            if (nguoiDung == null) {
                System.out.println("❌ Không tìm thấy NguoiDung với email: " + email);
                return null;
            }
            
            // 2. Tìm KhachHang theo maNguoiDung
            KhachHang khachHang = findByMaNguoiDung(nguoiDung.getMaNguoiDung());
            if (khachHang == null) {
                System.out.println("❌ Không tìm thấy KhachHang cho NguoiDung: " + nguoiDung.getMaNguoiDung());
                return null;
            }
            
            System.out.println("✅ Tìm thấy khách hàng theo email: " + email);
            System.out.println("   - Mã KH: " + khachHang.getMaKH());
            System.out.println("   - Họ tên: " + khachHang.getHoTen());
            System.out.println("   - Email: " + email);
            
            return khachHang;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi tìm khách hàng theo email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Tìm khách hàng theo người dùng hiện tại (thay thế cho "current")
     */
    public KhachHang findCurrentCustomer(String maNguoiDung) {
        try {
            if (maNguoiDung == null || maNguoiDung.trim().isEmpty()) {
                System.out.println("⚠️ Mã người dùng không được để trống");
                return null;
            }
            
            return findByMaNguoiDung(maNguoiDung.trim());
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm khách hàng hiện tại: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    @Transactional
    public KhachHang updateCustomerInfo(String maKH, String hoTen, String sdt, LocalDate ngaySinh, String diaChi) {
        try {
            // Tìm khách hàng hiện tại
            KhachHang existingKhachHang = findActiveById(maKH);
            if (existingKhachHang == null) {
                throw new RuntimeException("Không tìm thấy khách hàng với mã: " + maKH);
            }
            
            // Cập nhật thông tin mới
            if (hoTen != null && !hoTen.trim().isEmpty()) {
                existingKhachHang.setHoTen(hoTen.trim());
            }
            if (sdt != null && !sdt.trim().isEmpty()) {
                existingKhachHang.setSdt(sdt.trim());
            }
            if (ngaySinh != null) {
                existingKhachHang.setNgaySinh(ngaySinh);
            }
            if (diaChi != null) {
                existingKhachHang.setDiaChi(diaChi.trim());
            }
            
            // Lưu thay đổi
            KhachHang updatedKhachHang = khachHangRepository.save(existingKhachHang);
            clearKhachHangCache();
            
            System.out.println("✅ Cập nhật thông tin khách hàng thành công: " + maKH);
            return updatedKhachHang;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi cập nhật thông tin khách hàng: " + e.getMessage());
            throw new RuntimeException("Lỗi cập nhật thông tin khách hàng: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public KhachHang getCustomerInfo(String maKH) {
        try {
            KhachHang khachHang = findActiveById(maKH);
            if (khachHang == null) {
                throw new RuntimeException("Không tìm thấy khách hàng với mã: " + maKH);
            }
            
            System.out.println("✅ Lấy thông tin khách hàng thành công: " + maKH);
            return khachHang;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lấy thông tin khách hàng: " + e.getMessage());
            throw new RuntimeException("Lỗi lấy thông tin khách hàng: " + e.getMessage());
        }
    }
    
    /**
     * Xóa tất cả cache liên quan đến khách hàng
     */
    private void clearKhachHangCache() {
        try {
            // Xóa cache khách hàng
            if (cacheManager.getCache("khachhang-info") != null) {
                cacheManager.getCache("khachhang-info").clear();
            }
            if (cacheManager.getCache("khachhang-profile") != null) {
                cacheManager.getCache("khachhang-profile").clear();
            }
            
            // Xóa cache giỏ hàng (vì có thể ảnh hưởng đến khách hàng)
            if (cacheManager.getCache("giohang-by-customer") != null) {
                cacheManager.getCache("giohang-by-customer").clear();
            }
            if (cacheManager.getCache("giohang-items") != null) {
                cacheManager.getCache("giohang-items").clear();
            }
            
            // Xóa cache hóa đơn (vì có thể ảnh hưởng đến khách hàng)
            if (cacheManager.getCache("hoadon-by-customer") != null) {
                cacheManager.getCache("hoadon-by-customer").clear();
            }
            
            System.out.println("✅ Đã xóa cache khách hàng sau khi thay đổi dữ liệu");
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi xóa cache khách hàng: " + e.getMessage());
        }
    }
} 
