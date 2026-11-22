package dev.experiment.linkforge.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateLinkRequest(
        @NotBlank @Size(max = 2048) String url,
        @Size(min = 3, max = 16) String customAlias,
        Instant expiresAt
) {
}
