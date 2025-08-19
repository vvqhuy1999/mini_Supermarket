# 🛒 Cart API - Quick Reference

## 🔗 Base URL
```
http://localhost:8080
```

## 🔐 Authentication
```javascript
// Headers cho tất cả authenticated requests
headers: {
  'Authorization': 'Bearer ' + localStorage.getItem('jwt-token'),
  'Content-Type': 'application/json'
}
```

---

## 📋 API Endpoints

### 🔍 **Debug & Info**
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/giohang/current-user` | Kiểm tra user hiện tại | ✅ |

### 🛒 **Cart Management**
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/giohang/by-khachhang/{maKH}` | Lấy tất cả giỏ hàng của KH | ✅ |
| `GET` | `/api/giohang/by-khachhang/{maKH}/with-items` | Lấy giỏ đang hoạt động + items | ✅ |
| `GET` | `/api/giohang/{maGH}/with-items` | Lấy giỏ hàng theo ID + items | ✅ |
| `POST` | `/api/giohang/sync` | **Sync localStorage → DB** | ✅ |
| `PUT` | `/api/giohang/{maGH}/status?value={0-3}` | Cập nhật trạng thái giỏ | ✅ |
| `DELETE` | `/api/giohang/{maGH}/items` | Xóa tất cả items | ✅ |

### 🛍️ **Cart Items**
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/chitietgiohang/by-giohang/{maGH}` | Lấy items theo giỏ hàng | ✅ |
| `POST` | `/api/chitietgiohang` | Thêm sản phẩm vào giỏ | ✅ |
| `PUT` | `/api/chitietgiohang/{maCTGH}/quantity?value={n}` | Cập nhật số lượng | ✅ |
| `DELETE` | `/api/chitietgiohang/{maCTGH}` | Xóa item khỏi giỏ | ✅ |

---

## 🎯 Key Request/Response Examples

### 1. **Cart Sync (Quan trọng nhất)**
```javascript
POST /api/giohang/sync
{
  "items": [
    { "maSP": "SP001", "soLuong": 2 },
    { "maSP": "SP002", "soLuong": 1 }
  ]
}

// Response: GioHangWithItemsDto với items đã merge
```

### 2. **Get Active Cart with Items**
```javascript
GET /api/giohang/by-khachhang/KH001/with-items

// Response:
{
  "maGH": 123,
  "maKH": "KH001",
  "trangThai": 0,
  "ngayTao": "2024-01-01T10:00:00.000+00:00",
  "items": [
    {
      "maCTGH": 1,
      "soLuong": 5,
      "donGiaHienTai": 18000,
      "thanhTien": 90000,
      "sanPham": { "maSP": "SP001", "tenSP": "Dưa leo" }
    }
  ],
  "tongTien": 90000
}
```

### 3. **Add Item to Cart**
```javascript
POST /api/chitietgiohang
{
  "gioHang": { "maGH": 123 },
  "sanPham": { "maSP": "SP001" },
  "soLuong": 2,
  "donGiaHienTai": 18000
}
```

---

## ⚡ Frontend Integration Steps

### **1. Login → Sync Flow**
```javascript
// 1. Login
const token = await login(credentials);

// 2. Sync localStorage
await fetch('/api/giohang/sync', {
  method: 'POST',
  headers: { 'Authorization': `Bearer ${token}` },
  body: JSON.stringify({ items: localStorageCart })
});

// 3. Clear localStorage
localStorage.removeItem('easymart-cart');
```

### **2. Get Current Cart**
```javascript
const userInfo = await fetch('/api/giohang/current-user', {
  headers: { 'Authorization': `Bearer ${token}` }
}).then(r => r.json());

const cart = await fetch(`/api/giohang/by-khachhang/${userInfo.maKH}/with-items`, {
  headers: { 'Authorization': `Bearer ${token}` }
}).then(r => r.json());
```

### **3. Logout → Save to localStorage**
```javascript
// 1. Get current cart
const cart = await getCurrentCart();

// 2. Save to localStorage
localStorage.setItem('easymart-cart', JSON.stringify(
  cart.items.map(item => ({
    maSP: item.sanPham.maSP,
    soLuong: item.soLuong
  }))
));

// 3. Clear token
localStorage.removeItem('jwt-token');
```

---

## 🔧 Cart Status Values
| Value | Status | Description |
|-------|--------|-------------|
| `0` | Đang chọn hàng | Shopping cart (active) |
| `1` | Đã thanh toán | Paid |
| `2` | Đang giao hàng | Delivering |
| `3` | Hoàn thành | Completed |

---

## 🚨 Error Codes
| Code | Meaning | Action |
|------|---------|--------|
| `401` | Unauthorized | Redirect to login |
| `403` | Forbidden | Security violation |
| `404` | Not Found | Resource doesn't exist |
| `400` | Bad Request | Invalid data |

---

## 💡 Tips
- ✅ **Merge Logic**: API tự động cộng dồn số lượng khi sync
- ✅ **Security**: User chỉ có thể truy cập giỏ hàng của chính mình
- ✅ **Auto-populate**: Timestamps và giá hiện tại tự động
- ✅ **Filter**: Chỉ hiển thị sản phẩm có `trangThai = 0`
