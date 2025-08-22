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
            "hoadon-summary",           // Cache cho danh sách hóa đơn summary
            "hoadon-by-customer",       // Cache cho hóa đơn theo khách hàng
            "hoadon-count",             // Cache cho số lượng hóa đơn
            "sanpham-list",             // Cache cho danh sách sản phẩm
            "khachhang-info",           // Cache cho thông tin khách hàng
            "nhanvien-info"             // Cache cho thông tin nhân viên
        ));
        
        return cacheManager;
    }
}
