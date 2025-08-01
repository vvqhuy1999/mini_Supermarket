package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietPhieuXuat", indexes = {
    @Index(name = "idx_chitietphieuxuat_phieu", columnList = "MaPXK"),
    @Index(name = "idx_chitietphieuxuat_sanpham", columnList = "MaSP")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuXuat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaCTPXK")
    private Integer maCTPXK;

    @ManyToOne
    @JoinColumn(name = "MaPXK", nullable = false)
    private PhieuXuatKho phieuXuatKho;

    @ManyToOne
    @JoinColumn(name = "MaSP", nullable = false)
    private SanPham sanPham;

    @Column(name = "SoLuongXuat", nullable = false)
    private Integer soLuongXuat;

    @Column(name = "DonGiaXuat", precision = 15, scale = 2, nullable = false)
    private BigDecimal donGiaXuat;

    @Column(name = "ThanhTien", precision = 15, scale = 2)
    private BigDecimal thanhTien; // SoLuongXuat * DonGiaXuat (computed)

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 