# Test OAuth2 Configuration
Write-Host "🔍 Testing OAuth2 Configuration..." -ForegroundColor Cyan

# Test config endpoint
try {
    $configResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/oauth2-test/config" -Method GET -TimeoutSec 10
    Write-Host "✅ Config endpoint: $($configResponse.StatusCode)" -ForegroundColor Green
    Write-Host "📄 Response: $($configResponse.Content)" -ForegroundColor Yellow
} catch {
    Write-Host "❌ Config endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🔍 Testing OAuth2 User Info (should fail if not logged in)..." -ForegroundColor Cyan

# Test user info endpoint (should fail if not logged in)
try {
    $userInfoResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/oauth2-test/user-info" -Method GET -TimeoutSec 10
    Write-Host "✅ User Info endpoint: $($userInfoResponse.StatusCode)" -ForegroundColor Green
    Write-Host "📄 Response: $($userInfoResponse.Content)" -ForegroundColor Yellow
} catch {
    Write-Host "❌ User Info endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🔍 Testing OAuth2 Success endpoint..." -ForegroundColor Cyan

# Test success endpoint
try {
    $successResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/oauth2-test/success" -Method GET -TimeoutSec 10
    Write-Host "✅ Success endpoint: $($successResponse.StatusCode)" -ForegroundColor Green
    Write-Host "📄 Response: $($successResponse.Content)" -ForegroundColor Yellow
} catch {
    Write-Host "❌ Success endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🔍 Testing OAuth2 Failure endpoint..." -ForegroundColor Cyan

# Test failure endpoint
try {
    $failureResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/oauth2-test/failure" -Method GET -TimeoutSec 10
    Write-Host "✅ Failure endpoint: $($failureResponse.StatusCode)" -ForegroundColor Green
    Write-Host "📄 Response: $($failureResponse.Content)" -ForegroundColor Yellow
} catch {
    Write-Host "❌ Failure endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎯 OAuth2 Test Summary:" -ForegroundColor Magenta
Write-Host "1. Config endpoint should return configuration status" -ForegroundColor White
Write-Host "2. User Info endpoint should return 401 if not logged in" -ForegroundColor White
Write-Host "3. Success endpoint should return 401 if not logged in" -ForegroundColor White
Write-Host "4. Failure endpoint should return failure message" -ForegroundColor White
Write-Host "`n📝 Next steps:" -ForegroundColor Cyan
Write-Host "- Visit: http://localhost:8080/oauth2/authorization/google" -ForegroundColor Yellow
Write-Host "- Visit: http://localhost:8080/oauth2/authorization/facebook" -ForegroundColor Yellow
Write-Host "- After login, check: http://localhost:8080/api/oauth2-test/user-info" -ForegroundColor Yellow 