package com.edulanguage.entity;

import com.edulanguage.entity.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teachers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    @Email
    @Size(max = 100)
    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Size(max = 100)
    @Column(name = "specialty", length = 100)
    private String specialty;

    @Column(name = "hire_date")
    private LocalDateTime hireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private List<Clazz> classes;



    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public LocalDateTime getHireDate() { return hireDate; }
    public void setHireDate(LocalDateTime hireDate) { this.hireDate = hireDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public List<Clazz> getClasses() { return classes; }
    public void setClasses(List<Clazz> classes) { this.classes = classes; }

}
