package com.example.mini_supermarket.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Định nghĩa các cache names
        cacheManager.setCacheNames(java.util.Arrays.asList(
            // ===== HÓA ĐƠN CACHE =====
            "hoadon-summary",           // Cache cho danh sách hóa đơn summary
            "hoadon-by-customer",       // Cache cho hóa đơn theo khách hàng
            "hoadon-by-status",         // Cache cho hóa đơn theo trạng thái
            "hoadon-count",             // Cache cho số lượng hóa đơn
            "hoadon-statistics",        // Cache cho thống kê hóa đơn
            "hoadon-full-details",      // Cache cho hóa đơn chi tiết đầy đủ
            
            // ===== SẢN PHẨM CACHE =====
            "sanpham-list",             // Cache cho danh sách sản phẩm
            "sanpham-detail",           // Cache cho chi tiết sản phẩm
            "sanpham-by-category",      // Cache cho sản phẩm theo category
            "sanpham-active",           // Cache cho sản phẩm đang hoạt động
            "sanpham-with-tonkho",      // Cache cho sản phẩm với tồn kho
            "sanpham-optimized",        // Cache cho sản phẩm tối ưu
            "sanpham-search",           // Cache cho tìm kiếm sản phẩm
            
            // ===== CATEGORY CACHE =====
            "loaisanpham-list",         // Cache cho danh sách loại sản phẩm
            "loaisanpham-detail",       // Cache cho chi tiết loại sản phẩm
            "loaisanpham-active",       // Cache cho loại sản phẩm đang hoạt động
            "loaisanpham-tree",         // Cache cho cây category (nếu có hierarchy)
            
            // ===== KHÁCH HÀNG & NHÂN VIÊN CACHE =====
            "khachhang-info",           // Cache cho thông tin khách hàng
            "khachhang-profile",        // Cache cho profile khách hàng
            "nhanvien-info",            // Cache cho thông tin nhân viên
            
            // ===== GIỎ HÀNG CACHE =====
            "giohang-by-customer",      // Cache cho giỏ hàng theo khách hàng
            "giohang-items",            // Cache cho items trong giỏ hàng
            
            // ===== KHUYẾN MÃI CACHE =====
            "khuyenmai-active",         // Cache cho khuyến mãi đang hoạt động
            "khuyenmai-by-product",     // Cache cho khuyến mãi theo sản phẩm
            "khuyenmai-by-customer",    // Cache cho khuyến mãi theo khách hàng
            
            // ===== TỒN KHO & GIÁ CACHE =====
            "tonkho-by-product",        // Cache cho tồn kho theo sản phẩm
            "giasanpham-current",       // Cache cho giá hiện tại sản phẩm
            "giasanpham-history",       // Cache cho lịch sử giá sản phẩm
            
            // ===== SYSTEM CACHE =====
            "system-config",            // Cache cho cấu hình hệ thống
            "user-permissions"          // Cache cho quyền người dùng
        ));
        
        return cacheManager;
    }
}
