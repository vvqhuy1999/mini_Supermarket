
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
                           Sub NVARCHAR(255),
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


-- ===================================
-- DỮ LIỆU MẪU CẬP NHẬT THEO INIT.SQL
-- ===================================

-- Thêm dữ liệu mẫu cho bảng chính
-- VaiTro: 0=Admin, 1=QuanLy, 2=NhanVien, 3=KhachHang
INSERT INTO NguoiDung (MaNguoiDung, Email, MatKhau, Sub, VaiTro) VALUES
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

INSERT INTO CuaHang (MaCH, TenCH, DiaChi, SDT, NgayThanhLap, TrangThai) VALUES
('CH001', N'Cửa Hàng EasyMart1', N'123 Lê Lợi, Q1', '0909123456', '2020-01-01', 1),
('CH002', N'Cửa Hàng EasyMart2', N'456 Nguyễn Đình Chiểu, Q3', '0911222333', '2020-02-01', 1);

INSERT INTO NhaCungCap (MaNCC, TenNCC, DiaChi, SDT, Email, ThongTinHopDong, NgayHopTac, TrangThai) VALUES
('NCC001', N'Rau Xanh Sạch Đà Lạt', N'Đà Lạt - Lâm Đồng', '0901000001', 'rauxanh@gmail.com', N'Hợp đồng cung cấp rau sạch', '2020-01-01', 1),
('NCC002', N'Thủy Sản Đông Lạnh Biển Xanh', N'Quận 7 - TP.HCM', '0901000002', 'bienxanh@gmail.com', N'Hợp đồng cung cấp thủy sản', '2020-02-01', 1),
('NCC003', N'Công Ty Đồ Hộp Việt', N'Bình Dương', '0901000003', 'dohop@gmail.com', N'Hợp đồng cung cấp đồ hộp', '2020-03-01', 1),
('NCC004', N'Nước Giải Khát Việt Nam', N'TP.HCM', '0901000004', 'nuocgiaikhat@gmail.com', N'Hợp đồng cung cấp nước giải khát', '2020-04-01', 1),
('NCC005', N'Công Ty Sữa & Bé Khỏe', N'Hà Nội', '0901000005', 'suabekhoe@gmail.com', N'Hợp đồng cung cấp sữa', '2020-05-01', 1),
('NCC006', N'Gia Vị Nam Ngư', N'TP.HCM', '0901000006', 'giavi@gmail.com', N'Hợp đồng cung cấp gia vị', '2020-06-01', 1),
('NCC007', N'Hóa Phẩm & Đồ Gia Dụng Unihome', N'Đồng Nai', '0901000007', 'unihome@gmail.com', N'Hợp đồng cung cấp hóa phẩm', '2020-07-01', 1);

INSERT INTO NhanVien (MaNV, MaNguoiDung, HoTen, SDT, DiaChi, NgaySinh, NgayVaoLam, ChucVu, MaQuanLy, MaCH, TrangThai) VALUES
('NV001', 'ND001', N'Nguyễn Văn A', '0909111222', N'123 Lê Lợi', '1990-01-01', '2020-01-01', N'Giám đốc', NULL, 'CH001', 1),
('NV002', 'ND002', N'Trần Thị B', '0909333444', N'456 Nguyễn Đình Chiểu', '1992-02-02', '2020-02-01', N'Quản lý', 'NV001', 'CH001', 1),
('NV003', 'ND003', N'Lê Văn C', '0911223344', N'789 Phan Văn Trị', '1993-03-03', '2020-03-01', N'Nhân viên bán hàng', 'NV002', 'CH001', 1),
('NV004', 'ND004', N'Phạm Thị D', '0922334455', N'111 Võ Văn Ngân', '1994-04-04', '2020-04-01', N'Nhân viên kho', 'NV002', 'CH001', 1),
('NV005', 'ND005', N'Hoàng Văn E', '0933445566', N'222 Lý Thường Kiệt', '1995-05-05', '2020-05-01', N'Nhân viên thu ngân', 'NV002', 'CH001', 1),
('NV006', 'ND006', N'Ngô Thị F', '0944556677', N'333 Cách Mạng Tháng 8', '1996-06-06', '2020-06-01', N'Nhân viên bán hàng', 'NV002', 'CH001', 1),
('NV007', 'ND007', N'Đỗ Văn G', '0955667788', N'444 Pasteur', '1997-07-07', '2020-07-01', N'Nhân viên kho', 'NV002', 'CH001', 1),
('NV008', 'ND008', N'Nguyễn Văn H', '0966778899', N'123 Lê Lợi', '1990-08-08', '2020-08-01', N'Giám đốc', NULL, 'CH002', 1),
('NV009', 'ND009', N'Trần Thị I', '0977889900', N'456 Nguyễn Đình Chiểu', '1992-09-09', '2020-09-01', N'Quản lý', 'NV008', 'CH002', 1),
('NV010', 'ND010', N'Lê Văn J', '0988990011', N'789 Phan Văn Trị', '1993-10-10', '2020-10-01', N'Nhân viên bán hàng', 'NV009', 'CH002', 1),
('NV011', 'ND011', N'Phạm Thị K', '0999001122', N'111 Võ Văn Ngân', '1994-11-11', '2020-11-01', N'Nhân viên kho', 'NV009', 'CH002', 1),
('NV012', 'ND012', N'Hoàng Văn L', '0900123456', N'222 Lý Thường Kiệt', '1995-12-12', '2020-12-01', N'Nhân viên thu ngân', 'NV009', 'CH002', 1),
('NV013', 'ND013', N'Ngô Thị M', '0911234567', N'333 Cách Mạng Tháng 8', '1996-01-13', '2021-01-01', N'Nhân viên bán hàng', 'NV009', 'CH002', 1),
('NV014', 'ND014', N'Đỗ Văn N', '0922345678', N'444 Pasteur', '1997-02-14', '2021-02-01', N'Nhân viên kho', 'NV009', 'CH002', 1);

INSERT INTO KhachHang (MaKH, MaNguoiDung, HoTen, SDT, Email, DiaChi, NgaySinh, DiemTichLuy, LoaiKhachHang, NgayDangKy) VALUES
('KH001', 'ND015', N'Nguyễn Văn KH1', '0988111222', 'khach1@gmail.com', N'123 Q1', '1985-01-01', 100, N'Thường', '2020-01-01'),
('KH002', 'ND016', N'Trần Thị KH2', '0977223344', 'khach2@gmail.com', N'456 Q3', '1986-02-02', 200, N'VIP', '2020-02-01'),
('KH003', 'ND017', N'Lê Văn KH3', '0966334455', 'khach3@gmail.com', N'789 Gò Vấp', '1987-03-03', 150, N'Thường', '2020-03-01'),
('KH004', 'ND018', N'Phạm Thị KH4', '0955445566', 'khach4@gmail.com', N'111 Thủ Đức', '1988-04-04', 50, N'Thường', '2020-04-01'),
('KH005', 'ND019', N'Đỗ Văn KH5', '0944556677', 'khach5@gmail.com', N'222 Tân Bình', '1989-05-05', 300, N'Vàng', '2020-05-01'),
('KH006', 'ND020', N'Võ Minh KH6', '0933667788', 'khach6@gmail.com', N'15 Bình Thạnh', '1990-06-06', 120, N'Thường', '2020-06-01'),
('KH007', 'ND021', N'Huỳnh Lan KH7', '0922778899', 'khach7@gmail.com', N'89 Quận 10', '1991-07-07', 180, N'Bạc', '2020-07-01'),
('KH008', 'ND022', N'Phan Văn KH8', '0911889900', 'khach8@gmail.com', N'12 Quận 7', '1992-08-08', 220, N'Vàng', '2020-08-01'),
('KH009', 'ND023', N'Trương Mỹ KH9', '0909000111', 'khach9@gmail.com', N'35 Quận 5', '1993-09-09', 80, N'Thường', '2020-09-01'),
('KH010', 'ND024', N'Lâm Quốc KH10', '0988776655', 'khach10@gmail.com', N'77 Quận 8', '1994-10-10', 260, N'Kim cương', '2020-10-01');

-- Thêm dữ liệu bảng lương
INSERT INTO BangLuong (MaNV, ThangLuong, NamLuong, LuongCoBan, PhuCap, Thuong, KhauTru, SoNgayLam, SoGioLam, GhiChu, TrangThai) VALUES
('NV001', 7, 2025, 15000000, 2000000, 1000000, 0, 22, 176, N'Lương tháng 7/2025', 1),
('NV002', 7, 2025, 12000000, 1500000, 800000, 0, 21, 168, N'Lương tháng 7/2025', 1),
('NV003', 7, 2025, 8000000, 500000, 300000, 0, 20, 160, N'Lương tháng 7/2025', 1),
('NV004', 7, 2025, 8500000, 600000, 400000, 0, 21, 168, N'Lương tháng 7/2025', 1),
('NV005', 7, 2025, 7500000, 400000, 200000, 0, 19, 152, N'Lương tháng 7/2025', 1),
('NV006', 7, 2025, 7000000, 300000, 150000, 0, 18, 144, N'Lương tháng 7/2025', 1),
('NV007', 7, 2025, 6500000, 250000, 100000, 0, 17, 136, N'Lương tháng 7/2025', 1),
('NV008', 7, 2025, 15000000, 2000000, 1000000, 0, 22, 176, N'Lương tháng 7/2025', 1),
('NV009', 7, 2025, 12000000, 1500000, 800000, 0, 21, 168, N'Lương tháng 7/2025', 1),
('NV010', 7, 2025, 8000000, 500000, 300000, 0, 20, 160, N'Lương tháng 7/2025', 1),
('NV011', 7, 2025, 8500000, 600000, 400000, 0, 21, 168, N'Lương tháng 7/2025', 1),
('NV012', 7, 2025, 7500000, 400000, 200000, 0, 19, 152, N'Lương tháng 7/2025', 1),
('NV013', 7, 2025, 7000000, 300000, 150000, 0, 18, 144, N'Lương tháng 7/2025', 1),
('NV014', 7, 2025, 6500000, 250000, 100000, 0, 17, 136, N'Lương tháng 7/2025', 1);

-- Tiếp tục với các bảng khác...
INSERT INTO LoaiSanPham (MaLoaiSP, TenLoai, MoTa, MaLoaiCha, ThuTuHienThi) VALUES
('LSP001', N'Tươi sống', N'Các loại rau củ quả tươi', NULL, 1),
('LSP002', N'Đông lạnh', N'Thực phẩm đông lạnh', NULL, 2),
('LSP003', N'Đồ đóng hộp', N'Thực phẩm đóng hộp', NULL, 3),
('LSP004', N'Đồ uống', N'Các loại nước giải khát', NULL, 4),
('LSP005', N'Sữa & em bé', N'Sản phẩm cho trẻ em', NULL, 5),
('LSP006', N'Gia vị & Dầu ăn', N'Gia vị và dầu ăn', NULL, 6),
('LSP007', N'Hóa phẩm & Tẩy rửa', N'Sản phẩm vệ sinh', NULL, 7);

-- ===================================
-- DỮ LIỆU SẢN PHẨM THEO TỪNG LOẠI
-- ===================================

-- LSP001 – TƯƠI SỐNG (20 sản phẩm)
INSERT INTO SanPham (MaSP, MaLoaiSP, TenSP, MoTa, GiaBan, DonViTinh, TrongLuong, KichThuoc, HanSuDung, TrangThai) VALUES
('SP001', 'LSP001', N'Dưa leo Đà Lạt', N'[Ngắn] Dưa tươi ngon sạch. [Dài] Dưa leo Đà Lạt được chọn lọc kỹ càng từ nông trại sạch, vỏ xanh mướt, giòn ngọt, thích hợp cho các món salad, dưa muối hoặc ăn sống trực tiếp.', 15000, N'Kg', 0.5, N'20x5cm', 7, 1),
('SP002', 'LSP001', N'Cà chua bi', N'[Ngắn] Cà chua bi đỏ mọng. [Dài] Cà chua bi được trồng theo phương pháp hữu cơ, vỏ mỏng, vị ngọt thanh, thích hợp cho ăn sống, làm salad hoặc xào nấu.', 18000, N'Kg', 0.3, N'2x2cm', 5, 1),
('SP003', 'LSP001', N'Cải thìa tươi', N'[Ngắn] Rau xanh giòn ngọt. [Dài] Cải thìa sạch được thu hoạch trong ngày, giàu vitamin A và C, thường dùng trong các món xào hoặc luộc.', 12000, N'Kg', 0.4, N'25x3cm', 3, 1),
('SP004', 'LSP001', N'Cải ngọt Đà Lạt', N'[Ngắn] Rau tươi sạch. [Dài] Cải ngọt được trồng trong điều kiện khí hậu mát mẻ Đà Lạt, ít sâu bệnh, thích hợp nấu canh, xào hoặc ăn lẩu.', 13000, N'Kg', 0.3, N'20x2cm', 3, 1),
('SP005', 'LSP001', N'Rau muống', N'[Ngắn] Rau muống giòn ngon. [Dài] Rau muống tươi được lựa chọn kỹ lưỡng, thân giòn, lá xanh, thích hợp cho các món luộc, xào tỏi hoặc làm gỏi.', 10000, N'Kg', 0.5, N'30x2cm', 2, 1),
('SP006', 'LSP001', N'Bắp cải trắng', N'[Ngắn] Bắp cải tươi giòn. [Dài] Bắp cải trắng giòn ngọt, có thể dùng để nấu canh, xào hoặc làm dưa muối.', 14000, N'Cái', 1.0, N'15x15cm', 7, 1),
('SP007', 'LSP001', N'Cà rốt Đà Lạt', N'[Ngắn] Cà rốt giòn ngọt. [Dài] Cà rốt trồng tại Đà Lạt, củ đều màu cam đẹp, giàu beta-carotene tốt cho mắt, thường dùng nấu canh, luộc, xào.', 16000, N'Kg', 0.6, N'20x3cm', 10, 1),
('SP008', 'LSP001', N'Khoai tây vàng', N'[Ngắn] Khoai tây sạch. [Dài] Khoai tây vàng vỏ mỏng, ít nhựa, thích hợp để chiên, nấu súp hoặc nghiền làm món ăn dặm.', 17000, N'Kg', 0.8, N'8x5cm', 14, 1),
('SP009', 'LSP001', N'Hành lá', N'[Ngắn] Hành tươi xanh. [Dài] Hành lá được thu hoạch từ vườn sạch, lá xanh, mùi thơm nhẹ, là nguyên liệu không thể thiếu cho các món canh và chiên.', 8000, N'Kg', 0.2, N'25x1cm', 5, 1),
('SP010', 'LSP001', N'Rau dền đỏ', N'[Ngắn] Rau dền mát gan. [Dài] Rau dền đỏ nhiều sắt, hỗ trợ tuần hoàn máu, thích hợp cho các món canh và luộc.', 9000, N'Kg', 0.3, N'20x2cm', 2, 1),
('SP011', 'LSP001', N'Mướp hương', N'[Ngắn] Mướp mềm thơm. [Dài] Mướp hương có vị ngọt thanh, mềm, thường được dùng trong các món canh hoặc xào chung với trứng.', 11000, N'Kg', 0.4, N'25x4cm', 3, 1),
('SP012', 'LSP001', N'Dưa gang', N'[Ngắn] Dưa giải nhiệt. [Dài] Dưa gang mọng nước, vị ngọt nhẹ, được ưa chuộng trong mùa nóng vì tác dụng giải nhiệt, ăn sống hoặc làm sinh tố.', 18000, N'Kg', 0.8, N'15x10cm', 5, 1),
('SP013', 'LSP001', N'Rau má', N'[Ngắn] Rau má mát gan. [Dài] Rau má có tác dụng thanh nhiệt, giải độc, thường dùng để ép nước hoặc làm gỏi.', 9000, N'Kg', 0.2, N'20x2cm', 2, 1),
('SP014', 'LSP001', N'Nấm rơm tươi', N'[Ngắn] Nấm mềm ngon. [Dài] Nấm rơm tươi từ nông trại sạch, thích hợp cho các món kho, xào, canh.', 28000, N'Kg', 0.3, N'3x3cm', 3, 1),
('SP015', 'LSP001', N'Nấm bào ngư', N'[Ngắn] Nấm dai ngon. [Dài] Nấm bào ngư trắng, thịt dày, giòn ngọt, thường dùng trong các món xào, súp hoặc chiên giòn.', 30000, N'Kg', 0.4, N'4x2cm', 5, 1),
('SP016', 'LSP001', N'Mồng tơi', N'[Ngắn] Rau trơn mát. [Dài] Mồng tơi chứa nhiều chất nhầy, hỗ trợ tiêu hóa, là nguyên liệu quen thuộc trong món canh cua.', 8000, N'Kg', 0.3, N'25x2cm', 2, 1),
('SP017', 'LSP001', N'Đậu que', N'[Ngắn] Đậu non giòn. [Dài] Đậu que non, xanh mướt, thường được xào với thịt bò hoặc luộc ăn kèm nước chấm.', 14000, N'Kg', 0.3, N'15x1cm', 3, 1),
('SP018', 'LSP001', N'Dền cơm', N'[Ngắn] Rau dền sạch. [Dài] Dền cơm là loại rau dại giàu dinh dưỡng, được trồng theo hướng hữu cơ, dùng để nấu canh hoặc luộc.', 9000, N'Kg', 0.2, N'20x2cm', 2, 1),
('SP019', 'LSP001', N'Rau tần ô', N'[Ngắn] Rau thơm ngon. [Dài] Tần ô có hương thơm đặc trưng, thường xuất hiện trong lẩu hoặc nấu canh với thịt bằm.', 11000, N'Kg', 0.3, N'25x2cm', 3, 1),
('SP020', 'LSP001', N'Bí đỏ trái tròn', N'[Ngắn] Bí đỏ ngọt dẻo. [Dài] Bí đỏ được trồng tại nông trại hữu cơ, giàu vitamin A, thường dùng nấu canh hoặc hấp.', 13000, N'Kg', 1.5, N'20x15cm', 7, 1);

-- LSP002 – ĐÔNG LẠNH (20 sản phẩm)
INSERT INTO SanPham (MaSP, MaLoaiSP, TenSP, MoTa, GiaBan, DonViTinh, TrongLuong, KichThuoc, HanSuDung, TrangThai) VALUES
('SP021', 'LSP002', N'Tôm sú đông lạnh', N'[Ngắn] Tôm đông lạnh sạch. [Dài] Tôm sú đông lạnh được cấp đông ngay sau khi đánh bắt để giữ độ tươi ngon, thịt chắc và ngọt, dùng để nấu lẩu, hấp, chiên xù.', 120000, N'Kg', 1.0, N'15x3cm', 180, 1),
('SP022', 'LSP002', N'Cá hồi phi lê', N'[Ngắn] Cá hồi phi lê tươi ngon. [Dài] Cá hồi Na Uy phi lê được cấp đông nhanh, giữ nguyên chất dinh dưỡng và màu sắc tự nhiên, thích hợp cho sashimi hoặc áp chảo.', 230000, N'Kg', 0.8, N'20x5cm', 180, 1),
('SP023', 'LSP002', N'Mực ống đông lạnh', N'[Ngắn] Mực tươi cấp đông. [Dài] Mực ống được làm sạch và cấp đông nhanh, giữ được độ giòn và vị ngọt tự nhiên, thích hợp nướng, hấp hoặc chiên giòn.', 150000, N'Kg', 0.5, N'12x2cm', 180, 1),
('SP024', 'LSP002', N'Cá viên đông lạnh', N'[Ngắn] Cá viên tiện lợi. [Dài] Cá viên làm từ cá thát lát nguyên chất, được cấp đông sẵn, tiện lợi cho món lẩu, chiên hoặc nấu canh.', 60000, N'Kg', 0.5, N'2x2cm', 180, 1),
('SP025', 'LSP002', N'Thịt bò viên đông lạnh', N'[Ngắn] Bò viên thơm ngon. [Dài] Bò viên được chế biến từ thịt bò tươi, có vị thơm đặc trưng, dễ dàng chế biến trong các món lẩu, xào hoặc bún bò.', 65000, N'Kg', 0.5, N'2x2cm', 180, 1),
('SP026', 'LSP002', N'Gà nguyên con đông lạnh', N'[Ngắn] Gà cấp đông sạch. [Dài] Gà ta nguyên con được làm sạch và cấp đông theo chuẩn VSATTP, phù hợp để quay, luộc hoặc hấp.', 110000, N'Con', 1.5, N'25x15cm', 180, 1),
('SP027', 'LSP002', N'Chân gà rút xương đông lạnh', N'[Ngắn] Chân gà tiện dụng. [Dài] Chân gà đã được rút xương, cấp đông sạch, dùng để trộn gỏi hoặc nướng muối ớt.', 85000, N'Kg', 0.8, N'8x3cm', 180, 1),
('SP028', 'LSP002', N'Cá thu cắt lát đông lạnh', N'[Ngắn] Cá thu cắt lát. [Dài] Cá thu được cắt lát và cấp đông nhanh, thích hợp để chiên hoặc kho với nước dừa.', 130000, N'Kg', 0.6, N'10x5cm', 180, 1),
('SP029', 'LSP002', N'Xúc xích tiệt trùng', N'[Ngắn] Xúc xích đậm vị. [Dài] Xúc xích heo được tiệt trùng và cấp đông, dễ dàng chế biến các món ăn nhanh hoặc nướng BBQ.', 40000, N'Kg', 0.4, N'15x2cm', 180, 1),
('SP030', 'LSP002', N'Cá basa phi lê đông lạnh', N'[Ngắn] Cá basa tiện lợi. [Dài] Cá basa phi lê đã bỏ xương, không tanh, dễ chế biến các món chiên giòn, kho tộ hoặc nấu canh chua.', 85000, N'Kg', 0.7, N'18x4cm', 180, 1),
('SP031', 'LSP002', N'Tôm sú đông lạnh 1kg', N'Tôm tươi ngon được cấp đông nhanh. ... Giữ được vị ngọt tự nhiên và an toàn thực phẩm.', 195000, N'Kg', 1.0, N'15x3cm', 180, 1),
('SP032', 'LSP002', N'Cá diêu hồng đông lạnh 1kg', N'Cá được sơ chế sạch sẽ và cấp đông sâu. ... Tiện lợi cho mọi món ăn hằng ngày.', 85000, N'Kg', 1.0, N'25x8cm', 180, 1),
('SP033', 'LSP002', N'Mực ống đông lạnh 500g', N'Mực tươi được làm sạch và đóng gói kỹ lưỡng. ... Đảm bảo an toàn và tươi ngon cho bữa cơm gia đình.', 97000, N'Kg', 0.5, N'12x2cm', 180, 1),
('SP034', 'LSP002', N'Thịt ba rọi đông lạnh 500g', N'Thịt heo ba rọi thái lát mỏng và đóng gói. ... Phù hợp chế biến món xào, nướng hoặc lẩu.', 72000, N'Kg', 0.5, N'10x5cm', 180, 1),
('SP035', 'LSP002', N'Cánh gà đông lạnh 1kg', N'Cánh gà tươi được lựa chọn kỹ càng. ... Cấp đông nhanh giúp bảo quản lâu và giữ nguyên dinh dưỡng.', 105000, N'Kg', 1.0, N'12x8cm', 180, 1),
('SP036', 'LSP002', N'Thăn bò đông lạnh 500g', N'Thịt bò thăn nhập khẩu, mềm, thơm. ... Rất thích hợp cho món bít tết hoặc lẩu.', 168000, N'Kg', 0.5, N'15x8cm', 180, 1),
('SP037', 'LSP002', N'Hàu nửa vỏ đông lạnh 1kg', N'Hàu biển tươi được sơ chế và cấp đông. ... Dễ chế biến và bổ dưỡng cho cả gia đình.', 125000, N'Kg', 1.0, N'8x4cm', 180, 1),
('SP038', 'LSP002', N'Cá viên đông lạnh 500g', N'Cá viên được làm từ cá tươi nghiền nhuyễn. ... Dùng tốt cho món lẩu hoặc chiên.', 55000, N'Kg', 0.5, N'2x2cm', 180, 1),
('SP039', 'LSP002', N'Súp lơ đông lạnh 500g', N'Súp lơ tươi cắt nhỏ và cấp đông ngay sau thu hoạch. ... Giữ nguyên độ giòn và hương vị tự nhiên.', 39000, N'Kg', 0.5, N'15x10cm', 180, 1),
('SP040', 'LSP002', N'Đậu que đông lạnh 500g', N'Đậu que tươi cấp đông giữ trọn độ giòn và dinh dưỡng. ... Phù hợp chế biến xào, luộc, hấp.', 36000, N'Kg', 0.5, N'15x1cm', 180, 1);

-- LSP003 – ĐỒ ĐÓNG HỘP (20 sản phẩm)
INSERT INTO SanPham (MaSP, MaLoaiSP, TenSP, MoTa, GiaBan, DonViTinh, TrongLuong, KichThuoc, HanSuDung, TrangThai) VALUES
('SP041', 'LSP003', N'Cá ngừ ngâm dầu hộp 185g', N'Cá ngừ nguyên miếng ngâm dầu thơm béo. ... Đóng hộp tiện lợi, thích hợp ăn liền hoặc trộn salad.', 32000, N'Hộp', 0.185, N'10x8x3cm', 730, 1),
('SP042', 'LSP003', N'Pate gan heo hộp 170g', N'Pate gan heo mềm mịn, thơm ngon. ... Phù hợp cho bữa sáng hoặc món ăn nhẹ giàu đạm.', 26000, N'Hộp', 0.170, N'8x6x2cm', 730, 1),
('SP043', 'LSP003', N'Đậu hầm sốt cà hộp 400g', N'Đậu trắng được hầm mềm với sốt cà đậm đà. ... Món ăn bổ dưỡng, tiện lợi cho bữa cơm gia đình.', 23000, N'Hộp', 0.400, N'12x8x4cm', 730, 1),
('SP044', 'LSP003', N'Măng chua đóng hộp 400g', N'Măng được sơ chế kỹ và đóng hộp an toàn. ... Dùng nấu canh chua hoặc xào rất tiện lợi.', 19000, N'Hộp', 0.400, N'12x8x4cm', 730, 1),
('SP045', 'LSP003', N'Nấm rơm hộp 400g', N'Nấm rơm tươi ngon được đóng hộp giữ nguyên vị. ... Dùng cho các món canh, xào, lẩu cực kỳ tiện.', 25000, N'Hộp', 0.400, N'12x8x4cm', 730, 1),
('SP046', 'LSP003', N'Thịt kho trứng hộp 400g', N'Món thịt kho trứng truyền thống được chế biến sẵn. ... Hương vị đậm đà, mở nắp là ăn ngay.', 45000, N'Hộp', 0.400, N'12x8x4cm', 730, 1),
('SP047', 'LSP003', N'Chả cá sốt cà hộp 200g', N'Chả cá chiên sốt cà đậm vị, dễ dùng. ... Phù hợp cho các bữa ăn nhanh và vẫn đầy đủ dinh dưỡng.', 29000, N'Hộp', 0.200, N'10x6x3cm', 730, 1),
('SP048', 'LSP003', N'Ngô ngọt đóng hộp 400g', N'Ngô ngọt vàng óng, giòn ngọt tự nhiên. ... Có thể ăn liền hoặc chế biến món salad, soup.', 21000, N'Hộp', 0.400, N'12x8x4cm', 730, 1),
('SP049', 'LSP003', N'Cá mòi sốt cà hộp 155g', N'Cá mòi được nấu cùng nước sốt cà đậm đà. ... Tiện dụng cho mọi bữa ăn gia đình.', 27000, N'Hộp', 0.155, N'8x6x3cm', 730, 1),
('SP050', 'LSP003', N'Thịt hộp lợn vai 340g', N'Thịt lợn được nấu chín, nén hộp, dễ bảo quản. ... Phù hợp đi du lịch, dã ngoại hoặc ăn nhanh.', 37000, N'Hộp', 0.340, N'12x8x4cm', 730, 1),
('SP051', 'LSP003', N'Bắp cải muối chua hộp 400g', N'Bắp cải được muối chua vừa vị, giòn ngon. ... Dùng ngay hoặc nấu cùng món thịt đều phù hợp.', 18000, N'Hộp', 0.400, N'12x8x4cm', 730, 1),
('SP052', 'LSP003', N'Cà rốt đóng hộp 400g', N'Cà rốt được cắt khúc và hấp chín. ... Tiện lợi cho các món xào, soup hoặc salad.', 22000, N'Hộp', 0.400, N'12x8x4cm', 730, 1),
('SP053', 'LSP003', N'Giá đỗ đóng hộp 400g', N'Giá đỗ sạch, giòn ngon được đóng hộp. ... Bổ sung dinh dưỡng và dễ bảo quản lâu dài.', 21000, N'Hộp', 0.400, N'12x8x4cm', 730, 1),
('SP054', 'LSP003', N'Cà chua xay hộp 400g', N'Cà chua tươi được nghiền nhuyễn và tiệt trùng. ... Dùng làm nước sốt hoặc nấu canh rất tiện.', 24000, N'Hộp', 0.400, N'12x8x4cm', 730, 1),
('SP055', 'LSP003', N'Dưa cải chua hộp 400g', N'Dưa cải muối chua đậm đà hương vị Bắc. ... Thích hợp ăn kèm món thịt kho, canh chua.', 18500, N'Hộp', 0.400, N'12x8x4cm', 730, 1),
('SP056', 'LSP003', N'Hạt sen đóng hộp 400g', N'Hạt sen tươi được làm sạch và hấp chín. ... Phù hợp cho món chè, hầm hoặc cháo.', 28000, N'Hộp', 0.400, N'12x8x4cm', 730, 1),
('SP057', 'LSP003', N'Dừa non đóng hộp 400g', N'Dừa non thái lát được đóng hộp bảo quản lâu. ... Sử dụng tốt trong món chè hoặc cocktail trái cây.', 31000, N'Hộp', 0.400, N'12x8x4cm', 730, 1),
('SP058', 'LSP003', N'Thịt bò hầm hộp 340g', N'Thịt bò hầm mềm, vị đậm đà. ... Món ăn chế biến sẵn phù hợp cho dân văn phòng.', 46000, N'Hộp', 0.340, N'12x8x4cm', 730, 1),
('SP059', 'LSP003', N'Nấm bào ngư hộp 400g', N'Nấm bào ngư tươi được đóng hộp tiện lợi. ... Dùng để xào, nấu lẩu hoặc hầm đều ngon.', 27000, N'Hộp', 0.400, N'12x8x4cm', 730, 1),
('SP060', 'LSP003', N'Mì bò kho hộp 350g', N'Mì ăn liền với nước dùng bò kho đậm vị. ... Món ăn nhanh đầy đủ năng lượng cho người bận rộn.', 33000, N'Hộp', 0.350, N'12x8x4cm', 730, 1);

-- LSP004 – ĐỒ UỐNG (20 sản phẩm)
INSERT INTO SanPham (MaSP, MaLoaiSP, TenSP, MoTa, GiaBan, DonViTinh, TrongLuong, KichThuoc, HanSuDung, TrangThai) VALUES
('SP061', 'LSP004', N'Nước khoáng thiên nhiên 500ml', N'Nước khoáng tinh khiết, giải khát tức thì. ... Giàu khoáng chất, tốt cho sức khỏe, thích hợp sử dụng hàng ngày.', 6000, N'Chai', 0.500, N'7x7x20cm', 365, 1),
('SP062', 'LSP004', N'Trà xanh không độ 455ml', N'Trà xanh thanh mát, không đường. ... Giúp giải nhiệt, chống oxy hóa và tăng cường sức khỏe.', 9000, N'Chai', 0.455, N'6x6x18cm', 365, 1),
('SP063', 'LSP004', N'Nước tăng lực Red Bull 250ml', N'Nước uống tăng lực hương vị đặc trưng. ... Phù hợp cho người hoạt động thể chất cao, giúp tỉnh táo.', 12000, N'Lon', 0.250, N'6x6x12cm', 365, 1),
('SP064', 'LSP004', N'Nước ép cam nguyên chất 330ml', N'Nước ép cam giàu vitamin C, vị tự nhiên. ... Tăng cường đề kháng, tốt cho làn da và hệ miễn dịch.', 18000, N'Chai', 0.330, N'6x6x15cm', 180, 1),
('SP065', 'LSP004', N'Sữa đậu nành Fami 200ml', N'Sữa đậu nành nguyên chất từ hạt đậu nành Việt. ... Bổ sung đạm thực vật và tốt cho tim mạch.', 7000, N'Hộp', 0.200, N'5x5x10cm', 180, 1),
('SP066', 'LSP004', N'Nước suối Aquafina 1.5L', N'Nước uống tinh khiết được lọc 7 bước. ... Thích hợp dùng cho cả gia đình và mang đi học, đi làm.', 10000, N'Chai', 1.500, N'8x8x25cm', 365, 1),
('SP067', 'LSP004', N'Nước ngọt Coca-Cola lon 330ml', N'Nước ngọt có gas hương vị cổ điển. ... Giải khát tức thì, phù hợp với các bữa tiệc và ăn nhanh.', 10000, N'Lon', 0.330, N'6x6x12cm', 365, 1),
('SP068', 'LSP004', N'Trà sữa trân châu đóng chai 320ml', N'Trà sữa thơm ngọt, kèm trân châu mềm dai. ... Phù hợp cho giới trẻ, mang đi mọi nơi.', 19000, N'Chai', 0.320, N'6x6x15cm', 180, 1),
('SP069', 'LSP004', N'Nước ép táo nguyên chất 330ml', N'Nước ép táo ngọt dịu, không chất bảo quản. ... Tốt cho hệ tiêu hóa và cung cấp vitamin A.', 17500, N'Chai', 0.330, N'6x6x15cm', 180, 1),
('SP070', 'LSP004', N'Bò húc Thái chai thủy tinh 250ml', N'Nước tăng lực nhập khẩu hương vị đậm đà. ... Giúp tỉnh táo, bổ sung vitamin B và taurine.', 15000, N'Chai', 0.250, N'5x5x12cm', 365, 1),
('SP071', 'LSP004', N'Nước dừa tươi đóng hộp 330ml', N'Nước dừa tự nhiên, giữ nguyên hương vị tươi mát. ... Giàu khoáng và chất điện giải, giải nhiệt tốt.', 14000, N'Hộp', 0.330, N'6x6x15cm', 180, 1),
('SP072', 'LSP004', N'Trà đào hương vị trái cây 455ml', N'Trà đào ngọt thanh, mùi thơm dịu nhẹ. ... Dùng lạnh sẽ ngon hơn, hợp mọi lứa tuổi.', 10000, N'Chai', 0.455, N'6x6x18cm', 365, 1),
('SP073', 'LSP004', N'Nước yến sào có đường 240ml', N'Nước yến giàu đạm và vi khoáng. ... Hỗ trợ phục hồi sức khỏe, đẹp da và tăng cường sức đề kháng.', 28000, N'Chai', 0.240, N'5x5x12cm', 365, 1),
('SP074', 'LSP004', N'Cà phê sữa đá đóng lon 330ml', N'Cà phê Việt đậm đà, hương vị truyền thống. ... Tiện lợi khi di chuyển, giữ nguyên độ ngon như pha máy.', 11000, N'Lon', 0.330, N'6x6x12cm', 365, 1),
('SP075', 'LSP004', N'Trà atiso đỏ 500ml', N'Trà atiso đỏ thanh mát, vị chua nhẹ. ... Giúp mát gan, hỗ trợ tiêu hóa và lợi tiểu.', 9000, N'Chai', 0.500, N'7x7x20cm', 365, 1),
('SP076', 'LSP004', N'Nước ép nho nguyên chất 330ml', N'Nước ép nho ngọt dịu, giàu vitamin và chất chống oxy hóa. ... Giúp cải thiện làn da và ngăn ngừa lão hóa.', 18000, N'Chai', 0.330, N'6x6x15cm', 180, 1),
('SP077', 'LSP004', N'Nước nha đam hạt chia 500ml', N'Nước uống kết hợp nha đam và hạt chia. ... Bổ dưỡng, làm mát cơ thể, đẹp da.', 16000, N'Chai', 0.500, N'7x7x20cm', 180, 1),
('SP078', 'LSP004', N'Nước cam có tép 450ml', N'Nước cam có tép thật, vị ngọt dịu tự nhiên. ... Giàu vitamin C, tăng cường miễn dịch và sáng da.', 15000, N'Chai', 0.450, N'6x6x18cm', 180, 1),
('SP079', 'LSP004', N'Nước khoáng có gas Vĩnh Hảo 500ml', N'Nước khoáng có gas vị nhẹ nhàng. ... Giúp tiêu hóa tốt, dùng với trái cây tươi rất ngon.', 10000, N'Chai', 0.500, N'7x7x20cm', 365, 1),
('SP080', 'LSP004', N'Nước chanh muối đóng chai 350ml', N'Nước chanh muối pha sẵn, vị mặn ngọt hài hòa. ... Giải khát, bù điện giải khi vận động nhiều.', 9500, N'Chai', 0.350, N'6x6x15cm', 180, 1);

-- LSP005 – SỮA & EM BÉ (20 sản phẩm)
INSERT INTO SanPham (MaSP, MaLoaiSP, TenSP, MoTa, GiaBan, DonViTinh, TrongLuong, KichThuoc, HanSuDung, TrangThai) VALUES
('SP081', 'LSP005', N'Sữa bột Enfagrow 400g', N'Sữa bột cho trẻ từ 1-3 tuổi, giàu DHA. ... Giúp phát triển trí não, tăng cường miễn dịch và tiêu hóa khỏe.', 245000, N'Hộp', 0.400, N'15x10x8cm', 730, 1),
('SP082', 'LSP005', N'Sữa tươi tiệt trùng TH True Milk 180ml', N'Sữa tươi tiệt trùng, vị nguyên chất. ... Giàu canxi, tốt cho xương, phù hợp mọi lứa tuổi.', 7000, N'Hộp', 0.180, N'5x5x10cm', 180, 1),
('SP083', 'LSP005', N'Bột ăn dặm Nestle gạo sữa 200g', N'Bột ăn dặm vị gạo sữa dễ tiêu hóa. ... Hỗ trợ bé tập ăn dặm, bổ sung vitamin và khoáng.', 58000, N'Hộp', 0.200, N'12x8x6cm', 730, 1),
('SP084', 'LSP005', N'Tã dán Pampers NB 40 miếng', N'Tã dán siêu mềm, thấm hút tốt. ... Giúp bé ngủ ngon, da khô thoáng suốt cả đêm.', 195000, N'Gói', 0.800, N'25x15x8cm', 1095, 1),
('SP085', 'LSP005', N'Nước rửa bình sữa D-nee 620ml', N'Dung dịch rửa bình sữa an toàn. ... Không chứa hóa chất độc hại, dễ trôi sạch, không mùi.', 53000, N'Chai', 0.620, N'8x8x20cm', 730, 1),
('SP086', 'LSP005', N'Khăn ướt Bobby không mùi 100 tờ', N'Khăn ướt mềm mại, không chứa cồn. ... Phù hợp vệ sinh cho bé, dùng được cho da nhạy cảm.', 33000, N'Gói', 0.300, N'15x10x5cm', 730, 1),
('SP087', 'LSP005', N'Sữa chua uống Probi 65ml (lốc 4 chai)', N'Sữa chua uống men sống hỗ trợ tiêu hóa. ... Tăng cường hệ miễn dịch, ngon mát dễ uống.', 16000, N'Lốc', 0.260, N'15x10x8cm', 180, 1),
('SP088', 'LSP005', N'Dụng cụ hút mũi cho bé', N'Dụng cụ hút mũi bằng silicon mềm. ... Giúp làm sạch mũi nhẹ nhàng, không gây tổn thương.', 29000, N'Cái', 0.050, N'8x3x2cm', 1095, 1),
('SP089', 'LSP005', N'Sữa công thức Friso Gold 900g', N'Sữa công thức dành cho trẻ từ 1-2 tuổi. ... Bổ sung chất xơ GOS, hỗ trợ đường ruột và miễn dịch.', 510000, N'Hộp', 0.900, N'20x15x10cm', 730, 1),
('SP090', 'LSP005', N'Bánh ăn dặm Pigeon vị bí đỏ 50g', N'Bánh ăn dặm tan nhanh trong miệng. ... Giúp bé làm quen với đồ ăn, dễ cầm nắm.', 45000, N'Hộp', 0.050, N'10x8x3cm', 730, 1),
('SP091', 'LSP005', N'Sữa rửa mặt cho mẹ bầu Organic 100ml', N'Sữa rửa mặt thiên nhiên cho da nhạy cảm. ... Không chứa paraben, dịu nhẹ và an toàn.', 79000, N'Chai', 0.100, N'6x6x15cm', 730, 1),
('SP092', 'LSP005', N'Dầu gội em bé Johnson 200ml', N'Dầu gội dịu nhẹ, không cay mắt. ... Làm sạch tóc và da đầu cho bé mà không gây kích ứng.', 57000, N'Chai', 0.200, N'7x7x18cm', 730, 1),
('SP093', 'LSP005', N'Thermometer đo trán điện tử', N'Nhiệt kế hồng ngoại đo trán nhanh chóng. ... Cho kết quả chính xác trong vài giây, an toàn.', 195000, N'Cái', 0.100, N'10x3x2cm', 1095, 1),
('SP094', 'LSP005', N'Sữa nước Grow Plus đỏ 180ml', N'Sữa dành cho bé nhẹ cân, suy dinh dưỡng. ... Giúp tăng cân đều, phát triển khỏe mạnh.', 12000, N'Hộp', 0.180, N'5x5x10cm', 180, 1),
('SP095', 'LSP005', N'Bình sữa Avent nhựa PP 260ml', N'Bình sữa cổ rộng, van chống sặc. ... Giúp bé bú dễ dàng, không bị đầy hơi.', 230000, N'Cái', 0.150, N'8x8x20cm', 1095, 1),
('SP096', 'LSP005', N'Nước muối sinh lý BabyCare 500ml', N'Nước muối sinh lý dùng nhỏ mũi cho bé. ... Làm sạch nhẹ nhàng, hỗ trợ phòng ngừa viêm mũi.', 18000, N'Chai', 0.500, N'7x7x20cm', 730, 1),
('SP097', 'LSP005', N'Trái cây nghiền Hipp táo chuối 125g', N'Trái cây nghiền sẵn, vị ngọt tự nhiên. ... Cung cấp vitamin C, giúp bé ăn ngon miệng.', 40000, N'Hộp', 0.125, N'8x6x4cm', 730, 1),
('SP098', 'LSP005', N'Sữa tươi tiệt trùng Dutch Lady 110ml', N'Sữa tươi vị socola hoặc dâu. ... Bổ sung dưỡng chất, ngon miệng dễ uống.', 5000, N'Hộp', 0.110, N'4x4x8cm', 180, 1),
('SP099', 'LSP005', N'Bàn chải răng silicon cho bé 6 tháng+', N'Bàn chải mềm, an toàn cho bé. ... Giúp bé tập đánh răng ngay từ sớm.', 29000, N'Cái', 0.050, N'12x2x1cm', 1095, 1),
('SP100', 'LSP005', N'Balo y tá đựng đồ sơ sinh', N'Balo chuyên dụng mang theo khi ra ngoài. ... Có nhiều ngăn, dễ sắp xếp đồ dùng cho bé.', 155000, N'Cái', 0.800, N'30x20x15cm', 1095, 1);

-- LSP006 – GIA VỊ & DẦU ĂN (20 sản phẩm)
INSERT INTO SanPham (MaSP, MaLoaiSP, TenSP, MoTa, GiaBan, DonViTinh, TrongLuong, KichThuoc, HanSuDung, TrangThai) VALUES
('SP101', 'LSP006', N'Nước mắm Nam Ngư 500ml', N'Nước mắm truyền thống đậm đà. ... Được ủ từ cá cơm, hương vị tự nhiên, dùng nêm nếm và chấm.', 24000, N'Chai', 0.500, N'7x7x20cm', 1095, 1),
('SP102', 'LSP006', N'Nước tương Maggi đậm đặc 700ml', N'Nước tương đậm đà, hương vị quen thuộc. ... Thích hợp ăn kèm món luộc, chiên, xào.', 32000, N'Chai', 0.700, N'8x8x25cm', 1095, 1),
('SP103', 'LSP006', N'Dầu ăn Tường An 1L', N'Dầu thực vật nguyên chất. ... Giàu vitamin A, E tốt cho tim mạch và sức khỏe.', 42000, N'Chai', 1.000, N'8x8x25cm', 1095, 1),
('SP104', 'LSP006', N'Muối i-ốt 500g', N'Muối trắng tinh khiết có bổ sung i-ốt. ... Giúp phòng ngừa bướu cổ và tăng cường sức khỏe.', 8000, N'Gói', 0.500, N'15x10x2cm', 1095, 1),
('SP105', 'LSP006', N'Hạt nêm Knorr thịt thăn 400g', N'Hạt nêm vị thịt thăn xương ống. ... Giúp món ăn đậm vị, thơm ngon hơn.', 45000, N'Hộp', 0.400, N'12x8x6cm', 1095, 1),
('SP106', 'LSP006', N'Tiêu đen xay Dh Foods 50g', N'Tiêu đen xay mịn, thơm nồng. ... Tăng hương vị cho các món kho, nướng, súp.', 29000, N'Hộp', 0.050, N'8x6x3cm', 1095, 1),
('SP107', 'LSP006', N'Tỏi băm sẵn 200g', N'Tỏi tươi xay nhuyễn, tiện lợi khi nấu ăn. ... Giữ nguyên hương vị và mùi thơm tự nhiên.', 17000, N'Hộp', 0.200, N'10x8x4cm', 180, 1),
('SP108', 'LSP006', N'Hành phi giòn 100g', N'Hành phi vàng thơm, giòn rụm. ... Dùng rắc lên cơm, cháo, bún, phở tăng hương vị.', 23000, N'Hộp', 0.100, N'8x6x3cm', 365, 1),
('SP109', 'LSP006', N'Dầu hào Maggi 350g', N'Dầu hào vị ngọt thanh. ... Dùng để xào rau, thịt giúp món ăn thêm đậm đà, bóng đẹp.', 27000, N'Chai', 0.350, N'7x7x18cm', 1095, 1),
('SP110', 'LSP006', N'Ớt bột Hàn Quốc 100g', N'Ớt bột vị cay nhẹ, màu đẹp. ... Dùng làm kim chi, lẩu, các món cay kiểu Hàn.', 38000, N'Hộp', 0.100, N'8x6x3cm', 1095, 1),
('SP111', 'LSP006', N'Bột nghệ nguyên chất 100g', N'Bột nghệ vàng nguyên chất. ... Dùng ướp thịt, làm bánh, tốt cho tiêu hóa.', 25000, N'Hộp', 0.100, N'8x6x3cm', 1095, 1),
('SP112', 'LSP006', N'Giấm gạo Lâm Thủy 500ml', N'Giấm gạo lên men tự nhiên. ... Dùng trộn gỏi, pha nước chấm, khử mùi tanh.', 16000, N'Chai', 0.500, N'7x7x20cm', 1095, 1),
('SP113', 'LSP006', N'Dầu mè đen Lee Kum Kee 200ml', N'Dầu mè nguyên chất thơm ngon. ... Tăng hương vị cho món Nhật, Hàn, salad.', 46000, N'Chai', 0.200, N'6x6x15cm', 1095, 1),
('SP114', 'LSP006', N'Bột canh Hải Châu 190g', N'Bột canh pha sẵn muối, bột ngọt. ... Dùng để nêm nếm tiện lợi, nhanh chóng.', 11000, N'Hộp', 0.190, N'10x8x4cm', 1095, 1),
('SP115', 'LSP006', N'Nước cốt dừa Aroy-D 400ml', N'Nước cốt dừa đóng hộp thơm béo. ... Dùng nấu chè, cà ri, bánh, món Thái.', 34000, N'Hộp', 0.400, N'12x8x4cm', 1095, 1),
('SP116', 'LSP006', N'Bột ngọt Ajinomoto 400g', N'Bột ngọt giúp làm nổi bật vị ngọt tự nhiên. ... Phù hợp cho mọi món ăn.', 28000, N'Hộp', 0.400, N'12x8x6cm', 1095, 1),
('SP117', 'LSP006', N'Bột sả khô 50g', N'Sả khô xay nhuyễn. ... Dùng tẩm ướp thịt nướng, món chay, món kho.', 15000, N'Hộp', 0.050, N'8x6x3cm', 1095, 1),
('SP118', 'LSP006', N'Tương ớt Chin-Su 250g', N'Tương ớt cay vừa, màu sắc hấp dẫn. ... Dùng chấm đồ chiên, rán, ăn với phở, bún.', 12000, N'Chai', 0.250, N'6x6x15cm', 1095, 1),
('SP119', 'LSP006', N'Nước màu dừa Bến Tre 250ml', N'Nước hàng kho cá, kho thịt. ... Giúp món ăn lên màu đẹp, vị ngọt thanh.', 20000, N'Chai', 0.250, N'6x6x15cm', 1095, 1),
('SP120', 'LSP006', N'Nước mắm Phú Quốc truyền thống 520ml', N'Nước mắm nguyên chất cá cơm. ... Đậm đà, thơm ngon đúng chất nước mắm xưa.', 68000, N'Chai', 0.520, N'7x7x20cm', 1095, 1);

-- LSP007 – HÓA PHẨM & TẨY RỬA (20 sản phẩm)
INSERT INTO SanPham (MaSP, MaLoaiSP, TenSP, MoTa, GiaBan, DonViTinh, TrongLuong, KichThuoc, HanSuDung, TrangThai) VALUES
('SP121', 'LSP007', N'Nước rửa chén Sunlight chanh 750ml', N'Nước rửa chén hương chanh. ... Tẩy sạch dầu mỡ, dịu nhẹ với da tay.', 28000, N'Chai', 0.750, N'8x8x25cm', 1095, 1),
('SP122', 'LSP007', N'Nước lau sàn Gift lavender 1L', N'Nước lau sàn hương oải hương. ... Diệt khuẩn, khử mùi hiệu quả, sàn sạch bóng.', 34000, N'Chai', 1.000, N'8x8x25cm', 1095, 1),
('SP123', 'LSP007', N'Nước giặt Omo Matic 2.7kg', N'Nước giặt cho máy giặt cửa ngang. ... Đánh bay vết bẩn, lưu hương thơm lâu.', 132000, N'Chai', 2.700, N'15x10x25cm', 1095, 1),
('SP124', 'LSP007', N'Nước xả vải Downy hương nắng mai 800ml', N'Nước xả làm mềm vải. ... Giữ mùi thơm mát, giúp quần áo luôn mềm mại.', 49000, N'Chai', 0.800, N'8x8x25cm', 1095, 1),
('SP125', 'LSP007', N'Nước tẩy toilet Duck 900ml', N'Tẩy rửa toilet diệt khuẩn. ... Làm sạch và khử mùi bồn cầu hiệu quả.', 36000, N'Chai', 0.900, N'8x8x25cm', 1095, 1),
('SP126', 'LSP007', N'Nước rửa tay Lifebuoy 500ml', N'Rửa tay diệt khuẩn 99.9%. ... Hương thơm dễ chịu, bảo vệ tay sạch khuẩn.', 42000, N'Chai', 0.500, N'7x7x20cm', 1095, 1),
('SP127', 'LSP007', N'Nước lau kính Gift 500ml', N'Nước lau kính chống bám bụi. ... Cho bề mặt kính sáng bóng, không vệt.', 26000, N'Chai', 0.500, N'7x7x20cm', 1095, 1),
('SP128', 'LSP007', N'Nước tẩy đa năng CIF 500ml', N'Tẩy rửa vết bẩn cứng đầu. ... Dùng cho nhà bếp, nhà tắm, vật dụng inox.', 45000, N'Chai', 0.500, N'7x7x20cm', 1095, 1),
('SP129', 'LSP007', N'Bột giặt Ariel hương Downy 3.8kg', N'Bột giặt sạch sâu, thơm lâu. ... Loại bỏ vết bẩn, giữ màu vải bền đẹp.', 125000, N'Hộp', 3.800, N'20x15x25cm', 1095, 1),
('SP130', 'LSP007', N'Khăn giấy Bless You hộp 200 tờ', N'Giấy mềm mịn, thấm hút tốt. ... Dùng lau mặt, dùng trong gia đình, văn phòng.', 29000, N'Hộp', 0.200, N'15x10x8cm', 1095, 1),
('SP131', 'LSP007', N'Giấy vệ sinh Pulppy 10 cuộn', N'Giấy vệ sinh trắng mềm. ... An toàn cho da, phù hợp gia đình và văn phòng.', 49000, N'Gói', 0.800, N'25x15x8cm', 1095, 1),
('SP132', 'LSP007', N'Nước rửa bình sữa D-nee 620ml', N'Rửa sạch bình sữa, đồ dùng trẻ em. ... Dịu nhẹ, an toàn cho bé sơ sinh.', 52000, N'Chai', 0.620, N'8x8x20cm', 730, 1),
('SP133', 'LSP007', N'Bông gòn y tế 100g', N'Bông trắng sạch, không tạp chất. ... Dùng lau chùi vết thương, vệ sinh cá nhân.', 17000, N'Gói', 0.100, N'15x10x3cm', 1095, 1),
('SP134', 'LSP007', N'Bàn chải vệ sinh nhà tắm đa năng', N'Thiết kế chắc chắn, dễ cầm. ... Làm sạch ngóc ngách nhà tắm, bồn rửa.', 32000, N'Cái', 0.200, N'25x5x2cm', 1095, 1),
('SP135', 'LSP007', N'Khăn ướt Mamamy 100 tờ', N'Khăn mềm, không cồn. ... Dùng lau mặt, tay chân cho bé và người lớn.', 37000, N'Gói', 0.300, N'15x10x5cm', 730, 1),
('SP136', 'LSP007', N'Bình xịt côn trùng Raid 600ml', N'Diệt muỗi, gián hiệu quả. ... Hương nhẹ, dùng an toàn trong nhà.', 69000, N'Chai', 0.600, N'8x8x20cm', 1095, 1),
('SP137', 'LSP007', N'Nước súc miệng Listerine 250ml', N'Làm sạch miệng, khử mùi. ... Giúp hơi thở thơm mát, bảo vệ răng miệng.', 49000, N'Chai', 0.250, N'6x6x15cm', 1095, 1),
('SP138', 'LSP007', N'Bột thông cống Hando 100g', N'Làm tan chất thải hữu cơ. ... Thông tắc ống thoát nước, không gây hại đường ống.', 14000, N'Gói', 0.100, N'10x8x3cm', 1095, 1),
('SP139', 'LSP007', N'Nước diệt khuẩn Dettol 500ml', N'Sát khuẩn mạnh mẽ, đa năng. ... Pha loãng để lau sàn, giặt đồ, vệ sinh da.', 87000, N'Chai', 0.500, N'7x7x20cm', 1095, 1),
('SP140', 'LSP007', N'Găng tay cao su Latex', N'Găng tay dẻo, co giãn tốt. ... Dùng khi rửa chén, lau dọn, an toàn cho da tay.', 22000, N'Đôi', 0.050, N'20x10x2cm', 1095, 1);

-- Thêm dữ liệu khuyến mãi
INSERT INTO KhuyenMai (MaKM, TenChuongTrinh, MoTa, LoaiKM, GiaTriKM, DieuKienApDung, NgayBatDau, NgayKetThuc, SoLuongToiDa, DaSuDung, MaQuanLy, TrangThai) VALUES
('KMSP001', N'Giảm giá tháng 7', N'Giảm giá cho tất cả mặt hàng', N'PhầnTrăm', 10.0, N'Áp dụng cho tất cả sản phẩm', '2025-07-01 00:00:00', '2025-07-31 23:59:59', 1000, 50, 'NV002', 1),
('KMSP002', N'Tặng điểm tích lũy', N'Tặng điểm cho khách hàng VIP', N'Điểm', 50, N'Khách hàng VIP trở lên', '2025-07-01 00:00:00', '2025-07-31 23:59:59', 500, 25, 'NV002', 1),
('KMSP003', N'Mua 1 tặng 1', N'Áp dụng cho sản phẩm mỹ phẩm', N'MuaXTangY', 0, N'Mua 1 sản phẩm tặng 1 sản phẩm cùng loại', '2025-07-10 00:00:00', '2025-07-20 23:59:59', 200, 10, 'NV002', 1);

-- Thêm dữ liệu phương thức thanh toán
INSERT INTO PhuongThucThanhToan (MaPTTT, TenPTTT, MoTa, PhiGiaoDich, TrangThai) VALUES
('PTTT001', N'Tiền Mặt', N'Thanh toán bằng tiền mặt', 0, 1),
('PTTT002', N'Chuyển Khoản', N'Thanh toán qua ngân hàng', 0.5, 1),
('PTTT003', N'MoMo', N'Thanh toán bằng ví điện tử MoMo', 1.0, 1),
('PTTT004', N'ZaloPay', N'Thanh toán qua ZaloPay', 1.0, 1),
('PTTT005', N'Thẻ Tín Dụng', N'Thanh toán bằng thẻ tín dụng', 2.0, 1);

-- Thêm dữ liệu kho
INSERT INTO Kho (TenKho, DiaChi, DienTich, SucChua, MaCH, TrangThai) VALUES
(N'Kho EasyMart1', N'123 Nguyễn Xí, Bình Thạnh', 500.00, 1000000.00, 'CH001', 1),
(N'Kho EasyMart2', N'456 Nguyễn Văn Trối, Phú Nhuận', 600.00, 1200000.00, 'CH002', 1);

-- Thêm dữ liệu ca làm việc
INSERT INTO CaLamViec (TenCa, GioBatDau, GioKetThuc, TrangThai) VALUES
(N'Sáng', '08:00:00', '12:00:00', 1),
(N'Chiều', '13:00:00', '17:00:00', 1),
(N'Tối', '18:00:00', '22:00:00', 1),
(N'Cả ngày', '08:00:00', '22:00:00', 1),
(N'Ca đêm', '22:00:00', '07:00:00', 1);

-- Thêm dữ liệu lịch làm việc (đã chuyển xuống phần cập nhật từ inserdata.sql)


-- Thêm dữ liệu giá sản phẩm
INSERT INTO GiaSanPham (MaSP, Gia, NgayBatDau, NgayKetThuc, LyDoThayDoi, NguoiThayDoi) VALUES
('SP001', 15000, '2025-07-01', '2025-07-31', N'Giá mới tháng 7', 'NV002'),
('SP002', 18000, '2025-07-01', '2025-07-31', N'Giá mới tháng 7', 'NV002'),
('SP003', 12000, '2025-07-01', '2025-07-31', N'Giá mới tháng 7', 'NV002'),
('SP004', 13000, '2025-07-01', '2025-07-31', N'Giá mới tháng 7', 'NV002'),
('SP005', 10000, '2025-07-01', '2025-07-31', N'Giá mới tháng 7', 'NV002');

-- Thêm dữ liệu tồn kho chi tiết
INSERT INTO TonKhoChiTiet (MaSP, MaKho, SoLuongTon, SoLuongToiThieu, SoLuongToiDa) VALUES
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
INSERT INTO PhieuNhapHang (MaNCC, MaKho, MaNVLap, NgayNhap, TongTienNhap, TrangThai, GhiChu) VALUES
('NCC001', 1, 'NV002', '2025-07-01 09:00:00', 5500000, 1, N'Nhập hàng tháng 7'),
('NCC002', 1, 'NV003', '2025-07-01 10:00:00', 2800000, 1, N'Nhập hàng tháng 7'),
('NCC003', 2, 'NV004', '2025-07-01 11:00:00', 8000000, 1, N'Nhập hàng tháng 7');

-- Thêm dữ liệu chi tiết phiếu nhập
INSERT INTO ChiTietPhieuNhap (MaPN, MaSP, SoLuongNhap, DonGiaNhap, NgayHetHan, SoLo, NgaySanXuat) VALUES
(1, 'SP001', 100, 12000, '2025-12-31', 'LOT001', '2025-06-15'),
(1, 'SP002', 200, 15000, '2025-11-30', 'LOT002', '2025-06-20'),
(2, 'SP003', 50, 10000, '2025-10-31', 'LOT003', '2025-06-25'),
(2, 'SP004', 80, 11000, '2025-09-30', 'LOT004', '2025-06-30'),
(3, 'SP005', 50, 8000, '2025-08-31', 'LOT005', '2025-07-01');

-- Thêm dữ liệu phiếu xuất kho
INSERT INTO PhieuXuatKho (MaKho, MaNVLap, NgayXuat, TongSoLuong, TongGiaTri, LyDoXuat, TrangThai, GhiChu) VALUES
(1, 'NV002', '2025-07-05 08:00:00', 180, 3240000, N'Bán hàng', 1, N'Xuất kho bán hàng'),
(1, 'NV003', '2025-07-06 09:00:00', 200, 3600000, N'Bán hàng', 1, N'Xuất kho bán hàng'),
(2, 'NV005', '2025-07-07 10:00:00', 140, 2520000, N'Chuyển kho', 1, N'Chuyển kho giữa các cửa hàng');

-- Thêm dữ liệu chi tiết phiếu xuất
INSERT INTO ChiTietPhieuXuat (MaPXK, MaSP, SoLuongXuat, DonGiaXuat) VALUES
(1, 'SP001', 40, 15000),
(1, 'SP002', 60, 18000),
(1, 'SP003', 30, 12000),
(2, 'SP004', 40, 13000),
(2, 'SP005', 30, 10000),
(3, 'SP001', 50, 15000),
(3, 'SP002', 40, 18000),
(3, 'SP003', 50, 12000);

-- Thêm dữ liệu hóa đơn
INSERT INTO HoaDon (MaKH, MaNVLap, MaKM, NgayLap, TongTienHang, TienGiamGia, MaPTTT, TrangThai, DiemTichLuy, GhiChu, NguoiTao) VALUES
('KH001', 'NV002', 'KMSP001', '2025-07-11 14:00:00', 500000, 50000, 'PTTT001', 1, 50, N'Hóa đơn tháng 7', 'NV002'),
('KH002', 'NV003', 'KMSP002', '2025-07-11 15:00:00', 750000, 0, 'PTTT002', 1, 75, N'Hóa đơn tháng 7', 'NV003'),
('KH003', 'NV005', NULL, '2025-07-11 16:00:00', 900000, 0, 'PTTT001', 2, 90, N'Hóa đơn tháng 7', 'NV005'),
('KH004', 'NV006', NULL, '2025-07-12 08:30:00', 200000, 0, 'PTTT002', 3, 20, N'Hóa đơn tháng 7', 'NV006'),
('KH005', 'NV001', 'KMSP003', '2025-07-12 09:45:00', 300000, 0, 'PTTT001', 1, 30, N'Hóa đơn tháng 7', 'NV001');

-- Thêm dữ liệu chi tiết hóa đơn
INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGiaBan, GiamGia) VALUES
(1, 'SP001', 2, 15000, 0),
(1, 'SP002', 5, 18000, 0),
(2, 'SP003', 1, 12000, 0),
(3, 'SP004', 2, 13000, 0),
(4, 'SP005', 2, 10000, 0),
(5, 'SP001', 3, 15000, 0);

-- Thêm dữ liệu khuyến mãi sản phẩm
INSERT INTO KhuyenMaiSanPham (MaKM, MaSP, NgayBatDau, NgayKetThuc) VALUES
('KMSP001', 'SP001', '2025-07-01 00:00:00', '2025-07-31 23:59:59'),
('KMSP001', 'SP002', '2025-07-01 00:00:00', '2025-07-31 23:59:59'),
('KMSP002', 'SP003', '2025-07-01 00:00:00', '2025-07-31 23:59:59'),
('KMSP003', 'SP004', '2025-07-10 00:00:00', '2025-07-20 23:59:59'),
('KMSP003', 'SP005', '2025-07-10 00:00:00', '2025-07-20 23:59:59');

-- Thêm dữ liệu khuyến mãi khách hàng
INSERT INTO KhuyenMaiKhachHang (MaKM, MaKH, NgayApDung, DaSuDung) VALUES
('KMSP001', 'KH001', '2025-07-01 00:00:00', FALSE),
('KMSP001', 'KH002', '2025-07-01 00:00:00', FALSE),
('KMSP002', 'KH003', '2025-07-01 00:00:00', FALSE),
('KMSP003', 'KH004', '2025-07-10 00:00:00', FALSE),
('KMSP003', 'KH005', '2025-07-10 00:00:00', FALSE);

-- Thêm dữ liệu thanh toán
INSERT INTO ThanhToan (MaHD, MaPTTT, SoTienThanhToan, NgayGioTT, TrangThaiTT, MaGiaoDichNganHang, GhiChu) VALUES
(1, 'PTTT001', 450000, '2025-07-11 14:05:00', 1, NULL, N'Thanh toán tiền mặt'),
(2, 'PTTT002', 750000, '2025-07-11 15:10:00', 1, 'GD001', N'Chuyển khoản ngân hàng'),
(3, 'PTTT001', 900000, '2025-07-11 16:20:00', 0, NULL, N'Đang xử lý'),
(4, 'PTTT002', 200000, '2025-07-12 09:00:00', 3, 'GD002', N'Giao dịch bị hủy'),
(5, 'PTTT001', 300000, '2025-07-12 10:00:00', 1, NULL, N'Thanh toán tiền mặt');

-- Thêm dữ liệu giỏ hàng
INSERT INTO GioHang (MaKH, MaNV, NgayTao, TrangThai, GhiChu) VALUES
('KH001', 'NV002', '2025-07-10 10:00:00', 0, N'Đang chọn hàng'),
('KH002', 'NV003', '2025-07-10 11:00:00', 0, N'Đang chọn hàng'),
('KH003', 'NV005', '2025-07-10 12:00:00', 1, N'Đã đặt hàng'),
('KH004', 'NV006', '2025-07-10 13:00:00', 2, N'Đã thanh toán'),
('KH005', 'NV001', '2025-07-10 14:00:00', 3, N'Đã hủy');

-- Thêm dữ liệu chi tiết giỏ hàng
INSERT INTO ChiTietGioHang (MaGH, MaSP, SoLuong, DonGiaHienTai) VALUES
(1, 'SP001', 2, 15000),
(2, 'SP002', 5, 18000),
(3, 'SP003', 1, 1200000),
(4, 'SP004', 2, 13000),
(5, 'SP005', 3, 10000);

-- Thêm dữ liệu thống kê báo cáo
INSERT INTO ThongKeBaoCao (MaCH, MaNV, LoaiBaoCao, TenBaoCao, ThoiGianTu, ThoiGianDen, SoTien, SoLuong, NgayBaoCao, NoiDung, TrangThai) VALUES
('CH001', 'NV001', N'DoanhThu', N'Báo cáo doanh thu Q1', '2025-07-01 00:00:00', '2025-07-31 23:59:59', 5000000, 1000, '2025-07-10 18:00:00', N'Báo cáo doanh thu Q1', 1),
('CH002', 'NV008', N'ChiPhi', N'Báo cáo chi phí Q3', '2025-07-01 00:00:00', '2025-07-31 23:59:59', 1500000, 500, '2025-07-10 18:30:00', N'Báo cáo chi phí Q3', 1);

-- ===================================
-- CẬP NHẬT DỮ LIỆU TỪ INSERDATA.SQL
-- ===================================

-- Cập nhật dữ liệu KhuyenMai từ inserdata.sql (đã có dữ liệu ở trên, bỏ qua để tránh duplicate)

-- Cập nhật dữ liệu LichLamViec từ inserdata.sql
INSERT INTO LichLamViec (MaNV, MaCa, NgayLam, MaNVQuanLy, TrangThai, NgayDuyet, GhiChu) VALUES
-- Ca cho cửa hàng CH001
('NV002', 1, '2025-07-12', 'NV001', 1, '2025-07-11 10:00:00', N'Ca sáng'),
('NV003', 2, '2025-07-12', 'NV001', 1, '2025-07-11 10:00:00', N'Ca chiều'),
('NV004', 3, '2025-07-12', 'NV001', 1, '2025-07-11 10:00:00', N'Ca tối'),
('NV005', 1, '2025-07-13', 'NV001', 1, '2025-07-12 10:00:00', N'Ca sáng'),
('NV006', 2, '2025-07-13', 'NV001', 1, '2025-07-12 10:00:00', N'Ca chiều'),
('NV007', 3, '2025-07-13', 'NV001', 1, '2025-07-12 10:00:00', N'Ca tối'),
('NV008', 1, '2025-07-14', 'NV001', 0, NULL, N'Đang chờ duyệt'),

-- Ca cho cửa hàng CH002
('NV009', 1, '2025-07-12', 'NV002', 1, '2025-07-11 10:00:00', N'Ca sáng'),
('NV010', 2, '2025-07-12', 'NV002', 1, '2025-07-11 10:00:00', N'Ca chiều'),
('NV011', 3, '2025-07-12', 'NV002', 1, '2025-07-11 10:00:00', N'Ca tối'),
('NV012', 1, '2025-07-13', 'NV002', 1, '2025-07-12 10:00:00', N'Ca sáng'),
('NV013', 2, '2025-07-13', 'NV002', 1, '2025-07-12 10:00:00', N'Ca chiều'),
('NV014', 3, '2025-07-13', 'NV002', 1, '2025-07-12 10:00:00', N'Ca tối');


-- ===================================
-- PROCEDURE INSERTPRODUCTIMAGES
-- ===================================

-- Chèn hình ảnh mẫu cho tất cả sản phẩm
DELIMITER $$
CREATE PROCEDURE InsertProductImages()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE product_id VARCHAR(10);
    DECLARE product_name VARCHAR(255);
    DECLARE product_cursor CURSOR FOR
        SELECT MaSP, TenSP FROM SanPham;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN product_cursor;
    read_loop: LOOP
        FETCH product_cursor INTO product_id, product_name;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- Chèn hình chính
        INSERT INTO HinhAnh (MaSP, URL, MoTa, LaChinh, ThuTuHienThi) 
        VALUES (product_id, CONCAT(product_id, '_main.jpg'),
                CONCAT('Hình chính ', product_name), TRUE, 1);
        
        -- Chèn hình góc nghiêng 1
        INSERT INTO HinhAnh (MaSP, URL, MoTa, LaChinh, ThuTuHienThi) 
        VALUES (product_id, CONCAT(product_id, '_main1.jpg'),
                CONCAT('Góc nghiêng 1 ', product_name), FALSE, 2);
        
        -- Chèn hình góc nghiêng 2
        INSERT INTO HinhAnh (MaSP, URL, MoTa, LaChinh, ThuTuHienThi) 
        VALUES (product_id, CONCAT(product_id, '_main2.jpg'), 
                CONCAT('Góc nghiêng 2 ', product_name), FALSE, 3);

    END LOOP;
    CLOSE product_cursor;
END$$
DELIMITER ;

-- Thực thi stored procedure để chèn dữ liệu hình ảnh
CALL InsertProductImages();

-- Xóa stored procedure sau khi sử dụng
-- DROP PROCEDURE InsertProductImages;

select * from NguoiDung;