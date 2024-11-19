package com.project.parkrental.security.DTO;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
public class PwdResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idx;

    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @Column(nullable = false)
    private Timestamp expirationDate;

    public PwdResetToken() {}

    public PwdResetToken(User user) {
        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.expirationDate = calculateExpirationDate();
    }

    private Timestamp calculateExpirationDate() {
        long now = System.currentTimeMillis();
        long halfHour = 30 * 60 * 1000;

        return new Timestamp(now + halfHour);
    }

    public boolean isExpired() {
        return expirationDate.before(new Timestamp(System.currentTimeMillis()));
    }
}
