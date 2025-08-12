
-- Tạo database (chạy lệnh này riêng trong psql hoặc công cụ quản lý DB)
CREATE DATABASE "QuanLySieuThi";

-- --- TẠO HÀM TRIGGER ĐỂ TỰ ĐỘNG CẬP NHẬT TIMESTAMP ---
CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW."NgayCapNhat" = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION trigger_set_ngay_sua()
RETURNS TRIGGER AS $$
BEGIN
  NEW."NgaySua" = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- ===== TẠO CÁC BẢNG CHÍNH =====

-- Bảng quản lý thông tin người dùng hệ thống
CREATE TABLE "NguoiDung" (
    "MaNguoiDung" VARCHAR(50) PRIMARY KEY,
    "Email" VARCHAR(50) UNIQUE NOT NULL,
    "MatKhau" VARCHAR(255) NOT NULL,
    "Sub" VARCHAR(255),
    "VaiTro" INT NOT NULL,
    "NgayTao" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_vai_tro CHECK ("VaiTro" IN (0, 1, 2, 3))
);

-- Bảng quản lý thông tin các cửa hàng trong hệ thống
CREATE TABLE "CuaHang" (
    "MaCH" VARCHAR(50) PRIMARY KEY,
    "TenCH" VARCHAR(255) NOT NULL,
    "DiaChi" VARCHAR(255),
    "SDT" VARCHAR(15),
    "NgayThanhLap" DATE,
    "TrangThai" INT DEFAULT 1,
    "IsDeleted" BOOLEAN DEFAULT FALSE
);

-- Bảng quản lý thông tin nhà cung cấp sản phẩm
CREATE TABLE "NhaCungCap" (
    "MaNCC" VARCHAR(50) PRIMARY KEY,
    "TenNCC" VARCHAR(255) NOT NULL,
    "DiaChi" VARCHAR(255),
    "SDT" VARCHAR(15),
    "Email" VARCHAR(100),
    "ThongTinHopDong" TEXT,
    "NgayHopTac" DATE,
    "TrangThai" INT DEFAULT 1,
    "IsDeleted" BOOLEAN DEFAULT FALSE
);

-- Bảng quản lý thông tin nhân viên làm việc tại cửa hàng
CREATE TABLE "NhanVien" (
    "MaNV" VARCHAR(50) PRIMARY KEY,
    "MaNguoiDung" VARCHAR(50),
    "HoTen" VARCHAR(255) NOT NULL,
    "SDT" VARCHAR(15),
    "DiaChi" VARCHAR(255),
    "NgaySinh" DATE,
    "NgayVaoLam" DATE,
    "ChucVu" VARCHAR(100),
    "MaQuanLy" VARCHAR(50),
    "MaCH" VARCHAR(50),
    "TrangThai" INT DEFAULT 1,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_ngay_sinh_vao_lam CHECK ("NgaySinh" < "NgayVaoLam")
);

-- Bảng quản lý thông tin khách hàng và điểm tích lũy
CREATE TABLE "KhachHang" (
    "MaKH" VARCHAR(50) PRIMARY KEY,
    "MaNguoiDung" VARCHAR(50),
    "HoTen" VARCHAR(255) NOT NULL,
    "SDT" VARCHAR(15),
    "DiaChi" VARCHAR(255),
    "NgaySinh" DATE,
    "DiemTichLuy" INT DEFAULT 0,
    "LoaiKhachHang" VARCHAR(50) DEFAULT 'Thường',
    "NgayDangKy" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_diem_tich_luy CHECK ("DiemTichLuy" >= 0)
);

-- Bảng phân loại các sản phẩm trong hệ thống
CREATE TABLE "LoaiSanPham" (
    "MaLoaiSP" VARCHAR(50) PRIMARY KEY,
    "TenLoai" VARCHAR(255) NOT NULL,
    "MoTa" TEXT,
    "MaLoaiCha" VARCHAR(50),
    "ThuTuHienThi" INT DEFAULT 0,
    "IsDeleted" BOOLEAN DEFAULT FALSE
);

-- Bảng quản lý thông tin chi tiết sản phẩm
CREATE TABLE "SanPham" (
    "MaSP" VARCHAR(50) PRIMARY KEY,
    "MaLoaiSP" VARCHAR(50) NOT NULL,
    "TenSP" VARCHAR(255) NOT NULL,
    "MoTa" TEXT,
    "GiaBan" DECIMAL(15,2) NOT NULL,
    "DonViTinh" VARCHAR(50) DEFAULT 'Cái',
    "TrongLuong" DECIMAL(10,3),
    "KichThuoc" VARCHAR(100),
    "HanSuDung" INT,
    "TrangThai" INT DEFAULT 1,
    "NgayTao" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_gia_ban CHECK ("GiaBan" > 0)
);

-- Bảng quản lý các chương trình khuyến mãi
CREATE TABLE "KhuyenMai" (
    "MaKM" VARCHAR(50) PRIMARY KEY,
    "TenChuongTrinh" VARCHAR(255) NOT NULL,
    "MoTa" TEXT,
    "LoaiKM" VARCHAR(50) NOT NULL,
    "GiaTriKM" DECIMAL(15,2) NOT NULL,
    "DieuKienApDung" TEXT,
    "NgayBatDau" TIMESTAMP NOT NULL,
    "NgayKetThuc" TIMESTAMP NOT NULL,
    "SoLuongToiDa" INT,
    "DaSuDung" INT DEFAULT 0,
    "MaQuanLy" VARCHAR(50),
    "TrangThai" INT DEFAULT 1,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_ngay_khuyen_mai CHECK ("NgayBatDau" < "NgayKetThuc"),
    CONSTRAINT check_so_luong_toi_da CHECK ("SoLuongToiDa" IS NULL OR "SoLuongToiDa" > 0),
    CONSTRAINT check_da_su_dung CHECK ("DaSuDung" >= 0)
);

-- Bảng quản lý các phương thức thanh toán được chấp nhận
CREATE TABLE "PhuongThucThanhToan" (
    "MaPTTT" VARCHAR(50) PRIMARY KEY,
    "TenPTTT" VARCHAR(100) NOT NULL,
    "MoTa" TEXT,
    "PhiGiaoDich" DECIMAL(10,4) DEFAULT 0,
    "TrangThai" INT DEFAULT 1,
    "IsDeleted" BOOLEAN DEFAULT FALSE
);

-- Bảng quản lý thông tin các kho hàng
CREATE TABLE "Kho" (
    "MaKho" SERIAL PRIMARY KEY,
    "TenKho" VARCHAR(255) NOT NULL,
    "DiaChi" VARCHAR(255),
    "DienTich" DECIMAL(10,2),
    "SucChua" DECIMAL(15,2),
    "MaCH" VARCHAR(50),
    "TrangThai" INT DEFAULT 1,
    "IsDeleted" BOOLEAN DEFAULT FALSE
);

-- Bảng định nghĩa các ca làm việc trong ngày
CREATE TABLE "CaLamViec" (
    "MaCa" SERIAL PRIMARY KEY,
    "TenCa" VARCHAR(100) NOT NULL,
    "GioBatDau" TIME NOT NULL,
    "GioKetThuc" TIME NOT NULL,
    "SoGioLam" DECIMAL(4,2) GENERATED ALWAYS AS (
        (
            CASE
                WHEN "GioKetThuc" >= "GioBatDau" THEN
                    EXTRACT(EPOCH FROM ("GioKetThuc" - "GioBatDau"))
                ELSE
                    EXTRACT(EPOCH FROM (TIME '24:00:00' - "GioBatDau")) + EXTRACT(EPOCH FROM "GioKetThuc")
            END
        ) / 3600.0
    ) STORED,
    "TrangThai" INT DEFAULT 1,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_gio_lam CHECK ("GioBatDau" != "GioKetThuc")
);

-- Bảng quản lý lịch làm việc của nhân viên
CREATE TABLE "LichLamViec" (
    "MaLich" SERIAL PRIMARY KEY,
    "MaNV" VARCHAR(50) NOT NULL,
    "MaCa" INT NOT NULL,
    "NgayLam" DATE NOT NULL,
    "MaNVQuanLy" VARCHAR(50),
    "TrangThai" INT DEFAULT 0,
    "NgayDuyet" TIMESTAMP,
    "GhiChu" TEXT,
    "GioVao" TIME,
    "GioRa" TIME,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    UNIQUE ("MaNV", "NgayLam", "MaCa")
);

-- Bảng quản lý lương nhân viên theo tháng
CREATE TABLE "BangLuong" (
    "MaLuong" SERIAL PRIMARY KEY,
    "MaNV" VARCHAR(50) NOT NULL,
    "ThangLuong" INT NOT NULL,
    "NamLuong" INT NOT NULL,
    "LuongCoBan" DECIMAL(15,2) NOT NULL,
    "PhuCap" DECIMAL(15,2) DEFAULT 0,
    "Thuong" DECIMAL(15,2) DEFAULT 0,
    "KhauTru" DECIMAL(15,2) DEFAULT 0,
    "TongLuong" DECIMAL(15,2) GENERATED ALWAYS AS ("LuongCoBan" + "PhuCap" + "Thuong" - "KhauTru") STORED,
    "SoNgayLam" INT DEFAULT 0,
    "SoGioLam" DECIMAL(8,2) DEFAULT 0,
    "GhiChu" TEXT,
    "TrangThai" INT DEFAULT 0,
    "NgayTao" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "NgayThanhToan" TIMESTAMP NULL,
    "NguoiThanhToan" VARCHAR(50) NULL,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_thang_luong CHECK ("ThangLuong" >= 1 AND "ThangLuong" <= 12),
    CONSTRAINT check_nam_luong CHECK ("NamLuong" >= 2020),
    CONSTRAINT check_luong_co_ban CHECK ("LuongCoBan" >= 0),
    CONSTRAINT check_phu_cap CHECK ("PhuCap" >= 0),
    CONSTRAINT check_thuong CHECK ("Thuong" >= 0),
    CONSTRAINT check_khau_tru CHECK ("KhauTru" >= 0),
    CONSTRAINT check_so_ngay_lam CHECK ("SoNgayLam" >= 0),
    CONSTRAINT check_so_gio_lam CHECK ("SoGioLam" >= 0),
    UNIQUE ("MaNV", "ThangLuong", "NamLuong")
);

-- Bảng lưu trữ hình ảnh sản phẩm
CREATE TABLE "HinhAnh" (
    "MaHinh" SERIAL PRIMARY KEY,
    "MaSP" VARCHAR(50) NOT NULL,
    "URL" VARCHAR(500) NOT NULL,
    "MoTa" VARCHAR(255),
    "LaChinh" BOOLEAN DEFAULT FALSE,
    "ThuTuHienThi" INT DEFAULT 0,
    "NgayTao" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "IsDeleted" BOOLEAN DEFAULT FALSE
);

-- Bảng quản lý lịch sử giá sản phẩm
CREATE TABLE "GiaSanPham" (
    "MaGia" SERIAL PRIMARY KEY,
    "MaSP" VARCHAR(50) NOT NULL,
    "Gia" DECIMAL(15,2) NOT NULL,
    "NgayBatDau" DATE NOT NULL,
    "NgayKetThuc" DATE,
    "LyDoThayDoi" VARCHAR(255),
    "NguoiThayDoi" VARCHAR(50),
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_gia CHECK ("Gia" > 0),
    CONSTRAINT check_ngay_gia CHECK ("NgayKetThuc" IS NULL OR "NgayBatDau" <= "NgayKetThuc")
);

-- Bảng theo dõi số lượng tồn kho của sản phẩm
CREATE TABLE "TonKhoChiTiet" (
    "MaTKCT" SERIAL PRIMARY KEY,
    "MaSP" VARCHAR(50) NOT NULL,
    "MaKho" INT NOT NULL,
    "SoLuongTon" INT DEFAULT 0,
    "SoLuongToiThieu" INT DEFAULT 0,
    "SoLuongToiDa" INT,
    "NgayCapNhat" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_so_luong_ton CHECK ("SoLuongTon" >= 0),
    UNIQUE ("MaSP", "MaKho")
);

-- Bảng quản lý phiếu nhập hàng từ nhà cung cấp
CREATE TABLE "PhieuNhapHang" (
    "MaPN" SERIAL PRIMARY KEY,
    "MaNCC" VARCHAR(50) NOT NULL,
    "MaKho" INT NOT NULL,
    "MaNVLap" VARCHAR(50) NOT NULL,
    "NgayNhap" TIMESTAMP NOT NULL,
    "TongTienNhap" DECIMAL(15,2) DEFAULT 0,
    "TrangThai" INT DEFAULT 0,
    "GhiChu" TEXT,
    "NgayTao" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "IsDeleted" BOOLEAN DEFAULT FALSE
);

-- Bảng chi tiết các sản phẩm trong phiếu nhập
CREATE TABLE "ChiTietPhieuNhap" (
    "MaCTPN" SERIAL PRIMARY KEY,
    "MaPN" INT NOT NULL,
    "MaSP" VARCHAR(50) NOT NULL,
    "SoLuongNhap" INT NOT NULL,
    "DonGiaNhap" DECIMAL(15,2) NOT NULL,
    "ThanhTien" DECIMAL(15,2) GENERATED ALWAYS AS ("SoLuongNhap" * "DonGiaNhap") STORED,
    "NgayHetHan" DATE,
    "SoLo" VARCHAR(50),
    "NgaySanXuat" DATE,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_so_luong_nhap CHECK ("SoLuongNhap" > 0),
    CONSTRAINT check_don_gia_nhap CHECK ("DonGiaNhap" > 0)
);

-- Bảng quản lý phiếu xuất kho
CREATE TABLE "PhieuXuatKho" (
    "MaPXK" SERIAL PRIMARY KEY,
    "MaKho" INT NOT NULL,
    "MaNVLap" VARCHAR(50) NOT NULL,
    "NgayXuat" TIMESTAMP NOT NULL,
    "TongSoLuong" INT DEFAULT 0,
    "TongGiaTri" DECIMAL(15,2) DEFAULT 0,
    "LyDoXuat" VARCHAR(255),
    "TrangThai" INT DEFAULT 0,
    "GhiChu" TEXT,
    "NgayTao" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "IsDeleted" BOOLEAN DEFAULT FALSE
);

-- Bảng chi tiết các sản phẩm trong phiếu xuất
CREATE TABLE "ChiTietPhieuXuat" (
    "MaCTPXK" SERIAL PRIMARY KEY,
    "MaPXK" INT NOT NULL,
    "MaSP" VARCHAR(50) NOT NULL,
    "SoLuongXuat" INT NOT NULL,
    "DonGiaXuat" DECIMAL(15,2) NOT NULL,
    "ThanhTien" DECIMAL(15,2) GENERATED ALWAYS AS ("SoLuongXuat" * "DonGiaXuat") STORED,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_so_luong_xuat CHECK ("SoLuongXuat" > 0),
    CONSTRAINT check_don_gia_xuat CHECK ("DonGiaXuat" > 0)
);

-- Bảng quản lý hóa đơn bán hàng
CREATE TABLE "HoaDon" (
    "MaHD" SERIAL PRIMARY KEY,
    "MaKH" VARCHAR(50),
    "MaNVLap" VARCHAR(50) NOT NULL,
    "MaKM" VARCHAR(50),
    "NgayLap" TIMESTAMP NOT NULL,
    "TongTienHang" DECIMAL(15,2) DEFAULT 0,
    "TienGiamGia" DECIMAL(15,2) DEFAULT 0,
    "TongTien" DECIMAL(15,2) GENERATED ALWAYS AS ("TongTienHang" - "TienGiamGia") STORED,
    "MaPTTT" VARCHAR(50),
    "TrangThai" INT DEFAULT 0,
    "DiemTichLuy" INT DEFAULT 0,
    "GhiChu" TEXT,
    "NgayTao" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "NguoiTao" VARCHAR(50),
    "NgaySua" TIMESTAMP,
    "NguoiSua" VARCHAR(50),
    "IsDeleted" BOOLEAN DEFAULT FALSE
);

-- Bảng chi tiết các sản phẩm trong hóa đơn
CREATE TABLE "ChiTietHoaDon" (
    "MaCTHD" SERIAL PRIMARY KEY,
    "MaHD" INT NOT NULL,
    "MaSP" VARCHAR(50) NOT NULL,
    "SoLuong" INT NOT NULL,
    "DonGiaBan" DECIMAL(15,2) NOT NULL,
    "ThanhTien" DECIMAL(15,2) GENERATED ALWAYS AS ("SoLuong" * "DonGiaBan") STORED,
    "GiamGia" DECIMAL(15,2) DEFAULT 0,
    "ThanhTienSauGiam" DECIMAL(15,2) GENERATED ALWAYS AS (("SoLuong" * "DonGiaBan") - "GiamGia") STORED,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_so_luong CHECK ("SoLuong" > 0)
);

-- Bảng áp dụng khuyến mãi cho sản phẩm
CREATE TABLE "KhuyenMaiSanPham" (
    "MaKMSP" SERIAL PRIMARY KEY,
    "MaKM" VARCHAR(50) NOT NULL,
    "MaSP" VARCHAR(50) NOT NULL,
    "NgayBatDau" TIMESTAMP,
    "NgayKetThuc" TIMESTAMP,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    UNIQUE ("MaKM", "MaSP")
);

-- Bảng áp dụng khuyến mãi cho khách hàng
CREATE TABLE "KhuyenMaiKhachHang" (
    "MaKMKH" SERIAL PRIMARY KEY,
    "MaKM" VARCHAR(50) NOT NULL,
    "MaKH" VARCHAR(50) NOT NULL,
    "NgayApDung" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "DaSuDung" BOOLEAN DEFAULT FALSE,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    UNIQUE ("MaKM", "MaKH")
);

-- Bảng quản lý các giao dịch thanh toán
CREATE TABLE "ThanhToan" (
    "MaTT" SERIAL PRIMARY KEY,
    "MaHD" INT NOT NULL,
    "MaPTTT" VARCHAR(50) NOT NULL,
    "SoTienThanhToan" DECIMAL(15,2) NOT NULL,
    "NgayGioTT" TIMESTAMP NOT NULL,
    "TrangThaiTT" INT DEFAULT 0,
    "MaGiaoDichNganHang" VARCHAR(100),
    "GhiChu" TEXT,
    "IsDeleted" BOOLEAN DEFAULT FALSE
);

-- Bảng quản lý giỏ hàng của khách
CREATE TABLE "GioHang" (
    "MaGH" SERIAL PRIMARY KEY,
    "MaKH" VARCHAR(50),
    "MaNV" VARCHAR(50),
    "NgayTao" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "NgayCapNhat" TIMESTAMP,
    "TrangThai" INT DEFAULT 0,
    "GhiChu" TEXT,
    "IsDeleted" BOOLEAN DEFAULT FALSE
);

-- Bảng chi tiết sản phẩm trong giỏ hàng
CREATE TABLE "ChiTietGioHang" (
    "MaCTGH" SERIAL PRIMARY KEY,
    "MaGH" INT NOT NULL,
    "MaSP" VARCHAR(50) NOT NULL,
    "SoLuong" INT NOT NULL,
    "DonGiaHienTai" DECIMAL(15,2) NOT NULL,
    "ThanhTien" DECIMAL(15,2) GENERATED ALWAYS AS ("SoLuong" * "DonGiaHienTai") STORED,
    "NgayThem" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "IsDeleted" BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_so_luong CHECK ("SoLuong" > 0),
    CONSTRAINT check_don_gia_hien_tai CHECK ("DonGiaHienTai" > 0),
    UNIQUE ("MaGH", "MaSP")
);

-- Bảng thống kê báo cáo
CREATE TABLE "ThongKeBaoCao" (
    "MaBaoCao" SERIAL PRIMARY KEY,
    "MaCH" VARCHAR(50),
    "MaNV" VARCHAR(50) NOT NULL,
    "LoaiBaoCao" VARCHAR(100) NOT NULL,
    "TenBaoCao" VARCHAR(255) NOT NULL,
    "ThoiGianTu" TIMESTAMP,
    "ThoiGianDen" TIMESTAMP,
    "SoTien" DECIMAL(15,2),
    "SoLuong" INT,
    "NgayBaoCao" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "NoiDung" TEXT,
    "FileDinhKem" VARCHAR(500),
    "TrangThai" INT DEFAULT 1,
    "IsDeleted" BOOLEAN DEFAULT FALSE
);

-- ===== THÊM COMMENT CHO CÁC BẢNG VÀ CỘT =====
COMMENT ON TABLE "NguoiDung" IS 'Bảng quản lý thông tin người dùng hệ thống';
COMMENT ON COLUMN "NguoiDung"."VaiTro" IS '0=Quản trị, 1=Quản lý, 2=Nhân viên, 3=Khách hàng';
-- ... (Tương tự cho các comment khác, giữ nguyên nội dung)


-- ===== TẠO CÁC INDEX ĐỂ TĂNG TỐC TRUY VẤN =====
-- Lưu ý: Tên index không cần đặt trong ngoặc kép, nhưng tên cột bên trong thì có.
CREATE INDEX idx_nguoidung_email ON "NguoiDung"("Email");
CREATE INDEX idx_nguoidung_vaitro ON "NguoiDung"("VaiTro");
-- ... (Tương tự cho các index khác)


-- ===== THÊM CÁC KHÓA NGOẠI (FOREIGN KEYS) =====

-- Khóa ngoại cho bảng "LoaiSanPham" (tự tham chiếu)
ALTER TABLE "LoaiSanPham" ADD CONSTRAINT fk_loaisanpham_loai_cha
    FOREIGN KEY ("MaLoaiCha") REFERENCES "LoaiSanPham"("MaLoaiSP")
    ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "NhanVien"
ALTER TABLE "NhanVien" ADD CONSTRAINT fk_nhanvien_nguoidung
    FOREIGN KEY ("MaNguoiDung") REFERENCES "NguoiDung"("MaNguoiDung")
    ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "NhanVien" ADD CONSTRAINT fk_nhanvien_quanly
    FOREIGN KEY ("MaQuanLy") REFERENCES "NhanVien"("MaNV")
    ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "NhanVien" ADD CONSTRAINT fk_nhanvien_cuahang
    FOREIGN KEY ("MaCH") REFERENCES "CuaHang"("MaCH")
    ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "KhachHang"
ALTER TABLE "KhachHang" ADD CONSTRAINT fk_khachhang_nguoidung
    FOREIGN KEY ("MaNguoiDung") REFERENCES "NguoiDung"("MaNguoiDung")
    ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "SanPham"
ALTER TABLE "SanPham" ADD CONSTRAINT fk_sanpham_loaisanpham
    FOREIGN KEY ("MaLoaiSP") REFERENCES "LoaiSanPham"("MaLoaiSP")
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "KhuyenMai"
ALTER TABLE "KhuyenMai" ADD CONSTRAINT fk_khuyenmai_quanly
    FOREIGN KEY ("MaQuanLy") REFERENCES "NhanVien"("MaNV")
    ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "Kho"
ALTER TABLE "Kho" ADD CONSTRAINT fk_kho_cuahang
    FOREIGN KEY ("MaCH") REFERENCES "CuaHang"("MaCH")
    ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "LichLamViec"
ALTER TABLE "LichLamViec" ADD CONSTRAINT fk_lichlamviec_nhanvien
    FOREIGN KEY ("MaNV") REFERENCES "NhanVien"("MaNV")
    ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "LichLamViec" ADD CONSTRAINT fk_lichlamviec_ca
    FOREIGN KEY ("MaCa") REFERENCES "CaLamViec"("MaCa")
    ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "LichLamViec" ADD CONSTRAINT fk_lichlamviec_quanly
    FOREIGN KEY ("MaNVQuanLy") REFERENCES "NhanVien"("MaNV")
    ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "BangLuong"
ALTER TABLE "BangLuong" ADD CONSTRAINT fk_bangluong_nhanvien
    FOREIGN KEY ("MaNV") REFERENCES "NhanVien"("MaNV")
    ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "BangLuong" ADD CONSTRAINT fk_bangluong_nguoithanhtoan
    FOREIGN KEY ("NguoiThanhToan") REFERENCES "NhanVien"("MaNV")
    ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "HinhAnh"
ALTER TABLE "HinhAnh" ADD CONSTRAINT fk_hinhanh_sanpham
    FOREIGN KEY ("MaSP") REFERENCES "SanPham"("MaSP")
    ON DELETE CASCADE ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "GiaSanPham"
ALTER TABLE "GiaSanPham" ADD CONSTRAINT fk_giasanpham_sanpham
    FOREIGN KEY ("MaSP") REFERENCES "SanPham"("MaSP")
    ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "GiaSanPham" ADD CONSTRAINT fk_giasanpham_nguoithaydoi
    FOREIGN KEY ("NguoiThayDoi") REFERENCES "NhanVien"("MaNV")
    ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "TonKhoChiTiet"
ALTER TABLE "TonKhoChiTiet" ADD CONSTRAINT fk_tonkhochitiet_sanpham
    FOREIGN KEY ("MaSP") REFERENCES "SanPham"("MaSP")
    ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "TonKhoChiTiet" ADD CONSTRAINT fk_tonkhochitiet_kho
    FOREIGN KEY ("MaKho") REFERENCES "Kho"("MaKho")
    ON DELETE CASCADE ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "PhieuNhapHang"
ALTER TABLE "PhieuNhapHang" ADD CONSTRAINT fk_phieunhaphang_nhacungcap
    FOREIGN KEY ("MaNCC") REFERENCES "NhaCungCap"("MaNCC")
    ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "PhieuNhapHang" ADD CONSTRAINT fk_phieunhaphang_kho
    FOREIGN KEY ("MaKho") REFERENCES "Kho"("MaKho")
    ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "PhieuNhapHang" ADD CONSTRAINT fk_phieunhaphang_nhanvien
    FOREIGN KEY ("MaNVLap") REFERENCES "NhanVien"("MaNV")
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "ChiTietPhieuNhap"
ALTER TABLE "ChiTietPhieuNhap" ADD CONSTRAINT fk_chitietphieunhap_phieunhap
    FOREIGN KEY ("MaPN") REFERENCES "PhieuNhapHang"("MaPN")
    ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "ChiTietPhieuNhap" ADD CONSTRAINT fk_chitietphieunhap_sanpham
    FOREIGN KEY ("MaSP") REFERENCES "SanPham"("MaSP")
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "PhieuXuatKho"
ALTER TABLE "PhieuXuatKho" ADD CONSTRAINT fk_phieuxuatkho_kho
    FOREIGN KEY ("MaKho") REFERENCES "Kho"("MaKho")
    ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "PhieuXuatKho" ADD CONSTRAINT fk_phieuxuatkho_nhanvien
    FOREIGN KEY ("MaNVLap") REFERENCES "NhanVien"("MaNV")
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "ChiTietPhieuXuat"
ALTER TABLE "ChiTietPhieuXuat" ADD CONSTRAINT fk_chitietphieuxuat_phieuxuat
    FOREIGN KEY ("MaPXK") REFERENCES "PhieuXuatKho"("MaPXK")
    ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "ChiTietPhieuXuat" ADD CONSTRAINT fk_chitietphieuxuat_sanpham
    FOREIGN KEY ("MaSP") REFERENCES "SanPham"("MaSP")
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "HoaDon"
ALTER TABLE "HoaDon" ADD CONSTRAINT fk_hoadon_khachhang
    FOREIGN KEY ("MaKH") REFERENCES "KhachHang"("MaKH")
    ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "HoaDon" ADD CONSTRAINT fk_hoadon_nhanvien
    FOREIGN KEY ("MaNVLap") REFERENCES "NhanVien"("MaNV")
    ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "HoaDon" ADD CONSTRAINT fk_hoadon_khuyenmai
    FOREIGN KEY ("MaKM") REFERENCES "KhuyenMai"("MaKM")
    ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "HoaDon" ADD CONSTRAINT fk_hoadon_phuongthucthanhtoan
    FOREIGN KEY ("MaPTTT") REFERENCES "PhuongThucThanhToan"("MaPTTT")
    ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "HoaDon" ADD CONSTRAINT fk_hoadon_nguoitao
    FOREIGN KEY ("NguoiTao") REFERENCES "NhanVien"("MaNV")
    ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "HoaDon" ADD CONSTRAINT fk_hoadon_nguoisua
    FOREIGN KEY ("NguoiSua") REFERENCES "NhanVien"("MaNV")
    ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "ChiTietHoaDon"
ALTER TABLE "ChiTietHoaDon" ADD CONSTRAINT fk_chitiethoadon_hoadon
    FOREIGN KEY ("MaHD") REFERENCES "HoaDon"("MaHD")
    ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "ChiTietHoaDon" ADD CONSTRAINT fk_chitiethoadon_sanpham
    FOREIGN KEY ("MaSP") REFERENCES "SanPham"("MaSP")
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "KhuyenMaiSanPham"
ALTER TABLE "KhuyenMaiSanPham" ADD CONSTRAINT fk_khuyenmaisanpham_khuyenmai
    FOREIGN KEY ("MaKM") REFERENCES "KhuyenMai"("MaKM")
    ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "KhuyenMaiSanPham" ADD CONSTRAINT fk_khuyenmaisanpham_sanpham
    FOREIGN KEY ("MaSP") REFERENCES "SanPham"("MaSP")
    ON DELETE CASCADE ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "KhuyenMaiKhachHang"
ALTER TABLE "KhuyenMaiKhachHang" ADD CONSTRAINT fk_khuyenmaikhachhang_khuyenmai
    FOREIGN KEY ("MaKM") REFERENCES "KhuyenMai"("MaKM")
    ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "KhuyenMaiKhachHang" ADD CONSTRAINT fk_khuyenmaikhachhang_khachhang
    FOREIGN KEY ("MaKH") REFERENCES "KhachHang"("MaKH")
    ON DELETE CASCADE ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "ThanhToan"
ALTER TABLE "ThanhToan" ADD CONSTRAINT fk_thanhtoan_hoadon
    FOREIGN KEY ("MaHD") REFERENCES "HoaDon"("MaHD")
    ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "ThanhToan" ADD CONSTRAINT fk_thanhtoan_phuongthucthanhtoan
    FOREIGN KEY ("MaPTTT") REFERENCES "PhuongThucThanhToan"("MaPTTT")
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "GioHang"
ALTER TABLE "GioHang" ADD CONSTRAINT fk_giohang_khachhang
    FOREIGN KEY ("MaKH") REFERENCES "KhachHang"("MaKH")
    ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "GioHang" ADD CONSTRAINT fk_giohang_nhanvien
    FOREIGN KEY ("MaNV") REFERENCES "NhanVien"("MaNV")
    ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "ChiTietGioHang"
ALTER TABLE "ChiTietGioHang" ADD CONSTRAINT fk_chitietgiohang_giohang
    FOREIGN KEY ("MaGH") REFERENCES "GioHang"("MaGH")
    ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "ChiTietGioHang" ADD CONSTRAINT fk_chitietgiohang_sanpham
    FOREIGN KEY ("MaSP") REFERENCES "SanPham"("MaSP")
    ON DELETE CASCADE ON UPDATE CASCADE;

-- Khóa ngoại cho bảng "ThongKeBaoCao"
ALTER TABLE "ThongKeBaoCao" ADD CONSTRAINT fk_thongkebaocao_cuahang
    FOREIGN KEY ("MaCH") REFERENCES "CuaHang"("MaCH")
    ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "ThongKeBaoCao" ADD CONSTRAINT fk_thongkebaocao_nhanvien
    FOREIGN KEY ("MaNV") REFERENCES "NhanVien"("MaNV")
    ON DELETE RESTRICT ON UPDATE CASCADE;


-- ===== TẠO TRIGGER CHO CÁC BẢNG CẦN TỰ ĐỘNG CẬP NHẬT TIMESTAMP =====

-- Trigger cho bảng "TonKhoChiTiet"
CREATE TRIGGER set_timestamp_ton_kho_chi_tiet
BEFORE UPDATE ON "TonKhoChiTiet"
FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- Trigger cho bảng "HoaDon"
CREATE TRIGGER set_timestamp_hoa_don
BEFORE UPDATE ON "HoaDon"
FOR EACH ROW
EXECUTE FUNCTION trigger_set_ngay_sua();

-- Trigger cho bảng "GioHang"
CREATE TRIGGER set_timestamp_gio_hang
BEFORE UPDATE ON "GioHang"
FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

-- ========== TỔNG QUAN CHUYỂN ĐỔI ==========
-- 1. Cú pháp INSERT:
--    - Loại bỏ tiền tố N'...' của MySQL, vì PostgreSQL mặc định hỗ trợ Unicode trong chuỗi.
--    - Giữ nguyên dấu ngoặc kép "" cho tên bảng và cột để đảm bảo tính nhất quán với lược đồ đã tạo.
-- 2. Stored Procedure:
--    - Chuyển đổi từ `CREATE PROCEDURE` của MySQL sang `CREATE OR REPLACE PROCEDURE` của PostgreSQL.
--    - Sử dụng khối `$$` (dollar-quoting) để định nghĩa thân procedure, giúp tránh lỗi với các dấu nháy đơn.
--    - Thay thế vòng lặp CURSOR thủ công của MySQL bằng vòng lặp `FOR ... IN` của PostgreSQL, cú pháp này ngắn gọn và an toàn hơn.
--    - Thay thế hàm `CONCAT()` bằng toán tử `||` để nối chuỗi.
--    - Bỏ `DELIMITER` vì không cần thiết trong PostgreSQL.
-- ==========================================

-- Thêm dữ liệu mẫu cho bảng chính
-- VaiTro: 0=Admin, 1=QuanLy, 2=NhanVien, 3=KhachHang
INSERT INTO "NguoiDung" ("MaNguoiDung", "Email", "MatKhau", "Sub", "VaiTro") VALUES
('ND001', 'admin1@gmail.com', 'pass123', null, 0),
('ND002', 'quanly1@gmail.com', 'pass123', null, 1),
('ND003', 'nhanvien1@gmail.com', 'pass123', null, 2),
('ND004', 'nhanvien2@gmail.com', 'pass123', null, 2),
('ND005', 'nhanvien3@gmail.com', 'pass123', null, 2),
('ND006', 'nhanvien4@gmail.com', 'pass123', null, 2),
('ND007', 'nhanvien5@gmail.com', 'pass123', null, 2),
('ND008', 'quanly2@gmail.com', 'pass123', null, 1),
('ND009', 'nhanvien6@gmail.com', 'pass123', null, 2),
('ND010', 'nhanvien7@gmail.com', 'pass123', null, 2),
('ND011', 'nhanvien8@gmail.com', 'pass123', null, 2),
('ND012', 'nhanvien9@gmail.com', 'pass123', null, 2),
('ND013', 'nhanvien10@gmail.com', 'pass123', null, 2),
('ND014', 'nhanvien11@gmail.com', 'pass123', null,2),
('ND015', 'khach1@gmail.com', 'pass456', null, 3),
('ND016', 'khach2@gmail.com', 'pass456', null, 3),
('ND017', 'khach3@gmail.com', 'pass456', null, 3),
('ND018', 'khach4@gmail.com', 'pass456', null, 3),
('ND019', 'khach5@gmail.com', 'pass456', null, 3),
('ND020', 'khach6@gmail.com', 'pass456', null, 3),
('ND021', 'khach7@gmail.com', 'pass456', null, 3),
('ND022', 'khach8@gmail.com', 'pass456', null, 3),
('ND023', 'khach9@gmail.com', 'pass456', null, 3),
('ND024', 'khach10@gmail.com', 'pass456', null, 3);

INSERT INTO "CuaHang" ("MaCH", "TenCH", "DiaChi", "SDT", "NgayThanhLap", "TrangThai") VALUES
('CH001', 'Cửa Hàng EasyMart1', '123 Lê Lợi, Q1', '0909123456', '2020-01-01', 1),
('CH002', 'Cửa Hàng EasyMart2', '456 Nguyễn Đình Chiểu, Q3', '0911222333', '2020-02-01', 1);

INSERT INTO "NhaCungCap" ("MaNCC", "TenNCC", "DiaChi", "SDT", "Email", "ThongTinHopDong", "NgayHopTac", "TrangThai") VALUES
('NCC001', 'Rau Xanh Sạch Đà Lạt', 'Đà Lạt - Lâm Đồng', '0901000001', 'rauxanh@gmail.com', 'Hợp đồng cung cấp rau sạch', '2020-01-01', 1),
('NCC002', 'Thủy Sản Đông Lạnh Biển Xanh', 'Quận 7 - TP.HCM', '0901000002', 'bienxanh@gmail.com', 'Hợp đồng cung cấp thủy sản', '2020-02-01', 1),
('NCC003', 'Công Ty Đồ Hộp Việt', 'Bình Dương', '0901000003', 'dohop@gmail.com', 'Hợp đồng cung cấp đồ hộp', '2020-03-01', 1),
('NCC004', 'Nước Giải Khát Việt Nam', 'TP.HCM', '0901000004', 'nuocgiaikhat@gmail.com', 'Hợp đồng cung cấp nước giải khát', '2020-04-01', 1),
('NCC005', 'Công Ty Sữa & Bé Khỏe', 'Hà Nội', '0901000005', 'suabekhoe@gmail.com', 'Hợp đồng cung cấp sữa', '2020-05-01', 1),
('NCC006', 'Gia Vị Nam Ngư', 'TP.HCM', '0901000006', 'giavi@gmail.com', 'Hợp đồng cung cấp gia vị', '2020-06-01', 1),
('NCC007', 'Hóa Phẩm & Đồ Gia Dụng Unihome', 'Đồng Nai', '0901000007', 'unihome@gmail.com', 'Hợp đồng cung cấp hóa phẩm', '2020-07-01', 1);

INSERT INTO "NhanVien" ("MaNV", "MaNguoiDung", "HoTen", "SDT", "DiaChi", "NgaySinh", "NgayVaoLam", "ChucVu", "MaQuanLy", "MaCH", "TrangThai") VALUES
('NV001', 'ND001', 'Nguyễn Văn A', '0909111222', '123 Lê Lợi', '1990-01-01', '2020-01-01', 'Giám đốc', NULL, 'CH001', 1),
('NV002', 'ND002', 'Trần Thị B', '0909333444', '456 Nguyễn Đình Chiểu', '1992-02-02', '2020-02-01', 'Quản lý', 'NV001', 'CH001', 1),
('NV003', 'ND003', 'Lê Văn C', '0911223344', '789 Phan Văn Trị', '1993-03-03', '2020-03-01', 'Nhân viên bán hàng', 'NV002', 'CH001', 1),
('NV004', 'ND004', 'Phạm Thị D', '0922334455', '111 Võ Văn Ngân', '1994-04-04', '2020-04-01', 'Nhân viên kho', 'NV002', 'CH001', 1),
('NV005', 'ND005', 'Hoàng Văn E', '0933445566', '222 Lý Thường Kiệt', '1995-05-05', '2020-05-01', 'Nhân viên thu ngân', 'NV002', 'CH001', 1),
('NV006', 'ND006', 'Ngô Thị F', '0944556677', '333 Cách Mạng Tháng 8', '1996-06-06', '2020-06-01', 'Nhân viên bán hàng', 'NV002', 'CH001', 1),
('NV007', 'ND007', 'Đỗ Văn G', '0955667788', '444 Pasteur', '1997-07-07', '2020-07-01', 'Nhân viên kho', 'NV002', 'CH001', 1),
('NV008', 'ND008', 'Nguyễn Văn H', '0966778899', '123 Lê Lợi', '1990-08-08', '2020-08-01', 'Giám đốc', NULL, 'CH002', 1),
('NV009', 'ND009', 'Trần Thị I', '0977889900', '456 Nguyễn Đình Chiểu', '1992-09-09', '2020-09-01', 'Quản lý', 'NV008', 'CH002', 1),
('NV010', 'ND010', 'Lê Văn J', '0988990011', '789 Phan Văn Trị', '1993-10-10', '2020-10-01', 'Nhân viên bán hàng', 'NV009', 'CH002', 1),
('NV011', 'ND011', 'Phạm Thị K', '0999001122', '111 Võ Văn Ngân', '1994-11-11', '2020-11-01', 'Nhân viên kho', 'NV009', 'CH002', 1),
('NV012', 'ND012', 'Hoàng Văn L', '0900123456', '222 Lý Thường Kiệt', '1995-12-12', '2020-12-01', 'Nhân viên thu ngân', 'NV009', 'CH002', 1),
('NV013', 'ND013', 'Ngô Thị M', '0911234567', '333 Cách Mạng Tháng 8', '1996-01-13', '2021-01-01', 'Nhân viên bán hàng', 'NV009', 'CH002', 1),
('NV014', 'ND014', 'Đỗ Văn N', '0922345678', '444 Pasteur', '1997-02-14', '2021-02-01', 'Nhân viên kho', 'NV009', 'CH002', 1);

INSERT INTO "KhachHang" ("MaKH", "MaNguoiDung", "HoTen", "SDT", "DiaChi", "NgaySinh", "DiemTichLuy", "LoaiKhachHang", "NgayDangKy") VALUES
('KH001', 'ND015', 'Nguyễn Văn KH1', '0988111222', '123 Q1', '1985-01-01', 100, 'Thường', '2020-01-01'),
('KH002', 'ND016', 'Trần Thị KH2', '0977223344', '456 Q3', '1986-02-02', 200, 'VIP', '2020-02-01'),
('KH003', 'ND017', 'Lê Văn KH3', '0966334455', '789 Gò Vấp', '1987-03-03', 150, 'Thường', '2020-03-01'),
('KH004', 'ND018', 'Phạm Thị KH4', '0955445566', '111 Thủ Đức', '1988-04-04', 50, 'Thường', '2020-04-01'),
('KH005', 'ND019', 'Đỗ Văn KH5', '0944556677', '222 Tân Bình', '1989-05-05', 300, 'Vàng', '2020-05-01'),
('KH006', 'ND020', 'Võ Minh KH6', '0933667788', '15 Bình Thạnh', '1990-06-06', 120, 'Thường', '2020-06-01'),
('KH007', 'ND021', 'Huỳnh Lan KH7', '0922778899', '89 Quận 10', '1991-07-07', 180, 'Bạc', '2020-07-01'),
('KH008', 'ND022', 'Phan Văn KH8', '0911889900', '12 Quận 7', '1992-08-08', 220, 'Vàng', '2020-08-01'),
('KH009', 'ND023', 'Trương Mỹ KH9', '0909000111', '35 Quận 5', '1993-09-09', 80, 'Thường', '2020-09-01'),
('KH010', 'ND024', 'Lâm Quốc KH10', '0988776655', '77 Quận 8', '1994-10-10', 260, 'Kim cương', '2020-10-01');

-- Thêm dữ liệu bảng lương
INSERT INTO "BangLuong" ("MaNV", "ThangLuong", "NamLuong", "LuongCoBan", "PhuCap", "Thuong", "KhauTru", "SoNgayLam", "SoGioLam", "GhiChu", "TrangThai") VALUES
('NV001', 7, 2025, 15000000, 2000000, 1000000, 0, 22, 176, 'Lương tháng 7/2025', 1),
('NV002', 7, 2025, 12000000, 1500000, 800000, 0, 21, 168, 'Lương tháng 7/2025', 1),
('NV003', 7, 2025, 8000000, 500000, 300000, 0, 20, 160, 'Lương tháng 7/2025', 1),
('NV004', 7, 2025, 8500000, 600000, 400000, 0, 21, 168, 'Lương tháng 7/2025', 1),
('NV005', 7, 2025, 7500000, 400000, 200000, 0, 19, 152, 'Lương tháng 7/2025', 1),
('NV006', 7, 2025, 7000000, 300000, 150000, 0, 18, 144, 'Lương tháng 7/2025', 1),
('NV007', 7, 2025, 6500000, 250000, 100000, 0, 17, 136, 'Lương tháng 7/2025', 1),
('NV008', 7, 2025, 15000000, 2000000, 1000000, 0, 22, 176, 'Lương tháng 7/2025', 1),
('NV009', 7, 2025, 12000000, 1500000, 800000, 0, 21, 168, 'Lương tháng 7/2025', 1),
('NV010', 7, 2025, 8000000, 500000, 300000, 0, 20, 160, 'Lương tháng 7/2025', 1),
('NV011', 7, 2025, 8500000, 600000, 400000, 0, 21, 168, 'Lương tháng 7/2025', 1),
('NV012', 7, 2025, 7500000, 400000, 200000, 0, 19, 152, 'Lương tháng 7/2025', 1),
('NV013', 7, 2025, 7000000, 300000, 150000, 0, 18, 144, 'Lương tháng 7/2025', 1),
('NV014', 7, 2025, 6500000, 250000, 100000, 0, 17, 136, 'Lương tháng 7/2025', 1);

-- Tiếp tục với các bảng khác...
INSERT INTO "LoaiSanPham" ("MaLoaiSP", "TenLoai", "MoTa", "MaLoaiCha", "ThuTuHienThi") VALUES
('LSP001', 'Tươi sống', 'Các loại rau củ quả tươi', NULL, 1),
('LSP002', 'Đông lạnh', 'Thực phẩm đông lạnh', NULL, 2),
('LSP003', 'Đồ đóng hộp', 'Thực phẩm đóng hộp', NULL, 3),
('LSP004', 'Đồ uống', 'Các loại nước giải khát', NULL, 4),
('LSP005', 'Sữa & em bé', 'Sản phẩm cho trẻ em', NULL, 5),
('LSP006', 'Gia vị & Dầu ăn', 'Gia vị và dầu ăn', NULL, 6),
('LSP007', 'Hóa phẩm & Tẩy rửa', 'Sản phẩm vệ sinh', NULL, 7);

-- ===================================
-- DỮ LIỆU SẢN PHẨM THEO TỪNG LOẠI
-- ===================================

-- LSP001 – TƯƠI SỐNG (20 sản phẩm)
INSERT INTO "SanPham" ("MaSP", "MaLoaiSP", "TenSP", "MoTa", "GiaBan", "DonViTinh", "TrongLuong", "KichThuoc", "HanSuDung", "TrangThai") VALUES
('SP001', 'LSP001', 'Dưa leo Đà Lạt', '[Ngắn] Dưa tươi ngon sạch. [Dài] Dưa leo Đà Lạt được chọn lọc kỹ càng từ nông trại sạch, vỏ xanh mướt, giòn ngọt, thích hợp cho các món salad, dưa muối hoặc ăn sống trực tiếp.', 15000, 'Kg', 0.5, '20x5cm', 7, 1),
('SP002', 'LSP001', 'Cà chua bi', '[Ngắn] Cà chua bi đỏ mọng. [Dài] Cà chua bi được trồng theo phương pháp hữu cơ, vỏ mỏng, vị ngọt thanh, thích hợp cho ăn sống, làm salad hoặc xào nấu.', 18000, 'Kg', 0.3, '2x2cm', 5, 1),
('SP003', 'LSP001', 'Cải thìa tươi', '[Ngắn] Rau xanh giòn ngọt. [Dài] Cải thìa sạch được thu hoạch trong ngày, giàu vitamin A và C, thường dùng trong các món xào hoặc luộc.', 12000, 'Kg', 0.4, '25x3cm', 3, 1),
('SP004', 'LSP001', 'Cải ngọt Đà Lạt', '[Ngắn] Rau tươi sạch. [Dài] Cải ngọt được trồng trong điều kiện khí hậu mát mẻ Đà Lạt, ít sâu bệnh, thích hợp nấu canh, xào hoặc ăn lẩu.', 13000, 'Kg', 0.3, '20x2cm', 3, 1),
('SP005', 'LSP001', 'Rau muống', '[Ngắn] Rau muống giòn ngon. [Dài] Rau muống tươi được lựa chọn kỹ lưỡng, thân giòn, lá xanh, thích hợp cho các món luộc, xào tỏi hoặc làm gỏi.', 10000, 'Kg', 0.5, '30x2cm', 2, 1),
('SP006', 'LSP001', 'Bắp cải trắng', '[Ngắn] Bắp cải tươi giòn. [Dài] Bắp cải trắng giòn ngọt, có thể dùng để nấu canh, xào hoặc làm dưa muối.', 14000, 'Cái', 1.0, '15x15cm', 7, 1),
('SP007', 'LSP001', 'Cà rốt Đà Lạt', '[Ngắn] Cà rốt giòn ngọt. [Dài] Cà rốt trồng tại Đà Lạt, củ đều màu cam đẹp, giàu beta-carotene tốt cho mắt, thường dùng nấu canh, luộc, xào.', 16000, 'Kg', 0.6, '20x3cm', 10, 1),
('SP008', 'LSP001', 'Khoai tây vàng', '[Ngắn] Khoai tây sạch. [Dài] Khoai tây vàng vỏ mỏng, ít nhựa, thích hợp để chiên, nấu súp hoặc nghiền làm món ăn dặm.', 17000, 'Kg', 0.8, '8x5cm', 14, 1),
('SP009', 'LSP001', 'Hành lá', '[Ngắn] Hành tươi xanh. [Dài] Hành lá được thu hoạch từ vườn sạch, lá xanh, mùi thơm nhẹ, là nguyên liệu không thể thiếu cho các món canh và chiên.', 8000, 'Kg', 0.2, '25x1cm', 5, 1),
('SP010', 'LSP001', 'Rau dền đỏ', '[Ngắn] Rau dền mát gan. [Dài] Rau dền đỏ nhiều sắt, hỗ trợ tuần hoàn máu, thích hợp cho các món canh và luộc.', 9000, 'Kg', 0.3, '20x2cm', 2, 1),
('SP011', 'LSP001', 'Mướp hương', '[Ngắn] Mướp mềm thơm. [Dài] Mướp hương có vị ngọt thanh, mềm, thường được dùng trong các món canh hoặc xào chung với trứng.', 11000, 'Kg', 0.4, '25x4cm', 3, 1),
('SP012', 'LSP001', 'Dưa gang', '[Ngắn] Dưa giải nhiệt. [Dài] Dưa gang mọng nước, vị ngọt nhẹ, được ưa chuộng trong mùa nóng vì tác dụng giải nhiệt, ăn sống hoặc làm sinh tố.', 18000, 'Kg', 0.8, '15x10cm', 5, 1),
('SP013', 'LSP001', 'Rau má', '[Ngắn] Rau má mát gan. [Dài] Rau má có tác dụng thanh nhiệt, giải độc, thường dùng để ép nước hoặc làm gỏi.', 9000, 'Kg', 0.2, '20x2cm', 2, 1),
('SP014', 'LSP001', 'Nấm rơm tươi', '[Ngắn] Nấm mềm ngon. [Dài] Nấm rơm tươi từ nông trại sạch, thích hợp cho các món kho, xào, canh.', 28000, 'Kg', 0.3, '3x3cm', 3, 1),
('SP015', 'LSP001', 'Nấm bào ngư', '[Ngắn] Nấm dai ngon. [Dài] Nấm bào ngư trắng, thịt dày, giòn ngọt, thường dùng trong các món xào, súp hoặc chiên giòn.', 30000, 'Kg', 0.4, '4x2cm', 5, 1),
('SP016', 'LSP001', 'Mồng tơi', '[Ngắn] Rau trơn mát. [Dài] Mồng tơi chứa nhiều chất nhầy, hỗ trợ tiêu hóa, là nguyên liệu quen thuộc trong món canh cua.', 8000, 'Kg', 0.3, '25x2cm', 2, 1),
('SP017', 'LSP001', 'Đậu que', '[Ngắn] Đậu non giòn. [Dài] Đậu que non, xanh mướt, thường được xào với thịt bò hoặc luộc ăn kèm nước chấm.', 14000, 'Kg', 0.3, '15x1cm', 3, 1),
('SP018', 'LSP001', 'Dền cơm', '[Ngắn] Rau dền sạch. [Dài] Dền cơm là loại rau dại giàu dinh dưỡng, được trồng theo hướng hữu cơ, dùng để nấu canh hoặc luộc.', 9000, 'Kg', 0.2, '20x2cm', 2, 1),
('SP019', 'LSP001', 'Rau tần ô', '[Ngắn] Rau thơm ngon. [Dài] Tần ô có hương thơm đặc trưng, thường xuất hiện trong lẩu hoặc nấu canh với thịt bằm.', 11000, 'Kg', 0.3, '25x2cm', 3, 1),
('SP020', 'LSP001', 'Bí đỏ trái tròn', '[Ngắn] Bí đỏ ngọt dẻo. [Dài] Bí đỏ được trồng tại nông trại hữu cơ, giàu vitamin A, thường dùng nấu canh hoặc hấp.', 13000, 'Kg', 1.5, '20x15cm', 7, 1);

-- LSP002 – ĐÔNG LẠNH (20 sản phẩm)
INSERT INTO "SanPham" ("MaSP", "MaLoaiSP", "TenSP", "MoTa", "GiaBan", "DonViTinh", "TrongLuong", "KichThuoc", "HanSuDung", "TrangThai") VALUES
('SP021', 'LSP002', 'Tôm sú đông lạnh', '[Ngắn] Tôm đông lạnh sạch. [Dài] Tôm sú đông lạnh được cấp đông ngay sau khi đánh bắt để giữ độ tươi ngon, thịt chắc và ngọt, dùng để nấu lẩu, hấp, chiên xù.', 120000, 'Kg', 1.0, '15x3cm', 180, 1),
('SP022', 'LSP002', 'Cá hồi phi lê', '[Ngắn] Cá hồi phi lê tươi ngon. [Dài] Cá hồi Na Uy phi lê được cấp đông nhanh, giữ nguyên chất dinh dưỡng và màu sắc tự nhiên, thích hợp cho sashimi hoặc áp chảo.', 230000, 'Kg', 0.8, '20x5cm', 180, 1),
('SP023', 'LSP002', 'Mực ống đông lạnh', '[Ngắn] Mực tươi cấp đông. [Dài] Mực ống được làm sạch và cấp đông nhanh, giữ được độ giòn và vị ngọt tự nhiên, thích hợp nướng, hấp hoặc chiên giòn.', 150000, 'Kg', 0.5, '12x2cm', 180, 1),
('SP024', 'LSP002', 'Cá viên đông lạnh', '[Ngắn] Cá viên tiện lợi. [Dài] Cá viên làm từ cá thát lát nguyên chất, được cấp đông sẵn, tiện lợi cho món lẩu, chiên hoặc nấu canh.', 60000, 'Kg', 0.5, '2x2cm', 180, 1),
('SP025', 'LSP002', 'Thịt bò viên đông lạnh', '[Ngắn] Bò viên thơm ngon. [Dài] Bò viên được chế biến từ thịt bò tươi, có vị thơm đặc trưng, dễ dàng chế biến trong các món lẩu, xào hoặc bún bò.', 65000, 'Kg', 0.5, '2x2cm', 180, 1),
('SP026', 'LSP002', 'Gà nguyên con đông lạnh', '[Ngắn] Gà cấp đông sạch. [Dài] Gà ta nguyên con được làm sạch và cấp đông theo chuẩn VSATTP, phù hợp để quay, luộc hoặc hấp.', 110000, 'Con', 1.5, '25x15cm', 180, 1),
('SP027', 'LSP002', 'Chân gà rút xương đông lạnh', '[Ngắn] Chân gà tiện dụng. [Dài] Chân gà đã được rút xương, cấp đông sạch, dùng để trộn gỏi hoặc nướng muối ớt.', 85000, 'Kg', 0.8, '8x3cm', 180, 1),
('SP028', 'LSP002', 'Cá thu cắt lát đông lạnh', '[Ngắn] Cá thu cắt lát. [Dài] Cá thu được cắt lát và cấp đông nhanh, thích hợp để chiên hoặc kho với nước dừa.', 130000, 'Kg', 0.6, '10x5cm', 180, 1),
('SP029', 'LSP002', 'Xúc xích tiệt trùng', '[Ngắn] Xúc xích đậm vị. [Dài] Xúc xích heo được tiệt trùng và cấp đông, dễ dàng chế biến các món ăn nhanh hoặc nướng BBQ.', 40000, 'Kg', 0.4, '15x2cm', 180, 1),
('SP030', 'LSP002', 'Cá basa phi lê đông lạnh', '[Ngắn] Cá basa tiện lợi. [Dài] Cá basa phi lê đã bỏ xương, không tanh, dễ chế biến các món chiên giòn, kho tộ hoặc nấu canh chua.', 85000, 'Kg', 0.7, '18x4cm', 180, 1),
('SP031', 'LSP002', 'Tôm sú đông lạnh 1kg', 'Tôm tươi ngon được cấp đông nhanh. ... Giữ được vị ngọt tự nhiên và an toàn thực phẩm.', 195000, 'Kg', 1.0, '15x3cm', 180, 1),
('SP032', 'LSP002', 'Cá diêu hồng đông lạnh 1kg', 'Cá được sơ chế sạch sẽ và cấp đông sâu. ... Tiện lợi cho mọi món ăn hằng ngày.', 85000, 'Kg', 1.0, '25x8cm', 180, 1),
('SP033', 'LSP002', 'Mực ống đông lạnh 500g', 'Mực tươi được làm sạch và đóng gói kỹ lưỡng. ... Đảm bảo an toàn và tươi ngon cho bữa cơm gia đình.', 97000, 'Kg', 0.5, '12x2cm', 180, 1),
('SP034', 'LSP002', 'Thịt ba rọi đông lạnh 500g', 'Thịt heo ba rọi thái lát mỏng và đóng gói. ... Phù hợp chế biến món xào, nướng hoặc lẩu.', 72000, 'Kg', 0.5, '10x5cm', 180, 1),
('SP035', 'LSP002', 'Cánh gà đông lạnh 1kg', 'Cánh gà tươi được lựa chọn kỹ càng. ... Cấp đông nhanh giúp bảo quản lâu và giữ nguyên dinh dưỡng.', 105000, 'Kg', 1.0, '12x8cm', 180, 1),
('SP036', 'LSP002', 'Thăn bò đông lạnh 500g', 'Thịt bò thăn nhập khẩu, mềm, thơm. ... Rất thích hợp cho món bít tết hoặc lẩu.', 168000, 'Kg', 0.5, '15x8cm', 180, 1),
('SP037', 'LSP002', 'Hàu nửa vỏ đông lạnh 1kg', 'Hàu biển tươi được sơ chế và cấp đông. ... Dễ chế biến và bổ dưỡng cho cả gia đình.', 125000, 'Kg', 1.0, '8x4cm', 180, 1),
('SP038', 'LSP002', 'Cá viên đông lạnh 500g', 'Cá viên được làm từ cá tươi nghiền nhuyễn. ... Dùng tốt cho món lẩu hoặc chiên.', 55000, 'Kg', 0.5, '2x2cm', 180, 1),
('SP039', 'LSP002', 'Súp lơ đông lạnh 500g', 'Súp lơ tươi cắt nhỏ và cấp đông ngay sau thu hoạch. ... Giữ nguyên độ giòn và hương vị tự nhiên.', 39000, 'Kg', 0.5, '15x10cm', 180, 1),
('SP040', 'LSP002', 'Đậu que đông lạnh 500g', 'Đậu que tươi cấp đông giữ trọn độ giòn và dinh dưỡng. ... Phù hợp chế biến xào, luộc, hấp.', 36000, 'Kg', 0.5, '15x1cm', 180, 1);

-- LSP003 – ĐỒ ĐÓNG HỘP (20 sản phẩm)
INSERT INTO "SanPham" ("MaSP", "MaLoaiSP", "TenSP", "MoTa", "GiaBan", "DonViTinh", "TrongLuong", "KichThuoc", "HanSuDung", "TrangThai") VALUES
('SP041', 'LSP003', 'Cá ngừ ngâm dầu hộp 185g', 'Cá ngừ nguyên miếng ngâm dầu thơm béo. ... Đóng hộp tiện lợi, thích hợp ăn liền hoặc trộn salad.', 32000, 'Hộp', 0.185, '10x8x3cm', 730, 1),
('SP042', 'LSP003', 'Pate gan heo hộp 170g', 'Pate gan heo mềm mịn, thơm ngon. ... Phù hợp cho bữa sáng hoặc món ăn nhẹ giàu đạm.', 26000, 'Hộp', 0.170, '8x6x2cm', 730, 1),
('SP043', 'LSP003', 'Đậu hầm sốt cà hộp 400g', 'Đậu trắng được hầm mềm với sốt cà đậm đà. ... Món ăn bổ dưỡng, tiện lợi cho bữa cơm gia đình.', 23000, 'Hộp', 0.400, '12x8x4cm', 730, 1),
('SP044', 'LSP003', 'Măng chua đóng hộp 400g', 'Măng được sơ chế kỹ và đóng hộp an toàn. ... Dùng nấu canh chua hoặc xào rất tiện lợi.', 19000, 'Hộp', 0.400, '12x8x4cm', 730, 1),
('SP045', 'LSP003', 'Nấm rơm hộp 400g', 'Nấm rơm tươi ngon được đóng hộp giữ nguyên vị. ... Dùng cho các món canh, xào, lẩu cực kỳ tiện.', 25000, 'Hộp', 0.400, '12x8x4cm', 730, 1),
('SP046', 'LSP003', 'Thịt kho trứng hộp 400g', 'Món thịt kho trứng truyền thống được chế biến sẵn. ... Hương vị đậm đà, mở nắp là ăn ngay.', 45000, 'Hộp', 0.400, '12x8x4cm', 730, 1),
('SP047', 'LSP003', 'Chả cá sốt cà hộp 200g', 'Chả cá chiên sốt cà đậm vị, dễ dùng. ... Phù hợp cho các bữa ăn nhanh và vẫn đầy đủ dinh dưỡng.', 29000, 'Hộp', 0.200, '10x6x3cm', 730, 1),
('SP048', 'LSP003', 'Ngô ngọt đóng hộp 400g', 'Ngô ngọt vàng óng, giòn ngọt tự nhiên. ... Có thể ăn liền hoặc chế biến món salad, soup.', 21000, 'Hộp', 0.400, '12x8x4cm', 730, 1),
('SP049', 'LSP003', 'Cá mòi sốt cà hộp 155g', 'Cá mòi được nấu cùng nước sốt cà đậm đà. ... Tiện dụng cho mọi bữa ăn gia đình.', 27000, 'Hộp', 0.155, '8x6x3cm', 730, 1),
('SP050', 'LSP003', 'Thịt hộp lợn vai 340g', 'Thịt lợn được nấu chín, nén hộp, dễ bảo quản. ... Phù hợp đi du lịch, dã ngoại hoặc ăn nhanh.', 37000, 'Hộp', 0.340, '12x8x4cm', 730, 1),
('SP051', 'LSP003', 'Bắp cải muối chua hộp 400g', 'Bắp cải được muối chua vừa vị, giòn ngon. ... Dùng ngay hoặc nấu cùng món thịt đều phù hợp.', 18000, 'Hộp', 0.400, '12x8x4cm', 730, 1),
('SP052', 'LSP003', 'Cà rốt đóng hộp 400g', 'Cà rốt được cắt khúc và hấp chín. ... Tiện lợi cho các món xào, soup hoặc salad.', 22000, 'Hộp', 0.400, '12x8x4cm', 730, 1),
('SP053', 'LSP003', 'Giá đỗ đóng hộp 400g', 'Giá đỗ sạch, giòn ngon được đóng hộp. ... Bổ sung dinh dưỡng và dễ bảo quản lâu dài.', 21000, 'Hộp', 0.400, '12x8x4cm', 730, 1),
('SP054', 'LSP003', 'Cà chua xay hộp 400g', 'Cà chua tươi được nghiền nhuyễn và tiệt trùng. ... Dùng làm nước sốt hoặc nấu canh rất tiện.', 24000, 'Hộp', 0.400, '12x8x4cm', 730, 1),
('SP055', 'LSP003', 'Dưa cải chua hộp 400g', 'Dưa cải muối chua đậm đà hương vị Bắc. ... Thích hợp ăn kèm món thịt kho, canh chua.', 18500, 'Hộp', 0.400, '12x8x4cm', 730, 1),
('SP056', 'LSP003', 'Hạt sen đóng hộp 400g', 'Hạt sen tươi được làm sạch và hấp chín. ... Phù hợp cho món chè, hầm hoặc cháo.', 28000, 'Hộp', 0.400, '12x8x4cm', 730, 1),
('SP057', 'LSP003', 'Dừa non đóng hộp 400g', 'Dừa non thái lát được đóng hộp bảo quản lâu. ... Sử dụng tốt trong món chè hoặc cocktail trái cây.', 31000, 'Hộp', 0.400, '12x8x4cm', 730, 1),
('SP058', 'LSP003', 'Thịt bò hầm hộp 340g', 'Thịt bò hầm mềm, vị đậm đà. ... Món ăn chế biến sẵn phù hợp cho dân văn phòng.', 46000, 'Hộp', 0.340, '12x8x4cm', 730, 1),
('SP059', 'LSP003', 'Nấm bào ngư hộp 400g', 'Nấm bào ngư tươi được đóng hộp tiện lợi. ... Dùng để xào, nấu lẩu hoặc hầm đều ngon.', 27000, 'Hộp', 0.400, '12x8x4cm', 730, 1),
('SP060', 'LSP003', 'Mì bò kho hộp 350g', 'Mì ăn liền với nước dùng bò kho đậm vị. ... Món ăn nhanh đầy đủ năng lượng cho người bận rộn.', 33000, 'Hộp', 0.350, '12x8x4cm', 730, 1);

-- LSP004 – ĐỒ UỐNG (20 sản phẩm)
INSERT INTO "SanPham" ("MaSP", "MaLoaiSP", "TenSP", "MoTa", "GiaBan", "DonViTinh", "TrongLuong", "KichThuoc", "HanSuDung", "TrangThai") VALUES
('SP061', 'LSP004', 'Nước khoáng thiên nhiên 500ml', 'Nước khoáng tinh khiết, giải khát tức thì. ... Giàu khoáng chất, tốt cho sức khỏe, thích hợp sử dụng hàng ngày.', 6000, 'Chai', 0.500, '7x7x20cm', 365, 1),
('SP062', 'LSP004', 'Trà xanh không độ 455ml', 'Trà xanh thanh mát, không đường. ... Giúp giải nhiệt, chống oxy hóa và tăng cường sức khỏe.', 9000, 'Chai', 0.455, '6x6x18cm', 365, 1),
('SP063', 'LSP004', 'Nước tăng lực Red Bull 250ml', 'Nước uống tăng lực hương vị đặc trưng. ... Phù hợp cho người hoạt động thể chất cao, giúp tỉnh táo.', 12000, 'Lon', 0.250, '6x6x12cm', 365, 1),
('SP064', 'LSP004', 'Nước ép cam nguyên chất 330ml', 'Nước ép cam giàu vitamin C, vị tự nhiên. ... Tăng cường đề kháng, tốt cho làn da và hệ miễn dịch.', 18000, 'Chai', 0.330, '6x6x15cm', 180, 1),
('SP065', 'LSP004', 'Sữa đậu nành Fami 200ml', 'Sữa đậu nành nguyên chất từ hạt đậu nành Việt. ... Bổ sung đạm thực vật và tốt cho tim mạch.', 7000, 'Hộp', 0.200, '5x5x10cm', 180, 1),
('SP066', 'LSP004', 'Nước suối Aquafina 1.5L', 'Nước uống tinh khiết được lọc 7 bước. ... Thích hợp dùng cho cả gia đình và mang đi học, đi làm.', 10000, 'Chai', 1.500, '8x8x25cm', 365, 1),
('SP067', 'LSP004', 'Nước ngọt Coca-Cola lon 330ml', 'Nước ngọt có gas hương vị cổ điển. ... Giải khát tức thì, phù hợp với các bữa tiệc và ăn nhanh.', 10000, 'Lon', 0.330, '6x6x12cm', 365, 1),
('SP068', 'LSP004', 'Trà sữa trân châu đóng chai 320ml', 'Trà sữa thơm ngọt, kèm trân châu mềm dai. ... Phù hợp cho giới trẻ, mang đi mọi nơi.', 19000, 'Chai', 0.320, '6x6x15cm', 180, 1),
('SP069', 'LSP004', 'Nước ép táo nguyên chất 330ml', 'Nước ép táo ngọt dịu, không chất bảo quản. ... Tốt cho hệ tiêu hóa và cung cấp vitamin A.', 17500, 'Chai', 0.330, '6x6x15cm', 180, 1),
('SP070', 'LSP004', 'Bò húc Thái chai thủy tinh 250ml', 'Nước tăng lực nhập khẩu hương vị đậm đà. ... Giúp tỉnh táo, bổ sung vitamin B và taurine.', 15000, 'Chai', 0.250, '5x5x12cm', 365, 1),
('SP071', 'LSP004', 'Nước dừa tươi đóng hộp 330ml', 'Nước dừa tự nhiên, giữ nguyên hương vị tươi mát. ... Giàu khoáng và chất điện giải, giải nhiệt tốt.', 14000, 'Hộp', 0.330, '6x6x15cm', 180, 1),
('SP072', 'LSP004', 'Trà đào hương vị trái cây 455ml', 'Trà đào ngọt thanh, mùi thơm dịu nhẹ. ... Dùng lạnh sẽ ngon hơn, hợp mọi lứa tuổi.', 10000, 'Chai', 0.455, '6x6x18cm', 365, 1),
('SP073', 'LSP004', 'Nước yến sào có đường 240ml', 'Nước yến giàu đạm và vi khoáng. ... Hỗ trợ phục hồi sức khỏe, đẹp da và tăng cường sức đề kháng.', 28000, 'Chai', 0.240, '5x5x12cm', 365, 1),
('SP074', 'LSP004', 'Cà phê sữa đá đóng lon 330ml', 'Cà phê Việt đậm đà, hương vị truyền thống. ... Tiện lợi khi di chuyển, giữ nguyên độ ngon như pha máy.', 11000, 'Lon', 0.330, '6x6x12cm', 365, 1),
('SP075', 'LSP004', 'Trà atiso đỏ 500ml', 'Trà atiso đỏ thanh mát, vị chua nhẹ. ... Giúp mát gan, hỗ trợ tiêu hóa và lợi tiểu.', 9000, 'Chai', 0.500, '7x7x20cm', 365, 1),
('SP076', 'LSP004', 'Nước ép nho nguyên chất 330ml', 'Nước ép nho ngọt dịu, giàu vitamin và chất chống oxy hóa. ... Giúp cải thiện làn da và ngăn ngừa lão hóa.', 18000, 'Chai', 0.330, '6x6x15cm', 180, 1),
('SP077', 'LSP004', 'Nước nha đam hạt chia 500ml', 'Nước uống kết hợp nha đam và hạt chia. ... Bổ dưỡng, làm mát cơ thể, đẹp da.', 16000, 'Chai', 0.500, '7x7x20cm', 180, 1),
('SP078', 'LSP004', 'Nước cam có tép 450ml', 'Nước cam có tép thật, vị ngọt dịu tự nhiên. ... Giàu vitamin C, tăng cường miễn dịch và sáng da.', 15000, 'Chai', 0.450, '6x6x18cm', 180, 1),
('SP079', 'LSP004', 'Nước khoáng có gas Vĩnh Hảo 500ml', 'Nước khoáng có gas vị nhẹ nhàng. ... Giúp tiêu hóa tốt, dùng với trái cây tươi rất ngon.', 10000, 'Chai', 0.500, '7x7x20cm', 365, 1),
('SP080', 'LSP004', 'Nước chanh muối đóng chai 350ml', 'Nước chanh muối pha sẵn, vị mặn ngọt hài hòa. ... Giải khát, bù điện giải khi vận động nhiều.', 9500, 'Chai', 0.350, '6x6x15cm', 180, 1);

-- LSP005 – SỮA & EM BÉ (20 sản phẩm)
INSERT INTO "SanPham" ("MaSP", "MaLoaiSP", "TenSP", "MoTa", "GiaBan", "DonViTinh", "TrongLuong", "KichThuoc", "HanSuDung", "TrangThai") VALUES
('SP081', 'LSP005', 'Sữa bột Enfagrow 400g', 'Sữa bột cho trẻ từ 1-3 tuổi, giàu DHA. ... Giúp phát triển trí não, tăng cường miễn dịch và tiêu hóa khỏe.', 245000, 'Hộp', 0.400, '15x10x8cm', 730, 1),
('SP082', 'LSP005', 'Sữa tươi tiệt trùng TH True Milk 180ml', 'Sữa tươi tiệt trùng, vị nguyên chất. ... Giàu canxi, tốt cho xương, phù hợp mọi lứa tuổi.', 7000, 'Hộp', 0.180, '5x5x10cm', 180, 1),
('SP083', 'LSP005', 'Bột ăn dặm Nestle gạo sữa 200g', 'Bột ăn dặm vị gạo sữa dễ tiêu hóa. ... Hỗ trợ bé tập ăn dặm, bổ sung vitamin và khoáng.', 58000, 'Hộp', 0.200, '12x8x6cm', 730, 1),
('SP084', 'LSP005', 'Tã dán Pampers NB 40 miếng', 'Tã dán siêu mềm, thấm hút tốt. ... Giúp bé ngủ ngon, da khô thoáng suốt cả đêm.', 195000, 'Gói', 0.800, '25x15x8cm', 1095, 1),
('SP085', 'LSP005', 'Nước rửa bình sữa D-nee 620ml', 'Dung dịch rửa bình sữa an toàn. ... Không chứa hóa chất độc hại, dễ trôi sạch, không mùi.', 53000, 'Chai', 0.620, '8x8x20cm', 730, 1),
('SP086', 'LSP005', 'Khăn ướt Bobby không mùi 100 tờ', 'Khăn ướt mềm mại, không chứa cồn. ... Phù hợp vệ sinh cho bé, dùng được cho da nhạy cảm.', 33000, 'Gói', 0.300, '15x10x5cm', 730, 1),
('SP087', 'LSP005', 'Sữa chua uống Probi 65ml (lốc 4 chai)', 'Sữa chua uống men sống hỗ trợ tiêu hóa. ... Tăng cường hệ miễn dịch, ngon mát dễ uống.', 16000, 'Lốc', 0.260, '15x10x8cm', 180, 1),
('SP088', 'LSP005', 'Dụng cụ hút mũi cho bé', 'Dụng cụ hút mũi bằng silicon mềm. ... Giúp làm sạch mũi nhẹ nhàng, không gây tổn thương.', 29000, 'Cái', 0.050, '8x3x2cm', 1095, 1),
('SP089', 'LSP005', 'Sữa công thức Friso Gold 900g', 'Sữa công thức dành cho trẻ từ 1-2 tuổi. ... Bổ sung chất xơ GOS, hỗ trợ đường ruột và miễn dịch.', 510000, 'Hộp', 0.900, '20x15x10cm', 730, 1),
('SP090', 'LSP005', 'Bánh ăn dặm Pigeon vị bí đỏ 50g', 'Bánh ăn dặm tan nhanh trong miệng. ... Giúp bé làm quen với đồ ăn, dễ cầm nắm.', 45000, 'Hộp', 0.050, '10x8x3cm', 730, 1),
('SP091', 'LSP005', 'Sữa rửa mặt cho mẹ bầu Organic 100ml', 'Sữa rửa mặt thiên nhiên cho da nhạy cảm. ... Không chứa paraben, dịu nhẹ và an toàn.', 79000, 'Chai', 0.100, '6x6x15cm', 730, 1),
('SP092', 'LSP005', 'Dầu gội em bé Johnson 200ml', 'Dầu gội dịu nhẹ, không cay mắt. ... Làm sạch tóc và da đầu cho bé mà không gây kích ứng.', 57000, 'Chai', 0.200, '7x7x18cm', 730, 1),
('SP093', 'LSP005', 'Thermometer đo trán điện tử', 'Nhiệt kế hồng ngoại đo trán nhanh chóng. ... Cho kết quả chính xác trong vài giây, an toàn.', 195000, 'Cái', 0.100, '10x3x2cm', 1095, 1),
('SP094', 'LSP005', 'Sữa nước Grow Plus đỏ 180ml', 'Sữa dành cho bé nhẹ cân, suy dinh dưỡng. ... Giúp tăng cân đều, phát triển khỏe mạnh.', 12000, 'Hộp', 0.180, '5x5x10cm', 180, 1),
('SP095', 'LSP005', 'Bình sữa Avent nhựa PP 260ml', 'Bình sữa cổ rộng, van chống sặc. ... Giúp bé bú dễ dàng, không bị đầy hơi.', 230000, 'Cái', 0.150, '8x8x20cm', 1095, 1),
('SP096', 'LSP005', 'Nước muối sinh lý BabyCare 500ml', 'Nước muối sinh lý dùng nhỏ mũi cho bé. ... Làm sạch nhẹ nhàng, hỗ trợ phòng ngừa viêm mũi.', 18000, 'Chai', 0.500, '7x7x20cm', 730, 1),
('SP097', 'LSP005', 'Trái cây nghiền Hipp táo chuối 125g', 'Trái cây nghiền sẵn, vị ngọt tự nhiên. ... Cung cấp vitamin C, giúp bé ăn ngon miệng.', 40000, 'Hộp', 0.125, '8x6x4cm', 730, 1),
('SP098', 'LSP005', 'Sữa tươi tiệt trùng Dutch Lady 110ml', 'Sữa tươi vị socola hoặc dâu. ... Bổ sung dưỡng chất, ngon miệng dễ uống.', 5000, 'Hộp', 0.110, '4x4x8cm', 180, 1),
('SP099', 'LSP005', 'Bàn chải răng silicon cho bé 6 tháng+', 'Bàn chải mềm, an toàn cho bé. ... Giúp bé tập đánh răng ngay từ sớm.', 29000, 'Cái', 0.050, '12x2x1cm', 1095, 1),
('SP100', 'LSP005', 'Balo y tá đựng đồ sơ sinh', 'Balo chuyên dụng mang theo khi ra ngoài. ... Có nhiều ngăn, dễ sắp xếp đồ dùng cho bé.', 155000, 'Cái', 0.800, '30x20x15cm', 1095, 1);

-- LSP006 – GIA VỊ & DẦU ĂN (20 sản phẩm)
INSERT INTO "SanPham" ("MaSP", "MaLoaiSP", "TenSP", "MoTa", "GiaBan", "DonViTinh", "TrongLuong", "KichThuoc", "HanSuDung", "TrangThai") VALUES
('SP101', 'LSP006', 'Nước mắm Nam Ngư 500ml', 'Nước mắm truyền thống đậm đà. ... Được ủ từ cá cơm, hương vị tự nhiên, dùng nêm nếm và chấm.', 24000, 'Chai', 0.500, '7x7x20cm', 1095, 1),
('SP102', 'LSP006', 'Nước tương Maggi đậm đặc 700ml', 'Nước tương đậm đà, hương vị quen thuộc. ... Thích hợp ăn kèm món luộc, chiên, xào.', 32000, 'Chai', 0.700, '8x8x25cm', 1095, 1),
('SP103', 'LSP006', 'Dầu ăn Tường An 1L', 'Dầu thực vật nguyên chất. ... Giàu vitamin A, E tốt cho tim mạch và sức khỏe.', 42000, 'Chai', 1.000, '8x8x25cm', 1095, 1),
('SP104', 'LSP006', 'Muối i-ốt 500g', 'Muối trắng tinh khiết có bổ sung i-ốt. ... Giúp phòng ngừa bướu cổ và tăng cường sức khỏe.', 8000, 'Gói', 0.500, '15x10x2cm', 1095, 1),
('SP105', 'LSP006', 'Hạt nêm Knorr thịt thăn 400g', 'Hạt nêm vị thịt thăn xương ống. ... Giúp món ăn đậm vị, thơm ngon hơn.', 45000, 'Hộp', 0.400, '12x8x6cm', 1095, 1),
('SP106', 'LSP006', 'Tiêu đen xay Dh Foods 50g', 'Tiêu đen xay mịn, thơm nồng. ... Tăng hương vị cho các món kho, nướng, súp.', 29000, 'Hộp', 0.050, '8x6x3cm', 1095, 1),
('SP107', 'LSP006', 'Tỏi băm sẵn 200g', 'Tỏi tươi xay nhuyễn, tiện lợi khi nấu ăn. ... Giữ nguyên hương vị và mùi thơm tự nhiên.', 17000, 'Hộp', 0.200, '10x8x4cm', 180, 1),
('SP108', 'LSP006', 'Hành phi giòn 100g', 'Hành phi vàng thơm, giòn rụm. ... Dùng rắc lên cơm, cháo, bún, phở tăng hương vị.', 23000, 'Hộp', 0.100, '8x6x3cm', 365, 1),
('SP109', 'LSP006', 'Dầu hào Maggi 350g', 'Dầu hào vị ngọt thanh. ... Dùng để xào rau, thịt giúp món ăn thêm đậm đà, bóng đẹp.', 27000, 'Chai', 0.350, '7x7x18cm', 1095, 1),
('SP110', 'LSP006', 'Ớt bột Hàn Quốc 100g', 'Ớt bột vị cay nhẹ, màu đẹp. ... Dùng làm kim chi, lẩu, các món cay kiểu Hàn.', 38000, 'Hộp', 0.100, '8x6x3cm', 1095, 1),
('SP111', 'LSP006', 'Bột nghệ nguyên chất 100g', 'Bột nghệ vàng nguyên chất. ... Dùng ướp thịt, làm bánh, tốt cho tiêu hóa.', 25000, 'Hộp', 0.100, '8x6x3cm', 1095, 1),
('SP112', 'LSP006', 'Giấm gạo Lâm Thủy 500ml', 'Giấm gạo lên men tự nhiên. ... Dùng trộn gỏi, pha nước chấm, khử mùi tanh.', 16000, 'Chai', 0.500, '7x7x20cm', 1095, 1),
('SP113', 'LSP006', 'Dầu mè đen Lee Kum Kee 200ml', 'Dầu mè nguyên chất thơm ngon. ... Tăng hương vị cho món Nhật, Hàn, salad.', 46000, 'Chai', 0.200, '6x6x15cm', 1095, 1),
('SP114', 'LSP006', 'Bột canh Hải Châu 190g', 'Bột canh pha sẵn muối, bột ngọt. ... Dùng để nêm nếm tiện lợi, nhanh chóng.', 11000, 'Hộp', 0.190, '10x8x4cm', 1095, 1),
('SP115', 'LSP006', 'Nước cốt dừa Aroy-D 400ml', 'Nước cốt dừa đóng hộp thơm béo. ... Dùng nấu chè, cà ri, bánh, món Thái.', 34000, 'Hộp', 0.400, '12x8x4cm', 1095, 1),
('SP116', 'LSP006', 'Bột ngọt Ajinomoto 400g', 'Bột ngọt giúp làm nổi bật vị ngọt tự nhiên. ... Phù hợp cho mọi món ăn.', 28000, 'Hộp', 0.400, '12x8x6cm', 1095, 1),
('SP117', 'LSP006', 'Bột sả khô 50g', 'Sả khô xay nhuyễn. ... Dùng tẩm ướp thịt nướng, món chay, món kho.', 15000, 'Hộp', 0.050, '8x6x3cm', 1095, 1),
('SP118', 'LSP006', 'Tương ớt Chin-Su 250g', 'Tương ớt cay vừa, màu sắc hấp dẫn. ... Dùng chấm đồ chiên, rán, ăn với phở, bún.', 12000, 'Chai', 0.250, '6x6x15cm', 1095, 1),
('SP119', 'LSP006', 'Nước màu dừa Bến Tre 250ml', 'Nước hàng kho cá, kho thịt. ... Giúp món ăn lên màu đẹp, vị ngọt thanh.', 20000, 'Chai', 0.250, '6x6x15cm', 1095, 1),
('SP120', 'LSP006', 'Nước mắm Phú Quốc truyền thống 520ml', 'Nước mắm nguyên chất cá cơm. ... Đậm đà, thơm ngon đúng chất nước mắm xưa.', 68000, 'Chai', 0.520, '7x7x20cm', 1095, 1);

-- LSP007 – HÓA PHẨM & TẨY RỬA (20 sản phẩm)
INSERT INTO "SanPham" ("MaSP", "MaLoaiSP", "TenSP", "MoTa", "GiaBan", "DonViTinh", "TrongLuong", "KichThuoc", "HanSuDung", "TrangThai") VALUES
('SP121', 'LSP007', 'Nước rửa chén Sunlight chanh 750ml', 'Nước rửa chén hương chanh. ... Tẩy sạch dầu mỡ, dịu nhẹ với da tay.', 28000, 'Chai', 0.750, '8x8x25cm', 1095, 1),
('SP122', 'LSP007', 'Nước lau sàn Gift lavender 1L', 'Nước lau sàn hương oải hương. ... Diệt khuẩn, khử mùi hiệu quả, sàn sạch bóng.', 34000, 'Chai', 1.000, '8x8x25cm', 1095, 1),
('SP123', 'LSP007', 'Nước giặt Omo Matic 2.7kg', 'Nước giặt cho máy giặt cửa ngang. ... Đánh bay vết bẩn, lưu hương thơm lâu.', 132000, 'Chai', 2.700, '15x10x25cm', 1095, 1),
('SP124', 'LSP007', 'Nước xả vải Downy hương nắng mai 800ml', 'Nước xả làm mềm vải. ... Giữ mùi thơm mát, giúp quần áo luôn mềm mại.', 49000, 'Chai', 0.800, '8x8x25cm', 1095, 1),
('SP125', 'LSP007', 'Nước tẩy toilet Duck 900ml', 'Tẩy rửa toilet diệt khuẩn. ... Làm sạch và khử mùi bồn cầu hiệu quả.', 36000, 'Chai', 0.900, '8x8x25cm', 1095, 1),
('SP126', 'LSP007', 'Nước rửa tay Lifebuoy 500ml', 'Rửa tay diệt khuẩn 99.9%. ... Hương thơm dễ chịu, bảo vệ tay sạch khuẩn.', 42000, 'Chai', 0.500, '7x7x20cm', 1095, 1),
('SP127', 'LSP007', 'Nước lau kính Gift 500ml', 'Nước lau kính chống bám bụi. ... Cho bề mặt kính sáng bóng, không vệt.', 26000, 'Chai', 0.500, '7x7x20cm', 1095, 1),
('SP128', 'LSP007', 'Nước tẩy đa năng CIF 500ml', 'Tẩy rửa vết bẩn cứng đầu. ... Dùng cho nhà bếp, nhà tắm, vật dụng inox.', 45000, 'Chai', 0.500, '7x7x20cm', 1095, 1),
('SP129', 'LSP007', 'Bột giặt Ariel hương Downy 3.8kg', 'Bột giặt sạch sâu, thơm lâu. ... Loại bỏ vết bẩn, giữ màu vải bền đẹp.', 125000, 'Hộp', 3.800, '20x15x25cm', 1095, 1),
('SP130', 'LSP007', 'Khăn giấy Bless You hộp 200 tờ', 'Giấy mềm mịn, thấm hút tốt. ... Dùng lau mặt, dùng trong gia đình, văn phòng.', 29000, 'Hộp', 0.200, '15x10x8cm', 1095, 1),
('SP131', 'LSP007', 'Giấy vệ sinh Pulppy 10 cuộn', 'Giấy vệ sinh trắng mềm. ... An toàn cho da, phù hợp gia đình và văn phòng.', 49000, 'Gói', 0.800, '25x15x8cm', 1095, 1),
('SP132', 'LSP007', 'Nước rửa bình sữa D-nee 620ml', 'Rửa sạch bình sữa, đồ dùng trẻ em. ... Dịu nhẹ, an toàn cho bé sơ sinh.', 52000, 'Chai', 0.620, '8x8x20cm', 730, 1),
('SP133', 'LSP007', 'Bông gòn y tế 100g', 'Bông trắng sạch, không tạp chất. ... Dùng lau chùi vết thương, vệ sinh cá nhân.', 17000, 'Gói', 0.100, '15x10x3cm', 1095, 1),
('SP134', 'LSP007', 'Bàn chải vệ sinh nhà tắm đa năng', 'Thiết kế chắc chắn, dễ cầm. ... Làm sạch ngóc ngách nhà tắm, bồn rửa.', 32000, 'Cái', 0.200, '25x5x2cm', 1095, 1),
('SP135', 'LSP007', 'Khăn ướt Mamamy 100 tờ', 'Khăn mềm, không cồn. ... Dùng lau mặt, tay chân cho bé và người lớn.', 37000, 'Gói', 0.300, '15x10x5cm', 730, 1),
('SP136', 'LSP007', 'Bình xịt côn trùng Raid 600ml', 'Diệt muỗi, gián hiệu quả. ... Hương nhẹ, dùng an toàn trong nhà.', 69000, 'Chai', 0.600, '8x8x20cm', 1095, 1),
('SP137', 'LSP007', 'Nước súc miệng Listerine 250ml', 'Làm sạch miệng, khử mùi. ... Giúp hơi thở thơm mát, bảo vệ răng miệng.', 49000, 'Chai', 0.250, '6x6x15cm', 1095, 1),
('SP138', 'LSP007', 'Bột thông cống Hando 100g', 'Làm tan chất thải hữu cơ. ... Thông tắc ống thoát nước, không gây hại đường ống.', 14000, 'Gói', 0.100, '10x8x3cm', 1095, 1),
('SP139', 'LSP007', 'Nước diệt khuẩn Dettol 500ml', 'Sát khuẩn mạnh mẽ, đa năng. ... Pha loãng để lau sàn, giặt đồ, vệ sinh da.', 87000, 'Chai', 0.500, '7x7x20cm', 1095, 1),
('SP140', 'LSP007', 'Găng tay cao su Latex', 'Găng tay dẻo, co giãn tốt. ... Dùng khi rửa chén, lau dọn, an toàn cho da tay.', 22000, 'Đôi', 0.050, '20x10x2cm', 1095, 1);

-- Thêm dữ liệu khuyến mãi
INSERT INTO "KhuyenMai" ("MaKM", "TenChuongTrinh", "MoTa", "LoaiKM", "GiaTriKM", "DieuKienApDung", "NgayBatDau", "NgayKetThuc", "SoLuongToiDa", "DaSuDung", "MaQuanLy", "TrangThai") VALUES
('KMSP001', 'Giảm giá tháng 7', 'Giảm giá cho tất cả mặt hàng', 'PhầnTrăm', 10.0, 'Áp dụng cho tất cả sản phẩm', '2025-07-01 00:00:00', '2025-07-31 23:59:59', 1000, 50, 'NV002', 1),
('KMSP002', 'Tặng điểm tích lũy', 'Tặng điểm cho khách hàng VIP', 'Điểm', 50, 'Khách hàng VIP trở lên', '2025-07-01 00:00:00', '2025-07-31 23:59:59', 500, 25, 'NV002', 1),
('KMSP003', 'Mua 1 tặng 1', 'Áp dụng cho sản phẩm mỹ phẩm', 'MuaXTangY', 0, 'Mua 1 sản phẩm tặng 1 sản phẩm cùng loại', '2025-07-10 00:00:00', '2025-07-20 23:59:59', 200, 10, 'NV002', 1);

-- Thêm dữ liệu phương thức thanh toán
INSERT INTO "PhuongThucThanhToan" ("MaPTTT", "TenPTTT", "MoTa", "PhiGiaoDich", "TrangThai") VALUES
('PTTT001', 'Tiền Mặt', 'Thanh toán bằng tiền mặt', 0, 1),
('PTTT002', 'Chuyển Khoản', 'Thanh toán qua ngân hàng', 0.5, 1),
('PTTT003', 'MoMo', 'Thanh toán bằng ví điện tử MoMo', 1.0, 1),
('PTTT004', 'ZaloPay', 'Thanh toán qua ZaloPay', 1.0, 1),
('PTTT005', 'Thẻ Tín Dụng', 'Thanh toán bằng thẻ tín dụng', 2.0, 1);

-- Thêm dữ liệu kho
INSERT INTO "Kho" ("TenKho", "DiaChi", "DienTich", "SucChua", "MaCH", "TrangThai") VALUES
('Kho EasyMart1', '123 Nguyễn Xí, Bình Thạnh', 500.00, 1000000.00, 'CH001', 1),
('Kho EasyMart2', '456 Nguyễn Văn Trối, Phú Nhuận', 600.00, 1200000.00, 'CH002', 1);

-- Thêm dữ liệu ca làm việc
INSERT INTO "CaLamViec" ("TenCa", "GioBatDau", "GioKetThuc", "TrangThai") VALUES
('Sáng', '08:00:00', '12:00:00', 1),
('Chiều', '13:00:00', '17:00:00', 1),
('Tối', '18:00:00', '22:00:00', 1),
('Cả ngày', '08:00:00', '22:00:00', 1),
('Ca đêm', '22:00:00', '07:00:00', 1);

-- Thêm dữ liệu giá sản phẩm
INSERT INTO "GiaSanPham" ("MaSP", "Gia", "NgayBatDau", "NgayKetThuc", "LyDoThayDoi", "NguoiThayDoi") VALUES
('SP001', 15000, '2025-07-01', '2025-07-31', 'Giá mới tháng 7', 'NV002'),
('SP002', 18000, '2025-07-01', '2025-07-31', 'Giá mới tháng 7', 'NV002'),
('SP003', 12000, '2025-07-01', '2025-07-31', 'Giá mới tháng 7', 'NV002'),
('SP004', 13000, '2025-07-01', '2025-07-31', 'Giá mới tháng 7', 'NV002'),
('SP005', 10000, '2025-07-01', '2025-07-31', 'Giá mới tháng 7', 'NV002');

-- Thêm dữ liệu tồn kho chi tiết
INSERT INTO "TonKhoChiTiet" ("MaSP", "MaKho", "SoLuongTon", "SoLuongToiThieu", "SoLuongToiDa") VALUES
('SP001', 1, 100, 20, 200),
('SP002', 1, 200, 30, 300),
('SP003', 1, 150, 25, 250),
('SP004', 1, 80, 15, 150),
('SP005', 1, 90, 20, 180),
('SP001', 2, 90, 20, 200),
('SP002', 2, 150, 30, 300),
('SP003', 2, 130, 25, 250),
('SP004', 2, 70, 15, 150),
('SP005', 2, 100, 20, 180);

-- Thêm dữ liệu phiếu nhập hàng
INSERT INTO "PhieuNhapHang" ("MaNCC", "MaKho", "MaNVLap", "NgayNhap", "TongTienNhap", "TrangThai", "GhiChu") VALUES
('NCC001', 1, 'NV002', '2025-07-01 09:00:00', 5500000, 1, 'Nhập hàng tháng 7'),
('NCC002', 1, 'NV003', '2025-07-01 10:00:00', 2800000, 1, 'Nhập hàng tháng 7'),
('NCC003', 2, 'NV004', '2025-07-01 11:00:00', 8000000, 1, 'Nhập hàng tháng 7');

-- Thêm dữ liệu chi tiết phiếu nhập
INSERT INTO "ChiTietPhieuNhap" ("MaPN", "MaSP", "SoLuongNhap", "DonGiaNhap", "NgayHetHan", "SoLo", "NgaySanXuat") VALUES
(1, 'SP001', 100, 12000, '2025-12-31', 'LOT001', '2025-06-15'),
(1, 'SP002', 200, 15000, '2025-11-30', 'LOT002', '2025-06-20'),
(2, 'SP003', 50, 10000, '2025-10-31', 'LOT003', '2025-06-25'),
(2, 'SP004', 80, 11000, '2025-09-30', 'LOT004', '2025-06-30'),
(3, 'SP005', 50, 8000, '2025-08-31', 'LOT005', '2025-07-01');

-- Thêm dữ liệu phiếu xuất kho
INSERT INTO "PhieuXuatKho" ("MaKho", "MaNVLap", "NgayXuat", "TongSoLuong", "TongGiaTri", "LyDoXuat", "TrangThai", "GhiChu") VALUES
(1, 'NV002', '2025-07-05 08:00:00', 180, 3240000, 'Bán hàng', 1, 'Xuất kho bán hàng'),
(1, 'NV003', '2025-07-06 09:00:00', 200, 3600000, 'Bán hàng', 1, 'Xuất kho bán hàng'),
(2, 'NV005', '2025-07-07 10:00:00', 140, 2520000, 'Chuyển kho', 1, 'Chuyển kho giữa các cửa hàng');

-- Thêm dữ liệu chi tiết phiếu xuất
INSERT INTO "ChiTietPhieuXuat" ("MaPXK", "MaSP", "SoLuongXuat", "DonGiaXuat") VALUES
(1, 'SP001', 40, 15000),
(1, 'SP002', 60, 18000),
(1, 'SP003', 30, 12000),
(2, 'SP004', 40, 13000),
(2, 'SP005', 30, 10000),
(3, 'SP001', 50, 15000),
(3, 'SP002', 40, 18000),
(3, 'SP003', 50, 12000);

-- Thêm dữ liệu hóa đơn
INSERT INTO "HoaDon" ("MaKH", "MaNVLap", "MaKM", "NgayLap", "TongTienHang", "TienGiamGia", "MaPTTT", "TrangThai", "DiemTichLuy", "GhiChu", "NguoiTao") VALUES
('KH001', 'NV002', 'KMSP001', '2025-07-11 14:00:00', 500000, 50000, 'PTTT001', 1, 50, 'Hóa đơn tháng 7', 'NV002'),
('KH002', 'NV003', 'KMSP002', '2025-07-11 15:00:00', 750000, 0, 'PTTT002', 1, 75, 'Hóa đơn tháng 7', 'NV003'),
('KH003', 'NV005', NULL, '2025-07-11 16:00:00', 900000, 0, 'PTTT001', 2, 90, 'Hóa đơn tháng 7', 'NV005'),
('KH004', 'NV006', NULL, '2025-07-12 08:30:00', 200000, 0, 'PTTT002', 3, 20, 'Hóa đơn tháng 7', 'NV006'),
('KH005', 'NV001', 'KMSP003', '2025-07-12 09:45:00', 300000, 0, 'PTTT001', 1, 30, 'Hóa đơn tháng 7', 'NV001');

-- Thêm dữ liệu chi tiết hóa đơn
INSERT INTO "ChiTietHoaDon" ("MaHD", "MaSP", "SoLuong", "DonGiaBan", "GiamGia") VALUES
(1, 'SP001', 2, 15000, 0),
(1, 'SP002', 5, 18000, 0),
(2, 'SP003', 1, 12000, 0),
(3, 'SP004', 2, 13000, 0),
(4, 'SP005', 2, 10000, 0),
(5, 'SP001', 3, 15000, 0);

-- Thêm dữ liệu khuyến mãi sản phẩm
INSERT INTO "KhuyenMaiSanPham" ("MaKM", "MaSP", "NgayBatDau", "NgayKetThuc") VALUES
('KMSP001', 'SP001', '2025-07-01 00:00:00', '2025-07-31 23:59:59'),
('KMSP001', 'SP002', '2025-07-01 00:00:00', '2025-07-31 23:59:59'),
('KMSP002', 'SP003', '2025-07-01 00:00:00', '2025-07-31 23:59:59'),
('KMSP003', 'SP004', '2025-07-10 00:00:00', '2025-07-20 23:59:59'),
('KMSP003', 'SP005', '2025-07-10 00:00:00', '2025-07-20 23:59:59');

-- Thêm dữ liệu khuyến mãi khách hàng
INSERT INTO "KhuyenMaiKhachHang" ("MaKM", "MaKH", "NgayApDung", "DaSuDung") VALUES
('KMSP001', 'KH001', '2025-07-01 00:00:00', FALSE),
('KMSP001', 'KH002', '2025-07-01 00:00:00', FALSE),
('KMSP002', 'KH003', '2025-07-01 00:00:00', FALSE),
('KMSP003', 'KH004', '2025-07-10 00:00:00', FALSE),
('KMSP003', 'KH005', '2025-07-10 00:00:00', FALSE);

-- Thêm dữ liệu thanh toán
INSERT INTO "ThanhToan" ("MaHD", "MaPTTT", "SoTienThanhToan", "NgayGioTT", "TrangThaiTT", "MaGiaoDichNganHang", "GhiChu") VALUES
(1, 'PTTT001', 450000, '2025-07-11 14:05:00', 1, NULL, 'Thanh toán tiền mặt'),
(2, 'PTTT002', 750000, '2025-07-11 15:10:00', 1, 'GD001', 'Chuyển khoản ngân hàng'),
(3, 'PTTT001', 900000, '2025-07-11 16:20:00', 0, NULL, 'Đang xử lý'),
(4, 'PTTT002', 200000, '2025-07-12 09:00:00', 3, 'GD002', 'Giao dịch bị hủy'),
(5, 'PTTT001', 300000, '2025-07-12 10:00:00', 1, NULL, 'Thanh toán tiền mặt');

-- Thêm dữ liệu giỏ hàng
INSERT INTO "GioHang" ("MaKH", "MaNV", "NgayTao", "TrangThai", "GhiChu") VALUES
('KH001', 'NV002', '2025-07-10 10:00:00', 0, 'Đang chọn hàng'),
('KH002', 'NV003', '2025-07-10 11:00:00', 0, 'Đang chọn hàng'),
('KH003', 'NV005', '2025-07-10 12:00:00', 1, 'Đã đặt hàng'),
('KH004', 'NV006', '2025-07-10 13:00:00', 2, 'Đã thanh toán'),
('KH005', 'NV001', '2025-07-10 14:00:00', 3, 'Đã hủy');

-- Thêm dữ liệu chi tiết giỏ hàng
INSERT INTO "ChiTietGioHang" ("MaGH", "MaSP", "SoLuong", "DonGiaHienTai") VALUES
(1, 'SP001', 2, 15000),
(2, 'SP002', 5, 18000),
(3, 'SP003', 1, 12000),
(4, 'SP004', 2, 13000),
(5, 'SP005', 3, 10000);

-- Thêm dữ liệu thống kê báo cáo
INSERT INTO "ThongKeBaoCao" ("MaCH", "MaNV", "LoaiBaoCao", "TenBaoCao", "ThoiGianTu", "ThoiGianDen", "SoTien", "SoLuong", "NgayBaoCao", "NoiDung", "TrangThai") VALUES
('CH001', 'NV001', 'DoanhThu', 'Báo cáo doanh thu Q1', '2025-07-01 00:00:00', '2025-07-31 23:59:59', 5000000, 1000, '2025-07-10 18:00:00', 'Báo cáo doanh thu Q1', 1),
('CH002', 'NV008', 'ChiPhi', 'Báo cáo chi phí Q3', '2025-07-01 00:00:00', '2025-07-31 23:59:59', 1500000, 500, '2025-07-10 18:30:00', 'Báo cáo chi phí Q3', 1);

-- Cập nhật dữ liệu LichLamViec
INSERT INTO "LichLamViec" ("MaNV", "MaCa", "NgayLam", "MaNVQuanLy", "TrangThai", "NgayDuyet", "GhiChu") VALUES
-- Ca cho cửa hàng CH001
('NV002', 1, '2025-07-12', 'NV001', 1, '2025-07-11 10:00:00', 'Ca sáng'),
('NV003', 2, '2025-07-12', 'NV001', 1, '2025-07-11 10:00:00', 'Ca chiều'),
('NV004', 3, '2025-07-12', 'NV001', 1, '2025-07-11 10:00:00', 'Ca tối'),
('NV005', 1, '2025-07-13', 'NV001', 1, '2025-07-12 10:00:00', 'Ca sáng'),
('NV006', 2, '2025-07-13', 'NV001', 1, '2025-07-12 10:00:00', 'Ca chiều'),
('NV007', 3, '2025-07-13', 'NV001', 1, '2025-07-12 10:00:00', 'Ca tối'),
('NV008', 1, '2025-07-14', 'NV001', 0, NULL, 'Đang chờ duyệt'),

-- Ca cho cửa hàng CH002
('NV009', 1, '2025-07-12', 'NV002', 1, '2025-07-11 10:00:00', 'Ca sáng'),
('NV010', 2, '2025-07-12', 'NV002', 1, '2025-07-11 10:00:00', 'Ca chiều'),
('NV011', 3, '2025-07-12', 'NV002', 1, '2025-07-11 10:00:00', 'Ca tối'),
('NV012', 1, '2025-07-13', 'NV002', 1, '2025-07-12 10:00:00', 'Ca sáng'),
('NV013', 2, '2025-07-13', 'NV002', 1, '2025-07-12 10:00:00', 'Ca chiều'),
('NV014', 3, '2025-07-13', 'NV002', 1, '2025-07-12 10:00:00', 'Ca tối');


-- ===================================
-- PROCEDURE INSERTPRODUCTIMAGES
-- ===================================

-- Chèn hình ảnh mẫu cho tất cả sản phẩm
CREATE OR REPLACE PROCEDURE "InsertProductImages"()
LANGUAGE plpgsql
AS $$
DECLARE
    product_record RECORD;
BEGIN
    -- Sử dụng vòng lặp FOR để duyệt qua kết quả của câu lệnh SELECT
    -- Cách này tự động xử lý việc mở, fetch và đóng cursor.
    FOR product_record IN SELECT "MaSP", "TenSP" FROM "SanPham"
    LOOP
        -- Chèn hình chính
        INSERT INTO "HinhAnh" ("MaSP", "URL", "MoTa", "LaChinh", "ThuTuHienThi")
        VALUES (product_record."MaSP", product_record."MaSP" || '_main.jpg',
                'Hình chính ' || product_record."TenSP", TRUE, 1);

        -- Chèn hình góc nghiêng 1
        INSERT INTO "HinhAnh" ("MaSP", "URL", "MoTa", "LaChinh", "ThuTuHienThi")
        VALUES (product_record."MaSP", product_record."MaSP" || '_main1.jpg',
                'Góc nghiêng 1 ' || product_record."TenSP", FALSE, 2);

        -- Chèn hình góc nghiêng 2
        INSERT INTO "HinhAnh" ("MaSP", "URL", "MoTa", "LaChinh", "ThuTuHienThi")
        VALUES (product_record."MaSP", product_record."MaSP" || '_main2.jpg',
                'Góc nghiêng 2 ' || product_record."TenSP", FALSE, 3);
    END LOOP;
END;
$$;

-- Thực thi stored procedure để chèn dữ liệu hình ảnh
CALL "InsertProductImages"();
