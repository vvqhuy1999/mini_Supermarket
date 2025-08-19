package com.example.mini_supermarket.controller;

import com.example.mini_supermarket.dto.ThongKeDoanhThuDTO;
import com.example.mini_supermarket.dto.ThongKeKhachHangTiemNangDTO;
import com.example.mini_supermarket.service.ThongKeDoanhThuTuDongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/thong-ke-tu-dong")
@CrossOrigin(origins = "*")
@Slf4j
public class ThongKeDoanhThuTuDongController {

    @Autowired
    private ThongKeDoanhThuTuDongService thongKeService;

    /**
     * Tính toán doanh thu theo tháng
     * POST /api/thong-ke-tu-dong/tinh-doanh-thu-thang
     */
    @PostMapping("/tinh-doanh-thu-thang")
    public ResponseEntity<Map<String, Object>> tinhDoanhThuThang(
            @RequestParam int thang,
            @RequestParam int nam,
            @RequestParam(required = false) String maCH) {
        try {
            thongKeService.tinhDoanhThuThang(thang, nam, maCH);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã tính toán doanh thu tháng " + thang + "/" + nam + 
                                   (maCH != null ? " cho cửa hàng " + maCH : " cho toàn hệ thống"));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Lỗi khi tính doanh thu tháng: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Tính toán khách hàng tiềm năng theo tháng
     * POST /api/thong-ke-tu-dong/tinh-khach-hang-tiem-nang
     */
    @PostMapping("/tinh-khach-hang-tiem-nang")
    public ResponseEntity<Map<String, Object>> tinhKhachHangTiemNang(
            @RequestParam int thang,
            @RequestParam int nam,
            @RequestParam(required = false) String maCH) {
        try {
            thongKeService.tinhKhachHangTiemNang(thang, nam, maCH);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã tính toán khách hàng tiềm năng tháng " + thang + "/" + nam + 
                                   (maCH != null ? " cho cửa hàng " + maCH : " cho toàn hệ thống"));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Lỗi khi tính khách hàng tiềm năng: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Tính lại toàn bộ thống kê cho một tháng
     * POST /api/thong-ke-tu-dong/tinh-lai-thong-ke-thang
     */
    @PostMapping("/tinh-lai-thong-ke-thang")
    public ResponseEntity<Map<String, Object>> tinhLaiThongKeThang(
            @RequestParam int thang,
            @RequestParam int nam) {
        try {
            thongKeService.tinhLaiThongKeThang(thang, nam);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã tính lại toàn bộ thống kê tháng " + thang + "/" + nam);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Lỗi khi tính lại thống kê tháng: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Lấy thống kê doanh thu theo tháng
     * GET /api/thong-ke-tu-dong/doanh-thu-thang
     */
    @GetMapping("/doanh-thu-thang")
    public ResponseEntity<List<ThongKeDoanhThuDTO>> layThongKeDoanhThuThang(
            @RequestParam int thang,
            @RequestParam int nam,
            @RequestParam(required = false) String maCH) {
        try {
            List<ThongKeDoanhThuDTO> result = thongKeService.layThongKeDoanhThuThang(thang, nam, maCH);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Lỗi khi lấy thống kê doanh thu tháng: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy thống kê khách hàng tiềm năng theo tháng
     * GET /api/thong-ke-tu-dong/khach-hang-tiem-nang
     */
    @GetMapping("/khach-hang-tiem-nang")
    public ResponseEntity<List<ThongKeKhachHangTiemNangDTO>> layThongKeKhachHangTiemNang(
            @RequestParam int thang,
            @RequestParam int nam,
            @RequestParam(required = false) String maCH) {
        try {
            List<ThongKeKhachHangTiemNangDTO> result = thongKeService.layThongKeKhachHangTiemNang(thang, nam, maCH);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Lỗi khi lấy thống kê khách hàng tiềm năng: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * So sánh doanh thu giữa các tháng
     * GET /api/thong-ke-tu-dong/so-sanh-doanh-thu
     */
    @GetMapping("/so-sanh-doanh-thu")
    public ResponseEntity<List<ThongKeDoanhThuDTO>> soSanhDoanhThuTheoThang(
            @RequestParam(defaultValue = "6") int soThang,
            @RequestParam(required = false) String maCH) {
        try {
            List<ThongKeDoanhThuDTO> result = thongKeService.soSanhDoanhThuTheoThang(soThang, maCH);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Lỗi khi so sánh doanh thu theo tháng: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy top khách hàng tiềm năng trong tháng
     * GET /api/thong-ke-tu-dong/top-khach-hang-tiem-nang
     */
    @GetMapping("/top-khach-hang-tiem-nang")
    public ResponseEntity<List<ThongKeKhachHangTiemNangDTO>> layTopKhachHangTiemNang(
            @RequestParam int thang,
            @RequestParam int nam,
            @RequestParam(required = false) String maCH,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ThongKeKhachHangTiemNangDTO> result = thongKeService.layTopKhachHangTiemNang(thang, nam, maCH, limit);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Lỗi khi lấy top khách hàng tiềm năng: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Tự động tính toán thống kê cho tháng hiện tại
     * POST /api/thong-ke-tu-dong/tinh-thang-hien-tai
     */
    @PostMapping("/tinh-thang-hien-tai")
    public ResponseEntity<Map<String, Object>> tinhThongKeThangHienTai(
            @RequestParam(required = false) String maCH) {
        try {
            LocalDate now = LocalDate.now();
            int thang = now.getMonthValue();
            int nam = now.getYear();
            
            thongKeService.tinhDoanhThuThang(thang, nam, maCH);
            thongKeService.tinhKhachHangTiemNang(thang, nam, maCH);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã tính toán thống kê tháng hiện tại " + thang + "/" + nam + 
                                   (maCH != null ? " cho cửa hàng " + maCH : " cho toàn hệ thống"));
            response.put("thang", thang);
            response.put("nam", nam);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Lỗi khi tính thống kê tháng hiện tại: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Tính toán thống kê cho nhiều tháng liên tiếp
     * POST /api/thong-ke-tu-dong/tinh-nhieu-thang
     */
    @PostMapping("/tinh-nhieu-thang")
    public ResponseEntity<Map<String, Object>> tinhThongKeNhieuThang(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) String maCH) {
        try {
            int thangBatDau = tuNgay.getMonthValue();
            int namBatDau = tuNgay.getYear();
            int thangKetThuc = denNgay.getMonthValue();
            int namKetThuc = denNgay.getYear();
            
            int soThangDaTinh = 0;
            
            for (int nam = namBatDau; nam <= namKetThuc; nam++) {
                int thangMin = (nam == namBatDau) ? thangBatDau : 1;
                int thangMax = (nam == namKetThuc) ? thangKetThuc : 12;
                
                for (int thang = thangMin; thang <= thangMax; thang++) {
                    thongKeService.tinhDoanhThuThang(thang, nam, maCH);
                    thongKeService.tinhKhachHangTiemNang(thang, nam, maCH);
                    soThangDaTinh++;
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã tính toán thống kê cho " + soThangDaTinh + " tháng" + 
                                   (maCH != null ? " cho cửa hàng " + maCH : " cho toàn hệ thống"));
            response.put("soThangDaTinh", soThangDaTinh);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Lỗi khi tính thống kê nhiều tháng: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}