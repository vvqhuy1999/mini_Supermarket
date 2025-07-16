package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "NhanVien")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien implements Serializable {
    @Id
    @Column(name = "MaNV", length = 10)
    private String maNV;

    @ManyToOne
    @JoinColumn(name = "MaNguoiDung")
    private NguoiDung nguoiDung;

    @Column(name = "HoTen", length = 255)
    private String hoTen;

    @Column(name = "SDT", length = 15)
    private String sdt;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "Luong", precision = 15, scale = 2)
    private BigDecimal luong;

    @ManyToOne
    @JoinColumn(name = "MaQuanLy")
    private NhanVien quanLy;

    @ManyToOne
    @JoinColumn(name = "MaCH")
    private CuaHang cuaHang;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "nhanVienLap", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDon> hoaDonsLap;

    @JsonIgnore
    @OneToMany(mappedBy = "nhanVien", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GioHang> gioHangs;

    @JsonIgnore
    @OneToMany(mappedBy = "nhanVienLap", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhieuNhapHang> phieuNhapHangs;

    @JsonIgnore
    @OneToMany(mappedBy = "nhanVienLap", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhieuXuatKho> phieuXuatKhos;

    @JsonIgnore
    @OneToMany(mappedBy = "nhanVien", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LichLamViec> lichLamViecs;

    @JsonIgnore
    @OneToMany(mappedBy = "nhanVienQuanLy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LichLamViec> lichQuanLys;

    @JsonIgnore
    @OneToMany(mappedBy = "quanLy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NhanVien> nhanVienDuoiQuyen;

    @JsonIgnore
    @OneToMany(mappedBy = "nhanVien", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ThongKeBaoCao> thongKeBaoCaos;
} 