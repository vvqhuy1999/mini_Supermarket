package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThongKeBaoCao", indexes = {
    @Index(name = "idx_thongke_loai", columnList = "LoaiBaoCao"),
    @Index(name = "idx_thongke_ngay", columnList = "NgayBaoCao"),
    @Index(name = "idx_thongke_cuahang", columnList = "MaCH")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeBaoCao implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaBaoCao")
    private Integer maBaoCao;

    @ManyToOne
    @JoinColumn(name = "MaCH")
    private CuaHang cuaHang;

    @ManyToOne
    @JoinColumn(name = "MaNV", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "LoaiBaoCao", length = 100, nullable = false)
    private String loaiBaoCao; // DoanhThu, ChiPhi, TonKho, NhanVien, KhachHang

    @Column(name = "TenBaoCao", length = 255, nullable = false)
    private String tenBaoCao;

    @Column(name = "ThoiGianTu")
    private LocalDateTime thoiGianTu;

    @Column(name = "ThoiGianDen")
    private LocalDateTime thoiGianDen;

    @Column(name = "SoTien", precision = 15, scale = 2)
    private BigDecimal soTien;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "NgayBaoCao")
    private LocalDateTime ngayBaoCao = LocalDateTime.now();

    @Column(name = "NoiDung", columnDefinition = "LONGTEXT")
    private String noiDung;

    @Column(name = "FileDinhKem", length = 500)
    private String fileDinhKem;

    @Column(name = "TrangThai")
    private Integer trangThai = 1; // 0=Nháp, 1=Hoàn thành

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 