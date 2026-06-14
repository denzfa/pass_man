# Passwords Manager — Backend Resource Server

API-only Spring Boot backend (`denzfa.cockpit.passman`) acting as an **OAuth 2.1
resource server**. Every endpoint requires an authenticated principal holding
`ROLE_ADMIN`.

## Stack

- Java 25, Spring Boot 4.0.x (Spring Framework 7 / Spring Security 7)
- Spring Web, Spring Data JPA (Hibernate), Bean Validation
- PostgreSQL (runtime), H2 (tests)
- Schema managed by Hibernate (`ddl-auto: update`)

## Security model

Access tokens are issued by the Authorization Server at `http://localhost:9000`
(OAuth 2.1, default endpoints). The resource server validates the JWT via the
issuer's well-known metadata and maps the `roles` claim to Spring authorities
(`ADMIN` → `ROLE_ADMIN`). The roles claim name is configurable via
`app.security.roles-claim`.

All failures return a standard JSON body:

```json
{ "timestamp": "...", "status": 404, "error": "Not Found", "message": "...", "path": "/api/documents/..." }
```

| Situation              | Status |
|------------------------|--------|
| No / invalid token     | 401    |
| Token without ADMIN    | 403    |
| Validation error       | 400 (with `violations`) |
| Unknown id             | 404    |
| Duplicate name         | 409    |
| Unhandled error        | 500    |

## Domain model

`Document` (UUID primary key):

- `name` — `varchar(30)`, unique (not the PK)
- localized `title` (≤100) and `description` (≤255), stored per-locale in
  `document_translation` rows (one row per `(document, locale)`)
- audit fields: `createdAt`, `modifiedAt`, `createdBy`, `modifiedBy`
  (populated from the JWT subject)

Locale resolution uses the `Accept-Language` request header: exact match →
language-only match → first available translation.

## API

Base path `/api/documents`:

| Method | Path           | Body              | Success |
|--------|----------------|-------------------|---------|
| GET    | `/`            | —                 | 200     |
| GET    | `/{id}`        | —                 | 200     |
| POST   | `/`            | `DocumentRequest` | 201 + `Location` |
| PUT    | `/{id}`        | `DocumentRequest` | 200     |
| DELETE | `/{id}`        | —                 | 204     |

`DocumentRequest`:

```json
{
  "name": "vault-root",
  "translations": [
    { "locale": "en", "title": "Master Key", "description": "The primary credential store" },
    { "locale": "fr", "title": "Cle Maitresse", "description": "Le coffre principal" }
  ]
}
```

## Run

Prerequisites: JDK 25, a running PostgreSQL, and the authorization server on
`http://localhost:9000`.

```bash
# create the database (defaults shown; override via env vars)
createdb passman

# configurable via environment
export PASSMAN_DB_URL=jdbc:postgresql://localhost:5432/passman
export PASSMAN_DB_USER=passman
export PASSMAN_DB_PASSWORD=passman
export PASSMAN_ISSUER_URI=http://localhost:9000

mvn spring-boot:run
```

## Test

Tests run against in-memory H2 and inject authentication directly (no live
authorization server needed):

```bash
mvn test
```

Covered: context load, 401 (unauthenticated), 403 (non-admin), full CRUD
lifecycle as admin, 409 (duplicate name), 400 (validation).
