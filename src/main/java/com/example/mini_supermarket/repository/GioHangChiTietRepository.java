package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {
    
    // Tìm tất cả giỏ hàng chi tiết (không còn soft-delete)
    @Query("SELECT g FROM GioHangChiTiet g")
    List<GioHangChiTiet> findAllActive();
    
    // Tìm giỏ hàng chi tiết theo ID
    @Query("SELECT g FROM GioHangChiTiet g WHERE g.maGHCT = :id")
    Optional<GioHangChiTiet> findActiveById(@Param("id") Integer id);
    
    // Tìm giỏ hàng chi tiết theo ID (bao gồm cả đã xóa)
    @Query("SELECT g FROM GioHangChiTiet g WHERE g.maGHCT = :id")
    Optional<GioHangChiTiet> findByIdIncludeDeleted(@Param("id") Integer id);

    // Tìm tất cả items theo khách hàng
    @Query("SELECT g FROM GioHangChiTiet g WHERE g.khachHang.maKH = :maKH")
    List<GioHangChiTiet> findByKhachHang_MaKHAndIsDeletedFalse(@Param("maKH") String maKH);

    // Tìm items theo khách hàng và trạng thái
    @Query("SELECT g FROM GioHangChiTiet g WHERE g.khachHang.maKH = :maKH AND g.trangThai = :trangThai ORDER BY g.ngayCapNhat DESC")
    List<GioHangChiTiet> findByKhachHang_MaKHAndTrangThaiAndIsDeletedFalseOrderByNgayCapNhatDesc(@Param("maKH") String maKH, @Param("trangThai") Integer trangThai);

    // Tìm items theo khách hàng, sắp xếp theo ngày cập nhật
    @Query("SELECT g FROM GioHangChiTiet g WHERE g.khachHang.maKH = :maKH ORDER BY g.ngayCapNhat DESC")
    List<GioHangChiTiet> findByKhachHang_MaKHAndIsDeletedFalseOrderByNgayCapNhatDesc(@Param("maKH") String maKH);

    // Tìm item cụ thể theo khách hàng và sản phẩm (để tránh duplicate)
    @Query("SELECT g FROM GioHangChiTiet g WHERE g.khachHang.maKH = :maKH AND g.sanPham.maSP = :maSP AND g.trangThai = :trangThai")
    Optional<GioHangChiTiet> findByKhachHangAndSanPhamAndTrangThai(@Param("maKH") String maKH, @Param("maSP") String maSP, @Param("trangThai") Integer trangThai);

    // Lấy danh sách items theo sản phẩm, chỉ lấy sản phẩm đang hiển thị
    @Query("SELECT g FROM GioHangChiTiet g WHERE g.sanPham.maSP = :maSP AND g.sanPham.trangThai = 1")
    List<GioHangChiTiet> findBySanPham_MaSP_Active(@Param("maSP") String maSP);

    // Lấy danh sách items theo khách hàng và trạng thái cụ thể
    @Query("SELECT g FROM GioHangChiTiet g WHERE g.khachHang.maKH = :maKH AND g.trangThai = :trangThai AND g.sanPham.trangThai = 1")
    List<GioHangChiTiet> findActiveCartItemsByCustomerAndStatus(@Param("maKH") String maKH, @Param("trangThai") Integer trangThai);
}
