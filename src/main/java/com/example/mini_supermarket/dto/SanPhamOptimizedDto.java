package com.example.mini_supermarket.dto;

import com.example.mini_supermarket.entity.LoaiSanPham;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO tối ưu cho sản phẩm - chỉ chứa những trường cần thiết
 * Không có: trangThai, ngayTao, isDeleted
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamOptimizedDto {
    
    private String maSP;
    private LoaiSanPham loaiSanPham;
    private String tenSP;
    private String moTa;
    private BigDecimal giaBan;
    private String donViTinh;
    private BigDecimal trongLuong;
    private String kichThuoc;
    private Integer hanSuDung;
}
