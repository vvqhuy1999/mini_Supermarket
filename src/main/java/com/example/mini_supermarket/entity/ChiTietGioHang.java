package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietGioHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietGioHang implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaCTGH")
    private Integer maCTGH;

    @ManyToOne
    @JoinColumn(name = "MaGH")
    private GioHang gioHang;

    @ManyToOne
    @JoinColumn(name = "MaSP")
    private SanPham sanPham;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "DonGiaHienTai", precision = 15, scale = 2)
    private BigDecimal donGiaHienTai;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 