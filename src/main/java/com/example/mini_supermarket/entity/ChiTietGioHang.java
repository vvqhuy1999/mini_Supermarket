package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ChiTietGioHang", indexes = {
    @Index(name = "idx_chitietgiohang_giohang", columnList = "MaGH"),
    @Index(name = "idx_chitietgiohang_sanpham", columnList = "MaSP")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietGioHang implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaCTGH")
    private Integer maCTGH;

    @ManyToOne
    @JoinColumn(name = "MaGH", nullable = false)
    private GioHang gioHang;

    @ManyToOne
    @JoinColumn(name = "MaSP", nullable = false)
    private SanPham sanPham;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "DonGiaHienTai", precision = 15, scale = 2, nullable = false)
    private BigDecimal donGiaHienTai; // Giá sản phẩm tại thời điểm thêm vào giỏ

    @Column(name = "ThanhTien", precision = 15, scale = 2)
    private BigDecimal thanhTien; // SoLuong * DonGiaHienTai (computed)

    @Column(name = "NgayThem")
    private LocalDateTime ngayThem = LocalDateTime.now();

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 