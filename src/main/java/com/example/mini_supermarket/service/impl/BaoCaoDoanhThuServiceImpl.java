package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.dto.ThongKeKhachHangDTO;
import com.example.mini_supermarket.dto.ThongKeSanPhamDTO;
import com.example.mini_supermarket.entity.*;
import com.example.mini_supermarket.repository.*;
import com.example.mini_supermarket.service.BaoCaoDoanhThuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class BaoCaoDoanhThuServiceImpl implements BaoCaoDoanhThuService {

    @Autowired
    private BaoCaoDoanhThuRepository baoCaoDoanhThuRepository;
    
    @Autowired
    private HoaDonRepository hoaDonRepository;
    
    @Autowired
    private ChiTietHoaDonRepository chiTietHoaDonRepository;
    
    @Autowired
    private KhachHangRepository khachHangRepository;
    
    @Autowired
    private SanPhamRepository sanPhamRepository;
    
    
    @Autowired
    private NhanVienRepository nhanVienRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ===================================
    // CRUD OPERATIONS
    // ===================================

    @Override
    public List<BaoCaoDoanhThu> findAll() {
        return baoCaoDoanhThuRepository.findAll();
    }

    @Override
    public List<BaoCaoDoanhThu> findAllActive() {
        return baoCaoDoanhThuRepository.findAllActive();
    }

    @Override
    public BaoCaoDoanhThu findById(Long id) {
        return baoCaoDoanhThuRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy báo cáo với ID: " + id));
    }

    @Override
    public BaoCaoDoanhThu findActiveById(Long id) {
        return baoCaoDoanhThuRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy báo cáo hoạt động với ID: " + id));
    }

    @Override
    public BaoCaoDoanhThu save(BaoCaoDoanhThu baoCaoDoanhThu) {
        return baoCaoDoanhThuRepository.save(baoCaoDoanhThu);
    }

    @Override
    public BaoCaoDoanhThu update(BaoCaoDoanhThu baoCaoDoanhThu) {
        if (!baoCaoDoanhThuRepository.existsById(baoCaoDoanhThu.getMaBaoCao())) {
            throw new RuntimeException("Báo cáo không tồn tại với ID: " + baoCaoDoanhThu.getMaBaoCao());
        }
        return baoCaoDoanhThuRepository.save(baoCaoDoanhThu);
    }

    @Override
    public void deleteById(Long id) {
        baoCaoDoanhThuRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(Long id) {
        BaoCaoDoanhThu baoCao = findById(id);
        baoCao.setIsDeleted(true);
        baoCaoDoanhThuRepository.save(baoCao);
    }

    // ===================================
    // SEARCH OPERATIONS
    // ===================================

    @Override
    public List<BaoCaoDoanhThu> findByLoaiBaoCao(String loai) {
        return baoCaoDoanhThuRepository.findByLoaiBaoCao(loai);
    }


    @Override
    public List<BaoCaoDoanhThu> findByDateRange(LocalDate tuNgay, LocalDate denNgay) {
        return baoCaoDoanhThuRepository.findByDateRange(tuNgay, denNgay);
    }

    @Override
    public List<BaoCaoDoanhThu> findByLoaiAndDateRange(String loai, LocalDate tuNgay, LocalDate denNgay) {
        return baoCaoDoanhThuRepository.findByLoaiAndDateRange(loai, tuNgay, denNgay);
    }


    // ===================================
    // REPORT GENERATION
    // ===================================

    @Override
    public BaoCaoDoanhThu taoBaoCaoDoanhThu(String loaiBaoCao, LocalDate tuNgay, LocalDate denNgay) {
        // Kiểm tra báo cáo đã tồn tại
        if (kiemTraBaoCaoTonTai(loaiBaoCao, tuNgay, denNgay)) {
            throw new RuntimeException("Báo cáo đã tồn tại cho khoảng thời gian này!");
        }
        
        return taoOrCapNhatBaoCao(loaiBaoCao, tuNgay, denNgay);
    }

    @Override
    public BaoCaoDoanhThu taoOrCapNhatBaoCao(String loaiBaoCao, LocalDate tuNgay, LocalDate denNgay) {
        // Tạo báo cáo chính
        BaoCaoDoanhThu baoCao = new BaoCaoDoanhThu();
        
        // Set thông tin cơ bản
        baoCao.setLoaiBaoCao(loaiBaoCao);
        baoCao.setTuNgay(tuNgay);
        baoCao.setDenNgay(denNgay);
        baoCao.setNgayBaoCao(LocalDate.now());
        
        // Tạo tên báo cáo
        String tenBaoCao = taoTenBaoCao(loaiBaoCao, tuNgay, denNgay);
        baoCao.setTenBaoCao(tenBaoCao);
        
        // Tính toán thống kê tổng quan
        tinhToanThongKeTongQuan(baoCao, tuNgay, denNgay);
        
        // Tạo JSON data cho các phần chi tiết
        taoJsonData(baoCao, tuNgay, denNgay);
        
        // Lưu báo cáo
        return baoCaoDoanhThuRepository.save(baoCao);
    }

    private String taoTenBaoCao(String loaiBaoCao, LocalDate tuNgay, LocalDate denNgay) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        return "Báo cáo doanh thu " + loaiBaoCao.toLowerCase() + 
               " từ " + tuNgay.format(formatter) + 
               " đến " + denNgay.format(formatter) + " - Tổng hệ thống";
    }

    private void tinhToanThongKeTongQuan(BaoCaoDoanhThu baoCao, LocalDate tuNgay, LocalDate denNgay) {
        // Query doanh thu tổng
        List<Object[]> doanhThuData = hoaDonRepository.thongKeDoanhThuTongQuan(
            tuNgay.atStartOfDay(),
            denNgay.plusDays(1).atStartOfDay(),
            null
        );
        
        if (!doanhThuData.isEmpty()) {
            Object[] data = doanhThuData.get(0);
            baoCao.setTongDoanhThu((BigDecimal) data[0]);
            baoCao.setTongSoHoaDon(((Number) data[1]).intValue());
            baoCao.setTongSoKhachHang(((Number) data[2]).intValue());
            baoCao.setDoanhThuTrungBinh((BigDecimal) data[3]);
            baoCao.setHoaDonTrungBinh((BigDecimal) data[4]);
        }
        
        // Đếm tổng số sản phẩm đã bán
        Integer tongSoSanPham = chiTietHoaDonRepository.demTongSoSanPhamDaBan(
            tuNgay.atStartOfDay(),
            denNgay.plusDays(1).atStartOfDay(),
            null
        );
        baoCao.setTongSoSanPham(tongSoSanPham != null ? tongSoSanPham : 0);
        
        // Tính tăng trưởng so với kỳ trước
        BaoCaoDoanhThu baoCaoKyTruoc = layBaoCaoKyTruoc(baoCao.getLoaiBaoCao(), tuNgay);
        if (baoCaoKyTruoc != null) {
            BigDecimal tyLeDoanhThu = tinhTyLeTangTruong(baoCao.getTongDoanhThu(), baoCaoKyTruoc.getTongDoanhThu());
            BigDecimal tyLeHoaDon = tinhTyLeTangTruong(
                new BigDecimal(baoCao.getTongSoHoaDon()), 
                new BigDecimal(baoCaoKyTruoc.getTongSoHoaDon())
            );
            BigDecimal tyLeKhachHang = tinhTyLeTangTruong(
                new BigDecimal(baoCao.getTongSoKhachHang()), 
                new BigDecimal(baoCaoKyTruoc.getTongSoKhachHang())
            );
            
            baoCao.setTyLeTangTruongDoanhThu(tyLeDoanhThu);
            baoCao.setTyLeTangTruongHoaDon(tyLeHoaDon);
            baoCao.setTyLeTangTruongKhachHang(tyLeKhachHang);
        }
    }

    private void taoJsonData(BaoCaoDoanhThu baoCao, LocalDate tuNgay, LocalDate denNgay) {
        try {
            // Top sản phẩm bán chay
            List<ThongKeSanPhamDTO> topSanPham = thongKeSanPhamBanChay(tuNgay, denNgay, 10);
            baoCao.setTopSanPhamBanChay(objectMapper.writeValueAsString(topSanPham));
            
            // Top khách hàng tiềm năng
            List<ThongKeKhachHangDTO> topKhachHang = thongKeKhachHangTiemNang(tuNgay, denNgay, 10);
            baoCao.setTopKhachHangTiemNang(objectMapper.writeValueAsString(topKhachHang));
            
            // Chi tiết sản phẩm (top 50)
            List<ThongKeSanPhamDTO> chiTietSanPham = thongKeSanPhamBanChay(tuNgay, denNgay, 50);
            baoCao.setChiTietSanPham(objectMapper.writeValueAsString(chiTietSanPham));
            
            // Chi tiết khách hàng (top 50)
            List<ThongKeKhachHangDTO> chiTietKhachHang = thongKeKhachHangTiemNang(tuNgay, denNgay, 50);
            baoCao.setChiTietKhachHang(objectMapper.writeValueAsString(chiTietKhachHang));
            
            // Thống kê loại sản phẩm
            List<Map<String, Object>> thongKeLoai = layThongKeLoaiSanPham(tuNgay, denNgay);
            baoCao.setThongKeLoaiSanPham(objectMapper.writeValueAsString(thongKeLoai));
            baoCao.setChiTietLoaiSanPham(objectMapper.writeValueAsString(thongKeLoai));
            
            // Phân tích tăng trưởng
            Map<String, Object> phanTichTangTruong = layPhanTichTangTruong(baoCao, tuNgay, denNgay);
            baoCao.setPhanTichTangTruong(objectMapper.writeValueAsString(phanTichTangTruong));
            
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi tạo JSON data: " + e.getMessage(), e);
        }
    }

    // ===================================
    // STATISTICS OPERATIONS
    // ===================================

    @Override
    public List<ThongKeSanPhamDTO> thongKeSanPhamBanChay(LocalDate tuNgay, LocalDate denNgay, int limit) {
        List<Object[]> results = chiTietHoaDonRepository.thongKeSanPhamBanChay(
            tuNgay.atStartOfDay(),
            denNgay.plusDays(1).atStartOfDay(),
            null,
            limit
        );
        
        List<ThongKeSanPhamDTO> dtoList = new ArrayList<>();
        int thuTu = 1;
        
        for (Object[] row : results) {
            ThongKeSanPhamDTO dto = new ThongKeSanPhamDTO();
            dto.setMaSP((String) row[0]);
            dto.setTenSP((String) row[1]);
            dto.setTenLoaiSP((String) row[2]);
            dto.setSoLuongBan(((Number) row[3]).intValue());
            dto.setDoanhThu((BigDecimal) row[4]);
            dto.setSoHoaDon(((Number) row[5]).intValue());
            dto.setGiaTriTrungBinh((BigDecimal) row[6]);
            dto.setThuTuXepHang(thuTu++);
            
            // Tính tăng trưởng (có thể implement sau)
            dto.setTangTruong(BigDecimal.ZERO);
            
            dtoList.add(dto);
        }
        
        return dtoList;
    }

    @Override
    public List<ThongKeSanPhamDTO> thongKeSanPhamTangTruong(LocalDate tuNgay, LocalDate denNgay, int limit) {
        // Implementation tương tự thongKeSanPhamBanChay nhưng sắp xếp theo tăng trưởng
        return thongKeSanPhamBanChay(tuNgay, denNgay, limit);
    }

    @Override
    public List<ThongKeKhachHangDTO> thongKeKhachHangTiemNang(LocalDate tuNgay, LocalDate denNgay, int limit) {
        List<Object[]> results = hoaDonRepository.thongKeKhachHangTiemNang(
            tuNgay.atStartOfDay(),
            denNgay.plusDays(1).atStartOfDay(),
            null,
            limit
        );
        
        List<ThongKeKhachHangDTO> dtoList = new ArrayList<>();
        int thuTu = 1;
        
        for (Object[] row : results) {
            ThongKeKhachHangDTO dto = new ThongKeKhachHangDTO();
            dto.setMaKH((String) row[0]);
            dto.setTenKH((String) row[1]);
            dto.setSoHoaDon(((Number) row[2]).intValue());
            dto.setTongChiTieu((BigDecimal) row[3]);
            dto.setGiaTriTrungBinh((BigDecimal) row[4]);
            dto.setThuTuXepHang(thuTu++);
            
            // Set default values for missing fields
            dto.setLoaiKhachHang("Thường");
            dto.setDiemTichLuy(0);
            dto.setTangTruong(BigDecimal.ZERO);
            
            dtoList.add(dto);
        }
        
        return dtoList;
    }

    @Override
    public List<ThongKeKhachHangDTO> thongKeKhachHangTangTruong(LocalDate tuNgay, LocalDate denNgay, int limit) {
        // Implementation tương tự thongKeKhachHangTiemNang nhưng sắp xếp theo tăng trưởng
        return thongKeKhachHangTiemNang(tuNgay, denNgay, limit);
    }

    // ===================================
    // JSON DATA RETRIEVAL
    // ===================================

    @Override
    public List<Map<String, Object>> getTopSanPhamFromJson(Long maBaoCao) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        return parseJsonToList(baoCao.getTopSanPhamBanChay());
    }

    @Override
    public List<Map<String, Object>> getTopKhachHangFromJson(Long maBaoCao) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        return parseJsonToList(baoCao.getTopKhachHangTiemNang());
    }

    @Override
    public List<Map<String, Object>> getThongKeLoaiSanPhamFromJson(Long maBaoCao) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        return parseJsonToList(baoCao.getThongKeLoaiSanPham());
    }

    @Override
    public Map<String, Object> getPhanTichTangTruongFromJson(Long maBaoCao) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        return parseJsonToMap(baoCao.getPhanTichTangTruong());
    }

    @Override
    public List<Map<String, Object>> getChiTietSanPhamFromJson(Long maBaoCao) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        return parseJsonToList(baoCao.getChiTietSanPham());
    }

    @Override
    public List<Map<String, Object>> getChiTietKhachHangFromJson(Long maBaoCao) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        return parseJsonToList(baoCao.getChiTietKhachHang());
    }

    @Override
    public List<Map<String, Object>> getChiTietLoaiSanPhamFromJson(Long maBaoCao) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        return parseJsonToList(baoCao.getChiTietLoaiSanPham());
    }

    // ===================================
    // JSON SEARCH OPERATIONS
    // ===================================

    @Override
    public List<BaoCaoDoanhThu> findByTopSanPhamContains(String maSP) {
        return baoCaoDoanhThuRepository.findByTopSanPhamContains(maSP);
    }

    @Override
    public List<BaoCaoDoanhThu> findByTopKhachHangContains(String maKH) {
        return baoCaoDoanhThuRepository.findByTopKhachHangContains(maKH);
    }

    @Override
    public List<BaoCaoDoanhThu> findByChiTietSanPhamContains(String maSP) {
        return baoCaoDoanhThuRepository.findByChiTietSanPhamContains(maSP);
    }

    @Override
    public List<BaoCaoDoanhThu> findByChiTietKhachHangContains(String maKH) {
        return baoCaoDoanhThuRepository.findByChiTietKhachHangContains(maKH);
    }

    @Override
    public List<BaoCaoDoanhThu> findByLoaiSanPhamContains(String maLoaiSP) {
        return baoCaoDoanhThuRepository.findByLoaiSanPhamContains(maLoaiSP);
    }

    // ===================================
    // UTILITY METHODS
    // ===================================

    @Override
    public boolean kiemTraBaoCaoTonTai(String loai, LocalDate tuNgay, LocalDate denNgay) {
        return baoCaoDoanhThuRepository.existsByLoaiAndDateRange(loai, tuNgay, denNgay);
    }

    @Override
    public BaoCaoDoanhThu layBaoCaoKyTruoc(String loai, LocalDate tuNgay) {
        Optional<BaoCaoDoanhThu> result = baoCaoDoanhThuRepository.findPreviousPeriod(loai, tuNgay);
        return result.orElse(null);
    }

    // ===================================
    // HELPER METHODS
    // ===================================

    private BigDecimal tinhTyLeTangTruong(BigDecimal giaTriHienTai, BigDecimal giaTriTruoc) {
        if (giaTriTruoc == null || giaTriTruoc.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return giaTriHienTai.subtract(giaTriTruoc)
                .divide(giaTriTruoc, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private List<Map<String, Object>> parseJsonToList(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            return objectMapper.readValue(jsonString, new TypeReference<List<Map<String, Object>>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    private Map<String, Object> parseJsonToMap(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }

    private List<Map<String, Object>> layThongKeLoaiSanPham(LocalDate tuNgay, LocalDate denNgay) {
        // Implementation để lấy thống kê theo loại sản phẩm
        // Có thể query từ database hoặc tính toán từ dữ liệu có sẵn
        return new ArrayList<>();
    }

    private Map<String, Object> layPhanTichTangTruong(BaoCaoDoanhThu baoCao, LocalDate tuNgay, LocalDate denNgay) {
        Map<String, Object> phanTich = new HashMap<>();
        
        // Lấy báo cáo kỳ trước
        BaoCaoDoanhThu baoCaoKyTruoc = layBaoCaoKyTruoc(baoCao.getLoaiBaoCao(), tuNgay);
        if (baoCaoKyTruoc != null) {
            phanTich.put("doanhThuKyTruoc", baoCaoKyTruoc.getTongDoanhThu());
            phanTich.put("tangTruongDoanhThu", baoCao.getTyLeTangTruongDoanhThu());
            phanTich.put("tangTruongHoaDon", baoCao.getTyLeTangTruongHoaDon());
            phanTich.put("tangTruongKhachHang", baoCao.getTyLeTangTruongKhachHang());
        }
        
        return phanTich;
    }
}