package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.repository.GioHangChiTietRepository;
import com.example.mini_supermarket.entity.GioHangChiTiet;
import com.example.mini_supermarket.entity.KhachHang;
import com.example.mini_supermarket.entity.SanPham;
import com.example.mini_supermarket.entity.NhanVien;
import com.example.mini_supermarket.service.GioHangChiTietService;
import com.example.mini_supermarket.service.KhachHangService;
import com.example.mini_supermarket.service.SanPhamService;
import com.example.mini_supermarket.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GioHangChiTietServiceImpl implements GioHangChiTietService {

    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepository;
    
    @Autowired
    private KhachHangService khachHangService;
    
    @Autowired
    private SanPhamService sanPhamService;
    
    @Autowired
    private NhanVienService nhanVienService;

    @Override
    @Transactional(readOnly = true)
    public List<GioHangChiTiet> findAll() {
        return gioHangChiTietRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GioHangChiTiet> findAllActive() {
        return gioHangChiTietRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public GioHangChiTiet findById(Integer id) {
        return gioHangChiTietRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public GioHangChiTiet findActiveById(Integer id) {
        return gioHangChiTietRepository.findActiveById(id).orElse(null);
    }

    @Override
    @Transactional
    public GioHangChiTiet save(GioHangChiTiet gioHangChiTiet) {
        return gioHangChiTietRepository.save(gioHangChiTiet);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        gioHangChiTietRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDeleteById(Integer id) {
        // Soft delete no longer supported -> hard delete
        gioHangChiTietRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GioHangChiTiet> findByMaKhachHang(String maKH) {
        return gioHangChiTietRepository.findByKhachHang_MaKHAndIsDeletedFalse(maKH);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GioHangChiTiet> findActiveCartItemsByCustomer(String maKH) {
        return gioHangChiTietRepository.findActiveCartItemsByCustomerAndStatus(maKH, 0); // 0 = Shopping
    }

    @Override
    @Transactional(readOnly = true)
    public List<GioHangChiTiet> findCartItemsByCustomerAndStatus(String maKH, Integer trangThai) {
        return gioHangChiTietRepository.findActiveCartItemsByCustomerAndStatus(maKH, trangThai);
    }

    @Override
    @Transactional(readOnly = true)
    public GioHangChiTiet findExistingCartItem(String maKH, String maSP, Integer trangThai) {
        return gioHangChiTietRepository.findByKhachHangAndSanPhamAndTrangThai(maKH, maSP, trangThai).orElse(null);
    }

    @Override
    @Transactional
    public GioHangChiTiet addOrUpdateCartItem(String maKH, String maSP, Integer soLuong, BigDecimal donGia, String manvGuid) {
        // Tìm item hiện có
        GioHangChiTiet existingItem = findExistingCartItem(maKH, maSP, 0); // 0 = Shopping
        
        if (existingItem != null) {
            // Cập nhật số lượng
            existingItem.setSoLuong(existingItem.getSoLuong() + soLuong);
            if (donGia != null && donGia.compareTo(BigDecimal.ZERO) > 0) {
                existingItem.setDonGiaHienTai(donGia);
            }
            return save(existingItem);
        } else {
            // Tạo mới
            GioHangChiTiet newItem = new GioHangChiTiet();
            
            // Set khách hàng
            KhachHang khachHang = khachHangService.findById(maKH);
            if (khachHang == null) {
                throw new RuntimeException("Khách hàng không tồn tại: " + maKH);
            }
            newItem.setKhachHang(khachHang);
            
            // Set sản phẩm
            SanPham sanPham = sanPhamService.findById(maSP);
            if (sanPham == null) {
                throw new RuntimeException("Sản phẩm không tồn tại: " + maSP);
            }
            newItem.setSanPham(sanPham);
            
            // Set nhân viên nếu có
            if (manvGuid != null && !manvGuid.trim().isEmpty()) {
                NhanVien nhanVien = nhanVienService.findById(manvGuid);
                newItem.setNhanVien(nhanVien);
            }
            
            newItem.setSoLuong(soLuong);
            newItem.setDonGiaHienTai(donGia != null ? donGia : BigDecimal.ZERO);
            newItem.setTrangThai(0); // Shopping
            
            return save(newItem);
        }
    }

    @Override
    @Transactional
    public void clearCartByCustomerAndStatus(String maKH, Integer trangThai) {
        List<GioHangChiTiet> items = findCartItemsByCustomerAndStatus(maKH, trangThai);
        for (GioHangChiTiet item : items) {
            gioHangChiTietRepository.deleteById(item.getMaGHCT());
        }
    }

    @Override
    @Transactional
    public void updateCartItemsStatus(String maKH, Integer oldStatus, Integer newStatus) {
        // Nếu chuyển sang Paid (1) hoặc Canceled (2) => xóa giỏ hàng (clear items)
        if (newStatus != null && (newStatus == 1 || newStatus == 2)) {
            clearCartByCustomerAndStatus(maKH, oldStatus);
            return;
        }

        // Ngược lại: cập nhật trạng thái các item hiện tại
        List<GioHangChiTiet> items = findCartItemsByCustomerAndStatus(maKH, oldStatus);
        for (GioHangChiTiet item : items) {
            item.setTrangThai(newStatus);
            save(item);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int countActiveCartItemsByCustomer(String maKH) {
        return findActiveCartItemsByCustomer(maKH).size();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalByCustomerAndStatus(String maKH, Integer trangThai) {
        List<GioHangChiTiet> items = findCartItemsByCustomerAndStatus(maKH, trangThai);
        return items.stream()
                .map(item -> item.getThanhTien() != null ? item.getThanhTien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
