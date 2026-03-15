-- =======================================================
-- data.sql — EduLanguage Center Sample Data
-- SQL Server (T-SQL)
-- Chạy sau schema.sql.
-- An toàn để chạy lại: IF NOT EXISTS kiểm tra trước khi INSERT.
-- =======================================================

SET NOCOUNT ON;
PRINT N'==== BẮT ĐẦU CHÈN DỮ LIỆU MẪU ====';
GO

-- =========================================
-- 1. ROOMS (Phòng học)
-- =========================================
IF NOT EXISTS (SELECT 1 FROM rooms WHERE room_name = N'Phòng A101')
BEGIN
    INSERT INTO rooms (room_name, capacity, location, status, created_at, updated_at) VALUES
    (N'Phòng A101', 30, N'Tầng 1 - Tòa A', 'ACTIVE', GETDATE(), GETDATE()),
    (N'Phòng A102', 25, N'Tầng 1 - Tòa A', 'ACTIVE', GETDATE(), GETDATE()),
    (N'Phòng B201', 20, N'Tầng 2 - Tòa B', 'ACTIVE', GETDATE(), GETDATE());
    PRINT N'  ✓ Inserted rooms';
END
GO

-- =========================================
-- 2. COURSES (Khóa học)
-- =========================================
IF NOT EXISTS (SELECT 1 FROM courses WHERE course_name = N'Tiếng Anh giao tiếp A1')
BEGIN
    INSERT INTO courses (course_name, description, level, duration_hours, fee, status, created_at, updated_at) VALUES
    (N'Tiếng Anh giao tiếp A1', N'Khóa học căn bản cho người mới bắt đầu', 'A1', 48,  3500000, 'ACTIVE', GETDATE(), GETDATE()),
    (N'Tiếng Anh giao tiếp A2', N'Trình độ sơ cấp — củng cố nền tảng',      'A2', 60,  4200000, 'ACTIVE', GETDATE(), GETDATE()),
    (N'Tiếng Anh B1',           N'Trình độ trung cấp — phản xạ giao tiếp',   'B1', 72,  5000000, 'ACTIVE', GETDATE(), GETDATE()),
    (N'Tiếng Anh B2',           N'Nâng cao — luyện thi chứng chỉ quốc tế',   'B2', 90,  6500000, 'ACTIVE', GETDATE(), GETDATE()),
    (N'Tiếng Nhật N5',          N'Nhật ngữ cho người mới bắt đầu',           'N5', 60,  4500000, 'ACTIVE', GETDATE(), GETDATE()),
    (N'Tiếng Hàn Sơ cấp',       N'Tiếng Hàn cơ bản Hangul & hội thoại',     'Sơ cấp', 48, 4000000, 'ACTIVE', GETDATE(), GETDATE());
    PRINT N'  ✓ Inserted courses';
END
GO

-- =========================================
-- 3. STAFFS (Nhân viên / Admin)
-- =========================================
IF NOT EXISTS (SELECT 1 FROM staffs WHERE email = 'admin@edu.vn')
BEGIN
    INSERT INTO staffs (full_name, role, phone, email, created_at, updated_at) VALUES
    (N'Admin Quản trị',   'ADMIN', '0987654321', 'admin@edu.vn',  GETDATE(), GETDATE()),
    (N'Nguyễn Thị Hoa',  'STAFF', '0976543210', 'staff1@edu.vn', GETDATE(), GETDATE()),
    (N'Trần Văn Phúc',   'STAFF', '0965432109', 'staff2@edu.vn', GETDATE(), GETDATE());
    PRINT N'  ✓ Inserted staffs';
END
GO

-- =========================================
-- 4. TEACHERS (Giáo viên)
-- =========================================
IF NOT EXISTS (SELECT 1 FROM teachers WHERE email = 'teacher1@edu.vn')
BEGIN
    INSERT INTO teachers (full_name, phone, email, specialty, hire_date, status, created_at, updated_at) VALUES
    (N'Nguyễn Văn An',    '0901234567', 'teacher1@edu.vn', N'Tiếng Anh', GETDATE(), 'ACTIVE', GETDATE(), GETDATE()),
    (N'Trần Thị Bình',    '0912345678', 'teacher2@edu.vn', N'Tiếng Anh', GETDATE(), 'ACTIVE', GETDATE(), GETDATE()),
    (N'Lê Minh Tuấn',     '0923456789', 'teacher3@edu.vn', N'Tiếng Nhật', GETDATE(), 'ACTIVE', GETDATE(), GETDATE()),
    (N'Phạm Thị Thu Hà',  '0934567890', 'teacher4@edu.vn', N'Tiếng Hàn', GETDATE(), 'ACTIVE', GETDATE(), GETDATE());
    PRINT N'  ✓ Inserted teachers';
END
GO

-- =========================================
-- 5. STUDENTS (Học viên)
-- =========================================
IF NOT EXISTS (SELECT 1 FROM students WHERE email = 'student1@edu.vn')
BEGIN
    INSERT INTO students (full_name, date_of_birth, gender, phone, email, address, registration_date, status, created_at, updated_at) VALUES
    (N'Lê Văn Cường',     '2000-05-15', 'MALE',   '0901112233', 'student1@edu.vn', N'Hà Nội',       GETDATE(), 'ACTIVE', GETDATE(), GETDATE()),
    (N'Phạm Thị Dung',    '1999-08-20', 'FEMALE', '0912223344', 'student2@edu.vn', N'Hồ Chí Minh',  GETDATE(), 'ACTIVE', GETDATE(), GETDATE()),
    (N'Hoàng Minh Đức',   '2001-03-10', 'MALE',   '0923334455', 'student3@edu.vn', N'Đà Nẵng',      GETDATE(), 'ACTIVE', GETDATE(), GETDATE()),
    (N'Vũ Thị Lan Anh',   '2002-11-25', 'FEMALE', '0934445566', 'student4@edu.vn', N'Hải Phòng',    GETDATE(), 'ACTIVE', GETDATE(), GETDATE()),
    (N'Bùi Quang Huy',    '2000-07-08', 'MALE',   '0945556677', 'student5@edu.vn', N'Cần Thơ',      GETDATE(), 'ACTIVE', GETDATE(), GETDATE());
    PRINT N'  ✓ Inserted students';
END
GO

-- =========================================
-- 6. USER ACCOUNTS (Tài khoản đăng nhập)
-- =========================================
IF NOT EXISTS (SELECT 1 FROM user_accounts WHERE username = 'admin')
BEGIN
    -- Mật khẩu dạng text thuần: '123' (dùng NoOpPasswordEncoder)
    INSERT INTO user_accounts (username, password_hash, role, related_id, created_at, updated_at) VALUES
    -- Admin: trỏ vào staffs.id = 1
    ('admin',    '123', 'ADMIN',   1, GETDATE(), GETDATE()),
    -- Staff: trỏ vào staffs.id = 2
    ('staff1',   '123', 'STAFF',   2, GETDATE(), GETDATE()),
    -- Teachers: trỏ vào teachers.id
    ('teacher1', '123', 'TEACHER', 1, GETDATE(), GETDATE()),
    ('teacher2', '123', 'TEACHER', 2, GETDATE(), GETDATE()),
    -- Students: trỏ vào students.id
    ('student1', '123', 'STUDENT', 1, GETDATE(), GETDATE()),
    ('student2', '123', 'STUDENT', 2, GETDATE(), GETDATE()),
    ('student3', '123', 'STUDENT', 3, GETDATE(), GETDATE());
    PRINT N'  ✓ Inserted user_accounts (password = 123, plain text)';
END
GO

-- =========================================
-- 7. CLASSES (Lớp học)
-- =========================================
IF NOT EXISTS (SELECT 1 FROM classes WHERE class_name = N'Lớp A1-K1')
BEGIN
    INSERT INTO classes (class_name, course_id, teacher_id, room_id, start_date, end_date, max_student, status, created_at, updated_at) VALUES
    (N'Lớp A1-K1', 1, 1, 1, '2026-01-06', '2026-04-30', 30, 'ACTIVE', GETDATE(), GETDATE()),
    (N'Lớp A2-K1', 2, 1, 2, '2026-02-01', '2026-05-31', 25, 'ACTIVE', GETDATE(), GETDATE()),
    (N'Lớp B1-K1', 3, 2, 3, '2026-03-01', '2026-06-30', 20, 'ACTIVE', GETDATE(), GETDATE()),
    (N'Lớp N5-K1', 5, 3, 1, '2026-03-15', '2026-07-15', 15, 'ACTIVE', GETDATE(), GETDATE());
    PRINT N'  ✓ Inserted classes';
END
GO

-- =========================================
-- 8. ENROLLMENTS (Ghi danh)
-- =========================================
IF NOT EXISTS (SELECT 1 FROM enrollments WHERE student_id = 1 AND class_id = 1)
BEGIN
    INSERT INTO enrollments (student_id, class_id, enrollment_date, status, created_at, updated_at) VALUES
    (1, 1, GETDATE(), 'ACTIVE', GETDATE(), GETDATE()),
    (2, 1, GETDATE(), 'ACTIVE', GETDATE(), GETDATE()),
    (3, 2, GETDATE(), 'ACTIVE', GETDATE(), GETDATE()),
    (4, 3, GETDATE(), 'ACTIVE', GETDATE(), GETDATE()),
    (5, 4, GETDATE(), 'ACTIVE', GETDATE(), GETDATE());
    PRINT N'  ✓ Inserted enrollments';
END
GO

-- =========================================
-- 9. SCHEDULES (Lịch học)
-- =========================================
IF NOT EXISTS (SELECT 1 FROM schedules WHERE class_id = 1 AND date = '2026-03-17')
BEGIN
    INSERT INTO schedules (class_id, room_id, date, start_time, end_time, created_at, updated_at) VALUES
    (1, 1, '2026-03-17', '08:00', '10:00', GETDATE(), GETDATE()),
    (1, 1, '2026-03-19', '08:00', '10:00', GETDATE(), GETDATE()),
    (2, 2, '2026-03-18', '14:00', '16:00', GETDATE(), GETDATE()),
    (3, 3, '2026-03-20', '18:00', '20:00', GETDATE(), GETDATE());
    PRINT N'  ✓ Inserted schedules';
END
GO

-- =========================================
-- 10. ATTENDANCES (Điểm danh)
-- =========================================
IF NOT EXISTS (SELECT 1 FROM attendances WHERE student_id = 1 AND class_id = 1)
BEGIN
    INSERT INTO attendances (student_id, class_id, date, status, created_at, updated_at) VALUES
    (1, 1, '2026-03-17', 'PRESENT', GETDATE(), GETDATE()),
    (2, 1, '2026-03-17', 'PRESENT', GETDATE(), GETDATE()),
    (3, 2, '2026-03-18', 'PRESENT', GETDATE(), GETDATE()),
    (4, 3, '2026-03-20', 'ABSENT',  GETDATE(), GETDATE());
    PRINT N'  ✓ Inserted attendances';
END
GO

-- =========================================
-- 11. INVOICES (Hóa đơn học phí)
-- =========================================
IF NOT EXISTS (SELECT 1 FROM invoices WHERE student_id = 1 AND enrollment_id = 1)
BEGIN
    INSERT INTO invoices (student_id, enrollment_id, total_amount, issue_date, status, created_at, updated_at) VALUES
    (1, 1, 3500000, GETDATE(), 'PAID',    GETDATE(), GETDATE()),
    (2, 2, 3500000, GETDATE(), 'PAID',    GETDATE(), GETDATE()),
    (3, 3, 4200000, GETDATE(), 'UNPAID',  GETDATE(), GETDATE()),
    (4, 4, 5000000, GETDATE(), 'UNPAID',  GETDATE(), GETDATE()),
    (5, 5, 4500000, GETDATE(), 'UNPAID',  GETDATE(), GETDATE());
    PRINT N'  ✓ Inserted invoices';
END
GO

-- =========================================
-- 12. PAYMENTS (Giao dịch thanh toán)
-- =========================================
IF NOT EXISTS (SELECT 1 FROM payments WHERE student_id = 1 AND enrollment_id = 1)
BEGIN
    INSERT INTO payments (student_id, enrollment_id, amount, payment_date, payment_method, status, created_at, updated_at) VALUES
    (1, 1, 3500000, GETDATE(), 'BANK_TRANSFER', 'COMPLETED', GETDATE(), GETDATE()),
    (2, 2, 3500000, GETDATE(), 'CASH',          'COMPLETED', GETDATE(), GETDATE());
    PRINT N'  ✓ Inserted payments';
END
GO

-- =========================================
-- 13. RESULTS (Kết quả học tập)
-- =========================================
IF NOT EXISTS (SELECT 1 FROM results WHERE student_id = 1 AND class_id = 1)
BEGIN
    INSERT INTO results (student_id, class_id, score, grade, comment, created_at, updated_at) VALUES
    (1, 1, 8.5,  N'Khá',   N'Tiến bộ tốt',           GETDATE(), GETDATE()),
    (2, 1, 9.0,  N'Giỏi',  NULL,                      GETDATE(), GETDATE()),
    (3, 2, 7.5,  N'Khá',   N'Cần luyện thêm kỹ năng', GETDATE(), GETDATE()),
    (4, 3, 6.0,  N'Trung bình', N'Cần cố gắng hơn',   GETDATE(), GETDATE());
    PRINT N'  ✓ Inserted results';
END
GO

-- =========================================
-- 14. PROMO CODES (Mã khuyến mại mẫu)
-- =========================================
IF NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'WELCOME10')
BEGIN
    INSERT INTO promo_codes (code, description, discount_percentage, max_discount_amount, valid_from, valid_until, is_active, usage_count, max_usages, created_at, updated_at) VALUES
    ('WELCOME10',  N'Ưu đãi chào mừng học viên mới',   10, 500000,  '2026-01-01', '2026-12-31', 1, 0, 100,  GETDATE(), GETDATE()),
    ('SUMMER2026', N'Khuyến mại mùa Hè 2026',           15, 1000000, '2026-06-01', '2026-08-31', 1, 0, 50,   GETDATE(), GETDATE()),
    ('VIP20',      N'Ưu đãi VIP không giới hạn',        20, NULL,    NULL,         NULL,          1, 0, NULL, GETDATE(), GETDATE()),
    ('HANOI15',    N'Ưu đãi khai trương chi nhánh HN',  15, 750000,  '2026-03-01', '2026-05-31', 1, 0, 30,   GETDATE(), GETDATE());
    PRINT N'  ✓ Inserted promo_codes';
END
GO

PRINT N'==== ✅ CHÈN DỮ LIỆU MẪU HOÀN TẤT ====';
SET NOCOUNT OFF;
GO
