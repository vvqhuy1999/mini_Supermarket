package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "CaLamViec", indexes = {
    @Index(name = "idx_calamviec_trangthai", columnList = "TrangThai")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaLamViec implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaCa")
    private Integer maCa;

    @Column(name = "TenCa", length = 100, nullable = false)
    private String tenCa;

    @Column(name = "GioBatDau", nullable = false)
    private LocalTime gioBatDau;

    @Column(name = "GioKetThuc", nullable = false)
    private LocalTime gioKetThuc;

    @Column(name = "SoGioLam", precision = 4, scale = 2)
    private BigDecimal soGioLam; // Số giờ làm việc trong ca (computed)

    @Column(name = "TrangThai")
    private Integer trangThai = 1; // 0=Ngừng sử dụng, 1=Đang sử dụng

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;
    
    // Manual setter for isDeleted
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    // Manual getter for isDeleted
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "caLamViec", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LichLamViec> lichLamViecs;
} 