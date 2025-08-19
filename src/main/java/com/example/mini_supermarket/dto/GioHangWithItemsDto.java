package com.example.mini_supermarket.dto;

import com.example.mini_supermarket.entity.GioHangChiTiet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GioHangWithItemsDto {
	private String maKH;
	private Integer trangThai;
	private String ghiChu;
	private java.sql.Timestamp ngayTao;
	private java.sql.Timestamp ngayCapNhat;
	private List<GioHangChiTiet> items;
	private BigDecimal tongTien;

	public BigDecimal getTongTien() {
		if (tongTien != null) return tongTien;
		if (items == null || items.isEmpty()) return BigDecimal.ZERO;
		return items.stream()
			.map(i -> i.getThanhTien() != null ? i.getThanhTien() : BigDecimal.ZERO)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}


