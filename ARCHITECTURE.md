# Mô hình 3 lớp (Three-Tier Architecture)

Hệ thống tổ chức theo **3 tầng** rõ ràng:

```
┌─────────────────────────────────────────────────────────────┐
│  TẦNG 1 - PRESENTATION (Tầng trình bày / giao diện)         │
│  • controller  → Web (MVC, Thymeleaf)                       │
│  • UI          → Desktop (Swing)                             │
│  → Chỉ gọi Service, không gọi Repository                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  TẦNG 2 - BUSINESS LOGIC (Tầng nghiệp vụ)                   │
│  • service       → Interface dịch vụ                        │
│  • service.impl   → Triển khai, chứa logic nghiệp vụ         │
│  → Gọi Repository để đọc/ghi dữ liệu                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  TẦNG 3 - DATA ACCESS (Tầng truy cập dữ liệu)               │
│  • repository   → JpaRepository, truy vấn DB                │
│  → Chỉ Service gọi; Controller/UI không gọi trực tiếp      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  ENTITY (Mô hình dữ liệu)                                   │
│  • entity  → Các lớp ánh xạ bảng, dùng chung 3 tầng         │
└─────────────────────────────────────────────────────────────┘
```

## Quy tắc

| Tầng        | Package        | Được phép gọi                    | Không gọi        |
|------------|----------------|-----------------------------------|------------------|
| Presentation | controller, UI | Service                           | Repository       |
| Business     | service, impl  | Repository, Entity                | Controller, UI   |
| Data Access  | repository     | Entity (JPA)                      | Service, Controller |

## Cấu trúc thư mục

```
com.edulanguage
├── controller/          # Tầng 1 - Web
├── UI/                  # Tầng 1 - Desktop (Swing)
├── service/             # Tầng 2 - Interface
│   └── impl/            # Tầng 2 - Triển khai
├── repository/           # Tầng 3
├── entity/              # Domain
├── config/               # Cấu hình (Security, ...)
└── security/             # Adapter Security (gọi UserAccountService)
```

## Ví dụ luồng dữ liệu

- **Đăng nhập (desktop)**: `LoginFrame` → `AuthenticationManager` → `CustomUserDetailsService` → **UserAccountService** → UserAccountRepository.
- **Trang web danh sách học viên**: `StudentController` → **StudentService** → StudentRepository → trả về `List<Student>` cho view `student.html`.
- **Trang web danh sách giáo viên**: `TeacherController` → **TeacherService** → TeacherRepository → view `teacher.html`.
- **Trang web danh sách khóa học**: `CourseController` → **CourseService** → CourseRepository → view `courses.html`.
