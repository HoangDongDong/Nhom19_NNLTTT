package com.edulanguage.entity;

import com.edulanguage.entity.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @NotBlank
    @Size(max = 255)
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    private Role role;

    /**
     * Trường kỹ thuật lưu ID thực thể liên quan (Student/Teacher/Staff).
     * Kết hợp với role để xác định kiểu người dùng.
     */
    @Column(name = "related_id")
    private Long relatedId;

    /**
     * Ba quan hệ 1-1 tùy chọn dùng chung cột related_id (insertable=false, updatable=false).
     * Cần ràng buộc nghiệp vụ để mỗi tài khoản chỉ gắn với đúng một loại người dùng.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Student student;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Teacher teacher;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Staff staff;
}
