package com.example.mini_supermarket.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "giohang_chitiet", indexes = {
    @Index(name = "idx_giohang_chitiet_khachhang", columnList = "makh"),
    @Index(name = "idx_giohang_chitiet_sanpham", columnList = "masp"),
    @Index(name = "idx_giohang_chitiet_trangthai", columnList = "trangthai")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GioHangChiTiet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maghct")
    private Integer maGHCT;

    @ManyToOne
    @JoinColumn(name = "makh")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "manv")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "masp", nullable = false)
    private SanPham sanPham;

    @Column(name = "soluong", nullable = false)
    private Integer soLuong;

    @Column(name = "dongiahientai", precision = 15, scale = 2, nullable = false)
    private BigDecimal donGiaHienTai; // Giá sản phẩm tại thời điểm thêm vào giỏ

    @Column(name = "thanhtien", precision = 15, scale = 2, insertable = false, updatable = false)
    private BigDecimal thanhTien; // SoLuong * DonGiaHienTai (computed)

    @Column(name = "ngaythem")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private java.sql.Timestamp ngayThem;

    @Column(name = "ngaycapnhat")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private java.sql.Timestamp ngayCapNhat;

    @Column(name = "trangthai")
    private Integer trangThai = 0; // 0=Shopping,  1=Paid, 2=Canceled

}
