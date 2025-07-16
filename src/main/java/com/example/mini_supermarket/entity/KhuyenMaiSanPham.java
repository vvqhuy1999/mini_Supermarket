package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "KhuyenMaiSanPham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMaiSanPham implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKMSP")
    private Integer maKMSP;

    @ManyToOne
    @JoinColumn(name = "MaKM")
    private KhuyenMai khuyenMai;

    @ManyToOne
    @JoinColumn(name = "MaSP")
    private SanPham sanPham;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 