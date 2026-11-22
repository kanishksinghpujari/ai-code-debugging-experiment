package dev.experiment.linkforge.api;

import dev.experiment.linkforge.service.ShortLinkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class LinkController {
    private final ShortLinkService service;

    public LinkController(ShortLinkService service) {
        this.service = service;
    }

    @PostMapping("/api/links")
    @ResponseStatus(HttpStatus.CREATED)
    public LinkResponse create(@Valid @RequestBody CreateLinkRequest request) {
        return service.create(request);
    }

    @GetMapping("/{code:[A-Za-z0-9_-]{3,16}}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String destination = service.resolve(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(destination))
                .build();
    }

    @GetMapping("/api/links/{code}")
    public LinkResponse get(@PathVariable String code) {
        return service.get(code);
    }
}
