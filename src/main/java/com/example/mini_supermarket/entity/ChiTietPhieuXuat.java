package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietPhieuXuat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuXuat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaCTPXK")
    private Integer maCTPXK;

    @ManyToOne
    @JoinColumn(name = "MaPXK")
    private PhieuXuatKho phieuXuatKho;

    @ManyToOne
    @JoinColumn(name = "MaSP")
    private SanPham sanPham;

    @Column(name = "SoLuongXuat")
    private Integer soLuongXuat;

    @Column(name = "DonGiaXuat", precision = 15, scale = 2)
    private BigDecimal donGiaXuat;

    @Column(name = "ThanhTien", precision = 15, scale = 2)
    private BigDecimal thanhTien;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 