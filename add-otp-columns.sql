-- Script thêm các cột OTP và Reset Password vào bảng NguoiDung
-- Chạy script này trong PostgreSQL để cập nhật database

-- Thêm cột OTP
ALTER TABLE "NguoiDung" ADD COLUMN IF NOT EXISTS "otp_code" VARCHAR(6);
ALTER TABLE "NguoiDung" ADD COLUMN IF NOT EXISTS "otp_generated_time" TIMESTAMP;
ALTER TABLE "NguoiDung" ADD COLUMN IF NOT EXISTS "otp_attempts" INTEGER DEFAULT 0;

-- Thêm cột Reset Password
ALTER TABLE "NguoiDung" ADD COLUMN IF NOT EXISTS "reset_password_token" VARCHAR(255);
ALTER TABLE "NguoiDung" ADD COLUMN IF NOT EXISTS "reset_password_token_expiry" TIMESTAMP;

-- Cập nhật các cột mới thành NULL cho các record hiện có
UPDATE "NguoiDung" SET 
    "otp_code" = NULL,
    "otp_generated_time" = NULL,
    "otp_attempts" = 0,
    "reset_password_token" = NULL,
    "reset_password_token_expiry" = NULL
WHERE "otp_code" IS NULL;

-- Kiểm tra kết quả
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns 
WHERE table_name = 'NguoiDung' 
AND column_name IN ('otp_code', 'otp_generated_time', 'otp_attempts', 'reset_password_token', 'reset_password_token_expiry')
ORDER BY column_name;
