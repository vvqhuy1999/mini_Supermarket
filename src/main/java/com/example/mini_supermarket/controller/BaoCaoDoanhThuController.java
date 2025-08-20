package com.example.mini_supermarket.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.example.mini_supermarket.dto.ThongKeKhachHangDTO;
import com.example.mini_supermarket.dto.ThongKeSanPhamDTO;
import com.example.mini_supermarket.entity.BaoCaoDoanhThu;
import com.example.mini_supermarket.service.BaoCaoDoanhThuService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/baocao-doanhthu")
@CrossOrigin(origins = "*")
public class BaoCaoDoanhThuController {

    @Autowired
    private BaoCaoDoanhThuService baoCaoDoanhThuService;

    // CRUD Operations
    @GetMapping
    public ResponseEntity<List<BaoCaoDoanhThu>> getAllBaoCao() {
        try {
            List<BaoCaoDoanhThu> baoCaoList = baoCaoDoanhThuService.findAllActive();
            return ResponseEntity.ok(baoCaoList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaoCaoDoanhThu> getBaoCaoById(@PathVariable Long id) {
        try {
            BaoCaoDoanhThu baoCao = baoCaoDoanhThuService.findActiveById(id);
            return ResponseEntity.ok(baoCao);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBaoCao(@Valid @RequestBody BaoCaoDoanhThu baoCao) {
        Map<String, Object> response = new HashMap<>();
        try {
            BaoCaoDoanhThu savedBaoCao = baoCaoDoanhThuService.save(baoCao);
            response.put("success", true);
            response.put("message", "Tạo báo cáo doanh thu thành công");
            response.put("data", savedBaoCao);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBaoCao(
            @PathVariable Long id,
            @Valid @RequestBody BaoCaoDoanhThu baoCao) {
        Map<String, Object> response = new HashMap<>();
        try {
            baoCao.setMaBaoCao(id);
            BaoCaoDoanhThu updatedBaoCao = baoCaoDoanhThuService.update(baoCao);
            response.put("success", true);
            response.put("message", "Cập nhật báo cáo thành công");
            response.put("data", updatedBaoCao);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBaoCao(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            baoCaoDoanhThuService.softDeleteById(id);
            response.put("success", true);
            response.put("message", "Xóa báo cáo thành công");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Search Operations
    @GetMapping("/search/loai/{loai}")
    public ResponseEntity<List<BaoCaoDoanhThu>> getBaoCaoByLoai(@PathVariable String loai) {
        try {
            List<BaoCaoDoanhThu> baoCaoList = baoCaoDoanhThuService.findByLoaiBaoCao(loai);
            return ResponseEntity.ok(baoCaoList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/search/date-range")
    public ResponseEntity<List<BaoCaoDoanhThu>> getBaoCaoByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        try {
            List<BaoCaoDoanhThu> baoCaoList = baoCaoDoanhThuService.findByDateRange(tuNgay, denNgay);
            return ResponseEntity.ok(baoCaoList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search/advanced")
    public ResponseEntity<List<BaoCaoDoanhThu>> getBaoCaoAdvanced(
            @RequestParam(required = false) String loai,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        try {
            List<BaoCaoDoanhThu> baoCaoList;
            
            if (loai != null) {
                baoCaoList = baoCaoDoanhThuService.findByLoaiAndDateRange(loai, tuNgay, denNgay);
            } else {
                baoCaoList = baoCaoDoanhThuService.findByDateRange(tuNgay, denNgay);
            }
            
            return ResponseEntity.ok(baoCaoList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Report Generation
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateBaoCao(
            @RequestParam String loaiBaoCao,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra báo cáo đã tồn tại
            boolean exists = baoCaoDoanhThuService.kiemTraBaoCaoTonTai(loaiBaoCao, tuNgay, denNgay);
            
            if (exists) {
                response.put("success", false);
                response.put("message", "Báo cáo đã tồn tại cho khoảng thời gian này");
                return ResponseEntity.badRequest().body(response);
            }
            
            BaoCaoDoanhThu baoCao = baoCaoDoanhThuService.taoBaoCaoDoanhThu(loaiBaoCao, tuNgay, denNgay);
            response.put("success", true);
            response.put("message", "Tạo báo cáo doanh thu thành công");
            response.put("data", baoCao);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi tạo báo cáo: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/regenerate")
    public ResponseEntity<Map<String, Object>> regenerateBaoCao(
            @RequestParam String loaiBaoCao,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        Map<String, Object> response = new HashMap<>();
        try {
            BaoCaoDoanhThu baoCao = baoCaoDoanhThuService.taoOrCapNhatBaoCao(loaiBaoCao, tuNgay, denNgay);
            response.put("success", true);
            response.put("message", "Tạo lại báo cáo doanh thu thành công");
            response.put("data", baoCao);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi tạo lại báo cáo: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Detail Operations
    @GetMapping("/{id}/chitiet")
    public ResponseEntity<Map<String, Object>> getChiTietBaoCao(@PathVariable Long id) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("sanPham", baoCaoDoanhThuService.getChiTietSanPhamFromJson(id));
            result.put("khachHang", baoCaoDoanhThuService.getChiTietKhachHangFromJson(id));
            result.put("loaiSanPham", baoCaoDoanhThuService.getChiTietLoaiSanPhamFromJson(id));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/chitiet/{loai}")
    public ResponseEntity<List<Map<String, Object>>> getChiTietBaoCaoByLoai(
            @PathVariable Long id,
            @PathVariable String loai) {
        try {
            List<Map<String, Object>> chiTietList;
            switch (loai.toUpperCase()) {
                case "SAN_PHAM":
                    chiTietList = baoCaoDoanhThuService.getChiTietSanPhamFromJson(id);
                    break;
                case "KHACH_HANG":
                    chiTietList = baoCaoDoanhThuService.getChiTietKhachHangFromJson(id);
                    break;
                case "LOAI_SAN_PHAM":
                    chiTietList = baoCaoDoanhThuService.getChiTietLoaiSanPhamFromJson(id);
                    break;
                default:
                    return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(chiTietList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/top-sanpham")
    public ResponseEntity<List<Map<String, Object>>> getTopSanPham(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> topSanPham = baoCaoDoanhThuService.getTopSanPhamFromJson(id);
            // Limit results if needed
            if (limit > 0 && topSanPham.size() > limit) {
                topSanPham = topSanPham.subList(0, limit);
            }
            return ResponseEntity.ok(topSanPham);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/top-khachhang")
    public ResponseEntity<List<Map<String, Object>>> getTopKhachHang(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> topKhachHang = baoCaoDoanhThuService.getTopKhachHangFromJson(id);
            // Limit results if needed
            if (limit > 0 && topKhachHang.size() > limit) {
                topKhachHang = topKhachHang.subList(0, limit);
            }
            return ResponseEntity.ok(topKhachHang);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/phan-tich-tang-truong")
    public ResponseEntity<Map<String, Object>> getPhanTichTangTruong(@PathVariable Long id) {
        try {
            Map<String, Object> phanTich = baoCaoDoanhThuService.getPhanTichTangTruongFromJson(id);
            return ResponseEntity.ok(phanTich);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Statistics Operations
    @GetMapping("/thongke/sanpham-banchay")
    public ResponseEntity<List<ThongKeSanPhamDTO>> getThongKeSanPhamBanChay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) String maCH,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ThongKeSanPhamDTO> thongKe = baoCaoDoanhThuService.thongKeSanPhamBanChay(tuNgay, denNgay, maCH, limit);
            return ResponseEntity.ok(thongKe);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/thongke/sanpham-tangtruong")
    public ResponseEntity<List<ThongKeSanPhamDTO>> getThongKeSanPhamTangTruong(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) String maCH,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ThongKeSanPhamDTO> thongKe = baoCaoDoanhThuService.thongKeSanPhamTangTruong(tuNgay, denNgay, maCH, limit);
            return ResponseEntity.ok(thongKe);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/thongke/khachhang-tiemnang")
    public ResponseEntity<List<ThongKeKhachHangDTO>> getThongKeKhachHangTiemNang(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) String maCH,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ThongKeKhachHangDTO> thongKe = baoCaoDoanhThuService.thongKeKhachHangTiemNang(tuNgay, denNgay, maCH, limit);
            return ResponseEntity.ok(thongKe);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/thongke/khachhang-tangtruong")
    public ResponseEntity<List<ThongKeKhachHangDTO>> getThongKeKhachHangTangTruong(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) String maCH,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ThongKeKhachHangDTO> thongKe = baoCaoDoanhThuService.thongKeKhachHangTangTruong(tuNgay, denNgay, maCH, limit);
            return ResponseEntity.ok(thongKe);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Utility Operations
    @GetMapping("/check-exists")
    public ResponseEntity<Map<String, Object>> checkBaoCaoExists(
            @RequestParam String loai,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) String maCH) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean exists = baoCaoDoanhThuService.kiemTraBaoCaoTonTai(loai, tuNgay, denNgay, maCH);
            response.put("exists", exists);
            response.put("message", exists ? "Báo cáo đã tồn tại" : "Báo cáo chưa tồn tại");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Lỗi kiểm tra báo cáo: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/previous-period")
    public ResponseEntity<BaoCaoDoanhThu> getPreviousPeriodReport(
            @RequestParam String loai,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) String maCH) {
        try {
            BaoCaoDoanhThu baoCaoKyTruoc = baoCaoDoanhThuService.layBaoCaoKyTruoc(loai, tuNgay, maCH);
            if (baoCaoKyTruoc != null) {
                return ResponseEntity.ok(baoCaoKyTruoc);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // JSON Search Operations
    @GetMapping("/search/sanpham/{maSP}")
    public ResponseEntity<List<BaoCaoDoanhThu>> findBySanPham(@PathVariable String maSP) {
        try {
            List<BaoCaoDoanhThu> results = baoCaoDoanhThuService.findByTopSanPhamContains(maSP);
            results.addAll(baoCaoDoanhThuService.findByChiTietSanPhamContains(maSP));
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search/khachhang/{maKH}")
    public ResponseEntity<List<BaoCaoDoanhThu>> findByKhachHang(@PathVariable String maKH) {
        try {
            List<BaoCaoDoanhThu> results = baoCaoDoanhThuService.findByTopKhachHangContains(maKH);
            results.addAll(baoCaoDoanhThuService.findByChiTietKhachHangContains(maKH));
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search/loaisanpham/{maLoaiSP}")
    public ResponseEntity<List<BaoCaoDoanhThu>> findByLoaiSanPham(@PathVariable String maLoaiSP) {
        try {
            List<BaoCaoDoanhThu> results = baoCaoDoanhThuService.findByLoaiSanPhamContains(maLoaiSP);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}