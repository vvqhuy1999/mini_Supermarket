package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.config.VNPayConfig;
import com.example.mini_supermarket.entity.HoaDon;
import com.example.mini_supermarket.entity.PhuongThucThanhToan;
import com.example.mini_supermarket.entity.ThanhToan;
import com.example.mini_supermarket.repository.HoaDonRepository;
import com.example.mini_supermarket.repository.PhuongThucThanhToanRepository;
import com.example.mini_supermarket.service.ThanhToanService;
import com.example.mini_supermarket.service.HoaDonService;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

import java.util.List;

@RestController
@RequestMapping("/api/thanhtoan")
@CrossOrigin(origins = "*")
@Tag(name = "Thanh toán", description = "API quản lý thanh toán")
public class ThanhToanRestController {

    @Autowired
    private ThanhToanService thanhToanService;
    @Autowired
    private VNPayConfig vnpayConfig; // Tiêm instance của VNPayConfig

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Autowired
    private HoaDonService hoaDonService;

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
            thanhToan.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
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
                thanhToan.setIsDeleted(false); // Đảm bảo không bị đánh dấu là đã xóa
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
            Optional<PhuongThucThanhToan> ptttOpt = phuongThucThanhToanRepository.findActiveById("PTTT006");
            if (!ptttOpt.isPresent()) {
                return new ResponseEntity<>(Map.of("code", "96", "message", "Phương thức thanh toán VNPay không được cấu hình"), HttpStatus.BAD_REQUEST);
            }

            // Tham số VNPAY theo phiên bản 2.1.0
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

            // Tạo thời gian
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            // Thời gian hết hạn (15 phút)
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            // Thêm các tham số billing tùy chọn
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

            // ✅ Build hashData và querystring theo phiên bản 2.1.0
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    // ✅ Build hash data - PHẢI encode cả key và value (phiên bản 2.1.0)
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String queryUrl = query.toString();

            // ✅ Tạo vnp_SecureHash bằng HMACSHA512 (phiên bản 2.1.0)
            String vnp_SecureHash = vnpayConfig.hmacSHA512(vnpayConfig.getVnp_HashSecret(), hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = vnpayConfig.getVnp_PayUrl() + "?" + queryUrl;

            // Lưu thông tin thanh toán vào database
            ThanhToan thanhToan = new ThanhToan();
            thanhToan.setSoTienThanhToan(new BigDecimal(amountStr));
            thanhToan.setNgayGioTT(Timestamp.valueOf(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
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

    // ✅ Method hmacSHA512 cần có trong VnpayConfig
    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
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
    public ResponseEntity<String> handleVNPayReturn(
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
                }
            }

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
            String vnp_TxnRef = request.getParameter("vnp_TxnRef");
            String vnp_Amount = request.getParameter("vnp_Amount");
            String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");
            System.out.println("🔎 VNPay Return - OrderInfo: " + vnp_OrderInfo);
            String vnp_TransactionStatus = request.getParameter("vnp_TransactionStatus"); // ✅ Tham số mới trong 2.1.0

            // Kiểm tra các tham số bắt buộc
            if (vnp_SecureHash == null || vnp_ResponseCode == null || vnp_TxnRef == null) {
                return ResponseEntity.badRequest().body("Missing required parameters");
            }

            // Loại bỏ vnp_SecureHash và vnp_SecureHashType khỏi fields để tính toán hash
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }

            // ✅ Kiểm tra chữ ký bảo mật theo phiên bản 2.1.0
            String signValue = vnpayConfig.hmacSHA512(vnpayConfig.getVnp_HashSecret(), hashAllFields(fields));

            if (!signValue.equals(vnp_SecureHash)) {
                return ResponseEntity.badRequest().body("Invalid signature");
            }

            // ✅ Tìm giao dịch theo vnp_TxnRef (không dùng findAll() - tối ưu performance)
            ThanhToan thanhToan = thanhToanService.findByMaGiaoDichNganHang(vnp_TxnRef);

            if (thanhToan == null) {
                return ResponseEntity.badRequest().body("Transaction not found");
            }

            // ✅ Kiểm tra số tiền
            long dbAmount = thanhToan.getSoTienThanhToan().multiply(new BigDecimal("100")).longValue();
            long vnpAmount = Long.parseLong(vnp_Amount);

            if (dbAmount != vnpAmount) {
                return ResponseEntity.badRequest().body("Invalid amount");
            }

            // ✅ Kiểm tra trạng thái giao dịch đã được xử lý chưa
            if (thanhToan.getTrangThaiTT() != 0) { // Đã xử lý rồi
                if (thanhToan.getTrangThaiTT() == 1) { // Đã thành công
                    // Giao dịch đã thành công trước đó, trả về thông báo thành công
                    System.out.println("ℹ️ Giao dịch " + vnp_TxnRef + " đã được xử lý thành công trước đó");
                    
                    // Trả về HTML page để redirect người dùng
                    String successHtml = """
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <title>Thanh toán thành công</title>
                                <meta charset="UTF-8">
                                <style>
                                    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }
                                    .success { color: #28a745; font-size: 24px; margin-bottom: 20px; }
                                    .redirect { color: #6c757d; font-size: 16px; }
                                </style>
                            </head>
                            <body>
                                <div class="success">✅ Thanh toán thành công!</div>
                                <div class="redirect">Giao dịch đã được xử lý trước đó</div>
                                <div class="redirect">Đang chuyển hướng...</div>
                                <script>
                                    setTimeout(function() {
                                        window.location.href = 'http://localhost:3000/payment-success';
                                    }, 2000);
                                </script>
                            </body>
                            </html>
                            """;
                    return ResponseEntity.ok()
                            .contentType(org.springframework.http.MediaType.TEXT_HTML)
                            .body(successHtml);
                } else { // Đã thất bại
                    // Giao dịch đã thất bại trước đó, trả về thông báo thất bại
                    System.out.println("ℹ️ Giao dịch " + vnp_TxnRef + " đã được xử lý thất bại trước đó");
                    
                    // Trả về HTML page để redirect người dùng
                    String failedHtml = """
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <title>Thanh toán thất bại</title>
                                <meta charset="UTF-8">
                                <style>
                                    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }
                                    .failed { color: #dc3545; font-size: 24px; margin-bottom: 20px; }
                                    .redirect { color: #6c757d; font-size: 16px; }
                                </style>
                            </head>
                            <body>
                                <div class="failed">❌ Thanh toán thất bại!</div>
                                <div class="redirect">Giao dịch đã được xử lý trước đó</div>
                                <div class="redirect">Đang chuyển hướng về trang đơn hàng...</div>
                                <script>
                                    setTimeout(function() {
                                        window.location.href = 'http://localhost:3000/orders';
                                    }, 2000);
                                </script>
                            </body>
                            </html>
                            """;
                    return ResponseEntity.ok()
                            .contentType(org.springframework.http.MediaType.TEXT_HTML)
                            .body(failedHtml);
                }
            }

            // ✅ Xử lý theo vnp_ResponseCode và vnp_TransactionStatus (phiên bản 2.1.0)
            if ("00".equals(vnp_ResponseCode)) {
                // ✅ Kiểm tra vnp_TransactionStatus (tham số mới trong 2.1.0)
                if ("00".equals(vnp_TransactionStatus)) {
                    // Giao dịch thành công tại VNPAY
                    thanhToan.setTrangThaiTT(1); // Thành công
                    thanhToan.setGhiChu(thanhToan.getGhiChu() + " - Thanh toán thành công qua VNPAY");
                    thanhToanService.update(thanhToan);

                    // ✅ Cập nhật trạng thái hóa đơn nếu cần
                    HoaDon hoaDon = thanhToan.getHoaDon();

                    System.out.println("⚠️ Hóa đơn " + hoaDon.getMaHD() + " test trạng thai đó: " + hoaDon.getTrangThai());

                    if (hoaDon != null && hoaDon.getTrangThai() == 0) { // Chờ thanh toán
                        try {
                            // ✅ SỬA: Kiểm tra trạng thái trước khi cập nhật để tránh race condition
                            System.out.println("🔍 Kiểm tra trạng thái hóa đơn trước khi cập nhật...");
                            
                            // Lấy lại hóa đơn từ database để đảm bảo dữ liệu mới nhất
                            HoaDon freshHoaDon = hoaDonService.findById(hoaDon.getMaHD());
                            if (freshHoaDon != null && freshHoaDon.getTrangThai() == 0) {
                                // ✅ SỬA: Sử dụng repository trực tiếp để bypass business logic validation
                                System.out.println("🔍 Cập nhật trạng thái hóa đơn trực tiếp qua repository...");
                                freshHoaDon.setTrangThai(1); // Đã thanh toán
                                freshHoaDon.setNgaySua(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                                
                                // Sử dụng service để cập nhật và clear cache
                                hoaDonService.updateTrangThai(freshHoaDon.getMaHD(), 1);
                                System.out.println("✅ Cập nhật hóa đơn " + freshHoaDon.getMaHD() + " thành công: Chờ thanh toán -> Đã thanh toán");
                            } else if (freshHoaDon != null && freshHoaDon.getTrangThai() == 1) {
                                System.out.println("ℹ️ Hóa đơn " + freshHoaDon.getMaHD() + " đã được thanh toán trước đó (không cần cập nhật)");
                            } else if (freshHoaDon != null) {
                                System.out.println("⚠️ Hóa đơn " + freshHoaDon.getMaHD() + " có trạng thái " + freshHoaDon.getTrangThai() + " (không cần cập nhật)");
                            } else {
                                System.out.println("❌ Không thể tìm thấy hóa đơn " + hoaDon.getMaHD() + " trong database");
                            }
                        } catch (Exception e) {
                            // Nếu hóa đơn đã được thanh toán trước đó, ghi log và tiếp tục
                            System.out.println("⚠️ Hóa đơn " + hoaDon.getMaHD() + " đã được thanh toán trước đó: " + e.getMessage());
                            System.out.println("🔍 Stack trace: " + e.getStackTrace()[0]);
                            // Không cần throw exception, vì thanh toán vẫn thành công
                        }
                    } else if (hoaDon != null && hoaDon.getTrangThai() == 1) {
                        // Hóa đơn đã thanh toán rồi, ghi log
                        System.out.println("ℹ️ Hóa đơn " + hoaDon.getMaHD() + " đã được thanh toán trước đó");
                    } else if (hoaDon != null) {
                        // Hóa đơn có trạng thái khác, ghi log
                        System.out.println("ℹ️ Hóa đơn " + hoaDon.getMaHD() + " có trạng thái " + hoaDon.getTrangThai() + " (không cần cập nhật)");
                    }

                    // Trả về HTML page để redirect người dùng
                    String successHtml = """
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <title>Thanh toán thành công</title>
                                <meta charset="UTF-8">
                                <style>
                                    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }
                                    .success { color: #28a745; font-size: 24px; margin-bottom: 20px; }
                                    .redirect { color: #6c757d; font-size: 16px; }
                                </style>
                            </head>
                            <body>
                                <div class="success">✅ Thanh toán thành công!</div>
                                <div class="redirect">Đang chuyển hướng...</div>
                                <script>
                                    setTimeout(function() {
                                        window.location.href = 'http://localhost:3000/payment-success';
                                    }, 2000);
                                </script>
                            </body>
                            </html>
                            """;
                    return ResponseEntity.ok()
                            .contentType(org.springframework.http.MediaType.TEXT_HTML)
                            .body(successHtml);
                } else {
                    // ResponseCode = 00 nhưng TransactionStatus != 00
                    thanhToan.setTrangThaiTT(2); // Thất bại
                    thanhToan.setGhiChu(thanhToan.getGhiChu() + " - Giao dịch không thành công tại VNPAY. TransactionStatus: " + vnp_TransactionStatus);
                    thanhToanService.update(thanhToan);

                    // ✅ Cập nhật trạng thái hóa đơn nếu cần (chỉ khi thất bại)
                    HoaDon hoaDon = thanhToan.getHoaDon();
                    if (hoaDon != null && hoaDon.getTrangThai() == 0) { // Chờ thanh toán
                        try {
                            // ✅ SỬA: Kiểm tra trạng thái trước khi cập nhật để tránh race condition
                            System.out.println("🔍 Kiểm tra trạng thái hóa đơn trước khi cập nhật (thất bại)...");
                            
                            // Lấy lại hóa đơn từ database để đảm bảo dữ liệu mới nhất
                            HoaDon freshHoaDon = hoaDonService.findById(hoaDon.getMaHD());
                            if (freshHoaDon != null && freshHoaDon.getTrangThai() == 0) {
                                // ✅ SỬA: Khi giao dịch thất bại, giữ nguyên trạng thái 0 để khách hàng có thể thử lại
                                System.out.println("ℹ️ Hóa đơn " + freshHoaDon.getMaHD() + " giữ nguyên trạng thái 0 (chờ thanh toán) để khách hàng có thể thử lại");
                            } else if (freshHoaDon != null && freshHoaDon.getTrangThai() == 1) {
                                System.out.println("ℹ️ Hóa đơn " + freshHoaDon.getMaHD() + " đã được thanh toán trước đó (không cần cập nhật)");
                            } else if (freshHoaDon != null) {
                                System.out.println("⚠️ Hóa đơn " + freshHoaDon.getMaHD() + " có trạng thái " + freshHoaDon.getTrangThai() + " (không cần cập nhật)");
                            } else {
                                System.out.println("❌ Không thể tìm thấy hóa đơn " + hoaDon.getMaHD() + " trong database");
                            }
                        } catch (Exception e) {
                            // Nếu hóa đơn đã được xử lý trước đó, ghi log và tiếp tục
                            System.out.println("⚠️ Hóa đơn " + hoaDon.getMaHD() + " đã được xử lý trước đó: " + e.getMessage());
                            System.out.println("🔍 Stack trace: " + e.getStackTrace()[0]);
                        }
                    } else if (hoaDon != null && hoaDon.getTrangThai() == 1) {
                        // Hóa đơn đã thanh toán rồi, ghi log
                        System.out.println("ℹ️ Hóa đơn " + hoaDon.getMaHD() + " đã được thanh toán trước đó (không cần cập nhật)");
                    } else if (hoaDon != null) {
                        // Hóa đơn có trạng thái khác, ghi log
                        System.out.println("ℹ️ Hóa đơn " + hoaDon.getMaHD() + " có trạng thái " + hoaDon.getTrangThai() + " (không cần cập nhật)");
                    }

                    // Trả về HTML page để redirect người dùng
                    String failedHtml = """
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <title>Thanh toán thất bại</title>
                                <meta charset="UTF-8">
                                <style>
                                    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }
                                    .failed { color: #dc3545; font-size: 24px; margin-bottom: 20px; }
                                    .redirect { color: #6c757d; font-size: 16px; }
                                </style>
                            </head>
                            <body>
                                <div class="failed">❌ Thanh toán thất bại!</div>
                                <div class="redirect">Đang chuyển hướng về trang đơn hàng...</div>
                                <script>
                                    setTimeout(function() {
                                        window.location.href = 'http://localhost:3000/orders';
                                    }, 2000);
                                </script>
                            </body>
                            </html>
                            """;
                    return ResponseEntity.ok()
                            .contentType(org.springframework.http.MediaType.TEXT_HTML)
                            .body(failedHtml);
                }
            } else {
                // ✅ Xử lý các mã lỗi khác
                thanhToan.setTrangThaiTT(2); // Thất bại
                String errorMessage = getVNPayErrorMessage(vnp_ResponseCode);
                thanhToan.setGhiChu(thanhToan.getGhiChu() + " - " + errorMessage);
                thanhToanService.update(thanhToan);

                // ✅ Cập nhật trạng thái hóa đơn nếu cần (chỉ khi thất bại)
                HoaDon hoaDon = thanhToan.getHoaDon();
                if (hoaDon != null && hoaDon.getTrangThai() == 0) { // Chờ thanh toán
                    try {
                        // ✅ SỬA: Kiểm tra trạng thái trước khi cập nhật để tránh race condition
                        System.out.println("🔍 Kiểm tra trạng thái hóa đơn trước khi cập nhật (thất bại)...");
                        
                        // Lấy lại hóa đơn từ database để đảm bảo dữ liệu mới nhất
                        HoaDon freshHoaDon = hoaDonService.findById(hoaDon.getMaHD());
                        if (freshHoaDon != null && freshHoaDon.getTrangThai() == 0) {
                            // ✅ SỬA: Khi giao dịch thất bại, giữ nguyên trạng thái 0 để khách hàng có thể thử lại
                            System.out.println("ℹ️ Hóa đơn " + freshHoaDon.getMaHD() + " giữ nguyên trạng thái 0 (chờ thanh toán) để khách hàng có thể thử lại");
                        } else if (freshHoaDon != null && freshHoaDon.getTrangThai() == 1) {
                            System.out.println("ℹ️ Hóa đơn " + freshHoaDon.getMaHD() + " đã được thanh toán trước đó (không cần cập nhật)");
                        } else if (freshHoaDon != null) {
                            System.out.println("⚠️ Hóa đơn " + freshHoaDon.getMaHD() + " có trạng thái " + freshHoaDon.getTrangThai() + " (không cần cập nhật)");
                        } else {
                            System.out.println("❌ Không thể tìm thấy hóa đơn " + hoaDon.getMaHD() + " trong database");
                        }
                    } catch (Exception e) {
                        // Nếu hóa đơn đã được xử lý trước đó, ghi log và tiếp tục
                        System.out.println("⚠️ Hóa đơn " + hoaDon.getMaHD() + " đã được xử lý trước đó: " + e.getMessage());
                        System.out.println("🔍 Stack trace: " + e.getStackTrace()[0]);
                    }
                } else if (hoaDon != null && hoaDon.getTrangThai() == 1) {
                    // Hóa đơn đã thanh toán rồi, ghi log
                    System.out.println("ℹ️ Hóa đơn " + hoaDon.getMaHD() + " đã được thanh toán trước đó (không cần cập nhật)");
                } else if (hoaDon != null) {
                    // Hóa đơn có trạng thái khác, ghi log
                    System.out.println("ℹ️ Hóa đơn " + hoaDon.getMaHD() + " có trạng thái " + hoaDon.getTrangThai() + " (không cần cập nhật)");
                }

                // Trả về HTML page để redirect người dùng
                String errorHtml = String.format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <title>Thanh toán thất bại</title>
                            <meta charset="UTF-8">
                            <style>
                                body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }
                                .failed { color: #dc3545; font-size: 24px; margin-bottom: 20px; }
                                .redirect { color: #6c757d; font-size: 16px; }
                            </style>
                        </head>
                        <body>
                            <div class="failed">❌ Thanh toán thất bại!</div>
                            <div class="redirect">Lỗi: %s</div>
                            <div class="redirect">Đang chuyển hướng về trang đơn hàng...</div>
                            <script>
                                setTimeout(function() {
                                    window.location.href = 'http://localhost:3000/orders';
                                }, 3000);
                            </script>
                        </body>
                        </html>
                        """, errorMessage);
                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.TEXT_HTML)
                        .body(errorHtml);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("System error: " + e.getMessage());
        }
    }

    // ✅ IPN Handler - Xử lý thông báo từ VNPAY server
    @PostMapping("/vnpay/ipn")
    public ResponseEntity<Map<String, String>> handleVNPayIPN(
            HttpServletRequest request) {
        try {
            Map<String, String> fields = new HashMap<>();
            for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
                String fieldName = params.nextElement();
                String fieldValue = request.getParameter(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }

            String signValue = vnpayConfig.hmacSHA512(vnpayConfig.getVnp_HashSecret(), hashAllFields(fields));

            if (signValue.equals(vnp_SecureHash)) {
                String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
                String vnp_TxnRef = request.getParameter("vnp_TxnRef");
                String vnp_Amount = request.getParameter("vnp_Amount");
                String vnp_TransactionStatus = request.getParameter("vnp_TransactionStatus");

                ThanhToan thanhToan = thanhToanService.findByMaGiaoDichNganHang(vnp_TxnRef);

                if (thanhToan != null) {

                    // Kiểm tra số tiền
                    long dbAmount = thanhToan.getSoTienThanhToan().multiply(new BigDecimal("100")).longValue();
                    long vnpAmount = Long.parseLong(vnp_Amount);

                    if (dbAmount == vnpAmount) {
                        if (thanhToan.getTrangThaiTT() == 0) { // Chưa xử lý
                            if ("00".equals(vnp_ResponseCode) && "00".equals(vnp_TransactionStatus)) {
                                thanhToan.setTrangThaiTT(1); // Thành công
                            } else {
                                thanhToan.setTrangThaiTT(2); // Thất bại
                            }
                            thanhToanService.update(thanhToan);

                            return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));
                        } else {
                            return ResponseEntity.ok(Map.of("RspCode", "02", "Message", "Order already confirmed"));
                        }
                    } else {
                        return ResponseEntity.ok(Map.of("RspCode", "04", "Message", "Invalid amount"));
                    }
                } else {
                    return ResponseEntity.ok(Map.of("RspCode", "01", "Message", "Order not found"));
                }
            } else {
                return ResponseEntity.ok(Map.of("RspCode", "97", "Message", "Invalid signature"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("RspCode", "99", "Message", "Unknown error"));
        }
    }

    // ✅ Phương thức hash theo phiên bản 2.1.0 - PHẢI encode key và value
    private String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                // ✅ Encode cả key và value theo phiên bản 2.1.0
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }
        return hashData.toString();
    }

    // ✅ Phương thức lấy thông báo lỗi VNPAY
    private String getVNPayErrorMessage(String responseCode) {
        Map<String, String> errorMessages = new HashMap<>();
        errorMessages.put("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).");
        errorMessages.put("09", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.");
        errorMessages.put("10", "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần");
        errorMessages.put("11", "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.");
        errorMessages.put("12", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.");
        errorMessages.put("13", "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP).");
        errorMessages.put("24", "Giao dịch không thành công do: Khách hàng hủy giao dịch");
        errorMessages.put("51", "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.");
        errorMessages.put("65", "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.");
        errorMessages.put("75", "Ngân hàng thanh toán đang bảo trì.");
        errorMessages.put("79", "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định.");
        errorMessages.put("99", "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)");

        return errorMessages.getOrDefault(responseCode, "Giao dịch không thành công - Mã lỗi: " + responseCode);
    }

} 