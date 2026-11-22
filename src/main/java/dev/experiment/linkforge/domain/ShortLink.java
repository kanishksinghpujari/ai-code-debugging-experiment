package dev.experiment.linkforge.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "short_links", uniqueConstraints = @UniqueConstraint(name = "uk_short_links_code", columnNames = "code"))
public class ShortLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 16, unique = true)
    private String code;

    @Column(nullable = false, length = 2048)
    private String destinationUrl;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant expiresAt;

    @Column(nullable = false)
    private long clickCount;

    protected ShortLink() {
    }

    public ShortLink(String code, String destinationUrl, Instant createdAt, Instant expiresAt) {
        this.code = code;
        this.destinationUrl = destinationUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.clickCount = 0;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getDestinationUrl() { return destinationUrl; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public long getClickCount() { return clickCount; }

    public void incrementClickCount() {
        this.clickCount++;
    }

    public boolean isExpiredAt(Instant now) {
        return expiresAt != null && !expiresAt.isAfter(now);
    }
}
