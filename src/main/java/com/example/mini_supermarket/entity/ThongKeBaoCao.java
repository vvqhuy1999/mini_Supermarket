package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThongKeBaoCao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeBaoCao implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaBaoCao")
    private Integer maBaoCao;

    @ManyToOne
    @JoinColumn(name = "MaCH")
    private CuaHang cuaHang;

    @ManyToOne
    @JoinColumn(name = "MaNV")
    private NhanVien nhanVien;

    @Column(name = "LoaiBaoCao", length = 100)
    private String loaiBaoCao;

    @Column(name = "SoTien", precision = 15, scale = 2)
    private BigDecimal soTien;

    @Column(name = "NgayBaoCao")
    private LocalDateTime ngayBaoCao;

    @Column(name = "NoiDung", columnDefinition = "LONGTEXT")
    private String noiDung;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 