package com.example.mini_supermarket.util;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Utility class để generate mã tự động cho các entity
 * Sử dụng SecureRandom để đảm bảo tính ngẫu nhiên và bảo mật
 */
public class CodeGenerator {
    
    private static final SecureRandom RANDOM = new SecureRandom();
    
    // Ký tự sử dụng để generate mã
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    /**
     * Generate mã nhà cung cấp: NCC + 7 ký tự = tổng 10 ký tự
     * Ví dụ: NCC001, NCCABC1, NCC12345
     */
    public static String generateMaNhaCungCap() {
        return "NCC" + generateRandomString(7);
    }
    
    /**
     * Generate mã nhân viên: NV + 8 ký tự = tổng 10 ký tự
     * Ví dụ: NV001, NVABC123, NV123456
     */
    public static String generateMaNhanVien() {
        return "NV" + generateRandomString(8);
    }
    
    /**
     * Generate mã khách hàng: KH + 8 ký tự = tổng 10 ký tự
     * Ví dụ: KH001, KHABC123, KH123456
     */
    public static String generateMaKhachHang() {
        return "KH" + generateRandomString(8);
    }
    
    /**
     * Generate mã loại sản phẩm: LSP + 7 ký tự = tổng 10 ký tự
     * Ví dụ: LSP001, LSPABC1, LSP12345
     */
    public static String generateMaLoaiSanPham() {
        return "LSP" + generateRandomString(7);
    }
    
    /**
     * Generate mã sản phẩm: SP + 8 ký tự = tổng 10 ký tự
     * Ví dụ: SP001, SPABC123, SP123456
     */
    public static String generateMaSanPham() {
        return "SP" + generateRandomString(8);
    }
    
    /**
     * Generate mã khuyến mãi: KM + 8 ký tự = tổng 10 ký tự
     * Ví dụ: KM001, KMABC123, KM123456
     */
    public static String generateMaKhuyenMai() {
        return "KM" + generateRandomString(8);
    }
    
    /**
     * Generate mã phương thức thanh toán: PTTT + 6 ký tự = tổng 10 ký tự
     * Ví dụ: PTTT001, PTTTABC, PTTT123
     */
    public static String generateMaPhuongThucThanhToan() {
        return "PTTT" + generateRandomString(6);
    }
    
    /**
     * Generate mã cửa hàng: CH + 8 ký tự = tổng 10 ký tự
     * Ví dụ: CH001, CHABC123, CH123456
     */
    public static String generateMaCuaHang() {
        return "CH" + generateRandomString(8);
    }
    
    /**
     * Generate mã kho: K + 9 ký tự = tổng 10 ký tự
     * Ví dụ: K001, KABC12345, K123456789
     */
    public static String generateMaKho() {
        return "K" + generateRandomString(9);
    }
    
    /**
     * Generate mã phiếu nhập hàng: PNH + 7 ký tự = tổng 10 ký tự
     * Ví dụ: PNH001, PNHABC1, PNH12345
     */
    public static String generateMaPhieuNhapHang() {
        return "PNH" + generateRandomString(7);
    }
    
    /**
     * Generate mã phiếu xuất kho: PXK + 7 ký tự = tổng 10 ký tự
     * Ví dụ: PXK001, PXKABC1, PXK12345
     */
    public static String generateMaPhieuXuatKho() {
        return "PXK" + generateRandomString(7);
    }
    
    /**
     * Generate mã hóa đơn: HD + 8 ký tự = tổng 10 ký tự
     * Ví dụ: HD001, HDABC123, HD123456
     */
    public static String generateMaHoaDon() {
        return "HD" + generateRandomString(8);
    }
    
    /**
     * Generate mã thanh toán: TT + 8 ký tự = tổng 10 ký tự
     * Ví dụ: TT001, TTABC123, TT123456
     */
    public static String generateMaThanhToan() {
        return "TT" + generateRandomString(8);
    }
    
    /**
     * Generate mã giỏ hàng: GH + 8 ký tự = tổng 10 ký tự
     * Ví dụ: GH001, GHABC123, GH123456
     */
    public static String generateMaGioHang() {
        return "GH" + generateRandomString(8);
    }
    
    /**
     * Generate mã người dùng: ND + 8 ký tự = tổng 10 ký tự
     * Ví dụ: ND001, NDABC123, ND123456
     */
    public static String generateMaNguoiDung() {
        return "ND" + generateRandomString(8);
    }
    
    /**
     * Generate mã ca làm việc: CLV + 7 ký tự = tổng 10 ký tự
     * Ví dụ: CLV001, CLVABC1, CLV12345
     */
    public static String generateMaCaLamViec() {
        return "CLV" + generateRandomString(7);
    }
    
    /**
     * Generate mã lịch làm việc: LLV + 7 ký tự = tổng 10 ký tự
     * Ví dụ: LLV001, LLVABC1, LLV12345
     */
    public static String generateMaLichLamViec() {
        return "LLV" + generateRandomString(7);
    }
    
    /**
     * Generate mã bảng lương: BL + 8 ký tự = tổng 10 ký tự
     * Ví dụ: BL001, BLABC123, BL123456
     */
    public static String generateMaBangLuong() {
        return "BL" + generateRandomString(8);
    }
    
    /**
     * Generate mã hình ảnh: HA + 8 ký tự = tổng 10 ký tự
     * Ví dụ: HA001, HAABC123, HA123456
     */
    public static String generateMaHinhAnh() {
        return "HA" + generateRandomString(8);
    }
    
    /**
     * Generate mã giá sản phẩm: GSP + 7 ký tự = tổng 10 ký tự
     * Ví dụ: GSP001, GSPABC1, GSP12345
     */
    public static String generateMaGiaSanPham() {
        return "GSP" + generateRandomString(7);
    }
    
    /**
     * Generate mã tồn kho chi tiết: TKC + 7 ký tự = tổng 10 ký tự
     * Ví dụ: TKC001, TKCABC1, TKC12345
     */
    public static String generateMaTonKhoChiTiet() {
        return "TKC" + generateRandomString(7);
    }
    
    /**
     * Generate mã thống kê báo cáo: TKB + 7 ký tự = tổng 10 ký tự
     * Ví dụ: TKB001, TKBABC1, TKB12345
     */
    public static String generateMaThongKeBaoCao() {
        return "TKB" + generateRandomString(7);
    }
    
    /**
     * Generate chuỗi ngẫu nhiên với độ dài chỉ định
     * Sử dụng cả chữ cái và số để tăng tính đa dạng
     * 
     * @param length Độ dài chuỗi cần generate
     * @return Chuỗi ngẫu nhiên
     */
    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        
        // Đảm bảo có ít nhất 1 chữ cái và 1 số
        if (length >= 2) {
            // Thêm 1 chữ cái đầu tiên
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(26))); // Chỉ chữ cái (0-25)
            
            // Thêm 1 số
            sb.append(ALPHANUMERIC.charAt(26 + RANDOM.nextInt(10))); // Chỉ số (26-35)
            
            // Generate các ký tự còn lại
            for (int i = 2; i < length; i++) {
                sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
            }
        } else {
            // Nếu length < 2, generate bình thường
            for (int i = 0; i < length; i++) {
                sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Generate mã với prefix tùy chỉnh
     * 
     * @param prefix Prefix của mã (ví dụ: "SP", "KH", "NV")
     * @param length Độ dài phần ngẫu nhiên
     * @return Mã hoàn chỉnh
     */
    public static String generateCustomCode(String prefix, int length) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalArgumentException("Prefix không được để trống");
        }
        if (length < 0) {
            throw new IllegalArgumentException("Length phải >= 0");
        }
        
        return prefix + generateRandomString(length);
    }
    
    /**
     * Generate mã với format: PREFIX + số thứ tự có leading zeros
     * 
     * @param prefix Prefix của mã
     * @param sequence Số thứ tự
     * @param totalLength Tổng độ dài (bao gồm cả prefix)
     * @return Mã với format PREFIX + số thứ tự
     */
    public static String generateSequentialCode(String prefix, int sequence, int totalLength) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalArgumentException("Prefix không được để trống");
        }
        if (totalLength <= prefix.length()) {
            throw new IllegalArgumentException("Tổng độ dài phải lớn hơn độ dài prefix");
        }
        
        int numberLength = totalLength - prefix.length();
        String format = "%0" + numberLength + "d";
        String numberPart = String.format(format, sequence);
        
        return prefix + numberPart;
    }
    
    /**
     * Generate mã sử dụng UUID (đảm bảo unique tuyệt đối)
     * 
     * @param prefix Prefix của mã
     * @param uuidLength Độ dài UUID sử dụng (tối đa 32)
     * @return Mã với UUID
     */
    public static String generateUUIDCode(String prefix, int uuidLength) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalArgumentException("Prefix không được để trống");
        }
        if (uuidLength <= 0 || uuidLength > 32) {
            throw new IllegalArgumentException("UUID length phải từ 1-32");
        }
        
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, uuidLength);
        return prefix + uuid.toUpperCase();
    }
}
