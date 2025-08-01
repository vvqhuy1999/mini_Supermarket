package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThanhToan", indexes = {
    @Index(name = "idx_thanhtoan_hoadon", columnList = "MaHD"),
    @Index(name = "idx_thanhtoan_trangthai", columnList = "TrangThaiTT"),
    @Index(name = "idx_thanhtoan_ngay", columnList = "NgayGioTT")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToan implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTT")
    private Integer maTT;

    @ManyToOne
    @JoinColumn(name = "MaHD", nullable = false)
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "MaPTTT", nullable = false)
    private PhuongThucThanhToan phuongThucThanhToan;

    @Column(name = "SoTienThanhToan", precision = 15, scale = 2, nullable = false)
    private BigDecimal soTienThanhToan;

    @Column(name = "NgayGioTT", nullable = false)
    private LocalDateTime ngayGioTT;

    @Column(name = "TrangThaiTT")
    private Integer trangThaiTT = 0; // 0=Chờ xử lý, 1=Thành công, 2=Thất bại, 3=Hủy, 4=Hoàn tiền

    @Column(name = "MaGiaoDichNganHang", length = 100)
    private String maGiaoDichNganHang; // Mã giao dịch từ ngân hàng

    @Column(name = "GhiChu", columnDefinition = "LONGTEXT")
    private String ghiChu;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 