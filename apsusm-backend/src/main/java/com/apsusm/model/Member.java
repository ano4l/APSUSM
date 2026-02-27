package com.apsusm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String memberId; // APSUSM-2026-0001

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    private String phone;

    @NotBlank
    private String licenseNumber;

    private String institution;

    private String specialization;

    private String province;

    @Column(length = 500)
    private String photoPath;

    @Column(length = 500)
    private String cardFrontPath;

    @Column(length = 500)
    private String cardBackPath;

    @Enumerated(EnumType.STRING)
    private MemberStatus status = MemberStatus.PENDING_PAYMENT;

    private String paystackReference;

    private String paystackTransactionId;

    private Long amountPaid; // in smallest currency unit

    private String currency;

    private LocalDateTime registeredAt;

    private LocalDateTime paidAt;

    private LocalDateTime cardGeneratedAt;

    private LocalDateTime expiresAt;

    private boolean emailSent = false;

    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getCardFrontPath() { return cardFrontPath; }
    public void setCardFrontPath(String cardFrontPath) { this.cardFrontPath = cardFrontPath; }

    public String getCardBackPath() { return cardBackPath; }
    public void setCardBackPath(String cardBackPath) { this.cardBackPath = cardBackPath; }

    public MemberStatus getStatus() { return status; }
    public void setStatus(MemberStatus status) { this.status = status; }

    public String getPaystackReference() { return paystackReference; }
    public void setPaystackReference(String paystackReference) { this.paystackReference = paystackReference; }

    public String getPaystackTransactionId() { return paystackTransactionId; }
    public void setPaystackTransactionId(String paystackTransactionId) { this.paystackTransactionId = paystackTransactionId; }

    public Long getAmountPaid() { return amountPaid; }
    public void setAmountPaid(Long amountPaid) { this.amountPaid = amountPaid; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getCardGeneratedAt() { return cardGeneratedAt; }
    public void setCardGeneratedAt(LocalDateTime cardGeneratedAt) { this.cardGeneratedAt = cardGeneratedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isEmailSent() { return emailSent; }
    public void setEmailSent(boolean emailSent) { this.emailSent = emailSent; }
}
