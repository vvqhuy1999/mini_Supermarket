package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.config.VNPayConfig;
import com.example.mini_supermarket.entity.HoaDon;
import com.example.mini_supermarket.entity.PhuongThucThanhToan;
import com.example.mini_supermarket.entity.ThanhToan;
import com.example.mini_supermarket.repository.HoaDonRepository;
import com.example.mini_supermarket.repository.PhuongThucThanhToanRepository;
import com.example.mini_supermarket.service.ThanhToanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/api/thanhtoan")
@CrossOrigin(origins = "*")
@Tag(name = "Thanh toán", description = "API quản lý thanh toán")
public class ThanhToanRestController {

    @Autowired
    private ThanhToanService thanhToanService;

    @Autowired
    private VNPayConfig vnpayConfig;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Operation(summary = "Lấy tất cả thanh toán", description = "Trả về danh sách tất cả thanh toán chưa bị xóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ThanhToan.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping
    public ResponseEntity<List<ThanhToan>> getAllThanhToan() {
        try {
            List<ThanhToan> thanhToans = thanhToanService.findAllActive();
            return new ResponseEntity<>(thanhToans, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy thanh toán theo ID", description = "Trả về thông tin thanh toán theo ID (chỉ lấy thanh toán chưa bị xóa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy thanh toán",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ThanhToan.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thanh toán"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ThanhToan> getThanhToanById(
            @Parameter(description = "ID của thanh toán", required = true) @PathVariable Integer id) {
        try {
            ThanhToan thanhToan = thanhToanService.findActiveById(id);
            if (thanhToan != null) {
                return new ResponseEntity<>(thanhToan, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Tạo thanh toán mới", description = "Tạo một giao dịch thanh toán mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo thanh toán thành công",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ThanhToan.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping
    public ResponseEntity<ThanhToan> createThanhToan(@RequestBody ThanhToan thanhToan) {
        try {
            thanhToan.setIsDeleted(false);
            ThanhToan savedThanhToan = thanhToanService.save(thanhToan);
            return new ResponseEntity<>(savedThanhToan, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật thanh toán", description = "Cập nhật thông tin thanh toán theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ThanhToan.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thanh toán"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ThanhToan> updateThanhToan(
            @Parameter(description = "ID của thanh toán", required = true) @PathVariable Integer id,
            @RequestBody ThanhToan thanhToan) {
        try {
            ThanhToan existingThanhToan = thanhToanService.findActiveById(id);
            if (existingThanhToan != null) {
                thanhToan.setMaTT(id);
                thanhToan.setIsDeleted(false);
                ThanhToan updatedThanhToan = thanhToanService.save(thanhToan);
                return new ResponseEntity<>(updatedThanhToan, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa thanh toán", description = "Xóa mềm thanh toán (đánh dấu isDeleted = true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thanh toán"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteThanhToan(
            @Parameter(description = "ID của thanh toán", required = true) @PathVariable Integer id) {
        try {
            ThanhToan thanhToan = thanhToanService.findActiveById(id);
            if (thanhToan != null) {
                thanhToanService.softDeleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Tạo thanh toán VNPay", description = "Tạo giao dịch thanh toán VNPay và trả về URL thanh toán")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo URL thanh toán thành công",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping("/vnpay")
    public ResponseEntity<Map<String, String>> createVNPayPayment(
            @RequestBody Map<String, String> requestParams,
            HttpServletRequest request) {
        try {
            String vnp_OrderInfo = requestParams.get("vnp_OrderInfo");
            String orderType = requestParams.get("ordertype");
            String amountStr = requestParams.get("amount");
            if (vnp_OrderInfo == null || orderType == null || amountStr == null) {
                return new ResponseEntity<>(Map.of("code", "99", "message", "Thiếu tham số bắt buộc"), HttpStatus.BAD_REQUEST);
            }

            // Kiểm tra và định dạng số tiền
            int amount;
            try {
                amount = Integer.parseInt(amountStr);
                if (amount <= 0) {
                    return new ResponseEntity<>(Map.of("code", "98", "message", "Số tiền phải lớn hơn 0"), HttpStatus.BAD_REQUEST);
                }
                amount *= 100; // Chuyển sang định dạng VNPay (VND * 100)
            } catch (NumberFormatException e) {
                return new ResponseEntity<>(Map.of("code", "98", "message", "Số tiền không hợp lệ"), HttpStatus.BAD_REQUEST);
            }

            // Kiểm tra hóa đơn
            String maHDStr = requestParams.getOrDefault("maHD", "0");
            Integer maHD;
            try {
                maHD = Integer.valueOf(maHDStr);
            } catch (NumberFormatException e) {
                return new ResponseEntity<>(Map.of("code", "97", "message", "Mã hóa đơn không hợp lệ"), HttpStatus.BAD_REQUEST);
            }
            Optional<HoaDon> hoaDonOpt = hoaDonRepository.findActiveById(maHD);
            if (!hoaDonOpt.isPresent()) {
                return new ResponseEntity<>(Map.of("code", "97", "message", "Hóa đơn không tồn tại hoặc đã bị xóa"), HttpStatus.BAD_REQUEST);
            }

            // Kiểm tra phương thức thanh toán
            Optional<PhuongThucThanhToan> ptttOpt = phuongThucThanhToanRepository.findActiveById("PTTT006"); // ID cố định cho VNPay
            if (!ptttOpt.isPresent()) {
                return new ResponseEntity<>(Map.of("code", "96", "message", "Phương thức thanh toán VNPay không được cấu hình"), HttpStatus.BAD_REQUEST);
            }

            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_TxnRef = vnpayConfig.getRandomNumber(8);
            String vnp_IpAddr = vnpayConfig.getIpAddress(request);

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnpayConfig.getVnp_TmnCode());
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_OrderType", orderType);
            vnp_Params.put("vnp_Locale", requestParams.getOrDefault("language", "vn"));
            vnp_Params.put("vnp_ReturnUrl", vnpayConfig.getVnp_ReturnUrl());
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            String bankCode = requestParams.get("bankcode");
            if (bankCode != null && !bankCode.isEmpty()) {
                vnp_Params.put("vnp_BankCode", bankCode);
            }

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            vnp_Params.put("vnp_Bill_Mobile", requestParams.getOrDefault("txt_billing_mobile", ""));
            vnp_Params.put("vnp_Bill_Email", requestParams.getOrDefault("txt_billing_email", ""));
            String fullName = requestParams.getOrDefault("txt_billing_fullname", "").trim();
            if (!fullName.isEmpty()) {
                int idx = fullName.indexOf(' ');
                if (idx > 0) {
                    String firstName = fullName.substring(0, idx);
                    String lastName = fullName.substring(fullName.lastIndexOf(' ') + 1);
                    vnp_Params.put("vnp_Bill_FirstName", firstName);
                    vnp_Params.put("vnp_Bill_LastName", lastName);
                }
            }
            vnp_Params.put("vnp_Bill_Address", requestParams.getOrDefault("txt_inv_addr1", ""));
            vnp_Params.put("vnp_Bill_City", requestParams.getOrDefault("txt_bill_city", ""));
            vnp_Params.put("vnp_Bill_Country", requestParams.getOrDefault("txt_bill_country", ""));
            vnp_Params.put("vnp_Bill_State", requestParams.getOrDefault("txt_bill_state", ""));
            vnp_Params.put("vnp_Inv_Phone", requestParams.getOrDefault("txt_inv_mobile", ""));
            vnp_Params.put("vnp_Inv_Email", requestParams.getOrDefault("txt_inv_email", ""));
            vnp_Params.put("vnp_Inv_Customer", requestParams.getOrDefault("txt_inv_customer", ""));
            vnp_Params.put("vnp_Inv_Address", requestParams.getOrDefault("txt_inv_addr1", ""));
            vnp_Params.put("vnp_Inv_Company", requestParams.getOrDefault("txt_inv_company", ""));
            vnp_Params.put("vnp_Inv_Taxcode", requestParams.getOrDefault("txt_inv_taxcode", ""));
            vnp_Params.put("vnp_Inv_Type", requestParams.getOrDefault("cbo_inv_type", ""));

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            for (String fieldName : fieldNames) {
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = vnpayConfig.hmacSHA512(vnpayConfig.getVnp_HashSecret(), hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = vnpayConfig.getVnp_PayUrl() + "?" + queryUrl;

            ThanhToan thanhToan = new ThanhToan();
            thanhToan.setSoTienThanhToan(new BigDecimal(String.valueOf(amount))); // Sử dụng amount đã nhân 100
            thanhToan.setNgayGioTT(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            thanhToan.setPhuongThucThanhToan(ptttOpt.get());
            thanhToan.setHoaDon(hoaDonOpt.get());
            thanhToan.setGhiChu(vnp_OrderInfo);
            thanhToan.setMaGiaoDichNganHang(vnp_TxnRef);
            thanhToan.setTrangThaiTT(0); // Chờ xử lý

            thanhToanService.createVNPayPayment(thanhToan, vnp_Params);

            Map<String, String> response = new HashMap<>();
            response.put("code", "00");
            response.put("message", "success");
            response.put("data", paymentUrl);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(Map.of("code", "99", "message", "Lỗi server: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xử lý callback từ VNPAY", description = "Cập nhật và xác nhận trạng thái thanh toán từ VNPAY")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xác nhận thành công",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/vnpay/return")
    public ResponseEntity<Map<String, String>> handleVNPayReturn(
            @RequestParam Map<String, String> vnpParams,
            HttpServletRequest request) {
        try {
            // Lấy và lưu các tham số từ request
            Map<String, String> fields = new HashMap<>();
            for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
                String fieldName = params.nextElement();
                String fieldValue = request.getParameter(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    fields.put(fieldName, fieldValue);
                    System.out.println("Param: " + fieldName + " = " + fieldValue); // Log từng tham số
                }
            }
            System.out.println("Received all params: " + fields);

            if (fields.isEmpty()) {
                System.out.println("No parameters received from VNPay");
                return new ResponseEntity<>(Map.of("message", "Không nhận được dữ liệu từ VNPay"), HttpStatus.BAD_REQUEST);
            }

            String vnp_SecureHash = fields.get("vnp_SecureHash");
            if (vnp_SecureHash == null || vnp_SecureHash.isEmpty()) {
                System.out.println("Missing vnp_SecureHash");
                return new ResponseEntity<>(Map.of("message", "Thiếu chữ ký bảo mật vnp_SecureHash"), HttpStatus.BAD_REQUEST);
            }

            fields.remove("vnp_SecureHashType");
            fields.remove("vnp_SecureHash");

            // Kiểm tra chữ ký bảo mật
            String signValue = vnpayConfig.hmacSHA512(vnpayConfig.getVnp_HashSecret(), hashAllFields(fields));
            System.out.println("Calculated signValue: " + signValue);
            System.out.println("Received vnp_SecureHash: " + vnp_SecureHash);

            if (signValue == null || signValue.isEmpty()) {
                System.out.println("Cannot create secure hash");
                return new ResponseEntity<>(Map.of("message", "Không thể tạo chữ ký bảo mật"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (!signValue.equals(vnp_SecureHash)) {
                System.out.println("Signature mismatch");
                return new ResponseEntity<>(Map.of("message", "Chu ky khong hop le"), HttpStatus.BAD_REQUEST);
            }

            String vnp_ResponseCode = fields.get("vnp_ResponseCode");
            String vnp_TxnRef = fields.get("vnp_TxnRef");
            Map<String, String> response = new HashMap<>();

            if ("00".equals(vnp_ResponseCode)) {
                // Tìm và cập nhật trạng thái thanh toán trong cơ sở dữ liệu
                ThanhToan thanhToan = thanhToanService.findByMaGiaoDichNganHang(vnp_TxnRef);
                if (thanhToan != null) {
                    thanhToan.setTrangThaiTT(1); // Thành công
                    thanhToanService.update(thanhToan);

                    String vnpAmount = fields.get("vnp_Amount");
                    BigDecimal originalAmount = thanhToan.getSoTienThanhToan();
                    System.out.println("vnp_Amount: " + vnpAmount + ", Original amount: " + originalAmount + ", SoTienThanhToan from DB: " + thanhToan.getSoTienThanhToan());
                    if (vnpAmount != null) {
                        try {
                            BigDecimal vnpAmountBigDecimal = new BigDecimal(vnpAmount);
                            if (originalAmount.compareTo(vnpAmountBigDecimal) != 0) {
                                System.out.println("Amount mismatch - Debug: vnpAmount = " + vnpAmountBigDecimal + ", originalAmount = " + originalAmount);
                                return new ResponseEntity<>(Map.of("message", "Số tiền không khớp"), HttpStatus.BAD_REQUEST);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid vnp_Amount format: " + vnpAmount);
                            return new ResponseEntity<>(Map.of("message", "Số tiền từ VNPay không hợp lệ"), HttpStatus.BAD_REQUEST);
                        }
                    }
                }
                response.put("code", "00");
                response.put("message", "GD Thanh cong");
                response.put("orderCode", vnp_TxnRef);
                String totalValue = "0";
                if (fields.get("vnp_Amount") != null) {
                    try {
                        totalValue = String.valueOf(Integer.parseInt(fields.get("vnp_Amount")) / 100);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid vnp_Amount format: " + fields.get("vnp_Amount"));
                    }
                }
                response.put("total", totalValue);
                response.put("paymentMethod", "vnpay");
                response.put("redirectUrl", vnpayConfig.getVnp_ReturnUrl() != null ? vnpayConfig.getVnp_ReturnUrl() : "http://localhost:3000/payment-success");
            } else {
                // Cập nhật trạng thái thất bại nếu cần
                ThanhToan thanhToan = thanhToanService.findByMaGiaoDichNganHang(vnp_TxnRef);
                if (thanhToan != null) {
                    thanhToan.setTrangThaiTT(2); // Thất bại
                    thanhToanService.update(thanhToan);
                }
                response.put("code", "99");
                response.put("message", "GD Khong thanh cong");
                response.put("redirectUrl", "http://localhost:3000");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(Map.of("message", "Loi server: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                try {
                    hashData.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                            .append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                } catch (Exception e) {
                    hashData.append(fieldName).append('=').append(fieldValue);
                }
                if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                    hashData.append('&');
                }
            }
        }
        return hashData.toString();
    }
}