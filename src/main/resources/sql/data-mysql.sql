USE edulanguage;

-- =======================================================
-- data-mysql.sql — EduLanguage Center Sample Data
-- Chạy sau schema-mysql.sql
-- =======================================================

-- 1. ROOMS (Phòng học)
INSERT INTO rooms (room_name, capacity, location, status, created_at, updated_at)
SELECT 'Phòng A101', 30, 'Tầng 1 - Tòa A', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name = 'Phòng A101');

INSERT INTO rooms (room_name, capacity, location, status, created_at, updated_at)
SELECT 'Phòng A102', 25, 'Tầng 1 - Tòa A', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name = 'Phòng A102');

INSERT INTO rooms (room_name, capacity, location, status, created_at, updated_at)
SELECT 'Phòng B201', 20, 'Tầng 2 - Tòa B', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE room_name = 'Phòng B201');

-- 2. COURSES (Khóa học)
INSERT INTO courses (course_name, description, level, duration_hours, fee, status, created_at, updated_at)
SELECT 'Tiếng Anh giao tiếp A1', 'Khóa học căn bản cho người mới bắt đầu', 'A1', 48,  3500000, 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE course_name = 'Tiếng Anh giao tiếp A1');

INSERT INTO courses (course_name, description, level, duration_hours, fee, status, created_at, updated_at)
SELECT 'Tiếng Anh giao tiếp A2', 'Trình độ sơ cấp — củng cố nền tảng', 'A2', 60, 4200000, 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE course_name = 'Tiếng Anh giao tiếp A2');

INSERT INTO courses (course_name, description, level, duration_hours, fee, status, created_at, updated_at)
SELECT 'Tiếng Anh B1', 'Trình độ trung cấp — phản xạ giao tiếp', 'B1', 72, 5000000, 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE course_name = 'Tiếng Anh B1');

INSERT INTO courses (course_name, description, level, duration_hours, fee, status, created_at, updated_at)
SELECT 'Tiếng Anh B2', 'Nâng cao — luyện thi chứng chỉ quốc tế', 'B2', 90, 6500000, 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE course_name = 'Tiếng Anh B2');

INSERT INTO courses (course_name, description, level, duration_hours, fee, status, created_at, updated_at)
SELECT 'Tiếng Nhật N5', 'Nhật ngữ cho người mới bắt đầu', 'N5', 60, 4500000, 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE course_name = 'Tiếng Nhật N5');

INSERT INTO courses (course_name, description, level, duration_hours, fee, status, created_at, updated_at)
SELECT 'Tiếng Hàn Sơ cấp', 'Tiếng Hàn cơ bản Hangul & hội thoại', 'Sơ cấp', 48, 4000000, 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE course_name = 'Tiếng Hàn Sơ cấp');

-- 3. STAFFS (Nhân viên / Admin)
INSERT INTO staffs (full_name, role, phone, email, created_at, updated_at)
SELECT 'Admin Quản trị', 'ADMIN', '0987654321', 'admin@edu.vn', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM staffs WHERE email = 'admin@edu.vn');

INSERT INTO staffs (full_name, role, phone, email, created_at, updated_at)
SELECT 'Nguyễn Thị Hoa', 'STAFF', '0976543210', 'staff1@edu.vn', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM staffs WHERE email = 'staff1@edu.vn');

INSERT INTO staffs (full_name, role, phone, email, created_at, updated_at)
SELECT 'Trần Văn Phúc', 'STAFF', '0965432109', 'staff2@edu.vn', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM staffs WHERE email = 'staff2@edu.vn');

-- 4. TEACHERS (Giáo viên)
INSERT INTO teachers (full_name, phone, email, specialty, hire_date, status, created_at, updated_at)
SELECT 'Nguyễn Văn An', '0901234567', 'teacher1@edu.vn', 'Tiếng Anh', NOW(), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM teachers WHERE email = 'teacher1@edu.vn');

INSERT INTO teachers (full_name, phone, email, specialty, hire_date, status, created_at, updated_at)
SELECT 'Trần Thị Bình', '0912345678', 'teacher2@edu.vn', 'Tiếng Anh', NOW(), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM teachers WHERE email = 'teacher2@edu.vn');

INSERT INTO teachers (full_name, phone, email, specialty, hire_date, status, created_at, updated_at)
SELECT 'Lê Minh Tuấn', '0923456789', 'teacher3@edu.vn', 'Tiếng Nhật', NOW(), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM teachers WHERE email = 'teacher3@edu.vn');

INSERT INTO teachers (full_name, phone, email, specialty, hire_date, status, created_at, updated_at)
SELECT 'Phạm Thị Thu Hà', '0934567890', 'teacher4@edu.vn', 'Tiếng Hàn', NOW(), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM teachers WHERE email = 'teacher4@edu.vn');

-- 5. STUDENTS (Học viên)
INSERT INTO students (full_name, date_of_birth, gender, phone, email, address, registration_date, status, created_at, updated_at)
SELECT 'Lê Văn Cường', '2000-05-15', 'MALE', '0901112233', 'student1@edu.vn', 'Hà Nội', NOW(), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM students WHERE email = 'student1@edu.vn');

INSERT INTO students (full_name, date_of_birth, gender, phone, email, address, registration_date, status, created_at, updated_at)
SELECT 'Phạm Thị Dung', '1999-08-20', 'FEMALE', '0912223344', 'student2@edu.vn', 'Hồ Chí Minh', NOW(), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM students WHERE email = 'student2@edu.vn');

INSERT INTO students (full_name, date_of_birth, gender, phone, email, address, registration_date, status, created_at, updated_at)
SELECT 'Hoàng Minh Đức', '2001-03-10', 'MALE', '0923334455', 'student3@edu.vn', 'Đà Nẵng', NOW(), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM students WHERE email = 'student3@edu.vn');

INSERT INTO students (full_name, date_of_birth, gender, phone, email, address, registration_date, status, created_at, updated_at)
SELECT 'Vũ Thị Lan Anh', '2002-11-25', 'FEMALE', '0934445566', 'student4@edu.vn', 'Hải Phòng', NOW(), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM students WHERE email = 'student4@edu.vn');

INSERT INTO students (full_name, date_of_birth, gender, phone, email, address, registration_date, status, created_at, updated_at)
SELECT 'Bùi Quang Huy', '2000-07-08', 'MALE', '0945556677', 'student5@edu.vn', 'Cần Thơ', NOW(), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM students WHERE email = 'student5@edu.vn');

-- 6. USER ACCOUNTS (mật khẩu plain text '123')
INSERT INTO user_accounts (username, password_hash, role, related_id, created_at, updated_at)
SELECT 'admin', '123', 'ADMIN', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM user_accounts WHERE username = 'admin');

INSERT INTO user_accounts (username, password_hash, role, related_id, created_at, updated_at)
SELECT 'staff1', '123', 'STAFF', 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM user_accounts WHERE username = 'staff1');

INSERT INTO user_accounts (username, password_hash, role, related_id, created_at, updated_at)
SELECT 'teacher1', '123', 'TEACHER', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM user_accounts WHERE username = 'teacher1');

INSERT INTO user_accounts (username, password_hash, role, related_id, created_at, updated_at)
SELECT 'teacher2', '123', 'TEACHER', 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM user_accounts WHERE username = 'teacher2');

INSERT INTO user_accounts (username, password_hash, role, related_id, created_at, updated_at)
SELECT 'student1', '123', 'STUDENT', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM user_accounts WHERE username = 'student1');

INSERT INTO user_accounts (username, password_hash, role, related_id, created_at, updated_at)
SELECT 'student2', '123', 'STUDENT', 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM user_accounts WHERE username = 'student2');

INSERT INTO user_accounts (username, password_hash, role, related_id, created_at, updated_at)
SELECT 'student3', '123', 'STUDENT', 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM user_accounts WHERE username = 'student3');

-- 7. CLASSES (Lớp học)
INSERT INTO classes (class_name, course_id, teacher_id, room_id, start_date, end_date, max_student, status, created_at, updated_at)
SELECT 'Lớp A1-K1', 1, 1, 1, '2026-01-06', '2026-04-30', 30, 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM classes WHERE class_name = 'Lớp A1-K1');

INSERT INTO classes (class_name, course_id, teacher_id, room_id, start_date, end_date, max_student, status, created_at, updated_at)
SELECT 'Lớp A2-K1', 2, 1, 2, '2026-02-01', '2026-05-31', 25, 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM classes WHERE class_name = 'Lớp A2-K1');

INSERT INTO classes (class_name, course_id, teacher_id, room_id, start_date, end_date, max_student, status, created_at, updated_at)
SELECT 'Lớp B1-K1', 3, 2, 3, '2026-03-01', '2026-06-30', 20, 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM classes WHERE class_name = 'Lớp B1-K1');

INSERT INTO classes (class_name, course_id, teacher_id, room_id, start_date, end_date, max_student, status, created_at, updated_at)
SELECT 'Lớp N5-K1', 5, 3, 1, '2026-03-15', '2026-07-15', 15, 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM classes WHERE class_name = 'Lớp N5-K1');

-- 8. ENROLLMENTS (Ghi danh)
INSERT INTO enrollments (student_id, class_id, enrollment_date, status, created_at, updated_at)
SELECT 1, 1, NOW(), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM enrollments WHERE student_id = 1 AND class_id = 1);

INSERT INTO enrollments (student_id, class_id, enrollment_date, status, created_at, updated_at)
SELECT 2, 1, NOW(), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM enrollments WHERE student_id = 2 AND class_id = 1);

INSERT INTO enrollments (student_id, class_id, enrollment_date, status, created_at, updated_at)
SELECT 3, 2, NOW(), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM enrollments WHERE student_id = 3 AND class_id = 2);

INSERT INTO enrollments (student_id, class_id, enrollment_date, status, created_at, updated_at)
SELECT 4, 3, NOW(), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM enrollments WHERE student_id = 4 AND class_id = 3);

INSERT INTO enrollments (student_id, class_id, enrollment_date, status, created_at, updated_at)
SELECT 5, 4, NOW(), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM enrollments WHERE student_id = 5 AND class_id = 4);

-- 9. SCHEDULES (Lịch học)
INSERT INTO schedules (class_id, room_id, date, start_time, end_time, created_at, updated_at)
SELECT 1, 1, '2026-03-17', '08:00:00', '10:00:00', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM schedules WHERE class_id = 1 AND date = '2026-03-17');

INSERT INTO schedules (class_id, room_id, date, start_time, end_time, created_at, updated_at)
SELECT 1, 1, '2026-03-19', '08:00:00', '10:00:00', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM schedules WHERE class_id = 1 AND date = '2026-03-19');

INSERT INTO schedules (class_id, room_id, date, start_time, end_time, created_at, updated_at)
SELECT 2, 2, '2026-03-18', '14:00:00', '16:00:00', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM schedules WHERE class_id = 2 AND date = '2026-03-18');

INSERT INTO schedules (class_id, room_id, date, start_time, end_time, created_at, updated_at)
SELECT 3, 3, '2026-03-20', '18:00:00', '20:00:00', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM schedules WHERE class_id = 3 AND date = '2026-03-20');

-- 10. ATTENDANCES (Điểm danh)
INSERT INTO attendances (student_id, class_id, date, status, created_at, updated_at)
SELECT 1, 1, '2026-03-17', 'PRESENT', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM attendances WHERE student_id = 1 AND class_id = 1);

INSERT INTO attendances (student_id, class_id, date, status, created_at, updated_at)
SELECT 2, 1, '2026-03-17', 'PRESENT', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM attendances WHERE student_id = 2 AND class_id = 1);

INSERT INTO attendances (student_id, class_id, date, status, created_at, updated_at)
SELECT 3, 2, '2026-03-18', 'PRESENT', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM attendances WHERE student_id = 3 AND class_id = 2);

INSERT INTO attendances (student_id, class_id, date, status, created_at, updated_at)
SELECT 4, 3, '2026-03-20', 'ABSENT', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM attendances WHERE student_id = 4 AND class_id = 3);

-- 11. INVOICES (Hóa đơn học phí)
INSERT INTO invoices (student_id, enrollment_id, total_amount, issue_date, status, created_at, updated_at)
SELECT 1, 1, 3500000, NOW(), 'PAID', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM invoices WHERE student_id = 1 AND enrollment_id = 1);

INSERT INTO invoices (student_id, enrollment_id, total_amount, issue_date, status, created_at, updated_at)
SELECT 2, 2, 3500000, NOW(), 'PAID', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM invoices WHERE student_id = 2 AND enrollment_id = 2);

INSERT INTO invoices (student_id, enrollment_id, total_amount, issue_date, status, created_at, updated_at)
SELECT 3, 3, 4200000, NOW(), 'UNPAID', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM invoices WHERE student_id = 3 AND enrollment_id = 3);

INSERT INTO invoices (student_id, enrollment_id, total_amount, issue_date, status, created_at, updated_at)
SELECT 4, 4, 5000000, NOW(), 'UNPAID', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM invoices WHERE student_id = 4 AND enrollment_id = 4);

INSERT INTO invoices (student_id, enrollment_id, total_amount, issue_date, status, created_at, updated_at)
SELECT 5, 5, 4500000, NOW(), 'UNPAID', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM invoices WHERE student_id = 5 AND enrollment_id = 5);

-- 12. PAYMENTS (Giao dịch thanh toán)
INSERT INTO payments (student_id, enrollment_id, amount, payment_date, payment_method, status, created_at, updated_at)
SELECT 1, 1, 3500000, NOW(), 'BANK_TRANSFER', 'COMPLETED', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM payments WHERE student_id = 1 AND enrollment_id = 1);

INSERT INTO payments (student_id, enrollment_id, amount, payment_date, payment_method, status, created_at, updated_at)
SELECT 2, 2, 3500000, NOW(), 'CASH', 'COMPLETED', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM payments WHERE student_id = 2 AND enrollment_id = 2);

-- 13. RESULTS (Kết quả học tập)
INSERT INTO results (student_id, class_id, score, grade, comment, created_at, updated_at)
SELECT 1, 1, 8.5, 'Khá', 'Tiến bộ tốt', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM results WHERE student_id = 1 AND class_id = 1);

INSERT INTO results (student_id, class_id, score, grade, comment, created_at, updated_at)
SELECT 2, 1, 9.0, 'Giỏi', NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM results WHERE student_id = 2 AND class_id = 1);

INSERT INTO results (student_id, class_id, score, grade, comment, created_at, updated_at)
SELECT 3, 2, 7.5, 'Khá', 'Cần luyện thêm kỹ năng', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM results WHERE student_id = 3 AND class_id = 2);

INSERT INTO results (student_id, class_id, score, grade, comment, created_at, updated_at)
SELECT 4, 3, 6.0, 'Trung bình', 'Cần cố gắng hơn', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM results WHERE student_id = 4 AND class_id = 3);

-- 14. PROMO CODES (Mã khuyến mại)
INSERT INTO promo_codes (code, description, discount_percentage, max_discount_amount, valid_from, valid_until, is_active, usage_count, max_usages, created_at, updated_at)
SELECT 'WELCOME10', 'Ưu đãi chào mừng học viên mới', 10, 500000, '2026-01-01', '2026-12-31', 1, 0, 100, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'WELCOME10');

INSERT INTO promo_codes (code, description, discount_percentage, max_discount_amount, valid_from, valid_until, is_active, usage_count, max_usages, created_at, updated_at)
SELECT 'SUMMER2026', 'Khuyến mại mùa Hè 2026', 15, 1000000, '2026-06-01', '2026-08-31', 1, 0, 50, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'SUMMER2026');

INSERT INTO promo_codes (code, description, discount_percentage, max_discount_amount, valid_from, valid_until, is_active, usage_count, max_usages, created_at, updated_at)
SELECT 'VIP20', 'Ưu đãi VIP không giới hạn', 20, NULL, NULL, NULL, 1, 0, NULL, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'VIP20');

INSERT INTO promo_codes (code, description, discount_percentage, max_discount_amount, valid_from, valid_until, is_active, usage_count, max_usages, created_at, updated_at)
SELECT 'HANOI15', 'Ưu đãi khai trương chi nhánh HN', 15, 750000, '2026-03-01', '2026-05-31', 1, 0, 30, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM promo_codes WHERE code = 'HANOI15');

