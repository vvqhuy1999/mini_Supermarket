package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "CaLamViec")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaLamViec implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaCa")
    private Integer maCa;

    @Column(name = "TenCa", length = 100)
    private String tenCa;

    @Column(name = "GioBatDau")
    private LocalTime gioBatDau;

    @Column(name = "GioKetThuc")
    private LocalTime gioKetThuc;

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ OneToMany
    @JsonIgnore
    @OneToMany(mappedBy = "caLamViec", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LichLamViec> lichLamViecs;
} 