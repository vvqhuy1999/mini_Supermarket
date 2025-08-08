# OAuth2 Complete Test Suite
# Gộp tất cả các test OAuth2 thành một file duy nhất

Write-Host "=" * 80 -ForegroundColor Green
Write-Host "                    OAUTH2 COMPLETE TEST SUITE                    " -ForegroundColor Green
Write-Host "=" * 80 -ForegroundColor Green

# Hàm hiển thị header
function Show-TestHeader {
    param([string]$TestName)
    Write-Host "`n" + "=" * 60 -ForegroundColor Cyan
    Write-Host "TEST: $TestName" -ForegroundColor Cyan
    Write-Host "=" * 60 -ForegroundColor Cyan
}

# Hàm kiểm tra ứng dụng có chạy không
function Test-ApplicationRunning {
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method GET -TimeoutSec 5
        return $true
    } catch {
        try {
            $response = Invoke-RestMethod -Uri "http://localhost:8080/api/oauth2/test-config" -Method GET -TimeoutSec 5
            return $true
        } catch {
            return $false
        }
    }
}

# Kiểm tra ứng dụng có chạy không
Write-Host "Kiểm tra ứng dụng Spring Boot..." -ForegroundColor Yellow
if (-not (Test-ApplicationRunning)) {
    Write-Host "❌ Ứng dụng Spring Boot chưa chạy!" -ForegroundColor Red
    Write-Host "Vui lòng chạy: .\mvnw.cmd spring-boot:run" -ForegroundColor Yellow
    Write-Host "Hoặc: mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}
Write-Host "✅ Ứng dụng Spring Boot đang chạy" -ForegroundColor Green

# TEST 1: OAuth2 Configuration
Show-TestHeader "OAuth2 Configuration"
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/oauth2/test-config" -Method GET
    Write-Host "✅ OAuth2 Config: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

# TEST 2: Success Endpoint (without login)
Show-TestHeader "OAuth2 Success Endpoint (Not Logged In)"
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/oauth2/success" -Method GET
    Write-Host "✅ Success Response: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Expected Error (not logged in): $($_.Exception.Message)" -ForegroundColor Yellow
}

# TEST 3: Email Check
Show-TestHeader "Email Duplicate Check"
$testEmails = @("test@example.com", "admin@example.com", "nonexistent@example.com")
foreach ($email in $testEmails) {
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/oauth2/check-email?email=$email" -Method GET
        $exists = if ($response.result.exists) { "EXISTS" } else { "NOT EXISTS" }
        Write-Host "📧 Email '$email': $exists" -ForegroundColor Cyan
        Write-Host "   Response: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor White
    } catch {
        Write-Host "❌ Error checking email '$email': $($_.Exception.Message)" -ForegroundColor Red
    }
}

# TEST 4: Sub Check
Show-TestHeader "OAuth2 Sub Check"
$testSubs = @("test_google_id", "123456789", "nonexistent_sub")
foreach ($sub in $testSubs) {
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/oauth2/check-sub?sub=$sub" -Method GET
        $exists = if ($response.result.exists) { "EXISTS" } else { "NOT EXISTS" }
        Write-Host "🔑 Sub '$sub': $exists" -ForegroundColor Cyan
        Write-Host "   Response: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor White
    } catch {
        Write-Host "❌ Error checking sub '$sub': $($_.Exception.Message)" -ForegroundColor Red
    }
}

# TEST 5: User Info (without login)
Show-TestHeader "OAuth2 User Info (Not Logged In)"
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/oauth2/user-info" -Method GET
    Write-Host "✅ User Info Response: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Expected Error (not logged in): $($_.Exception.Message)" -ForegroundColor Yellow
}

# TEST 6: Database Check Functions
Show-TestHeader "Database Check Functions"

function Check-EmailInDatabase {
    param([string]$Email)
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/oauth2/check-email?email=$Email" -Method GET
        return $response
    } catch {
        Write-Host "❌ Error checking email: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

function Check-SubInDatabase {
    param([string]$Sub)
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/oauth2/check-sub?sub=$Sub" -Method GET
        return $response
    } catch {
        Write-Host "❌ Error checking sub: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

Write-Host "✅ Database check functions loaded" -ForegroundColor Green

# TEST 7: OAuth2 Flow Instructions
Show-TestHeader "Manual OAuth2 Testing Instructions"
Write-Host "🌐 1. Mở browser: http://localhost:8080/oauth2-test.html" -ForegroundColor White
Write-Host "🔑 2. Click 'Đăng nhập với Google'" -ForegroundColor White
Write-Host "✅ 3. Hoàn thành đăng nhập Google" -ForegroundColor White
Write-Host "🔄 4. Kiểm tra redirect về: /api/oauth2/success" -ForegroundColor White
Write-Host "📊 5. Kiểm tra Spring Boot console logs" -ForegroundColor White
Write-Host "🗄️ 6. Kiểm tra database record:" -ForegroundColor White
Write-Host "   - maNguoiDung: ND + 8 ký tự ngẫu nhiên (ví dụ: NDA1B2C3D4)" -ForegroundColor Gray
Write-Host "   - sub: [Google ID]" -ForegroundColor Gray
Write-Host "   - email: [your-email@gmail.com]" -ForegroundColor Gray
Write-Host "   - vaiTro: 3 (khách hàng)" -ForegroundColor Gray
Write-Host "   - isDeleted: false" -ForegroundColor Gray

# TEST 8: API Testing Commands
Show-TestHeader "API Testing Commands"
Write-Host "📧 Check email after login:" -ForegroundColor White
Write-Host "   Check-EmailInDatabase -Email 'your-email@gmail.com'" -ForegroundColor Gray
Write-Host ""
Write-Host "🔑 Check sub after login:" -ForegroundColor White
Write-Host "   Check-SubInDatabase -Sub '[Google ID]'" -ForegroundColor Gray

# TEST 9: Expected Behavior Summary
Show-TestHeader "Expected Behavior Summary"
Write-Host "✅ Sử dụng UserService.registerUser() thay vì tự tạo logic" -ForegroundColor Green
Write-Host "✅ maNguoiDung tự động tạo theo format: ND + 8 ký tự ngẫu nhiên" -ForegroundColor Green
Write-Host "✅ sub field lưu ID gốc từ OAuth2 provider" -ForegroundColor Green
Write-Host "✅ Tự động mã hóa mật khẩu bằng BCrypt" -ForegroundColor Green
Write-Host "✅ Kiểm tra email duplicate tự động" -ForegroundColor Green
Write-Host "✅ Đặt giá trị mặc định: isDeleted=false, vaiTro=3" -ForegroundColor Green

# TEST 10: Benefits Summary
Show-TestHeader "Benefits of UserService Integration"
Write-Host "✅ Không cần tự tạo logic đăng ký" -ForegroundColor Green
Write-Host "✅ Tự động tạo maNguoiDung theo format chuẩn" -ForegroundColor Green
Write-Host "✅ Tự động mã hóa mật khẩu" -ForegroundColor Green
Write-Host "✅ Kiểm tra email duplicate" -ForegroundColor Green
Write-Host "✅ Đặt giá trị mặc định" -ForegroundColor Green
Write-Host "✅ Dễ dàng maintain và debug" -ForegroundColor Green
Write-Host "✅ Tránh duplicate code" -ForegroundColor Green

# Kết thúc
Write-Host "`n" + "=" * 80 -ForegroundColor Green
Write-Host "                    OAUTH2 TEST SUITE COMPLETED                   " -ForegroundColor Green
Write-Host "=" * 80 -ForegroundColor Green

Write-Host "`n🎯 Tiếp theo:" -ForegroundColor Yellow
Write-Host "1. Thực hiện manual test bằng browser" -ForegroundColor White
Write-Host "2. Kiểm tra Spring Boot logs trong console" -ForegroundColor White
Write-Host "3. Sử dụng API functions để verify database" -ForegroundColor White
Write-Host "4. Test với nhiều tài khoản Google/Facebook khác nhau" -ForegroundColor White

# Hỗ trợ parameter
if ($args.Count -gt 0) {
    $action = $args[0]
    $value = $args[1]
    
    if ($action -eq "-Email" -and $value) {
        Write-Host "`n🔍 Checking email: $value" -ForegroundColor Cyan
        $result = Check-EmailInDatabase -Email $value
        if ($result) {
            Write-Host "📧 Email Check Result: $($result | ConvertTo-Json -Depth 3)" -ForegroundColor Green
        }
    } elseif ($action -eq "-Sub" -and $value) {
        Write-Host "`n🔍 Checking sub: $value" -ForegroundColor Cyan
        $result = Check-SubInDatabase -Sub $value
        if ($result) {
            Write-Host "🔑 Sub Check Result: $($result | ConvertTo-Json -Depth 3)" -ForegroundColor Green
        }
    } elseif ($action -eq "-OpenBrowser") {
        Write-Host "`n🌐 Opening OAuth2 test page..." -ForegroundColor Cyan
        Start-Process "http://localhost:8080/oauth2-test.html"
    }
}

Write-Host "`n📖 Usage examples:" -ForegroundColor Yellow
Write-Host "   .\oauth2-complete-test.ps1" -ForegroundColor Gray
Write-Host "   .\oauth2-complete-test.ps1 -Email 'test@gmail.com'" -ForegroundColor Gray
Write-Host "   .\oauth2-complete-test.ps1 -Sub 'google_id_123'" -ForegroundColor Gray
Write-Host "   .\oauth2-complete-test.ps1 -OpenBrowser" -ForegroundColor Gray
