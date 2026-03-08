-- ============================================
-- Tạo bảng branches (Chi nhánh) - SQL Server
-- Database: EduLanguageCenter
-- Chạy 1 lần nếu chưa có bảng (spring.jpa.hibernate.ddl-auto=none).
-- ============================================

USE EduLanguageCenter;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'branches')
BEGIN
    CREATE TABLE branches (
        id           BIGINT IDENTITY(1,1) PRIMARY KEY,
        created_at   DATETIME2 NOT NULL,
        updated_at   DATETIME2 NOT NULL,
        branch_name  NVARCHAR(100) NOT NULL,
        address      NVARCHAR(255) NULL,
        phone        NVARCHAR(20) NULL,
        status       NVARCHAR(20) NULL
    );
    PRINT N'Đã tạo bảng branches.';
END
ELSE
    PRINT N'Bảng branches đã tồn tại.';
GO
