package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "LichLamViec")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichLamViec implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaLich")
    private Integer maLich;

    @ManyToOne
    @JoinColumn(name = "MaNV")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "MaCa")
    private CaLamViec caLamViec;

    @Column(name = "NgayLam")
    private LocalDate ngayLam;

    @ManyToOne
    @JoinColumn(name = "MaNVQuanLy")
    private NhanVien nhanVienQuanLy;

    @Column(name = "TrangThai")
    private Integer trangThai; // 0=Chờ duyệt, 1=Đã duyệt, 2=Từ chối, 3=Hủy

    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;

    @Column(name = "GhiChu", columnDefinition = "LONGTEXT")
    private String ghiChu;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 