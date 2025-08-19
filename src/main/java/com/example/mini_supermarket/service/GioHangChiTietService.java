package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.GioHangChiTiet;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GioHangChiTietService {
    // Basic CRUD operations
    List<GioHangChiTiet> findAll();
    List<GioHangChiTiet> findAllActive(); // same as findAll now (no soft delete)
    GioHangChiTiet findById(Integer id);
    GioHangChiTiet findActiveById(Integer id);
    GioHangChiTiet save(GioHangChiTiet gioHangChiTiet);
    void deleteById(Integer id);
    void softDeleteById(Integer id); // becomes hard delete

    // Cart-specific operations
    List<GioHangChiTiet> findByMaKhachHang(String maKH);
    List<GioHangChiTiet> findActiveCartItemsByCustomer(String maKH);
    List<GioHangChiTiet> findCartItemsByCustomerAndStatus(String maKH, Integer trangThai);
    GioHangChiTiet findExistingCartItem(String maKH, String maSP, Integer trangThai);
    
    // Cart management operations
    GioHangChiTiet addOrUpdateCartItem(String maKH, String maSP, Integer soLuong, java.math.BigDecimal donGia, String manvGuid);
    void clearCartByCustomerAndStatus(String maKH, Integer trangThai);
    void updateCartItemsStatus(String maKH, Integer oldStatus, Integer newStatus);
    
    // Statistics
    int countActiveCartItemsByCustomer(String maKH);
    java.math.BigDecimal calculateTotalByCustomerAndStatus(String maKH, Integer trangThai);
}
