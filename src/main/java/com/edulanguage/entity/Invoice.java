package com.edulanguage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoices")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    private Enrollment enrollment;

    @Digits(integer = 12, fraction = 2)
    @Column(name = "total_amount", precision = 14, scale = 2, nullable = false)
    @NotNull
    private BigDecimal totalAmount;

    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    /** Mã khuyến mại đã áp dụng (nếu có) */
    @Size(max = 50)
    @Column(name = "applied_promo_code", length = 50)
    private String appliedPromoCode;

    /** Số tiền được khuyến mại */
    @Column(name = "promo_discount_amount", precision = 14, scale = 2)
    private BigDecimal promoDiscountAmount;

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Enrollment getEnrollment() { return enrollment; }
    public void setEnrollment(Enrollment enrollment) { this.enrollment = enrollment; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAppliedPromoCode() { return appliedPromoCode; }
    public void setAppliedPromoCode(String appliedPromoCode) { this.appliedPromoCode = appliedPromoCode; }
    public BigDecimal getPromoDiscountAmount() { return promoDiscountAmount; }
    public void setPromoDiscountAmount(BigDecimal promoDiscountAmount) { this.promoDiscountAmount = promoDiscountAmount; }
}
