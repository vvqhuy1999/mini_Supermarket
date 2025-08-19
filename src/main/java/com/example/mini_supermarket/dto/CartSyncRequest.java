package com.example.mini_supermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartSyncRequest {
	private String maKH; // Optional: nếu frontend biết mã KH
	private List<CartSyncItemDto> items; // Dữ liệu từ localStorage
}


