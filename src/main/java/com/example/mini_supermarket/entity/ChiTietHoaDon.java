package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietHoaDon", indexes = {
    @Index(name = "idx_chitiethoadon_hoadon", columnList = "MaHD"),
    @Index(name = "idx_chitiethoadon_sanpham", columnList = "MaSP")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietHoaDon implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaCTHD")
    private Integer maCTHD;

    @ManyToOne
    @JoinColumn(name = "MaHD", nullable = false)
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "MaSP", nullable = false)
    private SanPham sanPham;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "DonGiaBan", precision = 15, scale = 2, nullable = false)
    private BigDecimal donGiaBan;

    @Column(name = "ThanhTien", precision = 15, scale = 2)
    private BigDecimal thanhTien; // SoLuong * DonGiaBan (computed)

    @Column(name = "GiamGia", precision = 15, scale = 2)
    private BigDecimal giamGia = BigDecimal.ZERO;

    @Column(name = "ThanhTienSauGiam", precision = 15, scale = 2)
    private BigDecimal thanhTienSauGiam; // ThanhTien - GiamGia (computed)

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 