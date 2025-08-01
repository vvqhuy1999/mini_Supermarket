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
    @Index(name = "idx_hoadon_trangthai", columnList = "TrangThai"),
    @Index(name = "idx_hoadon_khachhang", columnList = "MaKH"),
    @Index(name = "idx_hoadon_nhanvien", columnList = "MaNVLap")
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
    @JoinColumn(name = "MaNVLap", nullable = false)
    private NhanVien nhanVienLap;

    @ManyToOne
    @JoinColumn(name = "MaKM")
    private KhuyenMai khuyenMai;

    @Column(name = "NgayLap", nullable = false)
    private LocalDateTime ngayLap;

    @Column(name = "TongTienHang", precision = 15, scale = 2)
    private BigDecimal tongTienHang = BigDecimal.ZERO;

    @Column(name = "TienGiamGia", precision = 15, scale = 2)
    private BigDecimal tienGiamGia = BigDecimal.ZERO;

    @Column(name = "TongTien", precision = 15, scale = 2)
    private BigDecimal tongTien;

    @ManyToOne
    @JoinColumn(name = "MaPTTT")
    private PhuongThucThanhToan phuongThucThanhToan;

    @Column(name = "TrangThai")
    private Integer trangThai = 0; // 0=Chờ xử lý, 1=Đã thanh toán, 2=Đang xử lý, 3=Hủy, 4=Hoàn trả

    @Column(name = "DiemTichLuy")
    private Integer diemTichLuy = 0; // Điểm tích lũy từ hóa đơn này

    @Column(name = "GhiChu", columnDefinition = "LONGTEXT")
    private String ghiChu;

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "NguoiTao")
    private NhanVien nguoiTao;

    @Column(name = "NgaySua")
    private LocalDateTime ngaySua;

    @ManyToOne
    @JoinColumn(name = "NguoiSua")
    private NhanVien nguoiSua;

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