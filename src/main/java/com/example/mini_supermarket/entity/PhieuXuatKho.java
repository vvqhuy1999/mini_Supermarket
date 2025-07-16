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
@Table(name = "PhieuXuatKho")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuXuatKho implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaPXK")
    private Integer maPXK;

    @ManyToOne
    @JoinColumn(name = "MaKho")
    private Kho kho;

    @ManyToOne
    @JoinColumn(name = "MaNVLap")
    private NhanVien nhanVienLap;

    @Column(name = "NgayXuat")
    private LocalDateTime ngayXuat;

    @Column(name = "TongSoLuong", precision = 15, scale = 2)
    private BigDecimal tongSoLuong;

    @Column(name = "LyDoXuat", length = 255)
    private String lyDoXuat;

    @Column(name = "TrangThai")
    private Integer trangThai; // 0=Chờ xử lý, 1=Đã xuất, 2=Từ chối, 3=Hủy

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "phieuXuatKho", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietPhieuXuat> chiTietPhieuXuats;
} 