package dev.experiment.linkforge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record LinkProperties(String baseUrl) {
}
