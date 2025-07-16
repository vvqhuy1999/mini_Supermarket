package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "NhaCungCap")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhaCungCap implements Serializable {
    @Id
    @Column(name = "MaNCC", length = 10)
    private String maNCC;

    @Column(name = "TenNCC", length = 255)
    private String tenNCC;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @Column(name = "SDT", length = 15)
    private String sdt;

    @Column(name = "ThongTinHopDong", columnDefinition = "LONGTEXT")
    private String thongTinHopDong;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "nhaCungCap", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhieuNhapHang> phieuNhapHangs;
} 