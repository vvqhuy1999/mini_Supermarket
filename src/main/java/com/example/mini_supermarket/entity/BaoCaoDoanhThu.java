package com.example.mini_supermarket.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "BaoCaoDoanhThu", indexes = {
        @Index(name = "idx_baocaodoanhthu_ngay", columnList = "NgayBaoCao"),
        @Index(name = "idx_baocaodoanhthu_loai", columnList = "LoaiBaoCao"),
        @Index(name = "idx_baocaodoanhthu_khoangthoi", columnList = "TuNgay, DenNgay")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaoCaoDoanhThu implements Serializable {
    
    @Id
    @Column(name = "MaBaoCao", length = 20)
    private String maBaoCao; // Changed to String for consistency with junction tables

    @Column(name = "LoaiBaoCao", length = 50, nullable = false)
    private String loaiBaoCao; // NGAY, TUAN, THANG, NAM

    @Column(name = "TenBaoCao", length = 255, nullable = false)
    private String tenBaoCao;

    @Column(name = "NgayBaoCao", nullable = false)
    private LocalDate ngayBaoCao;

    @Column(name = "TuNgay", nullable = false)
    private LocalDate tuNgay;

    @Column(name = "DenNgay", nullable = false)
    private LocalDate denNgay;

    // ===================================
    // THÔNG TIN TỔNG QUAN
    // ===================================
    
    @Column(name = "TongDoanhThu", precision = 18, scale = 2)
    private BigDecimal tongDoanhThu = BigDecimal.ZERO;

    @Column(name = "TongChiPhi", precision = 18, scale = 2)
    private BigDecimal tongChiPhi = BigDecimal.ZERO;

    @Column(name = "TongSoHoaDon")
    private Integer tongSoHoaDon = 0;

    @Column(name = "SoLuongHoaDon")
    private Integer soLuongHoaDon = 0;

    @Column(name = "TongSoSanPham")
    private Integer tongSoSanPham = 0;

    @Column(name = "TongSoKhachHang")
    private Integer tongSoKhachHang = 0;

    @Column(name = "DoanhThuTrungBinh", precision = 18, scale = 2)
    private BigDecimal doanhThuTrungBinh = BigDecimal.ZERO;

    @Column(name = "HoaDonTrungBinh", precision = 18, scale = 2)
    private BigDecimal hoaDonTrungBinh = BigDecimal.ZERO;

    // Các tỷ lệ tăng trưởng
    @Column(name = "TyLeTangTruongDoanhThu", precision = 5, scale = 2)
    private BigDecimal tyLeTangTruongDoanhThu = BigDecimal.ZERO;

    @Column(name = "TyLeTangTruongHoaDon", precision = 5, scale = 2)
    private BigDecimal tyLeTangTruongHoaDon = BigDecimal.ZERO;

    @Column(name = "TyLeTangTruongKhachHang", precision = 5, scale = 2)
    private BigDecimal tyLeTangTruongKhachHang = BigDecimal.ZERO;

    // ===================================
    // CHI TIẾT DƯỚI DẠNG JSON
    // ===================================
    
    @Column(name = "TopSanPhamBanChay", columnDefinition = "JSON")
    private String topSanPhamBanChay;
    // JSON: [{"maSP": "SP001", "tenSP": "...", "soLuongBan": 100, "doanhThu": 1000000, "tyLeDongGop": 15.5, "tangTruong": 10.5, "thuTuXepHang": 1, "giaTriTrungBinh": 50000}]

    @Column(name = "TopKhachHangTiemNang", columnDefinition = "JSON")
    private String topKhachHangTiemNang;
    // JSON: [{"maKH": "KH001", "tenKH": "...", "soHoaDon": 10, "tongChiTieu": 5000000, "tyLeDongGop": 25.0, "tangTruong": 15.2, "thuTuXepHang": 1, "giaTriTrungBinh": 500000}]

    @Column(name = "ThongKeLoaiSanPham", columnDefinition = "JSON")
    private String thongKeLoaiSanPham;
    // JSON: [{"maLoaiSP": "LSP001", "tenLoaiSP": "...", "soLuongBan": 500, "doanhThu": 10000000, "tyLeDongGop": 30.0, "tangTruong": 8.5, "thuTuXepHang": 1}]

    @Column(name = "PhanTichTangTruong", columnDefinition = "JSON")
    private String phanTichTangTruong;
    // JSON: {"doanhThuKyTruoc": 8000000, "tangTruongSoLuong": 15.5, "sanPhamTangTruongNhanh": [...], "khachHangTangTruongManh": [...]}

    @Column(name = "ChiTietSanPham", columnDefinition = "JSON")
    private String chiTietSanPham;
    // JSON: [{"maSP": "SP001", "tenSP": "...", "loaiChiTiet": "SAN_PHAM", "soLuongBan": 100, "doanhThu": 1000000, "soHoaDon": 50, "tangTruong": 10.5, "thuTuXepHang": 1, "tyLeDongGop": 15.5, "giaTriTrungBinh": 50000, "thongTinBosung": "..."}]

    @Column(name = "ChiTietKhachHang", columnDefinition = "JSON")
    private String chiTietKhachHang;
    // JSON: [{"maKH": "KH001", "tenKH": "...", "loaiChiTiet": "KHACH_HANG", "soHoaDon": 10, "doanhThu": 5000000, "tangTruong": 15.2, "thuTuXepHang": 1, "tyLeDongGop": 25.0, "giaTriTrungBinh": 500000, "thongTinBosung": "..."}]

    @Column(name = "ChiTietLoaiSanPham", columnDefinition = "JSON")
    private String chiTietLoaiSanPham;
    // JSON: [{"maLoaiSP": "LSP001", "tenLoaiSP": "...", "loaiChiTiet": "LOAI_SAN_PHAM", "soLuongBan": 500, "doanhThu": 10000000, "tangTruong": 8.5, "thuTuXepHang": 1, "tyLeDongGop": 30.0, "thongTinBosung": "..."}]

    // ===================================
    // THÔNG TIN BỔ SUNG
    // ===================================
    
    @Column(name = "GhiChu", columnDefinition = "LONGTEXT")
    private String ghiChu;

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "NgayCapNhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "TrangThai")
    private Integer trangThai = 1; // 0=Nháp, 1=Hoàn thành

    @Column(name = "IsDeleted")
    private Boolean isDeleted = false;

    // Quan hệ ManyToOne với NhanVien (người tạo báo cáo) - Consolidated single relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNVTao")
    private NhanVien nhanVienTao;

    // Quan hệ ManyToOne với CuaHang (cửa hàng)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaCH")
    private CuaHang cuaHang;

    // ===================================
    // RELATIONSHIPS
    // ===================================
    
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "BaoCaoDoanhThu_HoaDon",
        joinColumns = @JoinColumn(name = "MaBaoCao"),
        inverseJoinColumns = @JoinColumn(name = "MaHD"),
        indexes = {
            @Index(name = "idx_baocao_hoadon_baocao", columnList = "MaBaoCao"),
            @Index(name = "idx_baocao_hoadon_hoadon", columnList = "MaHD")
        }
    )
    private List<HoaDon> hoaDons;

    // Quan hệ ManyToMany với PhieuNhapHang (để tính chi phí)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "BaoCaoDoanhThu_PhieuNhap",
        joinColumns = @JoinColumn(name = "MaBaoCao"),
        inverseJoinColumns = @JoinColumn(name = "MaPN"),
        indexes = {
            @Index(name = "idx_baocao_phieunhap_baocao", columnList = "MaBaoCao"),
            @Index(name = "idx_baocao_phieunhap_phieunhap", columnList = "MaPN")
        }
    )
    private List<PhieuNhapHang> phieuNhapHangs;

    // ===================================
    // LIFECYCLE CALLBACKS
    // ===================================
    
    @PreUpdate
    public void preUpdate() {
        this.ngayCapNhat = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
        if (this.ngayBaoCao == null) {
            this.ngayBaoCao = LocalDate.now();
        }
    }

    // ===================================
    // HELPER METHODS
    // ===================================
    
    public boolean isValid() {
        return this.tuNgay != null && 
               this.denNgay != null && 
               !this.tuNgay.isAfter(this.denNgay) &&
               this.loaiBaoCao != null && 
               !this.loaiBaoCao.trim().isEmpty();
    }

    // Business logic methods
    public BigDecimal tinhLoiNhuan() {
        BigDecimal doanhThu = this.tongDoanhThu != null ? this.tongDoanhThu : BigDecimal.ZERO;
        BigDecimal chiPhi = this.tongChiPhi != null ? this.tongChiPhi : BigDecimal.ZERO;
        return doanhThu.subtract(chiPhi);
    }

    public void capNhatThongKeTuHoaDon() {
        if (this.hoaDons != null && !this.hoaDons.isEmpty()) {
            // Filter valid invoices within report date range
            var validHoaDons = this.hoaDons.stream()
                .filter(hd -> hd != null)
                .filter(hd -> !Boolean.TRUE.equals(hd.getIsDeleted()))
                .filter(hd -> hd.getTrangThai() != null && hd.getTrangThai() == 1) // Only completed invoices
                .filter(hd -> isWithinDateRange(hd.getNgayLap()))
                .toList();
            
            BigDecimal tongDoanhThuMoi = validHoaDons.stream()
                .map(hd -> hd.getTongTien() != null ? hd.getTongTien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            this.tongDoanhThu = tongDoanhThuMoi;
            this.soLuongHoaDon = validHoaDons.size();
            this.tongSoHoaDon = validHoaDons.size(); // Update total count as well
            
            // Calculate averages with null safety
            if (this.soLuongHoaDon > 0 && this.tongDoanhThu != null && this.tongDoanhThu.compareTo(BigDecimal.ZERO) > 0) {
                this.hoaDonTrungBinh = this.tongDoanhThu.divide(
                    BigDecimal.valueOf(this.soLuongHoaDon), 2, java.math.RoundingMode.HALF_UP);
            } else {
                this.hoaDonTrungBinh = BigDecimal.ZERO;
            }
            
            long soNgay = getSoNgayBaoCao();
            if (soNgay > 0 && this.tongDoanhThu != null && this.tongDoanhThu.compareTo(BigDecimal.ZERO) > 0) {
                this.doanhThuTrungBinh = this.tongDoanhThu.divide(
                    BigDecimal.valueOf(soNgay), 2, java.math.RoundingMode.HALF_UP);
            } else {
                this.doanhThuTrungBinh = BigDecimal.ZERO;
            }
        } else {
            // Reset values when no invoices
            this.tongDoanhThu = BigDecimal.ZERO;
            this.soLuongHoaDon = 0;
            this.tongSoHoaDon = 0;
            this.hoaDonTrungBinh = BigDecimal.ZERO;
            this.doanhThuTrungBinh = BigDecimal.ZERO;
        }
    }
    
    private boolean isWithinDateRange(java.time.LocalDateTime dateTime) {
        if (dateTime == null || this.tuNgay == null || this.denNgay == null) {
            return false;
        }
        LocalDate date = dateTime.toLocalDate();
        return !date.isBefore(this.tuNgay) && !date.isAfter(this.denNgay);
    }

    public void capNhatChiPhiTuPhieuNhap() {
        if (this.phieuNhapHangs != null && !this.phieuNhapHangs.isEmpty()) {
            // Filter valid import receipts within report date range
            var validPhieuNhaps = this.phieuNhapHangs.stream()
                .filter(pn -> pn != null)
                .filter(pn -> !Boolean.TRUE.equals(pn.getIsDeleted()))
                .filter(pn -> pn.getTrangThai() != null && pn.getTrangThai() == 1) // Only completed imports
                .filter(pn -> isWithinDateRange(pn.getNgayNhap()))
                .toList();
            
            BigDecimal tongChiPhiMoi = validPhieuNhaps.stream()
                .map(pn -> pn.getTongTienNhap() != null ? pn.getTongTienNhap() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            this.tongChiPhi = tongChiPhiMoi;
        } else {
            // Reset cost when no import receipts
            this.tongChiPhi = BigDecimal.ZERO;
        }
    }

    public String getTenHienThi() {
        if (this.tenBaoCao != null && !this.tenBaoCao.trim().isEmpty()) {
            return this.tenBaoCao;
        }
        
        return String.format("Báo cáo %s (%s - %s) - Toàn hệ thống", 
                            this.loaiBaoCao,
                            this.tuNgay.toString(),
                            this.denNgay.toString());
    }

    public long getSoNgayBaoCao() {
        if (this.tuNgay != null && this.denNgay != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(this.tuNgay, this.denNgay) + 1;
        }
        return 0;
    }

    public BigDecimal getDoanhThuTrungBinhTheoNgay() {
        long soNgay = getSoNgayBaoCao();
        if (soNgay > 0 && this.tongDoanhThu != null && this.tongDoanhThu.compareTo(BigDecimal.ZERO) > 0) {
            return this.tongDoanhThu.divide(BigDecimal.valueOf(soNgay), 2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}