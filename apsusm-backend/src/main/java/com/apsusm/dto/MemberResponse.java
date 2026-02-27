package com.apsusm.dto;

import com.apsusm.model.MemberStatus;
import java.time.LocalDateTime;

public class MemberResponse {
    private Long id;
    private String memberId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String licenseNumber;
    private String institution;
    private String specialization;
    private String province;
    private MemberStatus status;
    private LocalDateTime registeredAt;
    private LocalDateTime paidAt;
    private LocalDateTime cardGeneratedAt;
    private LocalDateTime expiresAt;
    private boolean emailSent;
    private boolean hasCard;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

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

    public MemberStatus getStatus() { return status; }
    public void setStatus(MemberStatus status) { this.status = status; }

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

    public boolean isHasCard() { return hasCard; }
    public void setHasCard(boolean hasCard) { this.hasCard = hasCard; }
}
