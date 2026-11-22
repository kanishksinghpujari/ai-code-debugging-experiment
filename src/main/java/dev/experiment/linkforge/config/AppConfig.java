package dev.experiment.linkforge.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LinkProperties.class)
public class AppConfig {
}
