package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.SanPham;
import com.example.mini_supermarket.dto.SanPhamOptimizedDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SanPhamService {
    // === METHODS CƠ BẢN - TRẢ VỀ ENTITY ĐẦY ĐỦ ===
    
    List<SanPham> findAll();

    List<SanPham> findAllActive(); // Chỉ lấy các record chưa bị xóa

    SanPham findById(String id);

    SanPham findActiveById(String id); // Chỉ lấy record chưa bị xóa

    SanPham save(SanPham sanPham);

    void deleteById(String id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(String id); // Soft delete - set isDeleted = true

    SanPham update(SanPham sanPham);
    
    // === METHODS TỐI ƯU - SỬ DỤNG DTO VỚI @BUILDER ===
    
    // Lấy tất cả sản phẩm active - tối ưu
    List<SanPhamOptimizedDto> findAllActiveOptimized();
    
    // Lấy sản phẩm theo ID - tối ưu
    SanPhamOptimizedDto findActiveByIdOptimized(String id);
    
    // Lấy sản phẩm theo category - tối ưu (không có trangThai, ngayTao, isDeleted)
    List<SanPhamOptimizedDto> findByCategoryOptimized(String maLoaiSP);
    
    // Lấy sản phẩm theo category và trạng thái kinh doanh - tối ưu
    List<SanPhamOptimizedDto> findByCategoryAndActiveOptimized(String maLoaiSP);
} 