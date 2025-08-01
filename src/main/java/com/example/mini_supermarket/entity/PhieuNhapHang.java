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
@Table(name = "PhieuNhapHang", indexes = {
    @Index(name = "idx_phieunhap_ngay", columnList = "NgayNhap"),
    @Index(name = "idx_phieunhap_trangthai", columnList = "TrangThai"),
    @Index(name = "idx_phieunhap_nhacungcap", columnList = "MaNCC")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuNhapHang implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaPN")
    private Integer maPN;

    @ManyToOne
    @JoinColumn(name = "MaNCC", nullable = false)
    private NhaCungCap nhaCungCap;

    @ManyToOne
    @JoinColumn(name = "MaKho", nullable = false)
    private Kho kho;

    @ManyToOne
    @JoinColumn(name = "MaNVLap", nullable = false)
    private NhanVien nhanVienLap;

    @Column(name = "NgayNhap", nullable = false)
    private LocalDateTime ngayNhap;

    @Column(name = "TongTienNhap", precision = 15, scale = 2)
    private BigDecimal tongTienNhap = BigDecimal.ZERO;

    @Column(name = "TrangThai")
    private Integer trangThai = 0; // 0=Chờ xử lý, 1=Đã nhập kho, 2=Từ chối, 3=Hủy

    @Column(name = "GhiChu", columnDefinition = "LONGTEXT")
    private String ghiChu;

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "phieuNhapHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietPhieuNhap> chiTietPhieuNhaps;
} 