USE edulanguage;

-- =======================================================
-- schema-mysql.sql — EduLanguage Center Database Schema (MySQL)
-- Chạy file này để tạo toàn bộ bảng + quan hệ từ đầu.
-- SAU KHI CHẠY: chạy tiếp data-mysql.sql để có dữ liệu mẫu.
-- =======================================================

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS attendances;
DROP TABLE IF EXISTS results;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS invoices;
DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS user_accounts;
DROP TABLE IF EXISTS classes;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS teachers;
DROP TABLE IF EXISTS staffs;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS branches;
DROP TABLE IF EXISTS promo_codes;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. branches
CREATE TABLE branches (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_name  VARCHAR(100)  NOT NULL,
    address      VARCHAR(255),
    phone        VARCHAR(20),
    status       VARCHAR(20)   DEFAULT 'ACTIVE',
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. rooms
CREATE TABLE rooms (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_name    VARCHAR(50)   NOT NULL,
    capacity     INT,
    location     VARCHAR(200),
    status       VARCHAR(20)   DEFAULT 'ACTIVE',
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3. courses
CREATE TABLE courses (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name     VARCHAR(100) NOT NULL,
    description     VARCHAR(1000),
    level           VARCHAR(50),
    duration_hours  INT,
    fee             DECIMAL(14,2),
    status          VARCHAR(20)  DEFAULT 'ACTIVE',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 4. staffs
CREATE TABLE staffs (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name    VARCHAR(100) NOT NULL,
    role         VARCHAR(20),
    phone        VARCHAR(20),
    email        VARCHAR(100) UNIQUE,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 5. teachers
CREATE TABLE teachers (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name    VARCHAR(100) NOT NULL,
    phone        VARCHAR(20),
    email        VARCHAR(100) UNIQUE,
    specialty    VARCHAR(100),
    hire_date    DATETIME,
    status       VARCHAR(20)  DEFAULT 'ACTIVE',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 6. students
CREATE TABLE students (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name           VARCHAR(100) NOT NULL,
    date_of_birth       DATE,
    gender              VARCHAR(20),
    phone               VARCHAR(20)  UNIQUE,
    email               VARCHAR(100) UNIQUE,
    address             VARCHAR(255),
    registration_date   DATETIME,
    status              VARCHAR(20)  DEFAULT 'ACTIVE',
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 7. user_accounts
CREATE TABLE user_accounts (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    role           VARCHAR(20)  NOT NULL,
    related_id     BIGINT,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 8. classes
CREATE TABLE classes (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_name   VARCHAR(100) NOT NULL,
    course_id    BIGINT,
    teacher_id   BIGINT,
    room_id      BIGINT,
    start_date   DATE,
    end_date     DATE,
    max_student  INT,
    status       VARCHAR(20)  DEFAULT 'ACTIVE',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_classes_course  FOREIGN KEY (course_id)  REFERENCES courses(id),
    CONSTRAINT fk_classes_teacher FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    CONSTRAINT fk_classes_room    FOREIGN KEY (room_id)    REFERENCES rooms(id)
) ENGINE=InnoDB;

-- 9. enrollments
CREATE TABLE enrollments (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT,
    class_id        BIGINT,
    enrollment_date DATETIME,
    status          VARCHAR(50) DEFAULT 'ACTIVE',
    result          VARCHAR(20),
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_enroll_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_enroll_class   FOREIGN KEY (class_id)   REFERENCES classes(id)
) ENGINE=InnoDB;

-- 10. schedules
CREATE TABLE schedules (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id    BIGINT,
    room_id     BIGINT,
    date        DATE,
    start_time  TIME,
    end_time    TIME,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sched_class FOREIGN KEY (class_id) REFERENCES classes(id),
    CONSTRAINT fk_sched_room  FOREIGN KEY (room_id)  REFERENCES rooms(id)
) ENGINE=InnoDB;

-- 11. attendances
CREATE TABLE attendances (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id  BIGINT,
    class_id    BIGINT,
    date        DATE,
    status      VARCHAR(20),
    note        VARCHAR(255),
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_att_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_att_class   FOREIGN KEY (class_id)   REFERENCES classes(id)
) ENGINE=InnoDB;

-- 12. invoices
CREATE TABLE invoices (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id             BIGINT        NOT NULL,
    enrollment_id          BIGINT,
    total_amount           DECIMAL(14,2) NOT NULL,
    issue_date             DATETIME,
    status                 VARCHAR(50)   DEFAULT 'UNPAID',
    applied_promo_code     VARCHAR(50),
    promo_discount_amount  DECIMAL(14,2),
    created_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_student    FOREIGN KEY (student_id)    REFERENCES students(id),
    CONSTRAINT fk_inv_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments(id)
) ENGINE=InnoDB;

-- 13. payments
CREATE TABLE payments (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id     BIGINT,
    enrollment_id  BIGINT,
    amount         DECIMAL(14,2),
    payment_date   DATETIME,
    payment_method VARCHAR(30),
    status         VARCHAR(50)  DEFAULT 'PENDING',
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pay_student    FOREIGN KEY (student_id)    REFERENCES students(id),
    CONSTRAINT fk_pay_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments(id)
) ENGINE=InnoDB;

-- 14. results
CREATE TABLE results (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    class_id   BIGINT,
    score      DECIMAL(7,2),
    grade      VARCHAR(20),
    comment    VARCHAR(1000),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_res_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_res_class   FOREIGN KEY (class_id)   REFERENCES classes(id)
) ENGINE=InnoDB;

-- 15. promo_codes
CREATE TABLE promo_codes (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    code                VARCHAR(50)  NOT NULL UNIQUE,
    description         VARCHAR(255),
    discount_percentage INT          NOT NULL DEFAULT 0,
    max_discount_amount DECIMAL(14,2),
    valid_from          DATE,
    valid_until         DATE,
    is_active           TINYINT(1)   NOT NULL DEFAULT 1,
    usage_count         INT          NOT NULL DEFAULT 0,
    max_usages          INT,
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

