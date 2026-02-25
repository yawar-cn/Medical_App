package com.medapp.auth.entity;

import com.medapp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "otp_challenges")
public class OtpChallenge extends BaseEntity {

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 128)
    private String otpHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean consumed;

    @Column(nullable = false)
    private int attempts;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public void setOtpHash(String otpHash) {
        this.otpHash = otpHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }
}
