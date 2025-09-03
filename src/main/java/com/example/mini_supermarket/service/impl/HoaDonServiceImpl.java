package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.HoaDonRepository;
import com.example.mini_supermarket.entity.HoaDon;
import com.example.mini_supermarket.entity.ChiTietHoaDon;
import com.example.mini_supermarket.entity.GioHangChiTiet;
import com.example.mini_supermarket.entity.KhachHang;
import com.example.mini_supermarket.entity.NhanVien;
import com.example.mini_supermarket.entity.SanPham;
import com.example.mini_supermarket.entity.KhuyenMai;
import com.example.mini_supermarket.service.HoaDonService;
import com.example.mini_supermarket.dto.HoaDonFullDetailsDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.CacheManager;
import com.example.mini_supermarket.service.ChiTietHoaDonService;
import com.example.mini_supermarket.service.GioHangChiTietService;
import com.example.mini_supermarket.service.KhachHangService;
import com.example.mini_supermarket.service.NhanVienService;
import com.example.mini_supermarket.service.SanPhamService;
import com.example.mini_supermarket.service.KhuyenMaiService;
import com.example.mini_supermarket.dto.CreateInvoiceFromCartRequest;
import com.example.mini_supermarket.dto.InvoiceCreatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HoaDonServiceImpl implements HoaDonService {
    private HoaDonRepository hoaDonRepository;
    
    @Autowired
    private ChiTietHoaDonService chiTietHoaDonService;
    
    @Autowired
    private GioHangChiTietService gioHangChiTietService;
    
    @Autowired
    private KhachHangService khachHangService;
    
    @Autowired
    private NhanVienService nhanVienService;
    
    @Autowired
    private KhuyenMaiService khuyenMaiService;
    
    @Autowired
    private CacheManager cacheManager;
    
    @Autowired
    public HoaDonServiceImpl(HoaDonRepository hoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDon> findAll() {
        return hoaDonRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDon findById(Integer theId) {
        Optional<HoaDon> result = hoaDonRepository.findById(theId);
        HoaDon theHoaDon = null;

        if (result.isPresent()) {
            theHoaDon = result.get();
        } else {
            throw new RuntimeException("Did not find HoaDon id - " + theId);
        }
        return theHoaDon;
    }

    @Override
    @Transactional
    public HoaDon save(HoaDon theHoaDon) {
        HoaDon savedHoaDon = hoaDonRepository.save(theHoaDon);
        clearAllRelatedCaches(); // Clear cache sau khi save
        return savedHoaDon;
    }

    @Override
    @Transactional
    public void deleteById(Integer theId) {
        hoaDonRepository.deleteById(theId);
    }

    @Override
    @Transactional
    public HoaDon update(HoaDon hoaDon) {
        Optional<HoaDon> existingHoaDon = hoaDonRepository.findById(hoaDon.getMaHD());

        if (!existingHoaDon.isPresent()) {
            throw new RuntimeException("Không tìm thấy hóa đơn với ID - " + hoaDon.getMaHD());
        }

        // Kiểm tra trạng thái hóa đơn (chỉ cho phép cập nhật khi chưa thanh toán)
        HoaDon existingHoaDonData = existingHoaDon.get();
        if (existingHoaDonData.getTrangThai() != null && existingHoaDonData.getTrangThai() != 0) {
            throw new RuntimeException("Chỉ có thể cập nhật hóa đơn chưa thanh toán");
        }

        // Xử lý khuyến mãi nếu có thay đổi
        if (hoaDon.getKhuyenMai() != null) {
            // Kiểm tra khuyến mãi có hợp lệ không
            KhuyenMai khuyenMai = khuyenMaiService.findById(hoaDon.getKhuyenMai().getMaKM());
            if (khuyenMai == null) {
                throw new RuntimeException("Không tìm thấy khuyến mãi với mã: " + hoaDon.getKhuyenMai().getMaKM());
            }

            // Kiểm tra khuyến mãi có hợp lệ không
            java.sql.Timestamp now = java.sql.Timestamp.valueOf(LocalDateTime.now());
            
            // Kiểm tra ngày bắt đầu
            if (khuyenMai.getNgayBatDau() != null && now.before(khuyenMai.getNgayBatDau())) {
                throw new RuntimeException("Khuyến mãi chưa bắt đầu");
            }
            
            // Kiểm tra ngày kết thúc
            if (khuyenMai.getNgayKetThuc() != null && now.after(khuyenMai.getNgayKetThuc())) {
                throw new RuntimeException("Khuyến mãi đã hết hạn");
            }
            
            // Kiểm tra số lượng đã sử dụng
            if (khuyenMai.getSoLuongToiDa() != null && khuyenMai.getDaSuDung() != null) {
                if (khuyenMai.getDaSuDung() >= khuyenMai.getSoLuongToiDa()) {
                    throw new RuntimeException("Khuyến mãi đã sử dụng hết");
                }
            }
            
            // Kiểm tra trạng thái
            if (khuyenMai.getTrangThai() != null && khuyenMai.getTrangThai() != 1) {
                throw new RuntimeException("Khuyến mãi không hoạt động");
            }

            // Tính toán tiền giảm giá
            BigDecimal tongTienHang = hoaDon.getTongTienHang();
            if (tongTienHang != null && tongTienHang.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal tienGiamGia = BigDecimal.ZERO;
                
                if ("PhanTram".equals(khuyenMai.getLoaiKM())) {
                    // Giảm theo phần trăm
                    BigDecimal phanTramGiam = khuyenMai.getGiaTriKM();
                    if (phanTramGiam != null) {
                        tienGiamGia = tongTienHang.multiply(phanTramGiam).divide(BigDecimal.valueOf(100));
                    }
                } else if ("SoTien".equals(khuyenMai.getLoaiKM())) {
                    // Giảm theo tiền mặt
                    tienGiamGia = khuyenMai.getGiaTriKM();
                }
                
                hoaDon.setTienGiamGia(tienGiamGia);
            }

            // Tăng số lượng đã sử dụng của khuyến mãi
            if (khuyenMai.getDaSuDung() != null) {
                khuyenMai.setDaSuDung(khuyenMai.getDaSuDung() + 1);
                khuyenMaiService.save(khuyenMai);
            }
        } else {
            // Nếu không có khuyến mãi, reset tiền giảm giá
            hoaDon.setTienGiamGia(BigDecimal.ZERO);
        }

        // Cập nhật thời gian sửa
        hoaDon.setNgaySua(java.sql.Timestamp.valueOf(LocalDateTime.now()));

        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);
        clearAllRelatedCaches(); // Clear cache sau khi cập nhật
        return savedHoaDon;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDon> findAllActive() {
        return hoaDonRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDon findActiveById(Integer id) {
        Optional<HoaDon> result = hoaDonRepository.findActiveById(id);
        return result.orElse(null);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        Optional<HoaDon> hoaDonOpt = hoaDonRepository.findActiveById(id);
        if (hoaDonOpt.isPresent()) {
            HoaDon hoaDon = hoaDonOpt.get();
            hoaDon.setIsDeleted(true);
            hoaDonRepository.save(hoaDon);
            clearAllRelatedCaches(); // Clear cache sau khi soft delete
        }
    }
    
    @Override
    @Transactional
    public InvoiceCreatedResponse createInvoiceFromCart(CreateInvoiceFromCartRequest request) {
        // 1. Validate request
        if (request.getMaKH() == null || request.getMaKH().trim().isEmpty()) {
            throw new RuntimeException("Mã khách hàng không được để trống");
        }
        
        if (request.getMaNV() == null || request.getMaNV().trim().isEmpty()) {
            throw new RuntimeException("Mã nhân viên không được để trống");
        }
        
        if (request.getSelectedCartItemIds() == null || request.getSelectedCartItemIds().isEmpty()) {
            throw new RuntimeException("Phải chọn ít nhất một item từ giỏ hàng");
        }
        
        // 2. Lấy thông tin khách hàng
        KhachHang khachHang = khachHangService.findById(request.getMaKH());
        if (khachHang == null) {
            throw new RuntimeException("Không tìm thấy khách hàng với mã: " + request.getMaKH());
        }
        
        // Kiểm tra khách hàng có bị xóa không
        if (khachHang.getIsDeleted() != null && khachHang.getIsDeleted()) {
            throw new RuntimeException("Khách hàng đã bị xóa");
        }
        
        // 3. Lấy thông tin nhân viên
        NhanVien nhanVien = nhanVienService.findById(request.getMaNV());
        if (nhanVien == null) {
            throw new RuntimeException("Không tìm thấy nhân viên với mã: " + request.getMaNV());
        }
        
        // 4. Lấy thông tin khuyến mãi (nếu có)
        KhuyenMai khuyenMai = null;
        if (request.getMaKM() != null && !request.getMaKM().trim().isEmpty()) {
            khuyenMai = khuyenMaiService.findById(request.getMaKM());
            if (khuyenMai == null) {
                throw new RuntimeException("Không tìm thấy khuyến mãi với mã: " + request.getMaKM());
            }
        }
        
        // 5. Lấy các item từ giỏ hàng VÀ KIỂM TRA BẢO MẬT
        List<GioHangChiTiet> cartItems = new ArrayList<>();
        BigDecimal tongTienHang = BigDecimal.ZERO;
        
        for (String itemIdStr : request.getSelectedCartItemIds()) {
            try {
                Integer itemId = Integer.parseInt(itemIdStr);
                
                // Lấy item từ giỏ hàng
                GioHangChiTiet cartItem = gioHangChiTietService.findActiveById(itemId);
                if (cartItem == null) {
                    throw new RuntimeException("Không tìm thấy item trong giỏ hàng với ID: " + itemId);
                }
                
                // 🔒 BẢO MẬT: Kiểm tra item có thuộc khách hàng này không
                if (cartItem.getKhachHang() == null || !cartItem.getKhachHang().getMaKH().equals(request.getMaKH())) {
                    throw new RuntimeException("Item không thuộc khách hàng này hoặc bị lỗi dữ liệu");
                }
                
                // Kiểm tra trạng thái Shopping (chỉ cho phép tạo hóa đơn từ item đang shopping)
                if (cartItem.getTrangThai() != 0) {
                    throw new RuntimeException("Item " + cartItem.getSanPham().getTenSP() + " không ở trạng thái Shopping");
                }
                
                // Kiểm tra số lượng tồn kho
                if (cartItem.getSoLuong() <= 0) {
                    throw new RuntimeException("Số lượng item " + cartItem.getSanPham().getTenSP() + " không hợp lệ");
                }
                
                // 🔍 KIỂM TRA SẢN PHẨM: Đảm bảo sản phẩm tồn tại và active
                if (cartItem.getSanPham() == null) {
                    throw new RuntimeException("Sản phẩm không tồn tại trong giỏ hàng");
                }
                
                // Kiểm tra sản phẩm có bị xóa không
                if (cartItem.getSanPham().getIsDeleted() != null && cartItem.getSanPham().getIsDeleted()) {
                    throw new RuntimeException("Sản phẩm " + cartItem.getSanPham().getTenSP() + " đã bị xóa");
                }
                
                // Kiểm tra trạng thái sản phẩm (đang kinh doanh)
                if (cartItem.getSanPham().getTrangThai() != null && cartItem.getSanPham().getTrangThai() != 1) {
                    throw new RuntimeException("Sản phẩm " + cartItem.getSanPham().getTenSP() + " đã ngừng kinh doanh");
                }
                
                // Kiểm tra giá sản phẩm
                if (cartItem.getDonGiaHienTai() == null || cartItem.getDonGiaHienTai().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("Giá sản phẩm " + cartItem.getSanPham().getTenSP() + " không hợp lệ");
                }
                
                cartItems.add(cartItem);
                tongTienHang = tongTienHang.add(cartItem.getThanhTien() != null ? cartItem.getThanhTien() : BigDecimal.ZERO);
                
            } catch (NumberFormatException e) {
                throw new RuntimeException("ID item không hợp lệ: " + itemIdStr);
            }
        }
        
        // Kiểm tra tổng tiền
        if (tongTienHang.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Tổng tiền hàng phải lớn hơn 0");
        }
        
        // 6. Tính toán giảm giá và tổng tiền
        BigDecimal tienGiamGia = BigDecimal.ZERO;
        if (khuyenMai != null) {
            // Logic tính giảm giá theo khuyến mãi
            // Nếu loại KM là phần trăm thì tính theo phần trăm, nếu không thì lấy giá trị trực tiếp
            if ("PhầnTrăm".equals(khuyenMai.getLoaiKM())) {
                tienGiamGia = tongTienHang.multiply(khuyenMai.getGiaTriKM()).divide(BigDecimal.valueOf(100));
            } else {
                tienGiamGia = khuyenMai.getGiaTriKM();
            }
            
            // Đảm bảo giảm giá không vượt quá tổng tiền hàng
            if (tienGiamGia.compareTo(tongTienHang) > 0) {
                tienGiamGia = tongTienHang;
            }
        }
        
        BigDecimal tongTien = tongTienHang.subtract(tienGiamGia);
        
        // 7. Tạo hóa đơn
        HoaDon hoaDon = new HoaDon();
        hoaDon.setKhachHang(khachHang);
        hoaDon.setNhanVienLap(nhanVien);
        hoaDon.setKhuyenMai(khuyenMai);
        hoaDon.setNgayLap(Timestamp.valueOf(LocalDateTime.now()));
        hoaDon.setTongTienHang(tongTienHang);
        hoaDon.setTienGiamGia(tienGiamGia);
        hoaDon.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : 0); // Mặc định chờ thanh toán
        hoaDon.setDiemTichLuy(0);
        hoaDon.setGhiChu(request.getGhiChu());
        hoaDon.setNgayTao(Timestamp.valueOf(LocalDateTime.now()));
        hoaDon.setNguoiTao(nhanVien);
        hoaDon.setIsDeleted(false);
        
        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);
        
        // 8. Tạo chi tiết hóa đơn
        List<InvoiceCreatedResponse.InvoiceItemResponse> invoiceItems = new ArrayList<>();
        
        for (GioHangChiTiet cartItem : cartItems) {
            ChiTietHoaDon chiTietHoaDon = new ChiTietHoaDon();
            chiTietHoaDon.setHoaDon(savedHoaDon);
            chiTietHoaDon.setSanPham(cartItem.getSanPham());
            chiTietHoaDon.setSoLuong(cartItem.getSoLuong());
            chiTietHoaDon.setDonGiaBan(cartItem.getDonGiaHienTai());
            chiTietHoaDon.setThanhTien(cartItem.getThanhTien());
            chiTietHoaDon.setIsDeleted(false);
            
            chiTietHoaDonService.save(chiTietHoaDon);
            
            // Tạo response item
            InvoiceCreatedResponse.InvoiceItemResponse invoiceItem = InvoiceCreatedResponse.InvoiceItemResponse.builder()
                    .maSP(cartItem.getSanPham().getMaSP())
                    .tenSP(cartItem.getSanPham().getTenSP())
                    .soLuong(cartItem.getSoLuong())
                    .donGia(cartItem.getDonGiaHienTai())
                    .thanhTien(cartItem.getThanhTien())
                    .build();
            
            invoiceItems.add(invoiceItem);
        }
        
        // 9. Xóa các item trong giỏ hàng (vì đã tạo hóa đơn)
        for (GioHangChiTiet cartItem : cartItems) {
            gioHangChiTietService.softDeleteById(cartItem.getMaGHCT());
        }
        
        // 10. Tạo response
        InvoiceCreatedResponse response = InvoiceCreatedResponse.builder()
                .maHD(savedHoaDon.getMaHD())
                .maKH(request.getMaKH())
                .maNV(request.getMaNV())
                .maKM(request.getMaKM())
                .ngayLap(savedHoaDon.getNgayLap())
                .tongTienHang(tongTienHang)
                .tienGiamGia(tienGiamGia)
                .tongTien(tongTien)
                .trangThai(savedHoaDon.getTrangThai())
                .items(invoiceItems)
                .build();
        
        // 11. Clear cache sau khi tạo hóa đơn
        clearAllRelatedCaches();
        
        return response;
    }
    
    // ===== HELPER METHOD - Clear all related caches =====
    private void clearAllRelatedCaches() {
        try {
            // Clear hóa đơn caches
            cacheManager.getCache("hoadon-summary").clear();
            cacheManager.getCache("hoadon-by-customer").clear();
            cacheManager.getCache("hoadon-count").clear();
            cacheManager.getCache("hoadon-by-status").clear();
            cacheManager.getCache("hoadon-full-details").clear();
            cacheManager.getCache("hoadon-statistics").clear();
            
            // Clear giỏ hàng caches
            cacheManager.getCache("giohang-by-customer").clear();
            cacheManager.getCache("giohang-items").clear();
            
            System.out.println("✅ Đã clear tất cả cache liên quan đến hóa đơn và giỏ hàng");
        } catch (Exception e) {
            System.out.println("⚠️ Lỗi khi clear cache: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public HoaDon createInvoiceAndClearCart(HoaDon hoaDon, List<Integer> cartItemIds) {
        // Lưu hóa đơn
        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);
        
        // Xóa các item trong giỏ hàng
        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            for (Integer cartItemId : cartItemIds) {
                gioHangChiTietService.softDeleteById(cartItemId);
            }
        }
        
        return savedHoaDon;
    }
    
    @Override
    @Transactional
    public HoaDon updateTrangThai(Integer maHD, Integer trangThaiMoi) {
        Optional<HoaDon> hoaDonOpt = hoaDonRepository.findById(maHD);
        if (hoaDonOpt.isPresent()) {
            HoaDon hoaDon = hoaDonOpt.get();
            hoaDon.setTrangThai(trangThaiMoi);
            HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);
            clearAllRelatedCaches(); // Clear cache sau khi cập nhật
            return savedHoaDon;
        }
        throw new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHD);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDon> findActiveByCustomer(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new RuntimeException("Mã khách hàng không được để trống");
        }
        return hoaDonRepository.findActiveByCustomer(maKH.trim());
    }
    
    // ===== OPTIMIZED METHODS IMPLEMENTATION - Temporarily disabled =====
    
    /*
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "hoadon-summary", key = "#pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort.toString()")
    public Page<HoaDonSummaryDTO> findAllActiveSummary(Pageable pageable) {
        return hoaDonRepository.findAllActiveSummary(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "hoadon-by-customer", key = "#maKH + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<HoaDonSummaryDTO> findActiveByCustomerSummary(String maKH, Pageable pageable) {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new RuntimeException("Mã khách hàng không được để trống");
        }
        return hoaDonRepository.findActiveByCustomerSummary(maKH.trim(), pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "hoadon-by-customer", key = "#maKH + '_list'")
    public List<HoaDonSummaryDTO> findActiveByCustomerSummaryList(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new RuntimeException("Mã khách hàng không được để trống");
        }
        return hoaDonRepository.findActiveByCustomerSummaryList(maKH.trim());
    }
    */
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "hoadon-count", key = "'trangthai_' + #trangThai")
    public Long countByTrangThai(Integer trangThai) {
        return hoaDonRepository.countByTrangThai(trangThai);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "hoadon-count", key = "'customer_' + #maKH")
    public Long countByCustomer(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new RuntimeException("Mã khách hàng không được để trống");
        }
        return hoaDonRepository.countByCustomer(maKH.trim());
    }
    
    // ===== ENHANCED METHODS IMPLEMENTATION =====
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "hoadon-by-status", key = "'status_' + #trangThai")
    public List<HoaDon> findByTrangThai(Integer trangThai) {
        return hoaDonRepository.findByTrangThai(trangThai);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "hoadon-by-customer", key = "#maKH + '_status_' + #trangThai")
    public List<HoaDon> findByCustomerAndStatus(String maKH, Integer trangThai) {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new RuntimeException("Mã khách hàng không được để trống");
        }
        return hoaDonRepository.findByCustomerAndStatus(maKH.trim(), trangThai);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HoaDon> findByDateRange(String fromDate, String toDate) {
        if (fromDate == null || toDate == null) {
            throw new RuntimeException("Ngày bắt đầu và ngày kết thúc không được để trống");
        }
        return hoaDonRepository.findByDateRange(fromDate, toDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HoaDon> findByCustomerAndDateRange(String maKH, String fromDate, String toDate) {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new RuntimeException("Mã khách hàng không được để trống");
        }
        if (fromDate == null || toDate == null) {
            throw new RuntimeException("Ngày bắt đầu và ngày kết thúc không được để trống");
        }
        return hoaDonRepository.findByCustomerAndDateRange(maKH.trim(), fromDate, toDate);
    }
    
    @Override
    @Transactional
    public HoaDon cancelHoaDon(Integer maHD, String lyDoHuy) {
        HoaDon hoaDon = findActiveById(maHD);
        if (hoaDon == null) {
            throw new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHD);
        }
        
        // Kiểm tra trạng thái hiện tại
        if (hoaDon.getTrangThai() == 3) {
            throw new RuntimeException("Hóa đơn đã được hủy trước đó");
        }
        if (hoaDon.getTrangThai() == 1) {
            throw new RuntimeException("Không thể hủy hóa đơn đã thanh toán");
        }
        
        // Cập nhật trạng thái và lý do hủy
        hoaDon.setTrangThai(3); // 3 = Hủy
        if (lyDoHuy != null && !lyDoHuy.trim().isEmpty()) {
            hoaDon.setGhiChu(hoaDon.getGhiChu() != null ? 
                hoaDon.getGhiChu() + "\n[Lý do hủy]: " + lyDoHuy : 
                "[Lý do hủy]: " + lyDoHuy);
        }
        hoaDon.setNgaySua(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
        
        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);
        clearAllRelatedCaches(); // Clear cache sau khi hủy
        return savedHoaDon;
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "hoadon-statistics", key = "'customer_stats_' + #maKH")
    public Object getStatisticsByCustomer(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new RuntimeException("Mã khách hàng không được để trống");
        }
        
        List<HoaDon> hoaDons = hoaDonRepository.findActiveByCustomer(maKH.trim());
        
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("totalInvoices", hoaDons.size());
        statistics.put("totalAmount", hoaDons.stream()
            .filter(h -> h.getTongTien() != null)
            .map(HoaDon::getTongTien)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        statistics.put("totalDiscount", hoaDons.stream()
            .filter(h -> h.getTienGiamGia() != null)
            .map(HoaDon::getTienGiamGia)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        statistics.put("totalPoints", hoaDons.stream()
            .filter(h -> h.getDiemTichLuy() != null)
            .mapToInt(HoaDon::getDiemTichLuy)
            .sum());
        
        return statistics;
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "hoadon-count", key = "'customer_status_count_' + #maKH")
    public Object countByCustomerAndStatus(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new RuntimeException("Mã khách hàng không được để trống");
        }
        
        java.util.Map<String, Object> countByStatus = new java.util.HashMap<>();
        countByStatus.put("pending", hoaDonRepository.findByCustomerAndStatus(maKH.trim(), 0).size()); // Chờ thanh toán
        countByStatus.put("paid", hoaDonRepository.findByCustomerAndStatus(maKH.trim(), 1).size()); // Đã thanh toán
        countByStatus.put("processing", hoaDonRepository.findByCustomerAndStatus(maKH.trim(), 2).size()); // Đang xử lý
        countByStatus.put("cancelled", hoaDonRepository.findByCustomerAndStatus(maKH.trim(), 3).size()); // Hủy
        countByStatus.put("returned", hoaDonRepository.findByCustomerAndStatus(maKH.trim(), 4).size()); // Hoàn trả
        
        return countByStatus;
    }
    
    // ===== FULL DETAILS METHODS IMPLEMENTATION =====
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "hoadon-full-details", key = "'full_' + #maHD")
    public HoaDonFullDetailsDTO getHoaDonFullDetails(Integer maHD) {
        HoaDon hoaDon = findActiveById(maHD);
        if (hoaDon == null) {
            throw new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHD);
        }
        
        // Lấy chi tiết hóa đơn
        List<com.example.mini_supermarket.entity.ChiTietHoaDon> chiTietList = 
            chiTietHoaDonService.findByHoaDonId(maHD);
        
        // Convert chi tiết hóa đơn sang DTO
        List<HoaDonFullDetailsDTO.ChiTietHoaDonDTO> chiTietDTOList = chiTietList.stream()
            .map(ct -> HoaDonFullDetailsDTO.ChiTietHoaDonDTO.builder()
                .maCTHD(ct.getMaCTHD())
                .maSP(ct.getSanPham().getMaSP())
                .tenSP(ct.getSanPham().getTenSP())
                .soLuong(ct.getSoLuong())
                .donGiaBan(ct.getDonGiaBan())
                .thanhTien(ct.getThanhTien())
                .giamGia(ct.getGiamGia())
                .thanhTienSauGiam(ct.getThanhTienSauGiam())
                .build())
            .collect(java.util.stream.Collectors.toList());
        
        // Tính thống kê
        Integer soLuongSanPham = chiTietList.stream()
            .mapToInt(com.example.mini_supermarket.entity.ChiTietHoaDon::getSoLuong)
            .sum();
        
        java.math.BigDecimal trungBinhGiaTriSanPham = soLuongSanPham > 0 ? 
            hoaDon.getTongTienHang().divide(java.math.BigDecimal.valueOf(soLuongSanPham), 
                2, java.math.RoundingMode.HALF_UP) : java.math.BigDecimal.ZERO;
        
        // Tạo DTO
        HoaDonFullDetailsDTO dto = HoaDonFullDetailsDTO.builder()
            .maHD(hoaDon.getMaHD())
            .maKH(hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getMaKH() : null)
            .tenKH(hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getHoTen() : null)
            .maNV(hoaDon.getNhanVienLap() != null ? hoaDon.getNhanVienLap().getMaNV() : null)
            .tenNV(hoaDon.getNhanVienLap() != null ? hoaDon.getNhanVienLap().getHoTen() : null)
            .maKM(hoaDon.getKhuyenMai() != null ? hoaDon.getKhuyenMai().getMaKM() : null)
            .tenKM(hoaDon.getKhuyenMai() != null ? hoaDon.getKhuyenMai().getTenChuongTrinh() : null)
            .ngayLap(hoaDon.getNgayLap())
            .tongTienHang(hoaDon.getTongTienHang())
            .tienGiamGia(hoaDon.getTienGiamGia())
            .tongTien(hoaDon.getTongTien())
            .trangThai(hoaDon.getTrangThai())
            .diemTichLuy(hoaDon.getDiemTichLuy())
            .ghiChu(hoaDon.getGhiChu())
            .chiTietList(chiTietDTOList)
            .soLuongSanPham(soLuongSanPham)
            .trungBinhGiaTriSanPham(trungBinhGiaTriSanPham)
            .build();
        
        // Set tên trạng thái
        dto.setTrangThai(hoaDon.getTrangThai());
        
        return dto;
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "hoadon-full-details", key = "'customer_full_' + #maKH")
    public List<HoaDonFullDetailsDTO> getHoaDonFullDetailsByCustomer(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new RuntimeException("Mã khách hàng không được để trống");
        }
        
        List<HoaDon> hoaDons = hoaDonRepository.findActiveByCustomer(maKH.trim());
        
        return hoaDons.stream()
            .map(hoaDon -> getHoaDonFullDetails(hoaDon.getMaHD()))
            .collect(java.util.stream.Collectors.toList());
    }
} 
