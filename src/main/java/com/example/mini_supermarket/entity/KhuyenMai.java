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
@Table(name = "KhuyenMai", indexes = {
    @Index(name = "idx_khuyenmai_ngay", columnList = "NgayBatDau, NgayKetThuc"),
    @Index(name = "idx_khuyenmai_trangthai", columnList = "TrangThai")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai implements Serializable {
    @Id
    @Column(name = "MaKM", length = 10)
    private String maKM;

    @Column(name = "TenChuongTrinh", length = 255, nullable = false)
    private String tenChuongTrinh;

    @Column(name = "MoTa", columnDefinition = "LONGTEXT")
    private String moTa;

    @Column(name = "LoaiKM", length = 50, nullable = false)
    private String loaiKM; // PhầnTrăm, SốTiền, Điểm, MuaXTangY

    @Column(name = "GiaTriKM", precision = 15, scale = 2, nullable = false)
    private BigDecimal giaTriKM;

    @Column(name = "DieuKienApDung", columnDefinition = "LONGTEXT")
    private String dieuKienApDung; // Điều kiện để áp dụng khuyến mãi

    @Column(name = "NgayBatDau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "NgayKetThuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Column(name = "SoLuongToiDa")
    private Integer soLuongToiDa; // Số lượng tối đa có thể áp dụng

    @Column(name = "DaSuDung")
    private Integer daSuDung = 0; // Số lượng đã sử dụng

    @ManyToOne
    @JoinColumn(name = "MaQuanLy")
    private NhanVien quanLy;

    @Column(name = "TrangThai")
    private Integer trangThai = 1; // 0=Tạm dừng, 1=Đang áp dụng

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "khuyenMai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons;

    @JsonIgnore
    @OneToMany(mappedBy = "khuyenMai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KhuyenMaiSanPham> khuyenMaiSanPhams;

    @JsonIgnore
    @OneToMany(mappedBy = "khuyenMai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KhuyenMaiKhachHang> khuyenMaiKhachHangs;
} 