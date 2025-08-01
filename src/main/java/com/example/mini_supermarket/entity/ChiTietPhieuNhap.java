package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ChiTietPhieuNhap", indexes = {
    @Index(name = "idx_chitietphieunhap_phieu", columnList = "MaPN"),
    @Index(name = "idx_chitietphieunhap_sanpham", columnList = "MaSP")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuNhap implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaCTPN")
    private Integer maCTPN;

    @ManyToOne
    @JoinColumn(name = "MaPN", nullable = false)
    private PhieuNhapHang phieuNhapHang;

    @ManyToOne
    @JoinColumn(name = "MaSP", nullable = false)
    private SanPham sanPham;

    @Column(name = "SoLuongNhap", nullable = false)
    private Integer soLuongNhap;

    @Column(name = "DonGiaNhap", precision = 15, scale = 2, nullable = false)
    private BigDecimal donGiaNhap;

    @Column(name = "ThanhTien", precision = 15, scale = 2)
    private BigDecimal thanhTien; // SoLuongNhap * DonGiaNhap (computed)

    @Column(name = "NgayHetHan")
    private LocalDate ngayHetHan; // Hạn sử dụng của sản phẩm

    @Column(name = "SoLo", length = 50)
    private String soLo; // Số lô sản xuất

    @Column(name = "NgaySanXuat")
    private LocalDate ngaySanXuat;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 