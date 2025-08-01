package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "SanPham", indexes = {
    @Index(name = "idx_sanpham_loai", columnList = "MaLoaiSP"),
    @Index(name = "idx_sanpham_gia", columnList = "GiaBan"),
    @Index(name = "idx_sanpham_trangthai", columnList = "TrangThai")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPham implements Serializable {
    @Id
    @Column(name = "MaSP", length = 10)
    private String maSP;

    @ManyToOne
    @JoinColumn(name = "MaLoaiSP", nullable = false)
    private LoaiSanPham loaiSanPham;

    @Column(name = "TenSP", length = 255, nullable = false)
    private String tenSP;

    @Column(name = "MoTa", columnDefinition = "LONGTEXT")
    private String moTa;

    @Column(name = "GiaBan", precision = 15, scale = 2, nullable = false)
    private BigDecimal giaBan;

    @Column(name = "DonViTinh", length = 50)
    private String donViTinh = "Cái";

    @Column(name = "TrongLuong", precision = 10, scale = 3)
    private BigDecimal trongLuong;

    @Column(name = "KichThuoc", length = 100)
    private String kichThuoc;

    @Column(name = "HanSuDung")
    private Integer hanSuDung; // Số ngày hạn sử dụng

    @Column(name = "TrangThai")
    private Integer trangThai = 1; // 0=Ngừng kinh doanh, 1=Đang kinh doanh

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietHoaDon> chiTietHoaDons;

    @JsonIgnore
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietGioHang> chiTietGioHangs;

    @JsonIgnore
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietPhieuNhap> chiTietPhieuNhaps;

    @JsonIgnore
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietPhieuXuat> chiTietPhieuXuats;

    @JsonIgnore
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TonKhoChiTiet> tonKhoChiTiets;

    @JsonIgnore
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GiaSanPham> giaSanPhams;

    @JsonIgnore
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HinhAnh> hinhAnhs;

    @JsonIgnore
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KhuyenMaiSanPham> khuyenMaiSanPhams;
} 