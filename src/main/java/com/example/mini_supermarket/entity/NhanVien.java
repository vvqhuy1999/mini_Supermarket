package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "NhanVien", indexes = {
    @Index(name = "idx_nhanvien_cuahang", columnList = "MaCH"),
    @Index(name = "idx_nhanvien_trangthai", columnList = "TrangThai")
})
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

    @Column(name = "HoTen", length = 255, nullable = false)
    private String hoTen;

    @Column(name = "SDT", length = 15)
    private String sdt;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "NgayVaoLam")
    private LocalDate ngayVaoLam;

    @Column(name = "ChucVu", length = 100)
    private String chucVu;

    @ManyToOne
    @JoinColumn(name = "MaQuanLy")
    private NhanVien quanLy;

    @ManyToOne
    @JoinColumn(name = "MaCH")
    private CuaHang cuaHang;

    @Column(name = "TrangThai")
    private Integer trangThai = 1; // 0=Nghỉ việc, 1=Đang làm việc

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

    @JsonIgnore
    @OneToMany(mappedBy = "nhanVien", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BangLuong> bangLuongs;

    @JsonIgnore
    @OneToMany(mappedBy = "quanLy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KhuyenMai> khuyenMais;

    @JsonIgnore
    @OneToMany(mappedBy = "nguoiThayDoi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GiaSanPham> giaSanPhams;

    @JsonIgnore
    @OneToMany(mappedBy = "nguoiThanhToan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BangLuong> bangLuongThanhToans;

    @JsonIgnore
    @OneToMany(mappedBy = "nguoiTao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDon> hoaDonsTao;

    @JsonIgnore
    @OneToMany(mappedBy = "nguoiSua", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDon> hoaDonsSua;
} 