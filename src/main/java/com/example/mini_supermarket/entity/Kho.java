package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Kho")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kho implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKho")
    private Integer maKho;

    @Column(name = "TenKho", length = 255)
    private String tenKho;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @ManyToOne
    @JoinColumn(name = "MaCH")
    private CuaHang cuaHang;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "kho", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhieuNhapHang> phieuNhapHangs;

    @JsonIgnore
    @OneToMany(mappedBy = "kho", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhieuXuatKho> phieuXuatKhos;

    @JsonIgnore
    @OneToMany(mappedBy = "kho", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TonKhoChiTiet> tonKhoChiTiets;
} 