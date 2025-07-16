package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "TonKhoChiTiet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TonKhoChiTiet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTKCT")
    private Integer maTKCT;

    @ManyToOne
    @JoinColumn(name = "MaSP")
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "MaKho")
    private Kho kho;

    @Column(name = "SoLuongTon")
    private Integer soLuongTon;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
} 