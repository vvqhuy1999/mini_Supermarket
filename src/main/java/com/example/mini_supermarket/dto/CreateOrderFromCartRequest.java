package com.example.mini_supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderFromCartRequest {
    
    private String maKH; // Mã khách hàng
    
    private String maNV; // Mã nhân viên (optional)
    
    private String diaChiGiaoHang; // Địa chỉ giao hàng (required nếu không có trong KhachHang)
    
    private List<String> selectedCartItemIds; // Danh sách ID của các item trong giỏ hàng được chọn
    
    private String ghiChu; // Ghi chú đơn hàng (optional)
}
