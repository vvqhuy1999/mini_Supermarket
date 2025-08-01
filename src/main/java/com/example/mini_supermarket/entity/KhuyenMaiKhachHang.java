package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "KhuyenMaiKhachHang", indexes = {
    @Index(name = "idx_khuyenmaikhachhang_km", columnList = "MaKM"),
    @Index(name = "idx_khuyenmaikhachhang_kh", columnList = "MaKH")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMaiKhachHang implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKMKH")
    private Integer maKMKH;

    @ManyToOne
    @JoinColumn(name = "MaKM", nullable = false)
    private KhuyenMai khuyenMai;

    @ManyToOne
    @JoinColumn(name = "MaKH", nullable = false)
    private KhachHang khachHang;

    @Column(name = "NgayApDung")
    private LocalDateTime ngayApDung = LocalDateTime.now();

    @Column(name = "DaSuDung")
    private Boolean daSuDung = false;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 