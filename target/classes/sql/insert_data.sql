-- ============================================
-- Project_GK_Nhom_19 - INSERT dữ liệu mẫu
-- SQL Server - Database: EduLanguageCenter
-- Chạy sau khi đã có bảng (JPA/Hibernate tạo hoặc script CREATE TABLE)
-- ============================================

USE EduLanguageCenter;
GO

-- BCrypt hash cho mật khẩu "123"
DECLARE @pwHash NVARCHAR(255) = N'$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW';

-- ========== 1. ROOMS ==========
SET IDENTITY_INSERT rooms ON;
INSERT INTO rooms (id, created_at, updated_at, room_name, capacity, location, status)
VALUES
    (1, GETDATE(), GETDATE(), N'Phòng A101', 30, N'Tầng 1 - Tòa A', 'ACTIVE'),
    (2, GETDATE(), GETDATE(), N'Phòng A102', 25, N'Tầng 1 - Tòa A', 'ACTIVE'),
    (3, GETDATE(), GETDATE(), N'Phòng B201', 20, N'Tầng 2 - Tòa B', 'ACTIVE');
SET IDENTITY_INSERT rooms OFF;

-- ========== 2. COURSES ==========
SET IDENTITY_INSERT courses ON;
INSERT INTO courses (id, created_at, updated_at, course_name, description, level, duration_hours, fee, status)
VALUES
    (1, GETDATE(), GETDATE(), N'Tiếng Anh giao tiếp A1', N'Khóa học căn bản cho người mới bắt đầu', N'A1', 48, 3500000.00, 'ACTIVE'),
    (2, GETDATE(), GETDATE(), N'Tiếng Anh giao tiếp A2', N'Trình độ sơ cấp', N'A2', 60, 4200000.00, 'ACTIVE'),
    (3, GETDATE(), GETDATE(), N'Tiếng Anh B1', N'Trình độ trung cấp', N'B1', 72, 5000000.00, 'ACTIVE');
SET IDENTITY_INSERT courses OFF;

-- ========== 3. TEACHERS ==========
SET IDENTITY_INSERT teachers ON;
INSERT INTO teachers (id, created_at, updated_at, full_name, phone, email, specialty, hire_date, status)
VALUES
    (1, GETDATE(), GETDATE(), N'Nguyễn Văn An', '0901234567', 'teacher1@edu.vn', N'Tiếng Anh', GETDATE(), 'ACTIVE'),
    (2, GETDATE(), GETDATE(), N'Trần Thị Bình', '0912345678', 'teacher2@edu.vn', N'Tiếng Anh', GETDATE(), 'ACTIVE');
SET IDENTITY_INSERT teachers OFF;

-- ========== 4. STAFFS ==========
SET IDENTITY_INSERT staffs ON;
INSERT INTO staffs (id, created_at, updated_at, full_name, role, phone, email)
VALUES
    (1, GETDATE(), GETDATE(), N'Admin Quản trị', 'ADMIN', '0987654321', 'admin@edu.vn');
SET IDENTITY_INSERT staffs OFF;

-- ========== 5. STUDENTS ==========
SET IDENTITY_INSERT students ON;
INSERT INTO students (id, created_at, updated_at, full_name, date_of_birth, gender, phone, email, address, registration_date, status)
VALUES
    (1, GETDATE(), GETDATE(), N'Lê Văn Cường', '2000-05-15', 'MALE', '0901112233', 'student1@edu.vn', N'Hà Nội', GETDATE(), 'ACTIVE'),
    (2, GETDATE(), GETDATE(), N'Phạm Thị Dung', '1999-08-20', 'FEMALE', '0912223344', 'student2@edu.vn', N'Hồ Chí Minh', GETDATE(), 'ACTIVE'),
    (3, GETDATE(), GETDATE(), N'Hoàng Minh Đức', '2001-03-10', 'MALE', '0923334455', 'student3@edu.vn', N'Đà Nẵng', GETDATE(), 'ACTIVE');
SET IDENTITY_INSERT students OFF;

-- ========== 6. CLASSES ==========
SET IDENTITY_INSERT classes ON;
INSERT INTO classes (id, created_at, updated_at, class_name, course_id, teacher_id, room_id, start_date, end_date, max_student, status)
VALUES
    (1, GETDATE(), GETDATE(), N'Lớp A1-K1', 1, 1, 1, '2025-01-06', '2025-04-30', 30, 'ACTIVE'),
    (2, GETDATE(), GETDATE(), N'Lớp A2-K1', 2, 1, 2, '2025-02-01', '2025-05-31', 25, 'ACTIVE'),
    (3, GETDATE(), GETDATE(), N'Lớp B1-K1', 3, 2, 3, '2025-03-01', '2025-06-30', 20, 'ACTIVE');
SET IDENTITY_INSERT classes OFF;

-- ========== 7. USER_ACCOUNTS (admin/123, teacher1/123, student1/123) ==========
SET IDENTITY_INSERT user_accounts ON;
INSERT INTO user_accounts (id, created_at, updated_at, username, password_hash, role, related_id)
VALUES
    (1, GETDATE(), GETDATE(), 'admin', @pwHash, 'ADMIN', 1),           -- related_id=1 → staffs.id
    (2, GETDATE(), GETDATE(), 'teacher1', @pwHash, 'TEACHER', 1),      -- related_id=1 → teachers.id
    (3, GETDATE(), GETDATE(), 'student1', @pwHash, 'STUDENT', 1);      -- related_id=1 → students.id
SET IDENTITY_INSERT user_accounts OFF;

-- ========== 8. ENROLLMENTS ==========
SET IDENTITY_INSERT enrollments ON;
INSERT INTO enrollments (id, created_at, updated_at, student_id, class_id, enrollment_date, status, result)
VALUES
    (1, GETDATE(), GETDATE(), 1, 1, GETDATE(), 'ACTIVE', NULL),
    (2, GETDATE(), GETDATE(), 2, 1, GETDATE(), 'ACTIVE', NULL),
    (3, GETDATE(), GETDATE(), 3, 2, GETDATE(), 'ACTIVE', NULL);
SET IDENTITY_INSERT enrollments OFF;

-- ========== 9. SCHEDULES ==========
SET IDENTITY_INSERT schedules ON;
INSERT INTO schedules (id, created_at, updated_at, class_id, room_id, date, start_time, end_time)
VALUES
    (1, GETDATE(), GETDATE(), 1, 1, '2025-03-05', '08:00', '10:00'),
    (2, GETDATE(), GETDATE(), 1, 1, '2025-03-07', '08:00', '10:00'),
    (3, GETDATE(), GETDATE(), 2, 2, '2025-03-06', '14:00', '16:00');
SET IDENTITY_INSERT schedules OFF;

-- ========== 10. ATTENDANCES ==========
SET IDENTITY_INSERT attendances ON;
INSERT INTO attendances (id, created_at, updated_at, student_id, class_id, date, status)
VALUES
    (1, GETDATE(), GETDATE(), 1, 1, '2025-03-04', 'PRESENT'),
    (2, GETDATE(), GETDATE(), 2, 1, '2025-03-04', 'PRESENT'),
    (3, GETDATE(), GETDATE(), 1, 1, '2025-03-03', 'PRESENT');
SET IDENTITY_INSERT attendances OFF;

-- ========== 11. PAYMENTS ==========
SET IDENTITY_INSERT payments ON;
INSERT INTO payments (id, created_at, updated_at, student_id, enrollment_id, amount, payment_date, payment_method, status)
VALUES
    (1, GETDATE(), GETDATE(), 1, 1, 1750000.00, GETDATE(), 'BANK_TRANSFER', 'COMPLETED'),
    (2, GETDATE(), GETDATE(), 2, 2, 3500000.00, GETDATE(), 'CASH', 'COMPLETED'),
    (3, GETDATE(), GETDATE(), 3, 3, 2100000.00, GETDATE(), 'MOMO', 'COMPLETED');
SET IDENTITY_INSERT payments OFF;

-- ========== 12. INVOICES ==========
SET IDENTITY_INSERT invoices ON;
INSERT INTO invoices (id, created_at, updated_at, student_id, enrollment_id, total_amount, issue_date, status)
VALUES
    (1, GETDATE(), GETDATE(), 1, 1, 3500000.00, GETDATE(), 'PAID'),
    (2, GETDATE(), GETDATE(), 2, 2, 3500000.00, GETDATE(), 'PAID'),
    (3, GETDATE(), GETDATE(), 3, 3, 4200000.00, GETDATE(), 'PARTIAL');
SET IDENTITY_INSERT invoices OFF;

-- ========== 13. RESULTS ==========
SET IDENTITY_INSERT results ON;
INSERT INTO results (id, created_at, updated_at, student_id, class_id, score, grade, comment)
VALUES
    (1, GETDATE(), GETDATE(), 1, 1, 8.50, N'Khá', N'Tiến bộ tốt'),
    (2, GETDATE(), GETDATE(), 2, 1, 9.00, N'Giỏi', NULL),
    (3, GETDATE(), GETDATE(), 3, 2, 7.50, N'Khá', NULL);
SET IDENTITY_INSERT results OFF;

PRINT N'Đã chèn dữ liệu mẫu thành công.';
GO
