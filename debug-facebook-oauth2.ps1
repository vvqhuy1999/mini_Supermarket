# Debug Facebook OAuth2 Script
Write-Host "🔍 Debug Facebook OAuth2 - Mini Supermarket" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green

# Test 1: Check application status
Write-Host "`n📋 Test 1: Kiểm tra trạng thái ứng dụng..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/oauth2/test-config" -Method GET -TimeoutSec 10
    $data = $response.Content | ConvertFrom-Json
    
    if ($data.success) {
        Write-Host "✅ Ứng dụng đang chạy bình thường" -ForegroundColor Green
        Write-Host "Configuration:" -ForegroundColor Cyan
        Write-Host "  - Google configured: $($data.result.google_configured)" -ForegroundColor Cyan
        Write-Host "  - Facebook configured: $($data.result.facebook_configured)" -ForegroundColor Cyan
        Write-Host "  - Google redirect URI: $($data.result.google_redirect_uri)" -ForegroundColor Cyan
        Write-Host "  - Facebook redirect URI: $($data.result.facebook_redirect_uri)" -ForegroundColor Cyan
    } else {
        Write-Host "❌ Ứng dụng có vấn đề" -ForegroundColor Red
        Write-Host "Error: $($data.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Không thể kết nối đến ứng dụng" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "`n💡 Hướng dẫn khắc phục:" -ForegroundColor Yellow
    Write-Host "1. Đảm bảo ứng dụng đang chạy: .\mvnw spring-boot:run" -ForegroundColor White
    Write-Host "2. Kiểm tra port 8080 có bị chiếm không: netstat -an | findstr :8080" -ForegroundColor White
    exit 1
}

# Test 2: Check Facebook test page
Write-Host "`n📋 Test 2: Kiểm tra trang test Facebook..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/facebook-test.html" -Method GET
    Write-Host "✅ Trang test Facebook có thể truy cập" -ForegroundColor Green
    Write-Host "URL: http://localhost:8080/facebook-test.html" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Không thể truy cập trang test Facebook" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Check OAuth2 endpoints
Write-Host "`n📋 Test 3: Kiểm tra endpoints OAuth2..." -ForegroundColor Yellow
$endpoints = @(
    "http://localhost:8080/oauth2/authorization/facebook",
    "http://localhost:8080/api/oauth2/debug/facebook",
    "http://localhost:8080/api/oauth2/analyze"
)

foreach ($endpoint in $endpoints) {
    try {
        $response = Invoke-WebRequest -Uri $endpoint -Method GET -TimeoutSec 5
        Write-Host "✅ $endpoint - Status: $($response.StatusCode)" -ForegroundColor Green
    } catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "⚠️  $endpoint - Status: $statusCode" -ForegroundColor Yellow
        if ($statusCode -eq 401) {
            Write-Host "   (401 là bình thường vì chưa đăng nhập)" -ForegroundColor Gray
        }
    }
}

# Test 4: Check application logs
Write-Host "`n📋 Test 4: Kiểm tra logs ứng dụng..." -ForegroundColor Yellow
Write-Host "💡 Để xem logs chi tiết, hãy mở terminal khác và chạy:" -ForegroundColor Cyan
Write-Host "   .\mvnw spring-boot:run" -ForegroundColor White
Write-Host "   Sau đó thử đăng nhập Facebook và xem logs" -ForegroundColor White

# Instructions for user
Write-Host "`n📋 Hướng dẫn debug:" -ForegroundColor Yellow
Write-Host "1. Mở trình duyệt và truy cập: http://localhost:8080/facebook-test.html" -ForegroundColor White
Write-Host "2. Click 'Test OAuth2 Config' để kiểm tra cấu hình" -ForegroundColor White
Write-Host "3. Click '📘 Đăng nhập với Facebook' để thử đăng nhập" -ForegroundColor White
Write-Host "4. Nếu có lỗi, hãy:" -ForegroundColor White
Write-Host "   - Mở Developer Tools (F12)" -ForegroundColor White
Write-Host "   - Chuyển sang tab Console" -ForegroundColor White
Write-Host "   - Thử đăng nhập lại và xem có lỗi gì không" -ForegroundColor White
Write-Host "5. Kiểm tra Facebook App settings:" -ForegroundColor White
Write-Host "   - App Domains: localhost" -ForegroundColor White
Write-Host "   - Valid OAuth Redirect URIs: http://localhost:8080/login/oauth2/code/facebook" -ForegroundColor White

# Common issues and solutions
Write-Host "`n📋 Các lỗi thường gặp:" -ForegroundColor Yellow
Write-Host "❌ 'Invalid redirect_uri parameter':" -ForegroundColor Red
Write-Host "   → Kiểm tra Valid OAuth Redirect URIs trong Facebook App" -ForegroundColor White
Write-Host "❌ 'App not configured for this domain':" -ForegroundColor Red
Write-Host "   → Thêm 'localhost' vào App Domains" -ForegroundColor White
Write-Host "❌ 'User cancelled':" -ForegroundColor Red
Write-Host "   → Người dùng hủy đăng nhập, thử lại" -ForegroundColor White
Write-Host "❌ 'Application error':" -ForegroundColor Red
Write-Host "   → Kiểm tra logs ứng dụng để xem chi tiết" -ForegroundColor White

Write-Host "`n🎯 Bước tiếp theo:" -ForegroundColor Green
Write-Host "1. Thử đăng nhập Facebook theo hướng dẫn trên" -ForegroundColor White
Write-Host "2. Nếu vẫn lỗi, hãy cung cấp:" -ForegroundColor White
Write-Host "   - Screenshot lỗi" -ForegroundColor White
Write-Host "   - Logs từ console trình duyệt" -ForegroundColor White
Write-Host "   - Logs từ ứng dụng" -ForegroundColor White 