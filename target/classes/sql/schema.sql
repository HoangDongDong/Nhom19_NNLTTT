-- =======================================================
-- schema.sql — EduLanguage Center Database Schema
-- SQL Server (T-SQL)
-- Chạy file này để tạo toàn bộ bảng + quan hệ từ đầu.
-- SAU KHI CHẠY: chạy tiếp data.sql để có dữ liệu mẫu.
-- =======================================================

-- Xóa bảng con trước (theo thứ tự FK)
IF OBJECT_ID('dbo.attendances',  'U') IS NOT NULL DROP TABLE dbo.attendances;
IF OBJECT_ID('dbo.results',      'U') IS NOT NULL DROP TABLE dbo.results;
IF OBJECT_ID('dbo.payments',     'U') IS NOT NULL DROP TABLE dbo.payments;
IF OBJECT_ID('dbo.invoices',     'U') IS NOT NULL DROP TABLE dbo.invoices;
IF OBJECT_ID('dbo.schedules',    'U') IS NOT NULL DROP TABLE dbo.schedules;
IF OBJECT_ID('dbo.enrollments',  'U') IS NOT NULL DROP TABLE dbo.enrollments;
IF OBJECT_ID('dbo.user_accounts','U') IS NOT NULL DROP TABLE dbo.user_accounts;
IF OBJECT_ID('dbo.classes',      'U') IS NOT NULL DROP TABLE dbo.classes;
IF OBJECT_ID('dbo.students',     'U') IS NOT NULL DROP TABLE dbo.students;
IF OBJECT_ID('dbo.teachers',     'U') IS NOT NULL DROP TABLE dbo.teachers;
IF OBJECT_ID('dbo.staffs',       'U') IS NOT NULL DROP TABLE dbo.staffs;
IF OBJECT_ID('dbo.courses',      'U') IS NOT NULL DROP TABLE dbo.courses;
IF OBJECT_ID('dbo.rooms',        'U') IS NOT NULL DROP TABLE dbo.rooms;
IF OBJECT_ID('dbo.branches',     'U') IS NOT NULL DROP TABLE dbo.branches;
IF OBJECT_ID('dbo.promo_codes',  'U') IS NOT NULL DROP TABLE dbo.promo_codes;

PRINT N'[1/15] Tạo bảng branches...';
CREATE TABLE dbo.branches (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    branch_name NVARCHAR(100) NOT NULL,
    address     NVARCHAR(255),
    phone       NVARCHAR(20),
    status      NVARCHAR(20) DEFAULT 'ACTIVE',
    created_at  DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at  DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'[2/15] Tạo bảng rooms...';
CREATE TABLE dbo.rooms (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    room_name   NVARCHAR(50)  NOT NULL,
    capacity    INT,
    location    NVARCHAR(200),
    status      NVARCHAR(20)  DEFAULT 'ACTIVE',
    created_at  DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at  DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'[3/15] Tạo bảng courses...';
CREATE TABLE dbo.courses (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    course_name     NVARCHAR(100) NOT NULL,
    description     NVARCHAR(1000),
    level           NVARCHAR(50),
    duration_hours  INT,
    fee             DECIMAL(14,2),
    status          NVARCHAR(20) DEFAULT 'ACTIVE',
    created_at      DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at      DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'[4/15] Tạo bảng staffs...';
CREATE TABLE dbo.staffs (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    full_name   NVARCHAR(100) NOT NULL,
    role        NVARCHAR(20),
    phone       NVARCHAR(20),
    email       NVARCHAR(100) UNIQUE,
    created_at  DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at  DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'[5/15] Tạo bảng teachers...';
CREATE TABLE dbo.teachers (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    full_name   NVARCHAR(100) NOT NULL,
    phone       NVARCHAR(20),
    email       NVARCHAR(100) UNIQUE,
    specialty   NVARCHAR(100),
    hire_date   DATETIME2,
    status      NVARCHAR(20)  DEFAULT 'ACTIVE',
    created_at  DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at  DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'[6/15] Tạo bảng students...';
CREATE TABLE dbo.students (
    id                  BIGINT IDENTITY(1,1) PRIMARY KEY,
    full_name           NVARCHAR(100) NOT NULL,
    date_of_birth       DATE,
    gender              NVARCHAR(20),
    phone               NVARCHAR(20)  UNIQUE,
    email               NVARCHAR(100) UNIQUE,
    address             NVARCHAR(255),
    registration_date   DATETIME2,
    status              NVARCHAR(20)  DEFAULT 'ACTIVE',
    created_at          DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at          DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'[7/15] Tạo bảng user_accounts...';
CREATE TABLE dbo.user_accounts (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    username        NVARCHAR(50)  NOT NULL UNIQUE,
    password_hash   NVARCHAR(255) NOT NULL,
    role            NVARCHAR(20)  NOT NULL,
    -- Polymorphic FK: trỏ tới students / teachers / staffs tuỳ theo role
    related_id      BIGINT,
    created_at      DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at      DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'[8/15] Tạo bảng classes...';
CREATE TABLE dbo.classes (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    class_name  NVARCHAR(100) NOT NULL,
    course_id   BIGINT        REFERENCES dbo.courses(id),
    teacher_id  BIGINT        REFERENCES dbo.teachers(id),
    room_id     BIGINT        REFERENCES dbo.rooms(id),
    start_date  DATE,
    end_date    DATE,
    max_student INT,
    status      NVARCHAR(20)  DEFAULT 'ACTIVE',
    created_at  DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at  DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'[9/15] Tạo bảng enrollments...';
CREATE TABLE dbo.enrollments (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    student_id      BIGINT REFERENCES dbo.students(id),
    class_id        BIGINT REFERENCES dbo.classes(id),
    enrollment_date DATETIME2,
    status          NVARCHAR(50) DEFAULT 'ACTIVE',
    result          NVARCHAR(20),
    created_at      DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at      DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'[10/15] Tạo bảng schedules...';
CREATE TABLE dbo.schedules (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    class_id   BIGINT REFERENCES dbo.classes(id),
    room_id    BIGINT REFERENCES dbo.rooms(id),
    date       DATE,
    start_time TIME,
    end_time   TIME,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'[11/15] Tạo bảng attendances...';
CREATE TABLE dbo.attendances (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    student_id BIGINT REFERENCES dbo.students(id),
    class_id   BIGINT REFERENCES dbo.classes(id),
    date       DATE,
    status     NVARCHAR(20),
    note       NVARCHAR(255),
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'[12/15] Tạo bảng invoices...';
CREATE TABLE dbo.invoices (
    id                    BIGINT IDENTITY(1,1) PRIMARY KEY,
    student_id            BIGINT         NOT NULL REFERENCES dbo.students(id),
    enrollment_id         BIGINT         REFERENCES dbo.enrollments(id),
    total_amount          DECIMAL(14,2)  NOT NULL,
    issue_date            DATETIME2,
    status                NVARCHAR(50)   DEFAULT 'UNPAID',
    applied_promo_code    NVARCHAR(50),
    promo_discount_amount DECIMAL(14,2),
    created_at            DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'[13/15] Tạo bảng payments...';
CREATE TABLE dbo.payments (
    id             BIGINT IDENTITY(1,1) PRIMARY KEY,
    student_id     BIGINT        REFERENCES dbo.students(id),
    enrollment_id  BIGINT        REFERENCES dbo.enrollments(id),
    amount         DECIMAL(14,2),
    payment_date   DATETIME2,
    payment_method NVARCHAR(30),
    status         NVARCHAR(50)  DEFAULT 'PENDING',
    created_at     DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at     DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'[14/15] Tạo bảng results...';
CREATE TABLE dbo.results (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    student_id BIGINT REFERENCES dbo.students(id),
    class_id   BIGINT REFERENCES dbo.classes(id),
    score      DECIMAL(7,2),
    grade      NVARCHAR(20),
    comment    NVARCHAR(1000),
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'[15/15] Tạo bảng promo_codes...';
CREATE TABLE dbo.promo_codes (
    id                  BIGINT IDENTITY(1,1) PRIMARY KEY,
    code                NVARCHAR(50)  NOT NULL UNIQUE,
    description         NVARCHAR(255),
    discount_percentage INT           NOT NULL DEFAULT 0,
    max_discount_amount DECIMAL(14,2),
    valid_from          DATE,
    valid_until         DATE,
    is_active           BIT           NOT NULL DEFAULT 1,
    usage_count         INT           NOT NULL DEFAULT 0,
    max_usages          INT,
    created_at          DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at          DATETIME2 NOT NULL DEFAULT GETDATE()
);

PRINT N'✅ SCHEMA TẠO THÀNH CÔNG — chạy data.sql để có dữ liệu mẫu.';
