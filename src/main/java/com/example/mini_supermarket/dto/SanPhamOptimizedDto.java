package com.example.mini_supermarket.dto;

import com.example.mini_supermarket.entity.LoaiSanPham;
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
@Builder
public class SanPhamOptimizedDto {
    
    private String maSP;
    private LoaiSanPham loaiSanPham;
    private String tenSP;
    private String moTa;
    private String donViTinh;
    private BigDecimal trongLuong;
    private String kichThuoc;
    private Integer hanSuDung;

    // Giá hiện tại (không nằm trong constructor JPQL)
    private BigDecimal giaHienTai;

    // Constructor khớp với JPQL constructor expression trong repository
    public SanPhamOptimizedDto(String maSP,
                               LoaiSanPham loaiSanPham,
                               String tenSP,
                               String moTa,
                               String donViTinh,
                               BigDecimal trongLuong,
                               String kichThuoc,
                               Integer hanSuDung) {
        this.maSP = maSP;
        this.loaiSanPham = loaiSanPham;
        this.tenSP = tenSP;
        this.moTa = moTa;
        this.donViTinh = donViTinh;
        this.trongLuong = trongLuong;
        this.kichThuoc = kichThuoc;
        this.hanSuDung = hanSuDung;
    }


    // Overload constructor to satisfy any JPQL selecting giaHienTai as last argument
    public SanPhamOptimizedDto(String maSP,
                               LoaiSanPham loaiSanPham,
                               String tenSP,
                               String moTa,
                               String donViTinh,
                               BigDecimal trongLuong,
                               String kichThuoc,
                               Integer hanSuDung,
                               BigDecimal giaHienTai) {
        this(maSP, loaiSanPham, tenSP, moTa, donViTinh, trongLuong, kichThuoc, hanSuDung);
        this.giaHienTai = giaHienTai;
    }
}
