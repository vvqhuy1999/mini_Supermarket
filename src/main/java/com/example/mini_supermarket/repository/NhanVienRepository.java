package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NhanVienRepository extends JpaRepository<NhanVien, String> {
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END " +
            "FROM NhanVien n WHERE n.maNV = :maNV AND n.ngaySinh = :ngaySinh")
    boolean existsByMaNVAndNgaySinh(@Param("maNV") String maNV, @Param("ngaySinh") java.time.LocalDate ngaySinh);

    // Tìm tất cả nhân viên chưa bị xóa (isDeleted = false)
    @Query("SELECT n FROM NhanVien n WHERE n.isDeleted = false")
    List<NhanVien> findAllActive();

    // Tìm nhân viên theo ID và chưa bị xóa
    @Query("SELECT n FROM NhanVien n WHERE n.maNV = :id AND n.isDeleted = false")
    Optional<NhanVien> findActiveById(@Param("id") String id);

    // Tìm nhân viên theo ID (bao gồm cả đã xóa)
    @Query("SELECT n FROM NhanVien n WHERE n.maNV = :id")
    Optional<NhanVien> findByIdIncludeDeleted(@Param("id") String id);
}