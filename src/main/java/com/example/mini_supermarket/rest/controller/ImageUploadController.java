package com.example.mini_supermarket.rest.controller;

import com.example.mini_supermarket.dto.ApiResponse;
import com.example.mini_supermarket.entity.HinhAnh;
import com.example.mini_supermarket.entity.SanPham;
import com.example.mini_supermarket.service.HinhAnhService;
import com.example.mini_supermarket.service.SanPhamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
@Tag(name = "Upload Ảnh", description = "API upload và quản lý ảnh sản phẩm")
public class ImageUploadController {

    @Autowired
    private HinhAnhService hinhAnhService;

    @Autowired
    private SanPhamService sanPhamService;

    @Value("${app.images.upload-dir:./uploads/images}")
    private String uploadDir;

    @Value("${app.images.allowed-extensions:jpg,jpeg,png,gif,webp}")
    private String allowedExtensions;

    @Value("${app.images.max-size:10485760}")
    private long maxFileSize; // 10MB default

    /**
     * Upload ảnh sản phẩm
     */
    @Operation(summary = "Upload ảnh sản phẩm", description = "Upload ảnh mới cho sản phẩm")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Upload thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "File không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @PostMapping(value = "/product-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadProductImage(
            @Parameter(description = "File ảnh cần upload", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Mã sản phẩm", required = true)
            @RequestParam("maSP") String maSP,
            @Parameter(description = "Mô tả ảnh")
            @RequestParam(value = "moTa", required = false) String moTa,
            @Parameter(description = "Ảnh chính hay không")
            @RequestParam(value = "laChinh", defaultValue = "false") Boolean laChinh,
            @Parameter(description = "Thứ tự hiển thị")
            @RequestParam(value = "thuTuHienThi", defaultValue = "0") Integer thuTuHienThi) {

        try {
            // Kiểm tra sản phẩm có tồn tại không
            SanPham sanPham = sanPhamService.findActiveById(maSP);
            if (sanPham == null) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.<Map<String, Object>>builder()
                        .success(false)
                        .message("Không tìm thấy sản phẩm với mã: " + maSP)
                        .error("Product not found")
                        .build()
                );
            }

            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.<Map<String, Object>>builder()
                        .success(false)
                        .message("File không được để trống")
                        .error("Empty file")
                        .build()
                );
            }

            // Kiểm tra kích thước file
            if (file.getSize() > maxFileSize) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.<Map<String, Object>>builder()
                        .success(false)
                        .message("File quá lớn. Kích thước tối đa: " + (maxFileSize / 1024 / 1024) + "MB")
                        .error("File too large")
                        .build()
                );
            }

            // Kiểm tra định dạng file
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            if (!isValidFileExtension(fileExtension)) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.<Map<String, Object>>builder()
                        .success(false)
                        .message("Định dạng file không được hỗ trợ. Định dạng hợp lệ: " + allowedExtensions)
                        .error("Invalid file format")
                        .build()
                );
            }

            // Tạo thư mục nếu chưa tồn tại
            String productImageDir = uploadDir + "/products";
            Path targetDir = Paths.get(productImageDir);
            Files.createDirectories(targetDir);

            // Tạo tên file unique
            String fileName = generateUniqueFileName(maSP, fileExtension);
            Path targetPath = Paths.get(productImageDir, fileName);

            // Lưu file
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Tạo URL để truy cập
            String imageUrl = "/images/products/" + fileName;

            // Lưu thông tin ảnh vào database
            HinhAnh hinhAnh = new HinhAnh();
            hinhAnh.setSanPham(sanPham);
            hinhAnh.setUrl(imageUrl);
            hinhAnh.setMoTa(moTa != null ? moTa : "Ảnh sản phẩm " + maSP);
            hinhAnh.setLaChinh(laChinh);
            hinhAnh.setThuTuHienThi(thuTuHienThi);
            hinhAnh.setNgayTao(Timestamp.valueOf(LocalDateTime.now()));
            hinhAnh.setIsDeleted(false);

            HinhAnh savedHinhAnh = hinhAnhService.save(hinhAnh);

            // Trả về kết quả
            Map<String, Object> result = new HashMap<>();
            result.put("maHinh", savedHinhAnh.getMaHinh());
            result.put("imageUrl", imageUrl);
            result.put("fileName", fileName);
            result.put("fileSize", file.getSize());
            result.put("maSP", maSP);
            result.put("moTa", savedHinhAnh.getMoTa());
            result.put("laChinh", savedHinhAnh.getLaChinh());
            result.put("thuTuHienThi", savedHinhAnh.getThuTuHienThi());

            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .result(result)
                .message("Upload ảnh sản phẩm thành công")
                .build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Lỗi upload ảnh: " + e.getMessage())
                    .error(e.getClass().getSimpleName())
                    .build()
            );
        }
    }

    /**
     * Upload nhiều ảnh sản phẩm cùng lúc
     */
    @Operation(summary = "Upload nhiều ảnh sản phẩm", description = "Upload nhiều ảnh cùng lúc cho sản phẩm")
    @PostMapping(value = "/product-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> uploadMultipleProductImages(
            @Parameter(description = "Danh sách file ảnh", required = true)
            @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "Mã sản phẩm", required = true)
            @RequestParam("maSP") String maSP) {

        try {
            List<Map<String, Object>> results = new ArrayList<>();

            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                
                // Upload từng ảnh
                ResponseEntity<ApiResponse<Map<String, Object>>> response = uploadProductImage(
                    file, maSP, 
                    "Ảnh " + (i + 1) + " của sản phẩm " + maSP, 
                    i == 0, // Ảnh đầu tiên là ảnh chính
                    i
                );

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    results.add(response.getBody().getResult());
                }
            }

            return ResponseEntity.ok(ApiResponse.<List<Map<String, Object>>>builder()
                .success(true)
                .result(results)
                .message("Upload " + results.size() + " ảnh thành công")
                .build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                ApiResponse.<List<Map<String, Object>>>builder()
                    .success(false)
                    .message("Lỗi upload nhiều ảnh: " + e.getMessage())
                    .error(e.getClass().getSimpleName())
                    .build()
            );
        }
    }

    /**
     * Xóa ảnh sản phẩm
     */
    @Operation(summary = "Xóa ảnh sản phẩm", description = "Xóa ảnh sản phẩm theo ID")
    @DeleteMapping("/product-image/{maHinh}")
    public ResponseEntity<ApiResponse<String>> deleteProductImage(
            @Parameter(description = "ID của ảnh", required = true)
            @PathVariable Integer maHinh) {

        try {
            HinhAnh hinhAnh = hinhAnhService.findActiveById(maHinh);
            if (hinhAnh == null) {
                return ResponseEntity.notFound().build();
            }

            // Xóa file vật lý
            String imageUrl = hinhAnh.getUrl();
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir + "/products", fileName);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            // Xóa trong database (soft delete)
            hinhAnh.setIsDeleted(true);
            hinhAnhService.update(hinhAnh);

            return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .result("Đã xóa ảnh: " + fileName)
                .message("Xóa ảnh sản phẩm thành công")
                .build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                ApiResponse.<String>builder()
                    .success(false)
                    .message("Lỗi xóa ảnh: " + e.getMessage())
                    .error(e.getClass().getSimpleName())
                    .build()
            );
        }
    }

    /**
     * Lấy danh sách ảnh của sản phẩm
     */
    @Operation(summary = "Lấy ảnh sản phẩm", description = "Lấy danh sách ảnh của sản phẩm theo mã")
    @GetMapping("/product-images/{maSP}")
    public ResponseEntity<ApiResponse<List<HinhAnh>>> getProductImages(
            @Parameter(description = "Mã sản phẩm", required = true)
            @PathVariable String maSP) {

        try {
            // Kiểm tra sản phẩm có tồn tại không
            SanPham sanPham = sanPhamService.findActiveById(maSP);
            if (sanPham == null) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.<List<HinhAnh>>builder()
                        .success(false)
                        .message("Không tìm thấy sản phẩm với mã: " + maSP)
                        .error("Product not found")
                        .build()
                );
            }

            // Lấy danh sách ảnh của sản phẩm
            List<HinhAnh> hinhAnhs = hinhAnhService.findBySanPhamAndNotDeleted(sanPham);

            return ResponseEntity.ok(ApiResponse.<List<HinhAnh>>builder()
                .success(true)
                .result(hinhAnhs)
                .message("Lấy danh sách ảnh sản phẩm thành công")
                .build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                ApiResponse.<List<HinhAnh>>builder()
                    .success(false)
                    .message("Lỗi lấy danh sách ảnh: " + e.getMessage())
                    .error(e.getClass().getSimpleName())
                    .build()
            );
        }
    }

    /**
     * Lấy ảnh đơn lẻ theo ID
     */
    @Operation(summary = "Lấy ảnh theo ID", description = "Lấy thông tin ảnh theo mã ảnh")
    @GetMapping("/product-image/{maHinh}")
    public ResponseEntity<ApiResponse<HinhAnh>> getProductImageById(
            @Parameter(description = "ID của ảnh", required = true)
            @PathVariable Integer maHinh) {

        try {
            HinhAnh hinhAnh = hinhAnhService.findActiveById(maHinh);
            if (hinhAnh == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(ApiResponse.<HinhAnh>builder()
                .success(true)
                .result(hinhAnh)
                .message("Lấy thông tin ảnh thành công")
                .build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                ApiResponse.<HinhAnh>builder()
                    .success(false)
                    .message("Lỗi lấy thông tin ảnh: " + e.getMessage())
                    .error(e.getClass().getSimpleName())
                    .build()
            );
        }
    }

    /**
     * Lấy ảnh chính của sản phẩm
     */
    @Operation(summary = "Lấy ảnh chính", description = "Lấy ảnh chính của sản phẩm")
    @GetMapping("/product-main-image/{maSP}")
    public ResponseEntity<ApiResponse<HinhAnh>> getProductMainImage(
            @Parameter(description = "Mã sản phẩm", required = true)
            @PathVariable String maSP) {

        try {
            // Kiểm tra sản phẩm có tồn tại không
            SanPham sanPham = sanPhamService.findActiveById(maSP);
            if (sanPham == null) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.<HinhAnh>builder()
                        .success(false)
                        .message("Không tìm thấy sản phẩm với mã: " + maSP)
                        .error("Product not found")
                        .build()
                );
            }

            // Lấy ảnh chính của sản phẩm
            List<HinhAnh> hinhAnhs = hinhAnhService.findBySanPhamAndNotDeleted(sanPham);
            HinhAnh mainImage = hinhAnhs.stream()
                .filter(h -> h.getLaChinh() != null && h.getLaChinh())
                .findFirst()
                .orElse(hinhAnhs.isEmpty() ? null : hinhAnhs.get(0));

            if (mainImage == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(ApiResponse.<HinhAnh>builder()
                .success(true)
                .result(mainImage)
                .message("Lấy ảnh chính thành công")
                .build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                ApiResponse.<HinhAnh>builder()
                    .success(false)
                    .message("Lỗi lấy ảnh chính: " + e.getMessage())
                    .error(e.getClass().getSimpleName())
                    .build()
            );
        }
    }

    /**
     * Cập nhật thông tin ảnh
     */
    @Operation(summary = "Cập nhật thông tin ảnh", description = "Cập nhật mô tả, ảnh chính, thứ tự hiển thị")
    @PutMapping("/product-image/{maHinh}")
    public ResponseEntity<ApiResponse<HinhAnh>> updateImageInfo(
            @Parameter(description = "ID của ảnh", required = true)
            @PathVariable Integer maHinh,
            @Parameter(description = "Thông tin cập nhật")
            @RequestBody Map<String, Object> updateData) {

        try {
            HinhAnh hinhAnh = hinhAnhService.findActiveById(maHinh);
            if (hinhAnh == null) {
                return ResponseEntity.notFound().build();
            }

            // Cập nhật thông tin
            if (updateData.containsKey("moTa")) {
                hinhAnh.setMoTa((String) updateData.get("moTa"));
            }
            if (updateData.containsKey("laChinh")) {
                hinhAnh.setLaChinh((Boolean) updateData.get("laChinh"));
            }
            if (updateData.containsKey("thuTuHienThi")) {
                hinhAnh.setThuTuHienThi((Integer) updateData.get("thuTuHienThi"));
            }

            HinhAnh updatedHinhAnh = hinhAnhService.update(hinhAnh);

            return ResponseEntity.ok(ApiResponse.<HinhAnh>builder()
                .success(true)
                .result(updatedHinhAnh)
                .message("Cập nhật thông tin ảnh thành công")
                .build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                ApiResponse.<HinhAnh>builder()
                    .success(false)
                    .message("Lỗi cập nhật ảnh: " + e.getMessage())
                    .error(e.getClass().getSimpleName())
                    .build()
            );
        }
    }

    /**
     * Serve ảnh trực tiếp từ server (thay thế cho static resource)
     */
    @Operation(summary = "Hiển thị ảnh", description = "Serve ảnh trực tiếp từ server")
    @GetMapping("/serve-image/{fileName}")
    public ResponseEntity<byte[]> serveImage(
            @Parameter(description = "Tên file ảnh", required = true)
            @PathVariable String fileName) {

        try {
            Path imagePath = Paths.get(uploadDir + "/products", fileName);
            
            if (!Files.exists(imagePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(imagePath);
            
            // Xác định content type dựa trên extension
            String contentType = determineContentType(fileName);
            
            return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                .body(imageBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Serve ảnh theo mã ảnh
     */
    @Operation(summary = "Hiển thị ảnh theo ID", description = "Serve ảnh trực tiếp theo mã ảnh")
    @GetMapping("/serve-image-by-id/{maHinh}")
    public ResponseEntity<byte[]> serveImageById(
            @Parameter(description = "ID của ảnh", required = true)
            @PathVariable Integer maHinh) {

        try {
            HinhAnh hinhAnh = hinhAnhService.findActiveById(maHinh);
            if (hinhAnh == null) {
                return ResponseEntity.notFound().build();
            }

            String imageUrl = hinhAnh.getUrl();
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            
            Path imagePath = Paths.get(uploadDir + "/products", fileName);
            
            if (!Files.exists(imagePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(imagePath);
            String contentType = determineContentType(fileName);
            
            return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                .body(imageBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Cập nhật URL ảnh trong database (để fix dữ liệu cũ)
     */
    @Operation(summary = "Cập nhật URL ảnh", description = "Cập nhật URL ảnh trong database để hiển thị đúng")
    @PutMapping("/fix-image-urls")
    public ResponseEntity<ApiResponse<String>> fixImageUrls() {
        try {
            // Lấy tất cả ảnh từ sản phẩm SP001 để test
            SanPham sanPham = sanPhamService.findActiveById("SP001");
            if (sanPham == null) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.<String>builder()
                        .success(false)
                        .message("Không tìm thấy sản phẩm SP001")
                        .error("Product not found")
                        .build()
                );
            }
            
            List<HinhAnh> productImages = hinhAnhService.findBySanPhamAndNotDeleted(sanPham);
            int updatedCount = 0;
            
            for (HinhAnh hinhAnh : productImages) {
                String currentUrl = hinhAnh.getUrl();
                
                // Kiểm tra và cập nhật URL
                if (currentUrl != null) {
                    String newUrl;
                    
                    // Nếu URL không bắt đầu bằng "/" hoặc chứa "api/upload"
                    if (!currentUrl.startsWith("/") || currentUrl.contains("api/upload")) {
                        // Lấy tên file từ URL
                        String fileName = currentUrl;
                        if (currentUrl.contains("/")) {
                            fileName = currentUrl.substring(currentUrl.lastIndexOf("/") + 1);
                        }
                        
                        // Tạo URL mới đúng
                        newUrl = "/images/products/" + fileName;
                        hinhAnh.setUrl(newUrl);
                        hinhAnhService.update(hinhAnh);
                        updatedCount++;
                    }
                }
            }
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .result("Đã cập nhật " + updatedCount + " URL ảnh")
                .message("Cập nhật URL ảnh thành công")
                .build());
                
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                ApiResponse.<String>builder()
                    .success(false)
                    .message("Lỗi cập nhật URL ảnh: " + e.getMessage())
                    .error(e.getClass().getSimpleName())
                    .build()
            );
        }
    }

    // Helper methods
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isValidFileExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        return Arrays.asList(allowedExtensions.split(",")).contains(extension.toLowerCase());
    }

    private String generateUniqueFileName(String maSP, String extension) {
        return maSP + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + extension;
    }

    private String determineContentType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
            case "jfif":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            default:
                return "application/octet-stream";
        }
    }
}
