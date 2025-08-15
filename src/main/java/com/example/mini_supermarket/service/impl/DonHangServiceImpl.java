package com.example.mini_supermarket.service.impl;

import com.example.mini_supermarket.entity.DonHang;
import com.example.mini_supermarket.repository.DonHangRepository;
import com.example.mini_supermarket.service.DonHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class DonHangServiceImpl implements DonHangService {

    @Autowired
    private DonHangRepository donHangRepository;

    @Override
    @Transactional
    public DonHang saveDonHang(DonHang donHang) {
        return donHangRepository.save(donHang);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DonHang> findDonHangByMaDH(String maDH) {
        return donHangRepository.findById(maDH);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> getAllDonHang() {
        return donHangRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> findDonHangByKhachHang(String maKH) {
        return donHangRepository.findByKhachHang_MaKH(maKH);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> findDonHangByNhanVien(String maNV) {
        return donHangRepository.findByNhanVien_MaNV(maNV);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> findDonHangByTrangThai(String trangThai) {
        return donHangRepository.findByTrangThai(trangThai);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> findDonHangByThoiGian(Timestamp tuNgay, Timestamp denNgay) {
        return donHangRepository.findByNgayDatHangBetween(tuNgay, denNgay);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> findDonHangChuaGiao() {
        return donHangRepository.findDonHangChuaGiao();
    }

    @Override
    @Transactional
    public DonHang updateTrangThaiDonHang(String maDH, String trangThaiMoi) {
        Optional<DonHang> donHangOpt = donHangRepository.findById(maDH);
        if (donHangOpt.isPresent()) {
            DonHang donHang = donHangOpt.get();
            donHang.setTrangThai(trangThaiMoi);
            return donHangRepository.save(donHang);
        }
        throw new RuntimeException("Không tìm thấy đơn hàng với mã: " + maDH);
    }

    @Override
    @Transactional
    public DonHang updateNgayGiaoHang(String maDH, Timestamp ngayGiaoHang) {
        Optional<DonHang> donHangOpt = donHangRepository.findById(maDH);
        if (donHangOpt.isPresent()) {
            DonHang donHang = donHangOpt.get();
            donHang.setNgayGiaoHang(ngayGiaoHang);
            return donHangRepository.save(donHang);
        }
        throw new RuntimeException("Không tìm thấy đơn hàng với mã: " + maDH);
    }

    @Override
    @Transactional
    public void deleteDonHang(String maDH) {
        donHangRepository.deleteById(maDH);
    }

    @Override
    @Transactional(readOnly = true)
    public long countDonHangByTrangThai(String trangThai) {
        return donHangRepository.countByTrangThai(trangThai);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> findDonHangByKhachHangAndThoiGian(String maKH, Timestamp tuNgay, Timestamp denNgay) {
        return donHangRepository.findDonHangByKhachHangAndThoiGian(maKH, tuNgay, denNgay);
    }

    @Override
    @Transactional
    public void softDeleteById(String maDH) {
        Optional<DonHang> donHangOpt = findDonHangByMaDH(maDH);
        if (donHangOpt.isPresent()) {
            DonHang donHang = donHangOpt.get();
            donHang.setIsdeleted(true);
            donHangRepository.save(donHang);
        }
    }
}
