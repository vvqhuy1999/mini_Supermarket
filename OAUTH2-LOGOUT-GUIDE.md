# 🚪 Hướng dẫn OAuth2 Logout

## 📋 **Tổng quan:**

Hướng dẫn cách implement logout cho OAuth2 Google với 3 loại logout khác nhau.

---

## 🔐 **3 Loại Logout:**

### **1️⃣ Local Logout (Đăng xuất local)**
- ✅ **Mục đích:** Chỉ xóa session/token trên app
- ✅ **Google session:** Vẫn đăng nhập
- ✅ **Lần đăng nhập sau:** Không cần nhập password Google

### **2️⃣ Google Logout (Đăng xuất Google)**
- ✅ **Mục đích:** Đăng xuất khỏi cả Google account
- ✅ **Google session:** Bị xóa hoàn toàn
- ✅ **Lần đăng nhập sau:** Cần nhập lại password Google

### **3️⃣ Clear Storage (Xóa dữ liệu)**
- ✅ **Mục đích:** Chỉ xóa localStorage (frontend only)
- ✅ **Backend:** Không biết user đã logout
- ✅ **Google session:** Vẫn tồn tại

---

## 🛠️ **Backend API Endpoints:**

### **🔒 Local Logout:**
```http
POST /api/oauth2/logout
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**Response:**
```json
{
  "success": true,
  "message": "Đăng xuất local thành công",
  "result": {
    "logout_type": "local",
    "success": true,
    "token_blacklisted": true,
    "message": "Token đã được blacklist"
  }
}
```

### **🌐 Google Logout:**
```http
POST /api/oauth2/logout/google
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**Response:**
```json
{
  "success": true,
  "message": "Hướng dẫn đăng xuất Google",
  "result": {
    "logout_type": "google",
    "success": true,
    "token_blacklisted": true,
    "message": "Để logout hoàn toàn khỏi Google, user cần truy cập: https://accounts.google.com/logout",
    "google_logout_url": "https://accounts.google.com/logout",
    "revoke_url": "https://myaccount.google.com/permissions"
  }
}
```

---

## 🌐 **Frontend Implementation:**

### **🔒 Local Logout Function:**
```javascript
async function localLogout() {
    const token = localStorage.getItem('jwt_token');
    
    try {
        const response = await fetch('/api/oauth2/logout', {
            method: 'POST',
            headers: {
                'Authorization': token ? `Bearer ${token}` : '',
                'Content-Type': 'application/json'
            }
        });
        
        const data = await response.json();
        
        if (response.ok) {
            // Clear localStorage
            localStorage.removeItem('jwt_token');
            localStorage.removeItem('user_email');
            localStorage.removeItem('user_role');
            localStorage.removeItem('user_id');
            
            alert('✅ Đăng xuất local thành công!');
            window.location.href = '/login';
        }
    } catch (error) {
        console.error('Logout error:', error);
    }
}
```

### **🌐 Google Logout Function:**
```javascript
async function googleLogout() {
    const token = localStorage.getItem('jwt_token');
    
    try {
        const response = await fetch('/api/oauth2/logout/google', {
            method: 'POST',
            headers: {
                'Authorization': token ? `Bearer ${token}` : '',
                'Content-Type': 'application/json'
            }
        });
        
        const data = await response.json();
        
        if (response.ok) {
            // Clear localStorage
            localStorage.removeItem('jwt_token');
            localStorage.removeItem('user_email');
            localStorage.removeItem('user_role');
            localStorage.removeItem('user_id');
            
            const confirmLogout = confirm(
                'Đăng xuất local thành công!\n\n' +
                'Bạn muốn logout khỏi Google không?'
            );
            
            if (confirmLogout) {
                // Open Google logout in new tab
                window.open(data.result.google_logout_url, '_blank');
            }
            
            window.location.href = '/login';
        }
    } catch (error) {
        console.error('Google logout error:', error);
    }
}
```

### **🗑️ Clear Storage Function:**
```javascript
function clearStorage() {
    // Clear all OAuth2 related data
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_email');
    localStorage.removeItem('user_role');
    localStorage.removeItem('user_id');
    
    alert('✅ Đã xóa tất cả dữ liệu localStorage!');
    window.location.href = '/login';
}
```

---

## 🎯 **Use Cases:**

### **🔒 Khi nào dùng Local Logout:**
- ✅ **Switch account:** User muốn đổi tài khoản khác
- ✅ **App logout:** Chỉ muốn logout khỏi app
- ✅ **Quick logout:** Logout nhanh, dễ login lại

### **🌐 Khi nào dùng Google Logout:**
- ✅ **Shared computer:** Máy tính dùng chung
- ✅ **Security logout:** Logout an toàn hoàn toàn
- ✅ **Public device:** Thiết bị công cộng

### **🗑️ Khi nào dùng Clear Storage:**
- ✅ **Debug/Testing:** Xóa dữ liệu để test
- ✅ **Privacy:** Xóa dữ liệu cá nhân
- ✅ **Reset app:** Reset trạng thái app

---

## 🧪 **Testing:**

### **✅ Test Scenarios:**

1. **Test Local Logout:**
   ```bash
   # Đăng nhập Google → Test local logout → Thử login lại
   # Expected: Không cần nhập password Google
   ```

2. **Test Google Logout:**
   ```bash
   # Đăng nhập Google → Test Google logout → Thử login lại
   # Expected: Cần nhập lại password Google
   ```

3. **Test Clear Storage:**
   ```bash
   # Có token trong localStorage → Clear storage → Check localStorage
   # Expected: localStorage trống
   ```

### **🌐 Manual Testing:**
1. **Truy cập:** `http://localhost:8080/oauth2-test.html`
2. **Đăng nhập Google**
3. **Test 3 loại logout** trong phần "🚪 Logout Actions"
4. **Verify kết quả** trong console và localStorage

---

## 🔧 **Advanced Features:**

### **🚫 Token Blacklisting (Optional):**
```java
// Trong OAuth2Controller.java
if (token != null) {
    tokenBlacklistService.blacklistToken(token);
    result.put("token_blacklisted", true);
}
```

### **📊 Logout Analytics (Optional):**
```javascript
// Track logout events
async function trackLogout(logoutType) {
    await fetch('/api/analytics/logout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            logout_type: logoutType,
            timestamp: new Date().toISOString()
        })
    });
}
```

### **⏰ Auto Logout (Optional):**
```javascript
// Auto logout when token expires
function checkTokenExpiration() {
    const token = localStorage.getItem('jwt_token');
    if (!token) return;
    
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const now = Math.floor(Date.now() / 1000);
        
        if (payload.exp < now) {
            console.log('Token expired, auto logout');
            localLogout();
        }
    } catch (error) {
        console.error('Token validation error:', error);
        localLogout();
    }
}

// Check every minute
setInterval(checkTokenExpiration, 60000);
```

---

## 🔗 **URLs Reference:**

### **Google URLs:**
- **Logout:** `https://accounts.google.com/logout`
- **Revoke Permissions:** `https://myaccount.google.com/permissions`
- **Account Settings:** `https://myaccount.google.com/`

### **App URLs:**
- **Login Page:** `/oauth2-test.html`
- **Success Page:** `/oauth2/success.html`
- **API Test:** `/api/oauth2/logout`

---

## 📱 **Different Frontend Frameworks:**

### **React/Vue/Angular:**
```javascript
// React Hook
const useLogout = () => {
    const logout = useCallback(async (type = 'local') => {
        if (type === 'google') {
            await googleLogout();
        } else {
            await localLogout();
        }
    }, []);
    
    return { logout };
};

// Vue Composable
export function useLogout() {
    const logout = async (type = 'local') => {
        if (type === 'google') {
            await googleLogout();
        } else {
            await localLogout();
        }
    };
    
    return { logout };
}
```

### **HTML/JavaScript:**
```html
<!-- Logout buttons -->
<button onclick="localLogout()">🔒 Logout Local</button>
<button onclick="googleLogout()">🌐 Logout Google</button>
<button onclick="clearStorage()">🗑️ Clear Data</button>
```

---

## 🎉 **Summary:**

### **✅ Đã implement:**
- ✅ **3 loại logout** với các use case khác nhau
- ✅ **Backend APIs** cho local và Google logout
- ✅ **Frontend functions** hoàn chỉnh
- ✅ **UI buttons** trong test pages
- ✅ **Error handling** và user feedback

### **🎯 User Experience:**
- ✅ **Flexible logout options** theo nhu cầu user
- ✅ **Clear feedback** về trạng thái logout
- ✅ **Smooth redirect** sau khi logout
- ✅ **Security options** cho different scenarios

**OAuth2 Logout system hoàn chỉnh và ready to use!** 🚀🔐
