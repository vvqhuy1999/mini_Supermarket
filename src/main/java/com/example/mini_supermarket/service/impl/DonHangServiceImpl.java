package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.entity.DonHang;
import com.example.mini_supermarket.entity.ChiTietDonHang;
import com.example.mini_supermarket.entity.GioHangChiTiet;
import com.example.mini_supermarket.entity.KhachHang;
import com.example.mini_supermarket.entity.NhanVien;
import com.example.mini_supermarket.entity.SanPham;
import com.example.mini_supermarket.repository.DonHangRepository;
import com.example.mini_supermarket.repository.ChiTietDonHangRepository;
import com.example.mini_supermarket.service.DonHangService;
import com.example.mini_supermarket.service.ChiTietDonHangService;
import com.example.mini_supermarket.service.GioHangChiTietService;
import com.example.mini_supermarket.service.KhachHangService;
import com.example.mini_supermarket.service.NhanVienService;
import com.example.mini_supermarket.service.SanPhamService;
import com.example.mini_supermarket.dto.CreateOrderFromCartRequest;
import com.example.mini_supermarket.dto.OrderCreatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DonHangServiceImpl implements DonHangService {

    @Autowired
    private DonHangRepository donHangRepository;
    
    @Autowired
    private ChiTietDonHangService chiTietDonHangService;
    
    @Autowired
    private GioHangChiTietService gioHangChiTietService;
    
    @Autowired
    private KhachHangService khachHangService;
    
    @Autowired
    private NhanVienService nhanVienService;
    
    @Autowired
    private SanPhamService sanPhamService;

    @Override
    @Transactional
    public DonHang saveDonHang(DonHang donHang) {
        return donHangRepository.save(donHang);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DonHang> findDonHangByMaDH(String maDH) {
        return donHangRepository.findById(maDH);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> getAllDonHang() {
        return donHangRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> findDonHangByKhachHang(String maKH) {
        return donHangRepository.findByKhachHang_MaKH(maKH);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> findDonHangByNhanVien(String maNV) {
        return donHangRepository.findByNhanVien_MaNV(maNV);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> findDonHangByTrangThai(String trangThai) {
        return donHangRepository.findByTrangThai(trangThai);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> findDonHangByThoiGian(Timestamp tuNgay, Timestamp denNgay) {
        return donHangRepository.findByNgayDatHangBetween(tuNgay, denNgay);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> findDonHangChuaGiao() {
        return donHangRepository.findDonHangChuaGiao();
    }

    @Override
    @Transactional
    public DonHang updateTrangThaiDonHang(String maDH, String trangThaiMoi) {
        Optional<DonHang> donHangOpt = donHangRepository.findById(maDH);
        if (donHangOpt.isPresent()) {
            DonHang donHang = donHangOpt.get();
            donHang.setTrangThai(trangThaiMoi);
            return donHangRepository.save(donHang);
        }
        throw new RuntimeException("Không tìm thấy đơn hàng với mã: " + maDH);
    }

    @Override
    @Transactional
    public DonHang updateNgayGiaoHang(String maDH, Timestamp ngayGiaoHang) {
        Optional<DonHang> donHangOpt = donHangRepository.findById(maDH);
        if (donHangOpt.isPresent()) {
            DonHang donHang = donHangOpt.get();
            donHang.setNgayGiaoHang(ngayGiaoHang);
            return donHangRepository.save(donHang);
        }
        throw new RuntimeException("Không tìm thấy đơn hàng với mã: " + maDH);
    }

    @Override
    @Transactional
    public void deleteDonHang(String maDH) {
        donHangRepository.deleteById(maDH);
    }

    @Override
    @Transactional(readOnly = true)
    public long countDonHangByTrangThai(String trangThai) {
        return donHangRepository.countByTrangThai(trangThai);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> findDonHangByKhachHangAndThoiGian(String maKH, Timestamp tuNgay, Timestamp denNgay) {
        return donHangRepository.findDonHangByKhachHangAndThoiGian(maKH, tuNgay, denNgay);
    }

    @Override
    @Transactional
    public void softDeleteById(String maDH) {
        Optional<DonHang> donHangOpt = findDonHangByMaDH(maDH);
        if (donHangOpt.isPresent()) {
            DonHang donHang = donHangOpt.get();
            donHang.setIsdeleted(true);
            donHangRepository.save(donHang);
        }
    }

    @Override
    @Transactional
    public OrderCreatedResponse createOrderFromCart(CreateOrderFromCartRequest request) {
        // 1. Validate request
        if (request.getMaKH() == null || request.getMaKH().trim().isEmpty()) {
            throw new RuntimeException("Mã khách hàng không được để trống");
        }
        
        if (request.getSelectedCartItemIds() == null || request.getSelectedCartItemIds().isEmpty()) {
            throw new RuntimeException("Phải chọn ít nhất một item từ giỏ hàng");
        }
        
        // 2. Lấy thông tin khách hàng
        KhachHang khachHang = khachHangService.findById(request.getMaKH());
        if (khachHang == null) {
            throw new RuntimeException("Không tìm thấy khách hàng với mã: " + request.getMaKH());
        }
        
        // 3. Lấy thông tin nhân viên (nếu có)
        NhanVien nhanVien = null;
        if (request.getMaNV() != null && !request.getMaNV().trim().isEmpty()) {
            nhanVien = nhanVienService.findById(request.getMaNV());
            if (nhanVien == null) {
                throw new RuntimeException("Không tìm thấy nhân viên với mã: " + request.getMaNV());
            }
        }
        
        // 4. Xác định địa chỉ giao hàng
        String diaChiGiaoHang = request.getDiaChiGiaoHang();
        if (diaChiGiaoHang == null || diaChiGiaoHang.trim().isEmpty()) {
            // Lấy từ thông tin khách hàng
            diaChiGiaoHang = khachHang.getDiaChi();
            if (diaChiGiaoHang == null || diaChiGiaoHang.trim().isEmpty()) {
                throw new RuntimeException("Phải cung cấp địa chỉ giao hàng");
            }
        }
        
        // 5. Lấy các item từ giỏ hàng
        List<GioHangChiTiet> cartItems = new ArrayList<>();
        BigDecimal tongTien = BigDecimal.ZERO;
        
        for (String itemIdStr : request.getSelectedCartItemIds()) {
            try {
                Integer itemId = Integer.parseInt(itemIdStr);
                GioHangChiTiet cartItem = gioHangChiTietService.findActiveById(itemId);
                
                if (cartItem == null) {
                    throw new RuntimeException("Không tìm thấy item trong giỏ hàng với ID: " + itemId);
                }
                
                // Kiểm tra item có thuộc khách hàng này không
                if (!cartItem.getKhachHang().getMaKH().equals(request.getMaKH())) {
                    throw new RuntimeException("Item không thuộc khách hàng này");
                }
                
                // Kiểm tra trạng thái Shopping
                if (cartItem.getTrangThai() != 0) {
                    throw new RuntimeException("Item không ở trạng thái Shopping");
                }
                
                cartItems.add(cartItem);
                tongTien = tongTien.add(cartItem.getThanhTien() != null ? cartItem.getThanhTien() : BigDecimal.ZERO);
                
            } catch (NumberFormatException e) {
                throw new RuntimeException("ID item không hợp lệ: " + itemIdStr);
            }
        }
        
        // 6. Tạo mã đơn hàng
        String maDH = "DH" + System.currentTimeMillis();
        
        // 7. Tạo đơn hàng
        DonHang donHang = new DonHang();
        donHang.setMaDH(maDH);
        donHang.setKhachHang(khachHang);
        donHang.setNhanVien(nhanVien);
        donHang.setNgayDatHang(Timestamp.valueOf(LocalDateTime.now()));
        donHang.setDiaChiGiaoHang(diaChiGiaoHang);
        donHang.setTrangThai("Pending");
        donHang.setIsdeleted(false);
        
        DonHang savedDonHang = donHangRepository.save(donHang);
        
        // 8. Tạo chi tiết đơn hàng
        List<OrderCreatedResponse.OrderItemResponse> orderItems = new ArrayList<>();
        
        for (GioHangChiTiet cartItem : cartItems) {
            ChiTietDonHang chiTietDonHang = new ChiTietDonHang();
            chiTietDonHang.setDonHang(savedDonHang);
            chiTietDonHang.setSanPham(cartItem.getSanPham());
            chiTietDonHang.setSoLuong(cartItem.getSoLuong());
            chiTietDonHang.setDonGia(cartItem.getDonGiaHienTai());
            chiTietDonHang.setGiamGia(BigDecimal.ZERO);
            chiTietDonHang.setIsdeleted(false);
            
            ChiTietDonHang savedChiTiet = chiTietDonHangService.saveChiTietDonHang(chiTietDonHang);
            
            // Tạo response item
            OrderCreatedResponse.OrderItemResponse orderItem = OrderCreatedResponse.OrderItemResponse.builder()
                    .maSP(cartItem.getSanPham().getMaSP())
                    .tenSP(cartItem.getSanPham().getTenSP())
                    .soLuong(cartItem.getSoLuong())
                    .donGia(cartItem.getDonGiaHienTai())
                    .thanhTien(cartItem.getThanhTien())
                    .build();
            
            orderItems.add(orderItem);
        }
        
        // 9. Cập nhật trạng thái các item trong giỏ hàng thành "Paid" và xóa chúng
        for (GioHangChiTiet cartItem : cartItems) {
            gioHangChiTietService.softDeleteById(cartItem.getMaGHCT());
        }
        
        // 10. Tạo response
        OrderCreatedResponse response = OrderCreatedResponse.builder()
                .maDH(maDH)
                .maKH(request.getMaKH())
                .maNV(request.getMaNV())
                .ngayDatHang(savedDonHang.getNgayDatHang())
                .diaChiGiaoHang(diaChiGiaoHang)
                .trangThai("Pending")
                .tongTien(tongTien)
                .items(orderItems)
                .build();
        
        return response;
    }
}
