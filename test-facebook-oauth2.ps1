# Test Facebook OAuth2 Script
Write-Host "🔧 Testing Facebook OAuth2 Configuration..." -ForegroundColor Green

# Test 1: Check if application is running
Write-Host "`n📋 Test 1: Checking if application is running..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/oauth2/test-config" -Method GET -TimeoutSec 10
    Write-Host "✅ Application is running" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Application is not running or not accessible" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 2: Test OAuth2 configuration
Write-Host "`n📋 Test 2: Testing OAuth2 configuration..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/oauth2/test-config" -Method GET
    $data = $response.Content | ConvertFrom-Json
    
    if ($data.success) {
        Write-Host "✅ OAuth2 configuration is working" -ForegroundColor Green
        Write-Host "Configuration: $($data.result | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
    } else {
        Write-Host "❌ OAuth2 configuration has issues" -ForegroundColor Red
        Write-Host "Error: $($data.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Failed to test OAuth2 configuration" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Test Facebook debug endpoint
Write-Host "`n📋 Test 3: Testing Facebook debug endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/oauth2/debug/facebook" -Method GET
    $data = $response.Content | ConvertFrom-Json
    
    if ($data.success) {
        Write-Host "✅ Facebook debug endpoint is working" -ForegroundColor Green
        Write-Host "Debug info: $($data.result | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
    } else {
        Write-Host "⚠️ Facebook debug endpoint returned error (this is normal if not logged in)" -ForegroundColor Yellow
        Write-Host "Message: $($data.message)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Failed to test Facebook debug endpoint" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Check available URLs
Write-Host "`n📋 Test 4: Available OAuth2 URLs..." -ForegroundColor Yellow
Write-Host "✅ Facebook Authorization: http://localhost:8080/oauth2/authorization/facebook" -ForegroundColor Green
Write-Host "✅ Facebook Callback: http://localhost:8080/api/oauth2/callback/facebook" -ForegroundColor Green
Write-Host "✅ Facebook Debug: http://localhost:8080/api/oauth2/debug/facebook" -ForegroundColor Green
Write-Host "✅ Test Config: http://localhost:8080/api/oauth2/test-config" -ForegroundColor Green
Write-Host "✅ Test Page: http://localhost:8080/facebook-test.html" -ForegroundColor Green

# Test 5: Check if test page is accessible
Write-Host "`n📋 Test 5: Checking test page accessibility..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/facebook-test.html" -Method GET
    Write-Host "✅ Facebook test page is accessible" -ForegroundColor Green
} catch {
    Write-Host "❌ Facebook test page is not accessible" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎯 Summary:" -ForegroundColor Green
Write-Host "1. Open browser and go to: http://localhost:8080/facebook-test.html" -ForegroundColor Cyan
Write-Host "2. Click 'Test OAuth2 Config' to verify configuration" -ForegroundColor Cyan
Write-Host "3. Click 'Đăng nhập với Facebook' to test login" -ForegroundColor Cyan
Write-Host "4. Check the results in the test page" -ForegroundColor Cyan

Write-Host "`n📝 Troubleshooting:" -ForegroundColor Yellow
Write-Host "- If Facebook login fails, check Facebook App configuration" -ForegroundColor White
Write-Host "- Ensure redirect URI is: http://localhost:8080/login/oauth2/code/facebook" -ForegroundColor White
Write-Host "- Check Facebook App Domains include 'localhost'" -ForegroundColor White
Write-Host "- Verify Client ID and Secret in application.yaml" -ForegroundColor White

Write-Host "`n✅ Testing completed!" -ForegroundColor Green 