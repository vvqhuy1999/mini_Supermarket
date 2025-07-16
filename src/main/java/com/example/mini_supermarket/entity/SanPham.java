package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "SanPham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPham implements Serializable {
    @Id
    @Column(name = "MaSP", length = 10)
    private String maSP;

    @ManyToOne
    @JoinColumn(name = "MaLoaiSP")
    private LoaiSanPham loaiSanPham;

    @Column(name = "TenSP", length = 255)
    private String tenSP;

    @Column(name = "MoTa", columnDefinition = "LONGTEXT")
    private String moTa;

    @Column(name = "GiaBan", precision = 15, scale = 2)
    private BigDecimal giaBan;

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