package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "KhachHang", indexes = {
    @Index(name = "idx_khachhang_sdt", columnList = "SDT"),
    @Index(name = "idx_khachhang_email", columnList = "Email"),
    @Index(name = "idx_khachhang_loai", columnList = "LoaiKhachHang")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang implements Serializable {
    @Id
    @Column(name = "MaKH", length = 10)
    @NotBlank(message = "Mã khách hàng không được để trống")
    @Size(max = 10, message = "Mã khách hàng không được vượt quá 10 ký tự")
    private String maKH;

    @ManyToOne
    @JoinColumn(name = "MaNguoiDung")
    private NguoiDung nguoiDung;

    @Column(name = "HoTen", length = 255, nullable = false)
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 255, message = "Họ tên không được vượt quá 255 ký tự")
    private String hoTen;

    @Column(name = "SDT", length = 15)
    @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự")
    private String sdt;

    @Column(name = "Email", length = 100)
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;

    @Column(name = "DiaChi", length = 255)
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String diaChi;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "DiemTichLuy")
    private Integer diemTichLuy = 0;

    @Column(name = "LoaiKhachHang", length = 50)
    @Size(max = 50, message = "Loại khách hàng không được vượt quá 50 ký tự")
    private String loaiKhachHang = "Thường"; // Thường, VIP, Bạc, Vàng, Kim cương

    @Column(name = "NgayDangKy")
    private LocalDateTime ngayDangKy = LocalDateTime.now();

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons;

    @JsonIgnore
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GioHang> gioHangs;

    @JsonIgnore
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KhuyenMaiKhachHang> khuyenMaiKhachHangs;

    @PrePersist
    protected void onCreate() {
        if (ngayDangKy == null) {
            ngayDangKy = LocalDateTime.now();
        }
        if (diemTichLuy == null) {
            diemTichLuy = 0;
        }
        if (loaiKhachHang == null || loaiKhachHang.trim().isEmpty()) {
            loaiKhachHang = "Thường";
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}