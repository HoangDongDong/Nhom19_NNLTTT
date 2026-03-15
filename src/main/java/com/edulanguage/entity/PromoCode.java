package com.edulanguage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promo_codes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "code", nullable = false, length = 50, unique = true)
    private String code;

    /** Mô tả ngắn cho mã khuyến mại (Ví dụ: "Ưu đãi khai giảng tháng 9") */
    @Size(max = 255)
    @Column(name = "description")
    private String description;

    /** Phần trăm giảm giá (0-100) */
    @NotNull
    @Min(0)
    @Max(100)
    @Column(name = "discount_percentage", nullable = false)
    private Integer discountPercentage;

    /** Số tiền giảm tối đa (VNĐ). Null = không giới hạn. */
    @Column(name = "max_discount_amount", precision = 14, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    /** Trạng thái kích hoạt */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /** Số lần đã được sử dụng */
    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Integer usageCount = 0;

    /** Số lần sử dụng tối đa. Null = không giới hạn. */
    @Column(name = "max_usages")
    private Integer maxUsages;

    // === Getters & Setters ===
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Integer discountPercentage) { this.discountPercentage = discountPercentage; }
    public BigDecimal getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
    public Integer getMaxUsages() { return maxUsages; }
    public void setMaxUsages(Integer maxUsages) { this.maxUsages = maxUsages; }
}
