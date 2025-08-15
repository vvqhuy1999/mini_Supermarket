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
    
    // Tìm đơn hàng theo khách hàng (chỉ lấy chưa bị xóa)
    @Query("SELECT dh FROM DonHang dh WHERE dh.khachHang.maKH = :maKH AND dh.isdeleted = false")
    List<DonHang> findByKhachHang_MaKH(@Param("maKH") String maKH);
    
    // Tìm đơn hàng theo nhân viên (chỉ lấy chưa bị xóa)
    @Query("SELECT dh FROM DonHang dh WHERE dh.nhanVien.maNV = :maNV AND dh.isdeleted = false")
    List<DonHang> findByNhanVien_MaNV(@Param("maNV") String maNV);
    
    // Tìm đơn hàng theo trạng thái (chỉ lấy chưa bị xóa)
    @Query("SELECT dh FROM DonHang dh WHERE dh.trangThai = :trangThai AND dh.isdeleted = false")
    List<DonHang> findByTrangThai(@Param("trangThai") String trangThai);
    
    // Tìm đơn hàng theo ngày đặt hàng (chỉ lấy chưa bị xóa)
    @Query("SELECT dh FROM DonHang dh WHERE dh.ngayDatHang BETWEEN :tuNgay AND :denNgay AND dh.isdeleted = false")
    List<DonHang> findByNgayDatHangBetween(@Param("tuNgay") Timestamp tuNgay, @Param("denNgay") Timestamp denNgay);
    
    // Tìm đơn hàng theo khách hàng và trạng thái (chỉ lấy chưa bị xóa)
    @Query("SELECT dh FROM DonHang dh WHERE dh.khachHang.maKH = :maKH AND dh.trangThai = :trangThai AND dh.isdeleted = false")
    List<DonHang> findByKhachHang_MaKHAndTrangThai(@Param("maKH") String maKH, @Param("trangThai") String trangThai);
    
    // Đếm số đơn hàng theo trạng thái (chỉ đếm chưa bị xóa)
    @Query("SELECT COUNT(dh) FROM DonHang dh WHERE dh.trangThai = :trangThai AND dh.isdeleted = false")
    long countByTrangThai(@Param("trangThai") String trangThai);
    
    // Tìm đơn hàng chưa giao hàng (chỉ lấy chưa bị xóa)
    @Query("SELECT dh FROM DonHang dh WHERE dh.ngayGiaoHang IS NULL AND dh.trangThai != 'Canceled' AND dh.isdeleted = false")
    List<DonHang> findDonHangChuaGiao();
    
    // Tìm đơn hàng theo khách hàng và khoảng thời gian (chỉ lấy chưa bị xóa)
    @Query("SELECT dh FROM DonHang dh WHERE dh.khachHang.maKH = :maKH AND dh.ngayDatHang BETWEEN :tuNgay AND :denNgay AND dh.isdeleted = false")
    List<DonHang> findDonHangByKhachHangAndThoiGian(@Param("maKH") String maKH, 
                                                      @Param("tuNgay") Timestamp tuNgay, 
                                                      @Param("denNgay") Timestamp denNgay);
    
    // Override findAll để chỉ lấy chưa bị xóa
    @Override
    @Query("SELECT dh FROM DonHang dh WHERE dh.isdeleted = false")
    List<DonHang> findAll();
    
    // Override findById để chỉ lấy chưa bị xóa
    @Override
    @Query("SELECT dh FROM DonHang dh WHERE dh.maDH = :id AND dh.isdeleted = false")
    java.util.Optional<DonHang> findById(@Param("id") String id);
}
