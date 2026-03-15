package com.edulanguage.entity;

import com.edulanguage.entity.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_accounts")
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
     * Dùng role + relatedId để tra cứu thủ công trong Service layer.
     */
    @Column(name = "related_id")
    private Long relatedId;

    // === Getters & Setters ===
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }
}

