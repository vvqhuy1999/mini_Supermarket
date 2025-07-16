package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "PhuongThucThanhToan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhuongThucThanhToan implements Serializable {
    @Id
    @Column(name = "MaPTTT", length = 10)
    private String maPTTT;

    @Column(name = "TenPTTT", length = 100)
    private String tenPTTT;

    @Column(name = "MoTa", columnDefinition = "LONGTEXT")
    private String moTa;

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