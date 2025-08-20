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
public class OrderCreatedResponse {
    
    private String maDH; // Mã đơn hàng
    
    private String maKH; // Mã khách hàng
    
    private String maNV; // Mã nhân viên (nếu có)
    
    private Timestamp ngayDatHang; // Ngày đặt hàng
    
    private String diaChiGiaoHang; // Địa chỉ giao hàng
    
    private String trangThai; // Trạng thái đơn hàng
    
    private BigDecimal tongTien; // Tổng tiền đơn hàng
    
    private List<OrderItemResponse> items; // Danh sách các item
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private String maSP; // Mã sản phẩm
        private String tenSP; // Tên sản phẩm
        private Integer soLuong; // Số lượng
        private BigDecimal donGia; // Đơn giá
        private BigDecimal thanhTien; // Thành tiền
    }
}
