package com.example.mini_supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceCreatedResponse {
    
    private Integer maHD; // Mã hóa đơn
    
    private String maKH; // Mã khách hàng
    
    private String maNV; // Mã nhân viên
    
    private String maKM; // Mã khuyến mãi (nếu có)
    
    private Timestamp ngayLap; // Ngày lập hóa đơn
    
    private BigDecimal tongTienHang; // Tổng tiền hàng
    
    private BigDecimal tienGiamGia; // Tiền giảm giá
    
    private BigDecimal tongTien; // Tổng tiền cuối cùng
    
    private Integer trangThai; // Trạng thái hóa đơn
    
    private List<InvoiceItemResponse> items; // Danh sách các item
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InvoiceItemResponse {
        private String maSP; // Mã sản phẩm
        private String tenSP; // Tên sản phẩm
        private Integer soLuong; // Số lượng
        private BigDecimal donGia; // Đơn giá
        private BigDecimal thanhTien; // Thành tiền
    }
}
