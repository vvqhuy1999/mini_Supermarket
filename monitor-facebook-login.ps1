# Monitor Facebook Login Script
Write-Host "📊 Monitor Facebook Login - Mini Supermarket" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green

Write-Host "`n🎯 Hướng dẫn sử dụng:" -ForegroundColor Yellow
Write-Host "1. Mở terminal này và chạy script này" -ForegroundColor White
Write-Host "2. Mở terminal khác và chạy: .\mvnw spring-boot:run" -ForegroundColor White
Write-Host "3. Mở trình duyệt và truy cập: http://localhost:8080/facebook-test.html" -ForegroundColor White
Write-Host "4. Thử đăng nhập Facebook và xem logs ở đây" -ForegroundColor White

Write-Host "`n📋 Bắt đầu monitor logs..." -ForegroundColor Yellow
Write-Host "Nhấn Ctrl+C để dừng" -ForegroundColor Gray

# Monitor application logs
try {
    # Check if application is running
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/oauth2/test-config" -Method GET -TimeoutSec 5
    Write-Host "✅ Ứng dụng đang chạy" -ForegroundColor Green
    
    # Monitor for 5 minutes
    $startTime = Get-Date
    $endTime = $startTime.AddMinutes(5)
    
    Write-Host "`n⏰ Bắt đầu monitor từ $($startTime.ToString('HH:mm:ss')) đến $($endTime.ToString('HH:mm:ss'))" -ForegroundColor Cyan
    Write-Host "Thử đăng nhập Facebook trong thời gian này..." -ForegroundColor White
    
    while ((Get-Date) -lt $endTime) {
        # Test endpoints every 10 seconds
        try {
            $configResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/oauth2/test-config" -Method GET -TimeoutSec 3
            $configData = $configResponse.Content | ConvertFrom-Json
            
            if ($configData.success) {
                Write-Host "$(Get-Date -Format 'HH:mm:ss') ✅ Ứng dụng OK" -ForegroundColor Green
            } else {
                Write-Host "$(Get-Date -Format 'HH:mm:ss') ❌ Ứng dụng có vấn đề: $($configData.message)" -ForegroundColor Red
            }
        } catch {
            Write-Host "$(Get-Date -Format 'HH:mm:ss') ❌ Không thể kết nối ứng dụng" -ForegroundColor Red
        }
        
        Start-Sleep -Seconds 10
    }
    
    Write-Host "`n⏰ Kết thúc monitor" -ForegroundColor Yellow
    Write-Host "Nếu bạn đã thử đăng nhập Facebook, hãy kiểm tra:" -ForegroundColor White
    Write-Host "1. Console trình duyệt (F12 > Console)" -ForegroundColor White
    Write-Host "2. Network tab trong Developer Tools" -ForegroundColor White
    Write-Host "3. Logs từ terminal chạy ứng dụng" -ForegroundColor White
    
} catch {
    Write-Host "❌ Ứng dụng không chạy hoặc không thể kết nối" -ForegroundColor Red
    Write-Host "Hãy chạy: .\mvnw spring-boot:run" -ForegroundColor White
}

Write-Host "`n📋 Checklist debug:" -ForegroundColor Yellow
Write-Host "□ Facebook App có App Domains: localhost" -ForegroundColor White
Write-Host "□ Facebook App có Valid OAuth Redirect URIs: http://localhost:8080/login/oauth2/code/facebook" -ForegroundColor White
Write-Host "□ Facebook App đang ở chế độ Development hoặc Live" -ForegroundColor White
Write-Host "□ Không có lỗi trong console trình duyệt" -ForegroundColor White
Write-Host "□ Không có lỗi trong logs ứng dụng" -ForegroundColor White

Write-Host "`n💡 Nếu vẫn lỗi, hãy cung cấp:" -ForegroundColor Cyan
Write-Host "- Screenshot trang lỗi" -ForegroundColor White
Write-Host "- Logs từ console trình duyệt (F12 > Console)" -ForegroundColor White
Write-Host "- Logs từ terminal chạy ứng dụng" -ForegroundColor White
Write-Host "- URL đầy đủ khi lỗi xảy ra" -ForegroundColor White 