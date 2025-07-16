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
@Table(name = "HoaDon", indexes = {
    @Index(name = "idx_hoadon_ngaylap", columnList = "NgayLap"),
    @Index(name = "idx_hoadon_trangthai", columnList = "TrangThai")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaHD")
    private Integer maHD;

    @ManyToOne
    @JoinColumn(name = "MaKH")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "MaNVLap")
    private NhanVien nhanVienLap;

    @ManyToOne
    @JoinColumn(name = "MaKM")
    private KhuyenMai khuyenMai;

    @Column(name = "NgayLap")
    private LocalDateTime ngayLap;

    @Column(name = "TongTien", precision = 15, scale = 2)
    private BigDecimal tongTien;

    @ManyToOne
    @JoinColumn(name = "MaPTTT")
    private PhuongThucThanhToan phuongThucThanhToan;

    @Column(name = "TrangThai")
    private Integer trangThai = 0; // 0=Chờ xử lý, 1=Đã thanh toán, 2=Đang xử lý, 3=Hủy

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "NguoiTao", length = 10)
    private String nguoiTao;

    @Column(name = "NgaySua")
    private LocalDateTime ngaySua;

    @Column(name = "NguoiSua", length = 10)
    private String nguoiSua;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietHoaDon> chiTietHoaDons;

    @JsonIgnore
    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ThanhToan> thanhToans;
} 