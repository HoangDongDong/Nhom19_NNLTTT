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
│  → Gọi DAO để đọc/ghi dữ liệu                               │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  TẦNG 3 - DATA ACCESS (Tầng truy cập dữ liệu)               │
│  • dao           → Interface DAO                             │
│  • dao.impl      → Triển khai, gọi Repository               │
│  • repository    → JpaRepository, truy vấn DB               │
│  → Controller/UI không gọi trực tiếp DAO/Repository         │
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
| Presentation | controller, UI | Service                           | DAO, Repository  |
| Business     | service, impl  | DAO, Entity                        | Controller, UI   |
| Data Access  | dao, repository| Entity (JPA)                      | Controller, UI   |

## Cấu trúc thư mục

```
com.edulanguage
├── controller/          # Tầng 1 - Web
├── UI/                  # Tầng 1 - Desktop (Swing)
├── service/             # Tầng 2 - Interface
│   └── impl/            # Tầng 2 - Triển khai
├── dao/                 # Tầng 3 - DAO interface + impl
│   └── impl/
├── repository/          # Tầng 3 - Spring Data JPA
├── entity/              # Domain
├── config/               # Cấu hình (Security, ...)
└── security/             # Adapter Security (gọi UserAccountService)
```

## Ví dụ luồng dữ liệu

- **Đăng nhập (desktop)**: `LoginFrame` → `AuthenticationManager` → `CustomUserDetailsService` → **UserAccountService** → (DAO) → UserAccountRepository.
- **Trang web danh sách học viên**: `StudentController` → **StudentService** → (DAO) → StudentRepository → trả về `List<Student>` cho view `student.html`.
- **Trang web danh sách giáo viên**: `TeacherController` → **TeacherService** → (DAO) → TeacherRepository → view `teacher.html`.
- **Trang web danh sách khóa học**: `CourseController` → **CourseService** → (DAO) → CourseRepository → view `courses.html`.
