package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "CuaHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuaHang implements Serializable {
    @Id
    @Column(name = "MaCH", length = 10)
    private String maCH;

    @Column(name = "TenCH", length = 255)
    private String tenCH;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @Column(name = "SDT", length = 15)
    private String sdt;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "cuaHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NhanVien> nhanViens;

    @JsonIgnore
    @OneToMany(mappedBy = "cuaHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Kho> khos;

    @JsonIgnore
    @OneToMany(mappedBy = "cuaHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ThongKeBaoCao> thongKeBaoCaos;
} 