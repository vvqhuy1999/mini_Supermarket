package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.dto.ThongKeDoanhThuDTO;
import com.example.mini_supermarket.dto.ThongKeKhachHangTiemNangDTO;
import com.example.mini_supermarket.entity.HoaDon;
import com.example.mini_supermarket.entity.NhanVien;
import com.example.mini_supermarket.repository.HoaDonRepository;
import com.example.mini_supermarket.repository.ThongKeBaoCaoRepository;
import com.example.mini_supermarket.service.ThongKeDoanhThuTuDongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class ThongKeDoanhThuTuDongServiceImpl implements ThongKeDoanhThuTuDongService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ThongKeBaoCaoRepository thongKeBaoCaoRepository;
    
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Override
    public void tinhDoanhThuThang(int thang, int nam, String maCH) {
        try {
            log.info("Bắt đầu tính doanh thu tháng {}/{} cho cửa hàng: {}", thang, nam, maCH);
            
            String sql = "SELECT sp_tinh_doanh_thu_thang(?, ?, ?)";
            jdbcTemplate.update(sql, thang, nam, maCH);
            
            log.info("Hoàn thành tính doanh thu tháng {}/{} cho cửa hàng: {}", thang, nam, maCH);
        } catch (Exception e) {
            log.error("Lỗi khi tính doanh thu tháng {}/{} cho cửa hàng {}: {}", thang, nam, maCH, e.getMessage());
            throw new RuntimeException("Không thể tính toán doanh thu: " + e.getMessage(), e);
        }
    }

    @Override
    public void tinhKhachHangTiemNang(int thang, int nam, String maCH) {
        try {
            log.info("Bắt đầu tính khách hàng tiềm năng tháng {}/{} cho cửa hàng: {}", thang, nam, maCH);
            
            String sql = "SELECT sp_tinh_khach_hang_tiem_nang(?, ?, ?)";
            jdbcTemplate.update(sql, thang, nam, maCH);
            
            log.info("Hoàn thành tính khách hàng tiềm năng tháng {}/{} cho cửa hàng: {}", thang, nam, maCH);
        } catch (Exception e) {
            log.error("Lỗi khi tính khách hàng tiềm năng tháng {}/{} cho cửa hàng {}: {}", thang, nam, maCH, e.getMessage());
            throw new RuntimeException("Không thể tính toán khách hàng tiềm năng: " + e.getMessage(), e);
        }
    }

    @Override
    public void tinhLaiThongKeThang(int thang, int nam) {
        try {
            log.info("Bắt đầu tính lại toàn bộ thống kê tháng {}/{}", thang, nam);
            
            String sql = "SELECT sp_tinh_lai_thong_ke_thang(?, ?)";
            jdbcTemplate.update(sql, thang, nam);
            
            log.info("Hoàn thành tính lại toàn bộ thống kê tháng {}/{}", thang, nam);
        } catch (Exception e) {
            log.error("Lỗi khi tính lại thống kê tháng {}/{}: {}", thang, nam, e.getMessage());
            throw new RuntimeException("Không thể tính lại thống kê: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ThongKeDoanhThuDTO> layThongKeDoanhThuThang(int thang, int nam, String maCH) {
        try {
            String sql = """
                SELECT 
                    t.tenbaocao,
                    t.sotien as doanh_thu,
                    t.soluong as so_hoa_don,
                    t.thoigiantu,
                    t.thoigianden,
                    t.noidung,
                    COALESCE(c.tench, 'Tổng hệ thống') as ten_cua_hang
                FROM thongkebaocao t
                LEFT JOIN cuahang c ON t.mach = c.mach
                WHERE t.loaibaocao = 'DOANH_THU_THANG'
                  AND EXTRACT(MONTH FROM t.thoigiantu) = ?
                  AND EXTRACT(YEAR FROM t.thoigiantu) = ?
                  AND (? IS NULL OR t.mach = ? OR (? IS NULL AND t.mach IS NULL))
                  AND t.isdeleted = FALSE
                ORDER BY t.thoigiantu DESC
            """;
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, thang, nam, maCH, maCH, maCH);
            List<ThongKeDoanhThuDTO> dtoList = new ArrayList<>();
            
            for (Map<String, Object> row : results) {
                ThongKeDoanhThuDTO dto = new ThongKeDoanhThuDTO(
                    (String) row.get("tenbaocao"),
                    (BigDecimal) row.get("doanh_thu"),
                    (Integer) row.get("so_hoa_don"),
                    ((java.sql.Timestamp) row.get("thoigiantu")).toLocalDateTime(),
                    ((java.sql.Timestamp) row.get("thoigianden")).toLocalDateTime(),
                    (String) row.get("noidung"),
                    (String) row.get("ten_cua_hang")
                );
                dtoList.add(dto);
            }
            
            return dtoList;
        } catch (Exception e) {
            log.error("Lỗi khi lấy thống kê doanh thu tháng {}/{}: {}", thang, nam, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<ThongKeKhachHangTiemNangDTO> layThongKeKhachHangTiemNang(int thang, int nam, String maCH) {
        try {
            String sql = """
                SELECT 
                    t.tenbaocao,
                    t.soluong as so_khach_hang_tiem_nang,
                    t.sotien as tong_chi_tieu,
                    t.thoigiantu,
                    t.thoigianden,
                    t.noidung,
                    COALESCE(c.tench, 'Tổng hệ thống') as ten_cua_hang
                FROM thongkebaocao t
                LEFT JOIN cuahang c ON t.mach = c.mach
                WHERE t.loaibaocao = 'KHACH_HANG_TIEM_NANG'
                  AND EXTRACT(MONTH FROM t.thoigiantu) = ?
                  AND EXTRACT(YEAR FROM t.thoigiantu) = ?
                  AND (? IS NULL OR t.mach = ? OR (? IS NULL AND t.mach IS NULL))
                  AND t.isdeleted = FALSE
                ORDER BY t.thoigiantu DESC
            """;
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, thang, nam, maCH, maCH, maCH);
            List<ThongKeKhachHangTiemNangDTO> dtoList = new ArrayList<>();
            
            for (Map<String, Object> row : results) {
                ThongKeKhachHangTiemNangDTO dto = new ThongKeKhachHangTiemNangDTO(
                    (String) row.get("tenbaocao"),
                    (Integer) row.get("so_khach_hang_tiem_nang"),
                    (BigDecimal) row.get("tong_chi_tieu"),
                    ((java.sql.Timestamp) row.get("thoigiantu")).toLocalDateTime(),
                    ((java.sql.Timestamp) row.get("thoigianden")).toLocalDateTime(),
                    (String) row.get("noidung"),
                    (String) row.get("ten_cua_hang")
                );
                dtoList.add(dto);
            }
            
            return dtoList;
        } catch (Exception e) {
            log.error("Lỗi khi lấy thống kê khách hàng tiềm năng tháng {}/{}: {}", thang, nam, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<ThongKeDoanhThuDTO> soSanhDoanhThuTheoThang(int soThang, String maCH) {
        try {
            String sql = """
                SELECT 
                    EXTRACT(MONTH FROM t.thoigiantu) as thang,
                    EXTRACT(YEAR FROM t.thoigiantu) as nam,
                    t.sotien as doanh_thu,
                    LAG(t.sotien) OVER (ORDER BY t.thoigiantu) as doanh_thu_thang_truoc,
                    CASE 
                        WHEN LAG(t.sotien) OVER (ORDER BY t.thoigiantu) > 0 THEN
                            ROUND(((t.sotien - LAG(t.sotien) OVER (ORDER BY t.thoigiantu)) / LAG(t.sotien) OVER (ORDER BY t.thoigiantu) * 100)::numeric, 2)
                        ELSE NULL
                    END as tang_truong_phan_tram,
                    t.tenbaocao,
                    t.soluong as so_hoa_don,
                    t.thoigiantu,
                    t.thoigianden,
                    COALESCE(c.tench, 'Tổng hệ thống') as ten_cua_hang
                FROM thongkebaocao t
                LEFT JOIN cuahang c ON t.mach = c.mach
                WHERE t.loaibaocao = 'DOANH_THU_THANG'
                  AND (? IS NULL OR t.mach = ? OR (? IS NULL AND t.mach IS NULL))
                  AND t.isdeleted = FALSE
                  AND t.thoigiantu >= (CURRENT_DATE - INTERVAL '%d months')
                ORDER BY t.thoigiantu DESC
                LIMIT ?
            """.formatted(soThang);
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, maCH, maCH, maCH, soThang);
            List<ThongKeDoanhThuDTO> dtoList = new ArrayList<>();
            
            for (Map<String, Object> row : results) {
                ThongKeDoanhThuDTO dto = new ThongKeDoanhThuDTO();
                dto.setThang(((Number) row.get("thang")).intValue());
                dto.setNam(((Number) row.get("nam")).intValue());
                dto.setDoanhThu((BigDecimal) row.get("doanh_thu"));
                dto.setDoanhThuThangTruoc((BigDecimal) row.get("doanh_thu_thang_truoc"));
                dto.setTangTruongPhanTram((BigDecimal) row.get("tang_truong_phan_tram"));
                dto.setTenBaoCao((String) row.get("tenbaocao"));
                dto.setSoHoaDon((Integer) row.get("so_hoa_don"));
                dto.setThoiGianTu(((java.sql.Timestamp) row.get("thoigiantu")).toLocalDateTime());
                dto.setThoiGianDen(((java.sql.Timestamp) row.get("thoigianden")).toLocalDateTime());
                dto.setTenCuaHang((String) row.get("ten_cua_hang"));
                
                dtoList.add(dto);
            }
            
            return dtoList;
        } catch (Exception e) {
            log.error("Lỗi khi so sánh doanh thu theo tháng: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<ThongKeKhachHangTiemNangDTO> layTopKhachHangTiemNang(int thang, int nam, String maCH, int limit) {
        try {
            String sql = """
                SELECT 
                    kh.makh,
                    kh.hoten,
                    kh.sdt,
                    kh.loaikhachhang,
                    kh.diemtichluy,
                    COUNT(h.mahd) as so_hoa_don,
                    SUM(h.tongtien) as tong_chi_tieu,
                    AVG(h.tongtien) as gia_tri_trung_binh
                FROM khachhang kh
                INNER JOIN hoadon h ON kh.makh = h.makh
                LEFT JOIN nhanvien nv ON h.manvlap = nv.manv
                WHERE EXTRACT(MONTH FROM h.ngaylap) = ?
                  AND EXTRACT(YEAR FROM h.ngaylap) = ?
                  AND h.trangthai = 1
                  AND h.isdeleted = FALSE
                  AND kh.isdeleted = FALSE
                  AND (? IS NULL OR nv.mach = ?)
                GROUP BY kh.makh, kh.hoten, kh.sdt, kh.loaikhachhang, kh.diemtichluy
                HAVING SUM(h.tongtien) >= 1000000 OR COUNT(h.mahd) >= 5
                ORDER BY SUM(h.tongtien) DESC
                LIMIT ?
            """;
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, thang, nam, maCH, maCH, limit);
            List<ThongKeKhachHangTiemNangDTO> dtoList = new ArrayList<>();
            
            for (Map<String, Object> row : results) {
                ThongKeKhachHangTiemNangDTO dto = new ThongKeKhachHangTiemNangDTO(
                    (String) row.get("makh"),
                    (String) row.get("hoten"),
                    (String) row.get("sdt"),
                    (String) row.get("loaikhachhang"),
                    ((Number) row.get("diemtichluy")).intValue(),
                    ((Number) row.get("so_hoa_don")).intValue(),
                    (BigDecimal) row.get("tong_chi_tieu"),
                    (BigDecimal) row.get("gia_tri_trung_binh")
                );
                dto.setThang(thang);
                dto.setNam(nam);
                
                dtoList.add(dto);
            }
            
            return dtoList;
        } catch (Exception e) {
            log.error("Lỗi khi lấy top khách hàng tiềm năng: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void tuDongTinhThongKe(Long maHoaDon) {
        try {
            HoaDon hoaDon = hoaDonRepository.findById(Math.toIntExact(maHoaDon)).orElse(null);
            if (hoaDon == null) {
                log.warn("Không tìm thấy hóa đơn với ID: {}", maHoaDon);
                return;
            }
            
            LocalDateTime ngayLap = hoaDon.getNgayLap();
            int thang = ngayLap.getMonthValue();
            int nam = ngayLap.getYear();
            
            // Lấy mã cửa hàng từ nhân viên lập hóa đơn
            NhanVien nhanVien = hoaDon.getNhanVienLap();
            String maCH = nhanVien != null && nhanVien.getCuaHang() != null ? 
                         nhanVien.getCuaHang().getMaCH() : null;
            
            // Tính toán cho cửa hàng cụ thể
            if (maCH != null) {
                tinhDoanhThuThang(thang, nam, maCH);
                tinhKhachHangTiemNang(thang, nam, maCH);
            }
            
            // Tính toán cho tổng hệ thống
            tinhDoanhThuThang(thang, nam, null);
            tinhKhachHangTiemNang(thang, nam, null);
            
            log.info("Đã tự động cập nhật thống kê cho hóa đơn: {}", maHoaDon);
        } catch (Exception e) {
            log.error("Lỗi khi tự động tính thống kê cho hóa đơn {}: {}", maHoaDon, e.getMessage());
        }
    }
}