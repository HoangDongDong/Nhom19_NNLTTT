-- ============================================
-- Project_GK_Nhom_19 - INSERT tài khoản đăng nhập
-- SQL Server - Database: EduLanguageCenter
-- File này CHỈ chèn dữ liệu phục vụ đăng nhập (staff/teacher/student + user_accounts).
-- ============================================

USE EduLanguageCenter;
GO

-- BCrypt hash cho mật khẩu mặc định (ví dụ "123").
-- NÊN: đổi giá trị này thành hash do ứng dụng generate cho mật khẩu bạn muốn.
DECLARE @pwHash NVARCHAR(255) = N'$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW';

DECLARE @staffAdminId  BIGINT;
DECLARE @teacher1Id    BIGINT;
DECLARE @student1Id    BIGINT;

-- ========== 1. STAFF (Admin) ==========
INSERT INTO staffs (created_at, updated_at, full_name, role, phone, email)
VALUES (GETDATE(), GETDATE(), N'Admin Quản trị', 'ADMIN', '0987654321', 'admin@edu.vn');
SET @staffAdminId = SCOPE_IDENTITY();

-- ========== 2. TEACHER (teacher1) ==========
INSERT INTO teachers (created_at, updated_at, full_name, phone, email, specialty, hire_date, status)
VALUES (GETDATE(), GETDATE(), N'Nguyễn Văn An', '0901234567', 'teacher1@edu.vn', N'Tiếng Anh', GETDATE(), 'ACTIVE');
SET @teacher1Id = SCOPE_IDENTITY();

-- ========== 3. STUDENT (student1) ==========
INSERT INTO students (created_at, updated_at, full_name, date_of_birth, gender, phone, email, address, registration_date, status)
VALUES (GETDATE(), GETDATE(), N'Lê Văn Cường', '2000-05-15', 'MALE', '0901112233', 'student1@edu.vn', N'Hà Nội', GETDATE(), 'ACTIVE');
SET @student1Id = SCOPE_IDENTITY();

-- ========== 4. USER_ACCOUNTS ==========
INSERT INTO user_accounts (created_at, updated_at, username, password_hash, role, related_id)
VALUES
    (GETDATE(), GETDATE(), 'admin',    @pwHash, 'ADMIN',   @staffAdminId),
    (GETDATE(), GETDATE(), 'teacher1', @pwHash, 'TEACHER', @teacher1Id),
    (GETDATE(), GETDATE(), 'student1', @pwHash, 'STUDENT', @student1Id);

PRINT N'Đã chèn tài khoản đăng nhập: admin / teacher1 / student1 (hash BCrypt trong @pwHash).';
GO

