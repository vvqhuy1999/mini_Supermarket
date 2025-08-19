
-- Bảng quản lý thông tin người dùng hệ thống
CREATE TABLE NguoiDung (
    MaNguoiDung VARCHAR(50) PRIMARY KEY,
    Email VARCHAR(50) UNIQUE NOT NULL,
    MatKhau VARCHAR(255) NOT NULL,
    Sub VARCHAR(255),
    VaiTro INT NOT NULL DEFAULT 3, -- 0=Quản trị, 1=Quản lý, 2=Nhân viên, 3=Khách hàng
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    IsDeleted BOOLEAN DEFAULT FALSE,
    
    -- Các cột cho chức năng OTP và Reset Password
    otp_code VARCHAR(6),
    otp_generated_time TIMESTAMP,
    otp_attempts INT DEFAULT 0,
    reset_password_token VARCHAR(255),
    reset_password_token_expiry TIMESTAMP,

    CONSTRAINT check_vaitro CHECK (VaiTro IN (0, 1, 2, 3))
);

-- Table to manage store information
CREATE TABLE cuahang (
    mach VARCHAR(50) PRIMARY KEY,
    tench VARCHAR(255) NOT NULL,
    diachi VARCHAR(255),
    sdt VARCHAR(15),
    ngaythanhlap DATE,
    trangthai INT DEFAULT 1, -- 0=Closed, 1=Active
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table to manage supplier information
CREATE TABLE nhacungcap (
    mancc VARCHAR(50) PRIMARY KEY,
    tenncc VARCHAR(255) NOT NULL,
    diachi VARCHAR(255),
    sdt VARCHAR(15),
    email VARCHAR(100),
    thongtinhopdong TEXT,
    ngayhoptac DATE,
    trangthai INT DEFAULT 1, -- 0=Inactive, 1=Active
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table to manage employee information
CREATE TABLE nhanvien (
    manv VARCHAR(50) PRIMARY KEY,
    manguoidung VARCHAR(50),
    hoten VARCHAR(255) NOT NULL,
    sdt VARCHAR(15),
    diachi VARCHAR(255),
    ngaysinh DATE,
    ngayvaolam DATE,
    chucvu VARCHAR(100),
    maquanly VARCHAR(50), -- Direct manager's employee ID
    mach VARCHAR(50), -- Store where the employee works
    trangthai INT DEFAULT 1, -- 0=Resigned, 1=Working
    isdeleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT chk_nhanvien_ngaysinh CHECK (ngaysinh < ngayvaolam)
);

-- Table to manage customer information and loyalty points
-- *** UPDATED: Removed 'email' column. ***
CREATE TABLE khachhang (
    makh VARCHAR(50) PRIMARY KEY,
    manguoidung VARCHAR(50),
    hoten VARCHAR(255) NOT NULL,
    sdt VARCHAR(15),
    diachi VARCHAR(255),
    ngaysinh DATE,
    diemtichluy INT DEFAULT 0, -- Loyalty points from purchases
    loaikhachhang VARCHAR(50) DEFAULT 'Thường', -- Regular, VIP, Silver, Gold, Diamond
    ngaydangky TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    isdeleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT chk_khachhang_diemtichluy CHECK (diemtichluy >= 0)
);

-- Table for product categories
CREATE TABLE loaisanpham (
    maloaisp VARCHAR(50) PRIMARY KEY,
    tenloai VARCHAR(255) NOT NULL,
    mota TEXT,
    maloaicha VARCHAR(50), -- For multi-level category tree
    thutuhienthi INT DEFAULT 0,
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table for detailed product information
-- *** UPDATED: Removed 'giaban' column. Price is now managed in 'giasanpham' table. ***
CREATE TABLE sanpham (
    masp VARCHAR(50) PRIMARY KEY,
    maloaisp VARCHAR(50) NOT NULL,
    tensp VARCHAR(255) NOT NULL,
    mota TEXT,
    donvitinh VARCHAR(50) DEFAULT 'Cái',
    trongluong DECIMAL(10,3), -- Product weight (kg)
    kichthuoc VARCHAR(100), -- Product dimensions
    hansudung INT, -- Shelf life in days
    trangthai INT DEFAULT 1, -- 0=Discontinued, 1=Available
    ngaytao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table for promotion programs
CREATE TABLE khuyenmai (
    makm VARCHAR(50) PRIMARY KEY,
    tenchuongtrinh VARCHAR(255) NOT NULL,
    mota TEXT,
    loaikm VARCHAR(50) NOT NULL, -- Percentage, Amount, Points, BuyXGetY
    giatrikm DECIMAL(15,2) NOT NULL, -- Promotion value (% or amount)
    dieukienapdung TEXT, -- Conditions for applying the promotion
    ngaybatdau TIMESTAMP NOT NULL,
    ngayketthuc TIMESTAMP NOT NULL,
    soluongtoida INT, -- Maximum number of applications
    dasudung INT DEFAULT 0, -- Number of times used
    maquanly VARCHAR(50), -- Manager in charge of the promotion
    trangthai INT DEFAULT 1, -- 0=Paused, 1=Active
    isdeleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT chk_khuyenmai_ngay CHECK (ngaybatdau < ngayketthuc),
    CONSTRAINT chk_khuyenmai_soluongtoida CHECK (soluongtoida IS NULL OR soluongtoida > 0),
    CONSTRAINT chk_khuyenmai_dasudung CHECK (dasudung >= 0)
);

-- Table for accepted payment methods
CREATE TABLE phuongthucthanhtoan (
    mapttt VARCHAR(50) PRIMARY KEY,
    tenpttt VARCHAR(100) NOT NULL,
    mota TEXT,
    phigiaodich DECIMAL(10,4) DEFAULT 0, -- Transaction fee (%)
    trangthai INT DEFAULT 1, -- 0=Inactive, 1=Active
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table for warehouse information
CREATE TABLE kho (
    makho SERIAL PRIMARY KEY,
    tenkho VARCHAR(255) NOT NULL,
    diachi VARCHAR(255),
    dientich DECIMAL(10,2), -- Warehouse area (m²)
    succhua DECIMAL(15,2), -- Maximum capacity
    mach VARCHAR(50), -- Store managing the warehouse
    trangthai INT DEFAULT 1, -- 0=Closed, 1=Active
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table to define work shifts
CREATE TABLE calamviec (
    maca SERIAL PRIMARY KEY,
    tenca VARCHAR(100) NOT NULL,
    giobatdau TIME NOT NULL, -- Shift start time
    gioketthuc TIME NOT NULL, -- Shift end time
    sogiolam DECIMAL(4,2) GENERATED ALWAYS AS (
        CAST(
            EXTRACT(EPOCH FROM (
                CASE
                    WHEN gioketthuc < giobatdau THEN gioketthuc::time + interval '1 day'
                    ELSE gioketthuc::time
                END - giobatdau::time
            )) / 3600
        AS DECIMAL(4,2))
    ) STORED, -- Calculated work hours
    trangthai INT DEFAULT 1, -- 0=Inactive, 1=Active
    isdeleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT chk_calamviec_gio CHECK (giobatdau != gioketthuc)
);

-- Bảng quản lý lịch làm việc của nhân viên
CREATE TABLE lichlamviec (
    malich SERIAL PRIMARY KEY,
    manv VARCHAR(50) NOT NULL,
    maca INT NOT NULL,
    ngaylam DATE NOT NULL,
    manvquanly VARCHAR(50),
    trangthai INT DEFAULT 0,
    ngayduyet TIMESTAMP,
    ghichu TEXT,
    giovao TIME,
    giora TIME,
    isdeleted BOOLEAN DEFAULT FALSE,
    UNIQUE (manv, ngaylam, maca)
);

-- Table for monthly employee payroll
CREATE TABLE bangluong (
    maluong SERIAL PRIMARY KEY,
    manv VARCHAR(50) NOT NULL,
    thangluong INT NOT NULL,
    namluong INT NOT NULL,
    luongcoban DECIMAL(15,2) NOT NULL,
    phucap DECIMAL(15,2) DEFAULT 0,
    thuong DECIMAL(15,2) DEFAULT 0,
    khautru DECIMAL(15,2) DEFAULT 0,
    tongluong DECIMAL(15,2) GENERATED ALWAYS AS (luongcoban + phucap + thuong - khautru) STORED,
    songaylam INT DEFAULT 0,
    sogiolam DECIMAL(8,2) DEFAULT 0,
    ghichu TEXT,
    trangthai INT DEFAULT 0,
    ngaytao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ngaythanhtoan TIMESTAMP NULL,
    nguoithanhtoan VARCHAR(50) NULL,
    isdeleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT check_thang_luong CHECK (thangluong >= 1 AND thangluong <= 12),
    CONSTRAINT check_nam_luong CHECK (namluong >= 2020),
    CONSTRAINT check_luong_co_ban CHECK (luongcoban >= 0),
    CONSTRAINT check_phu_cap CHECK (phucap >= 0),
    CONSTRAINT check_thuong CHECK (thuong >= 0),
    CONSTRAINT check_khau_tru CHECK (khautru >= 0),
    CONSTRAINT check_so_ngay_lam CHECK (songaylam >= 0),
    CONSTRAINT check_so_gio_lam CHECK (sogiolam >= 0),
    UNIQUE (manv, thangluong, namluong)
);

-- Table for product images
CREATE TABLE hinhanh (
    mahinh SERIAL PRIMARY KEY,
    masp VARCHAR(50) NOT NULL,
    url VARCHAR(500) NOT NULL,
    mota VARCHAR(255),
    lachinh BOOLEAN DEFAULT FALSE, -- Marks the main product image
    thutuhienthi INT DEFAULT 0, -- Display order
    ngaytao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table for product price history
CREATE TABLE giasanpham (
    magia SERIAL PRIMARY KEY,
    masp VARCHAR(50) NOT NULL,
    gia DECIMAL(15,2) NOT NULL,
    ngaybatdau DATE NOT NULL, -- Start date for the new price
    ngayketthuc DATE, -- End date for the price
    lydothaydoi VARCHAR(255),
    nguoithaydoi VARCHAR(50),
    isdeleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT chk_giasanpham_gia CHECK (gia > 0),
    CONSTRAINT chk_giasanpham_ngay CHECK (ngayketthuc IS NULL OR ngaybatdau <= ngayketthuc)
);

-- Table to track product inventory
CREATE TABLE tonkhochitiet (
    matkct SERIAL PRIMARY KEY,
    masp VARCHAR(50) NOT NULL,
    makho INT NOT NULL,
    soluongton INT DEFAULT 0, -- Current stock quantity
    soluongtoithieu INT DEFAULT 0, -- Minimum stock level
    soluongtoida INT, -- Maximum stock level
    ngaycapnhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    isdeleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT chk_tonkhochitiet_soluongton CHECK (soluongton >= 0)
);

-- Table for goods receipt notes from suppliers
CREATE TABLE phieunhaphang (
    mapn SERIAL PRIMARY KEY,
    mancc VARCHAR(50) NOT NULL,
    makho INT NOT NULL,
    manvlap VARCHAR(50) NOT NULL, -- Employee who created the note
    ngaynhap TIMESTAMP NOT NULL,
    tongtiennhap DECIMAL(15,2) DEFAULT 0,
    trangthai INT DEFAULT 0, -- 0=Pending, 1=Stocked, 2=Rejected, 3=Canceled
    ghichu TEXT,
    ngaytao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table for details of goods receipt notes
CREATE TABLE chitietphieunhap (
    mactpn SERIAL PRIMARY KEY,
    mapn INT NOT NULL,
    masp VARCHAR(50) NOT NULL,
    soluongnhap INT NOT NULL,
    dongianhap DECIMAL(15,2) NOT NULL,
    thanhtien DECIMAL(15,2) GENERATED ALWAYS AS (soluongnhap * dongianhap) STORED,
    ngayhethan DATE, -- Product expiration date
    solo VARCHAR(50), -- Production batch number
    ngaysanxuat DATE,
    isdeleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT chk_chitietphieunhap_soluongnhap CHECK (soluongnhap > 0),
    CONSTRAINT chk_chitietphieunhap_dongianhap CHECK (dongianhap > 0)
);

-- Table for goods issue notes
CREATE TABLE phieuxuatkho (
    mapxk SERIAL PRIMARY KEY,
    makho INT NOT NULL,
    manvlap VARCHAR(50) NOT NULL, -- Employee who created the note
    ngayxuat TIMESTAMP NOT NULL,
    tongsoluong INT DEFAULT 0,
    tonggiatri DECIMAL(15,2) DEFAULT 0,
    lydoxuat VARCHAR(255),
    trangthai INT DEFAULT 0, -- 0=Pending, 1=Issued, 2=Rejected, 3=Canceled
    ghichu TEXT,
    ngaytao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table for details of goods issue notes
CREATE TABLE chitietphieuxuat (
    mactpxk SERIAL PRIMARY KEY,
    mapxk INT NOT NULL,
    masp VARCHAR(50) NOT NULL,
    soluongxuat INT NOT NULL,
    dongiaxuat DECIMAL(15,2) NOT NULL,
    thanhtien DECIMAL(15,2) GENERATED ALWAYS AS (soluongxuat * dongiaxuat) STORED,
    isdeleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT chk_chitietphieuxuat_soluongxuat CHECK (soluongxuat > 0),
    CONSTRAINT chk_chitietphieuxuat_dongiaxuat CHECK (dongiaxuat > 0)
);

-- Orders Table
CREATE TABLE donhang (
    madh VARCHAR(50) PRIMARY KEY,
    makh VARCHAR(50) NOT NULL,
    manv VARCHAR(50),
    ngaydathang TIMESTAMP NOT NULL,
    ngaygiaohang TIMESTAMP,
    diachigiaohang VARCHAR(255) NOT NULL,
    trangthai VARCHAR(50) NOT NULL, -- e.g., Pending, Shipping, Completed, Canceled
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Order Details Table
CREATE TABLE chitietdonhang (
    macthd SERIAL PRIMARY KEY,
    madh VARCHAR(50) NOT NULL,
    masp VARCHAR(50) NOT NULL,
    soluong INT NOT NULL,
    dongia DECIMAL(15,2) NOT NULL,
    giamgia DECIMAL(5,2) DEFAULT 0,
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table for sales invoices
CREATE TABLE hoadon (
    mahd SERIAL PRIMARY KEY,
    makh VARCHAR(50),
    manvlap VARCHAR(50) NOT NULL, -- Employee who created the invoice
    makm VARCHAR(50), -- Applied promotion code
    ngaylap TIMESTAMP NOT NULL,
    tongtienhang DECIMAL(15,2) DEFAULT 0,
    tiengiamgia DECIMAL(15,2) DEFAULT 0,
    tongtien DECIMAL(15,2) GENERATED ALWAYS AS (tongtienhang - tiengiamgia) STORED,
    mapttt VARCHAR(50),
    trangthai INT DEFAULT 0, -- 0=Pending, 1=Paid, 2=Processing, 3=Canceled, 4=Returned
    diemtichluy INT DEFAULT 0, -- Points earned from this invoice
    ghichu TEXT,
    ngaytao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    nguoitao VARCHAR(50),
    ngaysua TIMESTAMP,
    nguoisua VARCHAR(50),
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table for invoice details
CREATE TABLE chitiethoadon (
    macthd SERIAL PRIMARY KEY,
    mahd INT NOT NULL,
    masp VARCHAR(50) NOT NULL,
    soluong INT NOT NULL,
    dongiaban DECIMAL(15,2) NOT NULL,
    thanhtien DECIMAL(15,2) GENERATED ALWAYS AS (soluong * dongiaban) STORED,
    giamgia DECIMAL(15,2) DEFAULT 0,
    thanhtiensaugiam DECIMAL(15,2) GENERATED ALWAYS AS ((soluong * dongiaban) - giamgia) STORED,
    isdeleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT chk_chitiethoadon_soluong CHECK (soluong > 0)
);

-- Table to apply promotions to products
CREATE TABLE khuyenmaisanpham (
    makmsp SERIAL PRIMARY KEY,
    makm VARCHAR(50) NOT NULL,
    masp VARCHAR(50) NOT NULL,
    ngaybatdau TIMESTAMP,
    ngayketthuc TIMESTAMP,
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table to apply promotions to customers
CREATE TABLE khuyenmaikhachhang (
    makmkh SERIAL PRIMARY KEY,
    makm VARCHAR(50) NOT NULL,
    makh VARCHAR(50) NOT NULL,
    ngayapdung TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dasudung BOOLEAN DEFAULT FALSE,
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table for payment transactions
CREATE TABLE thanhtoan (
    matt SERIAL PRIMARY KEY,
    mahd INT NOT NULL,
    mapttt VARCHAR(50) NOT NULL,
    sotienthanhtoan DECIMAL(15,2) NOT NULL,
    ngaygiott TIMESTAMP NOT NULL,
    trangthaitt INT DEFAULT 0, -- 0=Pending, 1=Success, 2=Failed, 3=Canceled, 4=Refunded
    magiaodichnganhang VARCHAR(100), -- Transaction ID from the bank
    ghichu TEXT,
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table for customer shopping carts
CREATE TABLE giohang (
    magh SERIAL PRIMARY KEY,
    makh VARCHAR(50),
    manv VARCHAR(50), -- Assisting employee (if any)
    ngaytao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ngaycapnhat TIMESTAMP,
    trangthai INT DEFAULT 0, -- 0=Shopping, 1=Ordered, 2=Paid, 3=Canceled
    ghichu TEXT,
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Table for shopping cart details
CREATE TABLE chitietgiohang (
    mactgh SERIAL PRIMARY KEY,
    magh INT NOT NULL,
    masp VARCHAR(50) NOT NULL,
    soluong INT NOT NULL,
    dongiahientai DECIMAL(15,2) NOT NULL, -- Price at the time of adding to cart
    thanhtien DECIMAL(15,2) GENERATED ALWAYS AS (soluong * dongiahientai) STORED,
    ngaythem TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    isdeleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT chk_chitietgiohang_soluong CHECK (soluong > 0),
    CONSTRAINT chk_chitietgiohang_dongia CHECK (dongiahientai > 0)
);

-- Table for statistics and reports
CREATE TABLE thongkebaocao (
    mabaocao SERIAL PRIMARY KEY,
    mach VARCHAR(50),
    manv VARCHAR(50) NOT NULL, -- Employee who created the report
    loaibaocao VARCHAR(100) NOT NULL, -- Report type: Revenue, Expense, Inventory, etc.
    tenbaocao VARCHAR(255) NOT NULL,
    thoigiantu TIMESTAMP,
    thoigianden TIMESTAMP,
    sotien DECIMAL(15,2),
    soluong INT,
    ngaybaocao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    noidung TEXT,
    filedinhkem VARCHAR(500),
    trangthai INT DEFAULT 1, -- 0=Draft, 1=Completed
    isdeleted BOOLEAN DEFAULT FALSE
);

-- ===================================
-- ADD UNIQUE CONSTRAINTS
-- ===================================

ALTER TABLE tonkhochitiet ADD CONSTRAINT uq_sanpham_kho UNIQUE (masp, makho);
ALTER TABLE khuyenmaisanpham ADD CONSTRAINT uq_khuyenmai_sanpham UNIQUE (makm, masp);
ALTER TABLE khuyenmaikhachhang ADD CONSTRAINT uq_khuyenmai_khachhang UNIQUE (makm, makh);
ALTER TABLE chitietgiohang ADD CONSTRAINT uq_giohang_sanpham UNIQUE (magh, masp);

-- ===================================
-- ADD FOREIGN KEYS
-- ===================================

-- Foreign key for loaisanpham (self-referencing)
ALTER TABLE loaisanpham ADD CONSTRAINT fk_loaisanpham_loaicha FOREIGN KEY (maloaicha) REFERENCES loaisanpham(maloaisp);

-- Foreign keys for nhanvien
ALTER TABLE nhanvien ADD CONSTRAINT fk_nhanvien_nguoidung FOREIGN KEY (manguoidung) REFERENCES nguoidung(manguoidung) ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE nhanvien ADD CONSTRAINT fk_nhanvien_quanly FOREIGN KEY (maquanly) REFERENCES nhanvien(manv);
ALTER TABLE nhanvien ADD CONSTRAINT fk_nhanvien_cuahang FOREIGN KEY (mach) REFERENCES cuahang(mach) ON DELETE SET NULL ON UPDATE CASCADE;

-- Foreign key for khachhang
ALTER TABLE khachhang ADD CONSTRAINT fk_khachhang_nguoidung FOREIGN KEY (manguoidung) REFERENCES nguoidung(manguoidung) ON DELETE SET NULL ON UPDATE CASCADE;

-- Foreign key for sanpham
ALTER TABLE sanpham ADD CONSTRAINT fk_sanpham_loaisanpham FOREIGN KEY (maloaisp) REFERENCES loaisanpham(maloaisp) ON DELETE NO ACTION ON UPDATE CASCADE;

-- Foreign key for khuyenmai
ALTER TABLE khuyenmai ADD CONSTRAINT fk_khuyenmai_quanly FOREIGN KEY (maquanly) REFERENCES nhanvien(manv) ON DELETE SET NULL ON UPDATE CASCADE;

-- Foreign key for kho
ALTER TABLE kho ADD CONSTRAINT fk_kho_cuahang FOREIGN KEY (mach) REFERENCES cuahang(mach) ON DELETE SET NULL ON UPDATE CASCADE;

-- Foreign keys for lichlamviec
ALTER TABLE lichlamviec ADD CONSTRAINT fk_lichlamviec_nhanvien FOREIGN KEY (manv) REFERENCES nhanvien(manv) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE lichlamviec ADD CONSTRAINT fk_lichlamviec_ca FOREIGN KEY (maca) REFERENCES calamviec(maca) ON DELETE NO ACTION ON UPDATE CASCADE;
ALTER TABLE lichlamviec ADD CONSTRAINT fk_lichlamviec_quanly FOREIGN KEY (manvquanly) REFERENCES nhanvien(manv) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- Foreign keys for bangluong
ALTER TABLE bangluong ADD CONSTRAINT fk_bangluong_nhanvien FOREIGN KEY (manv) REFERENCES nhanvien(manv) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE bangluong ADD CONSTRAINT fk_bangluong_nguoithanhtoan FOREIGN KEY (nguoithanhtoan) REFERENCES nhanvien(manv) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- Foreign key for hinhanh
ALTER TABLE hinhanh ADD CONSTRAINT fk_hinhanh_sanpham FOREIGN KEY (masp) REFERENCES sanpham(masp) ON DELETE CASCADE ON UPDATE CASCADE;

-- Foreign keys for giasanpham
ALTER TABLE giasanpham ADD CONSTRAINT fk_giasanpham_sanpham FOREIGN KEY (masp) REFERENCES sanpham(masp) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE giasanpham ADD CONSTRAINT fk_giasanpham_nguoithaydoi FOREIGN KEY (nguoithaydoi) REFERENCES nhanvien(manv) ON DELETE SET NULL ON UPDATE CASCADE;

-- Foreign keys for tonkhochitiet
ALTER TABLE tonkhochitiet ADD CONSTRAINT fk_tonkhochitiet_sanpham FOREIGN KEY (masp) REFERENCES sanpham(masp) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE tonkhochitiet ADD CONSTRAINT fk_tonkhochitiet_kho FOREIGN KEY (makho) REFERENCES kho(makho) ON DELETE CASCADE ON UPDATE CASCADE;

-- Foreign keys for phieunhaphang
ALTER TABLE phieunhaphang ADD CONSTRAINT fk_phieunhaphang_nhacungcap FOREIGN KEY (mancc) REFERENCES nhacungcap(mancc) ON DELETE NO ACTION ON UPDATE CASCADE;
ALTER TABLE phieunhaphang ADD CONSTRAINT fk_phieunhaphang_kho FOREIGN KEY (makho) REFERENCES kho(makho) ON DELETE NO ACTION ON UPDATE CASCADE;
ALTER TABLE phieunhaphang ADD CONSTRAINT fk_phieunhaphang_nhanvien FOREIGN KEY (manvlap) REFERENCES nhanvien(manv) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- Foreign keys for chitietphieunhap
ALTER TABLE chitietphieunhap ADD CONSTRAINT fk_chitietphieunhap_phieunhap FOREIGN KEY (mapn) REFERENCES phieunhaphang(mapn) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE chitietphieunhap ADD CONSTRAINT fk_chitietphieunhap_sanpham FOREIGN KEY (masp) REFERENCES sanpham(masp) ON DELETE NO ACTION ON UPDATE CASCADE;

-- Foreign keys for phieuxuatkho
ALTER TABLE phieuxuatkho ADD CONSTRAINT fk_phieuxuatkho_kho FOREIGN KEY (makho) REFERENCES kho(makho) ON DELETE NO ACTION ON UPDATE CASCADE;
ALTER TABLE phieuxuatkho ADD CONSTRAINT fk_phieuxuatkho_nhanvien FOREIGN KEY (manvlap) REFERENCES nhanvien(manv) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- Foreign keys for chitietphieuxuat
ALTER TABLE chitietphieuxuat ADD CONSTRAINT fk_chitietphieuxuat_phieuxuat FOREIGN KEY (mapxk) REFERENCES phieuxuatkho(mapxk) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE chitietphieuxuat ADD CONSTRAINT fk_chitietphieuxuat_sanpham FOREIGN KEY (masp) REFERENCES sanpham(masp) ON DELETE NO ACTION ON UPDATE CASCADE;

-- Foreign keys for donhang
ALTER TABLE donhang ADD CONSTRAINT fk_donhang_khachhang FOREIGN KEY (makh) REFERENCES khachhang(makh) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE donhang ADD CONSTRAINT fk_donhang_nhanvien FOREIGN KEY (manv) REFERENCES nhanvien(manv) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- Foreign keys for chitietdonhang
ALTER TABLE chitietdonhang ADD CONSTRAINT fk_chitietdonhang_donhang FOREIGN KEY (madh) REFERENCES donhang(madh) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE chitietdonhang ADD CONSTRAINT fk_chitietdonhang_sanpham FOREIGN KEY (masp) REFERENCES sanpham(masp) ON DELETE NO ACTION ON UPDATE CASCADE;

-- Foreign keys for hoadon
ALTER TABLE hoadon ADD CONSTRAINT fk_hoadon_khachhang FOREIGN KEY (makh) REFERENCES khachhang(makh) ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE hoadon ADD CONSTRAINT fk_hoadon_nhanvien FOREIGN KEY (manvlap) REFERENCES nhanvien(manv) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE hoadon ADD CONSTRAINT fk_hoadon_khuyenmai FOREIGN KEY (makm) REFERENCES khuyenmai(makm) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE hoadon ADD CONSTRAINT fk_hoadon_phuongthucthanhtoan FOREIGN KEY (mapttt) REFERENCES phuongthucthanhtoan(mapttt) ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE hoadon ADD CONSTRAINT fk_hoadon_nguoitao FOREIGN KEY (nguoitao) REFERENCES nhanvien(manv) ON DELETE SET NULL ON UPDATE NO ACTION;
ALTER TABLE hoadon ADD CONSTRAINT fk_hoadon_nguoisua FOREIGN KEY (nguoisua) REFERENCES nhanvien(manv) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- Foreign keys for chitiethoadon
ALTER TABLE chitiethoadon ADD CONSTRAINT fk_chitiethoadon_hoadon FOREIGN KEY (mahd) REFERENCES hoadon(mahd) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE chitiethoadon ADD CONSTRAINT fk_chitiethoadon_sanpham FOREIGN KEY (masp) REFERENCES sanpham(masp) ON DELETE NO ACTION ON UPDATE CASCADE;

-- Foreign keys for khuyenmaisanpham
ALTER TABLE khuyenmaisanpham ADD CONSTRAINT fk_khuyenmaisanpham_khuyenmai FOREIGN KEY (makm) REFERENCES khuyenmai(makm) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE khuyenmaisanpham ADD CONSTRAINT fk_khuyenmaisanpham_sanpham FOREIGN KEY (masp) REFERENCES sanpham(masp) ON DELETE CASCADE ON UPDATE CASCADE;

-- Foreign keys for khuyenmaikhachhang
ALTER TABLE khuyenmaikhachhang ADD CONSTRAINT fk_khuyenmaikhachhang_khuyenmai FOREIGN KEY (makm) REFERENCES khuyenmai(makm) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE khuyenmaikhachhang ADD CONSTRAINT fk_khuyenmaikhachhang_khachhang FOREIGN KEY (makh) REFERENCES khachhang(makh) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- Foreign keys for thanhtoan
ALTER TABLE thanhtoan ADD CONSTRAINT fk_thanhtoan_hoadon FOREIGN KEY (mahd) REFERENCES hoadon(mahd) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE thanhtoan ADD CONSTRAINT fk_thanhtoan_phuongthucthanhtoan FOREIGN KEY (mapttt) REFERENCES phuongthucthanhtoan(mapttt) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- Foreign keys for giohang
ALTER TABLE giohang ADD CONSTRAINT fk_giohang_khachhang FOREIGN KEY (makh) REFERENCES khachhang(makh) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE giohang ADD CONSTRAINT fk_giohang_nhanvien FOREIGN KEY (manv) REFERENCES nhanvien(manv) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- Foreign keys for chitietgiohang
ALTER TABLE chitietgiohang ADD CONSTRAINT fk_chitietgiohang_giohang FOREIGN KEY (magh) REFERENCES giohang(magh) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE chitietgiohang ADD CONSTRAINT fk_chitietgiohang_sanpham FOREIGN KEY (masp) REFERENCES sanpham(masp) ON DELETE CASCADE ON UPDATE CASCADE;

-- Foreign keys for thongkebaocao
ALTER TABLE thongkebaocao ADD CONSTRAINT fk_thongkebaocao_cuahang FOREIGN KEY (mach) REFERENCES cuahang(mach) ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE thongkebaocao ADD CONSTRAINT fk_thongkebaocao_nhanvien FOREIGN KEY (manv) REFERENCES nhanvien(manv) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ===================================
-- CREATE INDEXES
-- ===================================

CREATE INDEX idx_nguoidung_email ON nguoidung(email);
CREATE INDEX idx_nguoidung_vaitro ON nguoidung(vaitro);
CREATE INDEX idx_cuahang_trangthai ON cuahang(trangthai);
CREATE INDEX idx_nhacungcap_trangthai ON nhacungcap(trangthai);
CREATE INDEX idx_nhanvien_cuahang ON nhanvien(mach);
CREATE INDEX idx_nhanvien_trangthai ON nhanvien(trangthai);
CREATE INDEX idx_khachhang_sdt ON khachhang(sdt);
-- *** UPDATED: Removed index for 'email' column. ***
CREATE INDEX idx_khachhang_loai ON khachhang(loaikhachhang);
CREATE INDEX idx_loaisanpham_cha ON loaisanpham(maloaicha);
CREATE INDEX idx_sanpham_loai ON sanpham(maloaisp);
CREATE INDEX idx_sanpham_trangthai ON sanpham(trangthai);
CREATE INDEX idx_khuyenmai_ngay ON khuyenmai(ngaybatdau, ngayketthuc);
CREATE INDEX idx_khuyenmai_trangthai ON khuyenmai(trangthai);
CREATE INDEX idx_phuongthucthanhtoan_trangthai ON phuongthucthanhtoan(trangthai);
CREATE INDEX idx_kho_cuahang ON kho(mach);
CREATE INDEX idx_kho_trangthai ON kho(trangthai);
CREATE INDEX idx_calamviec_trangthai ON calamviec(trangthai);
CREATE INDEX idx_lichlamviec_ngay ON lichlamviec(ngaylam);
CREATE INDEX idx_lichlamviec_trangthai ON lichlamviec(trangthai);
CREATE INDEX idx_bangluong_thangnam ON bangluong(thangluong, namluong);
CREATE INDEX idx_bangluong_trangthai ON bangluong(trangthai);
CREATE INDEX idx_hinhanh_sanpham ON hinhanh(masp);
CREATE INDEX idx_hinhanh_chinh ON hinhanh(lachinh);
CREATE INDEX idx_giasanpham_sanpham ON giasanpham(masp);
CREATE INDEX idx_giasanpham_ngay ON giasanpham(ngaybatdau, ngayketthuc);
CREATE INDEX idx_tonkho_sanpham ON tonkhochitiet(masp);
CREATE INDEX idx_tonkho_kho ON tonkhochitiet(makho);
CREATE INDEX idx_phieunhap_ngay ON phieunhaphang(ngaynhap);
CREATE INDEX idx_phieunhap_trangthai ON phieunhaphang(trangthai);
CREATE INDEX idx_phieunhap_nhacungcap ON phieunhaphang(mancc);
CREATE INDEX idx_chitietphieunhap_phieu ON chitietphieunhap(mapn);
CREATE INDEX idx_chitietphieunhap_sanpham ON chitietphieunhap(masp);
CREATE INDEX idx_phieuxuat_ngay ON phieuxuatkho(ngayxuat);
CREATE INDEX idx_phieuxuat_trangthai ON phieuxuatkho(trangthai);
CREATE INDEX idx_phieuxuat_kho ON phieuxuatkho(makho);
CREATE INDEX idx_chitietphieuxuat_phieu ON chitietphieuxuat(mapxk);
CREATE INDEX idx_chitietphieuxuat_sanpham ON chitietphieuxuat(masp);
CREATE INDEX idx_hoadon_ngaylap ON hoadon(ngaylap);
CREATE INDEX idx_hoadon_trangthai ON hoadon(trangthai);
CREATE INDEX idx_hoadon_khachhang ON hoadon(makh);
CREATE INDEX idx_hoadon_nhanvien ON hoadon(manvlap);
CREATE INDEX idx_chitiethoadon_hoadon ON chitiethoadon(mahd);
CREATE INDEX idx_chitiethoadon_sanpham ON chitiethoadon(masp);
CREATE INDEX idx_khuyenmaisanpham_km ON khuyenmaisanpham(makm);
CREATE INDEX idx_khuyenmaisanpham_sp ON khuyenmaisanpham(masp);
CREATE INDEX idx_khuyenmaikhachhang_km ON khuyenmaikhachhang(makm);
CREATE INDEX idx_khuyenmaikhachhang_kh ON khuyenmaikhachhang(makh);
CREATE INDEX idx_thanhtoan_hoadon ON thanhtoan(mahd);
CREATE INDEX idx_thanhtoan_trangthai ON thanhtoan(trangthaitt);
CREATE INDEX idx_thanhtoan_ngay ON thanhtoan(ngaygiott);
CREATE INDEX idx_giohang_khachhang ON giohang(makh);
CREATE INDEX idx_giohang_trangthai ON giohang(trangthai);
CREATE INDEX idx_chitietgiohang_giohang ON chitietgiohang(magh);
CREATE INDEX idx_chitietgiohang_sanpham ON chitietgiohang(masp);
CREATE INDEX idx_thongke_loai ON thongkebaocao(loaibaocao);
CREATE INDEX idx_thongke_ngay ON thongkebaocao(ngaybaocao);
CREATE INDEX idx_thongke_cuahang ON thongkebaocao(mach);
