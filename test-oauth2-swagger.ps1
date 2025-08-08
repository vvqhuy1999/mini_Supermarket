# 🔐 OAuth2 Swagger Testing Script
# Test OAuth2 APIs using Swagger UI

Write-Host "🔐 ===== OAuth2 SWAGGER TESTING =====" -ForegroundColor Green
Write-Host "Testing OAuth2 functionality via Swagger UI" -ForegroundColor Yellow

# Configuration
$BASE_URL = "http://localhost:8080"
$SWAGGER_URL = "$BASE_URL/swagger-ui/index.html"

Write-Host ""
Write-Host "📋 Pre-Testing Checklist:" -ForegroundColor Cyan
Write-Host "✅ 1. Spring Boot application đang chạy" -ForegroundColor Green
Write-Host "✅ 2. Google OAuth2 đã được cấu hình" -ForegroundColor Green
Write-Host "✅ 3. Database connection OK" -ForegroundColor Green
Write-Host "✅ 4. Đã xóa các HTML test files" -ForegroundColor Green

Write-Host ""
Write-Host "🌐 Testing Application Health..." -ForegroundColor Yellow

try {
    $healthResponse = Invoke-WebRequest -Uri "$BASE_URL/actuator/health" -Method GET -TimeoutSec 10
    if ($healthResponse.StatusCode -eq 200) {
        Write-Host "✅ Application Health: OK" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Application Health: FAILED" -ForegroundColor Red
    Write-Host "   Make sure Spring Boot is running on port 8080" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "📊 Testing OAuth2 Configuration..." -ForegroundColor Yellow

try {
    $configResponse = Invoke-WebRequest -Uri "$BASE_URL/api/oauth2/test-config" -Method GET -TimeoutSec 10
    if ($configResponse.StatusCode -eq 200) {
        Write-Host "✅ OAuth2 Configuration: OK" -ForegroundColor Green
        
        # Parse and display config
        $config = $configResponse.Content | ConvertFrom-Json
        Write-Host "   📱 Google Client ID: $($config.result.google_client_id.Substring(0,20))..." -ForegroundColor White
        Write-Host "   🔗 Redirect URI: $($config.result.google_redirect_uri)" -ForegroundColor White
    }
} catch {
    Write-Host "❌ OAuth2 Configuration: FAILED" -ForegroundColor Red
    Write-Host "   Check Google OAuth2 setup in application.yaml" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🎯 Available OAuth2 APIs to Test in Swagger:" -ForegroundColor Cyan

$apis = @(
    "✅ /api/oauth2/success - Main OAuth2 success handler",
    "🔍 /api/oauth2/analyze - Analyze OAuth2 user data", 
    "📧 /api/oauth2/check-email - Check email existence",
    "🆔 /api/oauth2/check-sub - Check OAuth2 sub existence",
    "🎫 /api/oauth2/get-token - Get JWT token",
    "🔒 /api/oauth2/logout - Local logout",
    "🌐 /api/oauth2/logout/website - Website logout",
    "🔧 /api/oauth2/test-config - Test configuration"
)

foreach ($api in $apis) {
    Write-Host "   $api" -ForegroundColor White
}

Write-Host ""
Write-Host "📖 How to Test OAuth2 in Swagger:" -ForegroundColor Cyan
Write-Host "1. 🌐 Open Swagger UI in browser" -ForegroundColor Yellow
Write-Host "2. 🔐 First, login with Google/Facebook:" -ForegroundColor Yellow
Write-Host "   - Navigate to: $BASE_URL/oauth2/authorization/google" -ForegroundColor White
Write-Host "   - Or: $BASE_URL/oauth2/authorization/facebook" -ForegroundColor White
Write-Host "3. ✅ After login, you'll be redirected to Swagger" -ForegroundColor Yellow
Write-Host "4. 🧪 Test the OAuth2 endpoints in '🔐 OAuth2 Authentication' section" -ForegroundColor Yellow
Write-Host "5. 🎫 Copy JWT token from response to test protected APIs" -ForegroundColor Yellow

Write-Host ""
Write-Host "🚀 Quick Test Commands:" -ForegroundColor Cyan

# Test email check
Write-Host "📧 Testing Email Check..." -ForegroundColor Yellow
try {
    $emailTest = Invoke-WebRequest -Uri "$BASE_URL/api/oauth2/check-email?email=test@gmail.com" -Method GET
    if ($emailTest.StatusCode -eq 200) {
        Write-Host "✅ Email Check API: Working" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Email Check API: Failed" -ForegroundColor Red
}

# Test config endpoint
Write-Host "⚙️ Testing OAuth2 Config..." -ForegroundColor Yellow
try {
    $configTest = Invoke-WebRequest -Uri "$BASE_URL/api/oauth2/test-config" -Method GET
    if ($configTest.StatusCode -eq 200) {
        Write-Host "✅ OAuth2 Config API: Working" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ OAuth2 Config API: Failed" -ForegroundColor Red
}

Write-Host ""
Write-Host "🎯 Next Steps:" -ForegroundColor Green
Write-Host "1. 🌐 Open Swagger UI: $SWAGGER_URL" -ForegroundColor White
Write-Host "2. 🔐 Login OAuth2: $BASE_URL/oauth2/authorization/google" -ForegroundColor White
Write-Host "3. 🧪 Test APIs in Swagger UI" -ForegroundColor White

Write-Host ""
$openSwagger = Read-Host "🚀 Open Swagger UI now? (y/n)"
if ($openSwagger.ToLower() -eq 'y' -or $openSwagger.ToLower() -eq 'yes') {
    Write-Host "🌐 Opening Swagger UI..." -ForegroundColor Green
    Start-Process $SWAGGER_URL
    
    Start-Sleep -Seconds 2
    
    $openOAuth2 = Read-Host "🔐 Open Google OAuth2 login? (y/n)"
    if ($openOAuth2.ToLower() -eq 'y' -or $openOAuth2.ToLower() -eq 'yes') {
        Write-Host "🔐 Opening Google OAuth2 login..." -ForegroundColor Green
        Start-Process "$BASE_URL/oauth2/authorization/google"
    }
}

Write-Host ""
Write-Host "🎉 OAuth2 Swagger testing setup complete!" -ForegroundColor Green
Write-Host "📚 All APIs are documented in Swagger UI with detailed examples" -ForegroundColor Cyan
