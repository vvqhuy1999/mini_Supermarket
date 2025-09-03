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
public class CreateInvoiceFromCartRequest {
    
    private String maKH; // Mã khách hàng
    
    private String maNV; // Mã nhân viên (required)
    
    private String maKM; // Mã khuyến mãi (optional)
    
    private String ghiChu; // Ghi chú hóa đơn (optional)
    
    private List<String> selectedCartItemIds; // Danh sách ID của các item trong giỏ hàng được chọn
    
    private Integer trangThai; // Trạng thái hóa đơn (mặc định 0 = Chờ thanh toán)
}
