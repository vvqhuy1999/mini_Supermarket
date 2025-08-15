package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Entity
@Table(name = "TonKhoChiTiet", indexes = {
    @Index(name = "idx_tonkho_sanpham", columnList = "MaSP"),
    @Index(name = "idx_tonkho_kho", columnList = "MaKho")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TonKhoChiTiet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTKCT")
    private Integer maTKCT;

    @ManyToOne
    @JoinColumn(name = "MaSP", nullable = false)
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "MaKho", nullable = false)
    private Kho kho;

    @Column(name = "SoLuongTon")
    private Integer soLuongTon = 0;

    @Column(name = "SoLuongToiThieu")
    private Integer soLuongToiThieu = 0; // Mức tồn kho tối thiểu

    @Column(name = "SoLuongToiDa")
    private Integer soLuongToiDa; // Mức tồn kho tối đa

    @Column(name = "NgayCapNhat")
    @Temporal(TemporalType.TIMESTAMP)
    private java.sql.Timestamp ngayCapNhat;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 