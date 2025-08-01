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
@Table(name = "PhieuXuatKho", indexes = {
    @Index(name = "idx_phieuxuat_ngay", columnList = "NgayXuat"),
    @Index(name = "idx_phieuxuat_trangthai", columnList = "TrangThai"),
    @Index(name = "idx_phieuxuat_kho", columnList = "MaKho")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuXuatKho implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaPXK")
    private Integer maPXK;

    @ManyToOne
    @JoinColumn(name = "MaKho", nullable = false)
    private Kho kho;

    @ManyToOne
    @JoinColumn(name = "MaNVLap", nullable = false)
    private NhanVien nhanVienLap;

    @Column(name = "NgayXuat", nullable = false)
    private LocalDateTime ngayXuat;

    @Column(name = "TongSoLuong")
    private Integer tongSoLuong = 0;

    @Column(name = "TongGiaTri", precision = 15, scale = 2)
    private BigDecimal tongGiaTri = BigDecimal.ZERO;

    @Column(name = "LyDoXuat", length = 255)
    private String lyDoXuat;

    @Column(name = "TrangThai")
    private Integer trangThai = 0; // 0=Chờ xử lý, 1=Đã xuất, 2=Từ chối, 3=Hủy

    @Column(name = "GhiChu", columnDefinition = "LONGTEXT")
    private String ghiChu;

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "phieuXuatKho", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietPhieuXuat> chiTietPhieuXuats;
} 