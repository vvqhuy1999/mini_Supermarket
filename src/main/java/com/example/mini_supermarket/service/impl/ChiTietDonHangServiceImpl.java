package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.entity.ChiTietDonHang;
import com.example.mini_supermarket.repository.ChiTietDonHangRepository;
import com.example.mini_supermarket.service.ChiTietDonHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ChiTietDonHangServiceImpl implements ChiTietDonHangService {

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Override
    @Transactional
    public ChiTietDonHang saveChiTietDonHang(ChiTietDonHang chiTietDonHang) {
        return chiTietDonHangRepository.save(chiTietDonHang);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChiTietDonHang> findChiTietDonHangByMaCTHD(Integer maCTHD) {
        return chiTietDonHangRepository.findById(maCTHD);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChiTietDonHang> getAllChiTietDonHang() {
        return chiTietDonHangRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChiTietDonHang> findChiTietDonHangByDonHang(String maDH) {
        return chiTietDonHangRepository.findByDonHang_MaDH(maDH);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChiTietDonHang> findChiTietDonHangBySanPham(String maSP) {
        return chiTietDonHangRepository.findBySanPham_MaSP(maSP);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChiTietDonHang> findChiTietDonHangByDonHangAndSanPham(String maDH, String maSP) {
        return Optional.ofNullable(chiTietDonHangRepository.findByDonHang_MaDHAndSanPham_MaSP(maDH, maSP));
    }

    @Override
    @Transactional
    public List<ChiTietDonHang> saveAllChiTietDonHang(List<ChiTietDonHang> danhSachChiTiet) {
        return chiTietDonHangRepository.saveAll(danhSachChiTiet);
    }

    @Override
    @Transactional
    public ChiTietDonHang updateSoLuong(Integer maCTHD, Integer soLuongMoi) {
        Optional<ChiTietDonHang> chiTietOpt = chiTietDonHangRepository.findById(maCTHD);
        if (chiTietOpt.isPresent()) {
            ChiTietDonHang chiTiet = chiTietOpt.get();
            chiTiet.setSoLuong(soLuongMoi);
            return chiTietDonHangRepository.save(chiTiet);
        }
        throw new RuntimeException("Không tìm thấy chi tiết đơn hàng với mã: " + maCTHD);
    }

    @Override
    @Transactional
    public ChiTietDonHang updateGiamGia(Integer maCTHD, BigDecimal giamGiaMoi) {
        Optional<ChiTietDonHang> chiTietOpt = chiTietDonHangRepository.findById(maCTHD);
        if (chiTietOpt.isPresent()) {
            ChiTietDonHang chiTiet = chiTietOpt.get();
            chiTiet.setGiamGia(giamGiaMoi);
            return chiTietDonHangRepository.save(chiTiet);
        }
        throw new RuntimeException("Không tìm thấy chi tiết đơn hàng với mã: " + maCTHD);
    }

    @Override
    @Transactional
    public void deleteChiTietDonHang(Integer maCTHD) {
        chiTietDonHangRepository.deleteById(maCTHD);
    }

    @Override
    @Transactional
    public void deleteAllChiTietDonHangByDonHang(String maDH) {
        List<ChiTietDonHang> danhSachChiTiet = chiTietDonHangRepository.findByDonHang_MaDH(maDH);
        chiTietDonHangRepository.deleteAll(danhSachChiTiet);
    }

    @Override
    @Transactional(readOnly = true)
    public long countChiTietDonHangByDonHang(String maDH) {
        return chiTietDonHangRepository.countByDonHang_MaDH(maDH);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChiTietDonHang> findChiTietDonHangCoGiamGia() {
        return chiTietDonHangRepository.findChiTietCoGiamGia();
    }
    
    @Override
    @Transactional
    public void softDeleteById(Integer maCTHD) {
        Optional<ChiTietDonHang> chiTietOpt = findChiTietDonHangByMaCTHD(maCTHD);
        if (chiTietOpt.isPresent()) {
            ChiTietDonHang chiTiet = chiTietOpt.get();
            chiTiet.setIsdeleted(true);
            chiTietDonHangRepository.save(chiTiet);
        }
    }
}
