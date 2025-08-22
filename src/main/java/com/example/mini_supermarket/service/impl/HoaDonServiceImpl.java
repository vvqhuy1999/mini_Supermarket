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
        return hoaDonRepository.save(theHoaDon);
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

        return hoaDonRepository.save(hoaDon);
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
        
        return response;
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
            return hoaDonRepository.save(hoaDon);
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
} 
