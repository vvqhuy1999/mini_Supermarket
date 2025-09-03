package com.example.mini_supermarket.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class TokenBlacklistService {
    
    // Sử dụng ConcurrentHashMap để thread-safe
    private final ConcurrentMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    
    /**
     * Thêm token vào blacklist
     * @param token JWT token cần blacklist
     */
    public void blacklistToken(String token) {
        blacklistedTokens.put(token, System.currentTimeMillis());
        
        // Tự động cleanup nếu blacklist có quá 1000 token
        if (blacklistedTokens.size() > 1000) {
            cleanupBlacklist();
        }
    }
    
    /**
     * Kiểm tra token có trong blacklist không
     * @param token JWT token cần kiểm tra
     * @return true nếu token bị blacklist, false nếu không
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }
    
    /**
     * Xóa token khỏi blacklist (nếu cần)
     * @param token JWT token cần xóa
     */
    public void removeFromBlacklist(String token) {
        blacklistedTokens.remove(token);
    }
    
    /**
     * Dọn dẹp blacklist (xóa các token cũ)
     * Có thể gọi định kỳ để tránh memory leak
     */
    public void cleanupBlacklist() {
        long currentTime = System.currentTimeMillis();
        // Xóa các token cũ hơn 24 giờ
        long cutoffTime = currentTime - (24 * 60 * 60 * 1000);
        
        int removedCount = 0;
        for (Map.Entry<String, Long> entry : blacklistedTokens.entrySet()) {
            if (entry.getValue() < cutoffTime) {
                blacklistedTokens.remove(entry.getKey());
                removedCount++;
            }
        }
        
        if (removedCount > 0) {
            System.out.println("Cleaned up " + removedCount + " expired tokens from blacklist");
        }
    }
    

    
    /**
     * Lấy số lượng token trong blacklist
     * @return Số lượng token
     */
    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }
    
    /**
     * Lấy danh sách tất cả token trong blacklist với thông tin chi tiết
     * @return Map chứa token và thời gian bị blacklist
     */
    public Map<String, Object> getBlacklistDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("totalCount", blacklistedTokens.size());
        details.put("blacklistedTokens", new HashMap<>(blacklistedTokens));
        return details;
    }
    
    /**
     * Lấy thông tin chi tiết của một token cụ thể
     * @param token JWT token cần kiểm tra
     * @return Map chứa thông tin token hoặc null nếu không có trong blacklist
     */
    public Map<String, Object> getTokenDetails(String token) {
        if (!blacklistedTokens.containsKey(token)) {
            return null;
        }
        
        Map<String, Object> details = new HashMap<>();
        details.put("token", token);
        details.put("blacklistedAt", blacklistedTokens.get(token));
        details.put("blacklistedAtFormatted", new Date(blacklistedTokens.get(token)));
        
        return details;
    }
    
    /**
     * Tự động cleanup blacklist mỗi 6 giờ
     * Chạy vào 00:00, 06:00, 12:00, 18:00 mỗi ngày
     */
    @Scheduled(cron = "0 0 */6 * * ?")
    public void scheduledCleanup() {
        cleanupBlacklist();
    }
} 