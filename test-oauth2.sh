#!/bin/bash

# Script test OAuth2 cho Mini Supermarket
# Sử dụng: ./test-oauth2.sh

BASE_URL="http://localhost:8080"

echo "🔐 Test OAuth2 Mini Supermarket"
echo "=================================="

# Kiểm tra ứng dụng có chạy không
echo "📡 Kiểm tra ứng dụng..."
if curl -s "$BASE_URL/api/oauth2/test-config" > /dev/null; then
    echo "✅ Ứng dụng đang chạy"
else
    echo "❌ Ứng dụng không chạy. Hãy khởi động ứng dụng trước!"
    exit 1
fi

echo ""
echo "🔧 Test OAuth2 Configuration..."
CONFIG_RESPONSE=$(curl -s "$BASE_URL/api/oauth2/test-config")
echo "$CONFIG_RESPONSE" | jq '.' 2>/dev/null || echo "$CONFIG_RESPONSE"

echo ""
echo "📋 Các endpoint có thể test:"
echo "1. Test config: GET $BASE_URL/api/oauth2/test-config"
echo "2. User info: GET $BASE_URL/api/oauth2/user-info"
echo "3. Google OAuth: GET $BASE_URL/oauth2/authorization/google"
echo "4. Facebook OAuth: GET $BASE_URL/oauth2/authorization/facebook"
echo "5. Test page: $BASE_URL/oauth2-test.html"

echo ""
echo "🎯 Để test đăng nhập OAuth2:"
echo "1. Mở trình duyệt và truy cập: $BASE_URL/oauth2-test.html"
echo "2. Click 'Test OAuth2 Config' để kiểm tra cấu hình"
echo "3. Click 'Đăng nhập với Google' hoặc 'Đăng nhập với Facebook'"
echo "4. Xem kết quả trong phần 'Kết quả test'"

echo ""
echo "📝 Lưu ý:"
echo "- Đảm bảo đã cấu hình OAuth2 credentials trong file .env"
echo "- Kiểm tra redirect URIs trong Google/Facebook console"
echo "- Xem logs để debug nếu có lỗi"

echo ""
echo "🔗 Tài liệu chi tiết: OAUTH2-TEST-GUIDE.md" 