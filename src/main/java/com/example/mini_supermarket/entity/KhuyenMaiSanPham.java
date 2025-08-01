package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "KhuyenMaiSanPham", indexes = {
    @Index(name = "idx_khuyenmaisanpham_km", columnList = "MaKM"),
    @Index(name = "idx_khuyenmaisanpham_sp", columnList = "MaSP")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMaiSanPham implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKMSP")
    private Integer maKMSP;

    @ManyToOne
    @JoinColumn(name = "MaKM", nullable = false)
    private KhuyenMai khuyenMai;

    @ManyToOne
    @JoinColumn(name = "MaSP", nullable = false)
    private SanPham sanPham;

    @Column(name = "NgayBatDau")
    private LocalDateTime ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 