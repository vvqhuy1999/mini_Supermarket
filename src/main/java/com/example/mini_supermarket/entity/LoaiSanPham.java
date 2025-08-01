package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "LoaiSanPham", indexes = {
    @Index(name = "idx_loaisanpham_cha", columnList = "MaLoaiCha")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoaiSanPham implements Serializable {
    @Id
    @Column(name = "MaLoaiSP", length = 10)
    private String maLoaiSP;

    @Column(name = "TenLoai", length = 255, nullable = false)
    private String tenLoai;

    @Column(name = "MoTa", columnDefinition = "LONGTEXT")
    private String moTa;

    @ManyToOne
    @JoinColumn(name = "MaLoaiCha")
    private LoaiSanPham loaiCha; // Để tạo cây phân loại nhiều cấp

    @Column(name = "ThuTuHienThi")
    private Integer thuTuHienThi = 0;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "loaiSanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SanPham> sanPhams;

    @JsonIgnore
    @OneToMany(mappedBy = "loaiCha", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LoaiSanPham> loaiCon;
} 