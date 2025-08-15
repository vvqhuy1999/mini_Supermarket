package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "donhang", indexes = {
    @Index(name = "idx_donhang_khachhang", columnList = "makh"),
    @Index(name = "idx_donhang_nhanvien", columnList = "manv"),
    @Index(name = "idx_donhang_ngaydathang", columnList = "ngaydathang")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonHang implements Serializable {
    @Id
    @Column(name = "madh", length = 50)
    private String maDH;

    @ManyToOne
    @JoinColumn(name = "makh", nullable = false)
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "manv")
    private NhanVien nhanVien;

    @Column(name = "ngaydathang", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.sql.Timestamp ngayDatHang;

    @Column(name = "ngaygiaohang")
    @Temporal(TemporalType.TIMESTAMP)
    private java.sql.Timestamp ngayGiaoHang;

    @Column(name = "diachigiaohang", length = 255, nullable = false)
    private String diaChiGiaoHang;

    @Column(name = "trangthai", length = 50, nullable = false)
    private String trangThai; // Pending, Shipping, Completed, Canceled

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietDonHang> chiTietDonHangs;
}
