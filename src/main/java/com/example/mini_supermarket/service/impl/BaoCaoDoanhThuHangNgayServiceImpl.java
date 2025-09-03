package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.entity.BaoCaoDoanhThuHangNgay;
import com.example.mini_supermarket.repository.BaoCaoDoanhThuHangNgayRepository;
import com.example.mini_supermarket.service.BaoCaoDoanhThuHangNgayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BaoCaoDoanhThuHangNgayServiceImpl implements BaoCaoDoanhThuHangNgayService {
    
    @Autowired
    private BaoCaoDoanhThuHangNgayRepository baoCaoRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    // ===== CRUD CƠ BẢN =====
    
    @Override
    @Transactional(readOnly = true)
    public List<BaoCaoDoanhThuHangNgay> findAll() {
        return baoCaoRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public BaoCaoDoanhThuHangNgay findById(Integer id) {
        return baoCaoRepository.findById(id).orElse(null);
    }
    
    @Override
    @Transactional
    public BaoCaoDoanhThuHangNgay save(BaoCaoDoanhThuHangNgay baoCao) {
        return baoCaoRepository.save(baoCao);
    }
    
    @Override
    @Transactional
    public void deleteById(Integer id) {
        baoCaoRepository.deleteById(id);
    }
    
    // ===== TÌM KIẾM THEO NGÀY =====
    
    @Override
    @Transactional(readOnly = true)
    public List<BaoCaoDoanhThuHangNgay> findByNgayBan(LocalDate ngayBan) {
        return baoCaoRepository.findByNgayBan(ngayBan);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BaoCaoDoanhThuHangNgay> findByNgayBanAndMaCH(LocalDate ngayBan, String maCH) {
        return baoCaoRepository.findByNgayBanAndCuaHang_MaCH(ngayBan, maCH);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BaoCaoDoanhThuHangNgay> findByNgayBanAndMaSP(LocalDate ngayBan, String maSP) {
        return baoCaoRepository.findByNgayBanAndSanPham_MaSP(ngayBan, maSP);
    }
    
    // ===== TÌM KIẾM THEO KHOẢNG THỜI GIAN =====
    
    @Override
    @Transactional(readOnly = true)
    public List<BaoCaoDoanhThuHangNgay> findByKhoangThoiGian(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        return baoCaoRepository.findByNgayBanBetween(ngayBatDau, ngayKetThuc);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BaoCaoDoanhThuHangNgay> findByKhoangThoiGianAndMaCH(LocalDate ngayBatDau, LocalDate ngayKetThuc, String maCH) {
        return baoCaoRepository.findByCuaHang_MaCHAndNgayBanBetween(maCH, ngayBatDau, ngayKetThuc);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BaoCaoDoanhThuHangNgay> findByKhoangThoiGianAndMaSP(LocalDate ngayBatDau, LocalDate ngayKetThuc, String maSP) {
        return baoCaoRepository.findBySanPham_MaSPAndNgayBanBetween(maSP, ngayBatDau, ngayKetThuc);
    }
    
    // ===== TỔNG HỢP DOANH THU =====
    
    @Override
    @Transactional(readOnly = true)
    public Double tongDoanhThuTheoNgay(LocalDate ngayBan) {
        return baoCaoRepository.tongDoanhThuTheoNgay(ngayBan).orElse(0.0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double tongDoanhThuTheoKhoangThoiGian(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        return baoCaoRepository.tongDoanhThuTheoKhoangThoiGian(ngayBatDau, ngayKetThuc).orElse(0.0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double tongDoanhThuTheoKhoangThoiGianAndMaCH(LocalDate ngayBatDau, LocalDate ngayKetThuc, String maCH) {
        List<BaoCaoDoanhThuHangNgay> baoCaos = findByKhoangThoiGianAndMaCH(ngayBatDau, ngayKetThuc, maCH);
        return baoCaos.stream()
                .mapToDouble(bc -> bc.getDoanhThu().doubleValue())
                .sum();
    }
    
    // ===== TỔNG HỢP LỢI NHUẬN =====
    
    @Override
    @Transactional(readOnly = true)
    public Double tongLoiNhuanTheoNgay(LocalDate ngayBan) {
        return baoCaoRepository.tongLoiNhuanTheoNgay(ngayBan).orElse(0.0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double tongLoiNhuanTheoKhoangThoiGian(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        return baoCaoRepository.tongLoiNhuanTheoKhoangThoiGian(ngayBatDau, ngayKetThuc).orElse(0.0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double tongLoiNhuanTheoKhoangThoiGianAndMaCH(LocalDate ngayBatDau, LocalDate ngayKetThuc, String maCH) {
        List<BaoCaoDoanhThuHangNgay> baoCaos = findByKhoangThoiGianAndMaCH(ngayBatDau, ngayKetThuc, maCH);
        return baoCaos.stream()
                .mapToDouble(bc -> bc.getLoiNhuan().doubleValue())
                .sum();
    }
    
    // ===== TOP SẢN PHẨM =====
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> topSanPhamBanChay(LocalDate ngayBatDau, LocalDate ngayKetThuc, int limit) {
        List<Object[]> results = baoCaoRepository.topSanPhamBanChay(ngayBatDau, ngayKetThuc);
        return results.stream()
                .limit(limit)
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("maSP", row[0]);
                    map.put("tongSoLuong", row[1]);
                    return map;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> topSanPhamLoiNhuanCao(LocalDate ngayBatDau, LocalDate ngayKetThuc, int limit) {
        List<Object[]> results = baoCaoRepository.topSanPhamLoiNhuanCao(ngayBatDau, ngayKetThuc);
        return results.stream()
                .limit(limit)
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("maSP", row[0]);
                    map.put("tongLoiNhuan", row[1]);
                    return map;
                })
                .collect(Collectors.toList());
    }
    
    // ===== BÁO CÁO THEO CỬA HÀNG =====
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> doanhThuTheoCuaHang(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        List<Object[]> results = baoCaoRepository.doanhThuTheoCuaHang(ngayBatDau, ngayKetThuc);
        return results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("maCH", row[0]);
                    map.put("tongDoanhThu", row[1]);
                    return map;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> loiNhuanTheoCuaHang(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        List<Object[]> results = baoCaoRepository.loiNhuanTheoCuaHang(ngayBatDau, ngayKetThuc);
        return results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("maCH", row[0]);
                    map.put("tongLoiNhuan", row[1]);
                    return map;
                })
                .collect(Collectors.toList());
    }
    
    // ===== CẬP NHẬT BÁO CÁO (GỌI FUNCTION SQL) =====
    
    @Override
    @Transactional
    public void capNhatBaoCaoDoanhThu(LocalDate ngayCapNhat) {
        // Gọi function SQL có sẵn: CapNhatBaoCaoDoanhThu(ngay_cap_nhat DATE)
        Query query = entityManager.createNativeQuery(
            "SELECT CapNhatBaoCaoDoanhThu(:ngayCapNhat)"
        );
        query.setParameter("ngayCapNhat", ngayCapNhat);
        // Function RETURNS VOID nhưng SELECT vẫn trả về một hàng 'void'.
        // Cần đọc (consume) kết quả thay vì executeUpdate để tránh lỗi JDBC.
        query.getSingleResult();
    }
    
    @Override
    @Transactional
    public void capNhatBaoCaoDoanhThuHomNay() {
        capNhatBaoCaoDoanhThu(LocalDate.now());
    }
    
    @Override
    @Transactional
    public void capNhatBaoCaoDoanhThuHomQua() {
        capNhatBaoCaoDoanhThu(LocalDate.now().minusDays(1));
    }
    
    @Override
    @Transactional
    public void capNhatBaoCaoDoanhThuTheoKhoangThoiGian(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        LocalDate currentDate = ngayBatDau;
        while (!currentDate.isAfter(ngayKetThuc)) {
            capNhatBaoCaoDoanhThu(currentDate);
            currentDate = currentDate.plusDays(1);
        }
    }
    
    // ===== THỐNG KÊ TỔNG QUAN =====
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> thongKeTongQuan(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        Map<String, Object> thongKe = new HashMap<>();
        
        Double tongDoanhThu = tongDoanhThuTheoKhoangThoiGian(ngayBatDau, ngayKetThuc);
        Double tongLoiNhuan = tongLoiNhuanTheoKhoangThoiGian(ngayBatDau, ngayKetThuc);
        
        thongKe.put("ngayBatDau", ngayBatDau);
        thongKe.put("ngayKetThuc", ngayKetThuc);
        thongKe.put("tongDoanhThu", tongDoanhThu);
        thongKe.put("tongLoiNhuan", tongLoiNhuan);
        thongKe.put("tyLeLoiNhuan", tongDoanhThu > 0 ? (tongLoiNhuan / tongDoanhThu) * 100 : 0);
        thongKe.put("topSanPhamBanChay", topSanPhamBanChay(ngayBatDau, ngayKetThuc, 5));
        thongKe.put("topSanPhamLoiNhuanCao", topSanPhamLoiNhuanCao(ngayBatDau, ngayKetThuc, 5));
        thongKe.put("doanhThuTheoCuaHang", doanhThuTheoCuaHang(ngayBatDau, ngayKetThuc));
        
        return thongKe;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> thongKeTongQuanTheoCuaHang(LocalDate ngayBatDau, LocalDate ngayKetThuc, String maCH) {
        Map<String, Object> thongKe = new HashMap<>();
        
        Double tongDoanhThu = tongDoanhThuTheoKhoangThoiGianAndMaCH(ngayBatDau, ngayKetThuc, maCH);
        Double tongLoiNhuan = tongLoiNhuanTheoKhoangThoiGianAndMaCH(ngayBatDau, ngayKetThuc, maCH);
        
        thongKe.put("ngayBatDau", ngayBatDau);
        thongKe.put("ngayKetThuc", ngayKetThuc);
        thongKe.put("maCH", maCH);
        thongKe.put("tongDoanhThu", tongDoanhThu);
        thongKe.put("tongLoiNhuan", tongLoiNhuan);
        thongKe.put("tyLeLoiNhuan", tongDoanhThu > 0 ? (tongLoiNhuan / tongDoanhThu) * 100 : 0);
        
        return thongKe;
    }
}
