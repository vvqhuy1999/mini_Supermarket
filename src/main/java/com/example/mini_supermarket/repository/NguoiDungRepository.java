package com.example.mini_supermarket.repository;

import com.example.mini_supermarket.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, String> {
    
    // Tìm tất cả người dùng chưa bị xóa (isDeleted = false)
    @Query("SELECT n FROM NguoiDung n WHERE n.isDeleted = false")
    List<NguoiDung> findAllActive();
    
    // Tìm người dùng theo ID và chưa bị xóa
    @Query("SELECT n FROM NguoiDung n WHERE n.maNguoiDung = :id AND n.isDeleted = false")
    Optional<NguoiDung> findActiveById(@Param("id") String id);
    
    // Tìm người dùng theo ID (bao gồm cả đã xóa)
    @Query("SELECT n FROM NguoiDung n WHERE n.maNguoiDung = :id")
    Optional<NguoiDung> findByIdIncludeDeleted(@Param("id") String id);
    
    // Kiểm tra email đã tồn tại chưa (chưa bị xóa)
    @Query("SELECT COUNT(n) > 0 FROM NguoiDung n WHERE n.email = :email AND n.isDeleted = false")
    boolean existsByEmail(@Param("email") String email);
    
    // Tìm người dùng theo email (chưa bị xóa)
    @Query("SELECT n FROM NguoiDung n WHERE n.email = :email AND n.isDeleted = false")
    Optional<NguoiDung> findByEmail(@Param("email") String email);
    
    // Tìm người dùng theo email (bao gồm cả đã xóa)
    @Query("SELECT n FROM NguoiDung n WHERE n.email = :email")
    Optional<NguoiDung> findByEmailIncludeDeleted(@Param("email") String email);
    
    // Kiểm tra mã người dùng đã tồn tại chưa (chưa bị xóa)
    @Query("SELECT COUNT(n) > 0 FROM NguoiDung n WHERE n.maNguoiDung = :maNguoiDung AND n.isDeleted = false")
    boolean existsByMaNguoiDung(@Param("maNguoiDung") String maNguoiDung);
    
    // Kiểm tra mã người dùng đã tồn tại chưa (bao gồm cả đã xóa)
    @Query("SELECT COUNT(n) > 0 FROM NguoiDung n WHERE n.maNguoiDung = :maNguoiDung")
    boolean existsByMaNguoiDungIncludeDeleted(@Param("maNguoiDung") String maNguoiDung);
    
    // Tìm người dùng theo sub (OAuth2 ID) - chưa bị xóa
    @Query("SELECT n FROM NguoiDung n WHERE n.sub = :sub AND n.isDeleted = false")
    Optional<NguoiDung> findBySub(@Param("sub") String sub);
    
    // Tìm người dùng theo sub (OAuth2 ID) - bao gồm cả đã xóa
    @Query("SELECT n FROM NguoiDung n WHERE n.sub = :sub")
    Optional<NguoiDung> findBySubIncludeDeleted(@Param("sub") String sub);
    
    // Kiểm tra sub đã tồn tại chưa (chưa bị xóa)
    @Query("SELECT COUNT(n) > 0 FROM NguoiDung n WHERE n.sub = :sub AND n.isDeleted = false")
    boolean existsBySub(@Param("sub") String sub);
    
    // Kiểm tra sub đã tồn tại chưa (bao gồm cả đã xóa)
    @Query("SELECT COUNT(n) > 0 FROM NguoiDung n WHERE n.sub = :sub")
    boolean existsBySubIncludeDeleted(@Param("sub") String sub);
    
    // Tìm tất cả người dùng OAuth2 (có sub) - chưa bị xóa
    @Query("SELECT n FROM NguoiDung n WHERE n.sub IS NOT NULL AND n.sub != '' AND n.isDeleted = false")
    List<NguoiDung> findAllOAuth2Users();
} 
