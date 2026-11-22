package dev.experiment.linkforge.api;

import java.time.Instant;

public record ApiError(String code, String message, Instant timestamp) {
}
