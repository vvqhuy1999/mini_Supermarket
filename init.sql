
-- Tạo database quản lý siêu thị
CREATE DATABASE IF NOT EXISTS QuanLySieuThi;
USE QuanLySieuThi;

-- Xóa database nếu cần thiết lập lại
-- DROP DATABASE QuanLySieuThi;

-- ===== TẠO CÁC BẢNG CHÍNH =====

-- Bảng quản lý thông tin người dùng hệ thống
CREATE TABLE NguoiDung (
                           MaNguoiDung NVARCHAR(50) PRIMARY KEY,
                           Email NVARCHAR(50) UNIQUE NOT NULL,
                           MatKhau NVARCHAR(255) NOT NULL,
                           VaiTro INT NOT NULL COMMENT '0=Quản trị, 1=Quản lý, 2=Nhân viên, 3=Khách hàng',
                           NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
                           IsDeleted BIT DEFAULT 0,

                           CHECK (VaiTro IN (0, 1, 2, 3)),
                           INDEX idx_nguoidung_email (Email),
                           INDEX idx_nguoidung_vaitro (VaiTro)
);

-- Bảng quản lý thông tin các cửa hàng trong hệ thống
CREATE TABLE CuaHang (
                         MaCH NVARCHAR(50) PRIMARY KEY,
                         TenCH NVARCHAR(255) NOT NULL,
                         DiaChi NVARCHAR(255),
                         SDT NVARCHAR(15),
                         NgayThanhLap DATE,
                         TrangThai INT DEFAULT 1 COMMENT '0=Đóng cửa, 1=Hoạt động',
                         IsDeleted BIT DEFAULT 0,

                         INDEX idx_cuahang_trangthai (TrangThai)
);

-- Bảng quản lý thông tin nhà cung cấp sản phẩm
CREATE TABLE NhaCungCap (
                            MaNCC NVARCHAR(50) PRIMARY KEY,
                            TenNCC NVARCHAR(255) NOT NULL,
                            DiaChi NVARCHAR(255),
                            SDT NVARCHAR(15),
                            Email NVARCHAR(100),
                            ThongTinHopDong LONGTEXT,
                            NgayHopTac DATE,
                            TrangThai INT DEFAULT 1 COMMENT '0=Ngừng hợp tác, 1=Đang hợp tác',
                            IsDeleted BIT DEFAULT 0,

                            INDEX idx_nhacungcap_trangthai (TrangThai)
);

-- Bảng quản lý thông tin nhân viên làm việc tại cửa hàng
CREATE TABLE NhanVien (
                          MaNV NVARCHAR(50) PRIMARY KEY,
                          MaNguoiDung NVARCHAR(50),
                          HoTen NVARCHAR(255) NOT NULL,
                          SDT NVARCHAR(15),
                          DiaChi NVARCHAR(255),
                          NgaySinh DATE,
                          NgayVaoLam DATE,
                          ChucVu NVARCHAR(100),
                          MaQuanLy NVARCHAR(50) COMMENT 'Mã nhân viên quản lý trực tiếp',
                          MaCH NVARCHAR(50) COMMENT 'Cửa hàng nơi nhân viên làm việc',
                          TrangThai INT DEFAULT 1 COMMENT '0=Nghỉ việc, 1=Đang làm việc',
                          IsDeleted BIT DEFAULT 0,

                          CHECK (NgaySinh < NgayVaoLam),
                          INDEX idx_nhanvien_cuahang (MaCH),
                          INDEX idx_nhanvien_trangthai (TrangThai)
);

-- Bảng quản lý thông tin khách hàng và điểm tích lũy
CREATE TABLE KhachHang (
                           MaKH NVARCHAR(50) PRIMARY KEY,
                           MaNguoiDung NVARCHAR(50),
                           HoTen NVARCHAR(255) NOT NULL,
                           SDT NVARCHAR(15),
                           Email NVARCHAR(100),
                           DiaChi NVARCHAR(255),
                           NgaySinh DATE,
                           DiemTichLuy INT DEFAULT 0 CHECK (DiemTichLuy >= 0) COMMENT 'Điểm tích lũy từ các giao dịch mua hàng',
                           LoaiKhachHang NVARCHAR(50) DEFAULT 'Thường' COMMENT 'Thường, VIP, Bạc, Vàng, Kim cương',
                           NgayDangKy DATETIME DEFAULT CURRENT_TIMESTAMP,
                           IsDeleted BIT DEFAULT 0,

                           INDEX idx_khachhang_sdt (SDT),
                           INDEX idx_khachhang_email (Email),
                           INDEX idx_khachhang_loai (LoaiKhachHang)
);

-- Bảng phân loại các sản phẩm trong hệ thống
CREATE TABLE LoaiSanPham (
                             MaLoaiSP NVARCHAR(50) PRIMARY KEY,
                             TenLoai NVARCHAR(255) NOT NULL,
                             MoTa LONGTEXT,
                             MaLoaiCha NVARCHAR(50) COMMENT 'Để tạo cây phân loại nhiều cấp',
                             ThuTuHienThi INT DEFAULT 0,
                             IsDeleted BIT DEFAULT 0,

                             INDEX idx_loaisanpham_cha (MaLoaiCha)
);

-- Bảng quản lý thông tin chi tiết sản phẩm
CREATE TABLE SanPham (
                         MaSP NVARCHAR(50) PRIMARY KEY,
                         MaLoaiSP NVARCHAR(50) NOT NULL,
                         TenSP NVARCHAR(255) NOT NULL,
                         MoTa LONGTEXT,
                         GiaBan DECIMAL(15,2) NOT NULL CHECK (GiaBan > 0) COMMENT 'Giá bán hiện tại của sản phẩm',
                         DonViTinh NVARCHAR(50) DEFAULT 'Cái',
                         TrongLuong DECIMAL(10,3) COMMENT 'Trọng lượng sản phẩm (kg)',
                         KichThuoc NVARCHAR(100) COMMENT 'Kích thước sản phẩm',
                         HanSuDung INT COMMENT 'Số ngày hạn sử dụng',
                         TrangThai INT DEFAULT 1 COMMENT '0=Ngừng kinh doanh, 1=Đang kinh doanh',
                         NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
                         IsDeleted BIT DEFAULT 0,

                         INDEX idx_sanpham_loai (MaLoaiSP),
                         INDEX idx_sanpham_gia (GiaBan),
                         INDEX idx_sanpham_trangthai (TrangThai)
);

-- Bảng quản lý các chương trình khuyến mãi
CREATE TABLE KhuyenMai (
                           MaKM NVARCHAR(50) PRIMARY KEY,
                           TenChuongTrinh NVARCHAR(255) NOT NULL,
                           MoTa LONGTEXT,
                           LoaiKM NVARCHAR(50) NOT NULL COMMENT 'PhầnTrăm, SốTiền, Điểm, MuaXTangY',
                           GiaTriKM DECIMAL(15,2) NOT NULL COMMENT 'Giá trị khuyến mãi (% hoặc số tiền)',
                           DieuKienApDung LONGTEXT COMMENT 'Điều kiện để áp dụng khuyến mãi',
                           NgayBatDau DATETIME NOT NULL,
                           NgayKetThuc DATETIME NOT NULL,
                           SoLuongToiDa INT COMMENT 'Số lượng tối đa có thể áp dụng',
                           DaSuDung INT DEFAULT 0 COMMENT 'Số lượng đã sử dụng',
                           MaQuanLy NVARCHAR(50) COMMENT 'Nhân viên quản lý phụ trách khuyến mãi',
                           TrangThai INT DEFAULT 1 COMMENT '0=Tạm dừng, 1=Đang áp dụng',
                           IsDeleted BIT DEFAULT 0,

                           CHECK (NgayBatDau < NgayKetThuc),
                           CHECK (SoLuongToiDa IS NULL OR SoLuongToiDa > 0),
                           CHECK (DaSuDung >= 0),
                           INDEX idx_khuyenmai_ngay (NgayBatDau, NgayKetThuc),
                           INDEX idx_khuyenmai_trangthai (TrangThai)
);

-- Bảng quản lý các phương thức thanh toán được chấp nhận
CREATE TABLE PhuongThucThanhToan (
                                     MaPTTT NVARCHAR(50) PRIMARY KEY,
                                     TenPTTT NVARCHAR(100) NOT NULL,
                                     MoTa LONGTEXT,
                                     PhiGiaoDich DECIMAL(10,4) DEFAULT 0 COMMENT 'Phí giao dịch (%)',
                                     TrangThai INT DEFAULT 1 COMMENT '0=Ngừng sử dụng, 1=Đang sử dụng',
                                     IsDeleted BIT DEFAULT 0,

                                     INDEX idx_phuongthucthanhtoan_trangthai (TrangThai)
);

-- Bảng quản lý thông tin các kho hàng
CREATE TABLE Kho (
                     MaKho INT AUTO_INCREMENT PRIMARY KEY,
                     TenKho NVARCHAR(255) NOT NULL,
                     DiaChi NVARCHAR(255),
                     DienTich DECIMAL(10,2) COMMENT 'Diện tích kho (m²)',
                     SucChua DECIMAL(15,2) COMMENT 'Sức chứa tối đa',
                     MaCH NVARCHAR(50) COMMENT 'Cửa hàng quản lý kho',
                     TrangThai INT DEFAULT 1 COMMENT '0=Đóng cửa, 1=Hoạt động',
                     IsDeleted BIT DEFAULT 0,

                     INDEX idx_kho_cuahang (MaCH),
                     INDEX idx_kho_trangthai (TrangThai)
);

-- Bảng định nghĩa các ca làm việc trong ngày
CREATE TABLE CaLamViec (
                           MaCa INT AUTO_INCREMENT PRIMARY KEY,
                           TenCa NVARCHAR(100) NOT NULL,
                           GioBatDau TIME NOT NULL COMMENT 'Thời gian bắt đầu ca làm',
                           GioKetThuc TIME NOT NULL COMMENT 'Thời gian kết thúc ca làm',
                           SoGioLam DECIMAL(4,2) GENERATED ALWAYS AS (
                               CASE
                                   WHEN GioKetThuc >= GioBatDau THEN
                                       TIME_TO_SEC(TIMEDIFF(GioKetThuc, GioBatDau)) / 3600
                                   ELSE
                                       (TIME_TO_SEC(TIMEDIFF('24:00:00', GioBatDau)) + TIME_TO_SEC(GioKetThuc)) / 3600
                                   END
                               ) STORED COMMENT 'Số giờ làm việc trong ca',
                           TrangThai INT DEFAULT 1 COMMENT '0=Ngừng sử dụng, 1=Đang sử dụng',
                           IsDeleted BIT DEFAULT 0,

                           CHECK (GioBatDau != GioKetThuc),
    INDEX idx_calamviec_trangthai (TrangThai)
);

-- Bảng quản lý lịch làm việc của nhân viên
CREATE TABLE LichLamViec (
                             MaLich INT AUTO_INCREMENT PRIMARY KEY,
                             MaNV NVARCHAR(50) NOT NULL,
                             MaCa INT NOT NULL,
                             NgayLam DATE NOT NULL,
                             MaNVQuanLy NVARCHAR(50) COMMENT 'Người quản lý phê duyệt lịch',
                             TrangThai INT DEFAULT 0 COMMENT '0=Chờ duyệt, 1=Đã duyệt, 2=Từ chối, 3=Hủy, 4=Đã hoàn thành',
                             NgayDuyet DATETIME,
                             GhiChu LONGTEXT,
                             GioVao TIME COMMENT 'Giờ thực tế vào làm',
                             GioRa TIME COMMENT 'Giờ thực tế ra về',
                             IsDeleted BIT DEFAULT 0,

                             UNIQUE KEY unique_nhanvien_ngay_ca (MaNV, NgayLam, MaCa),
                             INDEX idx_lichlamviec_ngay (NgayLam),
                             INDEX idx_lichlamviec_trangthai (TrangThai)
);

-- Bảng quản lý lương nhân viên theo tháng
CREATE TABLE BangLuong (
                           MaLuong INT AUTO_INCREMENT PRIMARY KEY,
                           MaNV NVARCHAR(50) NOT NULL,
                           ThangLuong INT NOT NULL COMMENT 'Tháng áp dụng (1-12)',
                           NamLuong INT NOT NULL COMMENT 'Năm áp dụng (VD: 2024)',
                           LuongCoBan DECIMAL(15,2) NOT NULL CHECK (LuongCoBan >= 0),
                           PhuCap DECIMAL(15,2) DEFAULT 0 CHECK (PhuCap >= 0) COMMENT 'Tổng phụ cấp (nếu có)',
                           Thuong DECIMAL(15,2) DEFAULT 0 CHECK (Thuong >= 0) COMMENT 'Tổng tiền thưởng (nếu có)',
                           KhauTru DECIMAL(15,2) DEFAULT 0 CHECK (KhauTru >= 0) COMMENT 'Tổng số tiền bị khấu trừ (nếu có)',
                           TongLuong DECIMAL(15,2) GENERATED ALWAYS AS (LuongCoBan + PhuCap + Thuong - KhauTru) STORED,
                           SoNgayLam INT DEFAULT 0 COMMENT 'Số ngày thực tế làm việc',
                           SoGioLam DECIMAL(8,2) DEFAULT 0 COMMENT 'Tổng số giờ làm việc',
                           GhiChu LONGTEXT,
                           TrangThai INT DEFAULT 0 COMMENT '0=Chưa thanh toán, 1=Đã thanh toán',
                           NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
                           NgayThanhToan DATETIME NULL COMMENT 'Ngày thực tế thanh toán lương',
                           NguoiThanhToan NVARCHAR(50) NULL COMMENT 'Nhân viên thực hiện thanh toán',
                           IsDeleted BIT DEFAULT 0,

                           CHECK (ThangLuong >= 1 AND ThangLuong <= 12),
                           CHECK (NamLuong >= 2020),
                           CHECK (SoNgayLam >= 0),
                           CHECK (SoGioLam >= 0),
                           UNIQUE KEY unique_nhanvien_thang_nam (MaNV, ThangLuong, NamLuong),
                           INDEX idx_bangluong_thangnam (ThangLuong, NamLuong),
                           INDEX idx_bangluong_trangthai (TrangThai)
);

-- Bảng lưu trữ hình ảnh sản phẩm
CREATE TABLE HinhAnh (
                         MaHinh INT AUTO_INCREMENT PRIMARY KEY,
                         MaSP NVARCHAR(50) NOT NULL,
                         URL NVARCHAR(500) NOT NULL,
                         MoTa NVARCHAR(255),
                         LaChinh BOOLEAN DEFAULT FALSE COMMENT 'Đánh dấu ảnh chính của sản phẩm',
                         ThuTuHienThi INT DEFAULT 0 COMMENT 'Thứ tự hiển thị ảnh',
                         NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
                         IsDeleted BIT DEFAULT 0,

                         INDEX idx_hinhanh_sanpham (MaSP),
                         INDEX idx_hinhanh_chinh (LaChinh)
);

-- Bảng quản lý lịch sử giá sản phẩm
CREATE TABLE GiaSanPham (
                            MaGia INT AUTO_INCREMENT PRIMARY KEY,
                            MaSP NVARCHAR(50) NOT NULL,
                            Gia DECIMAL(15,2) NOT NULL CHECK (Gia > 0),
                            NgayBatDau DATE NOT NULL COMMENT 'Ngày bắt đầu áp dụng giá mới',
                            NgayKetThuc DATE COMMENT 'Ngày kết thúc áp dụng giá',
                            LyDoThayDoi NVARCHAR(255),
                            NguoiThayDoi NVARCHAR(50),
                            IsDeleted BIT DEFAULT 0,

                            CHECK (NgayKetThuc IS NULL OR NgayBatDau <= NgayKetThuc),
                            INDEX idx_giasanpham_sanpham (MaSP),
                            INDEX idx_giasanpham_ngay (NgayBatDau, NgayKetThuc)
);

-- Bảng theo dõi số lượng tồn kho của sản phẩm
CREATE TABLE TonKhoChiTiet (
                               MaTKCT INT AUTO_INCREMENT PRIMARY KEY,
                               MaSP NVARCHAR(50) NOT NULL,
                               MaKho INT NOT NULL,
                               SoLuongTon INT DEFAULT 0 CHECK (SoLuongTon >= 0) COMMENT 'Số lượng sản phẩm hiện có trong kho',
                               SoLuongToiThieu INT DEFAULT 0 COMMENT 'Mức tồn kho tối thiểu',
                               SoLuongToiDa INT COMMENT 'Mức tồn kho tối đa',
                               NgayCapNhat DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               IsDeleted BIT DEFAULT 0,

                               UNIQUE KEY unique_sanpham_kho (MaSP, MaKho),
                               INDEX idx_tonkho_sanpham (MaSP),
                               INDEX idx_tonkho_kho (MaKho)
);

-- Bảng quản lý phiếu nhập hàng từ nhà cung cấp
CREATE TABLE PhieuNhapHang (
                               MaPN INT AUTO_INCREMENT PRIMARY KEY,
                               MaNCC NVARCHAR(50) NOT NULL,
                               MaKho INT NOT NULL,
                               MaNVLap NVARCHAR(50) NOT NULL COMMENT 'Nhân viên lập phiếu nhập',
                               NgayNhap DATETIME NOT NULL,
                               TongTienNhap DECIMAL(15,2) DEFAULT 0,
                               TrangThai INT DEFAULT 0 COMMENT '0=Chờ xử lý, 1=Đã nhập kho, 2=Từ chối, 3=Hủy',
                               GhiChu LONGTEXT,
                               NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
                               IsDeleted BIT DEFAULT 0,

                               INDEX idx_phieunhap_ngay (NgayNhap),
                               INDEX idx_phieunhap_trangthai (TrangThai),
                               INDEX idx_phieunhap_nhacungcap (MaNCC)
);

-- Bảng chi tiết các sản phẩm trong phiếu nhập
CREATE TABLE ChiTietPhieuNhap (
                                  MaCTPN INT AUTO_INCREMENT PRIMARY KEY,
                                  MaPN INT NOT NULL,
                                  MaSP NVARCHAR(50) NOT NULL,
                                  SoLuongNhap INT NOT NULL CHECK (SoLuongNhap > 0),
                                  DonGiaNhap DECIMAL(15,2) NOT NULL CHECK (DonGiaNhap > 0),
                                  ThanhTien DECIMAL(15,2) GENERATED ALWAYS AS (SoLuongNhap * DonGiaNhap) STORED,
                                  NgayHetHan DATE COMMENT 'Hạn sử dụng của sản phẩm',
                                  SoLo NVARCHAR(50) COMMENT 'Số lô sản xuất',
                                  NgaySanXuat DATE,
                                  IsDeleted BIT DEFAULT 0,

                                  INDEX idx_chitietphieunhap_phieu (MaPN),
                                  INDEX idx_chitietphieunhap_sanpham (MaSP)
);

-- Bảng quản lý phiếu xuất kho
CREATE TABLE PhieuXuatKho (
                              MaPXK INT AUTO_INCREMENT PRIMARY KEY,
                              MaKho INT NOT NULL,
                              MaNVLap NVARCHAR(50) NOT NULL COMMENT 'Nhân viên lập phiếu xuất',
                              NgayXuat DATETIME NOT NULL,
                              TongSoLuong INT DEFAULT 0,
                              TongGiaTri DECIMAL(15,2) DEFAULT 0,
                              LyDoXuat NVARCHAR(255),
                              TrangThai INT DEFAULT 0 COMMENT '0=Chờ xử lý, 1=Đã xuất, 2=Từ chối, 3=Hủy',
                              GhiChu LONGTEXT,
                              NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
                              IsDeleted BIT DEFAULT 0,

                              INDEX idx_phieuxuat_ngay (NgayXuat),
                              INDEX idx_phieuxuat_trangthai (TrangThai),
                              INDEX idx_phieuxuat_kho (MaKho)
);

-- Bảng chi tiết các sản phẩm trong phiếu xuất
CREATE TABLE ChiTietPhieuXuat (
                                  MaCTPXK INT AUTO_INCREMENT PRIMARY KEY,
                                  MaPXK INT NOT NULL,
                                  MaSP NVARCHAR(50) NOT NULL,
                                  SoLuongXuat INT NOT NULL CHECK (SoLuongXuat > 0),
                                  DonGiaXuat DECIMAL(15,2) NOT NULL CHECK (DonGiaXuat > 0),
                                  ThanhTien DECIMAL(15,2) GENERATED ALWAYS AS (SoLuongXuat * DonGiaXuat) STORED,
                                  IsDeleted BIT DEFAULT 0,

                                  INDEX idx_chitietphieuxuat_phieu (MaPXK),
                                  INDEX idx_chitietphieuxuat_sanpham (MaSP)
);

-- Bảng quản lý hóa đơn bán hàng
CREATE TABLE HoaDon (
                        MaHD INT AUTO_INCREMENT PRIMARY KEY,
                        MaKH NVARCHAR(50),
                        MaNVLap NVARCHAR(50) NOT NULL COMMENT 'Nhân viên lập hóa đơn',
                        MaKM NVARCHAR(50) COMMENT 'Mã khuyến mãi áp dụng',
                        NgayLap DATETIME NOT NULL,
                        TongTienHang DECIMAL(15,2) DEFAULT 0,
                        TienGiamGia DECIMAL(15,2) DEFAULT 0,
                        TongTien DECIMAL(15,2) GENERATED ALWAYS AS (TongTienHang - TienGiamGia) STORED,
                        MaPTTT NVARCHAR(50),
                        TrangThai INT DEFAULT 0 COMMENT '0=Chờ xử lý, 1=Đã thanh toán, 2=Đang xử lý, 3=Hủy, 4=Hoàn trả',
                        DiemTichLuy INT DEFAULT 0 COMMENT 'Điểm tích lũy từ hóa đơn này',
                        GhiChu LONGTEXT,
                        NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
                        NguoiTao NVARCHAR(50),
                        NgaySua DATETIME ON UPDATE CURRENT_TIMESTAMP,
                        NguoiSua NVARCHAR(50),
                        IsDeleted BIT DEFAULT 0,

                        INDEX idx_hoadon_ngaylap (NgayLap),
                        INDEX idx_hoadon_trangthai (TrangThai),
                        INDEX idx_hoadon_khachhang (MaKH),
                        INDEX idx_hoadon_nhanvien (MaNVLap)
);

-- Bảng chi tiết các sản phẩm trong hóa đơn
CREATE TABLE ChiTietHoaDon (
                               MaCTHD INT AUTO_INCREMENT PRIMARY KEY,
                               MaHD INT NOT NULL,
                               MaSP NVARCHAR(50) NOT NULL,
                               SoLuong INT NOT NULL CHECK (SoLuong > 0),
                               DonGiaBan DECIMAL(15,2) NOT NULL,
                               ThanhTien DECIMAL(15,2) GENERATED ALWAYS AS (SoLuong * DonGiaBan) STORED,
                               GiamGia DECIMAL(15,2) DEFAULT 0,
                               ThanhTienSauGiam DECIMAL(15,2) GENERATED ALWAYS AS (ThanhTien - GiamGia) STORED,
                               IsDeleted BIT DEFAULT 0,

                               INDEX idx_chitiethoadon_hoadon (MaHD),
                               INDEX idx_chitiethoadon_sanpham (MaSP)
);

-- Bảng áp dụng khuyến mãi cho sản phẩm
CREATE TABLE KhuyenMaiSanPham (
                                  MaKMSP INT AUTO_INCREMENT PRIMARY KEY,
                                  MaKM NVARCHAR(50) NOT NULL,
                                  MaSP NVARCHAR(50) NOT NULL,
                                  NgayBatDau DATETIME,
                                  NgayKetThuc DATETIME,
                                  IsDeleted BIT DEFAULT 0,

                                  UNIQUE KEY unique_khuyenmai_sanpham (MaKM, MaSP),
                                  INDEX idx_khuyenmaisanpham_km (MaKM),
                                  INDEX idx_khuyenmaisanpham_sp (MaSP)
);

-- Bảng áp dụng khuyến mãi cho khách hàng
CREATE TABLE KhuyenMaiKhachHang (
                                    MaKMKH INT AUTO_INCREMENT PRIMARY KEY,
                                    MaKM NVARCHAR(50) NOT NULL,
                                    MaKH NVARCHAR(50) NOT NULL,
                                    NgayApDung DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    DaSuDung BOOLEAN DEFAULT FALSE,
                                    IsDeleted BIT DEFAULT 0,

                                    UNIQUE KEY unique_khuyenmai_khachhang (MaKM, MaKH),
                                    INDEX idx_khuyenmaikhachhang_km (MaKM),
                                    INDEX idx_khuyenmaikhachhang_kh (MaKH)
);

-- Bảng quản lý các giao dịch thanh toán
CREATE TABLE ThanhToan (
                           MaTT INT AUTO_INCREMENT PRIMARY KEY,
                           MaHD INT NOT NULL,
                           MaPTTT NVARCHAR(50) NOT NULL,
                           SoTienThanhToan DECIMAL(15,2) NOT NULL,
                           NgayGioTT DATETIME NOT NULL,
                           TrangThaiTT INT DEFAULT 0 COMMENT '0=Chờ xử lý, 1=Thành công, 2=Thất bại, 3=Hủy, 4=Hoàn tiền',
                           MaGiaoDichNganHang NVARCHAR(100) COMMENT 'Mã giao dịch từ ngân hàng',
                           GhiChu LONGTEXT,
                           IsDeleted BIT DEFAULT 0,

                           INDEX idx_thanhtoan_hoadon (MaHD),
                           INDEX idx_thanhtoan_trangthai (TrangThaiTT),
                           INDEX idx_thanhtoan_ngay (NgayGioTT)
);

-- Bảng quản lý giỏ hàng của khách
CREATE TABLE GioHang (
                         MaGH INT AUTO_INCREMENT PRIMARY KEY,
                         MaKH NVARCHAR(50),
                         MaNV NVARCHAR(50) COMMENT 'Nhân viên hỗ trợ (nếu có)',
                         NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
                         NgayCapNhat DATETIME ON UPDATE CURRENT_TIMESTAMP,
                         TrangThai INT DEFAULT 0 COMMENT '0=Đang chọn hàng, 1=Đã đặt hàng, 2=Đã thanh toán, 3=Hủy',
                         GhiChu LONGTEXT,
                         IsDeleted BIT DEFAULT 0,

                         INDEX idx_giohang_khachhang (MaKH),
                         INDEX idx_giohang_trangthai (TrangThai)
);

-- Bảng chi tiết sản phẩm trong giỏ hàng
CREATE TABLE ChiTietGioHang (
                                MaCTGH INT AUTO_INCREMENT PRIMARY KEY,
                                MaGH INT NOT NULL,
                                MaSP NVARCHAR(50) NOT NULL,
                                SoLuong INT NOT NULL CHECK (SoLuong > 0),
                                DonGiaHienTai DECIMAL(15,2) NOT NULL CHECK (DonGiaHienTai > 0) COMMENT 'Giá sản phẩm tại thời điểm thêm vào giỏ',
                                ThanhTien DECIMAL(15,2) GENERATED ALWAYS AS (SoLuong * DonGiaHienTai) STORED,
                                NgayThem DATETIME DEFAULT CURRENT_TIMESTAMP,
                                IsDeleted BIT DEFAULT 0,

                                UNIQUE KEY unique_giohang_sanpham (MaGH, MaSP),
                                INDEX idx_chitietgiohang_giohang (MaGH),
                                INDEX idx_chitietgiohang_sanpham (MaSP)
);

-- Bảng thống kê báo cáo
CREATE TABLE ThongKeBaoCao (
                               MaBaoCao INT AUTO_INCREMENT PRIMARY KEY,
                               MaCH NVARCHAR(50),
                               MaNV NVARCHAR(50) NOT NULL COMMENT 'Nhân viên lập báo cáo',
                               LoaiBaoCao NVARCHAR(100) NOT NULL COMMENT 'Loại báo cáo: DoanhThu, ChiPhi, TonKho, NhanVien, KhachHang',
                               TenBaoCao NVARCHAR(255) NOT NULL,
                               ThoiGianTu DATETIME,
                               ThoiGianDen DATETIME,
                               SoTien DECIMAL(15,2),
                               SoLuong INT,
                               NgayBaoCao DATETIME DEFAULT CURRENT_TIMESTAMP,
                               NoiDung LONGTEXT,
                               FileDinhKem NVARCHAR(500),
                               TrangThai INT DEFAULT 1 COMMENT '0=Nháp, 1=Hoàn thành',
                               IsDeleted BIT DEFAULT 0,

                               INDEX idx_thongke_loai (LoaiBaoCao),
                               INDEX idx_thongke_ngay (NgayBaoCao),
                               INDEX idx_thongke_cuahang (MaCH)
);

-- ===================================
-- THÊM CÁC KHÓA NGOẠI (FOREIGN KEYS)
-- ===================================

-- Foreign key constraints will be added in init_fixes.sql after data clearing


-- Add foreign key constraints
-- Khóa ngoại cho bảng LoaiSanPham (tự tham chiếu)
ALTER TABLE LoaiSanPham ADD CONSTRAINT FK_LoaiSanPham_LoaiCha
    FOREIGN KEY (MaLoaiCha) REFERENCES LoaiSanPham(MaLoaiSP)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng NhanVien
ALTER TABLE NhanVien ADD CONSTRAINT FK_NhanVien_NguoiDung
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung)
        ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE NhanVien ADD CONSTRAINT FK_NhanVien_QuanLy
    FOREIGN KEY (MaQuanLy) REFERENCES NhanVien(MaNV)
        ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE NhanVien ADD CONSTRAINT FK_NhanVien_CuaHang
    FOREIGN KEY (MaCH) REFERENCES CuaHang(MaCH)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng KhachHang
ALTER TABLE KhachHang ADD CONSTRAINT FK_KhachHang_NguoiDung
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng SanPham
ALTER TABLE SanPham ADD CONSTRAINT FK_SanPham_LoaiSanPham
    FOREIGN KEY (MaLoaiSP) REFERENCES LoaiSanPham(MaLoaiSP)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Khóa ngoại cho bảng KhuyenMai
ALTER TABLE KhuyenMai ADD CONSTRAINT FK_KhuyenMai_QuanLy
    FOREIGN KEY (MaQuanLy) REFERENCES NhanVien(MaNV)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng Kho
ALTER TABLE Kho ADD CONSTRAINT FK_Kho_CuaHang
    FOREIGN KEY (MaCH) REFERENCES CuaHang(MaCH)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng LichLamViec
ALTER TABLE LichLamViec ADD CONSTRAINT FK_LichLamViec_NhanVien
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE LichLamViec ADD CONSTRAINT FK_LichLamViec_Ca
    FOREIGN KEY (MaCa) REFERENCES CaLamViec(MaCa)
        ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE LichLamViec ADD CONSTRAINT FK_LichLamViec_QuanLy
    FOREIGN KEY (MaNVQuanLy) REFERENCES NhanVien(MaNV)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng BangLuong
ALTER TABLE BangLuong ADD CONSTRAINT FK_BangLuong_NhanVien
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE BangLuong ADD CONSTRAINT FK_BangLuong_NguoiThanhToan
    FOREIGN KEY (NguoiThanhToan) REFERENCES NhanVien(MaNV)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng HinhAnh
ALTER TABLE HinhAnh ADD CONSTRAINT FK_HinhAnh_SanPham
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
        ON DELETE CASCADE ON UPDATE CASCADE;

-- Khóa ngoại cho bảng GiaSanPham
ALTER TABLE GiaSanPham ADD CONSTRAINT FK_GiaSanPham_SanPham
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE GiaSanPham ADD CONSTRAINT FK_GiaSanPham_NguoiThayDoi
    FOREIGN KEY (NguoiThayDoi) REFERENCES NhanVien(MaNV)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng TonKhoChiTiet
ALTER TABLE TonKhoChiTiet ADD CONSTRAINT FK_TonKhoChiTiet_SanPham
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE TonKhoChiTiet ADD CONSTRAINT FK_TonKhoChiTiet_Kho
    FOREIGN KEY (MaKho) REFERENCES Kho(MaKho)
        ON DELETE CASCADE ON UPDATE CASCADE;

-- Khóa ngoại cho bảng PhieuNhapHang
ALTER TABLE PhieuNhapHang ADD CONSTRAINT FK_PhieuNhapHang_NhaCungCap
    FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC)
        ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE PhieuNhapHang ADD CONSTRAINT FK_PhieuNhapHang_Kho
    FOREIGN KEY (MaKho) REFERENCES Kho(MaKho)
        ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE PhieuNhapHang ADD CONSTRAINT FK_PhieuNhapHang_NhanVien
    FOREIGN KEY (MaNVLap) REFERENCES NhanVien(MaNV)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Khóa ngoại cho bảng ChiTietPhieuNhap
ALTER TABLE ChiTietPhieuNhap ADD CONSTRAINT FK_ChiTietPhieuNhap_PhieuNhap
    FOREIGN KEY (MaPN) REFERENCES PhieuNhapHang(MaPN)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE ChiTietPhieuNhap ADD CONSTRAINT FK_ChiTietPhieuNhap_SanPham
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Khóa ngoại cho bảng PhieuXuatKho
ALTER TABLE PhieuXuatKho ADD CONSTRAINT FK_PhieuXuatKho_Kho
    FOREIGN KEY (MaKho) REFERENCES Kho(MaKho)
        ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE PhieuXuatKho ADD CONSTRAINT FK_PhieuXuatKho_NhanVien
    FOREIGN KEY (MaNVLap) REFERENCES NhanVien(MaNV)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Khóa ngoại cho bảng ChiTietPhieuXuat
ALTER TABLE ChiTietPhieuXuat ADD CONSTRAINT FK_ChiTietPhieuXuat_PhieuXuat
    FOREIGN KEY (MaPXK) REFERENCES PhieuXuatKho(MaPXK)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE ChiTietPhieuXuat ADD CONSTRAINT FK_ChiTietPhieuXuat_SanPham
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Khóa ngoại cho bảng HoaDon
ALTER TABLE HoaDon ADD CONSTRAINT FK_HoaDon_KhachHang
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH)
        ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE HoaDon ADD CONSTRAINT FK_HoaDon_NhanVien
    FOREIGN KEY (MaNVLap) REFERENCES NhanVien(MaNV)
        ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE HoaDon ADD CONSTRAINT FK_HoaDon_KhuyenMai
    FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM)
        ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE HoaDon ADD CONSTRAINT FK_HoaDon_PhuongThucThanhToan
    FOREIGN KEY (MaPTTT) REFERENCES PhuongThucThanhToan(MaPTTT)
        ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE HoaDon ADD CONSTRAINT FK_HoaDon_NguoiTao
    FOREIGN KEY (NguoiTao) REFERENCES NhanVien(MaNV)
        ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE HoaDon ADD CONSTRAINT FK_HoaDon_NguoiSua
    FOREIGN KEY (NguoiSua) REFERENCES NhanVien(MaNV)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng ChiTietHoaDon
ALTER TABLE ChiTietHoaDon ADD CONSTRAINT FK_ChiTietHoaDon_HoaDon
    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE ChiTietHoaDon ADD CONSTRAINT FK_ChiTietHoaDon_SanPham
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Khóa ngoại cho bảng KhuyenMaiSanPham
ALTER TABLE KhuyenMaiSanPham ADD CONSTRAINT FK_KhuyenMaiSanPham_KhuyenMai
    FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE KhuyenMaiSanPham ADD CONSTRAINT FK_KhuyenMaiSanPham_SanPham
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
        ON DELETE CASCADE ON UPDATE CASCADE;

-- Khóa ngoại cho bảng KhuyenMaiKhachHang
ALTER TABLE KhuyenMaiKhachHang ADD CONSTRAINT FK_KhuyenMaiKhachHang_KhuyenMai
    FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE KhuyenMaiKhachHang ADD CONSTRAINT FK_KhuyenMaiKhachHang_KhachHang
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH)
        ON DELETE CASCADE ON UPDATE CASCADE;

-- Khóa ngoại cho bảng ThanhToan
ALTER TABLE ThanhToan ADD CONSTRAINT FK_ThanhToan_HoaDon
    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE ThanhToan ADD CONSTRAINT FK_ThanhToan_PhuongThucThanhToan
    FOREIGN KEY (MaPTTT) REFERENCES PhuongThucThanhToan(MaPTTT)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- Khóa ngoại cho bảng GioHang
ALTER TABLE GioHang ADD CONSTRAINT FK_GioHang_KhachHang
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE GioHang ADD CONSTRAINT FK_GioHang_NhanVien
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
        ON DELETE SET NULL ON UPDATE CASCADE;

-- Khóa ngoại cho bảng ChiTietGioHang
ALTER TABLE ChiTietGioHang ADD CONSTRAINT FK_ChiTietGioHang_GioHang
    FOREIGN KEY (MaGH) REFERENCES GioHang(MaGH)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE ChiTietGioHang ADD CONSTRAINT FK_ChiTietGioHang_SanPham
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
        ON DELETE CASCADE ON UPDATE CASCADE;

-- Khóa ngoại cho bảng ThongKeBaoCao
ALTER TABLE ThongKeBaoCao ADD CONSTRAINT FK_ThongKeBaoCao_CuaHang
    FOREIGN KEY (MaCH) REFERENCES CuaHang(MaCH)
        ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE ThongKeBaoCao ADD CONSTRAINT FK_ThongKeBaoCao_NhanVien
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
        ON DELETE RESTRICT ON UPDATE CASCADE;

