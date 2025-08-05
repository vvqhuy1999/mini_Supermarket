# Hướng dẫn cài đặt biến môi trường

## Bước 1: Tạo file .env
Copy file `env.example` thành file `.env`:
```bash
cp env.example .env
```

## Bước 2: Cập nhật các giá trị trong file .env

### Database Configuration
```env
DB_URL=jdbc:mysql://localhost:3306/QuanLySieuThi?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=admin
```

### JWT Configuration
```env
JWT_SECRET=your-super-secret-jwt-key-here-make-it-long-and-random
JWT_EXPIRATION=86400
```

### Google OAuth2 Configuration
```env
GOOGLE_CLIENT_ID=your-google-client-id-here
GOOGLE_CLIENT_SECRET=your-google-client-secret-here
```

### Facebook OAuth2 Configuration
```env
FACEBOOK_CLIENT_ID=your-facebook-client-id-here
FACEBOOK_CLIENT_SECRET=your-facebook-client-secret-here
```

## Bước 3: Lưu ý bảo mật

1. **KHÔNG BAO GIỜ** commit file `.env` lên git
2. File `.env` đã được thêm vào `.gitignore`
3. Chỉ commit file `env.example` để làm mẫu cho các developer khác

## Bước 4: Cách lấy Google OAuth2 credentials

1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo project mới hoặc chọn project có sẵn
3. Enable Google+ API
4. Tạo OAuth 2.0 credentials
5. Copy Client ID và Client Secret vào file `.env`

## Bước 5: Cách lấy Facebook OAuth2 credentials

1. Truy cập [Facebook Developers](https://developers.facebook.com/)
2. Tạo app mới
3. Thêm Facebook Login product
4. Lấy App ID và App Secret
5. Copy vào file `.env`

## Bước 6: Tạo JWT Secret mạnh

Sử dụng lệnh sau để tạo JWT secret mạnh:
```bash
# Trên Linux/Mac
openssl rand -base64 64

# Trên Windows PowerShell
[System.Web.Security.Membership]::GeneratePassword(64, 0)
```

## Lưu ý quan trọng

- Thay đổi tất cả các giá trị mặc định trong file `.env`
- JWT_SECRET phải đủ dài và phức tạp (ít nhất 32 ký tự)
- Không chia sẻ file `.env` với bất kỳ ai
- Backup file `.env` ở nơi an toàn 