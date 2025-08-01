package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "PhuongThucThanhToan", indexes = {
    @Index(name = "idx_phuongthucthanhtoan_trangthai", columnList = "TrangThai")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhuongThucThanhToan implements Serializable {
    @Id
    @Column(name = "MaPTTT", length = 10)
    private String maPTTT;

    @Column(name = "TenPTTT", length = 100, nullable = false)
    private String tenPTTT;

    @Column(name = "MoTa", columnDefinition = "LONGTEXT")
    private String moTa;

    @Column(name = "PhiGiaoDich", precision = 10, scale = 4)
    private BigDecimal phiGiaoDich = BigDecimal.ZERO; // Phí giao dịch (%)

    @Column(name = "TrangThai")
    private Integer trangThai = 1; // 0=Ngừng sử dụng, 1=Đang sử dụng

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "phuongThucThanhToan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons;

    @JsonIgnore
    @OneToMany(mappedBy = "phuongThucThanhToan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ThanhToan> thanhToans;
} 