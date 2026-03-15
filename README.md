# 📚 EduLanguage Center — Hệ thống Quản lý Trung tâm Ngoại ngữ

> **Nhóm 19 — Ngôn ngữ Lập trình Tiên tiến**  
> Nền tảng: Spring Boot 3.2 · Thymeleaf · SQL Server · Spring Security

---

## 🏗️ Kiến trúc tổng quan

```
src/main/
├── java/com/edulanguage/
│   ├── config/          # SecurityConfig, AppConfig (BCrypt, AuthManager)
│   ├── controller/      # MVC Controllers (Web + REST API)
│   │   ├── admin/       # AdminPromoController, AdminStaffController
│   │   └── api/         # PromoApiController (AJAX endpoints)
│   ├── entity/          # JPA Entities + Enums
│   ├── repository/      # Spring Data JPA Repositories
│   ├── security/        # CustomUserDetailsService
│   └── service/         # Business Logic (Interfaces + Impl)
└── resources/
    ├── sql/             # schema.sql · data.sql
    ├── templates/       # Thymeleaf HTML templates
    └── application.properties
```

---

## 🚀 Hướng dẫn cài đặt & chạy

### Yêu cầu môi trường

| Phần mềm | Phiên bản |
|---|---|
| Java (JDK) | 17 hoặc 21 |
| Maven | 3.8+ |
| SQL Server | 2019+ (hoặc Express) |
| SSMS | Tùy chọn, để quản lý DB |

---

### Bước 1 — Tạo Database trên SQL Server

Mở **SSMS** và chạy:

```sql
CREATE DATABASE EduLanguageCenter;
```

---

### Bước 2 — Chạy file SQL để tạo bảng & dữ liệu mẫu

Mở và chạy lần lượt trong SSMS:

1. `src/main/resources/sql/schema.sql` — Tạo 15 bảng + quan hệ
2. `src/main/resources/sql/data.sql` — Chèn dữ liệu mẫu

> ⚠️ Chạy `schema.sql` trước, sau đó mới chạy `data.sql`

---

### Bước 3 — Cấu hình kết nối Database

Mở `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=EduLanguageCenter;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD   ← đổi tại đây
```

---

### Bước 4 — Khởi chạy ứng dụng

```bash
mvn spring-boot:run
```

Mở trình duyệt: **http://localhost:8080**

---

## 🔑 Tài khoản đăng nhập mẫu

| Tài khoản | Mật khẩu | Vai trò | Mô tả |
|---|---|---|---|
| `admin` | `123` | ADMIN | Quản trị toàn hệ thống |
| `staff1` | `123` | STAFF | Nhân viên quản lý |
| `teacher1` | `123` | TEACHER | Giáo viên lớp A1/A2 |
| `teacher2` | `123` | TEACHER | Giáo viên lớp B1 |
| `student1` | `123` | STUDENT | Học viên (Lê Văn Cường) |
| `student2` | `123` | STUDENT | Học viên (Phạm Thị Dung) |
| `student3` | `123` | STUDENT | Học viên (Hoàng Minh Đức) |

---

## 🗂️ Danh sách tính năng

### 👑 Admin
| URL | Mô tả |
|---|---|
| `/admin/staffs` | Quản lý nhân viên (CRUD) |
| `/admin/promos` | Quản lý mã khuyến mại (CRUD) |

### 🧑‍💼 Staff (Nhân viên)
| URL | Mô tả |
|---|---|
| `/staff/courses` | Quản lý khóa học |
| `/staff/teachers` | Quản lý giáo viên |
| `/staff/classes` | Quản lý lớp học & lịch |
| `/staff/enrollments` | Quản lý ghi danh |
| `/staff/finance` | Xử lý hóa đơn & thanh toán |
| `/students` | Quản lý danh sách học viên |

### 👩‍🏫 Teacher (Giáo viên)
| URL | Mô tả |
|---|---|
| `/teacher/attendance` | Điểm danh học viên theo lớp |
| `/teacher/results` | Nhập điểm & xếp loại |

### 🎓 Student (Học viên)
| URL | Mô tả |
|---|---|
| `/student/portal` | Trang cá nhân (khóa học, lịch, hóa đơn) |
| `/student/payment/{id}` | Thanh toán học phí (hỗ trợ mã KM) |

### 🌐 Chung
| URL | Mô tả |
|---|---|
| `/profile` | Hồ sơ cá nhân & đổi mật khẩu |
| `/report/invoice/{id}` | In hóa đơn học phí |
| `/api/promos/check?code=XX&amount=YY` | REST API kiểm tra mã khuyến mại |

---

## 🗄️ Sơ đồ bảng (Schema tóm tắt)

```
branches          — Chi nhánh
rooms             — Phòng học
courses           — Khóa học
staffs            — Nhân viên / Admin
teachers          — Giáo viên
students          — Học viên
user_accounts     — Tài khoản đăng nhập (polymorphic related_id)
classes           — Lớp học (courses × teachers × rooms)
enrollments       — Ghi danh (students × classes)
schedules         — Lịch học (classes × rooms)
attendances       — Điểm danh (students × classes)
invoices          — Hóa đơn học phí (+ mã KM)
payments          — Giao dịch thanh toán
results           — Kết quả học tập
promo_codes       — Mã khuyến mại
```

---

## 🔧 Cấu hình nâng cao

### Hibernate DDL Auto
```properties
# update   = Tự tạo & cập nhật bảng theo Entity (khuyến nghị dev)
# none      = Không can thiệp DB (production)
# create    = Xóa & tạo lại mỗi lần start (mất data!)
spring.jpa.hibernate.ddl-auto=update
```

### Bật chạy SQL script tự động khi start
```properties
# Đổi thành 'always' để tự chạy schema.sql & data.sql khi khởi động
# Cảnh báo: data.sql có IF NOT EXISTS để tránh trùng, nhưng schema.sql sẽ DROP bảng!
spring.sql.init.mode=never
```

### SQL Logging
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

---

## 📋 Lưu ý quan trọng

> [!IMPORTANT]
> **Mật khẩu** trong `data.sql` được lưu dạng **BCrypt hash** của `123`.  
> Nếu muốn đổi mật khẩu mặc định, hãy sinh hash mới:
> ```java
> System.out.println(new BCryptPasswordEncoder().encode("your_password"));
> ```

> [!WARNING]
> Bảng `user_accounts.related_id` là **polymorphic** — KHÔNG có FK thật ra DB.  
> Hệ thống dùng kết hợp `role + related_id` để ánh xạ đến đúng bảng (`students`, `teachers`, `staffs`).

> [!NOTE]
> Khi thêm/sửa Entity và muốn DB cập nhật cột mới → chỉ cần restart app.  
> Hibernate `ddl-auto=update` sẽ tự thêm cột mà không xóa dữ liệu.

---

## 📦 Công nghệ sử dụng

| Thành phần | Công nghệ |
|---|---|
| Backend Framework | Spring Boot 3.2.5 |
| Security | Spring Security 6 + BCrypt |
| ORM | Spring Data JPA + Hibernate 6 |
| Database | Microsoft SQL Server |
| Template Engine | Thymeleaf 3 |
| CSS Framework | Bootstrap 5.3 + Bootstrap Icons |
| Build Tool | Maven |
| Java Version | 17+ |
| Lombok | Giảm boilerplate (getter/setter/builder) |

---

*Nhóm 19 — Khoa Công nghệ Thông tin*
