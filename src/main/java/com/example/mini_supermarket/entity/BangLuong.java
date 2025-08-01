package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "BangLuong", indexes = {
    @Index(name = "idx_bangluong_thangnam", columnList = "ThangLuong, NamLuong"),
    @Index(name = "idx_bangluong_trangthai", columnList = "TrangThai")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BangLuong implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaLuong")
    private Integer maLuong;

    @ManyToOne
    @JoinColumn(name = "MaNV", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "ThangLuong", nullable = false)
    private Integer thangLuong; // 1-12

    @Column(name = "NamLuong", nullable = false)
    private Integer namLuong; // VD: 2024

    @Column(name = "LuongCoBan", precision = 15, scale = 2, nullable = false)
    private BigDecimal luongCoBan;

    @Column(name = "PhuCap", precision = 15, scale = 2)
    private BigDecimal phuCap = BigDecimal.ZERO;

    @Column(name = "Thuong", precision = 15, scale = 2)
    private BigDecimal thuong = BigDecimal.ZERO;

    @Column(name = "KhauTru", precision = 15, scale = 2)
    private BigDecimal khauTru = BigDecimal.ZERO;

    @Column(name = "TongLuong", precision = 15, scale = 2)
    private BigDecimal tongLuong;

    @Column(name = "SoNgayLam")
    private Integer soNgayLam = 0;

    @Column(name = "SoGioLam", precision = 8, scale = 2)
    private BigDecimal soGioLam = BigDecimal.ZERO;

    @Column(name = "GhiChu", columnDefinition = "LONGTEXT")
    private String ghiChu;

    @Column(name = "TrangThai")
    private Integer trangThai = 0; // 0=Chưa thanh toán, 1=Đã thanh toán

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "NgayThanhToan")
    private LocalDateTime ngayThanhToan;

    @ManyToOne
    @JoinColumn(name = "NguoiThanhToan")
    private NhanVien nguoiThanhToan;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 