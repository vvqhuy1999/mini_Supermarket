package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "CuaHang", indexes = {
    @Index(name = "idx_cuahang_trangthai", columnList = "TrangThai")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuaHang implements Serializable {
    @Id
    @Column(name = "MaCH", length = 10)
    @Size(max = 10, message = "Mã cửa hàng không được vượt quá 10 ký tự")
    private String maCH;

    @Column(name = "TenCH", length = 255, nullable = false)
    @NotBlank(message = "Tên cửa hàng không được để trống")
    @Size(max = 255, message = "Tên cửa hàng không được vượt quá 255 ký tự")
    private String tenCH;

    @Column(name = "DiaChi", length = 255)
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String diaChi;

    @Column(name = "SDT", length = 15)
    @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự")
    private String sdt;

    @Column(name = "NgayThanhLap")
    private LocalDate ngayThanhLap;

    @Column(name = "TrangThai")
    private Integer trangThai = 1; // 0=Đóng cửa, 1=Hoạt động

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "cuaHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NhanVien> nhanViens;

    @JsonIgnore
    @OneToMany(mappedBy = "cuaHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Kho> khos;

    @JsonIgnore
    @OneToMany(mappedBy = "cuaHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ThongKeBaoCao> thongKeBaoCaos;

    @PrePersist
    protected void onCreate() {
        if (ngayThanhLap == null) {
            ngayThanhLap = LocalDate.now();
        }
        if (trangThai == null) {
            trangThai = 1;
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}