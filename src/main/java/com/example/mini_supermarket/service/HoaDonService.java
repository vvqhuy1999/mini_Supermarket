package com.example.mini_supermarket.service;

import com.example.mini_supermarket.entity.HoaDon;
import com.example.mini_supermarket.dto.CreateInvoiceFromCartRequest;
import com.example.mini_supermarket.dto.InvoiceCreatedResponse;
import com.example.mini_supermarket.dto.HoaDonFullDetailsDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HoaDonService {
    List<HoaDon> findAll();

    List<HoaDon> findAllActive(); // Chỉ lấy các record chưa bị xóa

    HoaDon findById(Integer id);

    HoaDon findActiveById(Integer id); // Chỉ lấy record chưa bị xóa

    HoaDon save(HoaDon hoaDon);

    void deleteById(Integer id); // Hard delete (giữ lại cho tương thích)

    void softDeleteById(Integer id); // Soft delete - set isDeleted = true

    HoaDon update(HoaDon hoaDon);
    
    // === METHODS MỚI - TẠO HÓA ĐƠN TỪ GIỎ HÀNG ===
    
    // Tạo hóa đơn từ các item trong giỏ hàng được chọn
    InvoiceCreatedResponse createInvoiceFromCart(CreateInvoiceFromCartRequest request);
    
    // Tạo hóa đơn mới và xóa giỏ hàng
    HoaDon createInvoiceAndClearCart(HoaDon hoaDon, List<Integer> cartItemIds);
    
    // Cập nhật trạng thái hóa đơn
    HoaDon updateTrangThai(Integer maHD, Integer trangThaiMoi);

    // Lấy danh sách hóa đơn theo khách hàng
    List<HoaDon> findActiveByCustomer(String maKH);
    
    // ===== OPTIMIZED METHODS - Temporarily disabled =====
    
    /*
    // Lấy danh sách hóa đơn tối ưu với pagination
    Page<HoaDonSummaryDTO> findAllActiveSummary(Pageable pageable);
    
    // Lấy hóa đơn theo khách hàng tối ưu với pagination
    Page<HoaDonSummaryDTO> findActiveByCustomerSummary(String maKH, Pageable pageable);
    
    // Lấy hóa đơn theo khách hàng tối ưu không pagination
    List<HoaDonSummaryDTO> findActiveByCustomerSummaryList(String maKH);
    */
    
    // Đếm số lượng hóa đơn theo trạng thái
    Long countByTrangThai(Integer trangThai);
    
    // Đếm số lượng hóa đơn của khách hàng
    Long countByCustomer(String maKH);
    
    // ===== ENHANCED METHODS =====
    
    // Tìm hóa đơn theo trạng thái
    List<HoaDon> findByTrangThai(Integer trangThai);
    
    // Tìm hóa đơn theo khách hàng và trạng thái
    List<HoaDon> findByCustomerAndStatus(String maKH, Integer trangThai);
    
    // Tìm hóa đơn theo khoảng ngày
    List<HoaDon> findByDateRange(String fromDate, String toDate);
    
    // Tìm hóa đơn theo khách hàng và khoảng ngày
    List<HoaDon> findByCustomerAndDateRange(String maKH, String fromDate, String toDate);
    
    // Hủy hóa đơn
    HoaDon cancelHoaDon(Integer maHD, String lyDoHuy);
    
    // Thống kê hóa đơn theo khách hàng
    Object getStatisticsByCustomer(String maKH);
    
    // Đếm hóa đơn theo khách hàng và trạng thái
    Object countByCustomerAndStatus(String maKH);
    
    // ===== FULL DETAILS METHODS =====
    
    // Lấy hóa đơn với chi tiết đầy đủ
    HoaDonFullDetailsDTO getHoaDonFullDetails(Integer maHD);
    
    // Lấy danh sách hóa đơn của khách hàng với chi tiết đầy đủ
    List<HoaDonFullDetailsDTO> getHoaDonFullDetailsByCustomer(String maKH);
} 