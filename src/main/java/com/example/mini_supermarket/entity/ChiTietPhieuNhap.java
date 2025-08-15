package com.example.mini_supermarket.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // @Column(name = "ThanhTien", precision = 15, scale = 2)
    // private BigDecimal thanhTien; // SoLuongNhap * DonGiaNhap (computed)

    @Column(name = "ThanhTien", precision = 15, scale = 2, insertable = false, updatable = false)
    private BigDecimal thanhTien;

    @Column(name = "NgayHetHan")
    private LocalDate ngayHetHan; // Hạn sử dụng của sản phẩm

    @Column(name = "SoLo", length = 50)
    private String soLo; // Số lô sản xuất

    @Column(name = "NgaySanXuat")
    private LocalDate ngaySanXuat;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 