package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThanhToan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToan implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTT")
    private Integer maTT;

    @ManyToOne
    @JoinColumn(name = "MaHD")
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "MaPTTT")
    private PhuongThucThanhToan phuongThucThanhToan;

    @Column(name = "SoTienThanhToan", precision = 15, scale = 2)
    private BigDecimal soTienThanhToan;

    @Column(name = "NgayGioTT")
    private LocalDateTime ngayGioTT;

    @Column(name = "TrangThaiTT")
    private Integer trangThaiTT; // 0=Chờ xử lý, 1=Thành công, 2=Thất bại, 3=Hủy

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 