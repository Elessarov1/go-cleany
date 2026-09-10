package com.cleany.authentication;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "apple_identity_credential")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class AppleIdentityCredential {
    @Id @Column(name = "identity_id") private long identityId;
    @Column(name = "refresh_token_ciphertext", nullable = false) private String refreshTokenCiphertext;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;

    AppleIdentityCredential(long identityId, String refreshTokenCiphertext, Instant updatedAt) {
        this.identityId = identityId;
        this.refreshTokenCiphertext = refreshTokenCiphertext;
        this.updatedAt = updatedAt;
    }

    void update(String ciphertext, Instant now) {
        refreshTokenCiphertext = ciphertext;
        updatedAt = now;
    }
}
