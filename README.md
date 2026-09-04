# Lifestarter

A wedding RSVP site: an Angular front-end backed by a Kotlin / Spring Boot service.

Guests fill in who is coming, what they eat and how they want to be contacted; the organisers get
aggregate counts on the public page and a spreadsheet export behind a login.

## Stack

| | |
| --- | --- |
| Backend | Kotlin 2.3 · Spring Boot 4.1 · JDK 25 · Maven |
| Persistence | SQLite (default) or MongoDB — one property decides |
| Frontend | Angular 10 · PrimeNG |

## Architecture

The backend is a **hexagon** sliced by feature. Each slice owns its own vertical:

```text
server/src/main/kotlin/be/gerard/lifestarter/<feature>/
  api/         REST controllers (inbound adapter) + *To transfer objects
  mapper/      *To  <->  domain mapping
  domain/      business rules, domain types and the ports they need
  adapter/     outbound adapters (mail, Excel, events, …)
  repository/  persistence adapters + model/*Record row and document types
```

Features: `registration` (the core aggregate), `allergy`, `pledge`, `wave`, `country`, `access`,
`export`, `events`, `user`, plus a `config` package for the framework wiring.

Two rules hold the whole thing together, and `HexagonalArchitectureTest` fails the build when
either is broken:

1. **The domain depends on nothing.** No Spring, no Jackson, no POI, no JDBC — the `domain`
   packages are plain Kotlin. Domain services are registered as beans in `config/DomainConfiguration`
   rather than annotated with `@Service`, precisely so that stays true.
2. **Dependencies point inwards.** Controllers and adapters know the domain; the domain never knows
   them. Everything crossing the boundary does so through a port the domain declares
   (`RegistrationRepository`, `RegistrationExporter`, `CountryCatalog`, …).

Domain types are immutable `data class`es with `val` properties, validated in `init` blocks with
`require`. Cross-slice needs go through ports too — `pledge` counts subscriptions through a
`PledgeSubscriptionCounter` port rather than reaching into `registration`.

## Persistence: one knob, two backends

```yaml
lifestarter:
  persistence:
    type: sqlite        # or: mongodb
    sqlite:
      path: data/lifestarter.db
```

`application.yml` turns that value into a Spring profile
(`spring.profiles.include: "${lifestarter.persistence.type:sqlite}"`), and the profile decides
everything else:

- **`sqlite`** — a hand-declared Hikari `DataSource`, `JdbcClient` repositories with explicit SQL,
  and Flyway migrations from `db/migration`. The MongoDB auto-configurations are excluded.
- **`mongodb`** — `MongoTemplate` repositories over `*Record` documents. The JDBC and Flyway
  auto-configurations are excluded.

The domain never learns which one is running. Reference data (allergies, pledges, waves) is seeded
through the ports by `ReferenceDataInitializer`, so both modes come up with identical content.

Override it however you like:

```bash
./mvnw -pl server spring-boot:run -Dspring-boot.run.jvmArguments=-Dlifestarter.persistence.type=mongodb
LIFESTARTER_PERSISTENCE_TYPE=mongodb java -jar server/target/lifestarter-server-1.0.0-SNAPSHOT.jar
```

SQLite needs no setup at all: the database file and its parent directory are created on first
start. The pool is deliberately capped at one connection — SQLite serialises writes anyway, and a
single connection avoids `SQLITE_BUSY`.

## Running it

```bash
./mvnw -pl server spring-boot:run          # http://localhost:8080, SQLite
./mvnw verify                              # build + tests
```

For MongoDB, point it at your server and switch the mode:

```bash
./mvnw -pl server spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dlifestarter.persistence.type=mongodb -Dspring.data.mongodb.uri=mongodb://localhost:27017/lifestarter"
```

The front-end runs separately during development (`cd client && npm start`, port 4200, already in
the CORS allow-list). `npm run build` writes the production bundle straight into
`server/src/main/resources/static/`, so the packaged jar serves the SPA from `/`.

API documentation is at `/swagger-ui.html`.

## Configuration

Everything lives under the `lifestarter` prefix in `application.yml` and is bound to typed
`@ConfigurationProperties`. Nothing is hardcoded and no secret has a default.

| Property | Default | Purpose |
| --- | --- | --- |
| `lifestarter.persistence.type` | `sqlite` | `sqlite` or `mongodb` |
| `lifestarter.persistence.sqlite.path` | `data/lifestarter.db` | Database file |
| `lifestarter.reference-data.enabled` | `true` | Seed allergies, pledges and waves on start |
| `lifestarter.web.allowed-origins` | `http://localhost:4200` | CORS |
| `lifestarter.security.admin.username` | `admin` | Account allowed to download the export |
| `lifestarter.security.admin.password` | *(none)* | Set it, or a random one is generated and logged |
| `lifestarter.mail.*` | — | Notification mail; disabled unless `spring.mail.host` is set |

As environment variables that is `LIFESTARTER_PERSISTENCE_TYPE`,
`LIFESTARTER_SECURITY_ADMIN_PASSWORD`, and so on.

## API

Everything is served under `/api` (applied centrally, so the SPA keeps `/`).

| Method | Path | |
| --- | --- | --- |
| `GET` | `/api/allergies` | Allergy catalogue |
| `GET` | `/api/diets` | Dietary preferences |
| `GET` | `/api/countries` | ISO countries, named per `Accept-Language` |
| `GET` | `/api/pledges` | Pledge tiers with their subscription counts |
| `GET` | `/api/waves` · `/api/waves/current` | RSVP rounds; `404` once every deadline has passed |
| `POST` | `/api/registrations` | File an RSVP → `201` + `Location` |
| `GET` | `/api/registrations/statistics` | Aggregate guest counters |
| `GET` | `/api/access/roles?first-name=&last-name=` | Extra ceremony roles for a named guest |
| `GET` | `/api/access/activities?pledge=&first-first-name=&…` | What a party may attend |
| `GET` | `/api/exports/registrations` | Streams an `.xlsx` — **ADMIN** |
| `GET` | `/api/events/ping` · `/api/events/stream` | Liveness probe and SSE feed |
| `GET` | `/api/users/me` | The calling user |

Only the export is protected: it is the one endpoint that returns personal data. The counters are
what the public RSVP page displays anyway. Validation failures and errors come back as RFC 9457
`application/problem+json`.

## Testing

```bash
./mvnw -pl server test
```

- `HexagonalArchitectureTest` — ArchUnit rules guarding the dependency direction.
- Domain unit tests with MockK against the ports.
- `RegistrationApiIntegrationTest` — the full stack over HTTP against a throwaway SQLite file.
- `SqliteRegistrationRepositoryTest` — aggregate round-trip through the normalised schema.
- `MongoPersistenceModeTest` — the same hexagon on MongoDB via Testcontainers. **Skipped**, not
  failed, when Docker is unavailable.

## Deployment

See [SETUP.md](SETUP.md) for the nginx reverse proxy, Let's Encrypt and systemd notes.
