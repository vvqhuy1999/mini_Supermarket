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
@Table(name = "Kho", indexes = {
    @Index(name = "idx_kho_cuahang", columnList = "MaCH"),
    @Index(name = "idx_kho_trangthai", columnList = "TrangThai")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kho implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKho")
    private Integer maKho;

    @Column(name = "TenKho", length = 255, nullable = false)
    private String tenKho;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @Column(name = "DienTich", precision = 10, scale = 2)
    private BigDecimal dienTich; // Diện tích kho (m²)

    @Column(name = "SucChua", precision = 15, scale = 2)
    private BigDecimal sucChua; // Sức chứa tối đa

    @ManyToOne
    @JoinColumn(name = "MaCH")
    private CuaHang cuaHang;

    @Column(name = "TrangThai")
    private Integer trangThai = 1; // 0=Đóng cửa, 1=Hoạt động

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "kho", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TonKhoChiTiet> tonKhoChiTiets;

    @JsonIgnore
    @OneToMany(mappedBy = "kho", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhieuNhapHang> phieuNhapHangs;

    @JsonIgnore
    @OneToMany(mappedBy = "kho", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhieuXuatKho> phieuXuatKhos;
} 