package com.example.mini_supermarket.service;

import com.example.mini_supermarket.dto.ThongKeDoanhThuDTO;
import com.example.mini_supermarket.dto.ThongKeKhachHangTiemNangDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * Service để tự động tính toán và quản lý thống kê doanh thu
 */
public interface ThongKeDoanhThuTuDongService {
    
    /**
     * Tính toán doanh thu theo tháng và lưu vào bảng thongkebaocao
     * @param thang Tháng cần tính (1-12)
     * @param nam Năm cần tính
     * @param maCH Mã cửa hàng (null nếu tính tổng hệ thống)
     */
    void tinhDoanhThuThang(int thang, int nam, String maCH);
    
    /**
     * Tính toán khách hàng tiềm năng theo tháng và lưu vào bảng thongkebaocao
     * @param thang Tháng cần tính (1-12)
     * @param nam Năm cần tính
     * @param maCH Mã cửa hàng (null nếu tính tổng hệ thống)
     */
    void tinhKhachHangTiemNang(int thang, int nam, String maCH);
    
    /**
     * Tính lại toàn bộ thống kê cho một tháng
     * @param thang Tháng cần tính lại
     * @param nam Năm cần tính lại
     */
    void tinhLaiThongKeThang(int thang, int nam);
    
    /**
     * Lấy thống kê doanh thu theo tháng
     * @param thang Tháng
     * @param nam Năm
     * @param maCH Mã cửa hàng (null để lấy tất cả)
     * @return Danh sách thống kê doanh thu
     */
    List<ThongKeDoanhThuDTO> layThongKeDoanhThuThang(int thang, int nam, String maCH);
    
    /**
     * Lấy thống kê khách hàng tiềm năng theo tháng
     * @param thang Tháng
     * @param nam Năm
     * @param maCH Mã cửa hàng (null để lấy tất cả)
     * @return Danh sách thống kê khách hàng tiềm năng
     */
    List<ThongKeKhachHangTiemNangDTO> layThongKeKhachHangTiemNang(int thang, int nam, String maCH);
    
    /**
     * So sánh doanh thu giữa các tháng
     * @param soThang Số tháng gần nhất cần so sánh
     * @param maCH Mã cửa hàng (null để lấy tổng hệ thống)
     * @return Danh sách so sánh doanh thu
     */
    List<ThongKeDoanhThuDTO> soSanhDoanhThuTheoThang(int soThang, String maCH);
    
    /**
     * Lấy top khách hàng tiềm năng trong tháng
     * @param thang Tháng
     * @param nam Năm
     * @param maCH Mã cửa hàng
     * @param limit Số lượng top khách hàng
     * @return Danh sách top khách hàng tiềm năng
     */
    List<ThongKeKhachHangTiemNangDTO> layTopKhachHangTiemNang(int thang, int nam, String maCH, int limit);
    
    /**
     * Tự động tính toán thống kê khi có hóa đơn mới
     * Được gọi từ trigger hoặc event
     * @param maHoaDon Mã hóa đơn vừa được tạo/cập nhật
     */
    void tuDongTinhThongKe(Long maHoaDon);
}