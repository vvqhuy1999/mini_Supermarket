package com.example.mini_supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "BaoCaoDoanhThu_HangNgay", indexes = {
    @Index(name = "idx_baocao_ngay_ban", columnList = "ngay_ban"),
    @Index(name = "idx_baocao_masp", columnList = "masp"),
    @Index(name = "idx_baocao_mach", columnList = "mach")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaoCaoDoanhThuHangNgay implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "ngay_ban", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate ngayBan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mach")
    @JsonIgnore
    private CuaHang cuaHang;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "masp", nullable = false)
    @JsonIgnore
    private SanPham sanPham;
    
    @Column(name = "soluong_ban", nullable = false)
    private Integer soLuongBan;
    
    @Column(name = "doanhthu", nullable = false, precision = 15, scale = 2)
    private BigDecimal doanhThu;
    
    @Column(name = "von_trungbinh", precision = 15, scale = 2)
    private BigDecimal vonTrungBinh;
    
    @Column(name = "loinhuan", precision = 15, scale = 2, insertable = false, updatable = false)
    private BigDecimal loiNhuan;
    
    // ===== GETTERS/SETTERS CHO CÁC TRƯỜNG QUAN HỆ =====
    
    public String getMaCH() {
        return cuaHang != null ? cuaHang.getMaCH() : null;
    }
    
    public void setMaCH(String maCH) {
        if (this.cuaHang == null) {
            this.cuaHang = new CuaHang();
        }
        this.cuaHang.setMaCH(maCH);
    }
    
    public String getMaSP() {
        return sanPham != null ? sanPham.getMaSP() : null;
    }
    
    public void setMaSP(String maSP) {
        if (this.sanPham == null) {
            this.sanPham = new SanPham();
        }
        this.sanPham.setMaSP(maSP);
    }
    
    // ===== METHODS TIỆN ÍCH =====
    
    /**
     * Tính toán lợi nhuận thủ công (nếu cần)
     */
    public BigDecimal tinhLoiNhuan() {
        if (doanhThu != null && vonTrungBinh != null) {
            return doanhThu.subtract(vonTrungBinh);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Kiểm tra xem báo cáo có lãi hay không
     */
    public boolean isCoLai() {
        return loiNhuan != null && loiNhuan.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Tính tỷ lệ lợi nhuận (%)
     */
    public BigDecimal tinhTyLeLoiNhuan() {
        if (doanhThu != null && doanhThu.compareTo(BigDecimal.ZERO) > 0 && vonTrungBinh != null) {
            return loiNhuan.divide(doanhThu, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public String toString() {
        return "BaoCaoDoanhThuHangNgay{" +
                "id=" + id +
                ", ngayBan=" + ngayBan +
                ", maCH='" + getMaCH() + '\'' +
                ", maSP='" + getMaSP() + '\'' +
                ", soLuongBan=" + soLuongBan +
                ", doanhThu=" + doanhThu +
                ", vonTrungBinh=" + vonTrungBinh +
                ", loiNhuan=" + loiNhuan +
                '}';
    }
}
