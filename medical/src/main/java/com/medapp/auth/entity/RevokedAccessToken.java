package com.medapp.auth.entity;

import com.medapp.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "revoked_access_tokens")
public class RevokedAccessToken extends BaseEntity {

    @Column(nullable = false, unique = true, length = 128)
    private String jti;

    @Column(nullable = false)
    private Instant expiresAt;

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
