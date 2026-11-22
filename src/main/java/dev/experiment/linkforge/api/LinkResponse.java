package dev.experiment.linkforge.api;

import java.time.Instant;

public record LinkResponse(
        String code,
        String shortUrl,
        String destinationUrl,
        Instant createdAt,
        Instant expiresAt,
        long clickCount
) {
}
