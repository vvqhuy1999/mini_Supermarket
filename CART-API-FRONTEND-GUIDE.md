# 🛒 API Giỏ Hàng - Hướng Dẫn Frontend

## 📋 Tổng Quan
API giỏ hàng hỗ trợ đầy đủ các tính năng:
- ✅ **Đăng nhập thường** và **Google OAuth2**
- ✅ **LocalStorage sync** tự động khi đăng nhập
- ✅ **Merge logic** thông minh (cộng dồn số lượng)
- ✅ **Security validation** (chỉ sync giỏ hàng của chính mình)
- ✅ **Auto-populate timestamps**

---

## 🔐 Authentication

### 1. Đăng nhập thường
```javascript
// Login và lấy JWT token
const loginResponse = await fetch('/api/auth/authenticate', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'customer@gmail.com',
    password: '123456'
  })
});

const { data } = await loginResponse.json();
const jwtToken = data.token;

// Lưu token vào localStorage
localStorage.setItem('jwt-token', jwtToken);
```

### 2. Google OAuth2
```javascript
// Redirect đến Google login
window.location.href = '/oauth2/authorization/google';

// Sau khi callback success, JWT token sẽ có trong URL parameters
// Frontend xử lý và lưu token vào localStorage
```

### 3. Tạo Authorization Headers
```javascript
const getAuthHeaders = () => {
  const token = localStorage.getItem('jwt-token');
  return token ? { 'Authorization': `Bearer ${token}` } : {};
};
```

---

## 🛒 Cart APIs

### 1. **Kiểm tra thông tin user hiện tại**
```javascript
// GET /api/giohang/current-user
const getCurrentUser = async () => {
  const response = await fetch('/api/giohang/current-user', {
    headers: getAuthHeaders()
  });
  return await response.json();
};

// Response:
// {
//   "authenticated": true,
//   "email": "customer@gmail.com",
//   "maNguoiDung": "ND001",
//   "maKH": "KH001",
//   "authorities": "[ROLE_CUSTOMER]"
// }
```

### 2. **Đồng bộ giỏ hàng từ localStorage (QUAN TRỌNG NHẤT)**
```javascript
// POST /api/giohang/sync
const syncCartFromLocalStorage = async () => {
  // Lấy cart từ localStorage
  const localCart = JSON.parse(localStorage.getItem('easymart-cart') || '[]');
  
  if (localCart.length === 0) return null;
  
  // Convert sang format API
  const syncData = {
    // maKH không cần thiết - API tự động lấy từ JWT token
    items: localCart.map(item => ({
      maSP: item.maSP,
      soLuong: item.soLuong
    }))
  };
  
  const response = await fetch('/api/giohang/sync', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...getAuthHeaders()
    },
    body: JSON.stringify(syncData)
  });
  
  const result = await response.json();
  
  // Xóa localStorage sau khi sync thành công
  if (response.ok) {
    localStorage.removeItem('easymart-cart');
    console.log('Cart synced successfully:', result);
  }
  
  return result;
};
```

### 3. **Lấy giỏ hàng của khách hàng**
```javascript
// GET /api/giohang/by-khachhang/{maKH}
const getCartsByCustomer = async (maKH) => {
  const response = await fetch(`/api/giohang/by-khachhang/${maKH}`, {
    headers: getAuthHeaders()
  });
  return await response.json();
};
```

### 4. **Lấy giỏ hàng đang hoạt động với items**
```javascript
// GET /api/giohang/by-khachhang/{maKH}/with-items
const getActiveCartWithItems = async (maKH) => {
  const response = await fetch(`/api/giohang/by-khachhang/${maKH}/with-items`, {
    headers: getAuthHeaders()
  });
  return await response.json();
};

// Response format:
// {
//   "maGH": 123,
//   "maKH": "KH001", 
//   "trangThai": 0,
//   "ghiChu": null,
//   "ngayTao": "2024-01-01T10:00:00.000+00:00",
//   "ngayCapNhat": "2024-01-01T11:00:00.000+00:00",
//   "items": [
//     {
//       "maCTGH": 1,
//       "soLuong": 5,
//       "donGiaHienTai": 18000,
//       "thanhTien": 90000,
//       "sanPham": { "maSP": "SP001", "tenSP": "Dưa leo", ... }
//     }
//   ],
//   "tongTien": 90000
// }
```

### 5. **Cập nhật trạng thái giỏ hàng**
```javascript
// PUT /api/giohang/{maGH}/status?value={0|1|2|3}
const updateCartStatus = async (maGH, status) => {
  const response = await fetch(`/api/giohang/${maGH}/status?value=${status}`, {
    method: 'PUT',
    headers: getAuthHeaders()
  });
  
  // Nếu status = 0 (shopping), API sẽ redirect về frontend
  return await response.json();
};

// Trạng thái:
// 0: Đang chọn hàng (shopping)
// 1: Đã thanh toán 
// 2: Đang giao hàng
// 3: Hoàn thành
```

### 6. **Xóa tất cả items trong giỏ**
```javascript
// DELETE /api/giohang/{maGH}/items
const clearCartItems = async (maGH) => {
  const response = await fetch(`/api/giohang/${maGH}/items`, {
    method: 'DELETE',
    headers: getAuthHeaders()
  });
  return response.ok;
};
```

---

## 🛍️ Cart Items APIs

### 1. **Lấy items theo giỏ hàng**
```javascript
// GET /api/chitietgiohang/by-giohang/{maGH}
const getCartItems = async (maGH) => {
  const response = await fetch(`/api/chitietgiohang/by-giohang/${maGH}`, {
    headers: getAuthHeaders()
  });
  return await response.json();
};
```

### 2. **Thêm sản phẩm vào giỏ**
```javascript
// POST /api/chitietgiohang
const addToCart = async (cartItemData) => {
  const response = await fetch('/api/chitietgiohang', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...getAuthHeaders()
    },
    body: JSON.stringify(cartItemData)
  });
  return await response.json();
};

// cartItemData format:
// {
//   "gioHang": { "maGH": 123 },
//   "sanPham": { "maSP": "SP001" },
//   "soLuong": 2,
//   "donGiaHienTai": 18000
// }
```

### 3. **Cập nhật số lượng**
```javascript
// PUT /api/chitietgiohang/{maCTGH}/quantity?value={n}
const updateItemQuantity = async (maCTGH, newQuantity) => {
  const response = await fetch(`/api/chitietgiohang/${maCTGH}/quantity?value=${newQuantity}`, {
    method: 'PUT',
    headers: getAuthHeaders()
  });
  return await response.json();
};
```

### 4. **Xóa item khỏi giỏ**
```javascript
// DELETE /api/chitietgiohang/{maCTGH}
const removeFromCart = async (maCTGH) => {
  const response = await fetch(`/api/chitietgiohang/${maCTGH}`, {
    method: 'DELETE',
    headers: getAuthHeaders()
  });
  return response.ok;
};
```

---

## 🎯 Frontend Integration Workflow

### 1. **Khi user đăng nhập**
```javascript
const handleLogin = async (credentials) => {
  try {
    // 1. Đăng nhập và lấy token
    const loginResponse = await login(credentials);
    const token = loginResponse.data.token;
    localStorage.setItem('jwt-token', token);
    
    // 2. Kiểm tra thông tin user
    const userInfo = await getCurrentUser();
    console.log('User info:', userInfo);
    
    // 3. Sync cart từ localStorage
    const syncResult = await syncCartFromLocalStorage();
    if (syncResult) {
      console.log('Cart synced:', syncResult);
      // Cập nhật UI với giỏ hàng đã sync
      updateCartUI(syncResult);
    }
    
    // 4. Lấy giỏ hàng hiện tại từ DB
    const activeCart = await getActiveCartWithItems(userInfo.maKH);
    if (activeCart) {
      updateCartUI(activeCart);
    }
    
  } catch (error) {
    console.error('Login error:', error);
  }
};
```

### 2. **Khi user thêm sản phẩm (chưa đăng nhập)**
```javascript
const addProductToCart = (product, quantity) => {
  // Lưu vào localStorage
  const localCart = JSON.parse(localStorage.getItem('easymart-cart') || '[]');
  
  const existingItem = localCart.find(item => item.maSP === product.maSP);
  if (existingItem) {
    existingItem.soLuong += quantity;
  } else {
    localCart.push({
      maSP: product.maSP,
      tenSP: product.tenSP,
      soLuong: quantity,
      donGia: product.giaHienTai
    });
  }
  
  localStorage.setItem('easymart-cart', JSON.stringify(localCart));
  updateCartUI(localCart);
};
```

### 3. **Khi user thêm sản phẩm (đã đăng nhập)**
```javascript
const addProductToCartAuthenticated = async (product, quantity, maGH) => {
  try {
    const cartItemData = {
      gioHang: { maGH: maGH },
      sanPham: { maSP: product.maSP },
      soLuong: quantity,
      donGiaHienTai: product.giaHienTai
    };
    
    const result = await addToCart(cartItemData);
    console.log('Added to cart:', result);
    
    // Refresh cart UI
    const updatedCart = await getActiveCartWithItems();
    updateCartUI(updatedCart);
    
  } catch (error) {
    console.error('Add to cart error:', error);
  }
};
```

### 4. **Khi user logout**
```javascript
const handleLogout = async () => {
  try {
    // 1. Lấy giỏ hàng hiện tại từ DB
    const userInfo = await getCurrentUser();
    if (userInfo.authenticated) {
      const activeCart = await getActiveCartWithItems(userInfo.maKH);
      
      // 2. Lưu vào localStorage
      if (activeCart && activeCart.items) {
        const localCartData = activeCart.items.map(item => ({
          maSP: item.sanPham.maSP,
          tenSP: item.sanPham.tenSP,
          soLuong: item.soLuong,
          donGia: item.donGiaHienTai
        }));
        localStorage.setItem('easymart-cart', JSON.stringify(localCartData));
      }
    }
    
    // 3. Xóa JWT token
    localStorage.removeItem('jwt-token');
    
    // 4. Redirect hoặc refresh UI
    window.location.href = '/';
    
  } catch (error) {
    console.error('Logout error:', error);
    localStorage.removeItem('jwt-token');
  }
};
```

---

## 🔍 Error Handling

```javascript
const handleApiError = (error, response) => {
  if (response?.status === 401) {
    // Token expired hoặc invalid
    localStorage.removeItem('jwt-token');
    window.location.href = '/login';
  } else if (response?.status === 403) {
    // Security violation
    console.error('Access denied:', error);
    alert('Bạn không có quyền thực hiện thao tác này');
  } else if (response?.status === 404) {
    // Resource not found
    console.error('Resource not found:', error);
  } else {
    // Other errors
    console.error('API Error:', error);
  }
};
```

---

## 📝 Notes

### ⚠️ **Quan trọng:**
1. **Authentication headers** bắt buộc cho tất cả cart APIs
2. **localStorage key** phải là `easymart-cart`
3. **Sync cart** ngay sau khi đăng nhập
4. **Clear localStorage** sau khi sync thành công
5. **Merge logic**: API tự động cộng dồn số lượng nếu sản phẩm đã có

### 🎯 **Best Practices:**
- Luôn kiểm tra `response.ok` trước khi xử lý data
- Implement proper error handling và loading states
- Cache cart data để giảm API calls
- Update UI ngay lập tức sau mỗi thao tác thành công

### 🐛 **Debug:**
- Sử dụng `GET /api/giohang/current-user` để debug authentication
- Kiểm tra console logs trong server để xem chi tiết sync process
- Verify JWT token format và expiration
