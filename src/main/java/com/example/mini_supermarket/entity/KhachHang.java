package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "KhachHang", indexes = {
    @Index(name = "idx_khachhang_sdt", columnList = "SDT"),
    @Index(name = "idx_khachhang_email", columnList = "Email"),
    @Index(name = "idx_khachhang_loai", columnList = "LoaiKhachHang")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang implements Serializable {
    @Id
    @Column(name = "MaKH", length = 10)
    private String maKH;

    @ManyToOne
    @JoinColumn(name = "MaNguoiDung")
    private NguoiDung nguoiDung;

    @Column(name = "HoTen", length = 255, nullable = false)
    private String hoTen;

    @Column(name = "SDT", length = 15)
    private String sdt;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "DiemTichLuy")
    private Integer diemTichLuy = 0;

    @Column(name = "LoaiKhachHang", length = 50)
    private String loaiKhachHang = "Thường"; // Thường, VIP, Bạc, Vàng, Kim cương

    @Column(name = "NgayDangKy")
    private LocalDateTime ngayDangKy = LocalDateTime.now();

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons;

    @JsonIgnore
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GioHang> gioHangs;

    @JsonIgnore
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KhuyenMaiKhachHang> khuyenMaiKhachHangs;
} 