package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "chitietdonhang", indexes = {
    @Index(name = "idx_chitietdonhang_donhang", columnList = "madh"),
    @Index(name = "idx_chitietdonhang_sanpham", columnList = "masp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDonHang implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "macthd")
    private Integer maCTHD;

    @ManyToOne
    @JoinColumn(name = "madh", nullable = false)
    private DonHang donHang;

    @ManyToOne
    @JoinColumn(name = "masp", nullable = false)
    private SanPham sanPham;

    @Column(name = "soluong", nullable = false)
    private Integer soLuong;

    @Column(name = "dongia", precision = 15, scale = 2, nullable = false)
    private BigDecimal donGia;

    @Column(name = "giamgia", precision = 5, scale = 2)
    private BigDecimal giamGia = BigDecimal.ZERO;
    
    // Field để đánh dấu soft delete
    @Column(name = "isdeleted", nullable = false)
    private Boolean isdeleted = false;
}
