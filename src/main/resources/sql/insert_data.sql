-- ============================================
-- RESET DATABASE + INSERT SAMPLE DATA
-- Database: EduLanguageCenter
-- ============================================

USE EduLanguageCenter;
GO

PRINT N'----- XÓA DỮ LIỆU CŨ -----';

DELETE FROM attendances;
DELETE FROM schedules;
DELETE FROM payments;
DELETE FROM invoices;
DELETE FROM results;
DELETE FROM enrollments;

DELETE FROM classes;

DELETE FROM user_accounts;

DELETE FROM students;
DELETE FROM teachers;
DELETE FROM staffs;

DELETE FROM courses;
DELETE FROM rooms;

PRINT N'----- RESET IDENTITY -----';

DBCC CHECKIDENT ('rooms', RESEED, 0);
DBCC CHECKIDENT ('courses', RESEED, 0);
DBCC CHECKIDENT ('teachers', RESEED, 0);
DBCC CHECKIDENT ('staffs', RESEED, 0);
DBCC CHECKIDENT ('students', RESEED, 0);
DBCC CHECKIDENT ('classes', RESEED, 0);
DBCC CHECKIDENT ('user_accounts', RESEED, 0);
DBCC CHECKIDENT ('enrollments', RESEED, 0);
DBCC CHECKIDENT ('schedules', RESEED, 0);
DBCC CHECKIDENT ('attendances', RESEED, 0);
DBCC CHECKIDENT ('payments', RESEED, 0);
DBCC CHECKIDENT ('invoices', RESEED, 0);
DBCC CHECKIDENT ('results', RESEED, 0);

PRINT N'----- INSERT DỮ LIỆU MẪU -----';

-- ROOMS
INSERT INTO rooms (room_name, capacity, location, status, created_at, updated_at)
VALUES
(N'Phòng A101',30,N'Tầng 1 - Tòa A','ACTIVE',GETDATE(),GETDATE()),
(N'Phòng A102',25,N'Tầng 1 - Tòa A','ACTIVE',GETDATE(),GETDATE()),
(N'Phòng B201',20,N'Tầng 2 - Tòa B','ACTIVE',GETDATE(),GETDATE());

-- COURSES
INSERT INTO courses (course_name, description, level, duration_hours, fee, status, created_at, updated_at)
VALUES
(N'Tiếng Anh giao tiếp A1',N'Khóa học căn bản cho người mới bắt đầu','A1',48,3500000,'ACTIVE',GETDATE(),GETDATE()),
(N'Tiếng Anh giao tiếp A2',N'Trình độ sơ cấp','A2',60,4200000,'ACTIVE',GETDATE(),GETDATE()),
(N'Tiếng Anh B1',N'Trình độ trung cấp','B1',72,5000000,'ACTIVE',GETDATE(),GETDATE());

-- TEACHERS
INSERT INTO teachers (full_name,phone,email,specialty,hire_date,status,created_at,updated_at)
VALUES
(N'Nguyễn Văn An','0901234567','teacher1@edu.vn',N'Tiếng Anh',GETDATE(),'ACTIVE',GETDATE(),GETDATE()),
(N'Trần Thị Bình','0912345678','teacher2@edu.vn',N'Tiếng Anh',GETDATE(),'ACTIVE',GETDATE(),GETDATE());

-- STAFF
INSERT INTO staffs (full_name,role,phone,email,created_at,updated_at)
VALUES
(N'Admin Quản trị','ADMIN','0987654321','admin@edu.vn',GETDATE(),GETDATE());

-- STUDENTS
INSERT INTO students 
(full_name,date_of_birth,gender,phone,email,address,registration_date,status,created_at,updated_at)
VALUES
(N'Lê Văn Cường','2000-05-15','MALE','0901112233','student1@edu.vn',N'Hà Nội',GETDATE(),'ACTIVE',GETDATE(),GETDATE()),
(N'Phạm Thị Dung','1999-08-20','FEMALE','0912223344','student2@edu.vn',N'Hồ Chí Minh',GETDATE(),'ACTIVE',GETDATE(),GETDATE()),
(N'Hoàng Minh Đức','2001-03-10','MALE','0923334455','student3@edu.vn',N'Đà Nẵng',GETDATE(),'ACTIVE',GETDATE(),GETDATE());

-- CLASSES
INSERT INTO classes
(class_name,course_id,teacher_id,room_id,start_date,end_date,max_student,status,created_at,updated_at)
VALUES
(N'Lớp A1-K1',1,1,1,'2025-01-06','2025-04-30',30,'ACTIVE',GETDATE(),GETDATE()),
(N'Lớp A2-K1',2,1,2,'2025-02-01','2025-05-31',25,'ACTIVE',GETDATE(),GETDATE()),
(N'Lớp B1-K1',3,2,3,'2025-03-01','2025-06-30',20,'ACTIVE',GETDATE(),GETDATE());

-- USER ACCOUNTS
INSERT INTO user_accounts
(username,password_hash,role,related_id,created_at,updated_at)
VALUES
('admin','123','ADMIN',1,GETDATE(),GETDATE()),
('teacher1','123','TEACHER',1,GETDATE(),GETDATE()),
('student1','123','STUDENT',1,GETDATE(),GETDATE());

-- ENROLLMENTS
INSERT INTO enrollments
(student_id,class_id,enrollment_date,status,created_at,updated_at)
VALUES
(1,1,GETDATE(),'ACTIVE',GETDATE(),GETDATE()),
(2,1,GETDATE(),'ACTIVE',GETDATE(),GETDATE()),
(3,2,GETDATE(),'ACTIVE',GETDATE(),GETDATE());

-- SCHEDULES
INSERT INTO schedules
(class_id,room_id,date,start_time,end_time,created_at,updated_at)
VALUES
(1,1,'2025-03-05','08:00','10:00',GETDATE(),GETDATE()),
(1,1,'2025-03-07','08:00','10:00',GETDATE(),GETDATE()),
(2,2,'2025-03-06','14:00','16:00',GETDATE(),GETDATE());

-- ATTENDANCES
INSERT INTO attendances
(student_id,class_id,date,status,created_at,updated_at)
VALUES
(1,1,'2025-03-04','PRESENT',GETDATE(),GETDATE()),
(2,1,'2025-03-04','PRESENT',GETDATE(),GETDATE()),
(1,1,'2025-03-03','PRESENT',GETDATE(),GETDATE());

-- PAYMENTS
INSERT INTO payments
(student_id,enrollment_id,amount,payment_date,payment_method,status,created_at,updated_at)
VALUES
(1,1,1750000,GETDATE(),'BANK_TRANSFER','COMPLETED',GETDATE(),GETDATE()),
(2,2,3500000,GETDATE(),'CASH','COMPLETED',GETDATE(),GETDATE()),
(3,3,2100000,GETDATE(),'MOMO','COMPLETED',GETDATE(),GETDATE());

-- INVOICES
INSERT INTO invoices
(student_id,enrollment_id,total_amount,issue_date,status,created_at,updated_at)
VALUES
(1,1,3500000,GETDATE(),'PAID',GETDATE(),GETDATE()),
(2,2,3500000,GETDATE(),'PAID',GETDATE(),GETDATE()),
(3,3,4200000,GETDATE(),'PARTIAL',GETDATE(),GETDATE());

-- RESULTS
INSERT INTO results
(student_id,class_id,score,grade,comment,created_at,updated_at)
VALUES
(1,1,8.5,N'Khá',N'Tiến bộ tốt',GETDATE(),GETDATE()),
(2,1,9.0,N'Giỏi',NULL,GETDATE(),GETDATE()),
(3,2,7.5,N'Khá',NULL,GETDATE(),GETDATE());

PRINT N'ĐÃ RESET VÀ CHÈN LẠI DỮ LIỆU MẪU THÀNH CÔNG';
GO