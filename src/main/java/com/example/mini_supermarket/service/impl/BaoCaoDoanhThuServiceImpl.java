package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.dto.ThongKeKhachHangDTO;
import com.example.mini_supermarket.dto.ThongKeSanPhamDTO;
import com.example.mini_supermarket.entity.BaoCaoDoanhThu;
import com.example.mini_supermarket.entity.HoaDon;
import com.example.mini_supermarket.entity.PhieuNhapHang;
import com.example.mini_supermarket.repository.BaoCaoDoanhThuRepository;
import com.example.mini_supermarket.repository.ChiTietHoaDonRepository;
import com.example.mini_supermarket.repository.HoaDonRepository;
import com.example.mini_supermarket.repository.PhieuNhapHangRepository;
import com.example.mini_supermarket.service.BaoCaoDoanhThuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class BaoCaoDoanhThuServiceImpl implements BaoCaoDoanhThuService {

    @Autowired
    private BaoCaoDoanhThuRepository baoCaoDoanhThuRepository;
    
    @Autowired
    private ChiTietHoaDonRepository chiTietHoaDonRepository;
    
    @Autowired
    private HoaDonRepository hoaDonRepository;
    
    @Autowired
    private PhieuNhapHangRepository phieuNhapHangRepository;

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
    public BaoCaoDoanhThu findById(String id) {
        return baoCaoDoanhThuRepository.findById(id).orElse(null);
    }

    @Override
    public BaoCaoDoanhThu findActiveById(String id) {
        return baoCaoDoanhThuRepository.findActiveById(id).orElse(null);
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
    public void deleteById(String id) {
        baoCaoDoanhThuRepository.deleteById(id);
    }

    @Override
    public void softDeleteById(String id) {
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
        
        // Lưu báo cáo trước
        baoCao = baoCaoDoanhThuRepository.save(baoCao);
        
        // Tự động liên kết các hóa đơn trong khoảng thời gian
        linkHoaDonsAutomatically(baoCao, tuNgay, denNgay);
        
        return baoCao;
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
            // Log error and set default values instead of throwing exception
            System.err.println("Lỗi tạo JSON data: " + e.getMessage());
            baoCao.setTopSanPhamBanChay("[]");
            baoCao.setTopKhachHangTiemNang("[]");
            baoCao.setChiTietSanPham("[]");
            baoCao.setChiTietKhachHang("[]");
            baoCao.setThongKeLoaiSanPham("[]");
            baoCao.setChiTietLoaiSanPham("[]");
            baoCao.setPhanTichTangTruong("{}");
        } catch (Exception e) {
            // Handle any other unexpected errors
            System.err.println("Lỗi không mong đợi khi cập nhật JSON: " + e.getMessage());
            baoCao.setTopSanPhamBanChay("[]");
            baoCao.setTopKhachHangTiemNang("[]");
            baoCao.setChiTietSanPham("[]");
            baoCao.setChiTietKhachHang("[]");
            baoCao.setThongKeLoaiSanPham("[]");
            baoCao.setChiTietLoaiSanPham("[]");
            baoCao.setPhanTichTangTruong("{}");
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
    public List<Map<String, Object>> getTopSanPhamFromJson(String maBaoCao) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        return parseJsonToList(baoCao.getTopSanPhamBanChay());
    }

    @Override
    public List<Map<String, Object>> getTopKhachHangFromJson(String maBaoCao) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        return parseJsonToList(baoCao.getTopKhachHangTiemNang());
    }

    @Override
    public List<Map<String, Object>> getThongKeLoaiSanPhamFromJson(String maBaoCao) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        return parseJsonToList(baoCao.getThongKeLoaiSanPham());
    }

    @Override
    public Map<String, Object> getPhanTichTangTruongFromJson(String maBaoCao) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        return parseJsonToMap(baoCao.getPhanTichTangTruong());
    }

    @Override
    public List<Map<String, Object>> getChiTietSanPhamFromJson(String maBaoCao) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        return parseJsonToList(baoCao.getChiTietSanPham());
    }

    @Override
    public List<Map<String, Object>> getChiTietKhachHangFromJson(String maBaoCao) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        return parseJsonToList(baoCao.getChiTietKhachHang());
    }

    @Override
    public List<Map<String, Object>> getChiTietLoaiSanPhamFromJson(String maBaoCao) {
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

    // ===================================
    // HOADON RELATIONSHIP METHODS
    // ===================================

    @Override
    public List<BaoCaoDoanhThu> findByHoaDonId(Integer maHD) {
        return baoCaoDoanhThuRepository.findByHoaDonId(maHD);
    }

    @Override
    public List<HoaDon> findHoaDonsByBaoCaoId(String maBaoCao) {
        return baoCaoDoanhThuRepository.findHoaDonsByBaoCaoId(maBaoCao);
    }

    @Override
    public Long countHoaDonsByBaoCaoId(String maBaoCao) {
        return baoCaoDoanhThuRepository.countHoaDonsByBaoCaoId(maBaoCao);
    }

    @Override
    public BigDecimal sumDoanhThuFromHoaDons(String maBaoCao) {
        BigDecimal result = baoCaoDoanhThuRepository.sumDoanhThuFromHoaDons(maBaoCao);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public void linkHoaDonsToBaoCao(String maBaoCao, List<Integer> hoaDonIds) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        
        // Lấy danh sách hóa đơn cần liên kết
        List<HoaDon> hoaDonsToLink = new ArrayList<>();
        for (Integer hoaDonId : hoaDonIds) {
            Optional<HoaDon> hoaDonOpt = hoaDonRepository.findActiveById(hoaDonId);
            if (hoaDonOpt.isPresent()) {
                hoaDonsToLink.add(hoaDonOpt.get());
            }
        }
        
        // Thêm vào danh sách hiện tại (nếu chưa có)
        if (baoCao.getHoaDons() == null) {
            baoCao.setHoaDons(new ArrayList<>());
        }
        
        for (HoaDon hoaDon : hoaDonsToLink) {
            if (!baoCao.getHoaDons().contains(hoaDon)) {
                baoCao.getHoaDons().add(hoaDon);
            }
        }
        
        baoCaoDoanhThuRepository.save(baoCao);
    }

    @Override
    @Transactional
    public void unlinkHoaDonsFromBaoCao(String maBaoCao, List<Integer> hoaDonIds) {
        BaoCaoDoanhThu baoCao = findActiveById(maBaoCao);
        
        if (baoCao.getHoaDons() != null) {
            // Xóa các hóa đơn khỏi danh sách
            baoCao.getHoaDons().removeIf(hoaDon -> hoaDonIds.contains(hoaDon.getMaHD()));
        }
        
        baoCaoDoanhThuRepository.save(baoCao);
    }

    /**
     * Tự động liên kết các hóa đơn trong khoảng thời gian với báo cáo
     */
    private void linkHoaDonsAutomatically(BaoCaoDoanhThu baoCao, LocalDate tuNgay, LocalDate denNgay) {
        try {
            // Lấy tất cả hóa đơn trong khoảng thời gian
            List<HoaDon> hoaDonsInRange = hoaDonRepository.findByDateRangeAndStore(
                tuNgay.atStartOfDay(),
                denNgay.plusDays(1).atStartOfDay(),
                null // Tất cả cửa hàng
            );
            
            // Khởi tạo danh sách nếu chưa có
            if (baoCao.getHoaDons() == null) {
                baoCao.setHoaDons(new ArrayList<>());
            }
            
            // Thêm các hóa đơn vào báo cáo (tránh trùng lặp)
            for (HoaDon hoaDon : hoaDonsInRange) {
                if (!baoCao.getHoaDons().contains(hoaDon)) {
                    baoCao.getHoaDons().add(hoaDon);
                }
            }
            
            // Lưu lại báo cáo với các hóa đơn đã liên kết
            baoCaoDoanhThuRepository.save(baoCao);
            
        } catch (Exception e) {
            // Log error nhưng không throw exception để không ảnh hưởng việc tạo báo cáo
            System.err.println("Lỗi khi tự động liên kết hóa đơn: " + e.getMessage());
        }
    }

    // ===================================
    // NEW RELATIONSHIP METHODS IMPLEMENTATION
    // ===================================

    @Override
    public List<BaoCaoDoanhThu> findByNhanVienTao(String maNV) {
        return baoCaoDoanhThuRepository.findByNhanVienTao(maNV);
    }

    @Override
    public long countByNhanVienTao(String maNV) {
        return baoCaoDoanhThuRepository.countByNhanVienTao(maNV);
    }

    @Override
    public List<BaoCaoDoanhThu> findByCuaHang(String maCH) {
        return baoCaoDoanhThuRepository.findByCuaHang(maCH);
    }

    @Override
    public long countByCuaHang(String maCH) {
        return baoCaoDoanhThuRepository.countByCuaHang(maCH);
    }

    @Override
    public BigDecimal sumDoanhThuByCuaHang(String maCH) {
        BigDecimal result = baoCaoDoanhThuRepository.sumDoanhThuByCuaHang(maCH);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public List<BaoCaoDoanhThu> findByPhieuNhapHang(Integer maPN) {
        return baoCaoDoanhThuRepository.findByPhieuNhapHang(maPN);
    }

    @Override
    public List<PhieuNhapHang> findPhieuNhapHangsByBaoCaoId(Long maBaoCao) {
        return baoCaoDoanhThuRepository.findPhieuNhapHangsByBaoCaoId(maBaoCao.toString());
    }

    @Override
    public long countPhieuNhapHangsByBaoCaoId(Long maBaoCao) {
        return baoCaoDoanhThuRepository.countPhieuNhapHangsByBaoCaoId(maBaoCao.toString());
    }

    @Override
    public BigDecimal sumChiPhiFromPhieuNhapHangs(Long maBaoCao) {
        BigDecimal result = baoCaoDoanhThuRepository.sumChiPhiFromPhieuNhapHangs(maBaoCao.toString());
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public BaoCaoDoanhThu linkPhieuNhapHangsToBaoCao(String maBaoCao, List<Integer> phieuNhapIds) {
        BaoCaoDoanhThu baoCao = findById(maBaoCao);
        if (baoCao == null) {
            throw new RuntimeException("Không tìm thấy báo cáo với ID: " + maBaoCao);
        }

        for (Integer phieuNhapId : phieuNhapIds) {
            PhieuNhapHang phieuNhap = phieuNhapHangRepository.findById(phieuNhapId).orElse(null);
            if (phieuNhap != null && !Boolean.TRUE.equals(phieuNhap.getIsDeleted())) {
                if (baoCao.getPhieuNhapHangs() == null) {
                    baoCao.setPhieuNhapHangs(new ArrayList<>());
                }
                if (!baoCao.getPhieuNhapHangs().contains(phieuNhap)) {
                    baoCao.getPhieuNhapHangs().add(phieuNhap);
                }
            }
        }

        // Cập nhật chi phí sau khi liên kết
        baoCao.capNhatChiPhiTuPhieuNhap();
        return baoCaoDoanhThuRepository.save(baoCao);
    }

    @Override
    @Transactional
    public BaoCaoDoanhThu unlinkPhieuNhapHangsFromBaoCao(String maBaoCao, List<Integer> phieuNhapIds) {
        BaoCaoDoanhThu baoCao = findById(maBaoCao);
        if (baoCao == null) {
            throw new RuntimeException("Không tìm thấy báo cáo với ID: " + maBaoCao);
        }

        if (baoCao.getPhieuNhapHangs() != null) {
            baoCao.getPhieuNhapHangs().removeIf(phieuNhap -> 
                phieuNhapIds.contains(phieuNhap.getMaPN())
            );
        }

        // Cập nhật chi phí sau khi bỏ liên kết
        baoCao.capNhatChiPhiTuPhieuNhap();
        return baoCaoDoanhThuRepository.save(baoCao);
    }

    @Override
    public List<BaoCaoDoanhThu> findByCuaHangAndNhanVienAndDateRange(String maCH, String maNV, LocalDate tuNgay, LocalDate denNgay) {
        return baoCaoDoanhThuRepository.findByCuaHangAndNhanVienAndDateRange(maCH, maNV, tuNgay, denNgay);
    }

    @Override
    public List<BaoCaoDoanhThu> findTopPerformingReportsByCuaHang(String maCH, LocalDate tuNgay, LocalDate denNgay, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return baoCaoDoanhThuRepository.findTopPerformingReportsByCuaHang(maCH, tuNgay, denNgay, pageable);
    }

    @Override
    @Transactional
    public BaoCaoDoanhThu capNhatThongKeToanDien(Long maBaoCao) {
        BaoCaoDoanhThu baoCao = findById(String.valueOf(maBaoCao));
        if (baoCao == null) {
            throw new RuntimeException("Không tìm thấy báo cáo với ID: " + maBaoCao);
        }

        // Cập nhật thống kê từ hóa đơn
        baoCao.capNhatThongKeTuHoaDon();
        
        // Cập nhật chi phí từ phiếu nhập
        baoCao.capNhatChiPhiTuPhieuNhap();

        return baoCaoDoanhThuRepository.save(baoCao);
    }

    @Override
    public BigDecimal tinhLoiNhuan(Long maBaoCao) {
        BaoCaoDoanhThu baoCao = findById(String.valueOf(maBaoCao));
        if (baoCao == null) {
            return BigDecimal.ZERO;
        }
        return baoCao.tinhLoiNhuan();
    }

    @Override
    public List<BaoCaoDoanhThu> findByHoaDonDateRange(LocalDateTime tuNgay, LocalDateTime denNgay) {
        return baoCaoDoanhThuRepository.findByHoaDonDateRange(tuNgay, denNgay);
    }
}