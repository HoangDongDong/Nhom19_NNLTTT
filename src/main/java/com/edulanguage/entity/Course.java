package com.edulanguage.entity;

import com.edulanguage.entity.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courses")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    @Size(max = 50)
    @Column(name = "level", length = 50)
    private String level;

    @Column(name = "duration_hours")
    private Integer duration;

    @Digits(integer = 12, fraction = 2)
    @Column(name = "fee", precision = 14, scale = 2)
    private BigDecimal fee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Clazz> classes;

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public List<Clazz> getClasses() { return classes; }
    public void setClasses(List<Clazz> classes) { this.classes = classes; }
}
