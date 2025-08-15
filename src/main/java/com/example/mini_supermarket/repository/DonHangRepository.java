package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, String> {
    
    // Tìm đơn hàng theo khách hàng
    List<DonHang> findByKhachHang_MaKH(String maKH);
    
    // Tìm đơn hàng theo nhân viên
    List<DonHang> findByNhanVien_MaNV(String maNV);
    
    // Tìm đơn hàng theo trạng thái
    List<DonHang> findByTrangThai(String trangThai);
    
    // Tìm đơn hàng theo ngày đặt hàng
    List<DonHang> findByNgayDatHangBetween(Timestamp tuNgay, Timestamp denNgay);
    
    // Tìm đơn hàng theo khách hàng và trạng thái
    List<DonHang> findByKhachHang_MaKHAndTrangThai(String maKH, String trangThai);
    
    // Đếm số đơn hàng theo trạng thái
    long countByTrangThai(String trangThai);
    
    // Tìm đơn hàng chưa giao hàng
    @Query("SELECT dh FROM DonHang dh WHERE dh.ngayGiaoHang IS NULL AND dh.trangThai != 'Canceled'")
    List<DonHang> findDonHangChuaGiao();
    
    // Tìm đơn hàng theo khách hàng và khoảng thời gian
    @Query("SELECT dh FROM DonHang dh WHERE dh.khachHang.maKH = :maKH AND dh.ngayDatHang BETWEEN :tuNgay AND :denNgay")
    List<DonHang> findDonHangByKhachHangAndThoiGian(@Param("maKH") String maKH, 
                                                      @Param("tuNgay") Timestamp tuNgay, 
                                                      @Param("denNgay") Timestamp denNgay);
}
