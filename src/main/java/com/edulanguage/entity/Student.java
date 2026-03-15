package com.edulanguage.entity;

import com.edulanguage.entity.enums.Gender;
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
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Past
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    @Size(max = 20)
    @Column(name = "phone", length = 20, unique = true)
    private String phone;

    @Email
    @Size(max = 100)
    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Size(max = 255)
    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Payment> payments;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Attendance> attendances;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Result> results;



    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
    public List<Invoice> getInvoices() { return invoices; }
    public void setInvoices(List<Invoice> invoices) { this.invoices = invoices; }
    public List<Attendance> getAttendances() { return attendances; }
    public void setAttendances(List<Attendance> attendances) { this.attendances = attendances; }
    public List<Result> getResults() { return results; }
    public void setResults(List<Result> results) { this.results = results; }

}
