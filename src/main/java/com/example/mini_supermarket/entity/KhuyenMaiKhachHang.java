package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "KhuyenMaiKhachHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMaiKhachHang implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKMKH")
    private Integer maKMKH;

    @ManyToOne
    @JoinColumn(name = "MaKM")
    private KhuyenMai khuyenMai;

    @ManyToOne
    @JoinColumn(name = "MaKH")
    private KhachHang khachHang;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 