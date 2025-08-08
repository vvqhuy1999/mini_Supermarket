# Hướng dẫn Debug Đăng nhập Facebook OAuth2

## 🎯 Mục tiêu
Khắc phục lỗi "ko đăng nhập hoặc đăng ký bằng facebook" trong ứng dụng Mini Supermarket.

## 📋 Bước 1: Kiểm tra ứng dụng

### 1.1 Chạy ứng dụng
```powershell
.\mvnw spring-boot:run
```

### 1.2 Kiểm tra trạng thái
```powershell
.\debug-facebook-oauth2.ps1
```

### 1.3 Truy cập trang test
Mở trình duyệt và truy cập: `http://localhost:8080/facebook-test.html`

## 📋 Bước 2: Test cấu hình

### 2.1 Click "Test OAuth2 Config"
Kiểm tra xem cấu hình có đúng không:
- Google configured: true
- Facebook configured: true
- Redirect URIs đúng

### 2.2 Click "Test Facebook Debug"
Kiểm tra endpoint debug Facebook

## 📋 Bước 3: Thử đăng nhập Facebook

### 3.1 Click "📘 Đăng nhập với Facebook"
Theo dõi quá trình:
1. Chuyển hướng đến Facebook
2. Nhập thông tin đăng nhập
3. Chuyển hướng về ứng dụng

### 3.2 Theo dõi logs
Mở Developer Tools (F12):
- **Console tab**: Xem lỗi JavaScript
- **Network tab**: Xem requests/responses
- **Application tab**: Xem cookies/session

## 📋 Bước 4: Phân tích lỗi

### 4.1 Lỗi thường gặp

#### ❌ "Invalid redirect_uri parameter"
**Nguyên nhân**: Facebook App chưa cấu hình đúng redirect URI
**Khắc phục**:
1. Vào Facebook Developers Console
2. Chọn App của bạn
3. Settings > Basic
4. Thêm vào "Valid OAuth Redirect URIs":
   ```
   http://localhost:8080/login/oauth2/code/facebook
   ```

#### ❌ "App not configured for this domain"
**Nguyên nhân**: App Domains chưa có localhost
**Khắc phục**:
1. Facebook Developers Console
2. Settings > Basic
3. App Domains: thêm `localhost`

#### ❌ "User cancelled"
**Nguyên nhân**: Người dùng hủy đăng nhập
**Khắc phục**: Thử lại

#### ❌ "Application error"
**Nguyên nhân**: Lỗi từ ứng dụng
**Khắc phục**: Kiểm tra logs ứng dụng

### 4.2 Kiểm tra Facebook App Settings

#### Cài đặt cơ bản:
- **App Domains**: `localhost`
- **Valid OAuth Redirect URIs**: `http://localhost:8080/login/oauth2/code/facebook`
- **App Status**: Development hoặc Live

#### Cài đặt OAuth2:
- **Client OAuth Login**: Enabled
- **Web OAuth Login**: Enabled
- **Enforce HTTPS**: Disabled (cho localhost)
- **Use Strict Mode for Redirect URIs**: Disabled

## 📋 Bước 5: Debug chi tiết

### 5.1 Monitor logs
```powershell
.\monitor-facebook-login.ps1
```

### 5.2 Kiểm tra endpoints
```powershell
# Test config
Invoke-WebRequest -Uri "http://localhost:8080/api/oauth2/test-config" -Method GET

# Test Facebook debug
Invoke-WebRequest -Uri "http://localhost:8080/api/oauth2/debug/facebook" -Method GET
```

### 5.3 Kiểm tra cấu hình ứng dụng
File: `src/main/resources/application.yaml`
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          facebook:
            client-id: your-facebook-client-id
            client-secret: your-facebook-client-secret
            redirect-uri: http://localhost:8080/login/oauth2/code/facebook
        provider:
          facebook:
            authorization-uri: https://www.facebook.com/dialog/oauth
            token-uri: https://graph.facebook.com/oauth/access_token
            user-info-uri: https://graph.facebook.com/me?fields=id,first_name,middle_name,last_name,name,email,verified
```

## 📋 Bước 6: Thu thập thông tin lỗi

### 6.1 Screenshot lỗi
Chụp màn hình khi lỗi xảy ra

### 6.2 Console logs
1. Mở Developer Tools (F12)
2. Console tab
3. Copy tất cả logs

### 6.3 Network logs
1. Developer Tools > Network tab
2. Thử đăng nhập
3. Copy requests/responses

### 6.4 Application logs
1. Terminal chạy ứng dụng
2. Copy logs khi lỗi xảy ra

## 📋 Bước 7: Gửi thông tin debug

Khi gặp lỗi, hãy cung cấp:

1. **Screenshot lỗi**
2. **Console logs** (F12 > Console)
3. **Network logs** (F12 > Network)
4. **Application logs** (terminal)
5. **URL đầy đủ** khi lỗi xảy ra
6. **Thời gian** lỗi xảy ra

## 🎯 Checklist hoàn thành

- [ ] Ứng dụng chạy bình thường
- [ ] Facebook App có App Domains: localhost
- [ ] Facebook App có Valid OAuth Redirect URIs đúng
- [ ] Facebook App ở chế độ Development/Live
- [ ] Không có lỗi trong console trình duyệt
- [ ] Không có lỗi trong logs ứng dụng
- [ ] Có thể đăng nhập thành công

## 💡 Lưu ý quan trọng

1. **Facebook App phải được cấu hình đúng**
2. **Redirect URI phải chính xác**
3. **App Domains phải có localhost**
4. **Kiểm tra logs chi tiết**
5. **Test từng bước một**

## 🔧 Scripts hỗ trợ

- `debug-facebook-oauth2.ps1`: Kiểm tra tổng quan
- `monitor-facebook-login.ps1`: Monitor logs
- `test-facebook-oauth2.ps1`: Test endpoints

## 📞 Hỗ trợ

Nếu vẫn gặp lỗi sau khi làm theo hướng dẫn này, hãy cung cấp:
1. Thông tin lỗi chi tiết
2. Screenshot
3. Logs từ các bước trên 