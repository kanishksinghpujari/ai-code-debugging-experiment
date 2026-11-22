package dev.experiment.linkforge.service;

import dev.experiment.linkforge.api.CreateLinkRequest;
import dev.experiment.linkforge.api.LinkResponse;
import dev.experiment.linkforge.config.LinkProperties;
import dev.experiment.linkforge.domain.ShortLink;
import dev.experiment.linkforge.repository.ShortLinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;

@Service
public class ShortLinkService {
    private final ShortLinkRepository repository;
    private final CodeGenerator codeGenerator;
    private final LinkProperties properties;
    private final Clock clock;

    public ShortLinkService(ShortLinkRepository repository, CodeGenerator codeGenerator,
                            LinkProperties properties) {
        this(repository, codeGenerator, properties, Clock.systemUTC());
    }

    ShortLinkService(ShortLinkRepository repository, CodeGenerator codeGenerator,
                     LinkProperties properties, Clock clock) {
        this.repository = repository;
        this.codeGenerator = codeGenerator;
        this.properties = properties;
        this.clock = clock;
    }

    @Transactional
    public LinkResponse create(CreateLinkRequest request) {
        String destination = validateAndNormalizeUrl(request.url());
        Instant now = clock.instant();

        if (request.expiresAt() != null && !request.expiresAt().isAfter(now)) {
            throw new IllegalArgumentException("expiresAt must be in the future");
        }

        String code = request.customAlias() == null || request.customAlias().isBlank()
                ? generateUniqueCode()
                : validateAlias(request.customAlias());

        if (repository.existsByCode(code)) {
            throw new AliasAlreadyExistsException(code);
        }

        ShortLink saved = repository.save(new ShortLink(code, destination, now, request.expiresAt()));
        return toResponse(saved);
    }

    @Transactional
    public String resolve(String code) {
        ShortLink link = repository.findByCode(code)
                .orElseThrow(() -> new LinkNotFoundException(code));
        if (link.isExpiredAt(clock.instant())) {
            throw new LinkNotFoundException(code);
        }
        link.incrementClickCount();
        return link.getDestinationUrl();
    }

    @Transactional(readOnly = true)
    public LinkResponse get(String code) {
        ShortLink link = repository.findByCode(code)
                .orElseThrow(() -> new LinkNotFoundException(code));
        return toResponse(link);
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 5; attempt++) {
            String candidate = codeGenerator.nextCode();
            if (!repository.existsByCode(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not allocate a unique short code");
    }

    private String validateAlias(String alias) {
        String normalized = alias.trim();
        if (!normalized.matches("[A-Za-z0-9_-]{3,16}")) {
            throw new IllegalArgumentException("customAlias must be 3-16 letters, digits, '_' or '-'");
        }
        return normalized;
    }

    private String validateAndNormalizeUrl(String rawUrl) {
        try {
            URI uri = URI.create(rawUrl.trim());
            String scheme = uri.getScheme();
            if (!uri.isAbsolute() || scheme == null ||
                    !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) ||
                    uri.getHost() == null) {
                throw new IllegalArgumentException("url must be an absolute http or https URL");
            }
            return uri.toASCIIString();
        } catch (IllegalArgumentException exception) {
            if (exception.getMessage() != null && exception.getMessage().startsWith("url must")) {
                throw exception;
            }
            throw new IllegalArgumentException("url must be a valid absolute http or https URL");
        }
    }

    private LinkResponse toResponse(ShortLink link) {
        String baseUrl = properties.baseUrl() == null ? "http://localhost:8080" : properties.baseUrl();
        String normalizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return new LinkResponse(link.getCode(), normalizedBase + "/" + link.getCode(),
                link.getDestinationUrl(), link.getCreatedAt(), link.getExpiresAt(), link.getClickCount());
    }
}
