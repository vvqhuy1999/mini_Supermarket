package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "HinhAnh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HinhAnh implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaHinh")
    private Integer maHinh;

    @ManyToOne
    @JoinColumn(name = "MaSP")
    private SanPham sanPham;

    @Column(name = "URL", length = 255)
    private String url;

    @Column(name = "MoTa", length = 255)
    private String moTa;

    @Column(name = "LaChinh")
    private Boolean laChinh = false;

    @Column(name = "ThuTuHienThi")
    private Integer thuTuHienThi = 0;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 