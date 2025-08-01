package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "GiaSanPham", indexes = {
    @Index(name = "idx_giasanpham_sanpham", columnList = "MaSP"),
    @Index(name = "idx_giasanpham_ngay", columnList = "NgayBatDau, NgayKetThuc")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiaSanPham implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaGia")
    private Integer maGia;

    @ManyToOne
    @JoinColumn(name = "MaSP", nullable = false)
    private SanPham sanPham;

    @Column(name = "Gia", precision = 15, scale = 2, nullable = false)
    private BigDecimal gia;

    @Column(name = "NgayBatDau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDate ngayKetThuc;

    @Column(name = "LyDoThayDoi", length = 255)
    private String lyDoThayDoi;

    @ManyToOne
    @JoinColumn(name = "NguoiThayDoi")
    private NhanVien nguoiThayDoi;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 