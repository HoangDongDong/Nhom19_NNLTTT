package com.edulanguage.entity;

import com.edulanguage.entity.enums.Status;
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

/**
 * Chi nhánh trung tâm ngoại ngữ.
 */
@Entity
@Table(name = "branches")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "branch_name", nullable = false, length = 100)
    private String branchName;

    @Size(max = 255)
    @Column(name = "address", length = 255)
    private String address;

    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status;

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
