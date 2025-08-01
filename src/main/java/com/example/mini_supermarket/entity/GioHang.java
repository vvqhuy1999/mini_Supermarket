package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "GioHang", indexes = {
    @Index(name = "idx_giohang_khachhang", columnList = "MaKH"),
    @Index(name = "idx_giohang_trangthai", columnList = "TrangThai")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GioHang implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaGH")
    private Integer maGH;

    @ManyToOne
    @JoinColumn(name = "MaKH")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "MaNV")
    private NhanVien nhanVien;

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "NgayCapNhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "TrangThai")
    private Integer trangThai = 0; // 0=Đang chọn hàng, 1=Đã đặt hàng, 2=Đã thanh toán, 3=Hủy

    @Column(name = "GhiChu", columnDefinition = "LONGTEXT")
    private String ghiChu;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "gioHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietGioHang> chiTietGioHangs;
} 