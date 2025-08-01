package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "HinhAnh", indexes = {
    @Index(name = "idx_hinhanh_sanpham", columnList = "MaSP"),
    @Index(name = "idx_hinhanh_chinh", columnList = "LaChinh")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HinhAnh implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaHinh")
    private Integer maHinh;

    @ManyToOne
    @JoinColumn(name = "MaSP", nullable = false)
    private SanPham sanPham;

    @Column(name = "URL", length = 500, nullable = false)
    private String url;

    @Column(name = "MoTa", length = 255)
    private String moTa;

    @Column(name = "LaChinh")
    private Boolean laChinh = false; // Đánh dấu ảnh chính của sản phẩm

    @Column(name = "ThuTuHienThi")
    private Integer thuTuHienThi = 0; // Thứ tự hiển thị ảnh

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 