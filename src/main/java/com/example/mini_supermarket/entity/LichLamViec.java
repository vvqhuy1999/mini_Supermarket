package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "LichLamViec", indexes = {
    @Index(name = "idx_lichlamviec_ngay", columnList = "NgayLam"),
    @Index(name = "idx_lichlamviec_trangthai", columnList = "TrangThai")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichLamViec implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaLich")
    private Integer maLich;

    @ManyToOne
    @JoinColumn(name = "MaNV", nullable = false)
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "MaCa", nullable = false)
    private CaLamViec caLamViec;

    @Column(name = "NgayLam", nullable = false)
    private LocalDate ngayLam;

    @ManyToOne
    @JoinColumn(name = "MaNVQuanLy")
    private NhanVien nhanVienQuanLy;

    @Column(name = "TrangThai")
    private Integer trangThai = 0; // 0=Chờ duyệt, 1=Đã duyệt, 2=Từ chối, 3=Hủy, 4=Đã hoàn thành

    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;

    @Column(name = "GhiChu", columnDefinition = "LONGTEXT")
    private String ghiChu;

    @Column(name = "GioVao")
    private LocalTime gioVao; // Giờ thực tế vào làm

    @Column(name = "GioRa")
    private LocalTime gioRa; // Giờ thực tế ra về

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 