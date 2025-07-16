package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "KhuyenMai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai implements Serializable {
    @Id
    @Column(name = "MaKM", length = 10)
    private String maKM;

    @Column(name = "TenChuongTrinh", length = 255)
    private String tenChuongTrinh;

    @Column(name = "MoTa", columnDefinition = "LONGTEXT")
    private String moTa;

    @Column(name = "LoaiKM", length = 50)
    private String loaiKM; // PhầnTrăm, Điểm, Khác

    @Column(name = "GiaTriKM", precision = 15, scale = 2)
    private BigDecimal giaTriKM;

    @Column(name = "NgayBatDau")
    private LocalDateTime ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "khuyenMai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons;

    @JsonIgnore
    @OneToMany(mappedBy = "khuyenMai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KhuyenMaiSanPham> khuyenMaiSanPhams;

    @JsonIgnore
    @OneToMany(mappedBy = "khuyenMai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KhuyenMaiKhachHang> khuyenMaiKhachHangs;
} 