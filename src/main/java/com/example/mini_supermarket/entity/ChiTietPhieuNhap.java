package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ChiTietPhieuNhap")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuNhap implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaCTPN")
    private Integer maCTPN;

    @ManyToOne
    @JoinColumn(name = "MaPN")
    private PhieuNhapHang phieuNhapHang;

    @ManyToOne
    @JoinColumn(name = "MaSP")
    private SanPham sanPham;

    @Column(name = "SoLuongNhap")
    private Integer soLuongNhap;

    @Column(name = "DonGiaNhap", precision = 15, scale = 2)
    private BigDecimal donGiaNhap;

    @Column(name = "NgayHetHan")
    private LocalDate ngayHetHan;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 