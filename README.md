# LinkForge — AI Coding & Debugging Experiment

A small URL-shortening service built for the experiment **“I Let AI Write 1,000 Lines of Code. Then I Tried to Debug Them.”**

The repository deliberately keeps the implementation, tests, and debugging notes in version control so the experiment can be audited.

## Stack
- Java 21
- Spring Boot 3.4
- Spring Web, Spring Data JPA, Validation
- PostgreSQL
- Flyway migrations
- Testcontainers for PostgreSQL integration tests
- Docker Compose

## Features
- Create short links
- Resolve a short code with an HTTP redirect
- Optional custom aliases
- Optional expiration timestamps
- Click-count analytics
- List and inspect links
- Input validation and consistent API errors

## Run locally

Requirements: Docker and Docker Compose.

```bash
docker compose up --build
```

The API listens on `http://localhost:8080`.

## API

### Create a link
```http
POST /api/links
Content-Type: application/json

{
  "url": "https://example.com/some/long/path",
  "customAlias": "docs",
  "expiresAt": "2027-01-01T00:00:00Z"
}
```

Only `url` is required. `customAlias` and `expiresAt` are optional.

### Resolve a link
```http
GET /{code}
```

A valid link returns `302 Found` with a `Location` header. Expired or unknown links return `404`.

### Inspect analytics
```http
GET /api/links/{code}
```

Returns the stored destination, creation/expiration times, and click count.

## Run tests

```bash
./mvnw test
```

The integration test suite uses Testcontainers and therefore needs a working Docker daemon.

## Experiment protocol

1. Record the baseline commit and run the full test suite.
2. Use an AI coding agent to implement the agreed feature set without manual code edits.
3. Record the generated LOC, elapsed time, and test outcome.
4. Run a separate adversarial test checklist covering aliases, expiry boundaries, concurrent increments, redirects, and malformed inputs.
5. Debug failures one at a time, recording the failing test, root cause, fix, and regression test.
6. Compare debugging time and changes with a human-written baseline only if that baseline is actually produced.

Do not treat generated LOC as a quality metric. The article should report measured results from this repository rather than estimates.

## Notes
This is an experiment project, not a production-ready link service. Before production use, add rate limiting, abuse detection, URL safety checks, authentication/ownership, retention policies, and operational monitoring.
